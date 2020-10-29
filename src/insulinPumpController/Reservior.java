package insulinPumpController;

public class Reservior {
    int capacity = 100; // capacity of insulin reservoir in mL
    int insulinAvailable; // insulin reservoir level
    boolean reservoirPresent;

        public Reservior(){
            //Get insulin via MySQL
//        this.insulinAvailable = get insulin from SQL;
    }

    public int getInsulinAvailable(){
            return insulinAvailable;
    }


    public void useInsulin(int dose){
            insulinAvailable =- dose;
            saveInsulin();
    }

    public void resetResoviour(){
            insulinAvailable = 100;
            saveInsulin();
    }

    private void saveInsulin(){
            //Saveinsulin mySQL
    }




}
