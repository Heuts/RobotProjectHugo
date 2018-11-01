package model.linefollower;

import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;
import lejos.utility.Delay;
import utility.ColorSensor;

/**
 * 
 * @author Tom
 * Launcher class is the start of the linefollower app.
 * This class sets the sensors and motors and provides these
 * to the appropriate classes. It is responsible for the flow of the application
 */
public class Launcher {
	/**
	 * The bot has a left and right track with each one motor.
	 * The linefollower does this with unregulated motors.
	 */
    private UnregulatedMotor motorL, motorR;

    
    /**
     * The bot drives on two color sensors, left and right.
     */
    private ColorSensor colorSensorL, colorSensorR;
    
    
    /**
     * ColorFinish is an array with R G B values from the finishline.
     * We have two ranges, a max and min range.
     * These are the calibrated finish RGB values adjusted with a margin
     * to increase the range of the finish color. When scanning for the
     * finish, we will see whether the values are in between the min and
     * max ranges.
     */
    private int[] colorFinishMax, colorFinishMin;

    
    /**
     * Calibration values to be set
     * These determine the precision of the calibration methods
     */
    private final static int CALIBRATION_CYCLES = 4;
    private final static int CALIBRATION_DELAY = 250;
    private final static double CALIBRATION_FINISH_MARGIN_ABS = 10;
    private final static double CALIBRATION_FINISH_MARGIN_REL = 0.1;
    
	
	/**
	 * Create calibrator to handle calibrations
	 */
    private Calibrator calibrator;

    
	/**
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
		View.alertUp();
		View.printStartCalibration();
        View.waitAny();
        
    	startCalibration();
    	
    	View.alertUp();
    	View.waitAny();

    	/*
    	 * Start driving
    	 */
    	Drive drive = new Drive(this);
    	drive.followLine();
    	
    	/*
    	 * End program
    	 */
        View.alertUp();
        View.waitAny();
    	View.printShutdown();
    	closeSensors();
    	Delay.msDelay(250);
	}

    /**
     * Allocate appropriate ports received from constructor
     * @param: Port, Port, Port, Port
     * 				Ports required to allocate sensors and motors
     * @return: void
     * 				Class variables are allocated
     */
	private void setPorts(Port leftMotor, Port rightMotor, Port leftColorSensor, Port rightColorSensor) {
		colorSensorR = new ColorSensor(rightColorSensor, "colorSensorR");
		colorSensorL = new ColorSensor(leftColorSensor, "colorSensorL");
    	motorR = new UnregulatedMotor(rightMotor);
    	motorL = new UnregulatedMotor(leftMotor);
    }
	
	/**
	 * Assign RGB mode to a sensor and turn on appropriate flood light
	 * @param: ColorSensor
	 * 					sensor that needs to be prepared
	 * @return: void
	 * 				sensor object is set
	 */
	private void prepareRGBSensor(ColorSensor colorSensor) {
        colorSensor.setRGBMode();
        colorSensor.setFloodLight(Color.WHITE);
        colorSensor.setFloodLight(true);
	}
    
	/**
	 * creates calibrator object
	 * handles calibration of finish rgb, max and min range of finish and background rgb
	 * these are all calculated per sensor
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

        View.CalibrationSuccess();
        
        calibrator.printCalibration();
	}

	/**
	 * This method should be called at the end of the program
	 * ensures all motors and sensors are closed properly
	 * to avoid memory leaks
	 */
	private void closeSensors() {
        motorL.close();
        motorR.close();
    	colorSensorL.close();
    	colorSensorR.close();
	}

	/**
	 * Get motor based on char L or R
	 * @parameter: char
	 * 				side of motor
	 * @return: UnregulatedMotor
	 * 				motor object is returned
	 */
    UnregulatedMotor getMotor(char motor) {
    	if(motor == 'L')
    		return motorL;
    	else
    		return motorR;
    }

	/**
	 * returns sensor based on char L or R
	 * @param: char 
	 * 				side of sensor
	 * @return: ColorSensor
	 * 				sensor object is returned
	 */
	ColorSensor getColorSensor(char sensor) {
	    if(sensor == 'L')
	    	return colorSensorL;
	    else
	    	return colorSensorR;
	}
	
	/**
	 * returns maximum of color range of finishline
	 * these are the max values for every individual color
	 * @return int array with r g b values
	 */
    int[] getColorFinishMax() {
    	return colorFinishMax;
    }
    

	/**
	 * returns minimum of color range of finishline
	 * these are the min values for every individual color
	 * @return int array with r g b values
	 */
    int[] getColorFinishMin() {
    	return colorFinishMin;
    }


	/**
	 * returns calibrator object, used to access rgb calculation methods
	 * @return calibrator
	 */
	public Calibrator getCalibrator() {
		return calibrator;
	}
}
