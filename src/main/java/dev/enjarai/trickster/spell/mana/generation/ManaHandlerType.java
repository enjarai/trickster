
package dev.enjarai.trickster.spell.mana.generation;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.mana.generation.event.EntityManaHandler;
import io.wispforest.endec.StructEndec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public record ManaHandlerType<T extends ManaHandler>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<ManaHandlerType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("mana_handler_type"));
    public static final Registry<ManaHandlerType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static final ManaHandlerType<EntityManaHandler> ENTITY = register("entity", EntityManaHandler.ENDEC);
    public static final ManaHandlerType<PlayerManaHandler> PLAYER = register("player", PlayerManaHandler.ENDEC);
    public static final ManaHandlerType<InventoryBlockManaHandler> INVENTORY_BLOCK = register("inventory_block", InventoryBlockManaHandler.ENDEC);

    private static <T extends ManaHandler> ManaHandlerType<T> register(String name, StructEndec<T> endec) {
        return Registry.register(REGISTRY, Trickster.id(name), new ManaHandlerType<>(endec));
    }

    public static void register() {
        // init the class :brombeere:
    }
}
