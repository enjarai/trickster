package dev.enjarai.trickster;

import dev.enjarai.trickster.entity.SpellRunningState;
import dev.enjarai.trickster.spell.CrowMindAttachment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@SuppressWarnings("UnstableApiUsage")
public class ModAttachments {
    public static final AttachmentType<CrowMindAttachment> CROW_MIND = AttachmentRegistry.<CrowMindAttachment>builder()
            .initializer(() -> new CrowMindAttachment(VoidFragment.INSTANCE))
            .persistent(CrowMindAttachment.CODEC)
            .buildAndRegister(Trickster.id("crow_mind"));

    public static final AttachmentType<Boolean> WHY_IS_THERE_NO_WAY_TO_DETECT_THIS = AttachmentRegistry
            .create(Trickster.id("why_is_there_no_way_to_detect_this"));

    public static final AttachmentType<SpellRunningState.State> RUNNING_STATE = AttachmentRegistry.<SpellRunningState.State>builder()
            .initializer(() -> SpellRunningState.Idle.instance)
            .persistent(SpellRunningState.CODEC)
            .buildAndRegister(Trickster.id("spell_running_state"));


    public static void register() {

    }
}
