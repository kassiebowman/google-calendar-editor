package kmb.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main class for communicating with Google Calendars API to edit calendar entries
 *
 * @author kbowman
 * @since 1.0.0
 */
public class CalendarEditor
{
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String APPLICATION_NAME = "Google Calendar Editor";
    private static final BatchCallback BATCH_CALLBACK = new BatchCallback();
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final long THREE_DAYS_IN_MS = TimeUnit.DAYS.toMillis(3);

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final String TEXT_TO_REPLACE = "Registration will open 72 hours before class start time. Class size is ";
    private static final String NEW_TEXT = "Spots:";
    private static final String CALENDAR_ID = "primary";

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException
    {
        // Load client secrets.
        InputStream in = CalendarEditor.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null)
        {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException
    {
        // Build a new authorized API client service.
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Calendar calendarClient = new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME)
                .build();

        BatchRequest batchRequest = calendarClient.batch();

        executorService.scheduleAtFixedRate(() -> {
            logger.info("Running update thread.");
            try
            {
                // Request all events in the next 3 days from the primary calendar.
                long currentTimeMs = System.currentTimeMillis();
                DateTime now = new DateTime(currentTimeMs);
                DateTime threeDays = new DateTime(currentTimeMs + THREE_DAYS_IN_MS);

                Events events = calendarClient.events().list(CALENDAR_ID)
                        .setTimeMin(now)
                        .setTimeMax(threeDays)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();

                List<Event> eventList = events.getItems();
                if (eventList.isEmpty())
                {
                    logger.info("No events found in the next 3 days.");
                } else
                {
                    boolean eventsUpdated = false;

                    for (Event event : eventList)
                    {
                        String eventTitle = event.getSummary();
                        String eventDescription = event.getDescription();

                        if (eventDescription != null && eventDescription.contains(TEXT_TO_REPLACE))
                        {
                            String updatedDescription = eventDescription.replace(TEXT_TO_REPLACE, NEW_TEXT);
                            event.setDescription(updatedDescription);

                            logger.info("Queuing update for event {}", eventTitle);
                            calendarClient.events().update(CALENDAR_ID, event.getId(), event).queue(batchRequest, BATCH_CALLBACK);
                            eventsUpdated = true;
                        }
                    }

                    // Only execute the batch request if there were events to be updated.
                    if (eventsUpdated)
                    {
                        batchRequest.execute();
                    }
                }
            } catch (IOException e)
            {
                logger.error("Error querying or updating events", e);
            }
        }, 0, 1, TimeUnit.MINUTES); // TODO: Change the unit to hours after testing
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