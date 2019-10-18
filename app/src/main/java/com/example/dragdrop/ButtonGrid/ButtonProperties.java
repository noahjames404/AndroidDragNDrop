package com.example.dragdrop.ButtonGrid;

/**
 * @author noahajamesy
 *
 * this class provides the properties for button.
 *
 *
 * */
public class ButtonProperties {

    /**
     * =====================================
     * Properties Description
     * =====================================
     * -code the id.
     * -position index location in a list.
     * -label preview text of button.
     * -color affects the background of button.
     * -image affects the background of button.
     *
     *
     * */
    public String code;
    public int position;
    public String label;
    public String color;
    public String image;
    public int width_ratio;
    public int height_ratio;

    public ButtonProperties(String code, int position, String label, String color, String image, int width_ratio, int height_ratio) {
        this.code = code;
        this.position = position;
        this.label = label;
        this.color = color;
        this.image = image;
        this.width_ratio = width_ratio;
        this.height_ratio = height_ratio;
    }
}
