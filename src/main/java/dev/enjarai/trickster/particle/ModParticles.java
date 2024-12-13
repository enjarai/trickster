package dev.enjarai.trickster.particle;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModParticles {
    public static final SimpleParticleType PROTECTED_BLOCK = FabricParticleTypes.simple();
    public static final ParticleType<SpellParticleOptions> SPELL = FabricParticleTypes.complex(SpellParticleOptions.CODEC, SpellParticleOptions.PACKET_CODEC);

    public static final SpellParticleOptions SPELL_WHITE = new SpellParticleOptions(0xffffff);

    public static void register() {
        Registry.register(Registries.PARTICLE_TYPE, Trickster.id("protected_block"), PROTECTED_BLOCK);
        Registry.register(Registries.PARTICLE_TYPE, Trickster.id("spell"), SPELL);
    }
}
