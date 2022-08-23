package org.miasta.atcsimulator;

import org.geotools.geometry.DirectPosition3D;
import org.geotools.referencing.GeodeticCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opengis.referencing.operation.TransformException;
import org.opengis.temporal.Instant;

import java.time.*;
import java.util.ArrayList;

public class AirTrafficControlTests {

    @Test
    void testPlanesAreSeparated() throws InterruptedException, TransformException {
        Plane plane1 = new Plane("SP-MMA", new DirectPosition3D(0, 0, 1000), new DirectPosition3D(1, 1, 1000));
        Plane plane2 = new Plane("SP-DDS", new DirectPosition3D(1, 1, 1000), new DirectPosition3D(0, 0, 1000));

        ArrayList<Plane> planes = new ArrayList<>();
        planes.add(plane1);
        planes.add(plane2);


        plane1.requestSpeed(500);
        plane2.requestSpeed(400);



        Clock clock = Clock.fixed(LocalDateTime
                .of(2020, 3, 23, 8, 30, 10, 20)
                .toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));

        AirTrafficControlUnit atc = new AirTrafficControlUnit(planes);
        Simulation simulation = new Simulation(planes);

        for (int i = 0; i < 50; i++) {

            clock = Clock.offset(clock, Duration.ofSeconds(20));
            System.out.println(LocalDateTime.now(clock));

            simulation.tick(clock);
            atc.control(); //@TODO: Every N-iteration only


            GeodeticCalculator calc = new GeodeticCalculator();
            calc.setStartingPosition(plane1.currentPosition());
            calc.setDestinationPosition(plane2.currentPosition());

            System.out.println("Separation: " + (int) calc.getOrthodromicDistance() + "m");
            Assertions.assertTrue(calc.getOrthodromicDistance() > 4000, "Separation minimum violated: " +calc.getOrthodromicDistance());


            System.out.println(plane1.heading());
            System.out.println(plane2.heading());
            System.out.println("---------------------");
        }
    }


}
