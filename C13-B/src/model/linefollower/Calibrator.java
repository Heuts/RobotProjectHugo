package model.linefollower;

import lejos.robotics.Color;
import lejos.utility.Delay;
import utility.ColorSensor;

public class Calibrator {

	/*
	 * Amount of calibration cycles used when calibrating
	 * Default: one sample/scan, less is not possible
	 */
	private final static int MIN_CYCLES = 1;
	private final static int DEFAULT_CYCLES = 1;
	private static int calibrationCycles;
	
	/*
	 * Absolute margin used to make color of finishline less sensitive
	 * Default: no margin is applied
	 */
	private final static int DEFAULT_FINISH_ABS = 0;
	private static double calibrationFinishMarginAbs;
	
	/*
	 * Relative margin used to make color of finishline less sensitive
	 * Default: no margin is applied
	 */
	private final static int DEFAULT_FINISH_REL = 0;
	private static double calibrationFinishMarginRel;
	
	/*
	 * Delay between calibration cycles
	 */
	private final static int MIN_DELAY = 1;
	private final static int DEFAULT_DELAY = 250;
	private static int calibrationDelay;
	
	/*
	 * Calibrated colors are managed in this class
	 */
	ColorManager colorManager = new ColorManager();
	
	
	/*
	 * All args constructor
	 */
	public Calibrator(int calibrationCycles, int calibrationDelay, double calibrationFinishMarginAbs, 
			double calibrationFinishMarginRel) {
		
		/*
		 * Can't be negative, min one scan is needed
		 */
		this.calibrationCycles = higherOrEqualMin(calibrationCycles, MIN_CYCLES, DEFAULT_CYCLES);
		this.calibrationDelay = higherOrEqualMin(calibrationDelay, MIN_DELAY, DEFAULT_DELAY);
		
		/*
		 * No restrictions
		 */
		this.calibrationFinishMarginAbs = calibrationFinishMarginAbs;
		this.calibrationFinishMarginRel = calibrationFinishMarginRel;
		
	}
	
	public Calibrator() {
		this(DEFAULT_CYCLES, DEFAULT_DELAY, DEFAULT_FINISH_ABS, DEFAULT_FINISH_REL);
	}
	
	/*
	 * Method used to check if a given value is higher or equels
	 * a provided minimum value
	 * if value is lower, default value will be returned
	 * @Param: int value
	 * 		 		value to be checked
	 * 		   int limit
	 * 				min value that value will be checked against
	 * 		   int defaultValue
	 * 				default value in case value is lower than minimum
	 * @Return int
	 * 		   value or default value will be returned
	 */
	private int higherOrEqualMin(int value, int limit, int defaultValue) {
		if(value >= limit) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
    /*
     * A scan is made of the surface below the scanners
     * @Param: ColorSensor[]
     * 				array containing the colorsensors
     * 				which require calibration
     * @Return: int[][]
     * 				first array with size == amount of sensors
     * 				second array with size == 3
     * 					values for: Red Green Blue
     */
	public void calibrateSurface(ColorSensor colorSensor, String surface) {
//		int numberOfSensors = colorSensors.length;
//		int[][] surfaceScan = new int[numberOfSensors][3];
		
		/*
		 * For every sensor, we will run the getAverageRGB method which returns 
		 * array of average RGB values
		 */
//		for(int i = 0; i < numberOfSensors; i++) {
//			surfaceScan[i] = calcAverageRGB(colorSensors[i]);
//		}
		
//		return surfaceScan;
		
		
		colorManager.setMap(surface, 
							calcAverageRGB(colorSensor), 
							colorSensor.getName());
	}
	
	/*
	 * Method takes average of multiple calibration cycle scans
	 * @param: ColorSensor
	 * 				values are processed per colorsensor
	 * @return: int[]
	 * 				array has a length of 3 (RGB)
	 * 				array of average RGB values is returned
	 */
	private int[] calcAverageRGB(ColorSensor colorSensor) {

		int[] sum = new int[3]; // 3 for R G B
		
		sum = calcRgbArraySumUsingCalibrationCycles(sum, colorSensor);
		sum = calcRgbArrayAverageUsingCalibrationCycles(sum);
		
		return sum;
	}

	/*
	 * sum every color in array for number of calibration cycles
	 * @Param: int[] array
	 * 				array of length 3 containing RGB values
	 * 		   Color rgb
	 * 				color object to extract r g b values
	 * @Return: int[3]
	 * 				array with RGB, per color sum of calibration cycles
	 */
	private int[] calcRgbArraySumUsingCalibrationCycles(int[] array, ColorSensor colorSensor) {
		Color rgb;
		
		for(int i = 0; i < calibrationCycles; i++) {
			rgb = colorSensor.getColor();
			
			array[0] += rgb.getRed();
			array[1] += rgb.getGreen();
			array[2] += rgb.getBlue();
			
			Delay.msDelay(calibrationDelay);
		}
		return array;
	}
	
	/*
	 * divide every color in array by number of calibration cycles
	 * @Param: int[]
	 * 				array with summed RGB values
	 * @Return: int[]
	 * 				array with average RGB values per calibration cycle
	 */
	private int[] calcRgbArrayAverageUsingCalibrationCycles(int[] array) {
		
		for(int i = 0; i < array.length; i++) {
			array[i] /= calibrationCycles;
		}
		
		return array;
	}

	public void calibrateFinishMinMax(ColorSensor colorSensor) {
		
		int[] finish = colorManager	.getSensor(colorSensor.getName())
									.getMap("finish")
									.getRgb();
		
//		int[] finishMax = new int[3];
//		int[] finishMin = new int[3];
//		
//		for(int i = 0; i < finish.length; i++) {
//			finishMax[i] = (int) (finish[0] *(1+calibrationFinishMarginRel) + calibrationFinishMarginAbs);
//			finishMin[i] = (int) (finish[0] *(1-calibrationFinishMarginRel) - calibrationFinishMarginAbs);
//		}
		
		//voorlopig hardcoded oranje finish en calibratie hierboven commented out
		int[] finishMax = {80, 50, 99};
		int[] finishMin = {60, 0, 0};
		
		colorManager.setMap("finishMax", 
							finishMax, 
							colorSensor.getName());
		colorManager.setMap("finishMin", 
							finishMin, 
							colorSensor.getName());
		
	}
	
}
