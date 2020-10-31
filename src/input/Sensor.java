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
       if (bloodSugar < 0){
           bloodSugar = 0;
       }
       //TODO save to SQL
    }

}
