package dev.enjarai.trickster.cca;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

import dev.enjarai.trickster.Trickster;

public class ModUwuComponents {
    public static final ComponentKey<WardBypassComponent> WARD_BYPASS = ComponentRegistry.getOrCreate(Trickster.id("ward_bypass"), WardBypassComponent.class);
}
