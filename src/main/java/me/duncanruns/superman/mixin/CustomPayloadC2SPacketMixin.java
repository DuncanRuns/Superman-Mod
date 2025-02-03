package me.duncanruns.superman.mixin;

import me.duncanruns.superman.mixinint.CustomPayloadC2SPacketInt;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CustomPayloadC2SPacket.class)
public abstract class CustomPayloadC2SPacketMixin implements CustomPayloadC2SPacketInt {
    @Shadow
    private Identifier channel;

    @Shadow private PacketByteBuf data;

    @Override
    public Identifier superman$getChannel() {
        return channel;
    }

    @Override
    public PacketByteBuf superman$getData() {
        return data;
    }
}
