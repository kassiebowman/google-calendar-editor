package dev.kassie.calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Responsible for selecting the calendars which will be monitored by the application.
 *
 * @author kbowman
 * @since 0.0.1
 */
@Component
public class CalendarManager
{
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final List<CalendarListEntry> selectedCalendars;
    private final Calendar calendarClient;

    /**
     * Constructor.
     *
     * @param calendarProperties The application properties
     * @param calendarClient     Client for interacting with the Google Calendar API
     */
    @Autowired
    public CalendarManager(CalendarProperties calendarProperties, Calendar calendarClient)
    {
        this.calendarClient = calendarClient;
        List<String> calendarNames = calendarProperties.getCalendarNames();

        List<CalendarListEntry> availableCalendars = getAvailableCalendars();

        selectedCalendars = getCalendarsForNames(calendarNames, availableCalendars);
    }

    /**
     * Queries the API for the available calendars.
     *
     * @return The list of available calendars or an empty list if an error occurred.
     */
    public List<CalendarListEntry> getAvailableCalendars()
    {
        try
        {
            CalendarList calendarListResponse = calendarClient.calendarList().list().execute();
            return calendarListResponse.getItems();
        } catch (IOException e)
        {
            logger.error("Error querying for calendars", e);
        }

        return Collections.emptyList();
    }

    /**
     * Attempts to find the corresponding calendar for each provided calendar name.
     *
     * @param calendarNames      The names of the calendars to find
     * @param availableCalendars The list of available calendars
     * @return The list of all calendars that were found.
     */
    private List<CalendarListEntry> getCalendarsForNames(List<String> calendarNames, List<CalendarListEntry> availableCalendars)
    {
        List<CalendarListEntry> calendars = new ArrayList<>();

        Map<String, CalendarListEntry> nameToCalendarMap = availableCalendars.stream()
                .collect(Collectors.toMap(CalendarListEntry::getSummary, calendar -> calendar));

        for (String calendarName : calendarNames)
        {
            CalendarListEntry calendar = nameToCalendarMap.get(calendarName);

            if (calendar == null)
            {
                logger.warn("No calendar found with the name `{}`", calendarName);
                continue;
            }

            calendars.add(calendar);
        }

        return calendars;
    }

    /**
     * @return The list of calendar which were selected.
     */
    public List<CalendarListEntry> getSelectedCalendars()
    {
        return Collections.unmodifiableList(selectedCalendars);
    }
}
