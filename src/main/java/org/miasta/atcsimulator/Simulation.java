package org.miasta.atcsimulator;

import org.geotools.geometry.DirectPosition3D;
import org.opengis.referencing.operation.TransformException;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Simulation {
    private ArrayList<Plane> planes;

    public Simulation() {

        this.planes = new ArrayList<>();

        DirectPosition3D KatowiceAirport = new DirectPosition3D(19.0746663, 50.4711, 1000);
        DirectPosition3D KrakowAirport = new DirectPosition3D(19.7841635, 50.0733, 1000);

        Plane plane1 = new Plane("SP-MMA", KatowiceAirport, KrakowAirport);
        Plane plane2 = new Plane("SP-DDS", KatowiceAirport, KrakowAirport);
        Plane plane3 = new Plane("SP-AAA", KrakowAirport, KatowiceAirport);
        Plane plane4 = new Plane("SP-BBB", KatowiceAirport, KrakowAirport);

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

    public void tick() {

        this.planes.forEach(plane -> {
            try {
                plane.move(LocalDateTime.now());

                if (plane.hasArrived()) {
                    this.planes.remove(plane); //@TODO: Move to "arrived" list
                    System.out.println(plane.callSign() + " has arrived to it's destination.");
                }

            } catch (TransformException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public ArrayList<Plane> planes() {
        return planes;
    }
}
