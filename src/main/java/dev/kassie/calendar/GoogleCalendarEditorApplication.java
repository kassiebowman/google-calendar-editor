package dev.kassie.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main class for Spring boot configuration.
 *
 * @author kbowman
 * @since 0.0.2
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class GoogleCalendarEditorApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(GoogleCalendarEditorApplication.class, args);
    }
}
