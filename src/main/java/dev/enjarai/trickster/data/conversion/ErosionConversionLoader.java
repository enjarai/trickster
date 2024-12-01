package dev.enjarai.trickster.data.conversion;

import net.minecraft.registry.RegistryWrapper;

public class ErosionConversionLoader extends BlockConversionLoader {
    public ErosionConversionLoader(RegistryWrapper.WrapperLookup registryLookup) {
        super("erode", registryLookup);
    }
}
