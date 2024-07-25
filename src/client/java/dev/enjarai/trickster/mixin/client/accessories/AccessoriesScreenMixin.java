package dev.enjarai.trickster.mixin.client.accessories;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.screen.SpellSlotWidget;
import io.wispforest.accessories.client.AccessoriesMenu;
import io.wispforest.accessories.client.gui.AccessoriesScreen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
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

// TODO: decide if we want to keep this? Its nice, but might be prone to breaking when blod updates accessories to use owo more...
@Mixin(AccessoriesScreen.class)
public abstract class AccessoriesScreenMixin extends AbstractInventoryScreen<AccessoriesMenu> {
    public AccessoriesScreenMixin(AccessoriesMenu screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Unique
    public List<SpellSlotWidget> spellSlots;

    @Inject(
            method = "init",
            at = @At("TAIL")
    )
    private void initSpellSlots(CallbackInfo ci) {
        spellSlots = new ArrayList<>();
        var spellData = client.player.getComponent(ModEntityCumponents.CASTER).getRunningSpellData();

        for (int i = 0; i < 5; i++) {
            var widget = new SpellSlotWidget(x + 156 - i * 18, y + 166, i);
            widget.updateState(spellData.get(i));
            spellSlots.add(widget);
            addDrawableChild(widget);
        }
    }

    @Override
    protected void handledScreenTick() {
        var spellData = client.player.getComponent(ModEntityCumponents.CASTER).getRunningSpellData();

        for (var slot : spellSlots) {
            slot.setPosition(x + 156 - slot.index * 18, y + 166);
            slot.updateState(spellData.get(slot.index));
        }
    }
}
