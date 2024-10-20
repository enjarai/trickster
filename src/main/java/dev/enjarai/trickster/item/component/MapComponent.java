package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.util.Hamt;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Optional;
import java.util.function.Supplier;

public record MapComponent(Hamt<Pattern, SpellPart> map) {
    public static final Codec<MapComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.toCodec(Hamt.endec(Pattern.ENDEC, SpellPart.ENDEC)).fieldOf("map").forGetter(MapComponent::map)
    ).apply(instance, MapComponent::new));

    public static Optional<Hamt<Pattern, SpellPart>> getMap(ItemStack stack) {
        return Optional.ofNullable(stack)
                .filter(stack2 -> stack2.contains(ModComponents.MAP))
                .map(stack2 -> stack2.get(ModComponents.MAP))
                .map(MapComponent::map);
    }

    public static void setMap(ItemStack stack, Hamt<Pattern, SpellPart> map) {
        stack.set(ModComponents.MAP, new MapComponent(map));
    }

    public static Optional<Hamt<Pattern, SpellPart>> getUserMergedMap(PlayerEntity user, String type) {
        var capability = user.accessoriesCapability();

        if (capability == null)
            return Optional.empty();

        var ringContainer = capability.getContainers().get(type);

        if (ringContainer == null)
            return Optional.empty();

        var result = Hamt.<Pattern, SpellPart>empty();

        for (var pair : ringContainer.getAccessories()) {
            result = result.assocAll(getMap(pair.getSecond()).orElse(Hamt.empty()));
        }

        return Optional.of(result);
    }

    public static Hamt<Pattern, SpellPart> getUserMergedMap(PlayerEntity user, String type, Supplier<Hamt<Pattern, SpellPart>> otherwise) {
        return getUserMergedMap(user, type).orElseGet(otherwise);
    }
}
