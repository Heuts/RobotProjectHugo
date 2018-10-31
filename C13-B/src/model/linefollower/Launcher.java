package model.linefollower;

import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;
import lejos.utility.Delay;
import utility.ColorSensor;


public class Launcher {
	/*
	 * The bot has a left and right track with each one motor
	 */
    UnregulatedMotor motorL, motorR;
    
    /*
     * The bot drives on two color sensors, left and right
     */
    ColorSensor colorSensorL, colorSensorR;
    
    /*
     * colorFinish is an array which consists of two arrays
     * 		first array with size == amount of sensors
     * 		second array with size == 3
     * 			values for: Red Green Blue
     */
//    private int[][] colorFinish;
    int[] colorFinishMax, colorFinishMin;
    
    /*
     * absolute RGB value per sensor
     */
    int colorBackgroundCumulativeL, colorBackgroundCumulativeR;
    
    /*
     * Calibration values
     */
    private final static int CALIBRATION_CYCLES = 4;
    private final static int CALIBRATION_DELAY = 250;
    private final static double CALIBRATION_FINISH_MARGIN_ABS = 10;
    private final static double CALIBRATION_FINISH_MARGIN_REL = 0.1;
    
	
	/*
	 * Create calibrator to handle calibrations
	 */
    Calibrator calibrator = new Calibrator(CALIBRATION_CYCLES,
    									   CALIBRATION_DELAY,
    									   CALIBRATION_FINISH_MARGIN_ABS, 
    									   CALIBRATION_FINISH_MARGIN_REL);
	
	
    /*
     * tijdelijke functie die weggaat bij integratie in project, zal 
     * de launch oproepen die de constructor zal worden
     */
    public static void main(String[] args)
    {
    	
    	Port LEFT_COLOR = SensorPort.S1,
				  RIGHT_COLOR = SensorPort.S2,
				  LEFT_MOTOR = MotorPort.A,
				  RIGHT_MOTOR = MotorPort.D;

    	Launcher launcher = new Launcher(LEFT_MOTOR, RIGHT_MOTOR, LEFT_COLOR, RIGHT_COLOR);
    }
    
    public Launcher(Port leftMotor, Port rightMotor, Port leftColorSensor, Port rightColorSensor) {
    	
    	/*
    	 * Allocation of hardware to variables
    	 */
    	setPorts(leftMotor, rightMotor, leftColorSensor, rightColorSensor);
    	
    	View.printTitle();
    	
    	/*
    	 * Prepare sensors
    	 */
    	View.printBooting();
    	prepareRGBSensor(colorSensorL);
    	prepareRGBSensor(colorSensorR);
    	View.printBootingReady();
    	
    	
		/*
		 * Start calibration
		 */
		View.alert();
		View.printStartCalibration();
        View.waitAny();
    	startCalibration();
//    	printCalibration();
    	View.alert();
    	View.waitAny();

    	/*
    	 * Start driving
    	 */
    	Drive drive = new Drive(this);
    	drive.followLine();
    	
    	/*
    	 * End program
    	 */

        View.alert();
        View.waitAny();
    	View.printShutdown();
    	closeSensors();
    	Delay.msDelay(250);
	}

    /*
     * Allocate appropriate ports received from constructor
     */
	private void setPorts(Port leftMotor, Port rightMotor, Port leftColorSensor, Port rightColorSensor) {
		colorSensorR = new ColorSensor(rightColorSensor, "colorSensorR");
		colorSensorL = new ColorSensor(leftColorSensor, "colorSensorL");
    	motorR = new UnregulatedMotor(rightMotor);
    	motorL = new UnregulatedMotor(leftMotor);
    }
	
	/*
	 * Assign RGB mode to a sensor and turn on appropriate flood light
	 */
	private void prepareRGBSensor(ColorSensor colorSensor) {
        colorSensor.setRGBMode();
        colorSensor.setFloodLight(Color.WHITE);
        colorSensor.setFloodLight(true);
	}
    
	/*
	 * handle different calibrations
	 */
	private void startCalibration() {
        View.prepareUserForCalibration("Finish");
        calibrator.calibrateSurface(colorSensorL, "finish");
        calibrator.calibrateSurface(colorSensorR, "finish");
        calibrator.calibrateFinishMinMax(colorSensorL);
        calibrator.calibrateFinishMinMax(colorSensorR);
        
        //maakt niet uit dat het L is want finish momenteel hard coded
        colorFinishMax = calibrator.colorManager.getSensor(colorSensorL.getName()).getMap("finishMax").getRgb();
        colorFinishMin = calibrator.colorManager.getSensor(colorSensorL.getName()).getMap("finishMin").getRgb();
        View.CalibrationSuccess();
        
        View.prepareUserForCalibration("Background");
        calibrator.calibrateSurface(colorSensorL, "background");
        calibrator.calibrateSurface(colorSensorR, "background");

        colorBackgroundCumulativeL = calibrator.calculateCumulRgbValue(colorSensorL, "background");
        colorBackgroundCumulativeL = calibrator.calculateCumulRgbValue(colorSensorR, "background");
        View.CalibrationSuccess();
        
        calibrator.printCalibration();
	}
	
	private void closeSensors() {
        motorL.close();
        motorR.close();
    	colorSensorL.close();
    	colorSensorR.close();
	}

}
