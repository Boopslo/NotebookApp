package practice.oslo.com.notebookapp;


import android.graphics.Color;

/**
 * Created by Oslo on 6/11/15.
 * The reason to create this kind of enum is to implement encapsulation
 *
 */
public enum Colors {

    RED("#DD1111"), BLUE("#33ABEE"), GREEN("#22AB55"), ORANGE("#DD5511"),
    PURPLE("#669B00EE"), LIGHTGRAY("#CCCCDD");

    private String colorCode;

    Colors(String s) {
        colorCode = s;
    }

    public String getColorCode(){
        return colorCode;

    }

    public int parseColorCode(){
        return Color.parseColor(colorCode);
    }

}
