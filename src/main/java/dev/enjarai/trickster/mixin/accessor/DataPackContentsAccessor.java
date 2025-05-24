package dev.enjarai.trickster.mixin.accessor;

import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DataPackContents.class)
public interface DataPackContentsAccessor {
    @Accessor
    TagManagerLoader getRegistryTagManager();
}
