package dev.enjarai.trickster.item;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.screen.ScrollAndQuillScreenHandler;
import dev.enjarai.trickster.spell.execution.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.PlayerSpellSource;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringHelper;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class WrittenScrollItem extends Item {
    public WrittenScrollItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        var otherStack = user.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
        var slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

        var meta = stack.get(ModComponents.WRITTEN_SCROLL_META);

        if (meta != null && meta.executable()) {
            if (!world.isClient()) {
                var spell = stack.get(ModComponents.SPELL);
                if (spell != null) {
                    // TODO: run this on the playerentity cast component for multitick support, but with the custom mana pool.
                    // Probably need to implement extra tomfoolery to make that possible. (mana pool override in ExecutionState?)
                    new SpellExecutor(spell.spell(), List.of()).run(new PlayerSpellSource(SimpleManaPool.getSingleUse(meta.mana()), (ServerPlayerEntity) user));
                    ((ServerPlayerEntity) user).getServerWorld().playSoundFromEntity(
                            null, user, ModSounds.CAST, SoundCategory.PLAYERS, 1f, ModSounds.randomPitch(0.8f, 0.2f));

                    stack.decrement(1);
                    return TypedActionResult.success(stack);
                }
            }
        } else {
            user.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return Text.translatable("trickster.screen.written_scroll");
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    return new ScrollAndQuillScreenHandler(
                            syncId, playerInventory, stack, otherStack, slot,
                            false, false
                    );
                }
            });
        }

        return TypedActionResult.success(stack);
    }

    @Override
    public Text getName(ItemStack stack) {
        var meta = stack.get(ModComponents.WRITTEN_SCROLL_META);
        if (meta != null) {
            var title = meta.title();
            if (!StringHelper.isBlank(title)) {
                return Text.literal(title);
            }
        }

        return super.getName(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var meta = stack.get(ModComponents.WRITTEN_SCROLL_META);
        if (meta != null) {
            if (!StringHelper.isBlank(meta.author())) {
                tooltip.add(Text.translatable("book.byAuthor", meta.author()).formatted(Formatting.GRAY));
            }

            tooltip.add(Text.translatable("book.generation." + meta.generation()).formatted(Formatting.GRAY));

            if (meta.executable()) {
                tooltip.add(Text.translatable("trickster.scroll_executable", meta.mana()).formatted(Formatting.GRAY));
            }
        }

        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        var meta = stack.get(ModComponents.WRITTEN_SCROLL_META);
        if (meta != null && meta.executable()) {
            return true;
        }

        return super.hasGlint(stack);
    }
}
