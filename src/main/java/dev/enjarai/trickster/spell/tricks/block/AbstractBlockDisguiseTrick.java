package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.RebuildChunkPacket;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractBlockDisguiseTrick extends Trick {
    public AbstractBlockDisguiseTrick(Pattern pattern) {
        super(pattern);
    }

    protected static void updateShadow(SpellContext ctx, BlockPos blockPos) {
        ModNetworking.CHANNEL.serverHandle(ctx.source().getWorld(), blockPos).send(new RebuildChunkPacket(blockPos));

        var particlePos = blockPos.toCenterPos();
        ctx.source().getWorld().spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0
        );
    }
}
