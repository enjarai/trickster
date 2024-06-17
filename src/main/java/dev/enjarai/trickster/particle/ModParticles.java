package dev.enjarai.trickster.particle;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModParticles {
    public static final SimpleParticleType PROTECTED_BLOCK = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registries.PARTICLE_TYPE, Trickster.id("protected_block"), PROTECTED_BLOCK);
    }
}
