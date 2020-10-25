package input;

public class Sensor {

    int reading = 0;

    public Sensor(int sensor_reading){
        this.reading = sensor_reading;
    }

    public int getReading(){
        return reading;
    }

}
