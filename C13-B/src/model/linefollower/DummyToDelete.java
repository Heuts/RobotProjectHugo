package model.linefollower;

public class DummyToDelete {
	public static void main(String[] args) {
		/*		System.out.println("hello");
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
				
				*/
				
				Test a = new Test(5);
				System.out.println(a);
				
				Test b = new Test(a);
				a.x[0] = 10;
				
				System.out.println(b);
				
			}
		}

		class Test {
			public int[] x;
			
			public Test(int x) {
				this.x = new int[5];
				this.x[0] = x;
			}
			
			public Test(Test t) {
				x = t.x;
			}
			
			public String toString() {
				return String.format("%d", x[0]);
			}
}
