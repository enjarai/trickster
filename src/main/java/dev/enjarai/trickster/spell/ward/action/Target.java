package dev.enjarai.trickster.spell.ward.action;

import org.joml.Vector3dc;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public sealed interface Target {
    record Vec(Vector3dc vec) implements Target {

    }

    record Blo(BlockPos blo) implements Target {

    }

    record Ent(Entity ent) implements Target {

    }
}
