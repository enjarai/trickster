package dev.enjarai.trickster;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class TricksterCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("trickster")
                .then(literal("killSpells")
                        .executes(TricksterCommand::killSpells)
                )
        );
    }

    private static int killSpells(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().getPlayerOrThrow().getComponent(ModEntityCumponents.CASTER).killAll();
        context.getSource().sendFeedback(() -> Text.literal("Killed running spells."), true);
        return 1;
    }
}
