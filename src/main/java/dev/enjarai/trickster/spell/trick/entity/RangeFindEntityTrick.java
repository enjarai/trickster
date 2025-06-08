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
import net.minecraft.entity.EntityType;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class RangeFindEntityTrick extends Trick<RangeFindEntityTrick> {
    public RangeFindEntityTrick() {
        super(Pattern.of(3, 1, 0, 3, 6, 7, 8, 5, 7), Signature.of(FragmentType.VECTOR, FragmentType.NUMBER, variadic(FragmentType.ENTITY_TYPE).unpack(), RangeFindEntityTrick::run, FragmentType.ENTITY.listOf()));
    }

    public ListFragment run(SpellContext ctx, VectorFragment posFragment, NumberFragment rangeFragment, List<EntityTypeFragment> typeFragments) throws BlunderException {
        var types = new ArrayList<EntityType<?>>(typeFragments.size());
        var pos = posFragment.vector();
        var range = rangeFragment.number();

        if (range > 32.0) {
            throw new OutOfRangeBlunder(this, 32.0, range);
        }

        for (var type : typeFragments) {
            types.add(type.entityType());
        }

        var squaredRange = range * range;
        var entities = new ArrayList<Entity>();
        var world = ctx.source().getWorld();

        world.collectEntitiesByType(
                TypeFilter.instanceOf(Entity.class), new Box(
                        pos.x() - range, pos.y() - range, pos.z() - range,
                        pos.x() + range, pos.y() + range, pos.z() + range
                ),
                e -> e.getPos().squaredDistanceTo(pos.x(), pos.y(), pos.z()) <= squaredRange && world.getEntity(e.getUuid()) != null && (types.isEmpty() || types.contains(e.getType())),
                entities
        );

        return new ListFragment(entities.stream()
                .filter(EntityFragment::isValidEntity)
                .<Fragment>map(EntityFragment::from)
                .toList());
    }
}
