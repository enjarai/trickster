package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.text.Text;

import java.util.List;

public class RevealTrick extends Trick<RevealTrick> {
    public RevealTrick() {
        super(Pattern.of(3, 4, 5, 8, 7, 6, 3), Signature.of(ArgType.ANY.variadicOfArg().require(), RevealTrick::reveal, RetType.ANY));
    }

    public Fragment reveal(SpellContext ctx, List<Fragment> fragments) {
        var first = fragments.getFirst();
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
        ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this)).sendMessage(Text.of(finalResult), false);

        return first;
    }
}
