package com.mc3699.smparch.util;

import java.util.ArrayList;
import java.util.List;

//I FUCKING HATE, EPSTEIN, RONALD REGAN, PORTER MICHEAL MCALLISTER, THE HUNTSVILLE METROPOLITAN AREA, AND MY HOA.
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import com.mc3699.smparch.SMPArch;

@EventBusSubscriber(modid = SMPArch.MODID)
public class DelayedTaskHandler {

    private static final List<DelayedAction> DELAYED_ACTIONS = new ArrayList<>();

    // Inner class to hold the remaining ticks and the code to run
    private static class DelayedAction {
        int ticksLeft;
        final Runnable action;

        DelayedAction(int ticks, Runnable action) {
            this.ticksLeft = ticks;
            this.action = action;
        }
    }

    // The event handler that ticks all delayed actions once per server tick
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        // Iterate backwards to allow safe removal
        for (int i = DELAYED_ACTIONS.size() - 1; i >= 0; i--) {
            DelayedAction d = DELAYED_ACTIONS.get(i);
            d.ticksLeft--;
            if (d.ticksLeft <= 0) {
                d.action.run(); // finally execute your code
                DELAYED_ACTIONS.remove(i);
            }
        }
    }

    // Helper method to schedule a delayed action (call this from anywhere
    // server‑side)
    public static void scheduleDelayed(int delayTicks, Runnable action) {
        DELAYED_ACTIONS.add(new DelayedAction(delayTicks, action));
    }
}