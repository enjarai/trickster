package dev.enjarai.trickster.data;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.data.conversion.CoolConversionLoader;
import dev.enjarai.trickster.data.conversion.ErosionConversionLoader;
import dev.enjarai.trickster.data.conversion.HeatConversionLoader;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class DataLoader {
    private static HeatConversionLoader heatLoader;
    private static CoolConversionLoader coolLoader;
    private static ErosionConversionLoader erosionLoader;

    public static void registerLoaders() {
        ResourceManagerHelper resourceManagerHelper = ResourceManagerHelper.get(ResourceType.SERVER_DATA);
        resourceManagerHelper.registerReloadListener(Trickster.id("conversion/heat"), wrapperLookup -> heatLoader = new HeatConversionLoader(wrapperLookup));
        resourceManagerHelper.registerReloadListener(Trickster.id("conversion/cool"), wrapperLookup -> coolLoader = new CoolConversionLoader(wrapperLookup));
        resourceManagerHelper.registerReloadListener(Trickster.id("conversion/erode"), wrapperLookup -> erosionLoader = new ErosionConversionLoader(wrapperLookup));
    }

    public static HeatConversionLoader getHeatLoader() {
        return heatLoader;
    }

    public static CoolConversionLoader getCoolLoader() {
        return coolLoader;
    }
}
