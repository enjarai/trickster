package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Optional;

public class RangeFindEntityTrick extends Trick<RangeFindEntityTrick> {
    public RangeFindEntityTrick() {
        super(Pattern.of(3, 1, 0, 3, 6, 7, 8, 5, 7), Signature.of(FragmentType.VECTOR, FragmentType.NUMBER, FragmentType.ENTITY_TYPE.optionalOf(), RangeFindEntityTrick::run));
    }

    public Fragment run(SpellContext ctx, VectorFragment posFragment, NumberFragment rangeFragment, Optional<EntityTypeFragment> type) throws BlunderException {
        TypeFilter<Entity, ?> filter = type
                .<TypeFilter<Entity, ?>>map(EntityTypeFragment::entityType)
                .orElse(TypeFilter.instanceOf(Entity.class));
        var pos = posFragment.vector();
        var range = rangeFragment.number();

        if (range > 32.0) {
            throw new OutOfRangeBlunder(this, 32.0, range);
        }

        var squaredRange = range * range;
        var entities = new ArrayList<Entity>();
        var world = ctx.source().getWorld();

        world.collectEntitiesByType(
                filter, new Box(
                        pos.x() - range, pos.y() - range, pos.z() - range,
                        pos.x() + range, pos.y() + range, pos.z() + range
                ),
                e -> e.getPos().squaredDistanceTo(pos.x(), pos.y(), pos.z()) <= squaredRange && world.getEntity(e.getUuid()) != null, entities
        );

        return new ListFragment(entities.stream()
                .filter(EntityFragment::isValidEntity)
                .<Fragment>map(EntityFragment::from)
                .toList());
    }
}
