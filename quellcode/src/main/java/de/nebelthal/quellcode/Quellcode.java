package de.nebelthal.quellcode;

import java.util.EnumMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class Quellcode implements ModInitializer {
    public static final String ID = "quellcode";
    public static final EnumMap<VehicleKind, EntityType<VehicleEntity>> VEHICLES = new EnumMap<>(VehicleKind.class);
    public static final EnumMap<VehicleKind, Item> ITEMS = new EnumMap<>(VehicleKind.class);
    public static final SoundEvent SIREN = SoundEvent.of(id("martinshorn"));
    public static Identifier id(String path) { return Identifier.of(ID, path); }

    @Override public void onInitialize() {
        Registry.register(Registries.SOUND_EVENT, id("martinshorn"), SIREN);
        for (VehicleKind kind : VehicleKind.values()) {
            EntityType<VehicleEntity> type = EntityType.Builder.<VehicleEntity>create(
                (entityType, world) -> new VehicleEntity(entityType, world, kind), SpawnGroup.MISC)
                .dimensions(kind.width, kind.height).maxTrackingRange(128).trackingTickInterval(1).build(id(kind.id).toString());
            VEHICLES.put(kind, Registry.register(Registries.ENTITY_TYPE, id(kind.id), type));
            ITEMS.put(kind, Registry.register(Registries.ITEM, id(kind.id), new VehicleItem(kind, new Item.Settings().maxCount(1))));
        }
        Registry.register(Registries.ITEM_GROUP, id("vehicles"), FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.quellcode"))
            .icon(() -> new ItemStack(ITEMS.get(VehicleKind.LF_10)))
            .entries((context, entries) -> { for (VehicleKind kind : VehicleKind.values()) entries.add(ITEMS.get(kind)); }).build());
        PayloadTypeRegistry.playC2S().register(ControlPayload.ID, ControlPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ControlPayload.ID, (payload, context) -> context.server().execute(() -> {
            if (context.player().getVehicle() instanceof VehicleEntity vehicle && vehicle.getFirstPassenger() == context.player()) {
                vehicle.acceptControls(payload);
            }
        }));
    }
}
