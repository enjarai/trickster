package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.entity.query.AbstractEntityQueryTrick;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class DispelPolymorphTrick extends AbstractEntityQueryTrick<LivingEntity> {
    public DispelPolymorphTrick() {
        super(Pattern.of(1, 0, 4, 8, 7), LivingEntity.class);
    }

    @Override
    public Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        if (entity instanceof ServerPlayerEntity player) {
            ctx.useMana(this, 1000);
            player.getComponent(ModEntityComponents.DISGUISE).setUuid(null);
        }

        return EntityFragment.from(entity);
    }
}
