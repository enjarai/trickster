package dev.enjarai.trickster.mixin.client;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.screen.SpellSlotWidget;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends RecipeBookScreen<PlayerScreenHandler> {
    @Unique
    public List<SpellSlotWidget> spellSlots;

    public InventoryScreenMixin(PlayerScreenHandler handler, RecipeBookWidget<?> recipeBook, PlayerInventory inventory, Text title) {
        super(handler, recipeBook, inventory, title);
    }

    @Inject(
            method = "init",
            at = @At("TAIL")
    )
    private void initSpellSlots(CallbackInfo ci) {
        spellSlots = new ArrayList<>();
        var spellData = client.player.getComponent(ModEntityComponents.CASTER).getRunningSpellData();

        for (int i = 0; i < 5; i++) {
            var widget = new SpellSlotWidget(x + 156 - i * 18, y + 166, i);
            widget.updateState(spellData.get(i));
            spellSlots.add(widget);
            addDrawableChild(widget);
        }
    }

    @Inject(
            method = "handledScreenTick",
            at = @At("TAIL")
    )
    private void tickSpellSlots(CallbackInfo ci) {
        var spellData = client.player.getComponent(ModEntityComponents.CASTER).getRunningSpellData();

        for (var slot : spellSlots) {
            slot.setPosition(x + 156 - slot.index * 18, y + 166);
            slot.updateState(spellData.get(slot.index));
        }
    }
}
