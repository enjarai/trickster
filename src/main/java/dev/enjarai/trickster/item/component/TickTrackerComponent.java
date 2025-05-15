
package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.World;

public record TickTrackerComponent(long tick) {
    public static final Codec<TickTrackerComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("sync_tick").forGetter(TickTrackerComponent::tick)
    ).apply(instance, TickTrackerComponent::new));

    public long getTick(World world) {
        return world.getTime() - this.tick;
    }
}
