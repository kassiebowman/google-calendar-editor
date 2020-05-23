package kmb.calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Responsible for selecting the calendars which will be monitored by the application.
 *
 * @author kbowman
 * @since 1.0.0
 */
public class CalendarSelector
{
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final String DEFAULT_CALENDAR_FILE_NAME = "calendarNames.csv";
    private final List<CalendarListEntry> calendars;

    /**
     * Constructor.
     *
     * @param calendarClient The client for accessing the Calendar API
     * @param args           The command line args for the application
     */
    public CalendarSelector(Calendar calendarClient, String[] args)
    {
        // TODO: Handle command line args to allow the user to specify a file name for the calendar names
        //  If the args are unexpected, print out info to let the user name what is expected.
        URL calendarFileUrl = getClass().getClassLoader().getResource(DEFAULT_CALENDAR_FILE_NAME);

        List<String> calendarNames = parseFileForCalendarNames(calendarFileUrl.getFile());

        List<CalendarListEntry> availableCalendars = queryForCalendars(calendarClient);

        calendars = getCalendarsForNames(calendarNames, availableCalendars);
    }

    /**
     * Parses the calendar file for the list of calendar names.
     *
     * @param calendarFileName The name of the file containing a comma-separated list of calendar names
     * @return The list of calendar names or an empty list if an error occurred.
     */
    private List<String> parseFileForCalendarNames(String calendarFileName)
    {
        List<String> calendarNames = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(calendarFileName)))
        {
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                String[] values = line.split(",");
                calendarNames.addAll(Arrays.asList(values));
            }
        } catch (IOException e)
        {
            logger.log(Level.SEVERE, "Error reading from calendar file " + calendarFileName, e);
        }

        return calendarNames;
    }

    /**
     * Queries the API for the available calendars.
     *
     * @param calendarClient The client for accessing the Calendar API
     * @return The list of available calendars or an empty list if an error occurred.
     */
    private List<CalendarListEntry> queryForCalendars(Calendar calendarClient)
    {
        try
        {
            CalendarList calendarListResponse = calendarClient.calendarList().list().execute();
            return calendarListResponse.getItems();
        } catch (IOException e)
        {
            logger.log(Level.SEVERE, "Error querying for calendars", e);
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
                logger.warning("No calendar found with the name `" + calendarName + "`");
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
        return Collections.unmodifiableList(calendars);
    }
}
