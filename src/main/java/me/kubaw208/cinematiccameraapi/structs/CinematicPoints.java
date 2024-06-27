package me.kubaw208.cinematiccameraapi.structs;

import me.kubaw208.cinematiccameraapi.interfaces.ICameraPoint;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class CinematicPoints implements ICameraPoint {

    private final List<Location> controlLocations;

    public CinematicPoints(List<Location> controlLocations, int time) {
        this.controlLocations = generateEqualDistancePoints(controlLocations, time);
    }

    @Override
    public List<Location> getLocations() {
        return List.of();
    }

    public static List<Location> generateEqualDistancePoints(List<Location> controlPoints, int totalTicks) {
        int n = controlPoints.size();
        double[] x = new double[n];
        double[] y = new double[n];
        double[] z = new double[n];

        for(int i = 0; i < n; i++) {
            x[i] = i; // Simple indexing
            y[i] = controlPoints.get(i).getY();
            z[i] = controlPoints.get(i).getZ();
        }

        SplineInterpolator interpolator = new SplineInterpolator();
        PolynomialSplineFunction ySpline = interpolator.interpolate(x, y);
        PolynomialSplineFunction zSpline = interpolator.interpolate(x, z);

        List<Location> result = new ArrayList<>();

        for(int i = 0; i < totalTicks; i++) {
            double t = i * (n - 1) / (totalTicks - 1.0);
            double interpolatedY = ySpline.value(t);
            double interpolatedZ = zSpline.value(t);
            Location interpolatedLocation = new Location(controlPoints.get(0).getWorld(), t, interpolatedY, interpolatedZ);
            result.add(interpolatedLocation);
        }

        return result;
    }

}