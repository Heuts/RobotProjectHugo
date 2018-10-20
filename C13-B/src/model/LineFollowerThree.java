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
    private int[][] colorFinish;
    /*
     * colorBackground is an array which consists of two arrays
     * 		first array with size == amount of sensors
     * 		second array with size == 3
     * 			values for: Red Green Blue
     */
    private int[][] colorBackground;
    /*
     * Used in determining calibration precision, more == longer == more precise
     */
    private final static int CALIBRATION_CYCLES = 4;
    
    
    /*
     * tijdelijke functie die weggaat bij integratie in project, zal de launch oproepen die de constructor zal worden
     */
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
    	/*
    	 * Toewijzen van de motoren en sensors
    	 */
    	setMotorL(leftMotor);
    	setMotorR(rightMotor);
    	setColorSensorL(leftColorSensor);
    	setColorSensorR(rightColorSensor);
    	
    	/*
    	 * Titel van programma
    	 */
    	Lcd.print(1, "Line Follower V.RGB");
    	
    	/*
    	 * Klaarzetten van sensors
    	 */
    	Lcd.print(2, "Opstarten sensors..");
    	prepareRGBSensor(colorSensorL);
    	prepareRGBSensor(colorSensorR);
    	
    	Lcd.clear(2);
    	Lcd.print(2, "Sensors ready");
    	
    	/*
    	 * Start calibratieprogramma
    	 */
    	startCalibration();
//    	printCalibration();
    	askUserInput();
    	Button.waitForAnyPress();
    	
    	/*
    	 * Start follow line methode
    	 */
    	followLine();
    	
    	/*
    	 * Einde programma
    	 */
    	askUserInput();
    	Button.waitForAnyPress();
        endOfProgram();
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
	
	/*
	 * RGB mode toewijzen aan sensor en bijpassende floodlights aanzetten
	 */
	private void prepareRGBSensor(ColorSensor colorSensor) {
        colorSensor.setRGBMode();
        colorSensor.setFloodLight(Color.WHITE);
        colorSensor.setFloodLight(true);
	}

	/*
	 * Hulpfunctie die groene led aanzet en piepend geluid maakt
	 * Indicatie dat userinput wordt verwacht
	 */
	private void askUserInput() {
		Button.LEDPattern(4);    // flash green led and 
        Sound.beepSequenceUp();  // make sound when ready.
	}
    
	/*
	 * Methode verantwoordelijk voor de calibratieprocedure van de bot
	 */
	private void startCalibration() {
		/*
		 * Geef begin aan van calibratie
		 */
    	askUserInput();
        Lcd.print(3, "Press ENTER to start calibrate");
        Button.waitForAnyPress();
        
        /*
         * calibratie van de verschillende aspecten van parcours
         */
        prepareUserForCalibration("Finish");
        colorFinish = calibrateSurface();
        wrapUpCalibration();
        
        prepareUserForCalibration("Background");
        colorBackground = calibrateSurface();
        wrapUpCalibration();
	}
	
	private void prepareUserForCalibration(String toCalibrate) {
        askUserInput();
        Lcd.clear();
        Lcd.print(4, "%s:", toCalibrate);
        Lcd.print(5, "Press ENTER to calibrate");
        Button.waitForAnyPress();
        Lcd.print(6, "Calibrating..");
	}
	
	private void wrapUpCalibration() {
        Lcd.print(7, "SUCCES!");
        Delay.msDelay(250);
	}
	
	private int[][] calibrateSurface() {
		return new int[][]{divideRgbValues(getAverageRGB(colorSensorL), 10),
						   divideRgbValues(getAverageRGB(colorSensorR), 10)};
	}
	

	/*
	 * zal rgb waarde over meerdere cycles nemen en gemiddelde return
	 * @param: ColorSensor => waardes worden per individuele sensor verwerkt
	 * @return: int[]
	 * 		deze array heeft lengte 3 (RGB)
	 * 		RGB waarden zijn gemiddelde R, gemiddelde G en gemiddelde B over aantal calibratie cycles
	 */
	private int[] getAverageRGB(ColorSensor colorSensor) {
		Color rgb = colorSensor.getColor();		
		int[] sum = new int[3]; // 3 for R G B
		
		//loop calibratie cycles om som van R G B te maken
		for(int i = 0; i < CALIBRATION_CYCLES; i++) {
			//TODO: functie get RGB values gebruiken
			sum[0] += rgb.getRed();
			sum[1] += rgb.getGreen();
			sum[2] += rgb.getBlue();
		}
		
		//loop R G B voor gemiddelde te berekenen
		for(int i = 0; i < 3; i++) {
			sum[i] /= CALIBRATION_CYCLES;
		}
		
		return sum;
	}
	
	/*
	 * gebruiken we om gevoeligheid van RGB waarden lager te maken en bv met tientallen te werken
	 * 
	 */
	private int[] divideRgbValues(int[] RgbValues, int divideBy) {
		for(int RGB: RgbValues) {
			RGB = Math.round((float) RGB / divideBy);
		}
		return RgbValues;
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
		
		motorL.forward();
		motorR.forward();
		motorL.setPower(power);
		motorR.setPower(power);
    	
		while (Button.ESCAPE.isUp()) {
	    	float positionL = detectPosition(colorSensorL);
	    	float positionR = detectPosition(colorSensorR);
	    	if(isFinish(positionL, positionR))
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
	    	
	    	positionR = detectPosition(colorSensorR);
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


	private boolean isFinish(float positionL, float positionR) {
		// TODO Auto-generated method stub
		return false;
	}

	private int[] detectPosition(ColorSensor colorSensor) {
		Color rgb = colorSensor.getColor();	 //TODO: WAT IS DE OUTPUT VAN DEZE FUNCTIE ??? kunnen we hierop rijden??
											 // dan kunnen we hierop alles doen en enkel rgb voor finish
		return divideRgbValues(new int[] {rgb.getRed(), rgb.getGreen(), rgb.getBlue()},10);
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


}
