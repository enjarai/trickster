package dev.enjarai.trickster.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class ScrollAndQuillScreen extends Screen implements ScreenHandlerProvider<ScrollAndQuillScreenHandler> {
    private static final ArrayList<PositionMemory> storedPositions = new ArrayList<>(5);

    protected final ScrollAndQuillScreenHandler handler;

    public SpellPartWidget partWidget;

    private boolean hasLoaded = false;

    public ScrollAndQuillScreen(ScrollAndQuillScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(title);
        this.handler = handler;
    }

    @Override
    protected void init() {
        super.init();
        partWidget = new SpellPartWidget(handler.spell.get(), width / 2d, height / 2d, 64, handler, true);
        handler.replacerCallback = frag -> partWidget.replaceCallback(frag);
        handler.updateDrawingPartCallback = spell -> partWidget.updateDrawingPartCallback(spell);

        addDrawableChild(new EvaluateButton(width - 48, 0, handler.evaluationButton));
        addDrawableChild(partWidget);

        this.handler.spell.observe(spell -> {
            var spellHash = spell.hashCode();

            if (!hasLoaded)
                for (var position : storedPositions) {
                    if (position.spellHash == spellHash) {
                        partWidget.load(position);
                        break;
                    }
                }

            partWidget.setSpell(spell);
            hasLoaded = true;
        });
        this.handler.isMutable.observe(mutable -> partWidget.setMutable(mutable));
    }

    @Override
    public void close() {
        var saved = partWidget.save();
        storedPositions.removeIf(position -> position.spellHash == saved.spellHash);
        storedPositions.add(saved);
        if (storedPositions.size() >= 5) {
            storedPositions.removeFirst();
        }

        //noinspection DataFlowIssue
        this.client.player.closeHandledScreen();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        partWidget.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, 0, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.setDragging(true);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isDragging())
            this.setDragging(false);
        return super.mouseReleased(mouseX, mouseY, button);
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

    private class EvaluateButton extends ClickableWidget {
        private boolean render;

        public EvaluateButton(int x, int y, SyncedProperty<Boolean> evaluationButton) {
            super(x, y, 48, 48, Text.empty());
            setTooltip(MultilineTooltip.create(Text.translatable("trickster.widget.evaluate_spell"), Text.translatable("trickster.widget.evaluate_spell.warning")));
            evaluationButton.observe(render -> {
                this.render = render;
            });
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            if (!render) return;

            double normalizedDistance = getNormalizedDistance(mouseX, mouseY);

            // Interpolate each color channel
            float r = (float) MathHelper.lerp(normalizedDistance, 0.8, 1.0);
            float g = (float) MathHelper.lerp(normalizedDistance, 0.5, 1.0);
            float b = (float) MathHelper.lerp(normalizedDistance, 1.0, 1.0);

            context.setShaderColor(r, g, b, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            context.drawGuiTexture(Trickster.id("circle_24"), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        private double getNormalizedDistance(int mouseX, int mouseY) {
            int midX = getX() + getWidth() / 2;
            int midY = getY() + getHeight() / 2;

            double distance = Vector2i.distance(mouseX, mouseY, midX, midY);

            // Define min and max distances
            double minDistance = width / 2d; // Minimum distance for color transition
            double maxDistance = width * 2; // Maximum distance for color transition

            // Clamp the current distance between min and max
            double clampedDistance = Math.max(minDistance, Math.min(distance, maxDistance));

            // Normalize the distance to a 0-1 range
            double normalizedDistance = (clampedDistance - minDistance) / (maxDistance - minDistance);
            normalizedDistance = Math.min(Math.max(normalizedDistance, 0.0), 1.0);
            return normalizedDistance;
        }

        @Override
        public boolean isNarratable() {
            return false;
        }

        @Override
        public void setFocused(boolean focused) {}

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            handler.evaluateCurrentSpell();
        }
    }

    private static class MultilineTooltip extends Tooltip {
        private static final int ROW_LENGTH = 170;
        private final Text[] content;
        @Nullable
        private List<OrderedText> lines;
        @Nullable
        private Language language;

        private MultilineTooltip(Text ...content) {
            super(null, null);
            this.content = content;
        }

        // Can't use "of" because Java defaults to Tooltip.of first
        public static MultilineTooltip create(Text ...content) {
            return new MultilineTooltip(content);
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {}

        public List<OrderedText> getLines(MinecraftClient client) {
            Language language = Language.getInstance();
            if (this.lines == null || language != this.language) {
                this.lines = wrapLines(client, this.content);
                this.language = language;
            }

            return this.lines;
        }

        public static List<OrderedText> wrapLines(MinecraftClient client, Text ...text) {
            ImmutableList.Builder<OrderedText> builder = ImmutableList.builder();
            for (Text text1 : text) {
                builder.addAll(client.textRenderer.wrapLines(text1, ROW_LENGTH));
            }
            return builder.build();
        }
    }

    record PositionMemory(int spellHash,
            double x,
            double y,
            double size,
            SpellPart rootSpellPart,
            SpellPart spellPart,
            ArrayList<SpellPart> parents,
            ArrayList<Double> angleOffsets) {
    }
}
