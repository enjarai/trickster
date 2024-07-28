package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.CasterComponent;
import dev.enjarai.trickster.net.KillSpellPacket;
import dev.enjarai.trickster.net.ModNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SpellSlotWidget extends ButtonWidget {
    public static final ButtonTextures TEXTURES_INACTIVE = new ButtonTextures(Trickster.id("spell_slot/inactive"), Trickster.id("spell_slot/inactive"));
    public static final ButtonTextures TEXTURES_ACTIVE_SUSPENDED = new ButtonTextures(Trickster.id("spell_slot/active_suspended"), Trickster.id("spell_slot/active_suspended_hover"));
    public static final ButtonTextures TEXTURES_ACTIVE_OK = new ButtonTextures(Trickster.id("spell_slot/active_ok"), Trickster.id("spell_slot/active_ok_hover"));
    public static final ButtonTextures TEXTURES_ACTIVE_PARTIAL = new ButtonTextures(Trickster.id("spell_slot/active_partial"), Trickster.id("spell_slot/active_partial_hover"));
    public static final ButtonTextures TEXTURES_ACTIVE_FULL = new ButtonTextures(Trickster.id("spell_slot/active_full"), Trickster.id("spell_slot/active_full_hover"));
    public static final ButtonTextures TEXTURES_ACTIVE_ERROR = new ButtonTextures(Trickster.id("spell_slot/active_error"), Trickster.id("spell_slot/active_error_hover"));

    public final int index;
    public State currentState = State.INACTIVE;

    public SpellSlotWidget(int x, int y, int index) {
        super(x, y, 16, 16, Text.empty(), btn -> {
        }, DEFAULT_NARRATION_SUPPLIER);
        this.index = index;
    }

    @Override
    public void onPress() {
        if (currentState != State.INACTIVE) {
            ModNetworking.CHANNEL.clientHandle().send(new KillSpellPacket(index));
        }
    }

    public void updateState(@Nullable CasterComponent.RunningSpellData spellData) {
        if (spellData == null) {
            currentState = State.INACTIVE;
        } else if (spellData.errored()) {
            currentState = State.ACTIVE_ERROR;
        } else if (spellData.executionsLastTick() >= Trickster.CONFIG.maxExecutionsPerSpellPerTick()) {
            currentState = State.ACTIVE_FULL;
        } else if (spellData.executionsLastTick() >= Trickster.CONFIG.maxExecutionsPerSpellPerTick() / 2) {
            currentState = State.ACTIVE_PARTIAL;
        } else if (spellData.executionsLastTick() <= 0) {
            currentState = State.ACTIVE_SUSPENDED;
        } else {
            currentState = State.ACTIVE_OK;
        }

        if (spellData != null && spellData.message().isPresent()) {
            setTooltip(Tooltip.of(spellData.message().get()));
        } else {
            setTooltip(null);
        }
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier identifier = currentState.textures.get(this.isNarratable(), this.isSelected());
        context.drawGuiTexture(identifier, this.getX(), this.getY(), this.width, this.height);
    }

    public enum State {
        INACTIVE(TEXTURES_INACTIVE),
        ACTIVE_SUSPENDED(TEXTURES_ACTIVE_SUSPENDED),
        ACTIVE_OK(TEXTURES_ACTIVE_OK),
        ACTIVE_PARTIAL(TEXTURES_ACTIVE_PARTIAL),
        ACTIVE_FULL(TEXTURES_ACTIVE_FULL),
        ACTIVE_ERROR(TEXTURES_ACTIVE_ERROR);

        public final ButtonTextures textures;

        State(ButtonTextures textures) {
            this.textures = textures;
        }
    }
}
