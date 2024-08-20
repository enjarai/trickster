package dev.enjarai.trickster.net;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SelectedSlotComponent;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.item.component.WrittenScrollMetaComponent;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Optional;

public class ModNetworking {
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(Trickster.id("main"));

    public static void register() {
        CHANNEL.registerServerbound(MladyPacket.class, MladyPacket::handleServer);
        CHANNEL.registerServerbound(ScrollInGamePacket.class, ScrollInGamePacket::handleServer);
        CHANNEL.registerServerbound(IsEditingScrollPacket.class, IsEditingScrollPacket::handleServer);
        CHANNEL.registerServerbound(SignScrollPacket.class, SignScrollPacket::handleServer);
        CHANNEL.registerServerbound(KillSpellPacket.class, KillSpellPacket::handleServer);
        CHANNEL.registerServerbound(ClipBoardSpellResponsePacket.class, ClipBoardSpellResponsePacket.ENDEC, ClipBoardSpellResponsePacket::handleServer);
        CHANNEL.registerServerbound(LoadExampleSpellPacket.class, LoadExampleSpellPacket.ENDEC, LoadExampleSpellPacket::handleServer);

        CHANNEL.registerClientboundDeferred(RebuildChunkPacket.class);
        CHANNEL.registerClientboundDeferred(GrabClipboardSpellPacket.class);
    }
}
