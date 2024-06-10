package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.endec.Endec;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class ScrollAndQuillScreenHandler extends ScreenHandler {
    private final ItemStack scrollStack;

    public final SyncedProperty<SpellPart> spell = createProperty(SpellPart.class, SpellPart.ENDEC, new SpellPart());

    public ScrollAndQuillScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }

    public ScrollAndQuillScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack scrollStack) {
        super(ModScreenHandlers.SCROLL_AND_QUILL, syncId);

        this.scrollStack = scrollStack;

        if (scrollStack != null) {
            var spell = scrollStack.get(ModComponents.SPELL);
            if (spell != null) {
                this.spell.set(spell.spell());
            }
        }

        addServerboundMessage(SpellMessage.class, SpellMessage.ENDEC, msg -> updateSpell(msg.spell()));
    }

    public void updateSpell(SpellPart spell) {
        if (scrollStack != null) {
            scrollStack.set(ModComponents.SPELL, new SpellComponent(spell));
        } else {
//            var result = SpellPart.CODEC.encodeStart(JsonOps.INSTANCE, spell).result().get();
//            Trickster.LOGGER.warn(result.toString());
            sendMessage(new SpellMessage(spell));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public record SpellMessage(SpellPart spell) {
        public static final Endec<SpellMessage> ENDEC = SpellPart.ENDEC.xmap(SpellMessage::new, SpellMessage::spell);
    }
}
