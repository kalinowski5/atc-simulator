package org.miasta.atcsimulator;

import org.geotools.geometry.DirectPosition3D;
import org.geotools.referencing.GeodeticCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opengis.referencing.operation.TransformException;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class PlaneTests {

    @Test
    void testHeadingCanBeChanged() {
        Plane plane = new Plane("SP-MMA", new DirectPosition3D(0, 0, 1000));

        plane.requestHeading(80);

        Assertions.assertEquals(80, plane.heading());
    }

    @Test
    void testPlaneDoesNotChangePositionWhenMovedFirstTime() throws TransformException {
        Plane plane = new Plane("SP-MMA", new DirectPosition3D(0, 0, 1000));

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

        Plane plane = new Plane("SP-MMA", originalPosition);
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
}
