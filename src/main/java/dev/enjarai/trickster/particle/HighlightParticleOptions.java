package dev.enjarai.trickster.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class HighlightParticleOptions implements ParticleEffect {
    public static final MapCodec<HighlightParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(p -> p.color)
    ).apply(instance, HighlightParticleOptions::new));
    public static final PacketCodec<RegistryByteBuf, HighlightParticleOptions> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, p -> p.color,
            HighlightParticleOptions::new
    );

    public final int color;

    public HighlightParticleOptions(int color) {
        this.color = color;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.HIGHLIGHT_BLOCK;
    }
}
