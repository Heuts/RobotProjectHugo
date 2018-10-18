package model;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;
import lejos.utility.Delay;
import customrobot.library.*;

//TODO: refactoring: arrays gebruiken zodat je één motor hebt en één sensor en één finish, etc.
//TODO: maak calibratiemethodes generieker, teveel herhaling => calibratie class?
//TODO: berichten op lcd uitlijnen
public class LineFollowerThree {
	/*
	 * The bot has a left and right track with each one motor
	 */
    private UnregulatedMotor motorL;
    private UnregulatedMotor motorR;
    private ColorSensor colorSensorL;
    private ColorSensor colorSensorR;
    private double colorFinishL;
    private double colorFinishR;
    private double colorLineL;
    private double colorLineR;
    private double colorBackgroundL;
    private double colorBackgroundR;
    private final static int CALIBRATION_CYCLES = 4;
    
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
    	
    	Lcd.print(1, "Line Follower V3");
    	
    	prepareSensor(colorSensorL);
    	prepareSensor(colorSensorR);
    	
//    	startCalibration();
//    	printCalibration();
    	askUserInput();
    	Button.waitForAnyPress();
    	
    	runEngine();
    	
    	closeSensors();

    
	}

    
    

    private void closeSensors() {
        motorL.close();
        motorR.close();
    	colorSensorL.close();
    	colorSensorR.close();
		
	}

	private void runEngine() {

    	motorL.forward();
//        motorR.forward();
//        motorL.setPower(150);
//        motorR.setPower(150);
//        Delay.msDelay(4000);

        for(int i = 0; i<200; i++) {

        	motorL.setPower(i);
            Lcd.clear();
            Lcd.print(5, "Snelheid: " + i);
            Delay.msDelay(80);
        	
        }
        
        
        
	}

	/*
     * CALLIBRATIE DEEL HIERONDER
     */
    
	private void printCalibration() {
        Lcd.clear();
        Lcd.print(1, "Calibration results:");
        Lcd.print(2, "finish L: %.3f", colorFinishL);
        Lcd.print(3, "finish R: %.3f", colorFinishR);
        Lcd.print(4, "line L: %.3f", colorLineL);
        Lcd.print(5, "line R: %.3f", colorLineR);
        Lcd.print(6, "background L: %.3f", colorBackgroundL);
        Lcd.print(7, "background R: %.3f", colorBackgroundR);
	}

	private void setColorSensorR(String rightColorSensor) {
		// TODO S4 vervangen door parameter
		colorSensorR = new ColorSensor(SensorPort.S2);
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
        Lcd.print(3, "Press ENTER to start calibrate");
        Button.waitForAnyPress();
        calibrateFinish();
        calibrateLine();
        calibrateBackground();
	}
	
	private void calibrateFinish() {
        askUserInput();
        Lcd.clear();
        Lcd.print(4, "Place bot on finishline");
        Lcd.print(5, "Press ENTER to calibrate");
        Button.waitForAnyPress();
        Lcd.print(6, "Calibrating..");
        colorFinishL = getAverageRedValue(colorSensorL);
        colorFinishR = getAverageRedValue(colorSensorR);
        Lcd.print(7, "SUCCES!");
        Delay.msDelay(250);
	}

	private void calibrateLine() {
        askUserInput();
        Lcd.clear();
        Lcd.print(4, "Place bot on racing line");
        Lcd.print(5, "Press ENTER to calibrate");
        Button.waitForAnyPress();
        Lcd.print(6, "Calibrating..");
        colorLineL = getAverageRedValue(colorSensorL);
        colorLineR = getAverageRedValue(colorSensorR);
        Lcd.print(7, "SUCCES!");
        Delay.msDelay(250);
	}
	
	private void calibrateBackground() {
        askUserInput();
        Lcd.clear();
        Lcd.print(4, "Place bot on background");
        Lcd.print(5, "Press ENTER to calibrate");
        Button.waitForAnyPress();
        Lcd.print(6, "Calibrating..");
        colorBackgroundL = getAverageRedValue(colorSensorL);
        colorBackgroundR = getAverageRedValue(colorSensorR);
        Lcd.print(7, "SUCCES!");
        Delay.msDelay(250);
	}
	
	private double getAverageRedValue(ColorSensor colorSensor) {
		float sum = 0;
        for(int i = 0; i < CALIBRATION_CYCLES; i++) {
        	Delay.msDelay(250);
        	sum += colorSensor.getRed();
        }
		return (double) sum/CALIBRATION_CYCLES;
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
