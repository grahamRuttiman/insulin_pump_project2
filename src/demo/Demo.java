package demo;

import input.Sensor;

public class Demo {

    public Sensor sensor = new Sensor();
    public int reading = 0;

    public void run(){
        // set up al scenarios to demo here

        sensor.setReading(3); // 3 is dummy value. Ideally read in to database
    }

    public int getReading(){
        return(reading);
    }

}
