package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.SignScrollPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
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
        var centerX = width / 2;
        var centerY = height / 2;

        addDrawableChild(new TextWidget(
                centerX - 100, centerY - 36, 200, 10,
                Text.translatable("trickster.widget.enter_scroll_name"),
                textRenderer));

        textField = addDrawableChild(new TextFieldWidget(
                textRenderer, centerX - 100, centerY - 24, 200, 20,
                Text.translatable("trickster.widget.scroll_name")));

        addDrawableChild(ButtonWidget.builder(Text.translatable("book.signButton"), button -> {
            ModNetworking.CHANNEL.clientHandle().send(new SignScrollPacket(hand, textField.getText()));
            this.client.setScreen(null);
        }).dimensions(centerX - 100, centerY, 98, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
            this.client.setScreen(null);
        }).dimensions(centerX + 2, centerY, 98, 20).build());

        addDrawableChild(new TextWidget(
                centerX - 100, centerY + 24, 200, 10,
                Text.translatable("trickster.widget.scroll_sign_note.line0"),
                textRenderer));
        addDrawableChild(new TextWidget(
                centerX - 100, centerY + 34, 200, 10,
                Text.translatable("trickster.widget.scroll_sign_note.line1"),
                textRenderer));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
