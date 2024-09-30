package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.MacroComponent;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Stack;

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
        partWidget = new SpellPartWidget(handler.spell.get(), width / 2d, height / 2d, 64, handler.macros.get(), handler);
        handler.replacerCallback = frag -> partWidget.replaceCallback(frag);
        handler.updateDrawingPartCallback = spell -> partWidget.updateDrawingPartCallback(spell);

        addDrawableChild(partWidget);

        this.handler.macros.observe(macros -> {
            partWidget.setMacros(macros);
        });

        this.handler.spell.observe(spell -> {
            var spellHash = spell.hashCode();

            for (var position : storedPositions) {
                if (position.spellHash == spellHash) {
                    partWidget.load(position);
                    break;
                }
            }

            partWidget.setSpell(spell);
        });
        this.handler.isMutable.observe(mutable -> partWidget.setMutable(mutable));
    }

    @Override
    public void close() {
        var saved = partWidget.save();
        storedPositions.removeIf(position -> position.spellHash == saved.spellHash);
        storedPositions.add(saved);
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

    record PositionMemory(int spellHash,
                          double x,
                          double y,
                          double size,
                          SpellPart rootSpellPart,
                          SpellPart spellPart,
                          ArrayList<SpellPart> parents,
                          ArrayList<Double> angleOffsets) { }
}
