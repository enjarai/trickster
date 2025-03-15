package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.entity.query.AbstractLivingEntityQueryTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class DispelPolymorphTrick extends AbstractLivingEntityQueryTrick {
    public DispelPolymorphTrick() {
        super(Pattern.of(1, 0, 4, 8, 7));
    }

    @Override
    public Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        if (entity instanceof ServerPlayerEntity player) {
            ArgType.tryWard(this, ctx, EntityFragment.from(entity), List.of());
            ctx.useMana(this, 1000);
            player.getComponent(ModEntityComponents.DISGUISE).setUuid(null);
        }

        return EntityFragment.from(entity);
    }
}
