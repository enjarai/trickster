package dev.enjarai.trickster.spell.ward.action;

import net.minecraft.world.World;

public interface Action<T extends Target> {
    ActionType<?> type();

    T target(World world);

    float cost(World world);
}
