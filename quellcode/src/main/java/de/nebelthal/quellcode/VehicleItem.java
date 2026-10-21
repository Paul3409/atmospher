package de.nebelthal.quellcode;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class VehicleItem extends Item {
    private final VehicleKind kind;
    public VehicleItem(VehicleKind kind, Settings settings) { super(settings); this.kind = kind; }
    @Override public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;
        if (context.getPlayer() == null) return ActionResult.PASS;
        Vec3d position = Vec3d.ofBottomCenter(context.getBlockPos().offset(context.getSide()));
        VehicleEntity vehicle = Quellcode.VEHICLES.get(kind).create(world);
        if (vehicle == null) return ActionResult.FAIL;
        vehicle.refreshPositionAndAngles(position.x, position.y, position.z, context.getPlayer().getYaw(), 0);
        if (!world.isSpaceEmpty(vehicle, vehicle.getBoundingBox())) return ActionResult.FAIL;
        if (!world.spawnEntity(vehicle)) return ActionResult.FAIL;
        if (!context.getPlayer().isCreative()) context.getStack().decrement(1);
        return ActionResult.CONSUME;
    }
}
