package dev.enjarai.trickster.cca;

import java.util.Optional;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class DisplacementComponent implements ServerTickingComponent {
    private static final KeyedEndec<Optional<Vector3d>> OFFSET_ENDEC = EndecTomfoolery.<Double, Vector3d>vectorEndec(Endec.DOUBLE, Vector3d::new, Vector3dc::x, Vector3dc::y, Vector3dc::z)
            .optionalOf()
            .keyed("offset", Optional.empty());

    private final Entity entity;
    private Optional<Vector3d> offset = Optional.empty();

    public DisplacementComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        offset = tag.get(OFFSET_ENDEC);
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        tag.put(OFFSET_ENDEC, offset);
    }

    @Override
    public void serverTick() {
        if (offset.isPresent() && !ModEntityComponents.GRACE.get(entity).isInGrace("displacement")) {
            //TODO: teleportttt

            offset = Optional.empty();
        }
    }

    public void modify(Vector3dc vector) {
        if (offset.isPresent()) {
            offset.get().add(vector);
        } else {
            offset = Optional.of(new Vector3d(vector));
        }

        ModEntityComponents.GRACE.get(entity).triggerGrace("displacement", 40);
    }

    public void clear() {
        offset = Optional.empty();
    }
}
