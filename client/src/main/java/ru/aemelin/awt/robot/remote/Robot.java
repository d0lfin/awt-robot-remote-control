package ru.aemelin.awt.robot.remote;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dolf
 * Date: 22.02.13
 * Time: 17:43
 */
public class Robot {
    private final String ADD_MARKER_SCRIPT_FILE = "awt.robot.marker.add.js";
    private final String REMOVE_MARKER_SCRIPT_FILE = "awt.robot.marker.remove.js";
    private final int LEFT_BUTTON = InputEvent.BUTTON1_MASK;
    private final int RIGHT_BUTTON = InputEvent.BUTTON3_MASK;
    private final Map<String, Integer> keyTextToCode = new HashMap<String, Integer>(256){{
        Field[] fields = KeyEvent.class.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            if (name.startsWith("VK_")) {
                try {
                    put(name.substring("VK_".length()).toUpperCase(), field.getInt(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }};

    private int[] browserOffset = null;

    private String url;

    public Robot(String url) {
        this.url = url;
    }

    public Color getRGBPixelColor(int x, int y){
        int rgb = Integer.valueOf(new Service.Robot.GetRGBPixelColorXY(url, x, y).getAsTextPlain(String.class));
        return new Color(rgb, true);
    }

    public void keyPress(int x){
        new Service.Robot.KeyPressX(url, x).postAsTextPlain(String.class);
    }

    public void keyRelease(int x){
        new Service.Robot.KeyReleaseX(url, x).postAsTextPlain(String.class);
    }

    public void mouseMove(int x, int y){
        new Service.Robot.MouseMoveXY(url, x, y).postAsTextPlain(String.class);
    }

    public void mousePress(int x){
        new Service.Robot.MousePressX(url, x).postAsTextPlain(String.class);
    }

    public void mouseRelease(int x){
        new Service.Robot.MouseReleaseX(url, x).postAsTextPlain(String.class);
    }

    public void mouseWhell(int x){
        new Service.Robot.MouseWheelX(url, x).postAsTextPlain(String.class);
    }

    public int[] getBrowserOffset(WebDriver driver) throws IOException{
        String addMarker = Resources.toString(Resources.getResource(ADD_MARKER_SCRIPT_FILE), Charsets.UTF_8);
        String removeMarker = Resources.toString(Resources.getResource(REMOVE_MARKER_SCRIPT_FILE), Charsets.UTF_8);

        ((JavascriptExecutor) driver).executeScript(addMarker + " return 0;");

        String[] answer;
        try{
            answer = (new Service.Robot.GetBrowserOffset(url).getAsTextPlain(String.class)).split(",");
        }
        finally {
            ((JavascriptExecutor) driver).executeScript(removeMarker + " return 0;");
        }

        if(answer.length == 2){
            browserOffset = new int[]{Integer.valueOf(answer[0]), Integer.valueOf(answer[1])};
            return browserOffset;
        }
        else{
            throw new NullPointerException("Can't find offset of browser window.");
        }
    }

    public void mouseClick(int x, int y){
        mouseMove(x, y);
        mousePress(LEFT_BUTTON);
        mouseRelease(LEFT_BUTTON);
    }

    public void mouseRightClick(int x, int y){
        mouseMove(x, y);
        mousePress(RIGHT_BUTTON);
        mouseRelease(RIGHT_BUTTON);
    }

    public void mouseClick(WebElement element){
        try{
            int x = browserOffset[0] + element.getLocation().getX() + element.getSize().getWidth() / 2;
            int y = browserOffset[1] + element.getLocation().getY() + element.getSize().getHeight() / 2;
            mouseClick(x, y);
        }
        catch (NullPointerException e){
            throw new NullPointerException("Get the offset of browser window before click on WebElement.");
        }
    }

    public void mouseMove(WebElement element){
        try{
            int x = browserOffset[0] + element.getLocation().getX() + element.getSize().getWidth() / 2;
            int y = browserOffset[1] + element.getLocation().getY() + element.getSize().getHeight() / 2;
            mouseMove(x, y);
        }
        catch (NullPointerException e){
            throw new NullPointerException("Get the offset of browser window before move to the WebElement.");
        }
    }

    public void sendKeys(String keys){
        for(int i = 0; i < keys.length(); i++){
            String key = keys.substring(i, i + 1);
            int keyCode = keyTextToCode.get(key.toUpperCase());
            boolean upperCase = key.equals(key.toUpperCase());
            if(upperCase) keyPress(KeyEvent.VK_SHIFT);
            keyPress(keyCode);
            keyRelease(keyCode);
            if(upperCase) keyRelease(KeyEvent.VK_SHIFT);
        }
    }
}
