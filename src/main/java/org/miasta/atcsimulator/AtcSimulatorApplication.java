package org.miasta.atcsimulator;

import org.geotools.geometry.DirectPosition3D;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Clock;
import java.util.ArrayList;

@SpringBootApplication
@EnableScheduling
public class AtcSimulatorApplication implements CommandLineRunner {

    private final Simulation simulation;
    private final AirTrafficControlUnit atc;

    public AtcSimulatorApplication() {

        ArrayList<Plane> planes = new ArrayList<Plane>();

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


        simulation = new Simulation(planes);
        atc = new AirTrafficControlUnit(simulation.planes());
    }

    public static void main(String[] args) {
        SpringApplication.run(AtcSimulatorApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }

    @Scheduled(fixedRate = 500L)
    void movePlanes() {
        Clock clock = Clock.systemUTC();
        this.simulation.tick(clock);
    }

    @Scheduled(fixedRate = 2000L)
    void controlAirspace() {
        atc.control();
    }

    @Bean
    public Simulation getSimulation() { //@TODO: return planes positions only
        return simulation;
    }
}
