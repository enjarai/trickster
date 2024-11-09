package dev.enjarai.trickster.render;

import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.util.ImGoingToStabWhoeverInventedTime;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.text.SimpleDateFormat;
import java.util.List;

public class MerlinKeeperTracker {
    private final Int2ObjectMap<MerlinUsage> stackMap = new Int2ObjectOpenHashMap<>();
    private final int tickSpan;

    public MerlinKeeperTracker(int tickSpan) {
        this.tickSpan = tickSpan;
    }

    public void tick(MinecraftClient client) {
        if (client.player != null && client.player.age % tickSpan == 0) {
            var inventory = client.player.getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                var stack = inventory.getStack(i);

                if (stack.get(ModComponents.MANA) instanceof ManaComponent component) {
                    var usage = stackMap.computeIfAbsent(i, j -> new MerlinUsage(component.pool().get()));
                    usage.update(component.pool().get());
                }
            }

            stackMap.keySet().removeIf(j -> !inventory.getStack(j).contains(ModComponents.MANA));
        }
    }

    public float getUsage(ItemStack stack) {
        if (MinecraftClient.getInstance().player != null) {
            var inventory = MinecraftClient.getInstance().player.getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                var searchStack = inventory.getStack(i);
                if (searchStack == stack && stackMap.get(i) instanceof MerlinUsage usage) {
                    return usage.getUsage();
                }
            }
        }
        return 0;
    }

    public void appendKnotTooltip(ItemStack stack, List<Text> tooltip) {
        var usage = getUsage(stack);

        tooltip.add(Text.literal("Current draw: %.2f kM".formatted(usage)).styled(s -> s.withColor(0xaaaabb)));
        if (usage != 0 && stack.get(ModComponents.MANA) instanceof ManaComponent component) {
            long timeUntilDrained = (long) (component.pool().get() / usage * 50);
            tooltip.add(Text.literal("Time until drained: %s".formatted(
                    ImGoingToStabWhoeverInventedTime.howLongIsThisQuestionMark(timeUntilDrained)))
                    .styled(s -> s.withColor(0xaaaabb)));
        }
    }

    public class MerlinUsage {
        public float prevLastMerlins;
        public float lastMerlins;
        public float latestMerlins;

        public MerlinUsage(float latest) {
            prevLastMerlins = latest;
            lastMerlins = latest;
            latestMerlins = latest;
        }

        public void update(float newMerlins) {
            prevLastMerlins = lastMerlins;
            lastMerlins = latestMerlins;
            latestMerlins = newMerlins;
        }

        public float getUsage() {
            if (MinecraftClient.getInstance().player == null) {
                return 0;
            }

            return MathHelper.lerp(
                    (float) (MinecraftClient.getInstance().player.age % tickSpan) / tickSpan,
                    prevLastMerlins - lastMerlins, lastMerlins - latestMerlins
            ) / tickSpan;
        }
    }
}
