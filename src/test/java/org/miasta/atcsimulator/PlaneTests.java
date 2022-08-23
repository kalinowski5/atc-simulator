package org.miasta.atcsimulator;

import org.geotools.geometry.DirectPosition3D;
import org.geotools.referencing.GeodeticCalculator;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opengis.referencing.operation.TransformException;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PlaneTests {

    @Test
    void testHeadingCanBeChanged() {
        Plane plane = new Plane("SP-MMA", new DirectPosition3D(0, 0, 1000), new DirectPosition3D(1, 1, 1000));

        plane.requestHeading(80);

        Assertions.assertEquals(80, plane.heading());
    }

    @Test
    void testPlaneDoesNotChangePositionWhenMovedFirstTime() throws TransformException {
        Plane plane = new Plane("SP-MMA", new DirectPosition3D(0, 0, 1000), new DirectPosition3D(1, 1, 1000));

        LocalDateTime now = LocalDateTime.of(2022, 8, 10, 13, 20, 0, 0);

        plane.move(now);

        Assertions.assertEquals(new DirectPosition3D(0, 0, 1000), plane.currentPosition());
    }

    @ParameterizedTest
    @MethodSource("providePlanesSituations")
    void testPlaneCoversProperDistanceWhenMoving(
            LocalDateTime previouslyMovedAt,
            LocalDateTime movedAt,
            int currentSpeed,
            double expectedDistanceTravelled
    ) throws TransformException {

        DirectPosition3D originalPosition = new DirectPosition3D(0, 0, 1000);
        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingPosition(originalPosition);

        Plane plane = new Plane("SP-MMA", originalPosition, new DirectPosition3D(10, 10, 1000));
        plane.requestSpeed(currentSpeed);

        plane.move(previouslyMovedAt);
        plane.move(movedAt);

        calc.setDestinationGeographicPoint(plane.currentPosition().x, plane.currentPosition().y);

        double actualDistanceCovered = calc.getOrthodromicDistance() / 1000; //In kilometers

        Assertions.assertEquals(expectedDistanceTravelled, actualDistanceCovered, 0.01);
    }

    private static Stream<Arguments> providePlanesSituations() {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.of(2022, 8, 10, 13, 20),
                        LocalDateTime.of(2022, 8, 10, 14, 20),
                        500,
                        500
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 10, 13, 20),
                        LocalDateTime.of(2022, 8, 10, 14, 50),
                        500,
                        750
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 10, 13, 20),
                        LocalDateTime.of(2022, 8, 10, 14, 50),
                        600,
                        900
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 10, 13, 20),
                        LocalDateTime.of(2022, 8, 10, 15, 9),
                        500,
                        908.33
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 10, 13, 20),
                        LocalDateTime.of(2022, 8, 10, 13, 21),
                        500,
                        8.333
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 10, 13, 20),
                        LocalDateTime.of(2022, 8, 10, 13, 21),
                        0,
                        0
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 10, 13, 20, 0),
                        LocalDateTime.of(2022, 8, 10, 13, 20, 1),
                        500,
                        0.14
                ),
                Arguments.of(
                        LocalDateTime.of(2022, 8, 10, 13, 20, 0),
                        LocalDateTime.of(2022, 8, 10, 13, 20, 1),
                        1000,
                        0.28
                )
        );
    }

    @ParameterizedTest(name= "{index}: {0}")
    @MethodSource("providePlanesWithArrivalStatus")
    void testPlaneArrivalStatus(
            boolean hasArrivedToDestination,
            DirectPosition3D currentPosition,
            DirectPosition3D destinationPosition
    ) {

        Plane plane = new Plane("SP-MMA", currentPosition, destinationPosition);

        Assertions.assertEquals(hasArrivedToDestination, plane.hasArrived());
    }

    private static Stream<Arguments> providePlanesWithArrivalStatus() {
        return Stream.of(
                arguments(
                        named("0 meters", true),
                        new DirectPosition3D(19.0746663, 50.4711, 1000),
                        new DirectPosition3D(19.0746663, 50.4711, 1000)
                ),
                arguments(
                        named("575 meters", true),
                        new DirectPosition3D(19.0746663, 50.4711, 1000),
                        new DirectPosition3D(19.08, 50.475, 1000)
                ),
                arguments(
                        named("999 meters", true),
                        new DirectPosition3D(20.00, 70.00, 1000),
                        new DirectPosition3D(20.01235, 70.0079, 1000)
                ),
                arguments(
                        named("1003 meters", false),
                        new DirectPosition3D(20.00, 70.00, 1000),
                        new DirectPosition3D(20.012, 70.008, 1000)
                ),
                arguments(
                        named("14338 meters", false),
                        new DirectPosition3D(19.0746663, 50.4711, 1000),
                        new DirectPosition3D(19.0746663, 50.600, 1000)
                ),
                arguments(
                        named("67150 meters", false),
                        new DirectPosition3D(19.0746663, 50.4711, 1000),
                        new DirectPosition3D(20.00, 50.600, 1000)
                )
        );
    }


}
