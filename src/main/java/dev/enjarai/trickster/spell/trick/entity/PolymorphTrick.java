package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.trick.entity.query.AbstractLivingEntityQueryTrick;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class PolymorphTrick extends AbstractLivingEntityQueryTrick {
    public PolymorphTrick() {
        super(Pattern.of(4, 2, 1, 0, 4, 8, 7, 6, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var realSource = getLivingEntity(ctx, fragments, 1);
        fragments = tryWard(ctx, realSource, fragments);

        var realTarget = getLivingEntity(ctx, fragments, 0);

        if (realSource.getUuid().equals(realTarget.getUuid()))
            return VoidFragment.INSTANCE;

        expectCanInteract(ctx, realTarget);

        if (realTarget instanceof ServerPlayerEntity targetPlayer && realSource instanceof ServerPlayerEntity sourcePlayer) {
            ctx.useMana(this, 480);

            var cumpoonent = targetPlayer.getComponent(ModEntityCumponents.DISGUISE);
            var sourceCumponent = sourcePlayer.getComponent(ModEntityCumponents.DISGUISE);
            var uuid = sourcePlayer.getUuid();

            if (sourceCumponent.getUuid() != null) {
                uuid = sourceCumponent.getUuid();
            }

            cumpoonent.setUuid(uuid);
        } else {
            throw new UnknownEntityBlunder(this);
        }

        return VoidFragment.INSTANCE;
    }
}
