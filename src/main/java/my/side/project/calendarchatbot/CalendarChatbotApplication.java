package my.side.project.calendarchatbot;

import static my.side.project.calendarchatbot.utils.LogLevel.INFO;

import com.google.cloud.firestore.Firestore;
import my.side.project.calendarchatbot.datastores.EventDataStore;
import my.side.project.calendarchatbot.datastores.EventFirestore;
import my.side.project.calendarchatbot.datastores.FirestoreFactory;
import my.side.project.calendarchatbot.exceptions.EventNotFoundException;
import my.side.project.calendarchatbot.modelmappers.Mapper;
import my.side.project.calendarchatbot.models.Event;
import my.side.project.calendarchatbot.services.EventService;
import my.side.project.calendarchatbot.utils.Output;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class CalendarChatbotApplication {

    private static final Output OUTPUT = Output.getOutput(CalendarChatbotApplication.class.getName());

    @Autowired
    EventService eventService;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CalendarChatbotApplication.class, args);

        Firestore db = FirestoreFactory.create();
        EventDataStore eventDataStore = new EventFirestore(db, new Mapper());
    }

    @GetMapping("/")
    public String hello(@RequestParam(value = "name", defaultValue = "world") String name) throws Exception {
        OUTPUT.print(INFO, "Received request with param: name={}", name);
        return String.format("Hello %s!", name);
    }

    @GetMapping("/events")
    public List<Event> queryEvents(@RequestParam(value = "userId", defaultValue = "") String userId) throws Exception {
        String now = DateTime.now().toString(ISODateTimeFormat.dateTime());
        return eventService.queryByUserAndStartTime(userId, now, 5);
    }

    @GetMapping("/events/{id}")
    public Event get(@PathVariable String id) throws Exception {
        Event event = eventService.get(id);
        if (event == null)
            throw new EventNotFoundException();
        return event;
    }
}
