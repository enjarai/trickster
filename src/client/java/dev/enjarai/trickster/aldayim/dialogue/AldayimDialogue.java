package dev.enjarai.trickster.aldayim.dialogue;

import dev.enjarai.trickster.aldayim.Dialogue;
import dev.enjarai.trickster.aldayim.Dialogue.Option;
import net.minecraft.text.Text;

public class AldayimDialogue {
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
                                                            Dialogue.translatable("trickster_aldayim.who_are_you") //TODO: responses
                                                    )
                                            )
                            )
                    )
    );
    public static final Dialogue INSTANCE = Dialogue.of(Text.literal("..."))
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
}
