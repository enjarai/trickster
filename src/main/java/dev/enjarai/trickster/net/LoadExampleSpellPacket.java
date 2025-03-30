package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public record LoadExampleSpellPacket(SpellPart spell) {
    public static final StructEndec<LoadExampleSpellPacket> ENDEC = StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("spell", LoadExampleSpellPacket::spell),
            LoadExampleSpellPacket::new
    );

    public void handleServer(ServerAccess access) {
        for (var hand : Hand.values()) {
            var stack = access.player().getStackInHand(hand);
            if (stack.isIn(ModItems.SCROLL_AND_QUILLS)) {
                var component = stack.get(ModComponents.FRAGMENT);
                if (component != null && component.immutable()) {
                    access.player().sendMessage(Text.translatable("trickster.message.immutable_scroll"), true);
                    return;
                }

                stack.set(ModComponents.FRAGMENT, new FragmentComponent(spell));
                access.player().sendMessage(Text.translatable("trickster.message.loaded_example"), true);
                return;
            }
        }

        access.player().sendMessage(Text.translatable("trickster.message.missing_scroll"), true);
    }
}
