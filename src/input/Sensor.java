package input;

public class Sensor {

    public int bloodSugar; //placeholder value

    public Sensor(){
        this.bloodSugar = 10;
    }

    public int getReading(){
        return bloodSugar;
    }

    public void lowerBloodSugar(int compDose){
       bloodSugar -= compDose;
       //Save the SQL
    }

}
