package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.server.network.ServerPlayerEntity;

public class PolymorphTrick extends Trick<PolymorphTrick> {
    public PolymorphTrick() {
        super(Pattern.of(4, 2, 1, 0, 4, 8, 7, 6, 4), Signature.of(FragmentType.ENTITY, FragmentType.ENTITY, PolymorphTrick::morph, FragmentType.VOID));
    }

    public VoidFragment morph(SpellContext ctx, EntityFragment source, EntityFragment target) throws BlunderException {
        var realSource = source.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));
        var realTarget = target.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (realSource.getUuid().equals(realTarget.getUuid()))
            return VoidFragment.INSTANCE;

        if (realTarget instanceof ServerPlayerEntity targetPlayer && realSource instanceof ServerPlayerEntity sourcePlayer) {
            ctx.useMana(this, 8000);

            var component = targetPlayer.getComponent(ModEntityComponents.DISGUISE);
            var sourceComponent = sourcePlayer.getComponent(ModEntityComponents.DISGUISE);
            var uuid = sourcePlayer.getUuid();

            if (sourceComponent.getUuid() != null) {
                uuid = sourceComponent.getUuid();
            }

            component.setUuid(uuid);
        } else {
            throw new UnknownEntityBlunder(this);
        }

        return VoidFragment.INSTANCE;
    }
}
