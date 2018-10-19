package model;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.Color;
import lejos.utility.Delay;
import customrobot.library.*;

//127 is max vooruit, 128 is max achteruit
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
    private int[][] colorFinish;
    private int[][] colorBackground;
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
    	
    	Lcd.print(2, "Sensors ready");
    	
    	startCalibration();
    	printCalibration();
    	askUserInput();
    	Button.waitForAnyPress();
    	
//    	followLine();
    	
    	askUserInput();
    	Button.waitForAnyPress();
        
        endOfProgram();
	}


	private void followLine() {
    	
		int power = 50;
		
		motorL.forward();
		motorR.forward();
		motorL.setPower(power);
		motorR.setPower(power);
    	
		while (Button.ESCAPE.isUp()) {
	    	float positionL = detectPosition(colorSensorL);
	    	int rechthoek;
	    	
	    	if(isFinish())
	    		break;
	    	
	    	Lcd.print(4, "Links: %.3f", positionL);
	    	
	    	//als hij naar rechts afwijkt
	    	//draai links licht
	    	if(positionL < straightLinePosition[0]) {
	    		motorR.forward();
	    		motorR.setPower(power);
	    		motorL.backward();
	    		motorL.setPower(power/2);
	    	}
	    	
	    	//als linkersensor boven zwarte lijn is
	    	//blijf blijf naar links draaien
	    	while(positionL < 0.3 && Button.ESCAPE.isUp()) {
	    		positionL = detectPosition(colorSensorL);
	    		motorR.forward();
	    		motorR.setPower(power+15); //assymetrisch zwiepen
	    		motorL.backward();
	    		motorL.setPower(power);
	    	}
	    	
	    	float positionR = detectPosition(colorSensorR);
	    	Lcd.print(5, "Rechts: %.3f", positionR);
	    	
	    	//als rechtersensor grijzer wordt
	    	//draai naar links
	    	if(positionR < straightLinePosition[1]) {
	    		motorL.forward();
	    		motorL.setPower(power + 10);
	    		motorR.backward();
	    		motorR.setPower(power/2);
	    		
	    	}

	    	//als rechtersensor boven zwart is
	    	//blijf naar links draaien
	    	while(positionR < 0.3  && Button.ESCAPE.isUp()) {
	    		positionR = detectPosition(colorSensorR);
	    		motorL.forward();
	    		motorL.setPower(power+20+20); //sterker om te zwakkere motor te corrigeren
	    		motorR.backward();
	    		motorR.setPower(power+20); //sterker om zwakkere motor te corrigeren
	    	}
	    	
	    	//rij rechtdoor als beide sensors wit genoeg zijn
	    	if(positionL > 0.5 && positionR > 0.5) {
	    		motorL.forward();
	    		motorL.setPower(power+10);
	    		motorR.forward();
	    		motorR.setPower(power+10);
	    	}
	    		
	    	
		}
		
    	//stop motors -- is buiten de while loop die stopt bij escape
    	motorL.stop();
    	motorR.stop();
    }


	private boolean isFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	private float detectPosition(ColorSensor colorSensor) {
		return colorSensor.getRed();
	}

	private void endOfProgram() {
    	closeSensors();
        Lcd.print(6, "FINAL");
        askUserInput();
    	Button.waitForAnyPress();
	}

	private void closeSensors() {
        motorL.close();
        motorR.close();
    	colorSensorL.close();
    	colorSensorR.close();
        Lcd.clear();
        Lcd.print(5, "in close");
	}


	/*
     * CALLIBRATIE DEEL HIERONDER
     */
    
	private void printCalibration() {
        Lcd.clear();
        Lcd.print(1, "Calibration results:");
        Lcd.print(2, "FL: %d %d %d", colorFinish[0][0], colorFinish[0][1], colorFinish[0][2]);
        Lcd.print(3, "FR: %d %d %d",  colorFinish[1][0], colorFinish[1][1], colorFinish[1][2]);
        Lcd.print(6, "BL: %d %d %d", colorBackground[0][0], colorBackground[0][1], colorBackground[0][2]);
        Lcd.print(7, "BR: %d %d %d", colorBackground[1][0], colorBackground[1][1], colorBackground[1][2]);
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
        calibrateBackground();
	}
	
	private void calibrateBackground() {
        askUserInput();
        Lcd.clear();
        Lcd.print(4, "Place bot on background");
        Lcd.print(5, "Press ENTER to calibrate");
        Button.waitForAnyPress();
        Lcd.print(6, "Calibrating..");
        colorBackground = new int[][]{getAverageRGB(colorSensorL), getAverageRGB(colorSensorR)};
        Lcd.print(7, "SUCCES!");
        Delay.msDelay(250);
	}
	
	private void calibrateFinish() {
        askUserInput();
        Lcd.clear();
        Lcd.print(4, "Place bot on finishline");
        Lcd.print(5, "Press ENTER to calibrate");
        Button.waitForAnyPress();
        Lcd.print(6, "Calibrating..");
        colorFinish = new int[][] {getAverageRGB(colorSensorL), getAverageRGB(colorSensorR)};
        Lcd.print(7, "SUCCES!");
        Delay.msDelay(250);
	}

	
	private int[] getAverageRGB(ColorSensor colorSensor) {
		Color rgb = colorSensor.getColor();		
		int[] sum = new int[3]; // 3 for R G B
		
		//loop calibratie cycles om som van R G B te maken
		for(int i = 0; i < CALIBRATION_CYCLES; i++) {
			sum[0] += rgb.getRed();
			sum[1] += rgb.getGreen();
			sum[2] += rgb.getBlue();
		}
		
		//loop R G B voor gemiddelde te berekenen
		//neem tiende waarden voor minder gevoeligheid
		for(int i = 0; i < 3; i++) {
			sum[i] /= CALIBRATION_CYCLES;
			sum[i] = Math.round((float) sum[i] / 10);
		}
		
		return sum;
	}

	private void askUserInput() {
		Button.LEDPattern(4);    // flash green led and 
        Sound.beepSequenceUp();  // make sound when ready.
	}

	private void prepareSensor(ColorSensor colorSensor) {
        colorSensor.setRGBMode();
        colorSensor.setFloodLight(Color.WHITE);
        colorSensor.setFloodLight(true);
	}
    
}
