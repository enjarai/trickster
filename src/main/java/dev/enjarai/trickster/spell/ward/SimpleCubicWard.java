package dev.enjarai.trickster.spell.ward;

import org.joml.Vector3dc;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.server.world.ServerWorld;

public class SimpleCubicWard implements Ward {
    public static final StructEndec<SimpleCubicWard> ENDEC = StructEndecBuilder.of(
            SimpleManaPool.ENDEC.fieldOf("buffer", ward -> ward.buffer),
            EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("pos1", ward -> ward.pos1),
            EndecTomfoolery.VECTOR_3D_ENDEC.fieldOf("pos2", ward -> ward.pos2),
            SimpleCubicWard::new
    );

    private final SimpleManaPool buffer;
    private final Vector3dc pos1;
    private final Vector3dc pos2;

    private SimpleCubicWard(SimpleManaPool buffer, Vector3dc pos1, Vector3dc pos2) {
        this.buffer = buffer;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public WardType<?> type() {
        return WardType.SIMPLE_CUBIC;
    }

    @Override
    public void tick(ServerWorld world) {
        buffer.use(1, world);
    }

    @Override
    public boolean shouldLive(ServerWorld world) {
        return buffer.get(world) > calculateMaintenanceCost(pos1, pos2);
    }

    @Override
    public boolean matchPos(Vector3dc pos) {
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

    public static SimpleCubicWard tryCreate(Trick<?> trickSource, SpellContext ctx, Vector3dc pos1, Vector3dc pos2) {
        float cost = calculateMaintenanceCost(pos1, pos2);
        ctx.useMana(trickSource, cost * 1.5f);

        var pool = new SimpleManaPool(cost * 1.5f, cost * 4);
        return new SimpleCubicWard(pool, pos1, pos2);
    }

    private static float calculateMaintenanceCost(Vector3dc pos1, Vector3dc pos2) {
        double x = Math.abs(pos1.x() - pos2.x());
        double y = Math.abs(pos1.y() - pos2.y());
        double z = Math.abs(pos1.z() - pos2.z());

        return (float) (x * y * z);
    }
}
