package dev.enjarai.trickster.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;

public class PlayerAnimationComponent implements ClientTickingComponent {
    public float hatTakeyNess;
    public float prevHatTakeyNess;

    public PlayerAnimationComponent(PlayerEntity player) {

    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {

    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {

    }

    @Override
    public void clientTick() {
        prevHatTakeyNess = hatTakeyNess;
        if (hatTakeyNess > 0.1) {
            hatTakeyNess -= 0.3f;
        } else {
            hatTakeyNess = 0;
        }
    }
}
