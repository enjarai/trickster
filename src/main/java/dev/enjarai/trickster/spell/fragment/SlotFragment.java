package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.NoSuchSlotBlunder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public record SlotFragment(int slot, Optional<BlockPos> maybePosition) implements Fragment {
    public static final MapCodec<SlotFragment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("slot").forGetter(SlotFragment::slot),
            BlockPos.CODEC.optionalFieldOf("maybePosition").forGetter(SlotFragment::maybePosition)
    ).apply(instance, SlotFragment::new));

    @Override
    public FragmentType<?> type() {
        return FragmentType.SLOT;
    }

    @Override
    public Text asText() {
        return Text.literal("slot %d at %s".formatted(slot, maybePosition.isPresent()
                ? "(%d, %d, %d)".formatted(maybePosition.get().getX(), maybePosition.get().getY(), maybePosition.get().getZ())
                : "caster"));
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }

    public ItemStack getStack(Trick source, SpellContext ctx) throws BlunderException {
        Inventory inventory;
        if (maybePosition.isPresent()) {
            if (ctx.getWorld().getBlockEntity(maybePosition.get()) instanceof Inventory entity)
                inventory = entity;
            else throw new BlockInvalidBlunder(source);
        } else {
            if (ctx.getPlayer().isPresent())
                inventory = ctx.getPlayer().get().getInventory();
            else throw new NoPlayerBlunder(source);
        }


        if (slot > inventory.size())
            throw new NoSuchSlotBlunder(source);

        return inventory.getStack(slot);
    }

    public void move(Trick source, SpellContext ctx) throws BlunderException {
        if (maybePosition.isPresent())
            ctx.useMana(source, (float)(32 + (ctx.getBlockPos().toCenterPos().distanceTo(maybePosition.get().toCenterPos()) * 0.8)));
    }
}