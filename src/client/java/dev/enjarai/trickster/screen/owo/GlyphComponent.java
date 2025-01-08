package dev.enjarai.trickster.screen.owo;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static dev.enjarai.trickster.render.SpellCircleRenderer.*;

public class GlyphComponent extends BaseComponent {
    protected Pattern pattern;
    protected List<Integer> patternList;
    protected int size;

    public GlyphComponent(Trick<?> trick, int size) {
        this(trick.getPattern(), size);
    }

    public GlyphComponent(Pattern pattern, int size) {
        super();
        this.pattern = pattern;
        this.size = size;
        this.patternList = pattern
                .entries().stream()
                .flatMapToInt(e -> IntStream.of(e.p1(), e.p2()))
                .distinct()
                .boxed()
                .toList();
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var patternSize = size / 2;

        for (int i = 0; i < 9; i++) {
            var pos = getPatternDotPosition(x + patternSize + 4, y + patternSize + 4, i, patternSize);

            var isLinked = patternList.contains(i);
            var dotSize = 1;

            drawFlatPolygon(context.getMatrices(), context.getVertexConsumers(), c -> {
                c.accept(pos.x - dotSize, pos.y - dotSize);
                c.accept(pos.x - dotSize, pos.y + dotSize);
                c.accept(pos.x + dotSize, pos.y + dotSize);
                c.accept(pos.x + dotSize, pos.y - dotSize);
            }, 0, 0, 0, 0, isLinked ? 0.9f : 0.5f);
        }

        for (var line : pattern.entries()) {
            var now = getPatternDotPosition(x + patternSize + 4, y + patternSize + 4, line.p1(), patternSize);
            var last = getPatternDotPosition(x + patternSize + 4, y + patternSize + 4, line.p2(), patternSize);
            drawGlyphLine(context.getMatrices(), context.getVertexConsumers(), last, now, 1, false, 0, 1f, 1f, 1f, 0.9f, false);
        }
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return size + 8;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return size + 8;
    }

    public static GlyphComponent parseTrick(Element element) {
        UIParsing.expectAttributes(element, "trick-id");
        UIParsing.expectAttributes(element, "size");

        var trickId = UIParsing.parseIdentifier(element.getAttributeNode("trick-id"));
        var trick = Tricks.REGISTRY.get(trickId);

        if (trick == null) {
            throw new UIModelParsingException("Not a valid trick: " + trickId);
        }

        var size = UIParsing.parseUnsignedInt(element.getAttributeNode("size"));

        return new GlyphComponent(trick, size);
    }

    public static GlyphComponent parseList(Element element) {
        UIParsing.expectAttributes(element, "pattern");
        UIParsing.expectAttributes(element, "size");

        var patternString = element.getAttributeNode("pattern").getTextContent();

        var pattern = Pattern.from(
                Arrays.stream(patternString.split(","))
                        .map(s -> Byte.valueOf(s, 10)).toList()
        );

        var size = UIParsing.parseUnsignedInt(element.getAttributeNode("size"));

        return new GlyphComponent(pattern, size);
    }
}
