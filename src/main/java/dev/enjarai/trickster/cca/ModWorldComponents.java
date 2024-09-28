package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.Trickster;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class ModWorldComponents implements WorldComponentInitializer {
    public static final ComponentKey<PinnedChunksComponent> PINNED_CHUNKS =
            ComponentRegistry.getOrCreate(Trickster.id("pinned_chunks"), PinnedChunksComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(PINNED_CHUNKS, PinnedChunksComponent::new);
    }
}
