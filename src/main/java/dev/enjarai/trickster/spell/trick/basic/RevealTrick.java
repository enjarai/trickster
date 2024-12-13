package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.owo.config.ConfigSynchronizer;
import io.wispforest.owo.config.Option;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public class RevealTrick extends Trick {
    public RevealTrick() {
        super(Pattern.of(3, 4, 5, 8, 7, 6, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var first = expectInput(fragments, 0);
        var result = first.asFormattedText();

        if (fragments.size() > 1) {
            var building = Text.literal("(");

            for (int i = 0; i < fragments.size(); i++) {
                var frag = fragments.get(i);
                if (i != 0) {
                    building = building.append(", ");
                }
                building = building.append(frag.asFormattedText());
            }

            result = building.append(")");;
        }

        Text finalResult = result;
        ctx.source().getPlayer().ifPresent(player -> {
            player.sendMessage(
                    Text.of(finalResult),
                    ModNetworking.clientOrDefault(player, Trickster.CONFIG.keys.revealToHotbar, Trickster.CONFIG.revealToHotbar())
            );
        });

        return first;
    }
}
