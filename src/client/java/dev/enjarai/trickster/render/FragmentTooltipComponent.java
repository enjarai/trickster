package dev.enjarai.trickster.render;

import dev.enjarai.trickster.render.fragment.FragmentRenderer;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import org.joml.Matrix4f;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec3d;

public class FragmentTooltipComponent implements TooltipComponent {

    private final CircleRenderer renderer;

    private final Fragment fragment;

    private final float size;

    public FragmentTooltipComponent(Fragment fragment) {
        this.fragment = fragment;
        this.renderer = new CircleRenderer(true, false, 4);

        this.size = 50.0f;
    }

    @Override
    public int getHeight() {
        return (int) (FragmentRenderer.get_fragment_proportional_height(fragment) * size) + 30;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return (int) size + 10;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {

    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        var matrices = context.getMatrices();
        var delta = 0;
        var radius = size;
        if (fragment instanceof SpellPart spell && spell.subParts.isEmpty()) {
            radius = size * 2;
        }

        matrices.push();
        //        renderer.renderCircle(
        //            matrices, spell, x + getWidth(textRenderer) / 2, y + getHeight() / 2, size,
        //            0.0, delta, 1, new Vec3d(0, 0, -1), null
        //        );
        FragmentRenderer fragmentRenderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(fragment.type()));
        if (fragmentRenderer != null) {
            fragmentRenderer.render(fragment, matrices, context.getVertexConsumers(), x + getWidth(textRenderer) / 2f, y + getHeight() / 2f, radius, 1, new Vec3d(0, 0, -1), delta, renderer);
        } else {
            FragmentRenderer.renderAsText(fragment, matrices, context.getVertexConsumers(), x + getWidth(textRenderer) / 2f, y + getHeight() / 2f, radius, 1);
        }

        CircleRenderer.VERTEX_CONSUMERS.draw();
        matrices.pop();
    }
}
