package org.miasta.atcsimulator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class AtcSimulatorApplication implements CommandLineRunner {

    private final Simulation simulation;

    public AtcSimulatorApplication() {
        simulation = new Simulation();
    }

    public static void main(String[] args) {
        SpringApplication.run(AtcSimulatorApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }

    @Scheduled(fixedRate = 500L)
    void movePlanes() {
        this.simulation.tick();
    }

    @Scheduled(fixedRate = 2000L)
    void controlAirspace() {
        System.out.println("Airspace is being controlled by ATC."); //@TODO: Implement ATC AI
    }

    @Bean
    public Simulation getSimulation() { //@TODO: return planes positions only
        return simulation;
    }
}
