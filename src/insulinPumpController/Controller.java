package insulinPumpController;

import input.*;

public class Controller {

    private static final int safeMin = 6; // minimum safe blood sugar level
    private static final int safeMax = 14; // maximum safe blood sugar level
    final static int maxSingleDose = 4; // maximum amount of single dose
    final static int maxDailyDose = 25; // maximum amount of daily dose
    final static int minDose = 1; // minimum dose
    public int compDose = 0;
    public int cumulativeDose; // total dose in last 24 hours //Get from SQL


    public static SugarLevel sugarLevel;
    private static int r2 = 0; // current sensor reading
    private static int r1 = safeMax; // previous sensor reading
    private static int r0 = safeMin; // previous to r1 sensor reading

    Reservoir reservoir = new Reservoir();
    Needle needle = new Needle();
    HardwareTest hardwareTest = HardwareTest.OK;
    Sensor sensor = new Sensor();

    public void administerInsulin(){
        reservoir.useInsulin(compDose);
        sensor.lowerBloodSugar(compDose);
        compDose = 0;
    }


    public void compDose() {

        r0 = r1;
        r1 = r2;
        r2 = sensor.getReading();


        // SUGAR_LOW schema
        if (r2 < safeMin) {
            sugarLevel = SugarLevel.LOW;
            compDose = 0;
        }

        // SUGAR_OK schema
        else if (r2 >= safeMin && r2 <= safeMax) {
            sugarLevel = SugarLevel.OK;
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
        else if (r2 > safeMax) {
            sugarLevel = SugarLevel.HIGH;
            // sugar level increasing. Round down if below 1 unit.
            if (r2 > r1 && (r2 - r1) / 4 == 0) {
                compDose = minDose;
            } else if (r2 > r1 && (r2 - r1) / 4 > 0) {
                compDose = (r2 - r1) / 4;
            }
            // sugar level stable
            else if (r2 == r1) {
                compDose = minDose;
            }
            // sugar level falling and rate of decrease increasing
            else if (r2 < r1 && (r2 - r1) > (r1 - r0)) {
                compDose = minDose;
            }
        }

        //Over max Single doses
        if (compDose > maxSingleDose){
            compDose = maxSingleDose;
        }

        // Would exceed max daily dose
        if (compDose + cumulativeDose > maxDailyDose){
            compDose = maxDailyDose - cumulativeDose;
        }

    }


}
