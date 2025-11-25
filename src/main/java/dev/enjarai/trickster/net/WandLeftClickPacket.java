package dev.enjarai.trickster.net;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.List;

public record WandLeftClickPacket(Hand hand) {
    public void handleServer(ServerAccess access) {
        PlayerEntity user = access.player();
        var stack = user.getStackInHand(hand);

        var component = stack.get(ModComponents.FRAGMENT);
        if (component != null) {
            var spell = component.value() instanceof SpellPart part ? part : new SpellPart(component.value());
            ModEntityComponents.CASTER.get(user).queueSpell(spell, List.of(BooleanFragment.FALSE));
        }
    }
}
