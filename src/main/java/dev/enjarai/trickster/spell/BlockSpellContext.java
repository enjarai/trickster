package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockSpellContext extends SpellContext {
    public static final MapCodec<BlockSpellContext> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(Codec.list(Fragment.CODEC.get().codec())).fieldOf("part_glyph_stack").forGetter(ctx -> new ArrayList<>(ctx.partGlyphStack())),
            Codec.BOOL.fieldOf("destructive").forGetter(SpellContext::isDestructive),
            Codec.BOOL.fieldOf("has_affected_world").forGetter(SpellContext::hasAffectedWorld),
            Codec.list(Codec.INT).fieldOf("stacktrace").forGetter(ctx -> new ArrayList<>(ctx.stacktrace())),
            ManaPool.CODEC.get().fieldOf("mana_pool").forGetter(ctx -> ctx.manaPool),
            Codec.list(ManaLink.CODEC).fieldOf("mana_links").forGetter(ctx -> ctx.manaLinks),
            World.CODEC.fieldOf("world").forGetter(ctx -> ctx.world.getRegistryKey()),
            BlockPos.CODEC.fieldOf("block_pos").forGetter(ctx -> ctx.pos)
    ).apply(instance, (partGlyphStack, destructive, hasAffectedWorld, stacktrace, manaPool, manaLinks, world, blockPos) -> new BlockSpellContext(partGlyphStack, destructive, hasAffectedWorld, stacktrace, manaPool, manaLinks, Objects.requireNonNull(Trickster.getCurrentServer().getWorld(world)), blockPos)));

    public final ServerWorld world;
    public final BlockPos pos;
    public final SpellCircleBlockEntity blockEntity;

    private BlockSpellContext(List<List<Fragment>> partGlyphStack, boolean destructive, boolean hasAffectedWorld, List<Integer> stacktrace, ManaPool manaPool, List<ManaLink> manaLinks, ServerWorld world, BlockPos pos) {
        super(partGlyphStack, destructive, hasAffectedWorld, stacktrace, manaPool, manaLinks);
        this.world = world;
        this.pos = pos;
        this.blockEntity = (SpellCircleBlockEntity) world.getBlockEntity(pos);
    }

    public BlockSpellContext(ServerWorld world, BlockPos pos, SpellCircleBlockEntity blockEntity) {
        super(blockEntity.manaPool);
        this.world = world;
        this.pos = pos;
        this.blockEntity = blockEntity;
    }

    @Override
    public Vector3d getPos() {
        return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    public ServerWorld getWorld() {
        return world;
    }

    @Override
    public Fragment getCrowMind() {
        return blockEntity.crowMind.fragment();
    }

    @Override
    public void setCrowMind(Fragment fragment) {
        blockEntity.crowMind = new CrowMind(fragment);
        blockEntity.markDirty();
    }

    @Override
    public SpellContextType<?> type() {
        return SpellContextType.BLOCK;
    }

    @Override
    public SpellContext delayed(List<Fragment> arguments) {
        var ctx = new BlockSpellContext(world, pos, blockEntity);
        ctx.manaLinks.addAll(manaLinks);
        ctx.pushPartGlyph(arguments);
        return ctx;
    }

    @Override
    public void addManaLink(Trick source, LivingEntity target, float limit) {
        addManaLink(source, new ManaLink(manaPool, target, manaPool.getMax() / 20, limit));
    }
}
