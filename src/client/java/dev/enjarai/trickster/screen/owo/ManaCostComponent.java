package dev.enjarai.trickster.screen.owo;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;

public class ManaCostComponent extends FlowLayout {

    public ManaCostComponent(String costCalculation, Identifier bookTexture) {
        super(Sizing.fill(100), Sizing.fixed(3), io.wispforest.owo.ui.container.FlowLayout.Algorithm.VERTICAL);

        child(Components.texture(bookTexture, 54, 240, 109, 5, 512, 256)
                .blend(true)
                .id("cost-texture")
                .positioning(Positioning.absolute(2, -1))
                .tooltip(Text.literal("Costs mana\n")
                .append(Text.literal(costCalculation)
                .styled(s -> s.withFormatting(Formatting.GRAY))))).allowOverflow(true)
                .horizontalAlignment(HorizontalAlignment.CENTER);
    }
}
