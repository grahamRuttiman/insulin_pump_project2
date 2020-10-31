package insulinPumpController;

public class Reservoir {
    int capacity = 100; // capacity of insulin reservoir in mL
    int insulinAvailable = 10; // placeholder read from SQL
    boolean reservoirPresent;

        public Reservoir(){
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

    public void resetReservoir(){
            insulinAvailable = capacity;
            saveInsulin();
    }

    private void saveInsulin(){
            //Saveinsulin mySQL
    }


}
