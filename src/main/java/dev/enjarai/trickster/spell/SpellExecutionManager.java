package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpellExecutionManager {
    public static final Codec<SpellExecutionManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpellQueue.CODEC.listOf().fieldOf("spells").forGetter((e) -> e.spells)
    ).apply(instance, SpellExecutionManager::new));

    private final List<SpellQueue> spells;

    private SpellExecutionManager(List<SpellQueue> spells) {
        this.spells = spells;
    }

    public SpellExecutionManager() {
        this(new ArrayList<>());
    }

    public Optional<Fragment> execute(SpellContext ctx, SpellPart spell) {
        return spell.runSafely(ctx);
    }

    public void queue(SpellContext ctx, SpellPart spell) {
        spells.add(new SpellQueue(ctx, spell));
    }

    public void tick() {
        for (var spell : spells) {
            spell.run();
        }
    }
}
