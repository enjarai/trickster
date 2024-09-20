package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.ScrollInGamePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ScrollContainerScreen extends HandledScreen<ScrollContainerScreenHandler> implements ScreenHandlerProvider<ScrollContainerScreenHandler> {
    private static final Identifier TEXTURE = Trickster.id("textures/gui/scroll_54.png");
//    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");
    private static final Identifier SELECTED_SLOT_TEXTURE = Trickster.id("textures/gui/selected_slot.png");
    private final int rows;

    public ScrollContainerScreen(ScrollContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.rows = handler.getRows();
        this.backgroundHeight = 114 + this.rows * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
        context.drawTexture(TEXTURE, i, j + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0xc0c0c0, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 0x404040, false);
    }

    @Override
    protected void drawSlot(DrawContext context, Slot slot) {
        super.drawSlot(context, slot);

        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 0.0F, 1000.0F);
        if (slot.id == handler.selectedSlot.get()) {
            context.drawTexture(SELECTED_SLOT_TEXTURE, slot.x - 4, slot.y - 4, 0, 0, 24, 24, 24, 24);
        }
        context.getMatrices().pop();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            var maxSlot = 0;
            var inventory = handler.getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                if (!inventory.getStack(i).isEmpty()) {
                    maxSlot = i + 1;
                }
            }

            var newSlot = (int) Math.round(handler.selectedSlot.get() + verticalAmount);

            if (maxSlot > 0) {
                while (newSlot < 0) {
                    newSlot += maxSlot;
                }
                while (newSlot >= maxSlot) {
                    newSlot -= maxSlot;
                }
            } else {
                newSlot = 0;
            }

            ModNetworking.CHANNEL.clientHandle().send(new ScrollInGamePacket((float) (Trickster.CONFIG.invertTopHatScrolling() ? -verticalAmount : verticalAmount)));
            handler.selectedSlot.set(newSlot);
        }

        return true;
    }
}

