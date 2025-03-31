package dev.enjarai.trickster.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.config.annotation.RangeConstraint;
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
    @PredicateConstraint("requirePositive")
    public int maxExecutionsPerSpellPerTick = 64;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @PredicateConstraint("requirePositive")
    public float maxBlockBreakingHardness = 55.5f;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean allowSwapBedrock = true;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @PredicateConstraint("requirePositive")
    public float whorlRechargeRate = 1.0f;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @PredicateConstraint("requirePositive")
    public float whorlMaxMana = 256.0f;

    @SectionHeader("aurora-client")

    public boolean dragDrawing = false;
    public boolean barsHorizontal = false;

    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean disableOffhandScrollOpening = false;

    @RangeConstraint(min = 0, max = 1)
    public float adjacentPixelCollisionOffset = 0.25f;

    public static boolean requirePositive(int value) {
        return value >= 0;
    }

    public static boolean requirePositive(float value) {
        return value >= 0;
    }
}
