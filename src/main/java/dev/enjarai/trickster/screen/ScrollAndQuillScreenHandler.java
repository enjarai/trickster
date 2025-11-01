package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.revision.RevisionContext;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.source.PlayerSpellSource;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NaNBlunder;
import io.vavr.collection.HashMap;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ScrollAndQuillScreenHandler extends ScreenHandler implements RevisionContext {
    private ItemStack scrollStack;
    private ItemStack otherHandStack;

    public final InitialData initialData;

    public Consumer<Fragment> replacerCallback;
    public Consumer<Optional<SpellPart>> updateDrawingPartCallback;

    // Client constructor
    public ScrollAndQuillScreenHandler(int syncId, PlayerInventory playerInventory, InitialData initialData) {
        super(ModScreenHandlers.SCROLL_AND_QUILL, syncId);
        this.initialData = initialData;

        addServerboundMessage(SpellMessage.class, SpellMessage.ENDEC, msg -> updateSpell(msg.spell()));
    }

    // Server constructor
    public ScrollAndQuillScreenHandler(int syncId, PlayerInventory playerInventory, InitialData initialData, ItemStack scrollStack, ItemStack otherHandStack) {
        this(syncId, playerInventory, initialData);

        this.scrollStack = scrollStack;
        this.otherHandStack = otherHandStack;
    }

    public static ExtendedScreenHandlerFactory<InitialData> factory(Text name, InitialData initialData, ItemStack mainStack, ItemStack otherHandStack) {
        return new ExtendedScreenHandlerFactory<>() {
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ScrollAndQuillScreenHandler(
                    syncId, playerInventory, initialData, mainStack, otherHandStack
                );
            }

            @Override
            public Text getDisplayName() {
                return name;
            }

            @Override
            public InitialData getScreenOpeningData(ServerPlayerEntity player) {
                return initialData;
            }
        };
    }

    public void updateSpell(SpellPart spell) {
        if (initialData.mutable()) {
            if (scrollStack != null) {
                var server = player().getServer();
                if (server != null) {
                    server.execute(() -> {
                        var spell2 = spell;

                        if (false) { // greedy
                            var executionState = new ExecutionState(List.of());
                            try {
                                spell2.destructiveRun(new SpellContext(executionState, new PlayerSpellSource((ServerPlayerEntity) player()), new TickData()));
                                //                                this.spell.set(spell2);
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
                            spell2 = spell.applyEphemeral();
                            //                            this.spell.set(spell2);
                        }

                        FragmentComponent.setValue(scrollStack, spell2, Optional.empty(), false);
                    });
                }
            } else {
                sendMessage(new SpellMessage(spell));
            }
        }
    }

    @Override
    public HashMap<Pattern, SpellPart> getMacros() {
        return HashMap.empty();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public boolean isOffhand() {
        return initialData.hand == Hand.OFF_HAND;
    }

    public record SpellMessage(SpellPart spell) {
        public static final Endec<SpellMessage> ENDEC = SpellPart.ENDEC.xmap(SpellMessage::new, SpellMessage::spell);
    }

    public record InitialData(SpellPart spell, boolean mutable, Hand hand) {
        public static final Endec<InitialData> ENDEC = StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("spell", InitialData::spell),
            Endec.BOOLEAN.fieldOf("mutable", InitialData::mutable),
            Endec.forEnum(Hand.class).fieldOf("hand", InitialData::hand),
            InitialData::new
        );
    }
}
