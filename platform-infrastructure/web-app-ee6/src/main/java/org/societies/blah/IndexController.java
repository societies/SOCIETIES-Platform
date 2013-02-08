package org.societies.blah;

import javax.faces.bean.ManagedBean;

@ManagedBean(name = "index")
public class IndexController {

    private String whateverValue = "1";
    private int intValue = 1;

    public String getWhateverValue() {
        return whateverValue;
    }

    public void setWhateverValue(String whateverValue) {
        this.whateverValue = whateverValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void buttonAction() {
        whateverValue = Integer.toString(Integer.valueOf(whateverValue) + 1);
        intValue++;
    }
}
