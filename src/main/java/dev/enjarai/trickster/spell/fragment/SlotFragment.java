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

    public ItemStack getStack(Trick trickSource, SpellContext ctx) throws BlunderException {
        Inventory inventory;
        if (source.isPresent()) {
            if (ctx.getWorld().getBlockEntity(source.get()) instanceof Inventory entity)
                inventory = entity;
            else throw new BlockInvalidBlunder(trickSource);
        } else {
            if (ctx.getPlayer().isPresent())
                inventory = ctx.getPlayer().get().getInventory();
            else throw new NoPlayerBlunder(trickSource);
        }


        if (slot > inventory.size())
            throw new NoSuchSlotBlunder(trickSource);

        return inventory.getStack(slot);
    }

    public void move(Trick trickSource, SpellContext ctx) throws BlunderException {
        source.ifPresent(pos ->
                ctx.useMana(trickSource, (float) (32 + (ctx.getBlockPos().toCenterPos().distanceTo(pos.toCenterPos()) * 0.8))));
    }
}