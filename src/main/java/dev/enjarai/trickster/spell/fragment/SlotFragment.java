package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.*;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public record SlotFragment(int slot, Optional<BlockPos> source) implements Fragment {
    public static final MapCodec<SlotFragment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("slot").forGetter(SlotFragment::slot),
            BlockPos.CODEC.optionalFieldOf("source").forGetter(SlotFragment::source)
    ).apply(instance, SlotFragment::new));

    @Override
    public FragmentType<?> type() {
        return FragmentType.SLOT;
    }

    @Override
    public Text asText() {
        return Text.literal("slot %d at %s".formatted(slot, source.isPresent()
                ? "(%d, %d, %d)".formatted(source.get().getX(), source.get().getY(), source.get().getZ())
                : "caster"));
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
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