package dev.enjarai.trickster.spell.ward;

import java.util.List;

import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.ward.action.ActionType;
import dev.enjarai.trickster.spell.ward.action.Target;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.world.World;

public class SimpleCubicWard implements Ward {
    public static final StructEndec<SimpleCubicWard> ENDEC = StructEndecBuilder.of(
            SimpleManaPool.ENDEC.fieldOf("buffer", ward -> ward.buffer),
            CodecUtils.toEndec(BlockBox.CODEC).fieldOf("area", ward -> ward.area),
            MinecraftEndecs.ofRegistry(ActionType.REGISTRY).listOf().fieldOf("actions", ward -> ward.actions),
            SimpleCubicWard::new
    );

    private final SimpleManaPool buffer;
    private final BlockBox area;
    private final List<ActionType<?>> actions;

    private SimpleCubicWard(SimpleManaPool buffer, BlockBox area, List<ActionType<?>> actions) {
        this.buffer = buffer;
        this.area = area;
        this.actions = actions;
    }

    @Override
    public WardType<?> type() {
        return WardType.SIMPLE_CUBIC;
    }

    @Override
    public void tick(World world) {
        buffer.use(0.05f, world);
    }

    @Override
    public void drain(World world, float amount) {
        buffer.use(amount, world);
    }

    @Override
    public boolean shouldLive(World world) {
        return buffer.get(world) > calculateMaintenanceCost(area, actions);
    }

    @Override
    public boolean matchTarget(Target target) {
        BlockPos pos;

        if (target instanceof Target.Vector vec) {
            var v = vec.vector();
            pos = BlockPos.ofFloored(v.x(), v.y(), v.z());
        } else if (target instanceof Target.Block blo) {
            pos = blo.block();
        } else {
            return false;
        }

        return area.contains(pos);
    }

    @Override
    public boolean matchAction(ActionType<?> action) {
        return actions.contains(action);
    }

    public static SimpleCubicWard tryCreate(Trick<?> trickSource, SpellContext ctx, BlockBox area, List<ActionType<?>> actions) {
        float cost = calculateMaintenanceCost(area, actions);
        ctx.useMana(trickSource, cost * 1.5f);

        var pool = new SimpleManaPool(cost * 1.5f, cost * 4);
        return new SimpleCubicWard(pool, area, actions);
    }

    private static float calculateMaintenanceCost(BlockBox area, List<ActionType<?>> actions) {
        return (float) (100.0 + 3.0 * area.getBlockCountX() * area.getBlockCountY() * area.getBlockCountZ() * actions.size());
    }
}
