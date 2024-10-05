package dev.enjarai.trickster.mixin.client.polymorph;

import dev.enjarai.trickster.pond.QuadrupedDuck;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(QuadrupedEntityModel.class)
public class QuadrupedEntityModelMixin implements QuadrupedDuck {
    @Shadow @Final protected ModelPart rightFrontLeg;

    @Shadow @Final protected ModelPart leftFrontLeg;

    @Override
    public ModelPart trickster$getRightFrontLeg() {
        return rightFrontLeg;
    }

    @Override
    public ModelPart trickster$getLeftFrontLeg() {
        return leftFrontLeg;
    }
}
