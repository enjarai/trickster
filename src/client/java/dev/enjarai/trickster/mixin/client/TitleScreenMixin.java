package dev.enjarai.trickster.mixin.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(
        method = "init", at = @At("TAIL")
    )
    private void init(CallbackInfo ci) {
        //        addDrawableChild(
        //            ButtonWidget.builder(Text.translatable("menu.options"), button -> this.client.setScreen(new BraidScreen(new CircleSoupWidget())))
        //                .dimensions(this.width / 2 - 200, this.height / 2, 98, 20)
        //                .build()
        //        );
    }
}
