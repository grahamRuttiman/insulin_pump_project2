package input;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Clock {

    public String getTime(){
        return (LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
}
