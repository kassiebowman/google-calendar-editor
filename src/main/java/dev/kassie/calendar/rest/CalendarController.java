package dev.kassie.calendar.rest;

import com.google.api.services.calendar.model.CalendarListEntry;
import dev.kassie.calendar.CalendarManager;
import dev.kassie.calendar.CalendarProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST endpoint for controlling the calendars to be monitored by the application.
 *
 * @author kbowman
 * @since 0.0.2
 */
@RestController
public class CalendarController
{
    private final CalendarManager calendarManager;
    private final CalendarProperties calendarProperties;

    @Autowired
    public CalendarController(CalendarManager calendarManager, CalendarProperties calendarProperties)
    {
        this.calendarManager = calendarManager;
        this.calendarProperties = calendarProperties;
    }

    /**
     * @return The names of all calendars available for the user.
     */
    @GetMapping("/calendars/available")
    List<String> getCalendars()
    {
        return calendarManager.getAvailableCalendars().stream()
                .map(CalendarListEntry::getSummary)
                .collect(Collectors.toList());
    }

    /**
     * @return The names of the calendars the user has selected to monitor and update.
     */
    @GetMapping("/calendars")
    List<String> getSelectedCalendars()
    {
        return calendarProperties.getCalendarNames();
    }

    /**
     * Add a calendar for monitoring by the application.
     *
     * @param calendarName The name to add
     */
    @PostMapping("/calendars")
    void addSelectedCalendar(@RequestBody String calendarName)
    {
        calendarProperties.addCalendarName(calendarName);
    }

    /**
     * Remove a calendar from the list of calendars to be monitored by the application.
     *
     * @param name The name to remove
     */
    @DeleteMapping("/calendars/{name}")
    void removeSelectedCalendar(@PathVariable String name)
    {
        calendarProperties.removeCalendarName(name);
    }
}
