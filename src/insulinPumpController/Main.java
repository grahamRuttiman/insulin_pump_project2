package insulinPumpController;


import input.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.Timer;
import javax.swing.text.NumberFormatter;

public class Main {

    static boolean manualDoseStarted = false;
    static Timer clockTimer;
    static Timer manualDoseTimer;

    //Classes
    static State state;
    static Clock clock = new Clock();
    static Controller controller = new Controller();
    static Alarm alarm = new Alarm();

    static final JTextField display1 = new JTextField();
    static final JTextField display2 = new JTextField();
    static final JTextField clockDisplay = new JTextField();

    static void environmentGUI() {

        //Environment GUI
        final JFrame environmentGUI = new JFrame("Environment GUI");
        final JToggleButton reservoirButton = new JToggleButton("Reservoir Present");
        final JToggleButton needleButton = new JToggleButton("Needle Present");
        final JButton hardwareButton = new JButton("Hardware OK");
        final JButton testButton = new JButton("HardwareTest");
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(100);
        formatter.setAllowsInvalid(false);
        final JTextField insulinAvailableDisplay = new JTextField();
        final JFormattedTextField insulinAvailableInput = new JFormattedTextField(formatter);
        final JTextField bloodSugarDisplay = new JTextField();
        final JFormattedTextField bloodSugarInput = new JFormattedTextField(formatter);
        final JButton dosageButton = new JButton("Administer Dosage");
        final JTextField errorDisplay = new JTextField();

        //Reservoir Button
        reservoirButton.setBounds(0, 0, 150, 50);
        reservoirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (controller.reservoir.reservoirPresent) {
                    controller.reservoir.reservoirPresent = false;
                    reservoirButton.setText("Reservoir Missing");
                } else {
                    controller.reservoir.reservoirPresent = true;
                    reservoirButton.setText("Reservoir Present");
                }
            }
        });

        //Needle Button
        needleButton.setBounds(150, 0, 150, 50);
        needleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (controller.needle.needlePresent) {
                    controller.needle.needlePresent = false;
                    needleButton.setText("Needle Missing");
                } else {
                    controller.needle.needlePresent = true;
                    needleButton.setText("Needle Present");
                }
            }
        });

        //Cycle Hardware Button
        hardwareButton.setBounds(300, 0, 150, 50);
        hardwareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (controller.hardwareTest == HardwareTest.OK) {
                    controller.hardwareTest = HardwareTest.BATTERYLOW;
                    hardwareButton.setText("Battery Low");
                } else if (controller.hardwareTest == HardwareTest.BATTERYLOW) {
                    controller.hardwareTest = HardwareTest.PUMPFAIL;
                    hardwareButton.setText("Pump Fail");
                } else if (controller.hardwareTest == HardwareTest.PUMPFAIL) {
                    controller.hardwareTest = HardwareTest.SENSORFAIL;
                    hardwareButton.setText("Sensor Fail");
                } else if (controller.hardwareTest == HardwareTest.SENSORFAIL) {
                    controller.hardwareTest = HardwareTest.DELIVERYFAIL;
                    hardwareButton.setText("Delivery Fail");
                } else if (controller.hardwareTest == HardwareTest.DELIVERYFAIL) {
                    controller.hardwareTest = HardwareTest.OK;
                    hardwareButton.setText("Hardware OK");
                } else {
                    errorDisplay.setText("Hardware Button Error");
                }
            }
        });
        //Test Button
        testButton.setBounds(75, 50, 300, 50);
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (state == State.RUN) {
                    test();
                } else {
                    errorDisplay.setText("Set device to auto");
                }

            }
        });

        //Insulin Available Display
        insulinAvailableDisplay.setBounds(0, 100, 100, 50);
        insulinAvailableDisplay.setText("Insulin Available: ");
        //Insulin Input
        insulinAvailableInput.setBounds(100, 100, 100, 50);

        //Insulin Available Display
        bloodSugarDisplay.setBounds(250, 100, 100, 50);
        bloodSugarDisplay.setText("Dosage: ");
        //Insulin Input
        bloodSugarInput.setBounds(350, 100, 100, 50);

        //Dosage Button
        dosageButton.setBounds(75, 150, 300, 50);
        dosageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //FIXME
                //TODO test inputs and get them to work on the controller.
                if (insulinAvailableDisplay != null) {
                    controller.reservoir.insulinAvailable = Integer.getInteger(insulinAvailableInput.getText());
                }
                if (bloodSugarInput != null) {
                    controller.sensor.bloodSugar = Integer.getInteger(bloodSugarDisplay.toString());
                }


            }
        });

        //error display
        errorDisplay.setBounds(75, 200, 300, 50);
        environmentGUI.add(reservoirButton);
        environmentGUI.add(needleButton);
        environmentGUI.add(hardwareButton);
        environmentGUI.add(testButton);
        environmentGUI.add(insulinAvailableDisplay);
        environmentGUI.add(insulinAvailableInput);
        environmentGUI.add(bloodSugarDisplay);
        environmentGUI.add(bloodSugarInput);
        environmentGUI.add(dosageButton);
        environmentGUI.add(errorDisplay);
        environmentGUI.setSize(450, 300);
        environmentGUI.getContentPane().setBackground(Color.green);
        environmentGUI.setLayout(null);
        environmentGUI.setVisible(true);

    }

    static void insulinPumpGUI() {

        final JFrame insulinPumpGUI = new JFrame("Insulin Pump");
        final JButton offButton = new JButton("OFF");
        final JButton autoButton = new JButton("AUTO");
        final JButton manualButton = new JButton("MANUAL");
        final JButton doseButton = new JButton("DOSE");

        display1.setBounds(25, 175, 335, 50);
        display2.setBounds(25, 75, 335, 100);
        clockDisplay.setBounds(25, 25, 335, 50);
        turnScreensOff();


        // Off Button
        offButton.setBounds(250, 250, 95, 30);
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
                off();
            }
        });

        // Auto Button
        autoButton.setBounds(250, 350, 95, 30);
        autoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                offButton.setBackground(Color.white);
                manualButton.setBackground(Color.white);
                autoButton.setBackground(Color.gray);

                if (state == State.OFF) {
                    startUp();
                    run();
                } else {
                    run();
                }

            }
        });

        // Manual Button
        manualButton.setBounds(250, 300, 95, 30);
        manualButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                offButton.setBackground(Color.white);
                manualButton.setBackground(Color.gray);
                autoButton.setBackground(Color.white);

                if (state == State.OFF) {
                    startUp();
                    manual();
                } else {
                    manual();
                }
            }
        });


        // Manual Dose Button
        doseButton.setBounds(50, 300, 95, 30);
        doseButton.setBackground(Color.green);
        doseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (state != State.MANUAL){
                    display1.setText("Must be in manual mode");

                    //Manual dosage in 5 second period
                } else if (!manualDoseStarted){
                    display1.setText("Manual Dosage Activated");
                    manualDoseStarted = true;
                    manualDoseTimer = new Timer(5000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (controller.compDose > controller.reservoir.insulinAvailable){
                                display1.setText("Not enough Insulin");
                            } else {
                            display1.setText(controller.compDose + " units of Insulin Administered");
                            controller.administerInsulin();
                            }
                            manualDoseStarted = false;
                            manualDoseTimer.stop();

                        }
                    });
                    manualDoseTimer.start();
                } else {
                    controller.compDose += 1;
                    display1.setText("Manual Dosgae Units: " + controller.compDose);
                }

            }
        });

        // Frame
        insulinPumpGUI.add(clockDisplay);
        insulinPumpGUI.add(display1);
        insulinPumpGUI.add(display2);
        insulinPumpGUI.add(doseButton);
        insulinPumpGUI.add(offButton);
        insulinPumpGUI.add(manualButton);
        insulinPumpGUI.add(autoButton);
        insulinPumpGUI.setSize(400, 500);
        insulinPumpGUI.getContentPane().setBackground(Color.blue);
        insulinPumpGUI.setLayout(null);
        insulinPumpGUI.setVisible(true);

    }

    static void turnScreensOff() {
        display1.setBackground(Color.gray);
        display2.setBackground(Color.gray);
        clockDisplay.setBackground(Color.gray);
    }

    static void turnScreensOn() {
        display1.setBackground(Color.white);
        display2.setBackground(Color.white);
        clockDisplay.setBackground(Color.white);
    }


    static void startUp() {
        state = State.STARTUP;
        turnScreensOn();
        display1.setText("Starting Up");
        clockDisplay.setText(clock.getTime());
        //TODO read from SQL

        //clock Timer update every second
        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clockDisplay.setText(clock.getTime());
                //Check if its a new day.
                if (clock.getTime().equals("00:00:00")) {
                    controller.cumulativeDose = 0;
                }
            }
        });
        clockTimer.start();

        //Self Test Timer every 30 seconds
        Timer testTimer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                state = State.TEST;
                display1.setText("Testing...");
                test();
            }
        });
        testTimer.start();
    }

    static void run() {
        state = State.RUN;
        display1.setText("Automatic Mode");

        //blood sugar timer wait 10 minutes
        Timer sensorTimer = new Timer(600000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.compDose();
                //Warnings about sugar level - alarm?
                if (controller.compDose == 0) {
                    //Send update that nothing happened?
                } else if (controller.compDose > 0) {
                    controller.reservoir.useInsulin(controller.compDose);
                    //Send update to terminal
                    controller.compDose = 0;
                }

            }
        });
        sensorTimer.start();
    }

    static void manual() {
        state = State.MANUAL;
        display1.setText("Manual Mode");

    }

    static void reset() {
        state = State.RESET;
        controller.reservoir.resetReservoir();
    }

    static void off() {
        if (state != State.OFF){
            state = State.OFF;
            controller.compDose = 0;
            clockTimer.stop();
            turnScreensOff();
            //Save values to SQL
        }
    }

    static void test() {
        if (controller.hardwareTest != HardwareTest.OK) {
            alarm.alarmOn = true;
            setAlarm();

            if (!controller.needle.needlePresent) {
                display1.setText("No Needle Uni");
            } else if (!controller.reservoir.reservoirPresent) {
                display1.setText("No Insulin");
            } else if (controller.hardwareTest == HardwareTest.BATTERYLOW) {
                display1.setText("Battery Low");
            } else if (controller.hardwareTest == HardwareTest.PUMPFAIL) {
                display1.setText("Pump Failure");
            } else if (controller.hardwareTest == HardwareTest.SENSORFAIL) {
                display1.setText("Sensor Failure");
            } else if (controller.hardwareTest == HardwareTest.DELIVERYFAIL) {
                display1.setText("Delivery Failure");
            }
        } else {
            alarm.alarmOn = false;
            display1.setText("Test completed at " + clock.getTime() + ". No issues found.");

        }
    }

    static void setAlarm() {
        Runnable r = new Runnable() {
            public void run() {
                while (alarm.alarmOn) {
                    Toolkit.getDefaultToolkit().beep();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException x) {
                    }
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }


    public static void main(String[] args) {
        insulinPumpGUI();
        environmentGUI();
        state = State.OFF;
    }

}
