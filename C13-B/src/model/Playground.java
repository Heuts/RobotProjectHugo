package model;

import java.util.Date;

import customrobot.library.Lcd;
import lejos.hardware.Button;
import lejos.utility.Delay;

public class Playground {
	public static void main(String[] args) {
		int red = 76, green = 86, blue = 96;
		int[] values = detectPosition(red, green, blue);
		System.out.printf("%d %d %d", values[0], values[1], values[2]);
		loop();
	}
	
	private static int[] detectPosition(int red, int green, int blue) {
		return divideRgbValues(new int[] {red, green, blue},10);
	}
	
	private static int[] divideRgbValues(int[] RgbValues, int divideBy) {
//		for(int RGB: RgbValues) {
//			RGB = Math.round((float) RGB / divideBy);
//		}
		for(int i = 0; i < RgbValues.length; i++) {
			RgbValues[i] = Math.round((float) RgbValues[i] / divideBy);
		}
		return RgbValues;
	}
	
	
	private static void timer() {
		
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0L;

		
		while (true) {
		    elapsedTime = (new Date()).getTime() - startTime;
			System.out.println(elapsedTime/1000);
			Delay.msDelay(1000);
		}
	}

	private static void loop() {
		
		boolean stopMe = true;
		boolean running = false;
		
		while (true) {
			
			
			long startTime = 0L;
			long elapsedTime = 0L;
			
	    	if(true) {
	    		if(running) {
	    			System.out.println("lala");
//	    			stopMe = false;
	    			break;
	    		} else {
	    			running = true;
	    			System.out.println("started");
	    			Delay.msDelay(2000);
	    		}
	    	}
		}
		
		System.out.println("the end");
	}

}
