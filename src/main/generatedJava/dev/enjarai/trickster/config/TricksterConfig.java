package dev.enjarai.trickster.config;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TricksterConfig extends ConfigWrapper<dev.enjarai.trickster.config.TricksterConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Boolean> topHatInterceptScrolling = this.optionForKey(this.keys.topHatInterceptScrolling);
    private final Option<java.lang.Boolean> invertTopHatScrolling = this.optionForKey(this.keys.invertTopHatScrolling);
    private final Option<java.lang.Boolean> revealToHotbar = this.optionForKey(this.keys.revealToHotbar);
    private final Option<java.lang.Integer> maxExecutionsPerSpellPerTick = this.optionForKey(this.keys.maxExecutionsPerSpellPerTick);
    private final Option<java.lang.Float> maxBlockBreakingHardness = this.optionForKey(this.keys.maxBlockBreakingHardness);
    private final Option<java.lang.Boolean> allowSwapBedrock = this.optionForKey(this.keys.allowSwapBedrock);
    private final Option<java.lang.Boolean> dragDrawing = this.optionForKey(this.keys.dragDrawing);
    private final Option<java.lang.Boolean> barsHorizontal = this.optionForKey(this.keys.barsHorizontal);
    private final Option<java.lang.Boolean> disableOffhandScrollOpening = this.optionForKey(this.keys.disableOffhandScrollOpening);

    private TricksterConfig() {
        super(dev.enjarai.trickster.config.TricksterConfigModel.class);
    }

    private TricksterConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(dev.enjarai.trickster.config.TricksterConfigModel.class, janksonBuilder);
    }

    public static TricksterConfig createAndLoad() {
        var wrapper = new TricksterConfig();
        wrapper.load();
        return wrapper;
    }

    public static TricksterConfig createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new TricksterConfig(janksonBuilder);
        wrapper.load();
        return wrapper;
    }

    public boolean topHatInterceptScrolling() {
        return topHatInterceptScrolling.value();
    }

    public void topHatInterceptScrolling(boolean value) {
        topHatInterceptScrolling.set(value);
    }

    public boolean invertTopHatScrolling() {
        return invertTopHatScrolling.value();
    }

    public void invertTopHatScrolling(boolean value) {
        invertTopHatScrolling.set(value);
    }

    public boolean revealToHotbar() {
        return revealToHotbar.value();
    }

    public void revealToHotbar(boolean value) {
        revealToHotbar.set(value);
    }

    public int maxExecutionsPerSpellPerTick() {
        return maxExecutionsPerSpellPerTick.value();
    }

    public void maxExecutionsPerSpellPerTick(int value) {
        maxExecutionsPerSpellPerTick.set(value);
    }

    public float maxBlockBreakingHardness() {
        return maxBlockBreakingHardness.value();
    }

    public void maxBlockBreakingHardness(float value) {
        maxBlockBreakingHardness.set(value);
    }

    public boolean allowSwapBedrock() {
        return allowSwapBedrock.value();
    }

    public void allowSwapBedrock(boolean value) {
        allowSwapBedrock.set(value);
    }

    public boolean dragDrawing() {
        return dragDrawing.value();
    }

    public void dragDrawing(boolean value) {
        dragDrawing.set(value);
    }

    public boolean barsHorizontal() {
        return barsHorizontal.value();
    }

    public void barsHorizontal(boolean value) {
        barsHorizontal.set(value);
    }

    public boolean disableOffhandScrollOpening() {
        return disableOffhandScrollOpening.value();
    }

    public void disableOffhandScrollOpening(boolean value) {
        disableOffhandScrollOpening.set(value);
    }


    public static class Keys {
        public final Option.Key topHatInterceptScrolling = new Option.Key("topHatInterceptScrolling");
        public final Option.Key invertTopHatScrolling = new Option.Key("invertTopHatScrolling");
        public final Option.Key revealToHotbar = new Option.Key("revealToHotbar");
        public final Option.Key maxExecutionsPerSpellPerTick = new Option.Key("maxExecutionsPerSpellPerTick");
        public final Option.Key maxBlockBreakingHardness = new Option.Key("maxBlockBreakingHardness");
        public final Option.Key allowSwapBedrock = new Option.Key("allowSwapBedrock");
        public final Option.Key dragDrawing = new Option.Key("dragDrawing");
        public final Option.Key barsHorizontal = new Option.Key("barsHorizontal");
        public final Option.Key disableOffhandScrollOpening = new Option.Key("disableOffhandScrollOpening");
    }
}

