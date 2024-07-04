package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.SignScrollPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class SignScrollScreen extends Screen {
    private final Hand hand;
    private TextFieldWidget textField;

    public SignScrollScreen(Text title, Hand hand) {
        super(title);
        this.hand = hand;
    }

    @Override
    protected void init() {
        textField = addDrawableChild(new TextFieldWidget(
                textRenderer, this.width / 2 - 100, 80, 200, 20,
                Text.translatable("trickster.widget.scroll_name")
        ));

        addDrawableChild(ButtonWidget.builder(Text.translatable("book.signButton"), button -> {
            ModNetworking.CHANNEL.clientHandle().send(new SignScrollPacket(hand, textField.getText()));
            this.client.setScreen(null);
        }).dimensions(this.width / 2 - 100, 104, 98, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
            this.client.setScreen(null);
        }).dimensions(this.width / 2 + 2, 104, 98, 20).build());
    }
}
