package insulinPumpController;

public class Reservoir {
    int capacity = 100; // capacity of insulin reservoir in mL
    int insulinAvailable = 10; // placeholder read from SQL
    boolean reservoirPresent = true;

        public Reservoir(){
            //Get insulin via MySQL
//        this.insulinAvailable = get insulin from SQL;
    }


    public void useInsulin(int dose){
            insulinAvailable =- dose;
    }

    public void resetReservoir(){
            insulinAvailable = capacity;
    }



}
