package me.duncanruns.superman.mixinint;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface CustomPayloadC2SPacketInt {
    Identifier superman$getChannel();

    PacketByteBuf superman$getData();
}
