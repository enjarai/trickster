package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.revision.Revision;
import dev.enjarai.trickster.spell.revision.RevisionContext;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.revision.Revisions;
import io.vavr.collection.HashMap;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class ScrollAndQuillScreenHandler extends ScreenHandler implements RevisionContext {
    // Common fields
    public final InitialData initialData;

    // Client fields
    private final Int2ObjectMap<Consumer<SpellPart>> syncedReplacements = new Int2ObjectOpenHashMap<>();

    // Server fields
    private ItemStack scrollStack;
    private ItemStack otherHandStack;
    private SpellPart currentSpellPart;
    private HashMap<Pattern, SpellPart> macros;

    // Client constructor
    public ScrollAndQuillScreenHandler(int syncId, PlayerInventory playerInventory, InitialData initialData) {
        super(ModScreenHandlers.SCROLL_AND_QUILL, syncId);
        this.initialData = initialData;

        addServerboundMessage(SpellMessage.class, SpellMessage.ENDEC, msg -> updateSpell(msg.spell()));
        addServerboundMessage(DelegateRevisionToServer.class, DelegateRevisionToServer.ENDEC, msg -> {
            var view = SpellView.index(currentSpellPart).traverseTo(msg.path());
            if (view != null) {
                delegateToServer(msg.sync(), msg.revision(), view, null);
            }
        });

        addClientboundMessage(ReplyToClient.class, ReplyToClient.ENDEC, msg -> {
            var handler = syncedReplacements.get(msg.sync());
            if (handler != null) {
                syncedReplacements.remove(msg.sync());
                handler.accept(msg.replacement());
            }
        });
    }

    // Server constructor
    public ScrollAndQuillScreenHandler(int syncId, PlayerInventory playerInventory, InitialData initialData, ItemStack scrollStack, ItemStack otherHandStack, HashMap<Pattern, SpellPart> macros) {
        this(syncId, playerInventory, initialData);

        this.scrollStack = scrollStack;
        this.otherHandStack = otherHandStack;
        this.currentSpellPart = initialData.spell;
        this.macros = macros;
    }

    public static ExtendedScreenHandlerFactory<InitialData> factory(Text name, InitialData initialData, ItemStack mainStack, ItemStack otherHandStack, HashMap<Pattern, SpellPart> macros) {
        return new ExtendedScreenHandlerFactory<>() {
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ScrollAndQuillScreenHandler(
                    syncId, playerInventory, initialData, mainStack, otherHandStack, macros
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
                        currentSpellPart = spell;

                        //                        if (false) { // greedy
                        //                            var executionState = new ExecutionState(List.of());
                        //                            try {
                        //                                spell2.destructiveRun(new SpellContext(executionState, new PlayerSpellSource((ServerPlayerEntity) player()), new TickData()));
                        //                                //                                this.spell.set(spell2);
                        //                            } catch (BlunderException e) {
                        //                                if (e instanceof NaNBlunder)
                        //                                    ModCriteria.NAN_NUMBER.trigger((ServerPlayerEntity) player());
                        //
                        //                                player().sendMessage(e.createMessage().append(" (").append(executionState.formatStackTrace()).append(")"));
                        //                            } catch (Exception e) {
                        //                                player().sendMessage(Text.literal("Uncaught exception in spell: " + e.getMessage())
                        //                                    .append(" (").append(executionState.formatStackTrace()).append(")"));
                        //                            }
                        //
                        //                            ((ServerPlayerEntity) player()).getServerWorld().playSoundFromEntity(
                        //                                null, player(), ModSounds.CAST, SoundCategory.PLAYERS, 1f, ModSounds.randomPitch(0.8f, 0.2f));
                        //                        } else {
                        //                            spell2 = spell.applyEphemeral();
                        //                            //                            this.spell.set(spell2);
                        //                        }

                        FragmentComponent.setValue(scrollStack, currentSpellPart.applyEphemeral(), Optional.empty(), false);
                    });
                }
            } else {
                sendMessage(new SpellMessage(spell));
            }
        }
    }

    @Override
    public void delegateToServer(Revision revision, SpellView view, Consumer<SpellPart> responseHandler) {
        delegateToServer(player().getRandom().nextInt(), revision.pattern(), view, responseHandler);
    }

    private void delegateToServer(int sync, Pattern revision, SpellView view, Consumer<SpellPart> responseHandler) {
        var server = player().getServer();
        if (server != null) {
            server.execute(() -> {
                var macro = macros.get(revision);
                if (macro.isDefined()) {

                    return;
                }

                Revisions.lookup(revision).ifPresent(r -> {
                    r.applyServer(this, view, part -> sendMessage(new ReplyToClient(sync, part)));
                });
            });
        } else {
            syncedReplacements.put(sync, part -> {
                if (view.beingReplaced) {
                    view.beingReplaced = false;
                    responseHandler.accept(part);
                }
            });
            view.beingReplaced = true;
            sendMessage(new DelegateRevisionToServer(sync, revision, view.getPath()));
        }
    }

    @Override
    public HashMap<Pattern, SpellPart> getMacros() {
        return HashMap.empty();
    }

    @Override
    public @Nullable ItemStack getOtherHandStack() {
        return otherHandStack;
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

    public record DelegateRevisionToServer(int sync, Pattern revision, List<Integer> path) {
        public static final Endec<DelegateRevisionToServer> ENDEC = StructEndecBuilder.of(
            Endec.INT.fieldOf("sync", DelegateRevisionToServer::sync),
            Pattern.ENDEC.fieldOf("revision", DelegateRevisionToServer::revision),
            Endec.INT.listOf().fieldOf("path", DelegateRevisionToServer::path),
            DelegateRevisionToServer::new
        );
    }

    public record ReplyToClient(int sync, SpellPart replacement) {
        public static final Endec<ReplyToClient> ENDEC = StructEndecBuilder.of(
            Endec.INT.fieldOf("sync", ReplyToClient::sync),
            SpellPart.ENDEC.fieldOf("replacement", ReplyToClient::replacement),
            ReplyToClient::new
        );
    }

    public record InitialData(SpellPart spell, boolean mutable, Hand hand, int hash, Set<Pattern> macros) {
        public static final Endec<InitialData> ENDEC = StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("spell", InitialData::spell),
            Endec.BOOLEAN.fieldOf("mutable", InitialData::mutable),
            Endec.forEnum(Hand.class).fieldOf("hand", InitialData::hand),
            Endec.INT.fieldOf("hash", InitialData::hash),
            Pattern.ENDEC.setOf().fieldOf("macros", InitialData::macros),
            InitialData::new
        );
    }
}
