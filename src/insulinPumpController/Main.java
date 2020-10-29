package insulinPumpController;

import input.Clock;
import input.Sensor;

import javax.naming.ldap.Control;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.*;
import javax.swing.Timer;

public class Main {

    private static JFrame jFrame = new JFrame("Insulin Pump");

    private static JTextField display1 =new JTextField();
   private static JTextField display2 = new JTextField();
    private static JTextField clockDisplay =new JTextField();

    private static JButton offButton = new JButton("OFF");
    private static JButton autoButton = new JButton("AUTO");
    private static JButton manualButton = new JButton("MANUAL");
    private static JButton doseButton = new JButton("DOSE");

    private static State state;
    private static Sensor sensor;
    private static Clock clock;
    private static Controller controller;


    static void drawGUI() {

        display1.setBounds(25,175, 335,50);
        display2.setBounds(25,75, 335,100);
        clockDisplay.setBounds(25,25, 335,50);

        // Off Button
        offButton.setBounds(250,250,95,30);
        offButton.setBackground(Color.gray);
        offButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Set Colour
                offButton.setBackground(Color.gray);
                manualButton.setBackground(Color.white);
                autoButton.setBackground(Color.white);
                //Clear Display
                display1.setText("");
                display2.setText("");
                clockDisplay.setText("");
            }
        });

        // Manual Button
        manualButton.setBounds(250,300,95,30);
        manualButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                offButton.setBackground(Color.white);
                manualButton.setBackground(Color.gray);
                autoButton.setBackground(Color.white);
            }
        });

        // Auto Button
        autoButton.setBounds(250,350,95,30);
        autoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                offButton.setBackground(Color.white);
                manualButton.setBackground(Color.white);
                autoButton.setBackground(Color.gray);

                if (state == State.OFF){
                    startUp();
                }

            }
        });

        // Manual Dose Button
        doseButton.setBounds(50,300,95,30);
        doseButton.setBackground(Color.green);
        doseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // updateDosage();

            }
        });

        // Frame
        jFrame.add(clockDisplay);
        jFrame.add(display1);
        jFrame.add(display2);
        jFrame.add(doseButton);
        jFrame.add(offButton);
        jFrame.add(manualButton);
        jFrame.add(autoButton);
        jFrame.setSize(400,500);
        jFrame.getContentPane().setBackground(Color.blue);
        jFrame.setLayout(null);
        jFrame.setVisible(true);

    }


    static void startUp(){
        state = State.STARTUP;
        display1.setText("Starting Up");
        //clock Timer
        Timer clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clockDisplay.setText(clock.getTime());
            }
        });
        clockTimer.start();

        //blood sugar timer wait 10 minutes
        Timer sensorTimer = new Timer(600000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getReading;
            }
        });
        sensorTimer.start();

        //Self Test Timer every 30 seconds
        Timer testTimer = new Timer(30000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                state = State.TEST;
                display1.setText("Testing...");
                //do a test
            }
        });
        testTimer.start();

    }

    public static void main(String[] args){

        drawGUI();
        state = State.OFF;



        int blood_sugar_level;
        int dosage;
        dosage = 1;
//        time = "12";
        String message;

//        ClockIn clock = new ClockIn();



        // Get user data
        User user = new User();


        message = "Helloworld";
        ZoneId zone = ZoneId.systemDefault();
//        Clock clock = Clock.tickSeconds(zone);
//        time = clock.instant().toString();





} }
