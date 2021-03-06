package model.linefollower;

import lejos.hardware.Button;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.robotics.Color;
import lejos.utility.Delay;
import utility.ColorSensor;
import utility.Stopwatch;


/**
 * 
 * @author Tom
 * Drive class is used for the actual driving of the robot
 * this class contains the algorithm to follow the line and
 * supporting driving methods to go forward, turn and scan current position.
 */
public class Drive {
	
	/**
	 * The speed of the unregulated motors.
	 */
	private final static int POWER = 40;
	
	/**
	 * Max speed of the bot with unregulated motors
	 */
	private final static int MAX_SPEED = 120;
	
	/**
	 * Threshold used to determine the background is white enough to 
	 * drive straight forward.
	 */
	private final static int STRAIGHT_FORWARD_THRESHOLD = 100;
	
	/**
	 * Correction of the speed on a straight line.
	 */
	private final static int STRAIGHT_LINE_BOOST = 10;
	
	/**
	 * Turning threshold to follow black line.
	 * When the cumul value of the R G B colors goes below this
	 * threshold, a strong turn is needed.
	 */
	private final static int BLACK_TRESHOLD = 140;
	
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
    private Calibrator calibrator;
    
    /**
     * Absolute RGB value per sensor used for driving
     * 
     */
    private int colorBackgroundCumulativeL, colorBackgroundCumulativeR;
	
    
    /**
     * Drive constructor initializes the motors, colorsensors,
     * calibrated finish values and calibrator object obtained from the launcher. 
     * @param launcher
     */
	public Drive(Launcher launcher) {
		this.motorL = launcher.getMotor('L');
		this.motorR = launcher.getMotor('R');
		this.colorSensorL = launcher.getColorSensor('L');
		this.colorSensorR = launcher.getColorSensor('R');
		this.colorFinishMin = launcher.getColorFinishMin();
		this.colorFinishMax = launcher.getColorFinishMax();
		this.calibrator = launcher.getCalibrator();
	}
	
	/**
	 * algorithm to follow the line 
	 */
	void followLine() {
    	
		/*
		 * variable to indicate start of lap
		 * starts false and is turned true when finish line
		 * is passed for first time
		 */
		boolean running = false;
		
		/*
		 * stopwatch to keep track of time while driving
		 */
		Stopwatch stopwatch = new Stopwatch();
		
		/*
		 * start driving forward full speed until start is reached
		 */
		driveForward(MAX_SPEED);

		while (Button.ESCAPE.isUp()) {
			
			/*
			 * Scan position of both sensors in R G B.
			 * This needs to be R G B as finish line is checked first in R G B
			 * driving will be with cumul value
			 */
	    	int[] RgbPositionL = detectPosition(colorSensorL);
	    	int[] RgbPositionR = detectPosition(colorSensorR);
	    	
	    	/*
	    	 * get cumul value of scanned R G B positions, used for the actual
	    	 * following of the black line.
	    	 */
	    	int positionL = calibrator.calculateCumulRgbValue(RgbPositionL);
	    	int positionR = calibrator.calculateCumulRgbValue(RgbPositionR);
	    	
	    	/*
	    	 * At the start of every loop, scan dor the finish line.
	    	 * When the finish line is passed for the first time, stopwatch
	    	 * is started and running is set to true.
	    	 * When the finish line is passed for the second time, stopwatch
	    	 * is turned off and followline loop is terminated.
	    	 */
	    	if(isFinish(RgbPositionL, RgbPositionR)) {
	    		View.alertUp();
	    		if(running) {
	    			stopwatch.stop();
	    			break;
	    		} else {
	    			running = true;
	    			stopwatch.start();
	    		}
	    	}
	    	
	    	/*
	    	 * If the lap has not started yet, skip blackline detection and
	    	 * keep scanning for finish in methods above.
	    	 * Bot keeps driving full speed ahead.
	    	 */
	    	if(!running) continue;
	    	
	    	
	    	/*
	    	 * Keep track of current position with stopwatch and printing results to the screen
	    	 */
	    	View.printElapsedTime(stopwatch);
	    	View.printRgbPosition('L', RgbPositionL[0], RgbPositionL[1], RgbPositionL[2]);
	    	
	    	/*
	    	 * Check if cumulvalue of left scanner is lower than calibrated value.
	    	 * Black is low value, white is high value.
	    	 * A lower current position indicates more black values.
	    	 * If the left sensors sees black, the bot turns left to get back in the white.
	    	 */
	    	if(positionL < colorBackgroundCumulativeL) {
	    		turnLeftLight(POWER);
	    	}
	    	
	    	/*
	    	 * When the preset black treshold is exceeded, trigger a hard turn.
	    	 * We loop this turn until the threshold is back in the clear.
	    	 * During the loop, new scans are made. 
	    	 */
	    	while(positionL < BLACK_TRESHOLD && Button.ESCAPE.isUp()) {
	    		positionL = calibrator.calculateCumulRgbValue(detectPosition(colorSensorL));
	    		turnLeftHard(POWER);
	    	}
	    	
	    	/*
	    	 * Keep track of current position with stopwatch and printing results to the screen
	    	 */
	    	positionR = calibrator.calculateCumulRgbValue(detectPosition(colorSensorR));
	    	View.printRgbPosition('R', RgbPositionR[0], RgbPositionR[1], RgbPositionR[2]);
	    	
	    	/*
	    	 * Check if cumulvalue of right scanner is lower than calibrated value.
	    	 * Black is low value, white is high value.
	    	 * A lower current position indicates more black values.
	    	 * If the right sensors sees black, the bot turns right to get back in the white.
	    	 */
	    	if(positionR < colorBackgroundCumulativeR) {
	    		turnRightLight(POWER);
	    	}

	    	/*
	    	 * When the preset black treshold is exceeded, trigger a hard turn.
	    	 * We loop this turn until the threshold is back in the clear.
	    	 * During the loop, new scans are made. 
	    	 */
	    	while(positionR < BLACK_TRESHOLD  && Button.ESCAPE.isUp()) {
	    		positionR = calibrator.calculateCumulRgbValue(detectPosition(colorSensorR));
	    		turnRightHard(POWER);
	    	}
	    	
	    	/*
	    	 * When both sensors exceed this thresholds, they are both far enough from the line
	    	 * to drive straight forward. An acceleration boost is given as will.
	    	 */
	    	if(positionL > STRAIGHT_FORWARD_THRESHOLD && positionR > STRAIGHT_FORWARD_THRESHOLD) {
	    		driveForward(POWER+STRAIGHT_LINE_BOOST);
	    	}
		}
		
    	//stop motors -- is buiten de while loop die stopt bij escape
		
		/*
		 * When the driving algorithm is exited, the motors are stopped and the
		 * stopwatch results are printed.
		 * Delays are used to give the sensors enough time to properly shutdown.
		 */
		Delay.msDelay(250);
		stopMotor();
		Delay.msDelay(250);
    	View.printFinal(stopwatch);
 
    }
	
