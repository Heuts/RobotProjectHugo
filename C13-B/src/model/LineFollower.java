package model;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;
import customrobot.library.*;

public class LineFollower 
{ 
    static UnregulatedMotor motorA = new UnregulatedMotor(MotorPort.A);
    static UnregulatedMotor motorB = new UnregulatedMotor(MotorPort.B);
    static TouchSensor touch = new TouchSensor(SensorPort.S1);
    static ColorSensor color = new ColorSensor(SensorPort.S3);
    
    public static void main(String[] args)
    {
        float    colorValue;
        
        System.out.println("Line Follower\n");
        
        color.setRedMode();
        color.setFloodLight(Color.RED);
        color.setFloodLight(true);

        Button.LEDPattern(4);    // flash green led and 
        Sound.beepSequenceUp();  // make sound when ready.

        System.out.println("Press any key to start");
        
        Button.waitForAnyPress();
        
        motorA.setPower(40);
        motorB.setPower(40);
       
        // drive waiting for touch sensor or escape key to stop driving.

        while (!touch.isTouched() && Button.ESCAPE.isUp()) 
        {
            colorValue = color.getRed();
            
            Lcd.clear(7);
            Lcd.print(7,  "value=%.3f", colorValue);

            if (colorValue > .100)
            {
                motorA.setPower(40);
                motorB.setPower(20);
            }
            else
            {
                motorA.setPower(20);    
                motorB.setPower(40);
            }
        }
       
        // stop motors with brakes on.
        motorA.stop();
        motorB.stop();

        // free up resources.
        motorA.close();
        motorB.close();
        touch.close();
        color.close();
       
        Sound.beepSequence(); // we are done.
    }
}
   
/*
	This line follower example can be improved upon by controlling 
	the rate of turn based on how far away the color sensor value is from .100. 
	So for a value of .80 we would turn right less than for a value of .70  or .60. 
	This determines the rate of turn proportionally to how far from the edge of the tape 
	the robot is. Further from the edge, more turn, closer to the edge less turn. 
	This is called proportional control and should smooth out the movements of the robot.
	Modify the example code to to do this.
*/