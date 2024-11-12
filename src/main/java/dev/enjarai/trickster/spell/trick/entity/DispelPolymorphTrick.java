package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.entity.query.AbstractLivingEntityQueryTrick;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class DispelPolymorphTrick extends AbstractLivingEntityQueryTrick {
    public DispelPolymorphTrick() {
        super(Pattern.of(1, 0, 4, 8, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = getLivingEntity(ctx, fragments, 0);

        if (target instanceof ServerPlayerEntity player) {
            ctx.useMana(this, 1000);
            player.getComponent(ModEntityComponents.DISGUISE).setUuid(null);
        }

        return EntityFragment.from(target);
    }
}
