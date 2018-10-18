package model;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;
import lejos.utility.Delay;
import customrobot.library.*;


public class LineFollowerThree {
	
	/*
	 * The bot has a left and right track with each one motor
	 */
    private UnregulatedMotor motorL;
    private UnregulatedMotor motorR;
    private ColorSensor colorSensorL = new ColorSensor(SensorPort.S1);
    private ColorSensor colorSensorR = new ColorSensor(SensorPort.S4);
    private double colorFinish;
    private double colorLine;
    private double colorBackground;
    
    public static void main(String[] args)
    {
    	//temporary main
    	String leftMotor = "A", rightMotor = "A", 
    			leftColorSensor = "A", rightColorSensor = "A"; //dit wordt constructor

    	LineFollowerThree launcher = new LineFollowerThree();
    	launcher.launch(leftMotor, rightMotor, leftColorSensor, rightColorSensor);
    }
    
    //TODO: launch zal constructor worden
    private void launch(String leftMotor, String rightMotor, String leftColorSensor, String rightColorSensor) {
    	setMotorL(leftMotor);
    	setMotorR(rightMotor);
    	setColorSensorL(leftColorSensor);
    	setColorSensorR(rightColorSensor);
    	
    	System.out.println("Line Follower V3\n");
    	
    	prepareSensor(colorSensorL);
    	prepareSensor(colorSensorR);
    	
    	startCalibration();
	}


	private void setColorSensorR(String rightColorSensor) {
		// TODO S4 vervangen door parameter
		colorSensorR = new ColorSensor(SensorPort.S4);
	}

	private void setColorSensorL(String leftColorSensor) {
		//TODO S1 vervangen door parameter
		colorSensorL = new ColorSensor(SensorPort.S1);
	}

	private void setMotorR(String rightMotor) {
    	//TODO A vervangen door rightmotor
    	motorR = new UnregulatedMotor(MotorPort.D);
	}

	private void setMotorL(String leftMotor) {
    	//TODO A vervangen door leftmotor
    	motorL = new UnregulatedMotor(MotorPort.A);
    }

	private void startCalibration() {
    	askUserInput();
        System.out.println("Press ENTER to start calibrate");
        Button.waitForAnyPress();
        calibrateFinish();
        calibrateLine();
        calibrateBackground();
	}
	
	private void calibrateFinish() {
        askUserInput();
        System.out.println("Place bot on finishline");
        System.out.println("Press ENTER to calibrate");
        Button.waitForAnyPress();
        colorFinish = getAverageRedValue();
	}
	
	private double getAverageRedValue() {
		float defaultValue = color.getRed();
		return 0;
	}

	private void calibrateLine() {
        askUserInput();
        System.out.println("Place bot on line");
        System.out.println("Press ENTER to calibrate");
        Button.waitForAnyPress();
	}
	
	private void calibrateBackground() {
        askUserInput();
        System.out.println("Place bot next to line");
        System.out.println("Press ENTER to calibrate");
        Button.waitForAnyPress();
	}

	private void askUserInput() {
		Button.LEDPattern(4);    // flash green led and 
        Sound.beepSequenceUp();  // make sound when ready.
	}

	private void prepareSensor(ColorSensor colorSensor) {
        colorSensor.setRedMode();
        colorSensor.setFloodLight(Color.RED);
        colorSensor.setFloodLight(true);
	}
    
}
