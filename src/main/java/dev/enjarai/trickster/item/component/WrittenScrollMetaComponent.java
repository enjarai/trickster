package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record WrittenScrollMetaComponent(String title, String author, int generation) {
    public static final Codec<WrittenScrollMetaComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("title").forGetter(WrittenScrollMetaComponent::title),
            Codec.STRING.fieldOf("author").forGetter(WrittenScrollMetaComponent::author),
            Codec.INT.fieldOf("generation").forGetter(WrittenScrollMetaComponent::generation)
    ).apply(instance, WrittenScrollMetaComponent::new));
    public static final PacketCodec<RegistryByteBuf, WrittenScrollMetaComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, WrittenScrollMetaComponent::title,
            PacketCodecs.STRING, WrittenScrollMetaComponent::author,
            PacketCodecs.INTEGER, WrittenScrollMetaComponent::generation,
            WrittenScrollMetaComponent::new
    );
}
