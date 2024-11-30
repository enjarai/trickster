package dev.enjarai.trickster.cca;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class MessageHandlerComponent implements ServerTickingComponent {
    private final List<Listener> listeners = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();
    private final Scoreboard provider;
    private final Optional<MinecraftServer> server;

    public MessageHandlerComponent(Scoreboard provider, @Nullable MinecraftServer server) {
        this.provider = provider;
        this.server = Optional.ofNullable(server);
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
    }

    @Override
    public void serverTick() {
        for (var listener : listeners) {
            var result = new ArrayList<Fragment>();

            for (var message : messages) {
                if (message.key.match(listener.key)) {
                    result.add(message.value);
                }
            }

            if (result.size() > 0) {
                listener.consumer.accept(new ListFragment(result));
            }
        }

        listeners.clear();
        messages.clear();
    }

    public void await(Key key, Consumer<ListFragment> consumer) {
        listeners.add(new Listener(key, consumer));
    }

    public void send(Key key, Fragment value) {
        messages.add(new Message(key, value.applyEphemeral()));
    }

    private static record Message(Key key, Fragment value) {
    }

    private static record Listener(Key key, Consumer<ListFragment> consumer) {
    }

    public static interface Key {
        boolean match(Key other);
        
        public static record Channel(UUID uuid) implements Key {
            @Override
            public boolean match(Key other) {
                return other instanceof Channel channel
                    && channel.uuid.equals(uuid);
            }
        }

        public static record Broadcast(RegistryKey<World> world, Vector3d pos, double extraRange) implements Key {
            @Override
            public boolean match(Key other) {
                return other instanceof Broadcast broadcast
                    && broadcast.world.equals(world)
                    && broadcast.pos.distanceSquared(pos) <= (256 + Math.pow(broadcast.extraRange, 2) + Math.pow(extraRange, 2));
            }
        }
    }
}
