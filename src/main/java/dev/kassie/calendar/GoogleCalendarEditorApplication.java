package dev.kassie.calendar;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main class for Spring boot configuration.
 *
 * @author kbowman
 * @since 0.0.1
 */
@SpringBootApplication
public class GoogleCalendarEditorApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(GoogleCalendarEditorApplication.class, args);
    }

    @Bean
    public ApplicationRunner calendarEditor()
    {
        return args -> CalendarEditor.start(args.getSourceArgs());
    }
}
