package me.duncanruns.superman.mixin;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import me.duncanruns.superman.Superman;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
    @Shadow
    public Input input;

    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;

    public ClientPlayerEntityMixin(World world, BlockPos blockPos, GameProfile gameProfile) {
        super(world, blockPos, gameProfile);
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack forceElytra(ClientPlayerEntity instance, EquipmentSlot equipmentSlot) {
        return new ItemStack(Items.ELYTRA);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.AFTER))
    private void sendMoreInputPackets(CallbackInfo ci) {
        if (isFallFlying()) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            Vec3d vel = this.getVelocity();
            buf.writeDouble(vel.x);
            buf.writeDouble(vel.y);
            buf.writeDouble(vel.z);
            buf.writeBoolean(this.input.jumping);
            this.networkHandler.sendPacket(new CustomPayloadC2SPacket(Superman.FLIGHT_DATA, buf));
        }
    }
}
