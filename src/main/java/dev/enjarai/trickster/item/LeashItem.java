package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.CollarLinkComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
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

public class LeashItem extends Item implements LeftClickItem {
    public LeashItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient() && !stack.contains(ModComponents.COLLAR_LINK)) {
            var slot = SlotReference.of(entity, "collar", 0);
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
            user.getStackInHand(hand).set(ModComponents.COLLAR_LINK, component);
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
        return use(world, user, hand, true);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand, boolean rightClick) {
        var stack = user.getStackInHand(hand);
        var linkComponent = stack.get(ModComponents.COLLAR_LINK);

        if (linkComponent != null) {
            if (!world.isClient()) {
                var players = world.getPlayers().stream().filter(p -> {
                    var collarStack = SlotReference.of(p, "collar", 0).getStack();
                    if (collarStack == null) return false;

                    var component = collarStack.get(ModComponents.COLLAR_LINK);
                    if (component == null) return false;

                    return component.uuid().equals(linkComponent.uuid());
                }).toList();

                if (players.isEmpty()) {
                    user.sendMessage(Text.translatable("trickster.message.leash.not_online"), true);
                } else {
                    if (user.isSneaking()) {
                        for (var player : players) {
                            ModEntityComponents.CASTER.get(player).killCollar();
                            ModEntityComponents.CASTER.get(user).playCastSound(0.5f, 0.1f);
                            ModEntityComponents.CASTER.get(player).playCastSound(0.5f, 0.1f);
                        }
                    } else {
                        var fragmentComponent = stack.get(ModComponents.FRAGMENT);
                        if (fragmentComponent != null) {
                            for (var player : players) {
                                var spell = fragmentComponent.value() instanceof SpellPart part ? part : new SpellPart(fragmentComponent.value());
                                ModEntityComponents.CASTER.get(player).queueCollarSpell(spell, List.of(BooleanFragment.of(rightClick), EntityFragment.from(user)));
                                ModEntityComponents.CASTER.get(user).playCastSound(0.8f, 0.1f);
                            }
                        }
                    }
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
