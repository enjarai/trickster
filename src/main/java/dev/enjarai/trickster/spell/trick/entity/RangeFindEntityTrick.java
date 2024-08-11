package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class RangeFindEntityTrick extends Trick {
    public RangeFindEntityTrick() {
        super(Pattern.of(3, 1, 0, 3, 6, 7, 8, 5, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var posFragment = expectInput(fragments, FragmentType.VECTOR, 0);
        var rangeFragment = expectInput(fragments, FragmentType.NUMBER, 1);
        var type = supposeInput(fragments, FragmentType.ENTITY_TYPE, 2);

        TypeFilter<Entity, ?> filter = type
                .<TypeFilter<Entity, ?>>map(EntityTypeFragment::entityType)
                .orElse(TypeFilter.instanceOf(Entity.class));
        var pos = posFragment.vector();
        var range = rangeFragment.number();
        var squaredRange = range * range;

        var entities = new ArrayList<Entity>();
        ctx.source().getWorld().collectEntitiesByType(
                filter, new Box(
                        pos.x() - range, pos.y() - range, pos.z() - range,
                        pos.x() + range, pos.y() + range, pos.z() + range
                ),
                e -> e.getPos().squaredDistanceTo(pos.x(), pos.y(), pos.z()) <= squaredRange, entities
        );

        return new ListFragment(entities.stream()
                .filter(EntityFragment::isValidEntity)
                .<Fragment>map(EntityFragment::from)
                .toList());
    }
}
