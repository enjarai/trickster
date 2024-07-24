package dev.enjarai.trickster.item;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.PlayerSpellContext;
import dev.enjarai.trickster.spell.SpellQueue;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WandItem extends Item {
    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        var slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

        if (!world.isClient()) {
            var spell = stack.get(ModComponents.SPELL);
            if (spell != null) {
                ModEntityCumponents.CASTER.get(user).queueSpell(new PlayerSpellContext((ServerPlayerEntity) user, slot), spell.spell());
                ((ServerPlayerEntity) user).getServerWorld().playSoundFromEntity(
                        null, user, ModSounds.CAST, SoundCategory.PLAYERS, 1f, ModSounds.randomPitch(0.8f, 0.2f));
            }
        }

        return TypedActionResult.success(stack);
    }
}
