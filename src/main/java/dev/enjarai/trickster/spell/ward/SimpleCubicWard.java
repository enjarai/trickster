package dev.enjarai.trickster.spell.ward;

import java.util.List;

import org.joml.Vector3dc;

import dev.enjarai.trickster.EndecTomfoolery;
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
            EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("pos1", ward -> ward.pos1),
            EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("pos2", ward -> ward.pos2),
            MinecraftEndecs.ofRegistry(ActionType.REGISTRY).listOf().fieldOf("actions", ward -> ward.actions),
            SimpleCubicWard::new
    );

    private final SimpleManaPool buffer;
    private final Vector3dc pos1;
    private final Vector3dc pos2;
    private final List<ActionType<?>> actions;

    private SimpleCubicWard(SimpleManaPool buffer, Vector3dc pos1, Vector3dc pos2, List<ActionType<?>> actions) {
        this.buffer = buffer;
        this.pos1 = pos1;
        this.pos2 = pos2;
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
        return buffer.get(world) > calculateMaintenanceCost(pos1, pos2, actions);
    }

    @Override
    public boolean matchTarget(Target target) {
        Vector3dc pos;

        if (target instanceof Target.Vector vec) {
            pos = vec.vector();
        } else if (target instanceof Target.Block blo) {
            pos = blo.block().toCenterPos().toVector3d();
        } else {
            return false;
        }

        double minX = Math.min(pos1.x(), pos2.x());
        double maxX = Math.max(pos1.x(), pos2.x());
        double minY = Math.min(pos1.y(), pos2.y());
        double maxY = Math.max(pos1.y(), pos2.y());
        double minZ = Math.min(pos1.z(), pos2.z());
        double maxZ = Math.max(pos1.z(), pos2.z());

        return minX <= pos.x() && pos.x() <= maxX
                && minY <= pos.y() && pos.y() <= maxY
                && minZ <= pos.z() && pos.z() <= maxZ;
    }

    @Override
    public boolean matchAction(ActionType<?> action) {
        return actions.contains(action);
    }

    public static SimpleCubicWard tryCreate(Trick<?> trickSource, SpellContext ctx, Vector3dc pos1, Vector3dc pos2, List<ActionType<?>> actions) {
        float cost = calculateMaintenanceCost(pos1, pos2, actions);
        ctx.useMana(trickSource, cost * 1.5f);

        var pool = new SimpleManaPool(cost * 1.5f, cost * 4);
        return new SimpleCubicWard(pool, pos1, pos2, actions);
    }

    private static float calculateMaintenanceCost(Vector3dc pos1, Vector3dc pos2, List<ActionType<?>> actions) {
        double x = Math.abs(pos1.x() - pos2.x());
        double y = Math.abs(pos1.y() - pos2.y());
        double z = Math.abs(pos1.z() - pos2.z());

        return (float) (100.0 + 3.0 * x * y * z * actions.size());
    }
}
