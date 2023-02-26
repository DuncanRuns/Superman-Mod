package me.duncanruns.superman.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "checkFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack forceElytra(PlayerEntity instance, EquipmentSlot slot) {
        return new ItemStack(Items.ELYTRA);
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void preventFallDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (source.equals(DamageSource.FALL) || source.equals(DamageSource.FLY_INTO_WALL)) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "travel", at = @At("TAIL"))
    private void boosterMixin(Vec3d movementInput, CallbackInfo info) {
        if (isFallFlying()) {
            boolean jumping = this.jumping;
            boolean sneaking = isSneaking();
            if (sneaking && jumping) {
                setVelocity(getRotationVector().multiply(0.25));
            } else if (sneaking) {
                setVelocity(new Vec3d(0, 0.04, 0));
            } else if (jumping) {
                setVelocity(getRotationVector().multiply(5));
            }
        }
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"), cancellable = true)
    private void momentumBreakMixin(BlockState block, CallbackInfoReturnable<Float> info) {
        info.setReturnValue(100000F);
    }

    @Override
    public void jump() {
        if (!isFallFlying())
            super.jump();
    }
}
