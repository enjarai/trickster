package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record DimensionFragment(RegistryKey<World> world) implements Fragment {
    public static StructEndec<DimensionFragment> ENDEC = StructEndecBuilder.of(
            CodecUtils.toEndec(RegistryKey.createCodec(RegistryKeys.WORLD)).fieldOf("world", DimensionFragment::world),
            DimensionFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.DIMENSION;
    }

    @Override
    public Text asText() {
        return Text.literal(world.getValue().toString());
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }

    public static DimensionFragment of(World world) {
        return new DimensionFragment(world.getRegistryKey());
    }
}
