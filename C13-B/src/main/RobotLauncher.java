package main;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import model.*;
import model.beaconfinder.RobotMove;
import utility.Lcd;

public class RobotLauncher {

	final static String APP1 = "Line Follower";
	final static String APP2 = "Find Beacon";
	final static String APP3 = "Play Tag";

	/*
	 * Assigning ports
	 */
	private final static Port LEFT_COLOR = SensorPort.S1,
							  RIGHT_COLOR = SensorPort.S2,
							  PRESSURE_FRONT = SensorPort.S3,
							  PRESSURE_BACK = SensorPort.S2,
							  INFRARED_FRONT = SensorPort.S4,
							  INFRARED_BACK = SensorPort.S1,
							  LEFT_MOTOR = MotorPort.A,
							  RIGHT_MOTOR = MotorPort.D;
							  

	public RobotLauncher() {
		super();
		printWelcome();
		printMenu();
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
		
		Button.waitForAnyEvent();
		if (Button.UP.isDown()) {
			
			Lcd.clear();
			Lcd.print(1, APP1);
			startApp1();
			
			Delay.msDelay(500);
			
		}
		if (Button.DOWN.isDown()) {
			
			Lcd.clear();
			Lcd.print(1, APP2);
			startApp2();
			Delay.msDelay(500);
		}
		if (Button.RIGHT.isDown()) {
			
			Lcd.clear();
			Lcd.print(1, APP3);
			
			Delay.msDelay(500);
		}
		if (Button.ESCAPE.isDown()) {
			Lcd.clear();
			Lcd.print(1, "EXIT");
			Delay.msDelay(500);
		}
	}

	// TODO: methode om app1 te starten
	private void startApp1() {
		new LineFollower(LEFT_COLOR, RIGHT_COLOR, LEFT_MOTOR, RIGHT_MOTOR);
	}

	// TODO: methode om app2 te starten
	private void startApp2() {
		new RobotMove()
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
