package insulinPumpController;


import input.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.Timer;
import javax.swing.JSpinner;


public class Main {

    static boolean manualDoseStarted = false;
    static boolean bufferStarted = false;
    static Timer clockTimer;
    static Timer manualDoseTimer;
    static Timer messageBufferTimer;
    static Timer testTimer;
    static int bufferPosition = 0;


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

        SpinnerNumberModel model = new SpinnerNumberModel(controller.reservoir.insulinAvailable, 0, controller.reservoir.capacity, 1);
        SpinnerNumberModel model2 = new SpinnerNumberModel(5, 0, 100, 1);
        final JTextField insulinAvailableDisplay = new JTextField();
        JSpinner insulinAvailableSpinner = new JSpinner(model);
        final JTextField dosageDisplay = new JTextField();
        JSpinner dosageSpinner = new JSpinner(model2);
        final JTextField bloodSugarDisplay = new JTextField();
        final JButton dosageButton = new JButton("Administer Dosage");
        final JTextField errorDisplay = new JTextField();
        final JButton reservoirResetButton = new JButton("Replace Reservoir");

        //Reservoir Button
        reservoirButton.setBounds(0, 0, 150, 50);
        reservoirButton.addActionListener(actionEvent -> {
            if (controller.reservoir.reservoirPresent) {
                controller.reservoir.reservoirPresent = false;
                reservoirButton.setText("Reservoir Missing");
            } else {
                controller.reservoir.reservoirPresent = true;
                reservoirButton.setText("Reservoir Present");
            }
        });

        //Needle Button
        needleButton.setBounds(150, 0, 150, 50);
        needleButton.addActionListener(actionEvent -> {

            if (controller.needle.needlePresent) {
                controller.needle.needlePresent = false;
                needleButton.setText("Needle Missing");
            } else {
                controller.needle.needlePresent = true;
                needleButton.setText("Needle Present");
            }
        });

        //Cycle Hardware Button
        hardwareButton.setBounds(300, 0, 150, 50);
        hardwareButton.addActionListener(actionEvent -> {
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
        });
        //Test Button
        testButton.setBounds(75, 50, 300, 50);
        testButton.addActionListener(actionEvent -> {
            if (state == State.RUN) {
                test();
            } else {
                errorDisplay.setText("Set device to auto");
            }

        });

        //Insulin Display
        insulinAvailableDisplay.setBounds(0, 100, 100, 50);
        insulinAvailableDisplay.setText("Insulin Available: ");
        insulinAvailableDisplay.setHorizontalAlignment(JTextField.RIGHT);
        environmentGUI.add(insulinAvailableDisplay);
        //Insulin Input
        insulinAvailableSpinner.setBounds(100, 100, 50, 50);
        environmentGUI.add(insulinAvailableSpinner);
        //Dosage Display
        dosageDisplay.setBounds(150, 100, 100, 50);
        dosageDisplay.setText("Dosage: ");
        dosageDisplay.setHorizontalAlignment(JTextField.RIGHT);
        environmentGUI.add(dosageDisplay);
        //Dosage Input
        dosageSpinner.setBounds(250, 100, 50, 50);
        environmentGUI.add(dosageSpinner);
        //Blood Sugar display
        bloodSugarDisplay.setBounds(300, 100, 150, 50);
        bloodSugarDisplay.setText("Blood Sugar: " + controller.sensor.bloodSugar);
        bloodSugarDisplay.setHorizontalAlignment(JTextField.CENTER);
        environmentGUI.add(bloodSugarDisplay);

        //Dosage Button
        dosageButton.setBounds(0, 150, 300, 50);
        dosageButton.addActionListener(actionEvent -> {

            controller.compDose = (Integer) dosageSpinner.getValue();
            controller.reservoir.insulinAvailable = (Integer) insulinAvailableSpinner.getValue();

            if (state != State.RUN) {
                errorDisplay.setText("Must be in auto mode");
            } else {
                administerDosage();
                insulinAvailableSpinner.setValue(controller.reservoir.insulinAvailable);
                bloodSugarDisplay.setText("Blood Sugar: " + controller.sensor.bloodSugar);
            }
        });

