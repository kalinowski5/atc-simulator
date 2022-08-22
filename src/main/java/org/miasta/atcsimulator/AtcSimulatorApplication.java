package org.miasta.atcsimulator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
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
    void runSimulation() {
        this.simulation.tick(0.5); //@TODO: Pass date object instead
    }

    @Bean
    public Simulation getSimulation() { //@TODO: return planes positions only
        return simulation;
    }
}

@Configuration
@EnableScheduling
class SchedulingConfiguration {

}