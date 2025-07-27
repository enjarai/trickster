package dev.enjarai.trickster.coleus;

import dev.enjarai.trickster.screen.owo.ManaCostComponent;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import mod.master_bw3.coleus.PageContext;
import mod.master_bw3.coleus.SearchEntry;
import net.minecraft.util.Identifier;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;
import static j2html.TagCreator.div;
import static mod.master_bw3.coleus.Components.tooltip;
import static mod.master_bw3.coleus.Components.owo;
import static io.wispforest.owo.ui.component.Components.texture;

public class TricksterTemplateExpanders {

    public static DomContent patternTemplate(Map<String, String> properties, PageContext context) {
        if (!properties.containsKey("pattern")) return text("");

        Pattern pattern = Pattern.from(Arrays.stream(properties.get("pattern").split(",")).map(s -> Byte.valueOf(s, 10)).toList());

        var div = div().withClass("trick");
        if (properties.containsKey("title")) {
            div.with(h2(properties.get("title")));
        }
        div.with(Components.pattern(pattern, 400));

        return div;
    }

    public static DomContent glyphTemplate(Map<String, String> properties, PageContext context) {
        if (!properties.containsKey("trick-id")) return text("");

        Identifier trickId = Identifier.of(properties.get("trick-id"));
        Trick<?> trick = Tricks.REGISTRY.get(trickId);

        return Components.pattern(trick.getPattern(), 400);
    }

    public static DomContent revisionTemplate(Map<String, String> properties, PageContext context) {
        var title = properties.get("title");
        var id = title.toLowerCase().replaceAll("[^a-z0-9]", "");
        Pattern pattern = Pattern.from(Arrays.stream(properties.get("pattern").split(",")).map(s -> Byte.valueOf(s, 10)).toList());

        context.addSearchEntry(new SearchEntry(
                title,
                "",
                context.getBookDir().relativize(context.getPagePath()) + "#" + id,
                List.of()
        ));

        var trickContainer = div().withClass("trick").withId(id)
                .with(
                        h2(title),
                        Components.pattern(pattern, 400),
                        span("(Scribing Pattern)").withClass("gray")
                );

        var manaCostContainer = div().withClass("cost-rule embedded-component-container");
        var texture = properties.getOrDefault("texture", properties.get("book-texture"));

        TextureComponent component = texture(
                Identifier.of(texture), 54, 183, 109,
                3, 512, 256)
                .blend(true);
        manaCostContainer.with(
                owo(Containers.verticalFlow(Sizing.fixed(112), Sizing.fixed(8)).child(component).padding(Insets.of(2, 0, 0, 0)),
                        context.getPagePath(), context.getAssetsDir().resolve(id + "-mana-cost.png"),
                        500, 2).withClass("embedded-component")
        );
        trickContainer.with(manaCostContainer);

        return trickContainer;
    }

    public static DomContent trickTemplate(Map<String, String> properties, PageContext context) {
        Identifier trickId = Identifier.of(properties.get("trick-id"));
        Trick<?> trick = Tricks.REGISTRY.get(trickId);

        context.addSearchEntry(new SearchEntry(
                trick.getName().getString(),
                trick.getSignatures().stream().map((signature) -> signature.asText().getString()).collect(Collectors.joining("\n")),
                context.getBookDir().relativize(context.getPagePath()) + "#" + trickId,
                List.of()
        ));

        var trickContainer = div().withClass("trick").withId(trickId.toString())
                .with(h2(trick.getName().getString()), Components.pattern(trick.getPattern(), 400))
                .with(trick.getSignatures().stream().flatMap(signature -> Arrays.stream(new DomContent[] { br(), br(), Components.signature(signature).withClass("signature") })));

        var manaCostContainer = div().withClass("cost-rule embedded-component-container");
        var texture = properties.getOrDefault("texture", properties.get("book-texture"));
        var hasCost = properties.containsKey("cost");
        if (hasCost) {
            FlowLayout component = new ManaCostComponent(properties.get("cost"), Identifier.of(texture));
            manaCostContainer.with(
                    owo(Containers.verticalFlow(Sizing.fixed(112), Sizing.fixed(8)).child(component).padding(Insets.of(2, 0, 0, 0)),
                            context.getPagePath(), context.getAssetsDir().resolve(trickId.getNamespace()).resolve(trickId.getPath() + "-mana-cost.png"),
                            500, 2).withClass("embedded-component"),
                    tooltip(component.childById(Component.class, "cost-texture").tooltip(),
                            context.getPagePath(), context.getAssetsDir().resolve(trickId.getNamespace()).resolve(trickId.getPath() + "-mana-cost-tooltip.png"),
                            2).withClass("embedded-component-tooltip")
            );
        } else {
            TextureComponent component = texture(
                    Identifier.of(texture), 54, 183, 109,
                    3, 512, 256)
                    .blend(true);

            manaCostContainer.with(
                    owo(Containers.verticalFlow(Sizing.fixed(112), Sizing.fixed(8)).child(component).padding(Insets.of(2, 0, 0, 0)),
                            context.getPagePath(), context.getAssetsDir().resolve(trickId.getNamespace()).resolve(trickId.getPath() + "-mana-cost.png"),
                            500, 2).withClass("embedded-component")
            );
        }
        trickContainer.with(manaCostContainer);

        return trickContainer;
    }

    public static DomContent spellPreviewTemplate(Map<String, String> properties, PageContext context) {
        if (!properties.containsKey("spell")) return text("");

        var spellString = properties.get("spell");
        var urlEncodedSpellString = URLEncoder.encode(spellString, StandardCharsets.UTF_8);

        return div().withClass("spell-preview").with(
                iframe().withSrc("https://trickster-studio.maplesyrum.me/viewer/?fixed=false&spell=" + urlEncodedSpellString)
                        .attr("allowtransparency", "true")
                        .withClass("spell-preview-iframe"),
                script(new UnescapedText(
                        """
                                (() => {
                                    const currentScript = document.currentScript;
                                    const parent = currentScript.parentElement;
                                    const iframeElement = parent.querySelector(".spell-preview-iframe");
                                    const pageElement = document.getElementById("page");

                                    iframeElement.addEventListener("mouseenter", () => {
                                      pageElement.dataset.originalOverflow = pageElement.style.overflow;
                                      pageElement.style.overflow = "hidden";
                                    });

                                    iframeElement.addEventListener("mouseleave", () => {
                                      pageElement.style.overflow = pageElement.dataset.originalOverflow || "";
                                    });
                                })()
                                """
                ))
        );
    }

}
