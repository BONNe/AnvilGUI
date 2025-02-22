package net.wesjd.anvilgui.version;

import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.ChatComponentText;
import net.minecraft.server.v1_13_R1.ContainerAnvil;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.Packet;
import net.minecraft.server.v1_13_R1.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_13_R1.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.event.CraftEventFactory;
import org.bukkit.inventory.Inventory;

public class Wrapper1_13_R1 implements VersionWrapper {

    private final EntityPlayer nms;
    private final AnvilContainer container;
    private int containerId = 0;

    public Wrapper1_13_R1(CraftPlayer craftPlayer) {
        nms = craftPlayer.getHandle();
        container = new AnvilContainer(nms);
    }

    @Override
    public Inventory create() {
        CraftEventFactory.handleInventoryCloseEvent(nms);
        nms.activeContainer = nms.defaultContainer;

        return container.getBukkitView().getTopInventory();
    }

    @Override
    public void open() {
        containerId = nms.nextContainerCounter();
        String title = container.getBukkitView().getTitle();
        packet(new PacketPlayOutOpenWindow(container.windowId, "minecraft:anvil", new ChatComponentText(title)));
        nms.activeContainer = container;
        container.windowId = containerId;
        container.addSlotListener(nms);
    }

    @Override
    public void close() {
        CraftEventFactory.handleInventoryCloseEvent(nms);
        nms.activeContainer = nms.defaultContainer;
        packet(new PacketPlayOutCloseWindow(containerId));
    }

    private void packet(Packet packet) {
        nms.playerConnection.sendPacket(packet);
    }

    private class AnvilContainer extends ContainerAnvil {

        AnvilContainer(EntityPlayer entityPlayer) {
            super(entityPlayer.inventory, entityPlayer.world, BlockPosition.ZERO, entityPlayer);

            checkReachable = false;
        }

    }
}