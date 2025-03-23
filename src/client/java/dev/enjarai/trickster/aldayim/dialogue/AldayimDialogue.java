package dev.enjarai.trickster.aldayim.dialogue;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.TricksterClient;
import dev.enjarai.trickster.aldayim.Dialogue;
import dev.enjarai.trickster.aldayim.Dialogue.Option;
import dev.enjarai.trickster.aldayim.TextEntryDialogue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class AldayimDialogue {
    @SuppressWarnings({ "DataFlowIssue", "resource" })
    private final String playerName = MinecraftClient.getInstance().player.getName().getString();

    private final Dialogue menu = Dialogue.translatable("trickster_aldayim.main_menu")
            .onOpen((backend, self) -> {
                Trickster.CONFIG.skipKonIntro(true);
                return self;
            })
            .responses(
                    Option.translatable(
                            "trickster_aldayim.option.menu.import",
                            TextEntryDialogue.translatable(
                                    "trickster_aldayim.menu.import",
                                    (backend, chosenOption, input) -> {
                                        Trickster.LOGGER.warn(input);
                                    }
                            ).responses(
                                    Option.translatable("trickster_aldayim.option.ok", Dialogue.closer())
                            )
                    )
            );

    private final Dialogue start2 = Dialogue.translatable("trickster_aldayim.welcome_back")
            .responses(
                    Option.of("...", menu)
            );

    private final Option before = Option.translatable(
            "trickster_aldayim.option.before",
            Dialogue.translatable("trickster_aldayim.before_1")
                    .responses(
                            Option.of(
                                    "...",
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
                                                                                    I18n.translate("trickster_aldayim.option.pleasure_to_meet_you", playerName),
                                                                                    Dialogue.of(I18n.translate("trickster_aldayim.pleasure_to_meet_you", playerName))
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

    private final Dialogue start1 = Dialogue.of("...")
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
        TricksterClient.dialogueBackend.start(Trickster.CONFIG.skipKonIntro() ? start2 : start1);
    }
}
