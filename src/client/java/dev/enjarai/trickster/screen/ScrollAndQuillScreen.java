package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class ScrollAndQuillScreen extends Screen implements ScreenHandlerProvider<ScrollAndQuillScreenHandler> {
    private static final ArrayList<PositionMemory> storedPositions = new ArrayList<>(5);

    protected final ScrollAndQuillScreenHandler handler;

    public SpellPartWidget partWidget;

    private boolean hasLoaded = false;

    static final double ZOOM_SPEED = 1.0;
    static final double PAN_SPEED = 9.0;
    static final double SPRINT_MULTIPLIER = 1.4142;

    private double inputX = 0;
    private double inputY = 0;
    private double inputZ = 0;
    private boolean inputSprint = false;

    public ScrollAndQuillScreen(ScrollAndQuillScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(title);
        this.handler = handler;
    }

    @Override
    protected void init() {
        super.init();
        partWidget = new SpellPartWidget(handler.spell.get(), width / 2d, height / 2d, 64, handler, true);
        handler.replacerCallback = frag -> partWidget.replaceCallback(frag);
        handler.updateDrawingPartCallback = spell -> partWidget.updateDrawingPartCallback(spell);

        addDrawableChild(partWidget);

        this.handler.spell.observe(spell -> {
            var spellHash = spell.hashCode();

            if (!hasLoaded)
                for (var position : storedPositions) {
                    if (position.spellHash == spellHash) {
                        partWidget.load(position);
                        break;
                    }
                }

            partWidget.setSpell(spell);
            hasLoaded = true;
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        double deltaWithSprint = inputSprint ? SPRINT_MULTIPLIER * delta: delta;
        if (inputZ != 0.0) {
            partWidget.mouseScrolled(mouseX, mouseY, 0.0, inputZ * ZOOM_SPEED * deltaWithSprint);
        }
        if (inputX != 0.0 || inputY != 0.0) {
            partWidget.mouseDragged(mouseX, mouseY, 0, inputX * PAN_SPEED * deltaWithSprint, inputY * PAN_SPEED * deltaWithSprint);
        }
    }
   
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        } else if (this.client.options.forwardKey.matchesKey(keyCode, scanCode)) {
            inputZ = 1.0;
            return true;
        } else if (this.client.options.backKey.matchesKey(keyCode, scanCode)) {
            inputZ = -1.0;
            return true;
        } else if (this.client.options.leftKey.matchesKey(keyCode, scanCode)) {
            inputX = 1.0;
            return true;
        } else if (this.client.options.rightKey.matchesKey(keyCode, scanCode)) {
            inputX = -1.0;
            return true;
        } else if (this.client.options.sneakKey.matchesKey(keyCode, scanCode)) {
            inputY = -1.0;
            return true;
        } else if (this.client.options.jumpKey.matchesKey(keyCode, scanCode)) {
            inputY = 1.0;
            return true;
        } else if (this.client.options.sprintKey.matchesKey(keyCode, scanCode)) {
            inputSprint = true;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.forwardKey.matchesKey(keyCode, scanCode)) {
            inputZ = 0.0;
            return true;
        } else if (this.client.options.backKey.matchesKey(keyCode, scanCode)) {
            inputZ = 0.0;
            return true;
        } else if (this.client.options.leftKey.matchesKey(keyCode, scanCode)) {
            inputX = 0.0;
            return true;
        } else if (this.client.options.rightKey.matchesKey(keyCode, scanCode)) {
            inputX = 0.0;
            return true;
        } else if (this.client.options.sneakKey.matchesKey(keyCode, scanCode)) {
            inputY = 0.0;
            return true;
        } else if (this.client.options.jumpKey.matchesKey(keyCode, scanCode)) {
            inputY = 0.0;
            return true;
        } else if (this.client.options.sprintKey.matchesKey(keyCode, scanCode)) {
            inputSprint = false;
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
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
