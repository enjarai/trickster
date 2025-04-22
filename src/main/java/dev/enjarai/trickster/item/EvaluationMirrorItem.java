package dev.enjarai.trickster.item;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.screen.ScrollAndQuillScreenHandler;
import dev.enjarai.trickster.spell.SpellPart;
import io.vavr.collection.HashMap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
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

        user.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.translatable("trickster.screen.mirror_of_evaluation");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ScrollAndQuillScreenHandler(
                        syncId, playerInventory, stack, otherStack,
                        hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND,
                        macros,
                        true, true
                );
            }
        });

        return TypedActionResult.success(stack);
    }
}
