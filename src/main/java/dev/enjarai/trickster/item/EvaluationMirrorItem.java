package dev.enjarai.trickster.item;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.screen.ScrollAndQuillScreenHandler;
import dev.enjarai.trickster.spell.SpellPart;
import io.vavr.collection.HashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EvaluationMirrorItem extends Item {
    public EvaluationMirrorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        var otherStack = user.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
        var macros = FragmentComponent.getUserMergedMap(user, "ring", HashMap::empty);

        if (user.isSneaking()) {
            stack.set(ModComponents.FRAGMENT, new FragmentComponent(new SpellPart()));
            world.playSoundFromEntity(
                null, user, ModSounds.CAST,
                SoundCategory.PLAYERS, 1, 0.4f
            );
        }

        user.openHandledScreen(ScrollAndQuillScreenHandler.factory(
            Text.translatable("trickster.screen.mirror_of_evaluation"),
            new ScrollAndQuillScreenHandler.InitialData(
                FragmentComponent.getSpellPart(stack).orElse(new SpellPart()),
                true, hand, System.identityHashCode(stack)
            ),
            stack, otherStack
        ));

        return TypedActionResult.success(stack);
    }
}
