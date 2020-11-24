package dev.kassie.calendar;

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
    private static final String TEXT_TO_REPLACE = "Registration will open 72 hours before class start time. Class size is ";
    private static final Pattern TEXT_TO_REPLACE_PATTERN = Pattern.compile(TEXT_TO_REPLACE, Pattern.LITERAL);
    private static final String NEW_TEXT = "Spots:";
    private final Calendar calendarClient;
    private final List<CalendarListEntry> calendars;
    private final long updatePeriodInMs;

    /**
     * Constructor.
     *
     * @param calendarClient   The client for accessing the Calendar API
     * @param calendars        The list of calendars which should be monitored.
     * @param updatePeriodInMs How far into the future to update events, e.g. a period of 3 days means that the event
     *                         updater will update events starting within 3 days.
     */
    public EventUpdater(Calendar calendarClient, List<CalendarListEntry> calendars, long updatePeriodInMs)
    {
        this.calendarClient = calendarClient;
        this.calendars = new ArrayList<>(calendars);
        this.updatePeriodInMs = updatePeriodInMs;
    }

    /**
     * Queries for all events happening within the next three days and updates the descriptions for any events that have
     * the text indicating the event is not yet open.
     */
    public void updateEvents()
    {
        try
        {
            // Request all events in the next 3 days from each calendar.
            long currentTimeMs = System.currentTimeMillis();
            DateTime now = new DateTime(currentTimeMs);
            DateTime endTime = new DateTime(currentTimeMs + updatePeriodInMs);

            for (CalendarListEntry calendar : calendars)
            {
                // The API doesn't support batch operations on different calenders in the same request, so a batch
                // request needs to be created for each calendar.
                BatchRequest batchRequest = calendarClient.batch();

                int eventsUpdatedForCurrentCalendar = 0;
                String calendarId = calendar.getId();
                String calendarName = calendar.getSummary();

                Events eventsResponse = calendarClient.events().list(calendarId)
                        .setTimeMin(now)
                        .setTimeMax(endTime)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();

                List<Event> events = eventsResponse.getItems();
                if (events.isEmpty())
                {
                    logger.info("No events found in the next 3 days for calendar {}.", calendarName);
                    continue;
                } else
                {
                    logger.trace("Found {} events in the next 3 days for calendar {}", events.size(), calendarName);
                }

                for (Event event : events)
                {
                    String eventTitle = event.getSummary();
                    DateTime eventStart = event.getStart().getDateTime();
                    String eventDescription = event.getDescription();
                    logger.trace("Checking if update needed for event {} ({}) on calendar {}", eventTitle, eventStart, calendarName);

                    if (eventDescription != null && eventDescription.contains(TEXT_TO_REPLACE))
                    {
                        String updatedDescription = TEXT_TO_REPLACE_PATTERN.matcher(eventDescription).replaceAll(Matcher.quoteReplacement(NEW_TEXT));
                        event.setDescription(updatedDescription);

                        logger.debug("Queuing update for event {} on calendar {}", eventTitle, calendarName);
                        calendarClient.events().update(calendarId, event.getId(), event).queue(batchRequest, BATCH_CALLBACK);
                        eventsUpdatedForCurrentCalendar++;
                    }
                }

                if (eventsUpdatedForCurrentCalendar == 0)
                {
                    logger.debug("No events updated in the next 3 days for calendar {}.", calendarName);
                } else
                {
                    logger.trace("{} events updated in the next 3 days for calendar {}.", eventsUpdatedForCurrentCalendar, calendarName);

                    // Only execute the batch request if there are events to be updated.
                    batchRequest.execute();
                }
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
            logger.info("Event edit was successful: {} ({})", event.getSummary(), event.getStart().getDateTime());
            // TODO: Anything else to do on success?
        }
    }
}
