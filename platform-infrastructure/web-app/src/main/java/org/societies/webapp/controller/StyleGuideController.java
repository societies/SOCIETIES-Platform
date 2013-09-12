package org.societies.webapp.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "styleguide")
@SessionScoped
public class StyleGuideController extends BasePageController {

    private int sliderVal1 = 5;
    private int sliderVal2 = 25;
    private String stringVal = "some text";

    public StyleGuideController() {
        log.debug("StyleGuideController ctor()");
    }


    @SuppressWarnings("MethodMayBeStatic")
    public String[] getListOfStrings() {
        return new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};
    }

    public int getSliderVal1() {
        return sliderVal1;
    }

    public void setSliderVal1(int sliderVal1) {
        this.sliderVal1 = sliderVal1;
    }

    public int getSliderVal2() {
        return sliderVal2;
    }

    public void setSliderVal2(int sliderVal2) {
        this.sliderVal2 = sliderVal2;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public String getStringVal() {
        return stringVal;
    }

}
