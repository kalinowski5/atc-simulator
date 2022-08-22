package org.miasta.atcsimulator;

import org.geotools.geometry.DirectPosition3D;
import org.opengis.referencing.operation.TransformException;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Simulation {
    private ArrayList<Plane> planes;

    public Simulation() {

        this.planes = new ArrayList<>();

        Plane plane1 = new Plane("SP-MMA", new DirectPosition3D(19.074666368, 50.4711, 12000));
        Plane plane2 = new Plane("SP-DDS", new DirectPosition3D(19.074666368, 50.4711, 13000));
        Plane plane3 = new Plane("SP-AAA", new DirectPosition3D(19.074666368, 50.4711, 14000));
        Plane plane4 = new Plane("SP-BBB", new DirectPosition3D(19.074666368, 50.4711, 15000));

        plane1.requestHeading((int) (Math.random() * 360));
        plane2.requestHeading((int) (Math.random() * 360));
        plane3.requestHeading((int) (Math.random() * 360));
        plane4.requestHeading((int) (Math.random() * 360));

        plane1.requestSpeed((int) (Math.random() * 1000));
        plane2.requestSpeed((int) (Math.random() * 1000));
        plane3.requestSpeed((int) (Math.random() * 1000));
        plane4.requestSpeed((int) (Math.random() * 1000));

        planes.add(plane1);
        planes.add(plane2);
        planes.add(plane3);
        planes.add(plane4);
    }

    public void tick(double secondsFromLastTick) {

        this.planes.forEach(plane -> {
            try {
                plane.move(LocalDateTime.now());
                System.out.println(plane.callSign() + ": " + plane.currentPosition());
            } catch (TransformException e) {
                throw new RuntimeException(e);
            }
        });

//        plane1.isProperlySeparated(plane2); //@TODO

    }

    public ArrayList<Plane> planes() {
        return planes;
    }
}
