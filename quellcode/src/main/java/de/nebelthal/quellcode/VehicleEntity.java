package de.nebelthal.quellcode;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class VehicleEntity extends Entity {
    private static final TrackedData<Boolean> LIGHTS = DataTracker.registerData(VehicleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SIREN = DataTracker.registerData(VehicleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> LADDER = DataTracker.registerData(VehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public final VehicleKind kind;
    private boolean forward, back, left, right, ladderTarget;
    private int lastInput = -100, lastToggle = -100;
    private double speed;
    private int interpolationSteps;
    private double targetX, targetY, targetZ;
    private float targetYaw;

    public VehicleEntity(EntityType<? extends VehicleEntity> type, World world, VehicleKind kind) {
        super(type, world); this.kind = kind;
    }
    @Override protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(LIGHTS, false); builder.add(SIREN, false); builder.add(LADDER, 0f);
    }
    public boolean lightsOn() { return dataTracker.get(LIGHTS); }
    public boolean sirenOn() { return dataTracker.get(SIREN); }
    public float ladderExtension() { return dataTracker.get(LADDER); }
    public void acceptControls(ControlPayload controls) {
        forward = controls.forward(); back = controls.back(); left = controls.left(); right = controls.right();
        lastInput = age;
        if (age - lastToggle >= 6 && (controls.lights() || controls.siren() || controls.ladder())) {
            if (controls.lights()) dataTracker.set(LIGHTS, !lightsOn());
            if (controls.siren()) dataTracker.set(SIREN, !sirenOn());
            if (controls.ladder() && kind == VehicleKind.DLK_18 && Math.abs(speed) < 0.01 && isOnGround()) ladderTarget = !ladderTarget;
            lastToggle = age;
        }
    }
    @Override public void tick() {
        super.tick();
        if (getWorld().isClient) {
            if (interpolationSteps > 0) {
                setPosition(getX() + (targetX - getX()) / interpolationSteps,
                    getY() + (targetY - getY()) / interpolationSteps, getZ() + (targetZ - getZ()) / interpolationSteps);
                setYaw(getYaw() + MathHelper.wrapDegrees(targetYaw - getYaw()) / interpolationSteps);
                --interpolationSteps;
            }
            return;
        }
        if (!hasPassengers() || age - lastInput > 10) forward = back = left = right = false;
        float extension = ladderExtension();
        float next = MathHelper.clamp(extension + (ladderTarget ? 0.0125f : -0.0125f), 0, 1);
        if (next != extension) dataTracker.set(LADDER, next);
        boolean water = getWorld().getFluidState(getBlockPos()).isIn(FluidTags.WATER);
        boolean canDrive = kind == VehicleKind.MZB ? water : isOnGround();
        if (canDrive && next == 0) speed += (forward ? 0.012 : 0) - (back ? 0.009 : 0);
        speed *= (forward || back) && canDrive ? 0.98 : 0.88;
        if (next > 0) speed = 0;
        speed = MathHelper.clamp(speed, -kind.maxSpeed * 0.4, kind.maxSpeed);
        if (Math.abs(speed) > 0.003) setYaw(getYaw() + ((right ? 1 : 0) - (left ? 1 : 0)) * 2.2f * (float)Math.signum(speed));
        double radians = Math.toRadians(getYaw());
        double vertical = getVelocity().y;
        if (kind == VehicleKind.MZB && water) {
            double surface = getBlockY() + getWorld().getFluidState(getBlockPos()).getHeight(getWorld(), getBlockPos());
            vertical = MathHelper.clamp((surface - getY() - 0.35) * 0.15, -0.1, 0.1);
        } else vertical = isOnGround() ? -0.04 : Math.max(-0.8, vertical - 0.04);
        setVelocity(-Math.sin(radians) * speed, vertical, Math.cos(radians) * speed);
        move(MovementType.SELF, getVelocity());
        if (horizontalCollision) speed = 0;
        if (sirenOn() && age % 40 == 0) getWorld().playSound(null, getX(), getY(), getZ(), Quellcode.SIREN, SoundCategory.NEUTRAL, 1.8f, 1f);
    }
    @Override public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int steps) {
        targetX = x; targetY = y; targetZ = z; targetYaw = yaw; interpolationSteps = Math.max(1, steps);
    }
    @Override protected void updatePassengerPosition(Entity passenger, PositionUpdater updater) {
        if (!hasPassenger(passenger)) return;
        Vec3d seat = new Vec3d(-0.45, kind == VehicleKind.MZB ? 0.8 : 1.15, kind == VehicleKind.MZB ? -0.35 : 1.3)
            .rotateY(-getYaw() * MathHelper.RADIANS_PER_DEGREE);
        updater.accept(passenger, getX() + seat.x, getY() + seat.y, getZ() + seat.z);
    }
    @Override public ActionResult interact(PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        if (player.isSneaking()) {
            if (player.isCreative() && !hasPassengers()) {
                if (!getWorld().isClient) { dropItem(Quellcode.ITEMS.get(kind)); discard(); }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        if (!getWorld().isClient) {
            if (hasPassengers()) return ActionResult.FAIL;
            player.startRiding(this);
            player.sendMessage(Text.translatable("message.quellcode.controls"), true);
        }
        return ActionResult.SUCCESS;
    }
    @Override public float getStepHeight() { return kind == VehicleKind.MZB ? 0 : 0.6f; }
    @Override public Box getVisibilityBoundingBox() {
        return getBoundingBox().expand(kind == VehicleKind.DLK_18 ? 20 : 4);
    }
    @Override public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        for (int side : new int[] {1, -1}) {
            Vec3d offset = new Vec3d(side * (kind.width / 2 + 0.9), 0.1, 0)
                .rotateY(-getYaw() * MathHelper.RADIANS_PER_DEGREE);
            Vec3d position = getPos().add(offset);
            Box space = passenger.getBoundingBox().offset(position.subtract(passenger.getPos()));
            if (getWorld().isSpaceEmpty(passenger, space)) return position;
        }
        return getPos().add(0, kind.height + 0.2, 0);
    }
    @Override public boolean isCollidable() { return !isRemoved(); }
    @Override public boolean canHit() { return !isRemoved(); }
    @Override protected boolean canAddPassenger(Entity passenger) { return !hasPassengers(); }
    @Override protected void readCustomDataFromNbt(NbtCompound nbt) {
        dataTracker.set(LIGHTS, nbt.getBoolean("BlueLights"));
        dataTracker.set(SIREN, nbt.getBoolean("Siren"));
        float value = nbt.getFloat("Ladder");
        dataTracker.set(LADDER, Float.isFinite(value) ? MathHelper.clamp(value, 0, 1) : 0);
        ladderTarget = nbt.getBoolean("LadderDeployed") && kind == VehicleKind.DLK_18;
    }
    @Override protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("BlueLights", lightsOn()); nbt.putBoolean("Siren", sirenOn());
        nbt.putFloat("Ladder", ladderExtension()); nbt.putBoolean("LadderDeployed", ladderTarget);
    }
}
