package model.linefollower;

import lejos.hardware.Button;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.robotics.Color;
import lejos.utility.Delay;
import utility.ColorSensor;
import utility.Stopwatch;

public class Drive {
	
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
	
	public Drive(Launcher launcher) {
		this.motorL = launcher.getMotor('L');
		this.motorR = launcher.getMotor('R');
		this.colorSensorL = launcher.getColorSensor('L');
		this.colorSensorR = launcher.getColorSensor('R');
		this.colorFinishMin = launcher.getColorFinishMin();
		this.colorFinishMax = launcher.getColorFinishMax();
		this.calibrator = launcher.getCalibrator();
	}
	
	void followLine() {
    	
		int power = 40;
		
		boolean running = false;
		Stopwatch stopwatch = new Stopwatch();
		
		driveForward(power+80); //start met rechtdoor rijden tot de start

		while (Button.ESCAPE.isUp()) {
			
	    	int[] RgbPositionL = detectPosition(colorSensorL);
	    	int[] RgbPositionR = detectPosition(colorSensorR);
	    	
	    	int positionL = calibrator.calculateCumulRgbValue(RgbPositionL);
	    	int positionR = calibrator.calculateCumulRgbValue(RgbPositionR);
	    	
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
	    	
	    	if(!running) continue;
	    	
	    	View.printElapsedTime(stopwatch);
	    	View.printRgbPosition('L', RgbPositionL[0], RgbPositionL[1], RgbPositionL[2]);
	    	
	    	if(positionL < colorBackgroundCumulativeL) {
	    		turnLeftLight(power);
	    	}
	    	
	    	while(positionL < 140 && Button.ESCAPE.isUp()) {
	    		positionL = calibrator.calculateCumulRgbValue(detectPosition(colorSensorL));
	    		turnLeftHard(power);
	    	}
	    	
	    	positionR = calibrator.calculateCumulRgbValue(detectPosition(colorSensorR));
	    	View.printRgbPosition('R', RgbPositionR[0], RgbPositionR[1], RgbPositionR[2]);
	    	
	    	if(positionR < colorBackgroundCumulativeR) {
	    		turnRightLight(power);
	    	}

	    	while(positionR < 140  && Button.ESCAPE.isUp()) {
	    		positionR = calibrator.calculateCumulRgbValue(detectPosition(colorSensorR));
	    		turnRightHard(power);
	    	}
	    	
	    	if(positionL > 100 && positionR > 100) {
	    		driveForward(power+10);
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