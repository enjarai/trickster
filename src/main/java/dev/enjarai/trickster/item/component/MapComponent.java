package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.Map.Hamt;
import dev.enjarai.trickster.spell.fragment.Map.MapFragment;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public record MapComponent(MapFragment map) {
    public static final Codec<MapComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.toCodec(MapFragment.ENDEC).fieldOf("map").forGetter(MapComponent::map)
    ).apply(instance, MapComponent::new));

    public MapComponent(Hamt<Fragment, Fragment> hamt) {
        this(new MapFragment(hamt));
    }

    public static Optional<MapFragment> getMap(ItemStack stack) {
        return Optional.of(stack)
                .filter(stack2 -> stack2.contains(ModComponents.MAP))
                .map(stack2 -> stack2.get(ModComponents.MAP))
                .map(MapComponent::map);
    }

    public static boolean setMap(ItemStack stack, Hamt<Fragment, Fragment> hamt) {
        return setMap(stack, new MapFragment(hamt));
    }

    public static boolean setMap(ItemStack stack, MapFragment map) {
        stack.set(ModComponents.MAP, new MapComponent(map));

        return true;
    }
}
