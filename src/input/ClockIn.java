package input;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockIn {

    public static void main(String[] argv) throws Exception {
        Date dt = new Date();
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("kk:mm:ss");
        System.out.println("Time in 24 hr format = " + dateFormat.format(dt));
    }
}

