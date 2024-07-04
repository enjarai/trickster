package dev.enjarai.trickster.net;

import net.minecraft.util.Hand;

public record SignScrollPacket(Hand hand, String name) {
}
