package dev.enjarai.trickster.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ScrollAndQuillScreen extends Screen implements ScreenHandlerProvider<ScrollAndQuillScreenHandler> {
    protected final ScrollAndQuillScreenHandler handler;

    public SpellPartWidget partWidget;

    public ScrollAndQuillScreen(ScrollAndQuillScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(title);
        this.handler = handler;
    }

    @Override
    protected void init() {
        super.init();

        partWidget = new SpellPartWidget(
                handler.spell.get(), width / 2d, height / 2d, 64, handler::updateSpell,
                handler.otherHandSpell::get, handler::executeOffhand
        );
        handler.replacerCallback = frag -> partWidget.replaceCallback(frag);
        addDrawableChild(partWidget);

        this.handler.spell.observe(spell -> partWidget.setSpell(spell));
        this.handler.isMutable.observe(mutable -> partWidget.setMutable(mutable));
    }

    @Override
    public void close() {
        //noinspection DataFlowIssue
        this.client.player.closeHandledScreen();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        partWidget.mouseMoved(mouseX, mouseY);
    }

    @Override
    public ScrollAndQuillScreenHandler getScreenHandler() {
        return handler;
    }
}
