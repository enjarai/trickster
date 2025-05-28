package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class BlockFindEntityTrick extends Trick<BlockFindEntityTrick> {
    public BlockFindEntityTrick() {
        super(Pattern.of(2, 8, 6, 0, 2, 5, 4, 1, 2), Signature.of(FragmentType.VECTOR, variadic(FragmentType.ENTITY_TYPE).unpack(), BlockFindEntityTrick::find));
    }

    public Fragment find(SpellContext ctx, VectorFragment pos, List<EntityTypeFragment> typeFragments) throws BlunderException {
        var world = ctx.source().getWorld();
        var blockPos = pos.toBlockPos();
        var types = new ArrayList<EntityType<?>>(typeFragments.size());

        for (var type : typeFragments) {
            types.add(type.entityType());
        }

        var entities = new ArrayList<Entity>();
        ctx.source().getWorld().collectEntitiesByType(
                TypeFilter.instanceOf(Entity.class), Box.enclosing(blockPos, blockPos),
                e -> e.getBlockPos().equals(blockPos) && world.getEntity(e.getUuid()) != null && (types.isEmpty() || types.contains(e.getType())), entities, 1);

        return entities.stream().findFirst()
                .filter(EntityFragment::isValidEntity)
                .<Fragment>map(EntityFragment::from)
                .orElse(VoidFragment.INSTANCE);
    }
}
