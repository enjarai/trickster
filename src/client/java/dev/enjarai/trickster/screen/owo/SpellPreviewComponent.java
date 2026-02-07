package dev.enjarai.trickster.screen.owo;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.net.LoadExampleSpellPacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.spell.revision.RevisionContext;
import dev.enjarai.trickster.screen.scribing.CircleSoupWidget;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Clip;
import io.wispforest.owo.ui.component.BraidComponent;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.text.Text;
import org.w3c.dom.Element;

public class SpellPreviewComponent extends BraidComponent {
    protected SpellPart spell;

    private SpellPreviewComponent(Widget braidWidget) {
        super(braidWidget);
    }

    public static SpellPreviewComponent of(SpellPart spell) {
        var widget = new CircleSoupWidget(
            SpellView.index(spell), RevisionContext.DUMMY, false, false,
            0, 0, 24, 0, 0, (view, x1, y1, radius, angle, centerOffset) -> {}
        );
        var component = new SpellPreviewComponent(new Clip(
            widget
        ));
        component.spell = spell;
        widget.renderer.setColor(0.2f, 0.2f, 0.2f);
        widget.renderer.setCircleTransparency(0.6f);
        return component;
    }

    @Override
    public void mount(ParentComponent parent, int x, int y) {
        super.mount(parent, x, y);

        var loadButton = parent.childById(ButtonComponent.class, "load-button");
        if (loadButton != null) {
            loadButton.onPress(this::loadSpellToGame);
        }
    }

    public void loadSpellToGame(ButtonComponent button) {
        var client = MinecraftClient.getInstance();
        var bookScreen = client.currentScreen;

        client.setScreen(new ConfirmScreen(ok -> {
            if (ok) {
                ModNetworking.CHANNEL.clientHandle().send(new LoadExampleSpellPacket(spell));
                client.setScreen(null);
            } else {
                client.setScreen(bookScreen);
            }
        },
            Text.translatable("trickster.message.import_example"),
            Text.translatable("trickster.message.import_example.description")
        ));
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return 100;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 100;
    }

    public static SpellPreviewComponent parse(Element element) {
        UIParsing.expectAttributes(element, "spell");

        var spellString = element.getAttributeNode("spell").getTextContent();
        SpellPart spell;
        try {
            spell = (SpellPart) Fragment.fromBase64(spellString);
        } catch (Exception e) {
            throw new UIModelParsingException("Not a valid spell: " + spellString, e);
        }

        return SpellPreviewComponent.of(spell);
    }
}
