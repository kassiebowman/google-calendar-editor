package kmb.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication
@RestController
public class MainApplication
{
    public static void main(String[] args) throws IOException, GeneralSecurityException
    {
        SpringApplication.run(MainApplication.class, args);

        CalendarEditor.start(args);
    }

    @GetMapping("/")
    public String hello()
    {
        return "Hello world!";
    }
}
