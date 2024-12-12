package dev.enjarai.trickster.spell.trick.wristpocket;

import com.mojang.datafixers.util.Either;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.Entity;
import org.joml.Vector3dc;

import java.util.List;
import java.util.Optional;

public class MageHandTrick extends Trick {
    public MageHandTrick() {
        super(Pattern.of(7,4,1,2,8,4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        Either<Entity, VectorFragment> target = expectEitherInput(fragments, FragmentType.ENTITY, FragmentType.VECTOR, 0)
                .mapLeft(entityFragment -> entityFragment
                    .getEntity(ctx).orElseThrow(() -> new EntityInvalidBlunder(this)));

        var position = target.map(e -> e.getPos().toVector3d(), VectorFragment::vector);

        var face = supposeInput(fragments, FragmentType.VECTOR, 1).map(VectorFragment::toDirection);
        face.ifPresent(dir -> fragments.remove(1));

        var crouch = supposeInput(fragments, 1).map(Fragment::asBoolean);

        var wristpocket = ctx.source().getComponent(ModEntityComponents.WRIST_POCKET)
            .orElseThrow(() -> new IncompatibleSourceBlunder(this));

        ctx.useMana(this, (float) (Math.max(0.0, position.distance(ctx.source().getPos()) - 3.0f)));

        target.ifLeft(entity -> wristpocket.useOnEntity(entity, crouch, ctx.source().getWorld()));
        target.ifRight(block -> wristpocket.useOnBlock(block.toBlockPos(), face, crouch, ctx.source().getWorld()));

        return fragments.removeFirst();
    }
}
