package dev.enjarai.trickster.spell.tricks;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class RevealTrick extends Trick {
    public RevealTrick() {
        super(Pattern.of(3, 4, 5, 8, 7, 6, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var result = expectInput(fragments, 0).asText();

        if (fragments.size() > 1) {
            var building = Text.literal("(");

            for (int i = 0; i < fragments.size(); i++) {
                var frag = fragments.get(i);
                if (i != 0) {
                    building = building.append(", ");
                }
                building = building.append(frag.asText());
            }

            result = building.append(")");;
        }

        Text finalResult = result;
        ctx.getPlayer().ifPresent(player -> {
            player.sendMessage(Text.of(finalResult));
        });

        return VoidFragment.INSTANCE;
    }
}
