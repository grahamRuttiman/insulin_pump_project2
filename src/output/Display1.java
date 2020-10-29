package output;

import javax.swing.*;

public class Display1 {

    public String displayMessage;

    public static void setDisplay1(String message){
//        displayMessage = message;
        // Display 1
        JFrame jFrame=new JFrame("Display 1");
        final JTextField Display1 =new JTextField();
        Display1.setBounds(25,25, 335,50);
        Display1.setText(message);

    }

//    public static String getDisplay1(){
//
//    }


}
