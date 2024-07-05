package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.Nullable;

public record WrittenScrollMetaComponent(String title, String author, int generation, boolean executable) {
    public static final Codec<WrittenScrollMetaComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("title").forGetter(WrittenScrollMetaComponent::title),
            Codec.STRING.fieldOf("author").forGetter(WrittenScrollMetaComponent::author),
            Codec.INT.fieldOf("generation").forGetter(WrittenScrollMetaComponent::generation),
            Codec.BOOL.fieldOf("executable").forGetter(WrittenScrollMetaComponent::executable)
    ).apply(instance, WrittenScrollMetaComponent::new));
    public static final PacketCodec<RegistryByteBuf, WrittenScrollMetaComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, WrittenScrollMetaComponent::title,
            PacketCodecs.STRING, WrittenScrollMetaComponent::author,
            PacketCodecs.INTEGER, WrittenScrollMetaComponent::generation,
            PacketCodecs.BOOL, WrittenScrollMetaComponent::executable,
            WrittenScrollMetaComponent::new
    );

    public WrittenScrollMetaComponent(String title, String author, int generation) {
        this(title, author, generation, false);
    }

    @Nullable
    public WrittenScrollMetaComponent copy() {
        return this.generation >= 2 ? null : new WrittenScrollMetaComponent(title, author, generation + 1, false);
    }

    public WrittenScrollMetaComponent withExecutable() {
        return new WrittenScrollMetaComponent(title, author, generation, true);
    }
}
