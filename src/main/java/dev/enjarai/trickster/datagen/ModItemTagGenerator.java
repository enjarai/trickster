package dev.enjarai.trickster.datagen;

import dev.enjarai.trickster.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ModItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        for (ModItems.DyedVariant variant : ModItems.DYED_VARIANTS) {
            getOrCreateTagBuilder(TagKey.of(registryRef, reverseLookup(variant.original()).getValue().withPrefixedPath("dyed_")))
                    .add(variant.variant());
        }
    }
}
