package dev.enjarai.trickster.item;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.screen.ScrollAndQuillScreenHandler;
import dev.enjarai.trickster.spell.execution.SpellQueueResult;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringHelper;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

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
                    var result = ModEntityCumponents.CASTER.get(user).queueSpellAndCast(spell.spell(), List.of(), Optional.of(SimpleManaPool.getSingleUse(meta.mana())));

                    if (result.type() != SpellQueueResult.Type.NOT_QUEUED && result.state().hasUsedMana())
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
