package dev.enjarai.trickster;

import com.mojang.authlib.GameProfile;
import dev.enjarai.trickster.cca.DisguiseComponent;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

public class DisguiseUtil {
    @Nullable
    public static GameProfile getGameProfile(DisguiseComponent component) {
        if (component.getUuid() == null) {
            return null;
        }
        var result = MinecraftClient.getInstance().getSessionService()
                .fetchProfile(component.getUuid(), true);
        if (result == null) {
            return null;
        }
        return result.profile();
    }
}
