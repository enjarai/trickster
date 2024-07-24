package dev.enjarai.trickster.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "trickster")
@Config(name = "trickster-config", wrapperName = "TricksterConfig")
public class TricksterConfigModel {
    public boolean dragDrawing = false;
    public boolean topHatInterceptScrolling = false;
    public boolean revealToHotbar = true;
    public boolean allowSwapBedrock = true;
}
