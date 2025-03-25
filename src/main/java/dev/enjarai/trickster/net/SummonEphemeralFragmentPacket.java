package dev.enjarai.trickster.net;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.item.ItemStack;

public record SummonEphemeralFragmentPacket(Fragment fragment) {
    public static final StructEndec<SummonEphemeralFragmentPacket> ENDEC = StructEndecBuilder.of(
            Fragment.ENDEC.fieldOf("fragment", SummonEphemeralFragmentPacket::fragment),
            SummonEphemeralFragmentPacket::new
    );

    public void handleServer(ServerAccess access) {
        var stack = new ItemStack(ModItems.EPHEMERAL_FRAGMENT);
        stack.set(ModComponents.FRAGMENT, new FragmentComponent(fragment, true));
        access.player().getInventory().offerOrDrop(stack);
        //TODO: summon item at player position with fancy particles and all that
    }
}
