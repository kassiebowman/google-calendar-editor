package dev.kassie.calendar.rest;

import dev.kassie.calendar.CalendarProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * REST endpoint for controlling the configuration properties of the application.
 *
 * @author kbowman
 * @since 0.0.2
 */
@RestController
public class ConfigurationController
{
    private final CalendarProperties calendarProperties;

    @Autowired
    public ConfigurationController(CalendarProperties calendarProperties)
    {
        this.calendarProperties = calendarProperties;
    }

    // TODO: Based on convos with Eric and Bryan, we will have a global set of configuration and then the option to
    //  override the configuration for each calendar. This API will be updated to support getting/setting the
    //  configuration for individual calendars by name.

    @GetMapping("/configuration")
    Configuration getConfiguration()
    {
        return new Configuration(calendarProperties);
    }

    @PostMapping("/configuration")
    void setConfiguration(@RequestBody Configuration configuration)
    {
        calendarProperties.setOriginalText(configuration.getOriginalText());
        calendarProperties.setReplacementText(configuration.getReplacementText());
        calendarProperties.setUpdatePeriod(Duration.of(configuration.getUpdatePeriodValue(), configuration.getUpdatePeriodUnit()));
        calendarProperties.setQueryPeriod(Duration.of(configuration.getQueryPeriodValue(), configuration.getQueryPeriodUnit()));
    }
}
