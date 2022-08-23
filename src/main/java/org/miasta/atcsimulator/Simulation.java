package org.miasta.atcsimulator;

import org.geotools.geometry.DirectPosition3D;
import org.opengis.referencing.operation.TransformException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Simulation {
    private ArrayList<Plane> planes;

    public Simulation(ArrayList<Plane> planes) {
        this.planes = planes;
    }

    public void tick(Clock clock) {

        this.planes.forEach(plane -> { //@TODO: Test me
            try {
                plane.move(LocalDateTime.now(clock));
            } catch (TransformException e) {
                throw new RuntimeException(e);
            }
        });

        this.planes.removeIf(Plane::hasArrived); //@TODO: Test me
    }

    public ArrayList<Plane> planes() {
        return planes;
    }
}
