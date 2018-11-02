package model.playtag;

import utility.Lcd;
import utility.TouchSensor;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;

/**
 * @author Hugo Heuts
 *
 *         MITW SE 13
 */

public class PlayTag {
	/*
	 * voor deze class worden 5 poorten gebruikt: 2 motoren voor de aandrijving van
	 * de rupsbanden, 2 touchsensors voor het spelen van tikkertje en 1 infrarood
	 * voor het detecteren van de beacon
	 */
	EV3LargeRegulatedMotor motorRight;
	EV3LargeRegulatedMotor motorLeft;
	TouchSensor Bumper;
	TouchSensor Back;
	EV3IRSensor Infrared;

	SensorMode infraredSensor;
	float[] infraredSensorSample;

	/*
	 * ik heb verschillende snelheden ingesteld voor de verschillende methodes,
	 * zodat het voor ons makkelijker was om kirov te "taggen" (omdat hij niet te
	 * snel weg rijdt in de avoidBeacon methode) en dat het makkelijker voor kirov
	 * zou zijn om ons te tikken (omdat hij sneller is in deze chaseBeacon methode)
	 */
	final int CHASE_SPEED = 1000;
	final int AVOID_SPEED = 500;
	final int DETECT_SPEED = 750;
	boolean tagged = false;

	/*
	 * dit is de constructor, hier worden de poorten meegegeven en gaat het
	 * programma lopen dmv het aanroepen van de methodes
	 */
	public PlayTag(Port leftMotorPort, Port rightMotorPort, Port touchSensorBumperPort, Port touchSensorBackPort,
			Port infraredSensorFrontPort) {

		motorRight = new EV3LargeRegulatedMotor(rightMotorPort);
		motorLeft = new EV3LargeRegulatedMotor(leftMotorPort);
		Bumper = new TouchSensor(touchSensorBumperPort);
		Back = new TouchSensor(touchSensorBackPort);
		Infrared = new EV3IRSensor(infraredSensorFrontPort);

		infraredSensor = Infrared.getSeekMode();
		infraredSensorSample = new float[infraredSensor.sampleSize()];

		/*
		 * zodra de beacon is gevonden gaat het programma in de while loop waarin hij
		 * eerst probeert in de avoidBeacon methode te gaan, en vervolgens in de
		 * chaseBeacon te gaan, nadat Kirov "tagged" is. Vervolgens als Kirov zelf weer
		 * getikt wordt, wordt de loop gebroken en sluit het programma
		 */
		View.userInterface();
		detectBeacon();
		while (Button.ESCAPE.isUp()) {
			if (detectBeacon() && !tagged) {
				avoidBeacon();
			} else if (detectBeacon() && tagged) {
				chaseBeacon();
				break;
			}
		}
		shutdownSensors();
		View.shutdown();
	}

	/*
	 * Deze methode is de methode waarin wordt "gezocht" naar de beacon. Hij doet
	 * dit door rond te draaien op één plek totdat hij een signaal opvangt met de
	 * infrarood sensor. Deze sensor staat uit zichzelf al op 0, dus ik heb hem zo
	 * geprogrammeerd dat hij alleen reageert op het signaal als de sensor waarden
	 * van 0> of <0 detecteert. Zodra het beacon is gevonden wordt de boolean
	 * beaconFound op true gezet en wordt de loop gebroken
	 */
	public boolean detectBeacon() {
		Lcd.clear();
		Lcd.print(4, "Now in detectBeacon");

		motorRight.setSpeed(DETECT_SPEED);
		motorLeft.setSpeed(DETECT_SPEED);

		boolean beaconFound = false;

		while (Button.ESCAPE.isUp()) {
			infraredSensor.fetchSample(infraredSensorSample, 0);
			int direction = (int) infraredSensorSample[0];
			Lcd.clear(6);
			Lcd.print(6, "Direction " + direction);

			motorRight.backward();
			motorLeft.forward();
			if (direction > -25 && direction < 25 && direction != 0) {
				beaconFound = true;
				Sound.beepSequence();
				break;
			}
		}
		return beaconFound;
	}

	/*
	 * in deze methode wordt het beacon ontweken met de AVOID_SPEED snelheid(traag).
	 * Ik heb ervoor gezorgd dat hij ook af en toe vooruit rijdt (als de
	 * infraroodsensor waarden tussen de -10 en 10 zitten), zodra de touchsensor
	 * achterop kirov wordt aangeraakt gaat de boolean tagged op true en breekt de
	 * loop
	 */
	public void avoidBeacon() {
		Lcd.clear();
		Lcd.print(4, "Now in avoidBeacon");

		motorRight.setSpeed(AVOID_SPEED);
		motorLeft.setSpeed(AVOID_SPEED);

		while (Button.ESCAPE.isUp()) {
			infraredSensor.fetchSample(infraredSensorSample, 0);
			int direction = (int) infraredSensorSample[0];

			Lcd.clear(6);
			Lcd.print(6, "Direction " + direction);

			if (direction > 10) {
				motorRight.forward();
				motorLeft.stop(true);
			} else if (direction < -10) {
				motorRight.stop(true);
				motorLeft.forward();
			} else if (direction >= -10 && direction <= 10) {
				motorRight.forward();
				motorLeft.forward();
			}
			if (Back.isTouched()) {
				Sound.buzz();
				tagged = true;
				return;
			}
		}
	}

	/*
	 * door het breken van de loop in de chase methode gaat de boolean tagged op
	 * true en geraakt het programma in de chaseBeacon methode hieronderin. Deze
	 * methode is voornamelijk gelijk aan de bovenstaande alleen is het gedrag van
	 * de motors gespiegeld ook is de range van infrarood sensor waarin hij
	 * rechtdoor rijdt kleiner, zodat hij meer preciezer is om de andere beacon op
	 * het einde te tikken
	 */
	public void chaseBeacon() {
		Lcd.clear();
		Lcd.print(4, "Now in chaseBeacon");

		motorRight.setSpeed(CHASE_SPEED);
		motorLeft.setSpeed(CHASE_SPEED);

		while (Button.ESCAPE.isUp()) {
			infraredSensor.fetchSample(infraredSensorSample, 0);
			int direction = (int) infraredSensorSample[0];

			Lcd.clear(6);
			Lcd.print(6, "Direction " + direction);

			if (direction < -7) {
				motorRight.forward();
				motorLeft.stop(true);
			} else if (direction > 7) {
				motorRight.stop(true);
				motorLeft.forward();
			} else if (direction >= -7 && direction <= 7) {
				motorRight.forward();
				motorLeft.forward();
			}
			if (Bumper.isTouched()) {
				Sound.beepSequenceUp();
				return;
			}
		}
	}

	public void shutdownSensors() {
		Delay.msDelay(200);
		motorRight.stop();
		motorLeft.stop();
		Delay.msDelay(200);
		motorRight.close();
		motorLeft.close();
		Delay.msDelay(200);
		Infrared.close();
		Bumper.close();
		Back.close();
		Delay.msDelay(200);
	}
}
