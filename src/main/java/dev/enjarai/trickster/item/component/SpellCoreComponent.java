package dev.enjarai.trickster.item.component;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import io.wispforest.accessories.endec.MinecraftEndecs;
import net.minecraft.text.Text;

public record SpellCoreComponent(SpellExecutor executor, Optional<SpellPart> spell, Optional<Text> error) {
    public static final Codec<SpellCoreComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EndecTomfoolery.toCodec(SpellExecutor.ENDEC).fieldOf("executor").forGetter(SpellCoreComponent::executor),
            EndecTomfoolery.toCodec(SpellPart.ENDEC).optionalFieldOf("spell").forGetter(SpellCoreComponent::spell),
            EndecTomfoolery.toCodec(MinecraftEndecs.TEXT).optionalFieldOf("error").forGetter(SpellCoreComponent::error)
    ).apply(instance, SpellCoreComponent::new));

    public SpellCoreComponent(SpellPart spell) {
        this(new DefaultSpellExecutor(spell, List.of()), Optional.of(spell), Optional.empty());
    }

    public SpellCoreComponent fail(Text error) {
        return new SpellCoreComponent(executor, spell, Optional.of(error));
    }
}
