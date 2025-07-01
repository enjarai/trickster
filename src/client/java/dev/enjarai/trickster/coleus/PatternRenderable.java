package dev.enjarai.trickster.coleus;

import com.glisco.isometricrenders.property.DefaultPropertyBundle;
import com.glisco.isometricrenders.render.DefaultRenderable;
import com.glisco.isometricrenders.util.ExportPathSpec;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.fragment.PatternRenderer;
import dev.enjarai.trickster.spell.Pattern;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4fStack;

import java.util.List;
import java.util.stream.IntStream;

import static dev.enjarai.trickster.render.SpellCircleRenderer.*;

public class PatternRenderable extends DefaultRenderable<DefaultPropertyBundle> {

    private static final DefaultPropertyBundle PROPERTIES = new DefaultPropertyBundle() {
        @Override
        public void applyToViewMatrix(Matrix4fStack modelViewStack) {
            final float scale = this.scale.get() / 10000f;
            modelViewStack.scale(scale, scale, -scale);

//            modelViewStack.translate(this.xOffset.get() * , this.yOffset.get(), 0);
            modelViewStack.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            modelViewStack.rotate(RotationAxis.POSITIVE_Z.rotationDegrees(180));
        }
    };


    private final Pattern pattern;
    private final List<Integer> patternList;
    private final int size;

    public PatternRenderable(Pattern pattern, int size) {
        this.size = size;
        this.pattern = pattern;
        this.patternList = pattern
                .entries().stream()
                .flatMapToInt(e -> IntStream.of(e.p1(), e.p2()))
                .distinct()
                .boxed()
                .toList();
    }

    @Override
    public void emitVertices(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta) {
        matrices.push();
        var size = this.size / 4;

        var x = -size / 2;
        var y = -size  / 2;

        var patternSize = size / 2;

        boolean[] dotTerminalStatus = pattern.dotTerminalStatus();

        for (int i = 0; i < 9; i++) {
            var pos = getPatternDotPosition(x + patternSize + 4, y + patternSize + 4, i, patternSize);

            var isLinked = patternList.contains(i);
            var dotSize = 2;

            var r = 0f;
            var g = 0f;
            var b = 0f;

            if (dotTerminalStatus[i] && Trickster.CONFIG.dotEmphasis()) {
                r = Trickster.CONFIG.dotEmphasisColor().red();
                g = Trickster.CONFIG.dotEmphasisColor().green();
                b = Trickster.CONFIG.dotEmphasisColor().blue();
            }

            drawFlatPolygon(matrices, vertexConsumers,
                    pos.x - dotSize, pos.y - dotSize,
                    pos.x - dotSize, pos.y + dotSize,
                    pos.x + dotSize, pos.y + dotSize,
                    pos.x + dotSize, pos.y - dotSize,
                    0, r, g, b, isLinked ? 0.9f : 0.5f);
        }

        for (var line : pattern.entries()) {
            var now = getPatternDotPosition(x + patternSize + 4, y + patternSize + 4, line.p1(), patternSize);
            var last = getPatternDotPosition(x + patternSize + 4, y + patternSize + 4, line.p2(), patternSize);
            drawGlyphLine(matrices, vertexConsumers, last, now, 2, false, 0, 1f, 1f, 1f, 0.9f, false);
        }

        matrices.pop();
    }

    @Override
    public DefaultPropertyBundle properties() {
        return PROPERTIES;
    }

    @Override
    public ExportPathSpec exportPath() {
        return null;
    }
}

