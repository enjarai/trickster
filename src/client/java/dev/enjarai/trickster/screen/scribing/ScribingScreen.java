package dev.enjarai.trickster.screen.scribing;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScribingScreen extends Screen {
    public final CircleRenderer renderer = new CircleRenderer(true, true);
    private final List<CircleElement> circles = new ArrayList<>();

    public final boolean mutable;
    @Nullable
    private CircleElement drawingCircle;

    private double inputZ = 0;

    public ScribingScreen(Text title, boolean mutable) {
        super(title);
        this.mutable = mutable;
    }

    @Override
    protected void init() {
        circles.clear();
        addCircle(new CircleElement(
                this,
                (SpellPart) Fragment.fromBase64(
                        "YxEpKcpMzi4uSS2yKi5IzcmJL0gsKsEqKIgQLEgsAVJ5G32YWFkZmRkYGECYgZERUwkDAxNIiomBSsocoMrY8CnjFGBnIMoYDCsFEGrzSnOTUosYEICJgQlZKZFhBNbJWIfPORs8mFjxOtfFgYsse+GepL7RTIyc+Mw15GhqITZUSYgpHpLdCgpbRiLTHh4HOxiQ4WByQhWvS50awLEFDAhdfMpA2RKf/KIGvH61/0CsXx0QikgOFaLigVCCJcoQqmYYTogU7iiChAmRCRqfSg4GFqJDn9h8RgRBTqKVxudOpiaGhcSnRixWKAhQKUQXMTB04gks+weogYXbRkYF0lxEYg4B5m+8IUq3+oJIraDCnkDJhai4ia5sCQeZACMDHsOILsaUG5w68JjjoESkOUzoLicpATiBA4jUREVk4JBcBzigxQROc4C2cOB1hwO2QMHjS1YSrCM2aQPzAzPtmqlIgVeWmlySX8QMC7oDExjQAaHa7QD2WIEavGeCkW4DUG4/NG3bzI/pTiAumhywF31Qg/dfXssoguRMewSfxDYAsTE9GpO0iUn70ZgcpjG5fzQmh2hMopeuVItJDIJRFJ9xhDqGyC0YKg2uEKuSwl4hlpBAGp8YHCkZSxcvhIjwFnJhwCuPbAiUAAC2SONd1RMAAA=="),
                null,
                width / 4d, height / 2d,
                width / 8d,
                0
        ));

        addCircle(new CircleElement(
                this,
                (SpellPart) Fragment.fromBase64(
                        "YxEpKcpMzi4uSS2yKi5IzcmJL0gsKsEqKIgQLEgsAVJ5G32YWFkZmRkYGECYgZERUwkDAxNIiomBSsocoMrY8CnjFGBnIMoYDCsFEGrzSnOTUosYEICJgQlZKZFhBNbJWIfPORs8mFjxOtfFgYsse+GepL7RTIyc+Mw15GhqITZUSYgpHpLdCgpbRiLTHh4HOxiQ4WByQhWvS50awLEFDAhdfMpA2RKf/KIGvH61/0CsXx0QikgOFaLigVCCJcoQqmYYTogU7iiChAmRCRqfSg4GFqJDn9h8RgRBTqKVxudOpiaGhcSnRixWKAhQKUQXMTB04gks+weogYXbRkYF0lxEYg4B5m+8IUq3+oJIraDCnkDJhai4ia5sCQeZACMDHsOILsaUG5w68JjjoESkOUzoLicpATiBA4jUREVk4JBcBzigxQROc4C2cOB1hwO2QMHjS1YSrCM2aQPzAzPtmqlIgVeWmlySX8QMC7oDExjQAaHa7QD2WIEavGeCkW4DUG4/NG3bzI/pTiAumhywF31Qg/dfXssoguRMewSfxDYAsTE9GpO0iUn70ZgcpjG5fzQmh2hMopeuVItJDIJRFJ9xhDqGyC0YKg2uEKuSwl4hlpBAGp8YHCkZSxcvhIjwFnJhwCuPbAiUAAC2SONd1RMAAA=="),
                null,
                width / 4d * 3, height / 2d,
                width / 8d,
                0
        ));
    }

    public void addCircle(CircleElement circle) {
        if (!circles.contains(circle)) {
            circles.add(circle);
            addDrawableChild(circle);
            circle.initialize();
        }
    }

    public void removeCircle(CircleElement circle) {
        circles.remove(circle);
        remove(circle);
        if (drawingCircle == circle) {
            drawingCircle = null;
        }
    }

    public boolean isDrawing() {
        return drawingCircle != null;
    }

    public void setDrawingCircle(@Nullable CircleElement circle) {
        drawingCircle = circle;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderer.setMousePosition(mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
        CircleRenderer.VERTEX_CONSUMERS.draw();

        if (inputZ != 0.0) {
            zoom(mouseX, mouseY, inputZ * Trickster.CONFIG.keyZoomSpeed() * delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (drawingCircle != null) {
            drawingCircle.finishDrawing(true);
            drawingCircle = null;
            return true;
        }

        CircleElement smallest = null;
        for (var circle : circles) {
            if ((smallest == null || smallest.getRadius() > circle.getRadius()) && circle.isMouseOver(mouseX, mouseY)) {
                smallest = circle;
            }
        }
        if (smallest != null) {
            smallest.click(mouseX, mouseY, button);
        }
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        if (drawingCircle != null) {
            drawingCircle.movingDrawing(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            zoom(mouseX, mouseY, verticalAmount);
        }
        return true;
    }

    private void zoom(double mouseX, double mouseY, double amount) {
        List.copyOf(circles).forEach(c -> c.zoom(mouseX, mouseY, amount));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        } else if (this.client.options.forwardKey.matchesKey(keyCode, scanCode)) {
            inputZ = 1.0;
            return true;
        } else if (this.client.options.backKey.matchesKey(keyCode, scanCode)) {
            inputZ = -1.0;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.forwardKey.matchesKey(keyCode, scanCode)) {
            inputZ = 0.0;
            return true;
        } else if (this.client.options.backKey.matchesKey(keyCode, scanCode)) {
            inputZ = 0.0;
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        // First cancel drawing a pattern if applicable
        if (drawingCircle != null) {
            drawingCircle.finishDrawing(false);
            return;
        }

        //noinspection DataFlowIssue
        //        this.client.player.closeHandledScreen();
        super.close();
    }
}
