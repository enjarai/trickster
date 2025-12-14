package dev.enjarai.trickster.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LevitatingBlockDamageSource extends DamageSource {
    private final MutableText blockName;

    public LevitatingBlockDamageSource(World world, @Nullable Entity source, @Nullable Entity attacker, MutableText blockName) {
        super(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.LEVITATING_BLOCK), source, attacker);
        this.blockName = blockName;
    }

    @Override
    public Text getDeathMessage(LivingEntity killed) {
        if (getAttacker() != null) {
            return Text.translatable("death.attack.levitating_block.attacker", killed.getDisplayName(), blockName, getAttacker().getDisplayName());
        }

        var entity = killed.getPrimeAdversary();
        return entity != null ? Text.translatable("death.attack.levitating_block.player", killed.getDisplayName(), blockName, entity.getDisplayName())
                : Text.translatable("death.attack.levitating_block", killed.getDisplayName(), blockName);
    }
}
