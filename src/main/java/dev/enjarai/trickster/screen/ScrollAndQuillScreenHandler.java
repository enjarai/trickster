package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.revision.RevisionContext;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.source.PlayerSpellSource;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.NaNBlunder;
import io.wispforest.endec.Endec;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class ScrollAndQuillScreenHandler extends ScreenHandler implements RevisionContext {
    private final ItemStack scrollStack;
    private final ItemStack otherHandStack;

    public final SyncedProperty<SpellPart> spell = createProperty(SpellPart.class, SpellPart.ENDEC, new SpellPart());
    public final SyncedProperty<SpellPart> otherHandSpell = createProperty(SpellPart.class, SpellPart.ENDEC, new SpellPart());
    public final SyncedProperty<Boolean> isMutable = createProperty(Boolean.class, true);

    public Consumer<Fragment> replacerCallback;

    public final EquipmentSlot slot;
    public final boolean greedyEvaluation;

    public ScrollAndQuillScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null, null, null, false, true);
    }

    public ScrollAndQuillScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack scrollStack, ItemStack otherHandStack, EquipmentSlot slot, boolean greedyEvaluation, boolean isMutable) {
        super(ModScreenHandlers.SCROLL_AND_QUILL, syncId);

        this.scrollStack = scrollStack;
        this.otherHandStack = otherHandStack;

        this.slot = slot;
        this.greedyEvaluation = greedyEvaluation;

        if (scrollStack != null) {
            SpellComponent.getSpellPart(scrollStack).ifPresent(this.spell::set);
        }

        if (otherHandStack != null) {
            SpellComponent.getSpellPart(otherHandStack).ifPresent(this.otherHandSpell::set);
        }

        this.isMutable.set(isMutable);

        addServerboundMessage(SpellMessage.class, SpellMessage.ENDEC, msg -> updateSpell(msg.spell()));
        addServerboundMessage(OtherHandSpellMessage.class, OtherHandSpellMessage.ENDEC, msg -> updateOtherHandSpell(msg.spell()));

        addServerboundMessage(ExecuteOffhand.class, msg -> executeOffhand());
        addClientboundMessage(Replace.class, Replace.ENDEC, msg -> {
            if (replacerCallback != null) {
                replacerCallback.accept(msg.fragment());
            }
        });
    }

    public void updateSpell(SpellPart spell) {
        if (isMutable.get()) {
            if (scrollStack != null) {
                var server = player().getServer();
                if (server != null) {
                    server.execute(() -> {
                        if (greedyEvaluation) {
                            var executionState = new ExecutionState(List.of());
                            try {
                                spell.destructiveRun(new SpellContext(new PlayerSpellSource((ServerPlayerEntity) player()), executionState));
                                this.spell.set(spell);
                            } catch (BlunderException e) {
                                if (e instanceof NaNBlunder)
                                    ModCriteria.NAN_NUMBER.trigger((ServerPlayerEntity) player());

                                player().sendMessage(e.createMessage().append(" (").append(executionState.formatStackTrace()).append(")"));
                            } catch (Exception e) {
                                player().sendMessage(Text.literal("Uncaught exception in spell: " + e.getMessage())
                                        .append(" (").append(executionState.formatStackTrace()).append(")"));
                            }

                            ((ServerPlayerEntity) player()).getServerWorld().playSoundFromEntity(
                                    null, player(), ModSounds.CAST, SoundCategory.PLAYERS, 1f, ModSounds.randomPitch(0.8f, 0.2f));
                        } else {
                            spell.brutallyMurderEphemerals();
                        }

                        SpellComponent.setSpellPart(scrollStack, spell, false);
                    });
                }
            } else {
//            var result = SpellPart.CODEC.encodeStart(JsonOps.INSTANCE, spell).result().get();
//            Trickster.LOGGER.warn(result.toString());
                sendMessage(new SpellMessage(spell));
            }
        }
    }

    public void updateOtherHandSpell(SpellPart spell) {
        if (isMutable.get()) {
            if (otherHandStack != null) {
                if (!otherHandStack.isEmpty()) {
                    var server = player().getServer();
                    if (server != null) {
                        server.execute(() -> {
                            SpellComponent.setSpellPart(otherHandStack, spell, false);
                            otherHandSpell.set(spell);
                        });
                    }
                }
            } else {
                sendMessage(new OtherHandSpellMessage(spell));
            }
        }
    }

    @Override
    public SpellPart getOtherHandSpell() {
        return otherHandSpell.get();
    }

    public void executeOffhand() {
        var server = player().getServer();
        if (server != null) {
            server.execute(() -> {
                if (player().getInventory().contains(ModItems.CAN_EVALUATE_DYNAMICALLY)) {
                    var spell = new DefaultSpellExecutor(otherHandSpell.get(), List.of());
                    Fragment result = VoidFragment.INSTANCE;

                    try {
                        result = spell.singleTickRun(new PlayerSpellSource((ServerPlayerEntity) player()));
                    } catch (BlunderException blunder) {
                        if (blunder instanceof NaNBlunder)
                            ModCriteria.NAN_NUMBER.trigger((ServerPlayerEntity) player());

                        player().sendMessage(blunder.createMessage()
                                .append(" (").append(spell.getCurrentState().formatStackTrace()).append(")"));
                    } catch (Exception e) {
                        player().sendMessage(Text.literal("Uncaught exception in spell: " + e.getMessage())
                                .append(" (").append(spell.getCurrentState().formatStackTrace()).append(")"));
                    }

                    sendMessage(new Replace(result));
                    ((ServerPlayerEntity) player()).getServerWorld().playSoundFromEntity(
                            null, player(), ModSounds.CAST, SoundCategory.PLAYERS, 1f, ModSounds.randomPitch(0.8f, 0.2f));
                }
            });
        } else {
            sendMessage(new ExecuteOffhand());
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public record SpellMessage(SpellPart spell) {
        public static final Endec<SpellMessage> ENDEC = SpellPart.ENDEC.xmap(SpellMessage::new, SpellMessage::spell);
    }

    public record OtherHandSpellMessage(SpellPart spell) {
        public static final Endec<OtherHandSpellMessage> ENDEC = SpellPart.ENDEC.xmap(OtherHandSpellMessage::new, OtherHandSpellMessage::spell);
    }

    public record ExecuteOffhand() {
    }

    public record Replace(Fragment fragment) {
        public static final Endec<Replace> ENDEC = Fragment.ENDEC.xmap(Replace::new, Replace::fragment);
    }
}
