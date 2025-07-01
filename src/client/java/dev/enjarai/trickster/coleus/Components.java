package dev.enjarai.trickster.coleus;

import com.glisco.isometricrenders.render.RenderableDispatcher;
import dev.enjarai.trickster.spell.Pattern;
import j2html.tags.specialized.ImgTag;

import java.io.IOException;
import java.nio.file.Path;
import static j2html.TagCreator.*;

public class Components {
    public static ImgTag pattern(Pattern pattern, Path pagePath, Path imageOutPath, int size) {
        try(var image = RenderableDispatcher.drawIntoImage(new PatternRenderable(pattern, size), 0f, size);) {
            imageOutPath.getParent().toFile().mkdirs();
            image.writeTo(imageOutPath);
            return img().withSrc(pagePath.getParent().relativize(imageOutPath).toString());

        } catch (IOException e) {
            return img();
        }
    }
}
