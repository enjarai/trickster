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

    private final FragmentRenderer fragmentRenderer;

    public FragmentTooltipComponent(Fragment fragment) {
        this.fragment = fragment;
        this.renderer = new CircleRenderer(true, false, 4);
        this.fragmentRenderer = FragmentRenderer.REGISTRY.get(FragmentType.REGISTRY.getId(fragment.type()));
        if (fragment instanceof SpellPart) {
            this.size = 100.0f;
        } else {
            this.size = 50.0f;
        }
    }

    @Override
    public int getHeight() {
        if (fragmentRenderer != null) {
            return (int) (FragmentRenderer.getFragmentProportionalHeight(fragment) * size) + 30;
        } else {
            return 10;
        }
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        if (fragmentRenderer != null) {
            return (int) size + 10;
        } else {
            return textRenderer.getWidth(fragment.asFormattedText());
        }
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        if (fragmentRenderer == null) {
            textRenderer.draw(fragment.asFormattedText(), (float) x, (float) y, -1, true, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        }
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        var matrices = context.getMatrices();
        var delta = 0;
        var radius = size;
        if (fragment instanceof SpellPart spell && spell.subParts.isEmpty()) {
            radius = size * 1.8f;
        }

        matrices.push();
        if (fragmentRenderer != null) {
            fragmentRenderer.render(fragment, matrices, context.getVertexConsumers(), x + getWidth(textRenderer) / 2f, y + getHeight() / 2f, radius, 1, new Vec3d(0, 0, -1), delta, renderer);
        }

        CircleRenderer.VERTEX_CONSUMERS.draw();
        matrices.pop();
    }
}
