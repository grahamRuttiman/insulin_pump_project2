package insulinPumpController;

import input.*;

import static input.Switch.setValue;

public class Controller {

    public static void main(String[] args) {

        int r2 = 0; // current sensor reading
        int r1 = 0; // previous sensor reading
        int r0 = 0; // previous to r1 sensor readi
        int sample_reading = 8;
        Sensor sensor = new Sensor(sample_reading);
        int sensor_reading = sensor.getReading();
        Compdose compdose = new Compdose(r0, r1, r2);
        int computedDose = 0;
        final int capacity = 100; // capacity of insulin reservoir in mL
        int insulin_available = 100; // insulin reservoir level
        final int max_single_dose = 4; // maximum amount of single dose
        final int max_daily_dose = 25; // maximum amount of daily dose
        final int minimum_dose = 1; // minimum dose
        final int safemin = 6; // minimum safe blood sugar level
        final int safemax = 14; // maximum safe blood sugar level
        int cumulative_dose = 0; // total dose in last 24 hours
        int dose = 0;
        Status status = Status.RUNNING; // mode status
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
            r0 = safemin;
            r1 = safemax;
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
            status = Status.RUNNING;
            alarm = AlarmValue.OFF;
            display1 = "";
        }
        while (status == Status.ERROR){
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
                status = Status.RUNNING;
                alarm = AlarmValue.OFF;
                display1 = "";
            }
        }

        // RUN
        while (switchValue == SwitchValue.AUTO && (status ==  Status.RUNNING || status == Status.WARNING)
                && insulin_available >= max_single_dose && cumulative_dose < max_daily_dose){
            // If the computed insulin dose is zero, don’t deliver any insulin
            if (computedDose == 0){
                dose = 0;
            }
            // The maximum daily dose would be exceeded if the computed dose was delivered
            else if(computedDose + cumulative_dose > max_daily_dose){
                alarm = AlarmValue.ON;
                status = Status.WARNING;
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
                status = Status.WARNING;
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
