package org.backend.examprep_backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ExamprepBackendApplication {

    private static final Logger logger = LoggerFactory.getLogger(ExamprepBackendApplication.class);

    public static void main(String[] args) {
        // Run the application and get the application context
        ApplicationContext context = SpringApplication.run(ExamprepBackendApplication.class, args);

        // Retrieve the environment and log the active profiles
        Environment env = context.getEnvironment();
        String[] activeProfiles = env.getActiveProfiles();

        // Log the active profiles
        logger.info("Active profiles: {}", (Object) activeProfiles);

        // Validate if the application is running with a valid profile
        validateActiveProfiles(activeProfiles);
    }

    // Helper method to validate that a suitable profile is active
    private static void validateActiveProfiles(String[] activeProfiles) {
        // Check for required profiles based on the active profile
        if (activeProfiles.length == 0) {
            throw new IllegalStateException("No active profile set. Please specify a profile.");
        }

        // You can specify additional conditions based on your requirements
        boolean isValid = containsProfile(activeProfiles, "local") || containsProfile(activeProfiles, "prod");

        if (!isValid) {
            throw new IllegalStateException("The application must be run with either 'local' or 'prod' profile.");
        }
    }

    // Helper method to check for a specific profile in active profiles
    private static boolean containsProfile(String[] profiles, String targetProfile) {
        for (String profile : profiles) {
            if (profile.equals(targetProfile)) {
                return true;
            }
        }
        return false;
    }
}
