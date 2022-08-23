package org.miasta.atcsimulator;

import org.geotools.geometry.DirectPosition3D;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.operation.TransformException;

import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.LocalDateTime;

public class Plane {

    private final String callSign;

    private DirectPosition3D position;

    private DirectPosition3D destination;

    private LocalDateTime lastPositionUpdatedAt;

    private int heading; //@TODO: VO

    private int speedKmh; // @TODO: VO, @TODO: knots instead of km/h

    public Plane(String callSign, DirectPosition3D currentPosition, DirectPosition3D destination) {
        this.callSign = callSign;
        this.position = currentPosition;
        this.destination = destination;
        this.heading = 360;
        this.speedKmh = 0;
    }

    public DirectPosition3D currentPosition() {
        return position;
    }

    void move(LocalDateTime currentDateTime) throws TransformException {

        if (this.lastPositionUpdatedAt == null) {
            this.lastPositionUpdatedAt = currentDateTime;
            return;
        }

        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingPosition(this.position);

        Duration duration = Duration.between(this.lastPositionUpdatedAt, currentDateTime);

        double durationInHours = ((double) duration.toMillis()) / 1000 / 3600;
        double distanceToBeCoveredInKm = durationInHours * this.speedKmh;
        double distanceToBeCoveredInMeters = distanceToBeCoveredInKm * 1000;

        calc.setDirection(this.heading, distanceToBeCoveredInMeters);
        Point2D newPosition = calc.getDestinationGeographicPoint();

        //@TODO: Keep history of movements
        this.position = new DirectPosition3D(newPosition.getX(), newPosition.getY(), this.position.z);
        this.lastPositionUpdatedAt = currentDateTime;
    }

    void requestHeading(int newHeading) {//@TODO: VO
        this.heading = newHeading;
    }

    void requestSpeed(int newSpeedKmh) { //@TODO: VO
        this.speedKmh = newSpeedKmh;
    }

    public String callSign() {
        return callSign;
    }

    public int speedKmh() {
        return speedKmh;
    }

    public int heading() {
        return heading;
    }

    boolean isProperlySeparated(Plane anotherPlane) throws TransformException {
        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingPosition(this.position);
        calc.setDestinationPosition(anotherPlane.position);

        double horizontalDistanceInMeters = calc.getOrthodromicDistance();

        System.out.println(horizontalDistanceInMeters);

        return horizontalDistanceInMeters > 4000; //@TODO: Const + real value

        //@TODO: check vertical separation
    }
}
