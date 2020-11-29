package dev.kassie.calendar.rest;

import dev.kassie.calendar.CalendarProperties;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Configuration}.
 *
 * @author kbowman
 * @since 0.0.2
 */
class ConfigurationTest
{
    /**
     * Verify the correct units are used for a value in milliseconds.
     */
    @Test
    public void testConstructor_millisValue()
    {
        CalendarProperties calendarProperties = new CalendarProperties();
        calendarProperties.setUpdatePeriod(Duration.ofMillis(1));
        calendarProperties.setQueryPeriod(Duration.ofMillis(2));

        Configuration configuration = new Configuration(calendarProperties);

        assertThat(configuration.getUpdatePeriodUnit()).isEqualTo(ChronoUnit.MILLIS);
        assertThat(configuration.getUpdatePeriodValue()).isEqualTo(1);
        assertThat(configuration.getQueryPeriodUnit()).isEqualTo(ChronoUnit.MILLIS);
        assertThat(configuration.getQueryPeriodValue()).isEqualTo(2);
    }

    /**
     * Verify the correct units are used for a value in seconds.
     */
    @Test
    public void testConstructor_secondsValue()
    {
        CalendarProperties calendarProperties = new CalendarProperties();
        calendarProperties.setUpdatePeriod(Duration.ofSeconds(1));
        calendarProperties.setQueryPeriod(Duration.ofSeconds(2));

        Configuration configuration = new Configuration(calendarProperties);

        assertThat(configuration.getUpdatePeriodUnit()).isEqualTo(ChronoUnit.SECONDS);
        assertThat(configuration.getUpdatePeriodValue()).isEqualTo(1);
        assertThat(configuration.getQueryPeriodUnit()).isEqualTo(ChronoUnit.SECONDS);
        assertThat(configuration.getQueryPeriodValue()).isEqualTo(2);
    }

    /**
     * Verify the correct units are used for a value in minutes.
     */
    @Test
    public void testConstructor_minutesValue()
    {
        CalendarProperties calendarProperties = new CalendarProperties();
        calendarProperties.setUpdatePeriod(Duration.ofMinutes(1));
        calendarProperties.setQueryPeriod(Duration.ofMinutes(2));

        Configuration configuration = new Configuration(calendarProperties);

        assertThat(configuration.getUpdatePeriodUnit()).isEqualTo(ChronoUnit.MINUTES);
        assertThat(configuration.getUpdatePeriodValue()).isEqualTo(1);
        assertThat(configuration.getQueryPeriodUnit()).isEqualTo(ChronoUnit.MINUTES);
        assertThat(configuration.getQueryPeriodValue()).isEqualTo(2);
    }

    /**
     * Verify the correct units are used for a value in hours.
     */
    @Test
    public void testConstructor_hoursValue()
    {
        CalendarProperties calendarProperties = new CalendarProperties();
        calendarProperties.setUpdatePeriod(Duration.ofHours(1));
        calendarProperties.setQueryPeriod(Duration.ofHours(2));

        Configuration configuration = new Configuration(calendarProperties);

        assertThat(configuration.getUpdatePeriodUnit()).isEqualTo(ChronoUnit.HOURS);
        assertThat(configuration.getUpdatePeriodValue()).isEqualTo(1);
        assertThat(configuration.getQueryPeriodUnit()).isEqualTo(ChronoUnit.HOURS);
        assertThat(configuration.getQueryPeriodValue()).isEqualTo(2);
    }
}