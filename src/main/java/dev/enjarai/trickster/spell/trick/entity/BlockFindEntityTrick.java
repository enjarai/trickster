package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.EntityTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class BlockFindEntityTrick extends Trick {
    public BlockFindEntityTrick() {
        super(Pattern.of(2, 8, 6, 0, 2, 5, 4, 1, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var type = supposeInput(fragments, FragmentType.ENTITY_TYPE, 1);

        TypeFilter<Entity, ?> filter = type
                .<TypeFilter<Entity, ?>>map(EntityTypeFragment::entityType)
                .orElse(TypeFilter.instanceOf(Entity.class));
        var blockPos = pos.toBlockPos();

        var entities = new ArrayList<Entity>();
        ctx.source().getWorld().collectEntitiesByType(
                filter, Box.enclosing(blockPos, blockPos), e -> e.getBlockPos().equals(blockPos), entities, 1);

        return entities.stream().findFirst()
                .filter(EntityFragment::isValidEntity)
                .<Fragment>map(EntityFragment::from)
                .orElse(VoidFragment.INSTANCE);
    }
}
