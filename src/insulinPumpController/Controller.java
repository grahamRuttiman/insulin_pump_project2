package insulinPumpController;

import input.*;

import static input.HardwareTest.OK;
import static input.InsulinReservoir.NOT_PRESENT;
import static input.Needle.NEEDLE_NOT_PRESENT;
import static insulinPumpController.AlarmValue.ALARM_OFF;
import static insulinPumpController.AlarmValue.ALARM_ON;
import static insulinPumpController.InsulinLevel.NOT_OK;
import static insulinPumpController.Status.setStatus;
import static insulinPumpController.StatusValue.RUNNING;
import static insulinPumpController.StatusValue.WARNING;
import static insulinPumpController.SwitchValue.OFF;
import static output.Alarm.setAlarm;
import static output.Display1.setDisplay1;

public class Controller {

    public int r2 = 0; // current sensor reading
    public int r1 = 0; // previous sensor reading
    public int r0 = 0; // previous to r1 sensor reading

    public Controller () {

        int sample_reading = 8;
        Sensor sensor = new Sensor(sample_reading);
        int sensor_reading = sensor.getReading();
        final int capacity = 100; // capacity of insulin reservoir in mL
        int insulin_available = 100; // insulin reservoir level
        final int max_single_dose = 4; // maximum amount of single dose
        final int max_daily_dose = 25; // maximum amount of daily dose
        final int minimum_dose = 1; // minimum dose
        final int safemin = 6; // minimum safe blood sugar level
        final int safemax = 14; // maximum safe blood sugar level
        int cumulative_dose = 0; // total dose in last 24 hours
        int dose = 0;
        StatusValue statusValue = RUNNING; // mode status
        AlarmValue alarm; // alarm status
        String display1;
        Switch switchMode = null;
        switchMode.setValue(OFF);
        SwitchValue switchValue = switchMode.getValue();
        InsulinReservoir insulinReservoir = InsulinReservoir.NOT_PRESENT;
        InsulinLevel insulinLevel = NOT_OK;
        HardwareTest hardwareTest = OK;
        Needle needle = NEEDLE_NOT_PRESENT;
        int computedDose = Compdose();

        // STARTUP
        if (switchValue == OFF || switchValue == SwitchValue.AUTO) {
            dose = 0;
            r0 = safemin;
            r1 = safemax;
            // RUN TEST
        }

        // RESET
        while (insulinReservoir == NOT_PRESENT) {
            if (insulinReservoir == InsulinReservoir.PRESENT) {
                insulin_available = capacity;
                insulinLevel = InsulinLevel.OK;
                // RUN TEST
                break;
            }
        }

        // TEST
        if (hardwareTest == OK && needle == NEEDLE_NOT_PRESENT && insulinReservoir == InsulinReservoir.PRESENT) {
//            statusValue = RUNNING;
            setStatus(RUNNING);
//            alarm = AlarmValue.OFF;
            setAlarm(ALARM_OFF);
//            display1 = "";
            setDisplay1("");
        }
        while (statusValue == StatusValue.ERROR) {
//            alarm = AlarmValue.ON;
            setAlarm(ALARM_ON);
            if (needle == NEEDLE_NOT_PRESENT) {
//                display1 = "No needle unit";
                setDisplay1("No needle unit");
            } else if (insulinReservoir == NOT_PRESENT || insulin_available < max_single_dose) {
//                display1 = "No insulin";
                setDisplay1("No insulin");
            } else if (hardwareTest == HardwareTest.BATTERYLOW) {
//                display1 = "Battery low";
                setDisplay1("Battery low");
            } else if (hardwareTest == HardwareTest.PUMPFAIL) {
//                display1 = "Pump failure";
                setDisplay1("Pump failure");
            } else if (hardwareTest == HardwareTest.SENSORFAIL) {
//                display1 = "Sensor failure";
                setDisplay1("Sensor failure");
            } else if (hardwareTest == HardwareTest.DELIVERYFAIL) {
//                display1 = "Needle failure";
                setDisplay1("Needle failure");
            } else if (hardwareTest == OK && needle == NEEDLE_NOT_PRESENT && insulinReservoir == InsulinReservoir.PRESENT) {
//                statusValue = RUNNING;
                setStatus(RUNNING);
//                alarm = AlarmValue.OFF;
                setAlarm(ALARM_OFF);
//                display1 = "";
                setDisplay1("");
            }
        }

        // RUN
        while (switchValue == SwitchValue.AUTO && (statusValue == RUNNING || statusValue == StatusValue.WARNING)
                && insulin_available >= max_single_dose && cumulative_dose < max_daily_dose) {
            // If the computed insulin dose is zero, don’t deliver any insulin
            if (computedDose == 0) {
                dose = 0;
            }
            // The maximum daily dose would be exceeded if the computed dose was delivered
            else if (computedDose + cumulative_dose > max_daily_dose) {
//                alarm = AlarmValue.ON;
                setAlarm(ALARM_ON);
//                statusValue = StatusValue.WARNING;
                setStatus(WARNING);
                dose = max_daily_dose - cumulative_dose;
            }
            // The normal situation. If maximum single dose is not exceeded then deliver computed dose
            else if ((computedDose + cumulative_dose) < max_daily_dose && computedDose <= max_single_dose) {
                dose = computedDose;
            }
            // The single dose computed is too high. Restrict the dose delivered to the maximum single dose
            else if (computedDose > max_single_dose) {
                dose = max_single_dose;
            }

            insulin_available = insulin_available - dose;
            cumulative_dose = cumulative_dose + dose;

            if (insulin_available <= (max_single_dose * 4)) {
                setStatus(WARNING);
                setDisplay1("Insulin low");
//                statusValue = StatusValue.WARNING;
//                display1 = "Insulin low";
            }

            r1 = r2;
            r0 = r1;
        }

        // MANUAL
        if (switchValue == SwitchValue.MANUAL) {
            display1 = "Manual Override";
            // dose = read manualDeliveryButton;
            // do something with the reading to convert to dose
            cumulative_dose = cumulative_dose + dose;
            insulin_available = insulin_available - dose;
        }

    }

    public int Compdose(){
        int comp_dose = 0;
        int minimum_dose = 1; // minimum dose
        int safemin = 6; // minimum safe blood sugar level
        int safemax = 14; // maximum safe blood sugar level

        // SUGAR_LOW schema
        if (r2 < safemin) {
            comp_dose = 0;
//            alarm = AlarmValue.ON;
//            status = Status.WARNING;
//            display1 = "Sugar Low";
        }

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

        return comp_dose;
    }


}
