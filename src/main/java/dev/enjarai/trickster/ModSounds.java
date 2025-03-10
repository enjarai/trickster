package dev.enjarai.trickster;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;

public class ModSounds {
    public static final SoundEvent DRAW = register("draw");
    public static final SoundEvent COMPLETE = register("complete");
    public static final SoundEvent CAST = register("cast");
    public static final SoundEvent COLLAR_BELL = register("collar_bell");

    private static SoundEvent register(String path) {
        var id = Trickster.id(path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {

    }

    private static final Random PITCH_RANDOM = new LocalRandom(0xCAFEBABE);

    public static float randomPitch(float start, float range) {
        return start - range + PITCH_RANDOM.nextFloat() * range * 2;
    }
}
