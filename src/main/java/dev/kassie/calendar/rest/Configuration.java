package dev.kassie.calendar.rest;

import dev.kassie.calendar.CalendarProperties;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Data Transfer Object (DTO) for providing configuration information to the web client.
 *
 * @author kbowman
 * @since 0.0.2
 */
public class Configuration
{
    /**
     * The original text in an event before it has been updated.
     */
    private String originalText;

    /**
     * The replacement text in an event after it has been updated.
     */
    private String replacementText;

    /**
     * The value for the time prior to the start of the event that the event text should be updated.
     */
    private long updatePeriodValue;

    /**
     * The unit for the time prior to the start of the event that the event text should be updated. This should be one
     * of the following values:
     * <ul>
     *     <li>{@link ChronoUnit#MILLIS}</li>
     *     <li>{@link ChronoUnit#SECONDS}</li>
     *     <li>{@link ChronoUnit#MINUTES}</li>
     *     <li>{@link ChronoUnit#HOURS}</li>
     * </ul>
     */
    private ChronoUnit updatePeriodUnit;

    /**
     * The value for the amount of time between querying the calendar(s).
     */
    private long queryPeriodValue;

    /**
     * The unit for the amount of time between querying the calendar(s). This should be one of the following values:
     * <ul>
     *     <li>{@link ChronoUnit#MILLIS}</li>
     *     <li>{@link ChronoUnit#SECONDS}</li>
     *     <li>{@link ChronoUnit#MINUTES}</li>
     *     <li>{@link ChronoUnit#HOURS}</li>
     * </ul>
     */
    private ChronoUnit queryPeriodUnit;

    /**
     * No-arg constructor.
     */
    public Configuration()
    {
    }

    /**
     * Constructor to create the web DTO fom the internal calendar properties.
     *
     * @param calendarProperties The internal calendar properties
     */
    public Configuration(CalendarProperties calendarProperties)
    {
        originalText = calendarProperties.getOriginalText();
        replacementText = calendarProperties.getReplacementText();

        Duration updatePeriod = calendarProperties.getUpdatePeriod();
        updatePeriodUnit = determineBestUnit(updatePeriod);
        updatePeriodValue = getValueForUnit(updatePeriod, updatePeriodUnit);

        Duration queryPeriod = calendarProperties.getQueryPeriod();
        queryPeriodUnit = determineBestUnit(queryPeriod);
        queryPeriodValue = getValueForUnit(queryPeriod, queryPeriodUnit);
    }

    /**
     * Determines the largest unit that can be used without truncating the data. For example, if the duration is
     * 259200 seconds, the returned unit would be hours, since this number of seconds is equivalent to 72 hours.
     *
     * @param duration The duration for which to find a unit
     * @return The largest unit without loss of data.
     */
    private ChronoUnit determineBestUnit(Duration duration)
    {
        int millisPart = duration.toMillisPart();
        if (millisPart != 0) return ChronoUnit.MILLIS;

        int secondsPart = duration.toSecondsPart();
        if (secondsPart != 0) return ChronoUnit.SECONDS;

        int minutesPart = duration.toMinutesPart();
        if (minutesPart != 0) return ChronoUnit.MINUTES;

        // Hours is the largest unit for duration, since it is a time-based measurement. If all the other values are 0,
        // this value can safely be represented as hours.
        return ChronoUnit.HOURS;
    }

    /**
     * Gets the value for the duration using the provided unit.
     *
     * @param duration The duration from which to get the value
     * @param unit     The unit to use
     * @return The value of the duration in the provided unit.
     */
    private long getValueForUnit(Duration duration, ChronoUnit unit)
    {
        switch (unit)
        {
            case MILLIS:
                return duration.toMillis();
            case SECONDS:
                return duration.toSeconds();
            case MINUTES:
                return duration.toMinutes();
            case HOURS:
            default:
                return duration.toHours();
        }
    }

    public String getOriginalText()
    {
        return originalText;
    }

    public void setOriginalText(String originalText)
    {
        this.originalText = originalText;
    }

    public String getReplacementText()
    {
        return replacementText;
    }

    public void setReplacementText(String replacementText)
    {
        this.replacementText = replacementText;
    }

    public long getUpdatePeriodValue()
    {
        return updatePeriodValue;
    }

    public void setUpdatePeriodValue(long updatePeriodValue)
    {
        this.updatePeriodValue = updatePeriodValue;
    }

    public ChronoUnit getUpdatePeriodUnit()
    {
        return updatePeriodUnit;
    }

    public void setUpdatePeriodUnit(ChronoUnit updatePeriodUnit)
    {
        this.updatePeriodUnit = updatePeriodUnit;
    }

    public long getQueryPeriodValue()
    {
        return queryPeriodValue;
    }

    public void setQueryPeriodValue(long queryPeriodValue)
    {
        this.queryPeriodValue = queryPeriodValue;
    }

    public ChronoUnit getQueryPeriodUnit()
    {
        return queryPeriodUnit;
    }

    public void setQueryPeriodUnit(ChronoUnit queryPeriodUnit)
    {
        this.queryPeriodUnit = queryPeriodUnit;
    }
}
