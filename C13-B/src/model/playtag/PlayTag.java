package model.playtag;

import utility.Lcd;
import utility.TouchSensor;
import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
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
	 * voor deze class worden 5 poorten gebruikt: 2 motoren voor de aandrijving
	 * van de rupsbanden, 2 touchsensors voor het spelen van tikkertje en 1
	 * infrarood voor het detecteren van de beacon
	 */
	EV3LargeRegulatedMotor motorRight;
	EV3LargeRegulatedMotor motorLeft;
	TouchSensor Bumper;
	TouchSensor Back;
	EV3IRSensor Infrared;

	SensorMode infraredSensor;
	float[] infraredSensorSample;

	final int COMPENSATED_MAX_SPEED = 1000;
	final int MAX_SPEED = 1000;
	boolean tagged = false;

	Brick brick;

	public static void main(String[] args) {
		// dit gaat weg, komt in main programma
		Port INFRARED_FRONT = SensorPort.S4, TOUCH_FRONT = SensorPort.S3, TOUCH_BACK = SensorPort.S1,
				LEFT_MOTOR = MotorPort.A, RIGHT_MOTOR = MotorPort.D;

		new PlayTag(LEFT_MOTOR, RIGHT_MOTOR, TOUCH_FRONT, TOUCH_BACK, INFRARED_FRONT);
	}

	/*
	 * dit is de constructor, hier worden de poorten geinitieerd en gaat het
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

		View.userInterface();
		detectBeacon();
		while (Button.ESCAPE.isUp()) {
			if (detectBeacon() && tagged == false) {
				avoidBeacon();
			} else if (detectBeacon() && tagged == true) {
				chaseBeacon();
				break;
			}
		}
		shutdownSensors();
		View.shutdown();
	}

	public boolean detectBeacon() {
		Lcd.clear();
		Lcd.print(4, "Now in detectBeacon");

		boolean beaconFound = false;

		while (Button.ESCAPE.isUp()) {
			infraredSensor.fetchSample(infraredSensorSample, 0);
			int direction = (int) infraredSensorSample[0];
			Lcd.clear(6);
			Lcd.print(6, "Direction " + direction);

			motorRight.setSpeed(MAX_SPEED);
			motorLeft.setSpeed(COMPENSATED_MAX_SPEED);
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

	public void avoidBeacon() {
		Lcd.clear();
		Lcd.print(4, "Now in avoidBeacon");
		
		while (Button.ESCAPE.isUp()) {
			infraredSensor.fetchSample(infraredSensorSample, 0);
			int direction = (int) infraredSensorSample[0];

			Lcd.clear(6);
			Lcd.print(6, "Direction " + direction);

			if (direction > 0) {
				motorRight.forward();
				motorLeft.stop(true);
			} else if (direction < 0) {
				motorRight.stop(true);
				motorLeft.forward();
			}
			if (Back.isTouched()) {
				Sound.buzz();
				tagged = true;
				return;
			}
		}
	}

	public void chaseBeacon() {
		Lcd.clear();
		Lcd.print(4, "Now in chaseBeacon");
		
		while (Button.ESCAPE.isUp()) {
			infraredSensor.fetchSample(infraredSensorSample, 0);
			int direction = (int) infraredSensorSample[0];

			Lcd.clear(6);
			Lcd.print(6, "Direction " + direction);

			if (direction < 0) {
				motorRight.forward();
				motorLeft.stop(true);
			} else if (direction > 0) {
				motorRight.stop(true);
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
