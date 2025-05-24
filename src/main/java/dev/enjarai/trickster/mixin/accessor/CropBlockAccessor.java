package dev.enjarai.trickster.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.CropBlock;
import net.minecraft.state.property.IntProperty;

@Mixin(CropBlock.class)
public interface CropBlockAccessor {
    @Invoker
    IntProperty callGetAgeProperty();
}
