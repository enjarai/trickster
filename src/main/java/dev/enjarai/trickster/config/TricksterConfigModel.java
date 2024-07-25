package dev.enjarai.trickster.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = "trickster")
@Config(name = "trickster-config", wrapperName = "TricksterConfig")
public class TricksterConfigModel {
    public boolean dragDrawing = false;
    public boolean topHatInterceptScrolling = false;
    public boolean revealToHotbar = true;
    public boolean allowSwapBedrock = true;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public int maxExecutionsPerSpellPerTick = 255;
}
