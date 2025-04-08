package dev.enjarai.trickster.aldayim.dialogue;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.TricksterClient;
import dev.enjarai.trickster.aldayim.Dialogue;
import dev.enjarai.trickster.aldayim.Dialogue.Option;
import dev.enjarai.trickster.aldayim.TextEntryDialogue;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.SummonEphemeralFragmentPacket;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.StringFragment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class AldayimDialogue {
    @SuppressWarnings("DataFlowIssue")
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final String playerName = client.player.getName().getString();

    private final Dialogue menu = Dialogue.translatable("trickster_aldayim.main_menu")
            .onOpen((backend, self) -> {
                Trickster.CONFIG.skipKonIntro(true);
                return self;
            })
            .responses(
                    Option.translatable(
                            "trickster_aldayim.option.menu.import",
                            TextEntryDialogue.translatable("trickster_aldayim.menu.import", (backend, chosenOption, input) -> {
                                Fragment fragment;

                                try {
                                    fragment = Fragment.fromBase64(input);
                                    ModNetworking.CHANNEL.clientHandle().send(new SummonEphemeralFragmentPacket(fragment));
                                } catch (Throwable e) {
                                    //TODO: error handling
                                    Trickster.LOGGER.error("owo what the fucking hell happened here you fucking idiot");
                                }
                            })
                                    .responses(
                                            Option.translatable("trickster_aldayim.option.ok", Dialogue.closer())
                                    )
                    ),
                    Option.translatable(
                            "trickster_aldayim.option.menu.string_as_fragment",
                            TextEntryDialogue.translatable("trickster_aldayim.menu.string_as_fragment",
                            (backend, chosenOption, input) -> {
                                var fragment = new StringFragment(input);
                                ModNetworking.CHANNEL.clientHandle().send(new SummonEphemeralFragmentPacket(fragment));
                            })
                                    .responses(
                                            Option.translatable("trickster_aldayim.option.ok", Dialogue.closer())
                                    )
                    )
            );

    private final Dialogue start2 = Dialogue.translatable("trickster_aldayim.welcome_back")
            .responses(
                    Option.translatable("trickster_aldayim.option.continue", menu)
            );

    private final Option before = Option.translatable(
            "trickster_aldayim.option.before",
            Dialogue.translatable("trickster_aldayim.before_1")
                    .responses(
                            Option.translatable(
                                    "trickster_aldayim.option.continue",
                                    Dialogue.translatable("trickster_aldayim.before_2")
                                            .responses(
                                                    Option.translatable(
                                                            "trickster_aldayim.option.who_are_you",
                                                            Dialogue.translatable("trickster_aldayim.who_are_you")
                                                                    .onOpen((backend, self) -> {
                                                                        Trickster.CONFIG.konKnowsName(false);
                                                                        return self;
                                                                    })
                                                                    .responses(
                                                                            Option.of(
                                                                                    Text.translatable("trickster_aldayim.option.pleasure_to_meet_you", playerName),
                                                                                    Dialogue.translatable("trickster_aldayim.pleasure_to_meet_you", playerName)
                                                                                            .onOpen((backend, self) -> {
                                                                                                Trickster.CONFIG.konKnowsName(true);
                                                                                                return self;
                                                                                            })
                                                                                            .responses(
                                                                                                    Option.translatable("trickster_aldayim.option.menu_intrigued", menu)
                                                                                            )
                                                                            ),
                                                                            Option.translatable(
                                                                                    "trickster_aldayim.option.okay_and",
                                                                                    Dialogue.translatable("trickster_aldayim.okay_and")
                                                                                            .responses(
                                                                                                    Option.translatable("trickster_aldayim.option.menu_reluctant", menu)
                                                                                            )
                                                                            )
                                                                    )
                                                    )
                                            )
                            )
                    )
    );

    private final Dialogue start1 = Dialogue.translatable("trickster_aldayim.ellipses")
            .responses(
                    Option.translatable(
                            "trickster_aldayim.option.ok",
                            Dialogue.translatable("trickster_aldayim.start_1")
                                    .responses(
                                            Option.translatable(
                                                    "trickster_aldayim.option.ok",
                                                    Dialogue.translatable("trickster_aldayim.start_2")
                                                            .responses(
                                                                    Option.translatable(
                                                                            "trickster_aldayim.option.ok",
                                                                            Dialogue.translatable("trickster_aldayim.start_3")
                                                                                    .responses(
                                                                                            Option.translatable(
                                                                                                    "trickster_aldayim.option.ok",
                                                                                                    Dialogue.translatable("trickster_aldayim.start_4.ok")
                                                                                                            .responses(before)
                                                                                            ),
                                                                                            Option.translatable(
                                                                                                    "trickster_aldayim.option.i_do",
                                                                                                    Dialogue.translatable("trickster_aldayim.start_4.i_do")
                                                                                                            .responses(before)
                                                                                            )
                                                                                    )
                                                                    )
                                                            )
                                            )
                                    )
                    )
            );

    public void start() {
        if (TricksterClient.dialogueBackend.isActive()) {
            return;
        }

        TricksterClient.dialogueBackend.start(Trickster.CONFIG.skipKonIntro() ? start2 : start1);
    }
}
