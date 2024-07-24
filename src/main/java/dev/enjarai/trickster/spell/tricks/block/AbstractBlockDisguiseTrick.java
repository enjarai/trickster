package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.RebuildChunkPacket;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractBlockDisguiseTrick extends Trick {
    public AbstractBlockDisguiseTrick(Pattern pattern) {
        super(pattern);
    }

    protected static void updateShadow(SpellSource ctx, BlockPos blockPos) {
        ModNetworking.CHANNEL.serverHandle(ctx.getWorld(), blockPos).send(new RebuildChunkPacket(blockPos));
        ctx.setWorldAffected();

        var particlePos = blockPos.toCenterPos();
        ctx.getWorld().spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0
        );
    }
}
