package dev.ferretbeans.pawtism;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
    
public class position {
    public static Vec3d playerpos() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player != null) {
            return player.getPos();
        }
        return null;
    }
}
