package dev.enjarai.trickster;

import dev.enjarai.trickster.aldayim.backend.ImGuiDialogueBackend;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.entity.ModEntities;
import dev.enjarai.trickster.item.KnotItem;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.render.entity.LevitatingBlockEntityRenderer;
import dev.enjarai.trickster.render.fleck.FleckRenderer;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.ScrollAndQuillItem;
import dev.enjarai.trickster.net.IsEditingScrollPacket;
import dev.enjarai.trickster.net.ModClientNetworking;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.particle.ProtectedBlockParticle;
import dev.enjarai.trickster.particle.SpellParticle;
import dev.enjarai.trickster.render.*;
import dev.enjarai.trickster.render.fragment.FragmentRenderer;
import dev.enjarai.trickster.screen.ModHandledScreens;
import dev.enjarai.trickster.screen.ScrollAndQuillScreen;
import dev.enjarai.trickster.screen.SignScrollScreen;
import dev.enjarai.trickster.screen.owo.GlyphComponent;
import dev.enjarai.trickster.screen.owo.ItemTagComponent;
import dev.enjarai.trickster.screen.owo.SpellPreviewComponent;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.math.MathHelper;
import nl.enjarai.cicada.api.imgui.ImGuiThings;

public class TricksterClient implements ClientModInitializer {
    public static final MerlinKeeperTracker merlinKeeperTracker = new MerlinKeeperTracker(5);
    public static final ImGuiDialogueBackend dialogueBackend = new ImGuiDialogueBackend();

    @Override
    public void onInitializeClient() {
        ScrollAndQuillItem.screenOpener = (text, hand) -> {
            MinecraftClient.getInstance().setScreen(new SignScrollScreen(text, hand));
        };

        FleckRenderer.register();
        FragmentRenderer.register();

        ModHandledScreens.register();
        ModKeyBindings.register();
        ModClientNetworking.register();

        BlockEntityRendererFactories.register(ModBlocks.SPELL_CONSTRUCT_ENTITY, SpellConstructBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(
                ModBlocks.MODULAR_SPELL_CONSTRUCT_ENTITY,
                ModularSpellConstructBlockEntityRenderer::new
        );
        BlockEntityRendererFactories.register(ModBlocks.SCROLL_SHELF_ENTITY, ScrollShelfBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.CHARGING_ARRAY_ENTITY, ChargingArrayBlockEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.LEVITATING_BLOCK, LevitatingBlockEntityRenderer::new);

        UIParsing.registerFactory(Trickster.id("glyph"), GlyphComponent::parseTrick);
        UIParsing.registerFactory(Trickster.id("pattern"), GlyphComponent::parseList);
        UIParsing.registerFactory(Trickster.id("spell-preview"), SpellPreviewComponent::parse);
        UIParsing.registerFactory(Trickster.id("item-tag"), ItemTagComponent::parse);

        ParticleFactoryRegistry.getInstance().register(
                ModParticles.PROTECTED_BLOCK,
                ProtectedBlockParticle.Factory::new
        );
        ParticleFactoryRegistry.getInstance().register(ModParticles.SPELL, SpellParticle.Factory::new);

        AccessoriesRendererRegistry.registerRenderer(ModItems.TOP_HAT, HoldableHatRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(ModItems.WITCH_HAT, HoldableHatRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(ModItems.FEZ, HoldableHatRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(ModItems.COLLAR, CollarRenderer::new);
        AccessoriesRendererRegistry.registerNoRenderer(ModItems.MACRO_RING);
        AccessoriesRendererRegistry.registerNoRenderer(ModItems.AMETHYST_WHORL);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPELL_RESONATOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LIGHT, RenderLayer.getTranslucent());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                var editing = client.currentScreen instanceof ScrollAndQuillScreen;
                var serverEditing = ModEntityComponents.IS_EDITING_SCROLL.get(client.player).isEditing();

                if (editing != serverEditing) {
                    ModNetworking.CHANNEL.clientHandle()
                            .send(
                                    new IsEditingScrollPacket(
                                            editing, editing
                                                    ? ((ScrollAndQuillScreen) client.currentScreen).getScreenHandler().isOffhand()
                                                    : false
                                    )
                            );
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(merlinKeeperTracker::tick);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            dialogueBackend.resetStack();
        });

        Trickster.merlinTooltipAppender = merlinKeeperTracker;
        KnotItem.barStepFunction = stack -> {
            var manaComponent = stack.get(ModComponents.MANA);
            if (manaComponent == null || MinecraftClient.getInstance().world == null) {
                return 0;
            }

            ClientUtils.trySubscribe(manaComponent);

            float poolMax = manaComponent.pool().getMax(MinecraftClient.getInstance().world);
            return poolMax == 0 ? 0
                    : MathHelper.clamp(
                            Math.round(manaComponent.pool().get(MinecraftClient.getInstance().world) * 13.0F / poolMax),
                            0, 13
                    );
        };

        WorldRenderEvents.AFTER_ENTITIES.register(FlecksRenderer::render);

        HudRenderCallback.EVENT.register(BarsRenderer::render);
        HudRenderCallback.EVENT.register(SpellConstructErrorRenderer::render);

        EntityModelLayerRegistry.registerModelLayer(
                ScrollShelfBlockEntityRenderer.MODEL_LAYER,
                ScrollShelfBlockEntityRenderer::getTexturedModelData
        );
        EntityModelLayerRegistry.registerModelLayer(
                ModularSpellConstructBlockEntityRenderer.MODEL_LAYER,
                ModularSpellConstructBlockEntityRenderer::getTexturedModelData
        );

        ImGuiThings.add(dialogueBackend);
    }
}
