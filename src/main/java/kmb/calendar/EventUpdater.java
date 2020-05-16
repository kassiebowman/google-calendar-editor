package kmb.calendar;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for communicating with Google Calendars API to update calendar events.
 *
 * @author kbowman
 * @since 1.0.0
 */
public class EventUpdater
{
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final BatchCallback BATCH_CALLBACK = new BatchCallback();
    private static final long THREE_DAYS_IN_MS = TimeUnit.DAYS.toMillis(3);
    private static final String TEXT_TO_REPLACE = "Registration will open 72 hours before class start time. Class size is ";
    private static final Pattern TEXT_TO_REPLACE_PATTERN = Pattern.compile(TEXT_TO_REPLACE, Pattern.LITERAL);
    private static final String NEW_TEXT = "Spots:";
    private final Calendar calendarClient;
    private final List<CalendarListEntry> calendars;

    /**
     * Constructor.
     *
     * @param calendarClient The client for accessing the Calendar API
     * @param calendars      The list of calendars which should be monitored.
     */
    public EventUpdater(Calendar calendarClient, List<CalendarListEntry> calendars)
    {
        this.calendarClient = calendarClient;
        this.calendars = new ArrayList<>(calendars);
    }

    /**
     * Queries for all events happening within the next three days and updates the descriptions for any events that have
     * the text indicating the event is not yet open.
     */
    public void updateEvents()
    {
        BatchRequest batchRequest = calendarClient.batch();
        try
        {
            // Request all events in the next 3 days from each calendar.
            long currentTimeMs = System.currentTimeMillis();
            DateTime now = new DateTime(currentTimeMs);
            DateTime threeDays = new DateTime(currentTimeMs + THREE_DAYS_IN_MS);

            boolean eventsUpdated = false;

            for (CalendarListEntry calendar : calendars)
            {
                String calendarId = calendar.getId();
                String calendarName = calendar.getSummary();

                Events eventsResponse = calendarClient.events().list(calendarId)
                        .setTimeMin(now)
                        .setTimeMax(threeDays)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();

                List<Event> events = eventsResponse.getItems();
                if (events.isEmpty())
                {
                    logger.info("No events found in the next 3 days for calendar {}.", calendarName);
                    continue;
                }

                for (Event event : events)
                {
                    String eventTitle = event.getSummary();
                    String eventDescription = event.getDescription();

                    if (eventDescription != null && eventDescription.contains(TEXT_TO_REPLACE))
                    {
                        String updatedDescription = TEXT_TO_REPLACE_PATTERN.matcher(eventDescription).replaceAll(Matcher.quoteReplacement(NEW_TEXT));
                        event.setDescription(updatedDescription);

                        logger.info("Queuing update for event {} on calendar {}", eventTitle, calendarName);
                        calendarClient.events().update(calendarId, event.getId(), event).queue(batchRequest, BATCH_CALLBACK);
                        eventsUpdated = true;
                    }
                }
            }

            // Only execute the batch request if there are events to be updated.
            if (eventsUpdated)
            {
                batchRequest.execute();
            }
        } catch (IOException e)
        {
            logger.error("Error querying or updating events", e);
        }
    }

    /**
     * Callback for handling success/failure of batch requests.
     */
    private static class BatchCallback extends JsonBatchCallback<Event>
    {
        @Override
        public void onFailure(GoogleJsonError error, HttpHeaders responseHeaders)
        {
            logger.error("Error during event edit: {}", error.getMessage());
        }

        @Override
        public void onSuccess(Event event, HttpHeaders responseHeaders)
        {
            logger.info("Event edit was successful: {}", event.getSummary());
            // TODO: Anything else to do on success?
        }
    }
}
