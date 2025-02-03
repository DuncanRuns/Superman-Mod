package me.duncanruns.superman.mixin;

import com.mojang.authlib.GameProfile;
import me.duncanruns.superman.mixinint.JumpOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements JumpOwner {
    private boolean holdingJumpDuringGlide;

    public ServerPlayerEntityMixin(World world, BlockPos blockPos, GameProfile gameProfile) {
        super(world, blockPos, gameProfile);
    }

    @Inject(method = "method_14218", at = @At("HEAD"))
    private void receiveInputPacket(float f, float g, boolean bl, boolean bl2, CallbackInfo ci) {
        holdingJumpDuringGlide = bl;
    }

    @Override
    public boolean superman$getHoldingJumpDuringGlide() {
        return holdingJumpDuringGlide;
    }
}
