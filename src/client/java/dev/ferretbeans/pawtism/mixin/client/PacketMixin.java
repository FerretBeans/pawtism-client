package dev.ferretbeans.pawtism.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ferretbeans.pawtism.statictps;
import dev.ferretbeans.pawtism.tps;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

@Mixin(net.minecraft.client.network.ClientPlayNetworkHandler.class)
public class PacketMixin {
    private final tps tps = new tps();

    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"))
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        //gets packets from the worldtimeupdate and updates the tps in statictps
        double TPS = tps.onWorldTimeUpdate(packet);
        if (TPS != -1) {
            statictps.tpscurr = TPS;
        }
    }
}
