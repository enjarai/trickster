package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.screen.scribing.CircleSoupWidget;
import io.wispforest.owo.braid.core.BraidScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScrollAndQuillScreen extends BraidScreen implements ScreenHandlerProvider<ScrollAndQuillScreenHandler> {
    public static final List<PositionMemory> positions = new ArrayList<>();

    protected final ScrollAndQuillScreenHandler handler;

    public static ScrollAndQuillScreen create(ScrollAndQuillScreenHandler handler, PlayerInventory playerInventory, Text title) {
        var position = loadPosition(handler.initialData.hash());
        var view = SpellView.index(handler.initialData.spell());
        var x = 0d;
        var y = 0d;
        var radius = 80d;
        var angle = 0d;
        var centerOffset = 0d;

        if (position != null) {
            var loaded = view.traverseTo(position.path);
            if (loaded != null) {
                view = loaded;
                x = position.x;
                y = position.y;
                radius = position.radius;
                angle = position.angle;
                centerOffset = position.centerOffset;
            }
        }

        return new ScrollAndQuillScreen(new CircleSoupWidget(
            view, handler, handler.initialData.mutable(), handler.initialData.allowEval(),
            x, y, radius, angle, centerOffset, savePosition(handler)
        ), handler);
    }

    private ScrollAndQuillScreen(CircleSoupWidget widget, ScrollAndQuillScreenHandler handler) {
        super(widget);
        this.handler = handler;
    }

    @Override
    public void close() {
        // TODO
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

    @Nullable
    private static PositionMemory loadPosition(int hash) {
        return positions.stream()
            .filter(position -> position.hash == hash)
            .findFirst()
            .orElse(null);
    }

    private static CircleSoupWidget.DisposeCallback savePosition(ScrollAndQuillScreenHandler handler) {
        return (view, x, y, radius, angle, centerOffset) -> {
            positions.removeIf(m -> m.hash() == handler.initialData.hash());

            while (positions.size() > 10) {
                positions.removeFirst();
            }

            positions.add(new PositionMemory(
                handler.initialData.hash(),
                view.getPath(), x, y, radius, angle, centerOffset
            ));
        };
    }

    public record PositionMemory(
        int hash,
        List<Integer> path,
        double x,
        double y,
        double radius,
        double angle,
        double centerOffset) {
    }
}
