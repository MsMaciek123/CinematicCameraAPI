package me.kubaw208.cinematiccameraapi.structs;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import lombok.Getter;
import me.kubaw208.betterrunableapi.BetterRunnable;
import me.kubaw208.cinematiccameraapi.interfaces.ICameraPoint;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CinematicCamera {

    private final JavaPlugin plugin;
    @Getter private final List<ICameraPoint> animationPoints = new ArrayList<>();
    @Getter private final ArrayList<Player> players = new ArrayList<>();
    private final Entity armorstand;
    private BetterRunnable task;

    public CinematicCamera(JavaPlugin plugin, Location startLocation) {
        this.plugin = plugin;
        this.armorstand = startLocation.getWorld().spawnEntity(startLocation, EntityType.ARMOR_STAND);
    }

    /**
     * Starts animation
     */
    public void start() {
        int animationDurationInTicks = animationPoints.stream().mapToInt(point -> point.getLocations().size()).sum();
        AtomicInteger animationSegment = new AtomicInteger(0);
        AtomicInteger animationSegmentIndex = new AtomicInteger(0);

        players.forEach(player -> {
            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(armorstand);
        });

        task = new BetterRunnable(plugin, task -> {
            if(animationDurationInTicks < task.executions) {
                stop();
                return;
            }

            if(animationSegmentIndex.get() >= animationPoints.get(animationSegment.get()).getLocations().size()) {
                animationSegmentIndex.set(0);
                animationSegment.incrementAndGet();
            }

            ICameraPoint point = animationPoints.get(animationSegment.get());
            Location offPosition = point.getLocations().get(animationSegmentIndex.getAndIncrement());
            WrapperPlayServerEntityRelativeMoveAndRotation packet = new WrapperPlayServerEntityRelativeMoveAndRotation(armorstand.getEntityId(), offPosition.getX(), offPosition.getY(), offPosition.getZ(), offPosition.getYaw(), offPosition.getPitch(), false);

            for(Player player : players) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
                player.sendMessage("You got packet with armorstand movement");
            }
        }, 1);
    }

    /**
     * Stops animations
     */
    public void stop() {
        task.cancel();
        task = null;
    }

    /**
     * Pauses animation
     */
    public void pause() {

    }

    /**
     * Unpauses animation
     */
    public void unpause() {

    }

    /**
     * Adds new segment with animation
     */
    public CinematicCamera addPoint(ICameraPoint point) {
        animationPoints.add(point);
        return this;
    }

    /**
     * Removes last added animated segment
     */
    public CinematicCamera removeLastPoint(ICameraPoint point) {
        animationPoints.remove(animationPoints.size() - 1);
        return this;
    }

}