	/**
	 * Triggers a sharp turn to the right by driving forwards with left track
	 * and backwards with right track.
	 * We drive forward faster than backward to avoid getting stuck on very sharp corners,
	 * this keeps pushing the bot forward.
	 * @param power
	 */
	private void turnRightHard(int power) {
		motorL.forward();
		motorL.setPower(power+20+20);
		motorR.backward();
		motorR.setPower(power+20);
	}

	
	/**
	 * Light correction to the right by increasing power of left track
	 * and driving backwards slowly with right track.
	 * @param power
	 */
	private void turnRightLight(int power) {
		motorL.forward();
		motorL.setPower(power + 10);
		motorR.backward();
		motorR.setPower(power/2);
	}

	/**
	 * Triggers a sharp turn to the left by driving forwards with right track
	 * and backwards with left track.
	 * We drive forward faster than backward to avoid getting stuck on very sharp corners,
	 * this keeps pushing the bot forward.
	 * @param power
	 */
	private void turnLeftHard(int power) {
		motorR.forward();
		motorR.setPower(power+20+15);
		motorL.backward();
		motorL.setPower(power+20);
	}

	/**
	 * Light correction to the left by increasing power of right track
	 * and driving backwards slowly with left track.
	 * @param power
	 */
	private void turnLeftLight(int power) {
		motorR.forward();
		motorR.setPower(power);
		motorL.backward();
		motorL.setPower(power/2);
	}

	/**
	 * Set both motors forward and drive with equal power
	 * @param power
	 */
	private void driveForward(int power) {
		motorL.forward();
		motorR.forward();
		motorL.setPower(power);
		motorR.setPower(power);
	}
	
	/**
	 * Stop both motors
	 */
	private void stopMotor() {
	   	motorL.stop();
    	motorR.stop();
	}


//geen loopjes omdat we maximale performantie willen
	private boolean isFinish(int[] positionL, int[] positionR) {	
		
		if(positionL[0] > colorFinishMin[0] && positionL[0] < colorFinishMax[0] &&
		   positionL[1] > colorFinishMin[1] && positionL[1] < colorFinishMax[1] &&
		   positionL[2] > colorFinishMin[2] && positionL[2] < colorFinishMax[2]) return true;
		
		if(positionR[0] > colorFinishMin[0] && positionR[0] < colorFinishMax[0] &&
		   positionR[1] > colorFinishMin[1] && positionR[1] < colorFinishMax[1] &&
		   positionR[2] > colorFinishMin[2] && positionR[2] < colorFinishMax[2]) return true;
		
		return false;
	}

	private int[] detectPosition(ColorSensor colorSensor) {
		Color rgb = colorSensor.getColor();
		return new int[] {rgb.getRed(), rgb.getGreen(), rgb.getBlue()};
	}
}