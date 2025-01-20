package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.CollarLinkComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class LeashItem extends Item {
    public LeashItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient() && !stack.contains(ModComponents.COLLAR_LINK)) {
            var slot = SlotReference.of(entity, "necklace", 0);
            var collarStack = slot.getStack();

            if (!(entity instanceof PlayerEntity)) {
                return super.useOnEntity(stack, user, entity, hand);
            }

            var particlePos = entity.getPos().add(0, 1, 0);

            if (collarStack == null || !collarStack.isIn(ModItems.COLLARS) || collarStack.contains(ModComponents.COLLAR_LINK)) {
                ((ServerWorld) entity.getWorld()).spawnParticles(
                        ParticleTypes.SMOKE, particlePos.x, particlePos.y, particlePos.z,
                        10, 0.3, 0.3, 0.3, 0
                );

                user.sendMessage(Text.translatable("trickster.message.leash.invalid_collar", entity.getName()), true);
                return ActionResult.CONSUME;
            }

            if (!entity.isSneaking()) {
                ((ServerWorld) entity.getWorld()).spawnParticles(
                        ParticleTypes.SMOKE, particlePos.x, particlePos.y, particlePos.z,
                        10, 0.3, 0.3, 0.3, 0
                );

                user.sendMessage(Text.translatable("trickster.message.leash.not_sneaking", entity.getName()), true);
                return ActionResult.CONSUME;
            }

            var component = new CollarLinkComponent(UUID.randomUUID());
            stack.set(ModComponents.COLLAR_LINK, component);
            collarStack = collarStack.copy();
            collarStack.set(ModComponents.COLLAR_LINK, component);
            slot.setStack(collarStack);

            entity.getWorld().playSoundFromEntity(
                    null, entity, SoundEvents.BLOCK_COMPARATOR_CLICK,
                    SoundCategory.PLAYERS, 1, 1.6f
            );
            ((ServerWorld) entity.getWorld()).spawnParticles(
                    ParticleTypes.HEART, particlePos.x, particlePos.y, particlePos.z,
                    10, 0.3, 0.3, 0.3, 0
            );

            user.sendMessage(Text.translatable("trickster.message.leash.success", entity.getName()), true);
            return ActionResult.SUCCESS;
        }

        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        var collarComponent = stack.get(ModComponents.COLLAR_LINK);

        if (collarComponent != null) {
            if (!world.isClient()) {
                var player = world.getPlayers().stream().filter(p -> {
                    var collarStack = SlotReference.of(p, "necklace", 0).getStack();
                    if (collarStack == null) return false;

                    var component = collarStack.get(ModComponents.COLLAR_LINK);
                    if (component == null) return false;

                    return component.uuid().equals(collarComponent.uuid());
                }).findFirst().orElse(null);

                if (player != null) {
                    var component = stack.get(ModComponents.FRAGMENT);
                    if (component != null) {
                        var spell = component.value() instanceof SpellPart part ? part : new SpellPart(component.value());
                        ModEntityComponents.CASTER.get(player).queueSpell(spell, List.of());
                        ModEntityComponents.CASTER.get(user).playCastSound(0.8f, 0.1f);
                    }
                } else {
                    user.sendMessage(Text.translatable("trickster.message.leash.not_online"), true);
                }
            }

            return TypedActionResult.success(stack);
        }

        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (!stack.contains(ModComponents.COLLAR_LINK)) {
            tooltip.add(Text.translatable("trickster.tooltip.unlinked").withColor(0x775577));
        }

        super.appendTooltip(stack, context, tooltip, type);
    }
}
