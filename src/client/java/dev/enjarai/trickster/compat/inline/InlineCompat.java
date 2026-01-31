package dev.enjarai.trickster.compat.inline;

import com.samsthenerd.inline.api.InlineAPI;
import com.samsthenerd.inline.api.client.GlowHandling;
import com.samsthenerd.inline.api.client.InlineClientAPI;
import com.samsthenerd.inline.api.client.InlineRenderer;
import com.samsthenerd.inline.api.matching.InlineMatch;
import com.samsthenerd.inline.api.matching.MatcherInfo;
import com.samsthenerd.inline.api.matching.RegexMatcher;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.render.fragment.PatternRenderer;
import dev.enjarai.trickster.spell.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

public class InlineCompat {
    public static void register() {
        // test with <t,7403678524>, <t,012587630>
        InlineAPI.INSTANCE.addDataType(PatternData.TYPE);
        InlineClientAPI.INSTANCE.addMatcher(new RegexMatcher.Simple("<t,([0-8]+)>", Trickster.id("pattern/path"), r -> {
            final Pattern p;
            try {
                p = Pattern.of(r.group(1).chars().map(c -> c - '0').toArray());
            } catch (IllegalArgumentException e) {
                if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                    Trickster.LOGGER.error("Invalid path pattern encountered", e);
                }
                return null;
            }
            return new InlineMatch.DataMatch(new PatternData(p), PatternData.getStyle(p, true));
        }, MatcherInfo.fromId(Trickster.id("pattern/path"))));
        InlineClientAPI.INSTANCE.addMatcher(new RegexMatcher.Simple("<T,(-?\\d+)>", Trickster.id("pattern/index"), r -> {
            final Pattern p;
            try {
                p = Pattern.from(Integer.parseInt(r.group(1)));
            } catch (NumberFormatException e) {
                if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                    Trickster.LOGGER.error("Invalid index pattern encountered", e);
                }
                return null;
            }
            return new InlineMatch.DataMatch(new PatternData(p), PatternData.getStyle(p, true));
        }, MatcherInfo.fromId(Trickster.id("pattern/index"))));
        InlineClientAPI.INSTANCE.addRenderer(new InlineRenderer<PatternData>() {
            private static final SpellCircleRenderer delegate = new SpellCircleRenderer(false, 0, false);

            @Override
            public Identifier getId() {
                return Trickster.id("pattern");
            }

            @Override
            public int render(PatternData data, DrawContext context, int index, Style style, int codepoint, TextRenderingContext trContext) {
                MatrixStack matrices = context.getMatrices();
                matrices.push();
                matrices.scale(1 / 6f, 1 / 6f, 1);
                var color = style.getColor();
                if (color != null)
                    delegate.setColor((color.getRgb() >> 16 & 255) / 255f * trContext.brightnessMultiplier(), (color.getRgb() >> 8 & 255) / 255f * trContext.brightnessMultiplier(),
                            (color.getRgb() & 255) / 255f * trContext.brightnessMultiplier());
                else
                    delegate.setColor(trContext.red(), trContext.green(), trContext.blue());
                if (trContext.isGlowy())
                    matrices.translate(0, 0, -1);
                float d = trContext.shadow() ? 1 : 0;
                VertexConsumerProvider vertexConsumers = context.getVertexConsumers();
                float off = 4f;
                PatternRenderer.renderPattern(data.pattern(), matrices, vertexConsumers, 28f + d * off, 18f + d * off, 24, 3f, -1f, true, trContext.alpha() / 0.7f, delegate);
                matrices.pop();
                return 10;
            }

            @Override
            public GlowHandling getGlowPreference(PatternData data) {
                return new GlowHandling.None();
            }

            @Override
            public boolean handleOwnTransparency(PatternData data) {
                return true;
            }

            @Override
            public int charWidth(PatternData data, Style style, int codepoint) {
                return 10;
            }
        });
    }
}
