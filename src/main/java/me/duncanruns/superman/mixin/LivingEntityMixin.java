package me.duncanruns.superman.mixin;

import me.duncanruns.superman.mixinint.JumpOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "initAi", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack forceElytra(LivingEntity instance, EquipmentSlot equipmentSlot) {
        return new ItemStack(Items.ELYTRA);
    }

    @Inject(method = "initAi", at = @At("HEAD"), cancellable = true)
    private void cancelWhileHoldingJump(CallbackInfo info) {
        if (!((Object) this instanceof ServerPlayerEntity)) return;
        if (isFallFlying() && (((JumpOwner) this).superman$getHoldingJumpDuringGlide() ||isSneaking())) {
            info.cancel();
        }
    }

    @Shadow
    public abstract boolean isFallFlying();

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V"))
    private void cancelCancel(LivingEntity instance, int i, boolean b) {
        if (i != 7 || b) {
            setFlag(i, b);
        }
    }
}
