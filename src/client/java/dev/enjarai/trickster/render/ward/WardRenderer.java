package dev.enjarai.trickster.render.ward;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.client.render.VertexConsumerProvider;
import dev.enjarai.trickster.spell.ward.Ward;
import dev.enjarai.trickster.spell.ward.WardType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

public interface WardRenderer<T extends Ward> {
    RegistryKey<Registry<WardRenderer<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("ward_renderer"));
    Registry<WardRenderer<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    SimpleCubicWardRenderer SIMPLE_CUBIC = register(WardType.SIMPLE_CUBIC, new SimpleCubicWardRenderer());

    static <T extends WardRenderer<F>, F extends Ward> T register(WardType<F> type, T renderer) {
        return Registry.register(REGISTRY, WardType.REGISTRY.getId(type), renderer);
    }

    static void register() {}

    void render(T ward, @Nullable T lastWard, WorldRenderContext context, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta);
}
