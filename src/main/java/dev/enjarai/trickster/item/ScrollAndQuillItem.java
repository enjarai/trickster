package dev.enjarai.trickster.item;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.screen.ScrollAndQuillScreenHandler;
import dev.enjarai.trickster.spell.SpellPart;
import io.vavr.collection.HashMap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class ScrollAndQuillItem extends Item {
    public static BiConsumer<Text, Hand> screenOpener;

    public final WrittenScrollItem signedVersion;

    public ScrollAndQuillItem(Settings settings, WrittenScrollItem signedVersion) {
        super(settings);
        this.signedVersion = signedVersion;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        var otherStack = user.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
        var slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        var mergedMap = FragmentComponent.getUserMergedMap(user, "ring", HashMap::empty);

        if (
            hand == Hand.OFF_HAND
                && ModNetworking.clientOrDefault(
                    user,
                    Trickster.CONFIG.keys.disableOffhandScrollOpening,
                    Trickster.CONFIG.disableOffhandScrollOpening()
                )
        ) {
            return TypedActionResult.pass(stack);
        }

        var spell = stack.get(ModComponents.FRAGMENT);
        if (spell == null || spell.closed()) {
            return TypedActionResult.fail(stack);
        }

        if (user.isSneaking()) {
            if (world.isClient()) {
                screenOpener.accept(Text.of("trickster.screen.sign_scroll"), hand);
            }
        } else {
            user.openHandledScreen(ScrollAndQuillScreenHandler.factory(
                Text.translatable("trickster.screen.scroll_and_quill"),
                new ScrollAndQuillScreenHandler.InitialData(
                    FragmentComponent.getSpellPart(stack).orElse(new SpellPart()),
                    true, hand, System.identityHashCode(stack)
                ),
                stack, otherStack
            ));
        }

        return TypedActionResult.success(stack);
    }
}
