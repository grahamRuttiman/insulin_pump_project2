package insulinPumpController;

public class Compdose {

    //Enum status; // controller status

    // get switch value switch.getStatus
// example
//    SwitchValue switchValue; // switch status
    State insulinPump; // mode status
    AlarmValue alarm; // alarm status
    int r2 = 0; // current sensor reading
    int r1 = 0; // previous sensor reading
    int r0 = 0; // previous to r1 sensor reading
//    int capacity = 100; // capacity of insulin reservoir in mL
//    int insulin_available = 100; // insulin reservoir level
//    int max_single_dose = 4; // maximum amount of single dose
//    int max_daily_dose = 25; // maximum amount of daily dose
    int minimum_dose = 1; // minimum dose
    int safemin = 6; // minimum safe blood sugar level
    int safemax = 14; // maximum safe blood sugar level
//    int cumulative_dose = 0; // total dose in last 24 hours
//    int sensorReading;
    int comp_dose = 0;
    String display1;


    public Compdose(int v0, int v1, int v2) {

        this.r0 = v0;
        this.r1 = v1;
        this.r2 = v2;

<<<<<<< Updated upstream

=======
    public int calculate() {
        // SUGAR_LOW schema
        if (r2 < safemin) {
            comp_dose = 0;
//            alarm = ON;
            setAlarm(ALARM_ON);
//            statusValue = StatusValue.WARNING;
            setStatus(WARNING);
//            display1 = "Sugar Low";
            setDisplay1("Sugar Low");
        }
>>>>>>> Stashed changes

        // SUGAR_OK schema
        else if (r2 >= safemin && r2 <= safemax) {
            // sugar level stable or falling
            if (r2 <= r1) {
                comp_dose = 0;
            }
            // sugar level increasing but rate of increase falling
            else if (r2 > r1 && (r2 - r1) < (r1 - r0)) {
                comp_dose = 0;
            }
            // sugar level increasing and rate of increase increasing compute dose
            // a minimum dose must be delivered if rounded to zero
            else if (r2 > r1 && (r2 - r1) >= (r1 - r0) && (r2 - r1) == 0) {
                comp_dose = minimum_dose;
            } else if (r2 > r1 && (r2 - r1) >= (r1 - r0) && (r2 - r1) > 0) {
                comp_dose = (r2 - r1) / 4;
            }
        }

        // SUGAR_HIGH schema
        else if (r2 > safemax){
            // sugar level increasing. Round down if below 1 unit.
            if (r2 > r1 && (r2 - r1) / 4 == 0){
                comp_dose = minimum_dose;
            }else if (r2 > r1 && (r2 - r1) / 4 > 0){
                comp_dose = (r2 - r1) / 4;
            }
            // sugar level stable
            else if (r2 == r1){
                comp_dose = minimum_dose;
            }
            // sugar level falling and rate of decrease increasing
            else if (r2<r1 && (r2 - r1) > (r1 - r0)){
                comp_dose = minimum_dose;
            }
        }



        // Set Alarm
        setAlarm(alarm);
        // set status
        setStatus(insulinPump);
        // set display1
        setDisplay1(display1);
    }

    public void setAlarm(AlarmValue alarm) {
        this.alarm = alarm;
    }

    public AlarmValue getAlarm() {
        return alarm;
    }

    public void setStatus(State insulinPump) {
        this.insulinPump = insulinPump;
    }

    public State getStatus() {
        return insulinPump;
    }

    public void setDisplay1(String display1) {
        this.display1 = display1;
    }

    public String getDisplay1() {
        return display1;
    }

}
