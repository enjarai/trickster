package dev.enjarai.trickster.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ModDataStuff implements DataGeneratorEntrypoint {
    public static void register() {

    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(ModModelGenerator::new);
        pack.addProvider(ModRecipeGenerator::new);
        pack.addProvider(ModItemTagGenerator::new);
        pack.addProvider(ModBlockTagGenerator::new);
        pack.addProvider(HeatBlockConversionGenerator::new);
        pack.addProvider(CoolBlockConversionGenerator::new);
        pack.addProvider(ErodeBlockConversionGenerator::new);
    }
}
