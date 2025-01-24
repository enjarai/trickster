package dev.enjarai.trickster.render;

import dev.enjarai.trickster.ClientUtils;
import dev.enjarai.trickster.Trickster.TooltipAppender;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.util.ImGoingToStabWhoeverInventedTime;
import dev.enjarai.trickster.util.Unit;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class MerlinKeeperTracker implements TooltipAppender {
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
                    var usage = stackMap.computeIfAbsent(i, j -> new MerlinUsage(component.pool().get(client.world)));
                    usage.update(component.pool().get(client.world));
                }
            }

            stackMap.keySet().removeIf(j -> !inventory.getStack(j).contains(ModComponents.MANA));
        }
    }

    @SuppressWarnings("resource")
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

    @SuppressWarnings("resource")
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var world = MinecraftClient.getInstance().world;
        var usage = getUsage(stack);

        if (usage == 0) {
            tooltip.add(Text.translatable(Trickster.MOD_ID + ".merlin.idling").styled(s -> s.withColor(0xaaaabb)));
        } else {
            var usageUnit = Unit.getMerlinUnit(usage);

            tooltip.add(
                    (usage > 0
                            ? Text.translatable(Trickster.MOD_ID + ".merlin.draining", "%.2f ".formatted(usageUnit.correct(usage)) + usageUnit.shortName())
                            : Text.translatable(Trickster.MOD_ID + ".merlin.filling", "%.2f ".formatted(usageUnit.correct(-usage)) + usageUnit.shortName()))
                            .styled(s -> s.withColor(0xaaaabb))
            );
        }

        if (stack.get(ModComponents.MANA) instanceof ManaComponent component && world != null) {
            var pool = component.pool();

            ClientUtils.trySubscribe(component);

            if (usage != 0) {
                long timeUntilDrained = (long) (pool.get(world) / usage * 50);
                var str = timeUntilDrained >= 0 ? "Time until drained: %s" : "Time until charged: %s";

                if (timeUntilDrained < 0) {
                    timeUntilDrained = (long) ((pool.get(world) - pool.getMax(world)) / usage) * 50;
                }

                tooltip.add(
                        Text.literal(
                                str.formatted(
                                        ImGoingToStabWhoeverInventedTime.howLongIsThisQuestionMark(timeUntilDrained)
                                )
                        )
                                .styled(s -> s.withColor(0xaaaabb))
                );
            }

            if (type.isAdvanced()) {
                float current = pool.get(world);
                float max = pool.getMax(world);
                var currentUnit = Unit.getGandalfUnit(current);
                var maxUnit = Unit.getGandalfUnit(max);

                tooltip.add(
                        Text.translatable(
                                Trickster.MOD_ID + ".gandalf.stored",
                                ("%.1f " + currentUnit.shortName() + " / %.1f " + maxUnit.shortName())
                                        .formatted(currentUnit.correct(current), maxUnit.correct(max))
                        )
                                .styled(s -> s.withColor(0xaaaabb))
                );

                if (pool instanceof SharedManaPool shared && MinecraftClient.getInstance().world != null) {
                    tooltip.add(
                            Text
                                    .literal(shared.uuid().toString())
                                    .setStyle(Style.EMPTY.withFormatting(Formatting.LIGHT_PURPLE))
                    );
                }
            }
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

        @SuppressWarnings("resource")
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