        //Replace reservoir
        reservoirResetButton.setBounds(300, 150, 150, 50);
        reservoirResetButton.addActionListener(actionEvent -> reset());


        //error display
        errorDisplay.setBounds(75, 200, 300, 50);
        environmentGUI.add(errorDisplay);

        environmentGUI.add(reservoirButton);
        environmentGUI.add(needleButton);
        environmentGUI.add(hardwareButton);
        environmentGUI.add(testButton);
        environmentGUI.add(dosageButton);
        environmentGUI.add(reservoirResetButton);
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
        clockDisplay.setHorizontalAlignment(JTextField.CENTER);
        turnScreensOff();


        // Off Button
        offButton.setBounds(250, 250, 95, 30);
        offButton.setBackground(Color.gray);
        offButton.addActionListener(actionEvent -> {
            //Set Colour
            offButton.setBackground(Color.gray);
            manualButton.setBackground(Color.white);
            autoButton.setBackground(Color.white);
            //Clear Display
            display1.setText("");
            display2.setText("");
            clockDisplay.setText("");
            off();
        });

        // Auto Button
        autoButton.setBounds(250, 350, 95, 30);
        autoButton.addActionListener(actionEvent -> {
            offButton.setBackground(Color.white);
            manualButton.setBackground(Color.white);
            autoButton.setBackground(Color.gray);

            if (state == State.OFF) {
                startUp();
                run();
            } else {
                run();
            }

        });

        // Manual Button
        manualButton.setBounds(250, 300, 95, 30);
        manualButton.addActionListener(actionEvent -> {
            offButton.setBackground(Color.white);
            manualButton.setBackground(Color.gray);
            autoButton.setBackground(Color.white);

            if (state == State.OFF) {
                startUp();
                manual();
            } else {
                manual();
            }
        });


