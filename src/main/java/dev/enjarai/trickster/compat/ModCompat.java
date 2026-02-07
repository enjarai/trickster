package dev.enjarai.trickster.compat;

import net.fabricmc.loader.api.FabricLoader;

public class ModCompat {
    public static final boolean TRANSMOG_LOADED = FabricLoader.getInstance().isModLoaded("transmog");
    public static final boolean INLINE_LOADED = FabricLoader.getInstance().isModLoaded("inline");
    public static final boolean SURVEYOR_LOADED = FabricLoader.getInstance().isModLoaded("surveyor");
}
