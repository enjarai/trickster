package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.Entity;

import java.util.List;

public class GetFlecksTrick extends Trick {
    public GetFlecksTrick() {
        super(Pattern.of(0,4,8,7,4,1,2,4,6)); // bluetooth symbol
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new ListFragment(
            supposeInput(fragments, FragmentType.ENTITY, 0).map(n -> n.getEntity(ctx))
                .orElseGet(() -> ctx.source().getPlayer().map(n -> n)).map(ModEntityComponents.FLECKS::get)
                .orElseThrow(() -> new NoPlayerBlunder(this))
                .getRenderFlecks().stream().<Fragment>map(n -> new NumberFragment(n.id())).toList()
        );
    }
}