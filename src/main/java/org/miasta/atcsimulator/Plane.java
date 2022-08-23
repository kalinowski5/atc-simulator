package org.miasta.atcsimulator;

import org.geotools.geometry.DirectPosition3D;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.operation.TransformException;

import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.LocalDateTime;

public class Plane {

    private static final int ONE_THOUSAND_METERS = 1000;

    private final String callSign;

    private DirectPosition3D currentPosition;

    private DirectPosition3D destinationPosition;

    private LocalDateTime lastPositionUpdatedAt;

    private int heading; //@TODO: VO

    private int speedKmh; // @TODO: VO, @TODO: knots instead of km/h

    public Plane(String callSign, DirectPosition3D currentPosition, DirectPosition3D destinationPosition) {
        this.callSign = callSign;
        this.currentPosition = currentPosition;
        this.destinationPosition = destinationPosition;
        this.heading = 360;
        this.speedKmh = 0;
    }

    public DirectPosition3D currentPosition() {
        return currentPosition;
    }

    public DirectPosition3D destination() {
        return destinationPosition;
    }

    void move(LocalDateTime currentDateTime) throws TransformException {

        if (this.lastPositionUpdatedAt == null) {
            this.lastPositionUpdatedAt = currentDateTime;
            return;
        }

        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingPosition(this.currentPosition);

        Duration duration = Duration.between(this.lastPositionUpdatedAt, currentDateTime);

        double durationInHours = ((double) duration.toMillis()) / ONE_THOUSAND_METERS / 3600;
        double distanceToBeCoveredInKm = durationInHours * this.speedKmh;
        double distanceToBeCoveredInMeters = distanceToBeCoveredInKm * ONE_THOUSAND_METERS;

        calc.setDirection(this.heading, distanceToBeCoveredInMeters);
        Point2D newPosition = calc.getDestinationGeographicPoint();

        //@TODO: Keep history of movements
        this.currentPosition = new DirectPosition3D(newPosition.getX(), newPosition.getY(), this.currentPosition.z);
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
        calc.setStartingPosition(this.currentPosition);
        calc.setDestinationPosition(anotherPlane.currentPosition);

        double horizontalDistanceInMeters = calc.getOrthodromicDistance();

        return horizontalDistanceInMeters > 4000; //@TODO: Const + real value

        //@TODO: check vertical separation
    }

    boolean hasArrived()
    {
        //@TODO: Take altitude into account

        GeodeticCalculator calc = new GeodeticCalculator();
        try {
            calc.setStartingPosition(this.currentPosition);
            calc.setDestinationPosition(this.destinationPosition);
        } catch (TransformException e) {
            throw new RuntimeException(e);
        }

        return calc.getOrthodromicDistance() < ONE_THOUSAND_METERS;
    }
}
