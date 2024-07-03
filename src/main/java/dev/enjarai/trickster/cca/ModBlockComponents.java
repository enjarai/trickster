package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.ShadowBlockEntity;
import org.ladysnake.cca.api.v3.block.BlockComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.block.BlockComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

public class ModBlockComponents implements BlockComponentInitializer {
    public static final ComponentKey<ShadowDisguiseComponent> SHADOW_DISGUISE =
            ComponentRegistry.getOrCreate(Trickster.id("shadow_disguise"), ShadowDisguiseComponent.class);

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        registry.registerFor(ShadowBlockEntity.class, SHADOW_DISGUISE, ShadowDisguiseComponent::new);
    }
}
