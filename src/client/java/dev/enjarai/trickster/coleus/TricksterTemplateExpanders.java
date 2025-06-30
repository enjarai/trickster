package dev.enjarai.trickster.coleus;
//

//import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.Pattern;
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

            return iframe()
                    .withSrc("http://localhost:3000/pattern/" + URLEncoder.encode(pattern.toBase64(), StandardCharsets.UTF_8))
                    .withHeight("200")
                    .withWidth("200");
        }

    public static DomContent glyphTemplate(Map<String, String> properties, Path pagePath, Path extraResourcesDir) {
        if (!properties.containsKey("trick-id")) return text("");

        Identifier trickId = Identifier.of(properties.get("trick-id"));
        Pattern pattern = Tricks.REGISTRY.get(trickId).getPattern();

        return iframe()
                .withSrc("http://localhost:3000/pattern/" + URLEncoder.encode(pattern.toBase64(), StandardCharsets.UTF_8))
                .withHeight("200")
                .withWidth("200");
    }


}
