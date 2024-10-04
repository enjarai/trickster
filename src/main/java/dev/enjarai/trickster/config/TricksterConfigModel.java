package dev.enjarai.trickster.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Sync;
import io.wispforest.owo.config.annotation.SectionHeader;

@Modmenu(modId = "trickster")
@Config(name = "trickster-config", wrapperName = "TricksterConfig")
public class TricksterConfigModel {
    @SectionHeader("client")
    public boolean topHatInterceptScrolling = false;
    public boolean invertTopHatScrolling = false;

    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean revealToHotbar = true;

    @SectionHeader("server")
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public int maxExecutionsPerSpellPerTick = 64;
    
    public boolean allowSwapBedrock = true;

    @SectionHeader("aurora-client")
    public boolean dragDrawing = false;
    public boolean barsHorizontal = false;
}
