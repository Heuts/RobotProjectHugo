package model;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;
import lejos.utility.Delay;
import utility.*;

public class LineFollower 
{ 
    static UnregulatedMotor motorA = new UnregulatedMotor(MotorPort.A);
    static UnregulatedMotor motorD = new UnregulatedMotor(MotorPort.D);
    static TouchSensor touch = new TouchSensor(SensorPort.S3);
    static ColorSensor color = new ColorSensor(SensorPort.S1);
    
    public LineFollower()
    {
        float    colorValue;
        
        System.out.println("Line Follower\n");
        
        color.setRedMode();
        color.setFloodLight(Color.RED);
        color.setFloodLight(true);

        Button.LEDPattern(2);    // flash green led and 
        Sound.beepSequenceUp();  // make sound when ready.

        System.out.println("Press any key to start");
        
        Button.waitForAnyPress();
        
        //TODO: bericht opnemen dat hij aan het callibreren is
        Delay.msDelay(1000);
        float defaultValue = color.getRed();
        defaultValue *= 3;
        Delay.msDelay(1000);
        
        System.out.println("Default: " + defaultValue);
        
        motorA.setPower(40);
        motorD.setPower(40);
       

        
        // drive waiting for touch sensor or escape key to stop driving.

        while (!touch.isTouched() && Button.ESCAPE.isUp()) 
        {
            colorValue = color.getRed();
            
            Button.LEDPattern(getRandom());
            
            Lcd.clear(7);
            Lcd.print(7,  "value=%.3f", colorValue);

            if (colorValue > defaultValue)
            {
            	motorA.forward();
                motorA.setPower(80);
                motorD.backward();
                motorD.setPower(0);
            }
            else
            {
            	motorA.backward();
                motorA.setPower(60);
                motorD.forward();
                motorD.setPower(60);
            }
        }
       
        // stop motors with brakes on.
        motorA.stop();
        motorD.stop();

        // free up resources.
        motorA.close();
        motorD.close();
        touch.close();
        color.close();
       
        Sound.beepSequence(); // we are done.
    }
    
    public int getRandom() {
    	return (int)(Math.random()*5);
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