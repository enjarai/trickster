package dev.enjarai.trickster.screen.owo;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.revision.Revision;
import dev.enjarai.trickster.spell.revision.Revisions;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

public class TrickOverviewComponent extends FlowLayout {

    public static TrickOverviewComponent of(Trick<?> trick, @Nullable String costCalculation, Identifier bookTexture) {
        var pattern = trick.getPattern();

        var name = Text.empty();
        trick.getName().getSiblings().forEach(s -> name.append(s.copy().formatted(Formatting.DARK_GRAY)));

        var signatures = Text.empty();
        for (var iterator = trick.getSignatures().iterator(); iterator.hasNext();) {
            var signature = iterator.next();
            signatures = signatures.append(signature.asText());
            if (iterator.hasNext()) {
                signatures = signatures.append("\n");
            }
        }

        return new TrickOverviewComponent(pattern, name, signatures, costCalculation, bookTexture);
    }

    public static TrickOverviewComponent of(Revision rev, Identifier bookTexture) {
        var pattern = rev.pattern();

        var name = Text.empty();
        rev.getName().getSiblings().forEach(s -> name.append(s.copy().formatted(Formatting.DARK_GRAY)));

        return new TrickOverviewComponent(pattern, name, Text.translatable("trickster.revision"), null, bookTexture);
    }

    public static TrickOverviewComponent of(Pattern pattern, Text title, MutableText content, @Nullable String costCalculation, Identifier bookTexture) {
        return new TrickOverviewComponent(pattern, title, content, costCalculation, bookTexture);

    }

    private TrickOverviewComponent(Pattern pattern, Text title, MutableText content, @Nullable String costCalculation, Identifier bookTexture) {
        super(Sizing.fill(100), Sizing.content(), Algorithm.VERTICAL);
        alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        child(Components.label(title)
            .color(Color.ofFormatting(Formatting.DARK_GRAY))
            .horizontalTextAlignment(HorizontalAlignment.CENTER)
            .margins(Insets.of(2, 2, 0, 0))
            .sizing(Sizing.fill(100), Sizing.content())
        );
        child(new GlyphComponent(pattern, 50));

        child(Components.label(content.styled(s -> s
            .withFormatting(Formatting.DARK_GRAY)
            .withFont(MinecraftClient.UNICODE_FONT_ID)))
            .horizontalTextAlignment(HorizontalAlignment.LEFT)
            .horizontalSizing(Sizing.fill(100))
            .margins(Insets.of(0, 5, 3, 3)));

        if (costCalculation != null) {
            child(new ManaCostComponent(costCalculation, bookTexture));
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
        UIParsing.expectAttributes(element, "texture");
        var texture = UIParsing.parseIdentifier(element.getAttributeNode("texture"));

        var trickIdAttribute = element.getAttributeNode("trick-id");
        if (trickIdAttribute != null) {
            UIParsing.expectAttributes(element, "cost");
            var trickId = UIParsing.parseIdentifier(trickIdAttribute);
            var trick = Tricks.REGISTRY.get(trickId);

            if (trick == null) {
                throw new UIModelParsingException("Not a valid trick: " + trickId);
            }

            String costCalculation = null;
            if (element.hasAttribute("cost")) {
                costCalculation = element.getAttribute("cost");
            }

            return TrickOverviewComponent.of(trick, costCalculation, texture);
        }

        var revisionIdAttribute = element.getAttributeNode("revision-id");
        if (revisionIdAttribute != null) {
            var revisionId = UIParsing.parseIdentifier(revisionIdAttribute);
            var revision = Revisions.REGISTRY.get(revisionId);

            if (revision == null) {
                throw new UIModelParsingException("Not a valid revision: " + revisionId);
            }

            return TrickOverviewComponent.of(revision, texture);
        }

        throw new UIModelParsingException("trick-overview needs either trick-id or revision-id set");
    }
}
