package model.linefollower;

public class Drive {
	public static void main(String[] args) {
		System.out.println("hello");
		ColorManager grid = new ColorManager();

		String sensorL = "SensorL";
		String sensorR = "SensorR";
		
		int[] rgbL = {5, 6, 7};
		int[] rgbR = {1, 2, 3};
		

		grid.setMap("background", rgbL, sensorL);
		grid.setMap("background", rgbR, sensorR);
		

		grid.getSensor(sensorR).getMap("background").getBlue();
		
		System.out.println(grid.getSensor(sensorR).getMap("background").getBlue());
		System.out.println(grid.getSensor(sensorL).getMap("background").getBlue());
		
		
		
	}
}
