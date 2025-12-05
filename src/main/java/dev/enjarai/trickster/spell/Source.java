package dev.enjarai.trickster.spell;

import java.util.Optional;

import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import net.minecraft.world.World;

public interface Source {
    World getWorld();

    <T extends Component> Optional<T> getComponent(ComponentKey<T> key);
}
