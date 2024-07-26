package dev.enjarai.trickster.item;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class WandItem extends Item {
    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            var spell = stack.get(ModComponents.SPELL);
            if (spell != null) {
                user.getComponent(ModEntityCumponents.CASTER).queue(spell.spell(), List.of());
                ((ServerPlayerEntity) user).getServerWorld().playSoundFromEntity(
                        null, user, ModSounds.CAST, SoundCategory.PLAYERS, 1f, ModSounds.randomPitch(0.8f, 0.2f));
            }
        }

        return TypedActionResult.success(stack);
    }
}
