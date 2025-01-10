package dev.enjarai.trickster.render.blot;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.blot.Blot;
import dev.enjarai.trickster.spell.blot.BlotType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

public interface BlotRenderer<T extends Blot> {
    RegistryKey<Registry<BlotRenderer<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("blot_renderer"));
    Registry<BlotRenderer<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    //    LineFleckRenderer LINE = register(BlotType.LINE, new LineFleckRenderer());
    SpellBlotRenderer SPELL = register(BlotType.SPELL, new SpellBlotRenderer());

    static <T extends BlotRenderer<F>, F extends Blot> T register(BlotType<F> type, T renderer) {
        return Registry.register(REGISTRY, BlotType.REGISTRY.getId(type), renderer);
    }

    static void register() {}

    void render(T fleck, @Nullable T lastFleck, DrawContext context, RenderTickCounter tickCounter, int color);
}
