package dev.enjarai.trickster.mixin.accessor;

import com.mojang.serialization.MapCodec;
import net.minecraft.state.State;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(State.class)
public interface StateAccessor {
    @Accessor <O> O getOwner();
    @Accessor <S> MapCodec<S> getCodec();
}
