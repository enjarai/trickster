package dev.enjarai.trickster.coleus;

import mod.master_bw3.coleus.HtmlTemplateRegistry;
import net.minecraft.util.Identifier;

public class ColeusIntegration {
    public static void init() {
        HtmlTemplateRegistry.register(Identifier.of("trickster", "pattern"), TricksterTemplateExpanders::patternTemplate);
        HtmlTemplateRegistry.register(Identifier.of("trickster", "glyph"), TricksterTemplateExpanders::glyphTemplate);
        HtmlTemplateRegistry.register(Identifier.of("trickster", "trick"), TricksterTemplateExpanders::trickTemplate);
        HtmlTemplateRegistry.register(Identifier.of("trickster", "ploy"), TricksterTemplateExpanders::trickTemplate);
        HtmlTemplateRegistry.register(Identifier.of("trickster", "spell-preview"), TricksterTemplateExpanders::spellPreviewTemplate);
        HtmlTemplateRegistry.register(Identifier.of("trickster", "spell-preview-unloadable"), TricksterTemplateExpanders::spellPreviewTemplate);
    }
}
