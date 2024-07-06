package dev.enjarai.trickster.spell.tricks.entity;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class PolymorphTrick extends Trick {
    public PolymorphTrick() {
        super(Pattern.of(4, 2, 1, 0, 4, 8, 7, 6, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var source = expectInput(fragments, FragmentType.ENTITY, 1);
        var realSource = source.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        fragments = tryWard(ctx, realSource, fragments);

        var target = expectInput(fragments, FragmentType.ENTITY, 0);
        var realTarget = target.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

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