        // Manual Dose Button
        doseButton.setBounds(50, 300, 95, 30);
        doseButton.setBackground(Color.green);
        doseButton.addActionListener(actionEvent -> {

            if (state != State.MANUAL) {
                display1.setText("Must be in manual mode");

                //Manual dosage in 5 second period
            } else if (!manualDoseStarted) {
                display1.setText("Manual Dosage Activated");
                manualDoseStarted = true;
                manualDoseTimer = new Timer(5000, e -> {
                    if (controller.compDose > controller.reservoir.insulinAvailable) {
                        display1.setText("Not enough Insulin");
                    } else {
                        administerDosage();
                    }
                    manualDoseStarted = false;
                    manualDoseTimer.stop();
                    display1.setText("");
                });
                manualDoseTimer.start();
            } else {
                controller.compDose += 1;
                display1.setText("Manual Dosgae Units: " + controller.compDose);
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
        clockDisplay.setText(clock.getTimeS());
        controller.readFromDatabase();

        //clock Timer update every second
        clockTimer = new Timer(1000, actionEvent -> {
            clockDisplay.setText(clock.getTimeS());
            //Check if its a new day.
            if (clock.getTimeS().equals("00:00:00")) {
                controller.cumulativeDose = 0;
            }
        });
        clockTimer.start();

        //Self Test Timer every 30 seconds
        testTimer = new Timer(30000, actionEvent -> {
            state = State.TEST;
            display1.setText("Testing...");
            test();
        });
        testTimer.start();
    }

    static void run() {
        state = State.RUN;
        display1.setText("Automatic Mode");

        //blood sugar timer wait 10 minutes
        Timer sensorTimer = new Timer(600000, actionEvent -> {
            controller.compDose();
            administerDosage();
        });
        sensorTimer.start();
    }

    static void administerDosage() {
        if (controller.compDose == 0) {
            display2.setText("Last reading at " + clock.getTimeNoS() + ",\n\r " + "No Insulin Administered");
        } else if (controller.compDose > controller.reservoir.insulinAvailable) {
            notEnoughInsulin();
            display1.setText("Not enough insulin please replace reservoir");
        } else {
            display2.setText("Last dosage at " + clock.getTimeNoS() + ", Units administered: " + controller.compDose);
            controller.reservoir.useInsulin(controller.compDose);
            controller.sensor.lowerBloodSugar(controller.compDose);
            controller.cumulativeDose += controller.compDose;
            controller.compDose = 0;
        }
    }

    static void manual() {
        state = State.MANUAL;
        display1.setText("Manual Mode");

    }

    static void reset() {
        state = State.RESET;
        controller.reservoir.resetReservoir();
        display1.setText("Insulin Reservoir Replaced");
    }

    static void off() {
        if (state != State.OFF) {
            state = State.OFF;
            controller.compDose = 0;

            clockTimer.stop();
            testTimer.stop();

            if (manualDoseStarted){
                manualDoseTimer.stop();
            }
            if (bufferStarted){
                messageBufferTimer.stop();
            }
            turnScreensOff();
            alarm.alarmOn = false;
            controller.writeToDatabase();
        }
    }

    static void test() {

        if (controller.hardwareTest != HardwareTest.OK || !controller.needle.needlePresent || !controller.reservoir.reservoirPresent) {
            if (!alarm.alarmOn){
                setAlarm();
            }
            ArrayList<String> bufferArray = new ArrayList<>();
            if (!controller.needle.needlePresent) {
                bufferArray.add("No needle found");
            }
            if (!controller.reservoir.reservoirPresent) {
                bufferArray.add("No reservoir found");
            }
            if (controller.hardwareTest == HardwareTest.BATTERYLOW) {
                bufferArray.add("Battery Low");
            } else if (controller.hardwareTest == HardwareTest.PUMPFAIL) {
                bufferArray.add("Pump failure");
            } else if (controller.hardwareTest == HardwareTest.SENSORFAIL) {
                bufferArray.add("Sensor failure");
            } else if (controller.hardwareTest == HardwareTest.DELIVERYFAIL) {
                bufferArray.add("Delivery Failure");
            }
            if (bufferArray.size() == 1) {
                display1.setText(bufferArray.get(0));
            } else {
                messageBuffer(bufferArray);
            }
        } else {
            alarm.alarmOn = false;
            display1.setText("Last Test completed at " + clock.getTimeS() + ". No issues found.");
        }

    }

    static void messageBuffer(ArrayList<String> bufferArray) {
        //Cycle through message every 5 seconds
        bufferPosition = 0;
        display1.setText(bufferArray.get(0));
        bufferPosition++;
        bufferStarted = true;

        messageBufferTimer = new Timer(2000, actionEvent -> {
            if (alarm.alarmOn){
                if (bufferPosition == bufferArray.size()) {
                    bufferPosition = 0;
                }
                display1.setText(bufferArray.get(bufferPosition));
                bufferPosition++;
            } else {
                messageBufferTimer.stop();
                bufferStarted = false;
            }
        });
        messageBufferTimer.start();
    }

    static void setAlarm() {
        alarm.alarmOn = true;
        Runnable r = () -> {
            while (alarm.alarmOn) {
                Toolkit.getDefaultToolkit().beep();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    static void notEnoughInsulin() {
        setAlarm();
        //Check if insulin is available every 2 seconds
        Timer insulinTimer = new Timer(2000, actionEvent -> {
            if (controller.compDose <= controller.reservoir.insulinAvailable) {
                alarm.alarmOn = false;
                display1.setText("");
                administerDosage();
            }
        });
        insulinTimer.start();
    }


    public static void main(String[] args) {
        insulinPumpGUI();
        environmentGUI();
        state = State.OFF;
    }

}
