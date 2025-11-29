package dev.enjarai.trickster.particle;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModParticles {
    public static final ParticleType<SpellParticleOptions> SPELL = FabricParticleTypes.complex(SpellParticleOptions.CODEC, SpellParticleOptions.PACKET_CODEC);
    public static final ParticleType<HighlightParticleOptions> HIGHLIGHT_BLOCK = FabricParticleTypes.complex(HighlightParticleOptions.CODEC, HighlightParticleOptions.PACKET_CODEC);

    public static final SpellParticleOptions PROTECTED_BLOCK = new SpellParticleOptions(-0x66666601);
    public static final SpellParticleOptions SPELL_WHITE = new SpellParticleOptions(0xffffff);

    public static void register() {
        Registry.register(Registries.PARTICLE_TYPE, Trickster.id("protected_block"), HIGHLIGHT_BLOCK);
        Registry.register(Registries.PARTICLE_TYPE, Trickster.id("spell"), SPELL);
    }
}
