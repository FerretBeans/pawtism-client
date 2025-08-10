package dev.ferretbeans.pawtism;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class tps {
    //set mc time and computer time to basically a null number
    private long WorldTime = -1;
    private long ComputerTime = -1;

    public double onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) { // set package to be named packet
        //gets the current time
        long WorldTimeCurrent = packet.time();
        long ComputerTimeCurrent = System.currentTimeMillis();

        if(WorldTime != -1 && ComputerTime != -1) {
            //calculate the difference in time
            long WorldTimeDiff = WorldTimeCurrent - WorldTime;
            long ComputerTimeDiff = ComputerTimeCurrent - ComputerTime;

            //actually calculate tps
            if (ComputerTimeDiff > 0) {
                double tps = (WorldTimeDiff * 1000.0) / ComputerTimeDiff;
                tps = Math.min(tps, 20.0);
                return tps;
            }
        }

        //actually set the times
        WorldTime = WorldTimeCurrent;
        ComputerTime = ComputerTimeCurrent;

        return -1;
    }
}
