package de.nebelthal.quellcode;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/** Only the server-verified driver may control the vehicle. Inputs are bounded booleans. */
public record ControlPayload(boolean forward, boolean back, boolean left, boolean right,
                             boolean lights, boolean siren, boolean ladder) implements CustomPayload {
    public static final Id<ControlPayload> ID = new Id<>(Quellcode.id("controls"));
    public static final PacketCodec<RegistryByteBuf, ControlPayload> CODEC = PacketCodec.of(
        (value, buf) -> {
            buf.writeBoolean(value.forward); buf.writeBoolean(value.back);
            buf.writeBoolean(value.left); buf.writeBoolean(value.right);
            buf.writeBoolean(value.lights); buf.writeBoolean(value.siren); buf.writeBoolean(value.ladder);
        }, buf -> new ControlPayload(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(),
                                    buf.readBoolean(), buf.readBoolean(), buf.readBoolean()));
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
