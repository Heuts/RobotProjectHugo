package main;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Delay;

public class RobotLauncher {

	Brick brick;
	
	public RobotLauncher() {
		super();
		brick = LocalEV3.get();
		welcomeMessage();
	}
	
	//TODO: Gebruiker begroeten --> is waitforkey nodig?
	private void welcomeMessage() {
		TextLCD display = brick.getTextLCD();
		display.drawString("Welkom", 0, 3);
		display.drawString("Team!", 0, 4);
		Delay.msDelay(3000); //aantal seconden dat bericht zichtbaar is
	}
	
	//TODO: methode(s) die verschillende applicaties oplijst en user vraagt welk programma 
	//gerunned moet worden en roept methode aan
	
	//TODO: methode om app1 te starten
	//TODO: methode om app2 te starten
	
	
	/*
	 * Deze methode is nodig bij het wachten tot user app heeft gekozen
	 */
	public void waitForKey(Key key) {
		while(key.isUp()) {
			Delay.msDelay(100);
		}
		while(key.isDown()) {
			Delay.msDelay(100);
		}
	}

}
