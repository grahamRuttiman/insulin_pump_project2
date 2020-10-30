package insulinPumpController;

import input.*;

import static input.Switch.setValue;

public class Controller {

    private static final int safeMin = 6; // minimum safe blood sugar level
    private static final int safeMax = 14; // maximum safe blood sugar level
    private static Sensor sensor = new Sensor();
    private static int r2 = sensor.getReading(); // current sensor reading
    private static int r1 = safeMax; // previous sensor reading
    private static int r0 = safeMin; // previous to r1 sensor reading
    public static int compDose;
    public static SugarLevel sugarLevel;
    private static int minDose = 1; // minimum dose
    Reservior reservior = new Reservior();



    public static void compDose(){

        // SUGAR_LOW schema
        if (r2 < safeMin) {
            compDose = 0;
            sugarLevel = SugarLevel.LOW;
        }

        // SUGAR_OK schema
        else if (r2 >= safeMin && r2 <= safeMax) {
            // sugar level stable or falling
            if (r2 <= r1) {
                compDose = 0;
            }
            // sugar level increasing but rate of increase falling
            else if (r2 > r1 && (r2 - r1) < (r1 - r0)) {
                compDose = 0;
            }
            // sugar level increasing and rate of increase increasing compute dose
            // a minimum dose must be delivered if rounded to zero
            else if (r2 > r1 && (r2 - r1) >= (r1 - r0) && (r2 - r1) == 0) {
                compDose = minDose;
            } else if (r2 > r1 && (r2 - r1) >= (r1 - r0) && (r2 - r1) > 0) {
                compDose = (r2 - r1) / 4;
            }
        }

        // SUGAR_HIGH schema
        else if (r2 > safeMax){
            // sugar level increasing. Round down if below 1 unit.
            if (r2 > r1 && (r2 - r1) / 4 == 0){
                compDose = minDose;
            }else if (r2 > r1 && (r2 - r1) / 4 > 0){
                compDose = (r2 - r1) / 4;
            }
            // sugar level stable
            else if (r2 == r1){
                compDose = minDose;
            }
            // sugar level falling and rate of decrease increasing
            else if (r2<r1 && (r2 - r1) > (r1 - r0)){
                compDose = minDose;
            }
        }

    }

    public static void main(String[] args) {

        Compdose compdose = new Compdose(r0, r1, r2);
        int computedDose = 0;


        final int max_single_dose = 4; // maximum amount of single dose
        final int max_daily_dose = 25; // maximum amount of daily dose
        final int minimum_dose = 1; // minimum dose
        int cumulative_dose = 0; // total dose in last 24 hours
        int dose = 0;

        State insulinPump = State.RUN; // mode status
        AlarmValue alarm; // alarm status
        String display1;
        SwitchValue switchValue = setValue(SwitchValue.OFF);
        InsulinReservoir insulinReservoir = InsulinReservoir.NOT_PRESENT;
        InsulinLevel insulinLevel = InsulinLevel.NOT_OK;
        HardwareTest hardwareTest = HardwareTest.OK;
        Needle needle = Needle.NOT_PRESENT;

        // STARTUP
        if (switchValue == SwitchValue.OFF || switchValue == SwitchValue.AUTO){
            dose = 0;
            r0 = safeMin;
            r1 = safeMax;
            // RUN TEST
        }

        // RESET
        while (insulinReservoir == InsulinReservoir.NOT_PRESENT){
            if (insulinReservoir == InsulinReservoir.PRESENT){
                insulin_available = capacity;
                insulinLevel = InsulinLevel.OK;
                // RUN TEST
                break;
            }
        }

        // TEST
        if (hardwareTest == HardwareTest.OK && needle == Needle.PRESENT && insulinReservoir == InsulinReservoir.PRESENT){
            insulinPump = State.RUN;
            alarm = AlarmValue.OFF;
            display1 = "";
        }
        while (insulinPump == State.TEST){
            alarm = AlarmValue.ON;
            if (needle == Needle.NOT_PRESENT){
                display1 = "No needle unit";
            }
            else if (insulinReservoir == InsulinReservoir.NOT_PRESENT || insulin_available < max_single_dose){
                display1 = "No insulin";
            }
            else if (hardwareTest == HardwareTest.BATTERYLOW){
                display1 = "Battery low";
            }
            else if (hardwareTest == HardwareTest.PUMPFAIL){
                display1 = "Pump failure";
            }
            else if (hardwareTest == HardwareTest.SENSORFAIL){
                display1 = "Sensor failure";
            }
            else if (hardwareTest == HardwareTest.DELIVERYFAIL){
                display1 = "Needle failure";
            }
            else if (hardwareTest == HardwareTest.OK && needle == Needle.PRESENT && insulinReservoir == InsulinReservoir.PRESENT){
                insulinPump = State.RUN;
                alarm = AlarmValue.OFF;
                display1 = "";
            }
        }

        // RUN
        while (switchValue == SwitchValue.AUTO && (insulinPump ==  State.RUN || insulinPump == State.RUN)
                && insulin_available >= max_single_dose && cumulative_dose < max_daily_dose){
            // If the computed insulin dose is zero, don’t deliver any insulin
            if (computedDose == 0){
                dose = 0;
            }
            // The maximum daily dose would be exceeded if the computed dose was delivered
            else if(computedDose + cumulative_dose > max_daily_dose){
                alarm = AlarmValue.ON;
                insulinPump = State.TEST;
                dose = max_daily_dose - cumulative_dose;
            }
            // The normal situation. If maximum single dose is not exceeded then deliver computed dose
            else if ((computedDose + cumulative_dose) < max_daily_dose && computedDose <= max_single_dose){
                dose = computedDose;
            }
            // The single dose computed is too high. Restrict the dose delivered to the maximum single dose
            else if (computedDose > max_single_dose){
                dose = max_single_dose;
            }

            insulin_available = insulin_available - dose;
            cumulative_dose = cumulative_dose + dose;

            if (insulin_available <= (max_single_dose * 4)){
                insulinPump = State.TEST;
                display1 = "Insulin low";
            }

            r1 = r2;
            r0 = r1;
        }

        // MANUAL
        if (switchValue == SwitchValue.MANUAL){
            display1 = "Manual Override";
            // dose = read manualDeliveryButton;
            // do something with the reading to convert to dose
            cumulative_dose = cumulative_dose + dose;
            insulin_available = insulin_available - dose;
        }

    }
}
