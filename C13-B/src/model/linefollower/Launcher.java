package model.linefollower;

import lejos.hardware.Button;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;
import lejos.utility.Delay;
import model.Stopwatch;
import utility.ColorSensor;
import utility.Lcd;


//TODO: maak calibratiemethodes generieker, teveel herhaling => calibratie class
//TODO: berichten op lcd uitlijnen
public class Launcher {
	/*
	 * The bot has a left and right track with each one motor
	 */
    private UnregulatedMotor motorL;
    private UnregulatedMotor motorR;
    
    /*
     * The bot drives on two color sensors, left and right
     */
    private ColorSensor colorSensorL;
    private ColorSensor colorSensorR;
    
    /*
     * colorFinish is an array which consists of two arrays
     * 		first array with size == amount of sensors
     * 		second array with size == 3
     * 			values for: Red Green Blue
     */
//    private int[][] colorFinish;
    private int[][] colorFinishMax = new int[2][3];
    private int[][] colorFinishMin = new int[2][3];
    
    /*
     * colorBackground is an array which consists of two arrays
     * 		first array with size == amount of sensors
     * 		second array with size == 3
     * 			values for: Red Green Blue
     */
//    private int[][] colorBackground;
    
    /*
     * absolute RGB value per sensor
     */
    private int[] colorBackgroundAbsolute;
    
    
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

    	Launcher launcher = new Launcher();
    	launcher.launch(LEFT_MOTOR, RIGHT_MOTOR, LEFT_COLOR, RIGHT_COLOR);
    }
    
    //TODO: launch zal constructor worden
    private void launch(Port leftMotor, Port rightMotor, Port leftColorSensor, Port rightColorSensor) {
    	
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
    	 * Start follow line 
    	 */
    	followLine();
    	
    	/*
    	 * End program
    	 */
    	View.printFinal();
        View.alert();
        View.waitAny();
    	View.printShutdown();
    	closeSensors();
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
        View.CalibrationSuccess();
        
        View.prepareUserForCalibration("Background");
        calibrator.calibrateSurface(colorSensorL, "background");
        calibrator.calibrateSurface(colorSensorR, "background");
        
        colorBackgroundAbsolute = new int[] {calculateAbsolutePosition(colorBackground[0]), 
        		calculateAbsolutePosition(colorBackground[1])};
        View.CalibrationSuccess();
	}
	
	
	private void printCalibration() {
        Lcd.clear();
        Lcd.print(1, "Calibration results:");
        Lcd.print(2, "FL: %d %d %d", colorFinish[0][0], colorFinish[0][1], colorFinish[0][2]);
        Lcd.print(3, "FR: %d %d %d",  colorFinish[1][0], colorFinish[1][1], colorFinish[1][2]);
        Lcd.print(6, "BL: %d %d %d", colorBackground[0][0], colorBackground[0][1], colorBackground[0][2]);
        Lcd.print(7, "BR: %d %d %d", colorBackground[1][0], colorBackground[1][1], colorBackground[1][2]);
	}

	private void followLine() {
    	
		int power = 50;
		
		boolean running = false;
		Stopwatch stopwatch = new Stopwatch();
		
		driveForward(power);

    	
		while (Button.ESCAPE.isUp()) {
			
			
	    	int[] RgbPositionL = detectPosition(colorSensorL);
	    	int[] RgbPositionR = detectPosition(colorSensorR);
	    	
	    	int positionL = calculateAbsolutePosition(RgbPositionL);
	    	int positionR = calculateAbsolutePosition(RgbPositionR);
	    	
	    	if(isFinish(RgbPositionL, RgbPositionR)) {
	    		View.alert();
	    		if(running) {
	    			break;
	    		} else {
	    			running = true;
	    			stopwatch.start();
	    		}
	    	}
	    	Lcd.clear(8);
	    	Lcd.print(8, "%d", stopwatch.getElapsedTimeSecs());
	    	Lcd.print(4, "L Sensor: %d %d %d", RgbPositionL[0], RgbPositionL[1], RgbPositionL[2]);
	    	Lcd.print(5, "L Finish: %d %d %d", colorFinish[0][0], colorFinish[0][1], colorFinish[0][2]);
	    	
	    	//als hij naar rechts afwijkt
	    	//draai links licht
	    	if(positionL < colorBackgroundAbsolute[0]) {
	    		motorR.forward();
	    		motorR.setPower(power);
	    		motorL.backward();
	    		motorL.setPower(power/2);
	    	}
	    	
	    	//als linkersensor boven zwarte lijn is
	    	//blijf naar links draaien
	    	while(positionL < 140 && Button.ESCAPE.isUp()) {
	    		positionL = calculateAbsolutePosition(detectPosition(colorSensorL));
	    		motorR.forward();
	    		motorR.setPower(power+20+15);
	    		motorL.backward();
	    		motorL.setPower(power+20);
	    	}
	    	
	    	positionR = calculateAbsolutePosition(detectPosition(colorSensorR));
	    	Lcd.print(6, "R Sensor: %d %d %d", RgbPositionR[0], RgbPositionR[1], RgbPositionR[2]);
	    	Lcd.print(7, "R Finish: %d %d %d", colorFinish[1][0], colorFinish[1][1], colorFinish[1][2]);
	    	
	    	//als rechtersensor grijzer wordt
	    	//draai naar links
	    	if(positionR < colorBackgroundAbsolute[1]) {
	    		motorL.forward();
	    		motorL.setPower(power + 10);
	    		motorR.backward();
	    		motorR.setPower(power/2);
	    	}

	    	//als rechtersensor boven zwart is
	    	//blijf naar rechts draaien
	    	while(positionR < 140  && Button.ESCAPE.isUp()) {
	    		positionR = calculateAbsolutePosition(detectPosition(colorSensorR));
	    		motorL.forward();
	    		motorL.setPower(power+20+20); //sterker om te zwakkere motor te corrigeren
	    		motorR.backward();
	    		motorR.setPower(power+20); //sterker om zwakkere motor te corrigeren
	    	}
	    	
	    	//rij rechtdoor als beide sensors wit genoeg zijn
	    	if(positionL > 100 && positionR > 100) {
	    		driveForward(power+10);
	    	}
	    		
	    	
		}
		
    	//stop motors -- is buiten de while loop die stopt bij escape
		stopMotor();
 
    }
	
	private void stopMotor() {
	   	motorL.stop();
    	motorR.stop();
	}

	private void driveForward(int power) {
		motorL.forward();
		motorR.forward();
		motorL.setPower(power);
		motorR.setPower(power);
	}

