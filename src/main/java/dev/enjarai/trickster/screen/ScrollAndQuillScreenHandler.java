package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.PlayerSpellContext;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import io.wispforest.endec.Endec;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;

import java.util.function.Consumer;

public class ScrollAndQuillScreenHandler extends ScreenHandler {
    private final ItemStack scrollStack;

    public final SyncedProperty<SpellPart> spell = createProperty(SpellPart.class, SpellPart.ENDEC, new SpellPart());
    public final SyncedProperty<SpellPart> otherHandSpell = createProperty(SpellPart.class, SpellPart.ENDEC, new SpellPart());

    public Consumer<Fragment> replacerCallback;

    public final EquipmentSlot slot;
    public final boolean greedyEvaluation;

    public ScrollAndQuillScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null, null, null, false);
    }

    public ScrollAndQuillScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack scrollStack, ItemStack otherHandStack, EquipmentSlot slot, boolean greedyEvaluation) {
        super(ModScreenHandlers.SCROLL_AND_QUILL, syncId);

        this.scrollStack = scrollStack;
        this.slot = slot;
        this.greedyEvaluation = greedyEvaluation;

        if (scrollStack != null) {
            var spell = scrollStack.get(ModComponents.SPELL);
            if (spell != null) {
                this.spell.set(spell.spell());
            }
        }

        if (otherHandStack != null) {
            var spell = otherHandStack.get(ModComponents.SPELL);
            if (spell != null) {
                this.otherHandSpell.set(spell.spell());
            }
        }

        addServerboundMessage(SpellMessage.class, SpellMessage.ENDEC, msg -> updateSpell(msg.spell()));
        addServerboundMessage(ExecuteOffhand.class, msg -> executeOffhand());
        addClientboundMessage(Replace.class, Replace.ENDEC, msg -> {
            if (replacerCallback != null) {
                replacerCallback.accept(msg.fragment());
            }
        });
    }

    public void updateSpell(SpellPart spell) {
        if (scrollStack != null) {
            var server = player().getServer();
            if (server != null) {
                server.execute(() -> {
                    if (greedyEvaluation) {
                        var ctx = new PlayerSpellContext((ServerPlayerEntity) player(), slot).setDestructive();
                        spell.runSafely(ctx, err -> {});
                        if (ctx.hasAffectedWorld()) {
                            ((ServerPlayerEntity) player()).getServerWorld().playSoundFromEntity(
                                    null, player(), ModSounds.CAST, SoundCategory.PLAYERS, 1f, ModSounds.randomPitch(0.8f, 0.2f));
                        }
                        this.spell.set(spell);
                    }

                    scrollStack.set(ModComponents.SPELL, new SpellComponent(spell));
                });
            }
        } else {
//            var result = SpellPart.CODEC.encodeStart(JsonOps.INSTANCE, spell).result().get();
//            Trickster.LOGGER.warn(result.toString());
            sendMessage(new SpellMessage(spell));
        }
    }

    public void executeOffhand() {
        var server = player().getServer();
        if (server != null) {
            server.execute(() -> {
                if (player().getInventory().contains(ModItems.CAN_EVALUATE_DYNAMICALLY)) {
                    var fragment = otherHandSpell.get()
                            .runSafely(new PlayerSpellContext((ServerPlayerEntity) player(), slot))
                            .orElse(VoidFragment.INSTANCE);
                    ((ServerPlayerEntity) player()).getServerWorld().playSoundFromEntity(
                            null, player(), ModSounds.CAST, SoundCategory.PLAYERS, 1f, ModSounds.randomPitch(0.8f, 0.2f));
                    sendMessage(new Replace(fragment));
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

    public record ExecuteOffhand() {
    }

    public record Replace(Fragment fragment) {
        public static final Endec<Replace> ENDEC = Fragment.ENDEC.get().xmap(Replace::new, Replace::fragment);
    }
}
