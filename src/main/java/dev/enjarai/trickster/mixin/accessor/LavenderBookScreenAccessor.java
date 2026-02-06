package dev.enjarai.trickster.mixin.accessor;

import io.wispforest.lavender.client.LavenderBookScreen;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LavenderBookScreen.class)
public interface LavenderBookScreenAccessor {
    @Invoker("currentNavFrame")
    LavenderBookScreen.NavFrame trickster$currentNavFrame();

    @Invoker("rebuildContent")
    void trickster$rebuildContent(SoundEvent sound);
}
