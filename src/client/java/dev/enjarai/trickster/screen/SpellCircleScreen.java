package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class SpellCircleScreen extends Screen {
    public SpellPartWidget partWidget;


    public SpellCircleScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        var part = new SpellPart(
                new PatternGlyph(1, 2, 3, 4),
                List.of(
                        Optional.of(new SpellPart(
                                new PatternGlyph(2, 3, 6, 7),
                                List.of()
                        )),
                        Optional.of(new SpellPart(
                                new PatternGlyph(2, 3, 6, 7),
                                List.of(
                                        Optional.of(new SpellPart(
                                                new PatternGlyph(8, 6, 4),
                                                List.of()
                                        )),
                                        Optional.of(new SpellPart(
                                                new SpellPart(
                                                        new PatternGlyph(8, 6, 4),
                                                        List.of()
                                                ),
                                                List.of(
                                                        Optional.of(new SpellPart(
                                                                new PatternGlyph(8, 6, 4),
                                                                List.of()
                                                        ))
                                                )
                                        )),
                                        Optional.empty(),
                                        Optional.empty()
                                )
                        )),
                        Optional.of(new SpellPart(
                                new PatternGlyph(1, 7, 5, 8),
                                List.of(
                                        Optional.of(new SpellPart(
                                                new PatternGlyph(8, 6, 4),
                                                List.of()
                                        ))
                                )
                        ))
                )
        );

//        partWidget = new SpellPartWidget(part, width / 2d, height / 2d, 64, spell -> {});
//        addDrawableChild(partWidget);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        partWidget.mouseMoved(mouseX, mouseY);
    }
}
