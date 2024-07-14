package dev.enjarai.trickster.spell.tricks.entity;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.Tricks;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.tricks.entity.query.AbstractLivingEntityQueryTrick;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class AddVelocityTrick extends AbstractLivingEntityQueryTrick {
    public AddVelocityTrick() {
        super(Pattern.of(4, 6, 0, 1, 2, 8, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = getLivingEntity(ctx, fragments, 0);

        fragments = tryWard(ctx, target, fragments);

        var velocity = expectInput(fragments, FragmentType.VECTOR, 1);
        ctx.useMana(this, (float)velocity.vector().length() * 6);
        target.addVelocity(velocity.vector().x(), velocity.vector().y(), velocity.vector().z());
        target.velocityModified = true;
        ctx.setWorldAffected();

        return VoidFragment.INSTANCE;
    }
}
