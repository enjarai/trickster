package dev.enjarai.trickster.screen.owo;

import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

public class TrickOverviewComponent extends FlowLayout {
    protected Identifier bookTexture;
    protected Trick<?> trick;
    protected @Nullable String costCalculation;

    public TrickOverviewComponent(Trick<?> trick, @Nullable String costCalculation, Identifier bookTexture) {
        super(Sizing.fill(100), Sizing.content(), Algorithm.VERTICAL);
        this.trick = trick;
        this.costCalculation = costCalculation;
        this.bookTexture = bookTexture;

        alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        var name = Text.empty();
        trick.getName().getSiblings().forEach(s -> name.append(s.copy().formatted(Formatting.DARK_GRAY)));
        child(Components.label(name)
                .color(Color.ofFormatting(Formatting.DARK_GRAY))
                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                .margins(Insets.of(2, 2, 0, 0))
                .sizing(Sizing.fill(100), Sizing.content())
        );
        child(new GlyphComponent(trick, 50));

        var signatures = Text.empty();
        for (var iterator = trick.getSignatures().iterator(); iterator.hasNext();) {
            var signature = iterator.next();
            signatures = signatures.append(signature.asText());
            if (iterator.hasNext()) {
                signatures = signatures.append("\n");
            }
        }
        child(Components.label(signatures.styled(s -> s
                .withFormatting(Formatting.DARK_GRAY)
                .withFont(MinecraftClient.UNICODE_FONT_ID)))
                .horizontalTextAlignment(HorizontalAlignment.LEFT)
                .horizontalSizing(Sizing.fill(100))
                .margins(Insets.of(0, 5, 3, 3)));

        if (costCalculation != null) {
            child(Containers.verticalFlow(Sizing.fill(100), Sizing.fixed(3))
                    .child(Components.texture(
                            bookTexture, 54, 240, 109,
                            5, 512, 256)
                            .blend(true)
                            .positioning(Positioning.absolute(2, -1))
                            .tooltip(Text.literal("Costs mana\n").append(Text.literal(costCalculation)
                                    .styled(s -> s.withFormatting(Formatting.GRAY))))
                    )
                    .allowOverflow(true)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
            );
        } else {
            child(Components.texture(
                    bookTexture, 54, 183, 109,
                    3, 512, 256)
                    .blend(true)
            );
        }

        allowOverflow(true);
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
