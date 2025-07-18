package org.tiaa.in;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SplitMain {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SplitMain.class);

        // Set default profile to development if not specified
        if (System.getProperty("spring.profiles.active") == null) {
            app.setAdditionalProfiles("development");
        }

        app.run(args);
    }
}
