package dev.enjarai.trickster.item.component;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.UUID;

public record CollarLinkComponent(UUID uuid) {
    public static final StructEndec<CollarLinkComponent> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.UUID.fieldOf("uuid", CollarLinkComponent::uuid),
            CollarLinkComponent::new
    );
}
