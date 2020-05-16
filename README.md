Google Calendar Editor
======================

Looks for events up to 3 days in the future and modifies the events.

Setup instructions
------------------
1. Follow the instructions in the [Google Calendar API Java Quickstart](https://developers.google.com/calendar/quickstart/java)
   to enable the calendar API and generate the credentials.json file.
2. Copy the credentials.json files into the src/main/resources directory.
3. Update the calendar names in the src/main/resources/calendarNames.csv to be a comma-separated list of the names of 
   the calendars you want to update.
4. Execute `gradlew run` on the command line. The first time, it will open a browser and prompt you for access to your
   Google calendar. The application log an error the first time because of this; just run it again.
5. Check the logs for the application output.