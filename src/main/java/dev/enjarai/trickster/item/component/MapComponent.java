package dev.enjarai.trickster.item.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.util.Hamt;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

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
        return Optional.ofNullable(user.accessoriesCapability())
            .flatMap(capability -> Optional.ofNullable(capability.getContainers().get(type)))
            .map(ringContainer -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(ringContainer.getAccessories().iterator(), Spliterator.ORDERED), false)
                    .map(Pair::getSecond)
                    .map(MapComponent::getMap)
                    .reduce(Hamt.empty(),
                        (last, current) -> current.orElse(Hamt.empty()),
                        Hamt::assocAll));
    }

    public static Hamt<Pattern, SpellPart> getUserMergedMap(PlayerEntity user, String type, Supplier<Hamt<Pattern, SpellPart>> otherwise) {
        return getUserMergedMap(user, type).orElseGet(otherwise);
    }
}