//TODO: loopjes maken evt positionL en R in array steken als het helpt
	private boolean isFinish(int[] positionL, int[] positionR) {
		
		if(positionL[0] > colorFinishMin[0][0] && positionL[0] < colorFinishMax[0][0] &&
		   positionL[1] > colorFinishMin[0][1] && positionL[1] < colorFinishMax[0][1] &&
		   positionL[2] > colorFinishMin[0][2] && positionL[2] < colorFinishMax[0][2]) return true;
		
		if(positionR[0] > colorFinishMin[1][0] && positionR[0] < colorFinishMax[1][0] &&
		   positionR[1] > colorFinishMin[1][1] && positionR[1] < colorFinishMax[1][1] &&
		   positionR[2] > colorFinishMin[1][2] && positionR[2] < colorFinishMax[1][2]) return true;
		
		return false;
	}

	private int[] detectPosition(ColorSensor colorSensor) {
		Color rgb = colorSensor.getColor();
		return new int[] {rgb.getRed(), rgb.getGreen(), rgb.getBlue()};
	}
	
	private int calculateAbsolutePosition(int[] RgbValues) {
		int sum = 0;
		for(int color: RgbValues) {
			sum += color;
		}
		return sum;
	}

	private void closeSensors() {
        motorL.close();
        motorR.close();
    	colorSensorL.close();
    	colorSensorR.close();
	}

}
