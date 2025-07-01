package dev.enjarai.trickster.coleus;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import mod.master_bw3.coleus.HtmlTemplateRegistry;
import j2html.tags.DomContent;
import net.minecraft.util.Identifier;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import static j2html.TagCreator.*;

public class TricksterTemplateExpanders {

    public static DomContent patternTemplate(Map<String, String> properties, Path pagePath, Path extraResourcesDir) {
        if (!properties.containsKey("pattern")) return text("");

        Pattern pattern = pattern = Pattern.from(
                Arrays.stream(properties.get("pattern").split(","))
                        .map(s -> Byte.valueOf(s, 10)).toList()
        );

        var trick = Tricks.lookup(pattern);

        String patternName;
        if (trick != null) {
            patternName = trick.getName().getString();
        } else {
            patternName = Integer.toString(pattern.toInt());
        }

        var saveLocation = extraResourcesDir.resolve("pattern").resolve(patternName+".png");
        return  Components.pattern(pattern, pagePath, saveLocation, 500);
    }

    public static DomContent glyphTemplate(Map<String, String> properties, Path pagePath, Path extraResourcesDir) {
        if (!properties.containsKey("trick-id")) return text("");

        Identifier trickId = Identifier.of(properties.get("trick-id"));
        Trick<?> trick = Tricks.REGISTRY.get(trickId);

        var saveLocation = extraResourcesDir.resolve("pattern").resolve(trick.getName().getString()+".png");
        return Components.pattern(trick.getPattern(), pagePath, saveLocation, 500);
    }


}
