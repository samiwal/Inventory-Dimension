package net.whale.inventory_dimension.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.ClickType;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerBoundVirtualMenuClickPacket {
    private final Integer containerId;
    private final Integer slotNum;
    private final Integer buttonNum;
    private final ClickType clickType;


    public ServerBoundVirtualMenuClickPacket(int containerId,int slotNum,int buttonNum,ClickType clickType) {
        this.containerId = containerId;
        this.slotNum = slotNum;
        this.buttonNum = buttonNum;
        this.clickType = clickType;
    }

    public ServerBoundVirtualMenuClickPacket(FriendlyByteBuf buf) {
         this.containerId = buf.readVarInt();
         this.slotNum = buf.readVarInt();
         this.buttonNum = buf.readVarInt();
         this.clickType = buf.readEnum(ClickType.class);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.containerId);
        buffer.writeVarInt(this.slotNum);
        buffer.writeVarInt(this.buttonNum);
        buffer.writeEnum(this.clickType);
    }

    public static void handle(ServerBoundVirtualMenuClickPacket msg, CustomPayloadEvent.Context ctx) {
        // TODO
    }
}