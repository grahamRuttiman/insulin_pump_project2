package insulinPumpController;

import input.ClockIn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public class Main {

    public static void main(String[] args){

        int blood_sugar_level;
        int dosage;
        dosage = 1;
        String time;
        time = "12";
        String message;
        JButton off_button=new JButton("OFF");
        JButton automatic_button=new JButton("AUTO");
        JButton manual_button=new JButton("MANUAL");
        ClockIn clock = new ClockIn();

        // Get user data
        User user = new User();


        message = "Helloworld";
        ZoneId zone = ZoneId.systemDefault();
//        Clock clock = Clock.tickSeconds(zone);
//        time = clock.instant().toString();

        JFrame jFrame=new JFrame("Insulin Pump");

        // Display 1
        final JTextField Display1 =new JTextField();
        Display1.setBounds(25,25, 335,50);
//        Display1.setText(clock.instant().toString());
        Display1.setText(clock.toString());

        // Display 2
        final JTextField Display2 =new JTextField();
        Display2.setBounds(25,75, 335,100);
        Display2.setText("Blood sugar levels: " + user.blood_sugar);


        // Display 3
        final JTextField Display3 =new JTextField();
        Display3.setBounds(25,175, 335,50);
        Display3.setText("Last dosage: ");

        // Manual Dose Button
        JButton dose_button=new JButton("DOSE");
        dose_button.setBounds(50,300,95,30);
        dose_button.setBackground(Color.green);
        String finalTime = time;
        dose_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Display3.setText("Last dosage: " + dosage + "units delivered at " + finalTime);
            }
        });

        // Off Button
        off_button.setBounds(250,250,95,30);
        off_button.setBackground(Color.red);
        off_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                off_button.setBackground(Color.red);
                manual_button.setBackground(Color.white);
                automatic_button.setBackground(Color.white);
                Display2.setText("Power Off");
            }
        });

        // Manual Button
        manual_button.setBounds(250,300,95,30);
        set__button_colour(off_button, automatic_button, manual_button, Display2,"Manual Mode. Blood sugar levels: " + user.blood_sugar);

        // Automatic Button
        automatic_button.setBounds(250,350,95,30);
        set__button_colour(manual_button, off_button, automatic_button, Display2, "Automatic Mode. Blood sugar levels: " + user.blood_sugar);

        // Frame
        jFrame.add(Display1);
        jFrame.add(Display2);
        jFrame.add(Display3);
        jFrame.add(dose_button);
        jFrame.add(off_button);
        jFrame.add(manual_button);
        jFrame.add(automatic_button);
        jFrame.setSize(400,500);
        jFrame.getContentPane().setBackground(Color.blue);
        jFrame.setLayout(null);
        jFrame.setVisible(true);
    }

    private static void set__button_colour(JButton off_button, JButton automatic_button, JButton manual_button, JTextField display2, String message) {
        manual_button.setBackground(Color.white);
        manual_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                manual_button.setBackground(Color.red);
                off_button.setBackground(Color.white);
                automatic_button.setBackground(Color.white);
                display2.setText(message);
            }
        });
    }

}
