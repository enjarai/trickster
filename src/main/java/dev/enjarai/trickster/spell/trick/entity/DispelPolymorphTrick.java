package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.entity.query.AbstractEntityQueryTrick;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class DispelPolymorphTrick extends AbstractEntityQueryTrick<LivingEntity, EntityFragment> {
    public DispelPolymorphTrick() {
        super(Pattern.of(1, 0, 4, 8, 7), LivingEntity.class, FragmentType.ENTITY);
    }

    @Override
    public EntityFragment run(SpellContext ctx, LivingEntity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            ctx.useMana(this, 1000);
            player.getComponent(ModEntityComponents.DISGUISE).setUuid(null);
        }

        return EntityFragment.from(entity);
    }
}
