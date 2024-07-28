package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class ScrollAndQuillScreen extends Screen implements ScreenHandlerProvider<ScrollAndQuillScreenHandler> {
    private static final ArrayList<PositionMemory> storedPositions = new ArrayList<>(5);

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
                handler.spell.get(), width / 2d, height / 2d, 64,
                handler::updateSpell, handler::updateOtherHandSpell,
                handler.otherHandSpell::get, handler::executeOffhand
        );
        handler.replacerCallback = frag -> partWidget.replaceCallback(frag);
        addDrawableChild(partWidget);

        this.handler.spell.observe(spell -> {
            partWidget.setSpell(spell);

            var spellHash = handler.spell.get().hashCode();
            for (var position : storedPositions) {
                if (position.spell == spellHash) {
                    partWidget.x = position.x;
                    partWidget.y = position.y;
                    partWidget.size = position.size;
                    break;
                }
            }
        });
        this.handler.isMutable.observe(mutable -> partWidget.setMutable(mutable));
    }

    @Override
    public void close() {
        var spellHash = handler.spell.get().hashCode();
        storedPositions.removeIf(position -> position.spell == spellHash);
        storedPositions.add(new PositionMemory(spellHash, partWidget.x, partWidget.y, partWidget.size));
        if (storedPositions.size() >= 5) {
            storedPositions.removeFirst();
        }

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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, 0, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.setDragging(true);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isDragging()) this.setDragging(false);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public ScrollAndQuillScreenHandler getScreenHandler() {
        return handler;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void applyBlur(float delta) {
        if (!this.client.player.getOffHandStack().isOf(ModItems.TOME_OF_TOMFOOLERY)) {
            super.applyBlur(delta);
        }
    }

    record PositionMemory(int spell, BigDecimal x, BigDecimal y, BigDecimal size) {

    }
}
