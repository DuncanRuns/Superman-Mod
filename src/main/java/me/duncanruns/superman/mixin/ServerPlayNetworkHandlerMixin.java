package me.duncanruns.superman.mixin;

import me.duncanruns.superman.Superman;
import me.duncanruns.superman.mixinint.CustomPayloadC2SPacketInt;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onCustomPayload", at = @At("HEAD"))
    private void onVelocityPayload(CustomPayloadC2SPacket p, CallbackInfo ci) {
        CustomPayloadC2SPacketInt packet = (CustomPayloadC2SPacketInt) p;
        if (!packet.superman$getChannel().equals(Superman.FLIGHT_DATA)) return;
        PacketByteBuf buf = packet.superman$getData();
        player.setVelocity(new Vec3d(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        ));
        player.setJumping(buf.readBoolean());
    }
}
