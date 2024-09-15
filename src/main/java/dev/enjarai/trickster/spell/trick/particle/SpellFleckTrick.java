package dev.enjarai.trickster.spell.trick.particle;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.fleck.SpellFleck;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import org.joml.Vector3f;

import java.util.List;

public class SpellFleckTrick extends Trick {
    public SpellFleckTrick() {
        super(Pattern.of(3, 4, 5, 8, 7, 6, 3, 0, 1, 2, 5));
    }
    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {

        var id = expectInput(fragments,FragmentType.NUMBER, 0).asInt();
        var position = expectInput(fragments, FragmentType.VECTOR, 1).vector();
        var facing = expectInput(fragments, FragmentType.VECTOR, 2).vector();
        var spell = expectInput(fragments,FragmentType.SPELL_PART, 3);
        var entities = supposeInput(fragments, FragmentType.LIST, 4);

        ctx.source().getWorld().getPlayers().stream().filter(n -> true).forEach(player ->
            player.getComponent(ModEntityCumponents.FLECKS).addFleck(id,new SpellFleck(
                position.get(new Vector3f()),
                facing.get(new Vector3f()),
                spell))
        );

        return fragments.getFirst();
    }
}
