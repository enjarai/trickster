package dev.enjarai.trickster.cca;

import java.util.ArrayList;
import java.util.List;

import org.ladysnake.cca.api.v3.component.Component;

import dev.enjarai.trickster.spell.mana.generation.ManaHandler;
import dev.enjarai.trickster.spell.mana.generation.event.ManaEvent;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class WorldlyManaHandlerComponent implements Component {
    private static final KeyedEndec<List<ListenerEntry>> ENTRIES_ENDEC = ListenerEntry.ENDEC.listOf().keyed("entries", new ArrayList<>());

    private final List<ListenerEntry> entries = new ArrayList<>();
    private final World world;

    public WorldlyManaHandlerComponent(World world) {
        this.world = world;
    }

	@Override
	public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        entries.addAll(tag.get(ENTRIES_ENDEC));
	}

	@Override
	public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        tag.put(ENTRIES_ENDEC, entries);
	}

    public boolean handleEvent(ManaEvent event) {
        var amount = event.getMana();

        for (var entry : entries) {
            if (entry.event.fulfilledBy(event)) {
                amount = entry.handler.handleRefill((ServerWorld) world, amount);
            }
        }

        entries.removeIf(entry -> entry.event.detachedBy(event));
        return amount < event.getMana();
    }

    public void registerListener(ManaEvent event, ManaHandler handler) {
        entries.add(new ListenerEntry(event, handler));
    }

    record ListenerEntry(ManaEvent event, ManaHandler handler) {
        public static final StructEndec<ListenerEntry> ENDEC = StructEndecBuilder.of(
                ManaEvent.ENDEC.fieldOf("event", ListenerEntry::event),
                ManaHandler.ENDEC.fieldOf("handler", ListenerEntry::handler),
                ListenerEntry::new
        );
    }
}
