package insulinPumpController;

public class Run {

//    DINSULIN_PUMP_STATE
    // switch? = auto
    String sw = "Auto"; // maybe make enum
    // status = running Ú status = warning
    String status = "Running"; // maybe make enum
    // insulin_available ≥ max_single_dose
    String alarm; // maybe make enum
    String display1;
    int insulin_available;
    int max_single_dose;
    int r0, r1, r2;
    //cumulative_dose < max_daily_dose
    int cumulative_dose = 0;
    int max_daily_dose;
//        (SUGAR_LOW Ú SUGAR_OK Ú SUGAR_HIGH)

    int comp_dose = 0;
    int dose = 0;
    public Run(){

        // If the computed insulin dose is zero, don’t deliver any insulin
        // CompDose = 0 Þ dose! = 0
        if(comp_dose == 0){
            dose = 0;
        }

        //// The maximum daily dose would be exceeded if the computed dose was delivered
//    CompDose + cumulative_dose > max_daily_dose Þ alarm! = on
//    Ù status’ = warning Ù dose! = max_daily_dose – cumulative_dose
//            Ú
        if(comp_dose + cumulative_dose > max_daily_dose){
            alarm = "On";
            status = "Warning";
            dose = max_daily_dose - cumulative_dose;
        }
        //// The normal situation. If maximum single dose is not exceeded then deliver computed
//    dose
//    CompDose + cumulative_dose < max_daily_dose Þ
//            (CompDose ≤ max_single_dose Þ dose! = CompDose
        else if(comp_dose + cumulative_dose < max_daily_dose){
            if(comp_dose <= max_single_dose){
                dose = comp_dose;
            }
        }

//// The single dose computed is too high. Restrict the dose delivered to the maximum single
//                    dose
//                    CompDose > max_single_dose Þ dose! = max_single_dose
//                    § )
//    insulin_available’ = insulin_available – dose!
//    cumulative_dose’ = cumulative_dose + dose!

        else if(comp_dose > max_single_dose) {
            dose = max_single_dose;
            insulin_available = insulin_available - dose;
            cumulative_dose = cumulative_dose + dose;
        }

//    insulin_available ≤ max_single_dose * 4 Þ status’ = warning Ù display1! =
//    display1! È “Insulin low”
        else if(insulin_available <= max_single_dose){
            status = "Warning";
            display1 = "Insulin Low";
        }

//    r1’ = r2
//    r0’ = r1
        r1 = r2;
        r0 = r1;
    }


}
