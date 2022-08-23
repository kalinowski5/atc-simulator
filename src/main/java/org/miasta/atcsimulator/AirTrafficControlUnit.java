package org.miasta.atcsimulator;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.operation.TransformException;

import java.util.ArrayList;

public class AirTrafficControlUnit {

    ArrayList<Plane> planesUnderControl;

    public AirTrafficControlUnit(ArrayList<Plane> planesUnderControl) {
        this.planesUnderControl = planesUnderControl;
    }

    public void control()
    {
        this.planesUnderControl.forEach(this::findBestHeading);
    }

    private void findBestHeading(Plane plane)
    {
        GeodeticCalculator calc = new GeodeticCalculator();
        try {
            calc.setStartingPosition(plane.currentPosition());
            calc.setDestinationPosition(plane.destination());
        } catch (TransformException e) {
            throw new RuntimeException(e);
        }

        int heading = (int) calc.getAzimuth();

        plane.requestHeading(heading);
    }
}
