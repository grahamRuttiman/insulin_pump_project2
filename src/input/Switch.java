package input;


import insulinPumpController.SwitchValue;

public class Switch {

    SwitchValue switchValue;

    public void setValue(SwitchValue value) {
        this.switchValue = value;
    }

    public SwitchValue getValue(){
        return switchValue;
    }

}
