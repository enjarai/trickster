package dev.enjarai.trickster.fleck;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public interface FleckRenderer<T extends Fleck> {
    RegistryKey<Registry<FleckRenderer<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("fleck_renderer"));
    Registry<FleckRenderer<?>> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    LineFleckRenderer LINE = register(FleckType.LINE, new LineFleckRenderer());
    SpellFleckRenderer SPELL = register(FleckType.SPELL, new SpellFleckRenderer());
    TextFleckRenderer TEXT = register(FleckType.TEXT, new TextFleckRenderer());

    static <T extends FleckRenderer<F>, F extends Fleck> T register(FleckType<F> type, T renderer) {
        return Registry.register(REGISTRY, FleckType.REGISTRY.getId(type), renderer);
    }



    void render(T fleck, ClientWorld world, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);
}
