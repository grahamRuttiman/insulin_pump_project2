package input;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Clock {

    public String getTimeS(){
        return (LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    public String getTimeNoS(){
        return (LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}
