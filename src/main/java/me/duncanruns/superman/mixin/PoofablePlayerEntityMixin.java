package me.duncanruns.superman.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.duncanruns.superman.DuncansAwesomeUtil.angleBetween;

@Mixin(value = {ClientPlayerEntity.class, OtherClientPlayerEntity.class})
public abstract class PoofablePlayerEntityMixin extends PlayerEntity {

    private static final double LOW_POOF_THRESHOLD = 2.5d * 2.2d;
    private static final double HIGH_POOF_THRESHOLD = 4.9d * 4.9d;
    private static final double MIN_ANGLE_CHANGE = Math.PI / 3;
    private boolean poof = false;
    private int trailTicks;
    private int poofCooldown = 0;
    private Vec3d lastDir = Vec3d.ZERO.normalize();

    public PoofablePlayerEntityMixin(World world, BlockPos blockPos, GameProfile gameProfile) {
        super(world, blockPos, gameProfile);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void poofMixin(CallbackInfo info) {
        Vec3d velocity = getVelocity();
        Vec3d newDir = velocity.normalize();
        Vec3d pos = getPos();
        Vec3d facing = getRotationVector();
        double speedSquared = velocity.lengthSquared();

        ParticleEffect particleType = isSubmergedInWater() ? ParticleTypes.BUBBLE : ParticleTypes.CLOUD;

        if ((!poof && speedSquared > HIGH_POOF_THRESHOLD) || ((speedSquared > LOW_POOF_THRESHOLD) && (angleBetween(newDir, lastDir) > MIN_ANGLE_CHANGE))) {
            // POOF ENABLED HERE
            poof = true;
            if (poofCooldown == 0) {
                Vec3d followVelocity = velocity.multiply(0.2);
                Vec3d particleSpawnPos = pos.subtract(facing.multiply(5));
                spawnParticles(particleType, particleSpawnPos, followVelocity, 1.0d, 200);
                poofCooldown = 5;
                trailTicks = 0;
                //if (!isSubmergedInWater()) {
                //    float pitchRange = 0.3f;
                //    world.playSound(pos.x, pos.y, pos.z, SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, SoundCategory.PLAYERS, 8f, random.nextFloat() * pitchRange + pitchRange, true);
                //}
            }
        }

        if (poof) {
            if (speedSquared < LOW_POOF_THRESHOLD) {
                poof = false;
                // POOF DISABLED HERE
                return;
            }
            if (trailTicks < 10) {
                // POOF WITHIN LAST HALF SECOND HERE
                Vec3d followVelocity = velocity.multiply(0.2);
                Vec3d particleSpawnPos = pos.subtract(facing.multiply(5));

                spawnParticles(particleType, particleSpawnPos, followVelocity, 0.2d, 4);
            }
        }
        trailTicks++;
        if (poofCooldown > 0) poofCooldown--;
        lastDir = newDir;
    }

    private void spawnParticles(ParticleEffect particleType, Vec3d pos, Vec3d velocity, double spread, int count) {
        for (int i = 0; i < count; i++) {
            world.addImportantParticle(
                    particleType, true,
                    pos.x + random.nextGaussian() * spread, pos.y + random.nextGaussian() * spread, pos.z + random.nextGaussian() * spread,
                    velocity.x, velocity.y, velocity.z
            );
        }
    }
}
