package dev.enjarai.trickster.spell.ward.action;

public sealed interface Target {
    record Vector(org.joml.Vector3dc vector) implements Target {

    }

    record Block(net.minecraft.util.math.BlockPos block) implements Target {

    }

    record Entity(net.minecraft.entity.Entity entity) implements Target {

    }
}
