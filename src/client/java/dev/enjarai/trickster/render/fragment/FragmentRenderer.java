package dev.enjarai.trickster.render.fragment;

import dev.enjarai.trickster.Trickster;


import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

public interface FragmentRenderer<T extends Fragment> {
    RegistryKey<Registry<FragmentRenderer<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("fragment_renderer"));
    Registry<FragmentRenderer<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    PatternRenderer PATTERN = register(FragmentType.PATTERN, new PatternRenderer());
    PatternLiteralRenderer PATTERN_LITERAL = register(FragmentType.PATTERN_LITERAL, new PatternLiteralRenderer());
    ItemTypeRenderer ITEM_TYPE = register(FragmentType.ITEM_TYPE, new ItemTypeRenderer());
    BlockTypeRenderer BLOCK_TYPE = register(FragmentType.BLOCK_TYPE, new BlockTypeRenderer());
//    EntityRenderer ENTITY = register(FragmentType.ENTITY, new EntityRenderer());

    static <T extends FragmentRenderer<F>, F extends Fragment> T register(FragmentType<F> type, T renderer) {
        return Registry.register(REGISTRY, FragmentType.REGISTRY.getId(type), renderer);
    }

    static void register() {}

    static void renderAsText(Fragment fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha) {
        var textRenderer = MinecraftClient.getInstance().textRenderer;

//            var height = textRenderer.wrapLines(Text.literal(glyph.asString()), ) // TODO
        var text = fragment.asFormattedText();
        var height = 7;
        var width = textRenderer.getWidth(text);

        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(size / 1.3f / width, size / 1.3f / width, 1);

        var color = ColorHelper.Argb.withAlpha((int) (alpha * 0xff), 0xffffff);

        textRenderer.draw(
                text,
                -width / 2f, -height / 2f, color, false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers, TextRenderer.TextLayerType.NORMAL,
                0, 0xf000f0
        );

        matrices.pop();
    }

    void render(T fragment, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float x, float y, float size, float alpha, Vec3d normal, float tickDelta, SpellCircleRenderer delegator);

    default boolean renderRedrawDots() {
        return true;
    }

    default boolean doubleSided() {
        return true;
    }
}
