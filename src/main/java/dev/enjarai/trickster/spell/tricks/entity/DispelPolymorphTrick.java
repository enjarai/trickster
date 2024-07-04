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
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class DispelPolymorphTrick extends Trick {
    public DispelPolymorphTrick() {
        super(Pattern.of(1, 0, 4, 8, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var entity = expectInput(fragments, FragmentType.ENTITY, 0);

        var realEntity = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (realEntity instanceof ServerPlayerEntity player) {
            var cumpoonent = player.getComponent(ModEntityCumponents.DISGUISE);

            if (cumpoonent.getUuid() != null) {
                cumpoonent.setUuid(null);
                return BooleanFragment.TRUE;
            }
        }
        return BooleanFragment.FALSE;
    }
}
