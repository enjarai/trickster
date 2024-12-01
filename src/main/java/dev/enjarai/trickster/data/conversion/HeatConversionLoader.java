package dev.enjarai.trickster.data.conversion;

import net.minecraft.registry.RegistryWrapper;

public class HeatConversionLoader extends BlockConversionLoader {
    public HeatConversionLoader(RegistryWrapper.WrapperLookup registryLookup) {
        super("heat", registryLookup);
    }
}
