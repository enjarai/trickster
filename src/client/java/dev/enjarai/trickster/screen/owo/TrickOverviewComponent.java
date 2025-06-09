package dev.enjarai.trickster.screen.owo;

import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

public class TrickOverviewComponent extends FlowLayout {
    protected Identifier bookTexture;
    protected Trick<?> trick;
    protected @Nullable String costCalculation;

    public TrickOverviewComponent(Trick<?> trick, @Nullable String costCalculation, Identifier bookTexture) {
        super(Sizing.content(), Sizing.fill(100), Algorithm.VERTICAL);
        this.trick = trick;
        this.costCalculation = costCalculation;
        this.bookTexture = bookTexture;

        alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        child(Components.label(trick.getName().setStyle(Style.EMPTY))
                .color(Color.ofFormatting(Formatting.DARK_GRAY))
                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                .margins(Insets.of(2, 2, 0, 0))
                .sizing(Sizing.fill(100), Sizing.content())
        );
        child(new GlyphComponent(trick, 50));

        if (costCalculation != null) {
            child(Components.texture(
                    bookTexture, 54, 183, 109,
                    3, 512, 256)
                    .blend(true)
            );
        }
    }

    public static TrickOverviewComponent parse(Element element) {
        UIParsing.expectAttributes(element, "trick-id");
        UIParsing.expectAttributes(element, "texture");

        var trickId = UIParsing.parseIdentifier(element.getAttributeNode("trick-id"));
        var trick = Tricks.REGISTRY.get(trickId);

        if (trick == null) {
            throw new UIModelParsingException("Not a valid trick: " + trickId);
        }

        String costCalculation = null;
        if (element.hasAttribute("cost")) {
            costCalculation = element.getAttribute("cost");
        }

        var texture = UIParsing.parseIdentifier(element.getAttributeNode("texture"));

        return new TrickOverviewComponent(trick, costCalculation, texture);
    }
}
