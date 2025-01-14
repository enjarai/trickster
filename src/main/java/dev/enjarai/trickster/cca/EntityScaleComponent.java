package dev.enjarai.trickster.cca;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

public class EntityScaleComponent implements CommonTickingComponent, AutoSyncedComponent {
    private final LivingEntity entity;
    private double scale = 1.0;

    public EntityScaleComponent(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        scale = tag.getDouble("scale");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putDouble("scale", scale);
    }

    @Override
    public void tick() {
        if (scale != 1.0 && !ModEntityComponents.GRACE.get(entity).isInGrace("scale")) {
            if (scale < 0.99) {
                scale += 0.01;
            } else if (scale > 1.01) {
                scale -= 0.01;
            } else {
                scale = 1.0;
            }
        }
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        ModEntityComponents.SCALE.sync(entity);
    }
}
