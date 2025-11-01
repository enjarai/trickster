package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.screen.scribing.CircleSoupWidget;
import io.wispforest.owo.braid.core.BraidScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ScrollAndQuillScreen extends BraidScreen implements ScreenHandlerProvider<ScrollAndQuillScreenHandler> {
    protected final ScrollAndQuillScreenHandler handler;

    public ScrollAndQuillScreen(ScrollAndQuillScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(new CircleSoupWidget(handler.initialData.spell(), handler, handler.initialData.mutable()));
        this.handler = handler;
    }

    @Override
    public void close() {
        // First cancel drawing a pattern if applicable
        //        if (rootWidget.cancelDrawing()) {
        //            return;
        //        }

        //noinspection DataFlowIssue
        this.client.player.closeHandledScreen();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public ScrollAndQuillScreenHandler getScreenHandler() {
        return handler;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void applyBlur(float delta) {
        if (!this.client.player.getOffHandStack().isOf(ModItems.TOME_OF_TOMFOOLERY)) {
            super.applyBlur(delta);
        }
    }

    // TODO
    //    public record PositionMemory(int spellHash,
    //            Vector2d position,
    //            double radius,
    //            SpellPart rootSpellPart,
    //            SpellPart spellPart,
    //            ArrayList<SpellPart> parents,
    //            ArrayList<Double> angleOffsets) {
    //    }
}
