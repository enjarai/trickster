package dev.enjarai.trickster.block;

import dev.enjarai.trickster.ModSounds;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

public interface SpellCastingBlockEntity {
    default void playCastSound(ServerWorld world, BlockPos pos, float startPitch, float pitchRange) {
        world.playSound(null, pos, ModSounds.CAST, SoundCategory.PLAYERS, 1f, ModSounds.randomPitch(startPitch, pitchRange));
    }
}
