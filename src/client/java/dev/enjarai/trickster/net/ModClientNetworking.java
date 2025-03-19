package dev.enjarai.trickster.net;

import dev.enjarai.trickster.TricksterClient;
import dev.enjarai.trickster.aldayim.dialogue.AldayimDialogue;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.mixin.client.WorldRendererAccessor;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ChunkSectionPos;

public class ModClientNetworking {
    @SuppressWarnings("resource")
    public static void register() {
        ModNetworking.CHANNEL.registerClientbound(RebuildChunkPacket.class, (message, access) -> {
            var pos = message.pos();

            ChunkSectionPos.forEachChunkSectionAround(
                    pos, chunk -> ((WorldRendererAccessor) access.runtime().worldRenderer)
                            .trickster$scheduleChunkRender(
                                    ChunkSectionPos.unpackX(chunk),
                                    ChunkSectionPos.unpackY(chunk),
                                    ChunkSectionPos.unpackZ(chunk),
                                    true
                            )
            );
        });
        ModNetworking.CHANNEL.registerClientbound(GrabClipboardSpellPacket.class, (message, access) -> {
            var clipboard = access.runtime().keyboard.getClipboard();

            if (clipboard.isBlank()) {
                access.player().sendMessage(Text.literal("Clipboard is empty").formatted(Formatting.RED));
                return;
            }

            SpellPart spell;
            try {
                spell = (SpellPart) Fragment.fromBase64(clipboard);
            } catch (Exception e) {
                access.player().sendMessage(Text.literal("Failed to decode clipboard, does it contain a valid spell?").formatted(Formatting.RED));
                return;
            }

            ModNetworking.CHANNEL.clientHandle().send(new ClipBoardSpellResponsePacket(spell));
        });
        ModNetworking.CHANNEL.registerClientbound(MladyAnimationPacket.class, (message, access) -> {
            var entity = access.player().clientWorld.getEntityById(message.entityId());
            if (entity instanceof PlayerEntity player) {
                ModEntityComponents.PLAYER_ANIMATION.get(player).hatTakeyNess = 1;
            }
        });
        ModNetworking.CHANNEL.registerClientbound(
                TskEveryoneThinksTheGraveDirtAndAnimatedDustWillBeEnoughAndTheyDontBotherToMakeTheOilNoOneTakesTheTimeToDoAProperJobOfThingsAsIfTheFortyHoursOfRefinementInTheAlembicCouldntBeProductivelyFilled.class,
                (message, access) -> {
                    TricksterClient.dialogueBackend.start(AldayimDialogue.INSTANCE);
                }
        );
    }
}
