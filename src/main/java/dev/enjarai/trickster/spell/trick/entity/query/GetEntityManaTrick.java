package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class GetEntityManaTrick extends AbstractLivingEntityQueryTrick {
    public GetEntityManaTrick() {
        super(Pattern.of(3, 4, 0, 3, 6, 8, 5, 2, 4, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var arg = expectInput(fragments, 0);

        Fragment result = supposeType(arg, FragmentType.ENTITY).map(entity -> {
            var target = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));
            if (!(target instanceof LivingEntity)) {
                throw new EntityInvalidBlunder(this);
            }

            return new NumberFragment(ModEntityComponents.MANA.get(target).get());
        }).orElse(null);

        if (result == null) {
            result = supposeType(arg, FragmentType.VECTOR).map(vec -> {
                var target = ctx.source().getWorld().getBlockEntity(vec.toBlockPos());
                if (!(target instanceof SpellCircleBlockEntity)) {
                    throw new BlockInvalidBlunder(this);
                }

                return new NumberFragment(((SpellCircleBlockEntity) target).manaPool.get());
            }).orElse(null);
        }

        if (result == null) {
            throw new IncorrectFragmentBlunder(this, 0,
                    FragmentType.ENTITY.getName().append(" | ").append(FragmentType.VECTOR.getName()), arg);
        }

        return result;
    }
}
