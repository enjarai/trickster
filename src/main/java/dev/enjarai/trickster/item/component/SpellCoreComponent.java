package dev.enjarai.trickster.item.component;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.nbt.NbtCompound;

public record SpellCoreComponent(NbtCompound data) {
    public static final Codec<SpellCoreComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    NbtCompound.CODEC.fieldOf("data").forGetter(SpellCoreComponent::data)
            ).apply(instance, SpellCoreComponent::new)
    );
    private static final KeyedEndec<SpellExecutor> ENDEC = SpellExecutor.ENDEC.keyed("data", () -> null);

    public static SpellCoreComponent of(SpellExecutor executor) {
        var compound = new NbtCompound();
        compound.put(ENDEC, executor);
        return new SpellCoreComponent(compound);
    }

    public Optional<SpellExecutor> tryDeserialize() {
        try {
            return Optional.ofNullable(data.get(ENDEC));
        } catch (Throwable e) {
            Trickster.LOGGER.error(e.getMessage());
            return Optional.empty();
        }
    }
}
