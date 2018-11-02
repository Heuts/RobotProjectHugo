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
	 * The speed of the unregulated motors
	 */
	private final static int POWER = 40;
	
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
		driveForward(POWER+80);

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
	    	
	    	
	    	while(positionL < 140 && Button.ESCAPE.isUp()) {
	    		positionL = calibrator.calculateCumulRgbValue(detectPosition(colorSensorL));
	    		turnLeftHard(POWER);
	    	}
	    	
	    	positionR = calibrator.calculateCumulRgbValue(detectPosition(colorSensorR));
	    	View.printRgbPosition('R', RgbPositionR[0], RgbPositionR[1], RgbPositionR[2]);
	    	
	    	if(positionR < colorBackgroundCumulativeR) {
	    		turnRightLight(POWER);
	    	}

	    	while(positionR < 140  && Button.ESCAPE.isUp()) {
	    		positionR = calibrator.calculateCumulRgbValue(detectPosition(colorSensorR));
	    		turnRightHard(POWER);
	    	}
	    	
	    	if(positionL > 100 && positionR > 100) {
	    		driveForward(POWER+10);
	    	}
		}
		
    	//stop motors -- is buiten de while loop die stopt bij escape
		
		Delay.msDelay(250);
		stopMotor();
		Delay.msDelay(250);
    	View.printFinal(stopwatch);
 
    }
	
	private void turnRightHard(int power) {
		motorL.forward();
		motorL.setPower(power+20+20); //sterker om te zwakkere motor te corrigeren
		motorR.backward();
		motorR.setPower(power+20); //sterker om zwakkere motor te corrigeren
	}

	private void turnRightLight(int power) {
		motorL.forward();
		motorL.setPower(power + 10);
		motorR.backward();
		motorR.setPower(power/2);
	}

	private void turnLeftHard(int power) {
		motorR.forward();
		motorR.setPower(power+20+15);
		motorL.backward();
		motorL.setPower(power+20);
	}

	private void turnLeftLight(int power) {
		motorR.forward();
		motorR.setPower(power);
		motorL.backward();
		motorL.setPower(power/2);
	}

	private void driveForward(int power) {
		motorL.forward();
		motorR.forward();
		motorL.setPower(power);
		motorR.setPower(power);
	}
	
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