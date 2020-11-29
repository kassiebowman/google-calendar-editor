package dev.kassie.calendar;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The configuration properties for the application.
 *
 * @author kbowman
 * @since 0.0.2
 */
@ConfigurationProperties(prefix = "calendar")
public class CalendarProperties
{
    /**
     * The name(s) of calendars to monitor and edit.
     */
    private List<String> calendarNames = new ArrayList<>();

    /**
     * The original text in an event before it has been updated. This text will be replaced with {@link #replacementText}
     * at a set time before the event starts (controlled by {@link #updatePeriod}).
     */
    private String originalText = "Registration will open 72 hours before class start time. Class size is ";

    /**
     * The replacement text in an event after it has been updated. This text will replace {@link #originalText} at a set
     * time before the event starts (controlled by {@link #updatePeriod}).
     */
    private String replacementText = "Spots:";

    /**
     * The time prior to the start of the event that the event text should be updated.
     */
    private Duration updatePeriod = Duration.ofDays(3);

    /**
     * The amount of time between querying the calendar(s) and updating any events that are within their
     * {@link #updatePeriod}.
     */
    private Duration queryPeriod = Duration.ofMinutes(1);

    public void addCalendarName(String calendarName)
    {
        calendarNames.add(calendarName);
        // TODO: Fire event for changes
    }

    public void removeCalendarName(String calendarName)
    {
        calendarNames.remove(calendarName);
    }

    public List<String> getCalendarNames()
    {
        return Collections.unmodifiableList(calendarNames);
    }

    public void setCalendarNames(List<String> calendarNames)
    {
        this.calendarNames = new ArrayList<>();
        this.calendarNames.addAll(calendarNames);
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

    public Duration getUpdatePeriod()
    {
        return updatePeriod;
    }

    public void setUpdatePeriod(Duration updatePeriod)
    {
        this.updatePeriod = updatePeriod;
    }

    public Duration getQueryPeriod()
    {
        return queryPeriod;
    }

    public void setQueryPeriod(Duration queryPeriod)
    {
        this.queryPeriod = queryPeriod;
    }
}
