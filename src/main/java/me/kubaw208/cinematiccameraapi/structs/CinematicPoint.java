package me.kubaw208.cinematiccameraapi.structs;

import me.kubaw208.cinematiccameraapi.interfaces.ICameraPoint;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class CinematicPoint implements ICameraPoint {

    //TODO Zmienić goalLocation na liste w której będą punkty lokalizacji do animacji (ilość zależna od szybkości animacji podanej w konstruktorze)
    private final List<Location> controlLocations;

    public CinematicPoint(Location startLocation, Location goalLocation, int time) {
        this.controlLocations = generateLinearPath(startLocation, goalLocation, time);
    }

    @Override
    public List<Location> getLocations() {
        return controlLocations;
    }

    public static List<Location> generateLinearPath(Location startLocation, Location goalLocation, int totalTicks) {
        List<Location> path = new ArrayList<>();

        for(int i = 1; i <= totalTicks; i++) {
            Location finalDelta = goalLocation.clone();
            finalDelta.subtract(startLocation);
            finalDelta.multiply(i / (double) totalTicks);

            System.out.println("FINAL DELTA: " + finalDelta);

            finalDelta.add(startLocation);

            path.add(finalDelta);
        }
        return path;
    }

}