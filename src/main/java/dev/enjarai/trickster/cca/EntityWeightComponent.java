package dev.enjarai.trickster.cca;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

public class EntityWeightComponent implements CommonTickingComponent, AutoSyncedComponent {
    private final LivingEntity entity;
    private double weight = 1.0;

    public EntityWeightComponent(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        setWeight(tag.getDouble("weight"));
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putDouble("weight", getWeight());
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == entity;
    }

    @Override
    public void tick() {
        var weight = getWeight();

        if (weight != 1.0 && !ModEntityComponents.GRACE.get(entity).isInGrace("weight")) {
            if (weight < 0.99) {
                setWeight(weight + 0.01);
            } else {
                setWeight(1.0);
            }
        }
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
        ModEntityComponents.WEIGHT.sync(entity);
    }
}
