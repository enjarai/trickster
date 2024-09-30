package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public record SlotFragment(int slot, Optional<BlockPos> source) implements Fragment {
    public static final StructEndec<SlotFragment> ENDEC = StructEndecBuilder.of(
            Endec.INT.fieldOf("slot", SlotFragment::slot),
            EndecTomfoolery.safeOptionalOf(EndecTomfoolery.ALWAYS_READABLE_BLOCK_POS).fieldOf("source", SlotFragment::source),
            SlotFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.SLOT;
    }

    @Override
    public Text asText() {
        return Text.literal("slot %d at %s".formatted(slot,
                source.map(blockPos -> "(%d, %d, %d)".formatted(blockPos.getX(), blockPos.getY(), blockPos.getZ())).orElse("caster")));
    }

    @Override
    public boolean asBoolean() {
        return true;
    }

    public ItemStack move(Trick trickSource, SpellContext ctx) throws BlunderException {
        return move(trickSource, ctx, 1);
    }

    public ItemStack move(Trick trickSource, SpellContext ctx, int amount) {
        return move(trickSource, ctx, amount, ctx.source().getBlockPos());
    }

    public ItemStack move(Trick trickSource, SpellContext ctx, int amount, BlockPos pos) throws BlunderException {
        var stack = getStack(trickSource, ctx);

        if (stack.getCount() < amount)
            throw new MissingItemBlunder(trickSource);

        var result = stack.copyWithCount(amount);
        source.ifPresent(sourcePos -> ctx.useMana(trickSource, (float) (amount * (32 + (pos.toCenterPos().distanceTo(sourcePos.toCenterPos()) * 0.8)))));
        stack.decrement(amount);
        return result;
    }

    /**
     * Instead of taking items from the slot, directly reference the stored stack to modify it.
     */
    public ItemStack reference(Trick trickSource, SpellContext ctx) {
        var stack = getStack(trickSource, ctx);

        if (stack.isEmpty())
            throw new MissingItemBlunder(trickSource);

        return stack;
    }

    public Item getItem(Trick trickSource, SpellContext ctx) throws BlunderException {
        return getStack(trickSource, ctx).getItem();
    }

    private ItemStack getStack(Trick trickSource, SpellContext ctx) throws BlunderException {
        Inventory inventory;
        if (source.isPresent()) {
            if (ctx.source().getWorld().getBlockEntity(source.get()) instanceof Inventory entity)
                inventory = entity;
            else throw new BlockInvalidBlunder(trickSource);
        } else {
            if (ctx.source().getPlayer().isPresent())
                inventory = ctx.source().getPlayer().get().getInventory();
            else throw new NoPlayerBlunder(trickSource);
        }


        if (slot > inventory.size())
            throw new NoSuchSlotBlunder(trickSource);

        return inventory.getStack(slot);
    }
}