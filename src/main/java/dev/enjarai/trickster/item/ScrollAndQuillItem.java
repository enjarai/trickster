package dev.enjarai.trickster.item;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.screen.ScrollAndQuillScreenHandler;
import io.vavr.collection.HashMap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
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

        var spell = stack.get(ModComponents.FRAGMENT);
        if (spell == null || spell.closed()) {
            return TypedActionResult.fail(stack);
        }

        if (user.isSneaking()) {
            if (world.isClient()) {
                screenOpener.accept(Text.of("trickster.screen.sign_scroll"), hand);
            }
        } else {
            if (hand == Hand.OFF_HAND
                    && ModNetworking.clientOrDefault(user,
                        Trickster.CONFIG.keys.disableOffhandScrollOpening,
                        Trickster.CONFIG.disableOffhandScrollOpening())) {
                return TypedActionResult.pass(stack);
            }
            
            user.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return Text.translatable("trickster.screen.scroll_and_quill");
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    return new ScrollAndQuillScreenHandler(
                            syncId, stack, otherStack, slot,
                            mergedMap,
                            false, true
                    );
                }
            });
        }

        return TypedActionResult.success(stack);
    }
}
