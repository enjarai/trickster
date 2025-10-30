package dev.enjarai.trickster.cca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.joml.Vector3dc;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import dev.enjarai.trickster.spell.ward.Ward;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class WardManagerComponent implements ServerTickingComponent {
    private final World world;
    private final HashMap<UUID, Ward> wards = new HashMap<>();

    public WardManagerComponent(World world) {
        this.world = world;
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readFromNbt'");
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeToNbt'");
    }

    @Override
    public void serverTick() {
        var world = (ServerWorld) this.world;
        var toRemove = new ArrayList<UUID>();

        for (var kv : wards.entrySet()) {
            var ward = kv.getValue();
            ward.tick(world);

            if (!ward.shouldLive(world)) {
                toRemove.add(kv.getKey());
            }
        }

        for (var uuid : toRemove) {
            wards.remove(uuid);
        }
    }

    public Optional<Ward> get(UUID uuid) {
        return Optional.ofNullable(wards.get(uuid));
    }

    public ArrayList<Ward> get(Vector3dc pos) {
        var result = new ArrayList<Ward>();

        for (var ward : wards.values()) {
            if (ward.matchPos(pos)) {
                result.add(ward);
            }
        }

        return result;
    }
}
