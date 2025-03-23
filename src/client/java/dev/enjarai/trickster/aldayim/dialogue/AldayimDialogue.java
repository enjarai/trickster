package dev.enjarai.trickster.aldayim.dialogue;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.TricksterClient;
import dev.enjarai.trickster.aldayim.Dialogue;
import dev.enjarai.trickster.aldayim.Dialogue.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class AldayimDialogue {
    @SuppressWarnings("resource")
    private static final String PLAYER_NAME = MinecraftClient.getInstance().player.getName().getString();

    private static final Dialogue MENU = Dialogue.translatable("trickster_aldayim.main_menu")
            .onOpen((backend, self) -> {
                Trickster.CONFIG.skipKonIntro(true);
                return self;
            })
            .responses(
                    Option.translatable(
                            "trickster_aldayim.option.menu.import",
                            Dialogue.translatable("trickster_aldayim.menu.import")
                    )
            );

    private static final Dialogue START_2 = Dialogue.translatable("trickster_aldayim.welcome_back")
            .responses(
                    Option.of(Text.literal("..."), MENU)
            );

    private static final Option BEFORE = Option.translatable(
            "trickster_aldayim.option.before",
            Dialogue.translatable("trickster_aldayim.before_1")
                    .responses(
                            Option.of(
                                    Text.literal("..."),
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
                                                                                    Text.translatable("trickster_aldayim.option.pleasure_to_meet_you", PLAYER_NAME),
                                                                                    Dialogue.of(Text.translatable("trickster_aldayim.pleasure_to_meet_you", PLAYER_NAME))
                                                                                            .onOpen((backend, self) -> {
                                                                                                Trickster.CONFIG.konKnowsName(true);
                                                                                                return self;
                                                                                            })
                                                                                            .responses(
                                                                                                    Option.translatable("trickster_aldayim.option.menu_intrigued", MENU)
                                                                                            )
                                                                            ),
                                                                            Option.translatable(
                                                                                    "trickster_aldayim.option.okay_and",
                                                                                    Dialogue.translatable("trickster_aldayim.okay_and")
                                                                                            .responses(
                                                                                                    Option.translatable("trickster_aldayim.option.menu_reluctant", MENU)
                                                                                            )
                                                                            )
                                                                    )
                                                    )
                                            )
                            )
                    )
    );
    private static final Dialogue START_1 = Dialogue.of(Text.literal("..."))
            .responses(
                    Option.of(
                            Text.literal("..."),
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
                                                                                                            .responses(BEFORE)
                                                                                            ),
                                                                                            Option.translatable(
                                                                                                    "trickster_aldayim.option.i_do",
                                                                                                    Dialogue.translatable("trickster_aldayim.start_4.i_do")
                                                                                                            .responses(BEFORE)
                                                                                            )
                                                                                    )
                                                                    )
                                                            )
                                            )
                                    )
                    )
            );

    public static void start() {
        TricksterClient.dialogueBackend.start(Trickster.CONFIG.skipKonIntro() ? START_2 : START_1);
    }
}
