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
    private UnregulatedMotor motorL, motorR;

    
    /*
     * The bot drives on two color sensors, left and right
     */
    private ColorSensor colorSensorL, colorSensorR;
    
    
    /*
     * colorFinish is an array with R G B values from the finishline
     */
    private int[] colorFinishMax, colorFinishMin;

    
    /*
     * absolute RGB value per sensor used for driving
     */
    private int colorBackgroundCumulativeL, colorBackgroundCumulativeR;
    
    
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
    private Calibrator calibrator;


    
	/*
	 * Constructor, requires port allocation to start program    
	 */
    public Launcher(Port leftMotor, Port rightMotor, Port leftColorSensor, Port rightColorSensor) {
    	
    	setPorts(leftMotor, rightMotor, leftColorSensor, rightColorSensor);
    	
    	View.printTitle();
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
     * @Parameter: Port, Port, Port, Port
     * 				Ports required to allocate sensors and motors
     * @Return: void
     * 				Class variables are allocated
     */
	private void setPorts(Port leftMotor, Port rightMotor, Port leftColorSensor, Port rightColorSensor) {
		colorSensorR = new ColorSensor(rightColorSensor, "colorSensorR");
		colorSensorL = new ColorSensor(leftColorSensor, "colorSensorL");
    	motorR = new UnregulatedMotor(rightMotor);
    	motorL = new UnregulatedMotor(leftMotor);
    }
	
	/*
	 * Assign RGB mode to a sensor and turn on appropriate flood light
	 * @Parameter: ColorSensor
	 * 					sensor that needs to be prepared
	 * @Return: void
	 * 				sensor object is set
	 */
	private void prepareRGBSensor(ColorSensor colorSensor) {
        colorSensor.setRGBMode();
        colorSensor.setFloodLight(Color.WHITE);
        colorSensor.setFloodLight(true);
	}
    
	/*
	 * handle all required calibrations
	 */
	private void startCalibration() {
	    
	    calibrator = new Calibrator(CALIBRATION_CYCLES,
	    							CALIBRATION_DELAY,
	    							CALIBRATION_FINISH_MARGIN_ABS, 
	    							CALIBRATION_FINISH_MARGIN_REL);
		
		
        View.prepareUserForCalibration("Finish");
        calibrator.calibrateSurface(colorSensorL, "finish");
        calibrator.calibrateSurface(colorSensorR, "finish");
        calibrator.calibrateFinishMinMax(colorSensorL);
        calibrator.calibrateFinishMinMax(colorSensorR);
        
        /*
         * Allocate finish color range to RGB int arrays
         * currently finish range (min max) is hardcoded,
         * for this reason we are only pulling in the range of colorSensorL
         * as L and R are the same
         * TODO: Optimalization of finishrange calibration
         */
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

    UnregulatedMotor getMotor(char motor) {
    	if(motor == 'L')
    		return motorL;
    	else
    		return motorR;
    }
	
	ColorSensor getColorSensor(char sensor) {
	    if(sensor == 'L')
	    	return colorSensorL;
	    else
	    	return colorSensorR;
	}
	
    int[] getColorFinishMax() {
    	return colorFinishMax;
    }
    
    int[] getColorFinishMin() {
    	return colorFinishMin;
    }
    
    int colorBackgroundCumulative(char sensor) {
    	if(sensor == 'L') {
    		return colorBackgroundCumulativeL;
    	} else
    		return colorBackgroundCumulativeR;
    }

	public Calibrator getCalibrator() {
		return calibrator;
	}
}
