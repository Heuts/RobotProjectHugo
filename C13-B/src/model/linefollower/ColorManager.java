package model.linefollower;

import java.util.HashMap;
import java.util.Map;


public class ColorManager {
	public final static Map<String, int[]> backgroundMap = new HashMap<String, int[]>(),
											finishMap = new HashMap<String, int[]>(),
											finishMaxMap = new HashMap<String, int[]>(),
											finishMinMap = new HashMap<String, int[]>();
	
	private String currentSensor;
	private int[] currentRgb;
	
	public ColorManager getSensor(String sensor) {
		currentSensor = sensor;
		return this;
	}
	
	public ColorManager getMap(String map) {
		if(map.equals("background")) {
			currentRgb = backgroundMap.get(currentSensor);
		}
		if(map.equals("finish")) {
			currentRgb = finishMap.get(currentSensor);
		}
		if(map.equals("finishMax")) {
			currentRgb = finishMaxMap.get(currentSensor);
		}
		if(map.equals("finishMin")) {
			currentRgb = finishMinMap.get(currentSensor);
		}
		return this;
	}

	public void setMap(String map, int[] rgb, String sensor) {
		if(map.equals("background")) {
			backgroundMap.put(sensor, rgb);
		}
		if(map.equals("finish")) {
			finishMap.put(sensor, rgb);
		}
		if(map.equals("finishMax")) {
			finishMaxMap.put(sensor, rgb);
		}
		if(map.equals("finishMin")) {
			finishMinMap.put(sensor, rgb);
		}
	}
	
	public int[] getRgb() {
		return currentRgb;
	}
	
	public int getRed() {
		return currentRgb[0];
	}
	public int getGreen() {
		return currentRgb[1];
	}
	public int getBlue() {
		return currentRgb[2];
	}
	
}
