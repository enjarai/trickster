package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class RevealActionBarTrick extends Trick<RevealActionBarTrick> {
    public RevealActionBarTrick() {
        super(Pattern.of(6, 4, 8, 5, 4, 3, 6, 7, 8), Signature.of(ArgType.ANY.variadicOfArg().optionalOfArg(), RevealActionBarTrick::reveal, RetType.ANY));
    }

    public Fragment reveal(SpellContext ctx, Optional<List<Fragment>> optionalFragments) throws BlunderException {
        ServerPlayerEntity playerEntity = ctx.source().getPlayer().orElseThrow((() -> new NoPlayerBlunder(this)));

        if (optionalFragments.isEmpty()) {
            playerEntity.sendMessage(Text.empty(), true);
            return VoidFragment.INSTANCE;
        }

        List<Fragment> fragments = optionalFragments.get();
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
        playerEntity.sendMessage(Text.of(finalResult), true);

        return first;
    }
}
