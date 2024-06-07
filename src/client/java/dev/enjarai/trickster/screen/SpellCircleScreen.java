package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class SpellCircleScreen extends Screen {
    public SpellPartWidget partWidget;

    private double amountDragged;

    public SpellCircleScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        var part = new SpellPart(
                new PatternGlyph(1, 2, 3, 4),
                List.of(
                        Optional.of(new SpellPart(
                                new PatternGlyph(2, 3, 6, 7),
                                List.of()
                        )),
                        Optional.of(new SpellPart(
                                new PatternGlyph(2, 3, 6, 7),
                                List.of(
                                        Optional.of(new SpellPart(
                                                new PatternGlyph(8, 6, 4),
                                                List.of()
                                        )),
                                        Optional.of(new SpellPart(
                                                new SpellPart(
                                                        new PatternGlyph(8, 6, 4),
                                                        List.of()
                                                ),
                                                List.of(
                                                        Optional.of(new SpellPart(
                                                                new PatternGlyph(8, 6, 4),
                                                                List.of()
                                                        ))
                                                )
                                        )),
                                        Optional.empty(),
                                        Optional.empty()
                                )
                        )),
                        Optional.of(new SpellPart(
                                new PatternGlyph(1, 7, 5, 8),
                                List.of(
                                        Optional.of(new SpellPart(
                                                new PatternGlyph(8, 6, 4),
                                                List.of()
                                        ))
                                )
                        ))
                )
        );

        partWidget = new SpellPartWidget(part, width / 2d, height / 2d, 64);
        addDrawableChild(partWidget);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }

        var intensity = verticalAmount * partWidget.size / 10;
        partWidget.size += intensity;
        partWidget.x += verticalAmount * (partWidget.x - mouseX) / 10;
        partWidget.y += verticalAmount * (partWidget.y - mouseY) / 10;

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }

        if (!partWidget.isDrawing()) {
            partWidget.x += deltaX;
            partWidget.y += deltaY;

            amountDragged += Math.abs(deltaX) + Math.abs(deltaY);

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var dragged = amountDragged;
        amountDragged = 0;
        if (dragged <= 5) {
            return super.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }
}
