package main;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import utility.Lcd;

public class RobotLauncher {

	private final static String APP1 = "Line Follower";
	private final static String APP2 = "Find Beacon";
	private final static String APP3 = "Play Tag";
	
	private static boolean running;

	/*
	 * Assigning ports
	 */
	private final static Port	LEFT_COLOR = SensorPort.S1,
								RIGHT_COLOR = SensorPort.S2,
								PRESSURE_FRONT = SensorPort.S3,
								PRESSURE_BACK = SensorPort.S1,
								INFRARED = SensorPort.S4,
								LEFT_MOTOR = MotorPort.A,
								MIDDLE_MOTOR = MotorPort.C,
								RIGHT_MOTOR = MotorPort.D;
								
	public RobotLauncher() {
		running = true;
		printWelcome();
		
		while(running) {
			printMenu();
		}
	}

	private void printWelcome() {
		Lcd.print(0, "Welkom team Bravo!");
	}

	/*
	 * verschillende applicaties oplijsten en user vragen welk
	 * programma gerunned moet worden en roept methode aan
	 */
	private void printMenu() {
		
		Lcd.print(2, "Welke app?");
		Lcd.print(3, APP1+" (UP)");
		Lcd.print(4, APP2+" (DOWN)");
		Lcd.print(5, APP3+" (RIGHT)");
		Lcd.print(7, "Exit (ESCAPE)");
		
		boolean choiceMade = false;
		
		while(!choiceMade) {
			if (Button.UP.isDown()) {
				choiceMade = true;
				Lcd.clear();
				Lcd.print(1, APP1);
				startApp1();
				Delay.msDelay(500);
			}
			if (Button.DOWN.isDown()) {
				choiceMade = true;
				Lcd.clear();
				Lcd.print(1, APP2);
				startApp2();
				Delay.msDelay(500);
			}
			if (Button.RIGHT.isDown()) {
				choiceMade = true;
				Lcd.clear();
				Lcd.print(1, APP3);
				startApp3();
				Delay.msDelay(500);
			}
			if (Button.ESCAPE.isDown()) {
				choiceMade = true;
				Lcd.clear();
				Lcd.print(1, "EXIT");
				running = false;
				Delay.msDelay(500);
			}
		}
	}

	private void startApp1() {
		new model.linefollower.Launcher(LEFT_MOTOR, RIGHT_MOTOR, LEFT_COLOR, RIGHT_COLOR);
	}

	private void startApp2() {
		new model.beaconfinder.Launcher(LEFT_MOTOR, RIGHT_MOTOR, MIDDLE_MOTOR, INFRARED, PRESSURE_FRONT);
	}
	
	private void startApp3() {
		new model.playtag.PlayTag(LEFT_MOTOR, RIGHT_MOTOR, PRESSURE_FRONT, PRESSURE_BACK, INFRARED);
	}

	/*
	 * Deze methode is nodig bij het wachten tot user app heeft gekozen
	 */
	public void waitForKey(Key key) {
		while (key.isUp()) {
			Delay.msDelay(100);
		}
		while (key.isDown()) {
			Delay.msDelay(100);
		}
	}

}
