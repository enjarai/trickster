package dev.enjarai.trickster.render;

import org.joml.Matrix4f;

import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec3d;

public class SpellTooltipComponent implements TooltipComponent {

    private final CircleRenderer renderer;

    private final SpellPart spell;

    public SpellTooltipComponent(SpellPart spell) {
        this.spell = spell;
        this.renderer = new CircleRenderer(true, false, 4);
    }

    @Override
    public int getHeight() {
        return 110;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 110;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {

    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        var matrices = context.getMatrices();
        var delta = 0;
        var size = spell.subParts.isEmpty() ? 50.0 : 30.0;

        matrices.push();
        //noinspection IntegerDivisionInFloatingPointContext
        renderer.renderCircle(
            matrices, spell, x + getWidth(textRenderer) / 2, y + getHeight() / 2, size,
            0.0, delta, 1, new Vec3d(0, 0, -1), null
        );
        CircleRenderer.VERTEX_CONSUMERS.draw();
        matrices.pop();
    }
}
