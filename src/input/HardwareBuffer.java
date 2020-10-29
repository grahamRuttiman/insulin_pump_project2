package input;

import java.util.ArrayList;
import java.util.List;

public class HardwareBuffer {

    ArrayList<String> messages = new ArrayList<String>(); // Create an ArrayList object

    int add(String message){
        messages.add(message);
        return(messages.indexOf(message));
    }

    void remove(int index){
        messages.remove(index);
    }

    ArrayList<String> getMessages(){
//        List<String> returnMessages = new ArrayList<>();
//        for(int i=0; i<messages.size(); ++i){
//            returnMessages.add(messages.get(i));
//        }
//        return returnMessages;
        return messages;
    }

}
