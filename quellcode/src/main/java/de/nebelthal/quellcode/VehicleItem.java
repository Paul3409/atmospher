package de.nebelthal.quellcode;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public final class VehicleItem extends Item {
    private final VehicleKind kind;
    public VehicleItem(VehicleKind kind, Settings settings) { super(settings); this.kind = kind; }
    @Override public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (kind != VehicleKind.MZB) return TypedActionResult.pass(stack);
        BlockHitResult hit = raycast(world, player, RaycastContext.FluidHandling.ANY);
        if (hit.getType() != net.minecraft.util.hit.HitResult.Type.BLOCK) return TypedActionResult.pass(stack);
        BlockPos waterPos = hit.getBlockPos();
        if (!world.getFluidState(waterPos).isIn(FluidTags.WATER)) return TypedActionResult.pass(stack);
        double surface = waterPos.getY() + world.getFluidState(waterPos).getHeight(world, waterPos);
        if (world.isClient) return TypedActionResult.success(stack);
        if (place(world, player, stack, new Vec3d(hit.getPos().x, surface - .35, hit.getPos().z)))
            return TypedActionResult.success(stack);
        return TypedActionResult.fail(stack);
    }
    @Override public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (context.getPlayer() == null) return ActionResult.PASS;
        if (kind == VehicleKind.MZB && world.getFluidState(context.getBlockPos()).isIn(FluidTags.WATER))
            return ActionResult.PASS;
        if (world.isClient) return ActionResult.SUCCESS;
        Vec3d position = Vec3d.ofBottomCenter(context.getBlockPos().offset(context.getSide()));
        return place(world, context.getPlayer(), context.getStack(), position) ? ActionResult.CONSUME : ActionResult.FAIL;
    }
    private boolean place(World world, PlayerEntity player, ItemStack stack, Vec3d position) {
        VehicleEntity vehicle = Quellcode.VEHICLES.get(kind).create(world);
        if (vehicle == null) return false;
        vehicle.refreshPositionAndAngles(position.x, position.y, position.z, player.getYaw(), 0);
        if (!world.isSpaceEmpty(vehicle, vehicle.getBoundingBox())) return false;
        if (!world.spawnEntity(vehicle)) return false;
        if (!player.isCreative()) stack.decrement(1);
        return true;
    }
}
