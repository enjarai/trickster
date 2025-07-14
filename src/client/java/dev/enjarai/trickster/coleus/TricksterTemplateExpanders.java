package dev.enjarai.trickster.coleus;

import dev.enjarai.trickster.screen.owo.ManaCostComponent;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import j2html.tags.DomContent;
import mod.master_bw3.coleus.PageContext;
import mod.master_bw3.coleus.SearchEntry;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
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

        var trick = Tricks.lookup(pattern);

        String patternName;
        if (trick != null) {
            patternName = trick.getName().getString();
        } else {
            patternName = Integer.toString(pattern.toInt());
        }

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

    public static DomContent trickTemplate(Map<String, String> properties, PageContext context) {
        if (!properties.containsKey("trick-id")) return text("");

        Identifier trickId = Identifier.of(properties.get("trick-id"));
        //var trickElementId = trickId.toString().replace(':', '-');
        Trick<?> trick = Tricks.REGISTRY.get(trickId);

        context.addSearchEntry(new SearchEntry(
                trick.getName().getString(),
                trick.getSignatures().stream().map((signature) -> signature.asText().getString()).collect(Collectors.joining("\n")),
                context.getBookDir().relativize(context.getPagePath()) + "#" + trickId,
                List.of()
                ));

        var manaCostContainer = div().withClass("cost-rule embedded-component-container");
        var trickContainer = div().withClass("trick").withId(trickId.toString())
                .with(h2(trick.getName().getString()), Components.pattern(trick.getPattern(), 400))
                .with(trick.getSignatures().stream().flatMap(signature -> Arrays.stream(new DomContent[]{br(), br(), Components.signature(signature).withClass("signature")})))
                .with(manaCostContainer);

        var texture = properties.getOrDefault("texture", properties.get("book-texture"));
        var hasCost = properties.containsKey("cost");
        Component component;
        if (hasCost) {
            component = new ManaCostComponent(properties.get("cost"), Identifier.of(texture));
        } else {
            component = texture(Identifier.of(texture), 54, 183, 109,
                            3, 512, 256).blend(true);
        }
        manaCostContainer.with(
                owo(Containers.verticalFlow(Sizing.fixed(112), Sizing.fixed(8)).child(component).padding(Insets.of(2, 0, 0, 0)),
                        context.getPagePath(), context.getAssetsDir().resolve(trickId.getNamespace()).resolve(trickId.getPath() + "-mana-cost.png"),
                        500, 2).withClass("embedded-component")
        );
        if (hasCost) {
            manaCostContainer.with(
                    tooltip(((FlowLayout) component).childById(Component.class, "cost-texture").tooltip(),
                            context.getPagePath(), context.getAssetsDir().resolve(trickId.getNamespace()).resolve(trickId.getPath() + "-mana-cost-tooltip.png"),
                            2).withClass("embedded-component-tooltip")
            );
        }

        return trickContainer;
    }
}
