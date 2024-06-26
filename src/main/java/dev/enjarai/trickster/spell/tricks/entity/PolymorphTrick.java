package dev.enjarai.trickster.spell.tricks.entity;

import dev.enjarai.trickster.cca.ModCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
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
        var target = expectInput(fragments, FragmentType.ENTITY, 0);
        var source = expectInput(fragments, FragmentType.ENTITY, 1);

        var realTarget = target.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));
        var realSource = source.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (realTarget instanceof ServerPlayerEntity targetPlayer && realSource instanceof ServerPlayerEntity sourcePlayer) {
            var cumpoonent = targetPlayer.getComponent(ModCumponents.DISGUISE);

            var uuid = sourcePlayer.getUuid();
            var sourceCumponent = sourcePlayer.getComponent(ModCumponents.DISGUISE);
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
