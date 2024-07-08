package dev.enjarai.trickster.spell.tricks.entity;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.tricks.entity.query.AbstractLivingEntityQueryTrick;
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
            ctx.useMana(this, 70);

            var cumpoonent = player.getComponent(ModEntityCumponents.DISGUISE);

            if (cumpoonent.getUuid() != null) {
                cumpoonent.setUuid(null);
                return BooleanFragment.TRUE;
            }
        }

        return BooleanFragment.FALSE;
    }
}
