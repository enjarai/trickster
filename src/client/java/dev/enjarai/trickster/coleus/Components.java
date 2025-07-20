package dev.enjarai.trickster.coleus;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.type.Signature;
import j2html.tags.Tag;
import org.joml.Vector2f;

import static dev.enjarai.trickster.render.SpellCircleRenderer.*;
import static j2html.TagCreator.tag;
import static mod.master_bw3.coleus.Components.text;

public class Components {

    public static Tag<?> pattern(Pattern pattern, int size) {
        var svg = tag("svg")
                .attr("version", "1.1")
                .attr("viewbox", String.format("0 0 %d %d", size, size))
                .attr("xmlns", "http://www.w3.org/2000/svg");

        var patternSize = size * 0.46f;
        var x = (size - patternSize * 2) / 2;
        var y = (size - patternSize * 2) / 2;

        boolean[] dotTerminalStatus = pattern.dotTerminalStatus();

        for (int i = 0; i < 9; i++) {
            var pos = getPatternDotPosition(x + patternSize + 4, y + patternSize + 4, i, patternSize);

            var isLinked = pattern.contains(i);
            var dotSize = size / 50;

            var r = 0f;
            var g = 0f;
            var b = 0f;

            if (dotTerminalStatus[i] && Trickster.CONFIG.dotEmphasis()) {
                r = Trickster.CONFIG.dotEmphasisColor().red();
                g = Trickster.CONFIG.dotEmphasisColor().green();
                b = Trickster.CONFIG.dotEmphasisColor().blue();
            }

            var polygon = drawPolygon(
                    pos.x - dotSize, pos.y - dotSize,
                    pos.x - dotSize, pos.y + dotSize,
                    pos.x + dotSize, pos.y + dotSize,
                    pos.x + dotSize, pos.y - dotSize,
                    isLinked ? 0.9f : 0.5f
            );
            svg.with(polygon);

        }

        for (var line : pattern.entries()) {
            var now = getPatternDotPosition(x + patternSize + 4, y + patternSize + 4, line.p1(), patternSize);
            var last = getPatternDotPosition(x + patternSize + 4, y + patternSize + 4, line.p2(), patternSize);
            var lineSvg = drawLine(last, now, size / 50f);
            svg.with(lineSvg);
        }

        return svg.withClass("pattern");
    }

    public static Tag<?> signature(Signature<?> signature) {
        return text(signature.asText());
    }

    private static Tag<?> drawPolygon(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float alpha) {
        return tag("polygon")
                .attr("points", String.format("%f,%f %f,%f %f,%f %f,%f", x1, y1, x2, y2, x3, y3, x4, y4))
                .withStyle("opacity: " + alpha);

    }

    private static Tag<?> drawLine(Vector2f last, Vector2f now, float pixelSize) {
        var directionVec = new Vector2f(last.x - now.x, last.y - now.y).normalize().mul(pixelSize * 3);
        var lineStart = new Vector2f(last.x - directionVec.x, last.y - directionVec.y);
        var lineEnd = new Vector2f(now.x + directionVec.x, now.y + directionVec.y);

        return tag("line")
                .attr("x1", lineStart.x).attr("y1", lineStart.y)
                .attr("x2", lineEnd.x).attr("y2", lineEnd.y);
    }
}
