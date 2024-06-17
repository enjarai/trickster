package dev.enjarai.trickster.spell.tricks.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class RaycastTrick extends Trick {
    public RaycastTrick() {
        super(Pattern.of(3, 4, 5, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var player = ctx.getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        var hit = player.getWorld().raycast(new RaycastContext(
                player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(24d)),
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player
        ));
        return hit.getType() == HitResult.Type.MISS ? VoidFragment.INSTANCE : VectorFragment.of(hit.getBlockPos());
    }
}
