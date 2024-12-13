package dev.enjarai.trickster;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.net.GrabClipboardSpellPacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import static net.minecraft.server.command.CommandManager.literal;

public class TricksterCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("trickster")
                        .then(
                                literal("killSpells")
                                        .requires(ServerCommandSource::isExecutedByPlayer)
                                        .executes(TricksterCommand::killSpells)
                        )
                        .then(
                                literal("exportSpell")
                                        .requires(ServerCommandSource::isExecutedByPlayer)
                                        .executes(TricksterCommand::exportSpell)
                        )
                        .then(
                                literal("importSpell")
                                        .requires(ServerCommandSource::isExecutedByPlayer)
                                        .requires(s -> s.hasPermissionLevel(2))
                                        .executes(TricksterCommand::importSpell)
                        )
                        .then(
                                literal("fillKnot")
                                        .requires(ServerCommandSource::isExecutedByPlayer)
                                        .requires(s -> s.hasPermissionLevel(2))
                                        .executes(TricksterCommand::fillKnot)
                        )
        );
    }

    private static int exportSpell(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        for (var hand : Hand.values()) {
            var stack = player.getStackInHand(hand);
            var spell = FragmentComponent.getSpellPart(stack);
            if (spell.isPresent()) {
                var string = spell.get().toBase64();
                context.getSource().sendFeedback(
                        () -> Text.literal("Base64 spell string: ")
                                .append(
                                        Text.literal(string)
                                                .fillStyle(
                                                        Style.EMPTY
                                                                .withUnderline(true)
                                                                .withColor(Formatting.GREEN)
                                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to copy")))
                                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, string))
                                                )
                                ),
                        false
                );

                return 1;
            }
        }

        context.getSource().sendError(Text.literal("Must be holding an item with an inscribed spell."));
        return 0;
    }

    private static int importSpell(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(() -> Text.literal("Importing spell from clipboard..."), true);
        ModNetworking.CHANNEL.serverHandle(context.getSource().getPlayer()).send(new GrabClipboardSpellPacket());
        return 1;
    }

    public static void importCallback(ServerPlayerEntity player, SpellPart spell) {
        if (player.hasPermissionLevel(2)) {
            var stack = ModItems.SCROLL_AND_QUILL.getDefaultStack();
            stack.set(ModComponents.FRAGMENT, new FragmentComponent(spell));

            if (!player.giveItemStack(stack)) {
                player.dropItem(stack, false);
            }

            player.sendMessage(Text.literal("Gave 1 scroll"));
        }
    }

    private static int killSpells(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().getPlayerOrThrow().getComponent(ModEntityComponents.CASTER).killAll();
        context.getSource().sendFeedback(() -> Text.literal("Killed running spells."), true);
        return 1;
    }

    private static int fillKnot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        for (var hand : Hand.values()) {
            var stack = player.getStackInHand(hand);
            var comp = stack.get(ModComponents.MANA);

            if (comp != null) {
                var pool = comp.pool().makeClone(context.getSource().getWorld());
                pool.refill(pool.getMax(context.getSource().getWorld()), context.getSource().getWorld());
                stack.set(ModComponents.MANA, new ManaComponent(pool));
                player.sendMessage(Text.literal("Mana refilled"));
                return 0;
            }
        }

        context.getSource().sendError(Text.literal("Must be holding an item capable of storing mana."));
        return 0;
    }
}
