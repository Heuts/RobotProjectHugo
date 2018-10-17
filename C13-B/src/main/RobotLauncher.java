package main;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Delay;
import model.*;

public class RobotLauncher {

	final static String APP1 = "Line follower";
	final static String APP2 = "Dansen";
	private Brick brick;
	private TextLCD display;

	public RobotLauncher() {
		super();
		brick = LocalEV3.get();
		display = brick.getTextLCD();
		printWelcome();
		printMenu();
	}

	private void printWelcome() {
		display.drawString("Welkom team Bravo!", 0, 0);
	}

	/*
	 * verschillende applicaties oplijsten en user vragen welk
	 * programma gerunned moet worden en roept methode aan
	 */
	private void printMenu() {
		TextLCD display = brick.getTextLCD();
		
		display.drawString("Welke app?", 0, 2);
		display.drawString(APP1+" (UP)", 0, 3);
		display.drawString(APP2+" (DOWN)", 0, 4);
		display.drawString("App 3 (RIGHT)", 0, 5);
		display.drawString("Exit (ESCAPE)", 0, 7);
		
		Button.waitForAnyEvent();
		if (Button.UP.isDown()) {
			
			display.clear();
			display.refresh();
			display.drawString(APP1, 0, 1);
			startApp1();
			
			Delay.msDelay(500);
			
		}
		if (Button.DOWN.isDown()) {
			display.clear();
			display.drawString(APP2, 0, 1);
			Delay.msDelay(500);
		}
		if (Button.RIGHT.isDown()) {
			display.clear();
			display.drawString("App 3", 0, 1);
			Delay.msDelay(500);
		}
		if (Button.ESCAPE.isDown()) {
			display.clear();
			display.drawString("EXIT", 0, 1);
			Delay.msDelay(500);
		}
	}

	// TODO: methode om app1 te starten
	private void startApp1() {
		new LineFollower();
	}

	// TODO: methode om app2 te starten
	private void startApp2() {
		// new object keuzeproject
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
