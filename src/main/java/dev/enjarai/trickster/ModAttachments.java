package dev.enjarai.trickster;

import dev.enjarai.trickster.spell.CrowMind;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@SuppressWarnings("UnstableApiUsage")
public class ModAttachments {
    public static final AttachmentType<CrowMind> CROW_MIND = AttachmentRegistry.<CrowMind>builder()
            .initializer(() -> new CrowMind(VoidFragment.INSTANCE))
            .persistent(CrowMind.CODEC)
            .buildAndRegister(Trickster.id("crow_mind"));
    public static final AttachmentType<Boolean> WHY_IS_THERE_NO_WAY_TO_DETECT_THIS = AttachmentRegistry
            .create(Trickster.id("why_is_there_no_way_to_detect_this"));

    public static void register() {

    }
}
