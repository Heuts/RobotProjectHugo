package ev3.exercises;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.port.SensorPort;
import lejos.robotics.Color;
import lejos.utility.Delay;
import customrobot.library.ColorSensor;
import customrobot.library.Lcd;

public class ColorDemo2
{
    public static void main(String[] args)
    {
        ColorSensor    color = new ColorSensor(SensorPort.S1);
        ColorSensor color2 = new ColorSensor(SensorPort.S2);

        System.out.println("Color Demo");
        Lcd.print(2, "Press to start");
        
        Button.LEDPattern(4);    // flash green led and
        Sound.beepSequenceUp();    // make sound when ready.

        Button.waitForAnyPress();
        Button.LEDPattern(0);
        

        color.setRedMode();
        color.setFloodLight(Color.RED);
        color.setFloodLight(true);
        color2.setRedMode();
        color2.setFloodLight(Color.RED);
        color2.setFloodLight(true);
        
        float sum1 = 0;
        float sum2 = 0;
        for(int i = 0; i < 4; i++) {
        	Delay.msDelay(250);
        	sum1 += color.getRed();
        }
        for(int i = 0; i< 100; i++) {
        	sum2 += color.getRed();
        }
        float avg = sum1/4;
        float avg2 = sum2/100;
        Lcd.clear(3);
        Lcd.print(3, "red=%.3f", avg);        
        Lcd.clear(4);
        Lcd.print(4, "red=%.3f", avg2);
        
        while (Button.ESCAPE.isUp())
        {
            Lcd.clear(5);
            Lcd.print(5, "red=%.3f", color.getRed());
            Lcd.print(6, "red2=%.3f", color2.getRed());
            Delay.msDelay(200);
        }

        Delay.msDelay(1000);

        // free up resources.
        color.close();
        
        Sound.beepSequence();    // we are done.

        Button.LEDPattern(4);
        Button.waitForAnyPress();
    }

}