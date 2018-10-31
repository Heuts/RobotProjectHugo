package model.beaconfinder;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;
import utility.TouchSensor;

public class Launcher {
	
	// Create instances of the motors and cannon
	private EV3LargeRegulatedMotor motorL, motorR;
	private EV3MediumRegulatedMotor motorCn;
	
	// Create instance of the infrared and touch sensor
	private EV3IRSensor IRSensor;
	private TouchSensor touch;
	
	public Launcher(Port motorL, Port motorR, Port motorCn, Port IRSensor, Port touchSensor) {
    	/*
    	 * Allocation of hardware to variables
    	 */
    	setPorts(motorL, motorR, motorCn, IRSensor, touchSensor);
    	
    	// Run an instance of locating and approaching the beacon
    	RobotMove findBeacon = new RobotMove();
    	findBeacon.run(this);
    	closePorts();
	}
	
	private void setPorts(Port leftMotor, Port rightMotor, Port motorCn, Port IRSensor, Port touchSensor) {
		this.IRSensor = new EV3IRSensor(IRSensor);
		this.touch = new TouchSensor(touchSensor);
    	this.motorR = new EV3LargeRegulatedMotor(rightMotor);
    	this.motorL = new EV3LargeRegulatedMotor(leftMotor);
    	this.motorCn = new EV3MediumRegulatedMotor(motorCn);
    }
	
	// Close the ports of the motors and sensors
	private void closePorts() {
		
		// Close the motor ports
		motorR.close();
		motorL.close();
		
		// Close IR sensor
		IRSensor.close();
		
		// Close touch sensor
		touch.close();
	}
	
    EV3LargeRegulatedMotor getMotor(char motor) {
    	if(motor == 'L')
    		return motorL;
    	else
    		return motorR;
    }
    
    TouchSensor getTouch() {
		return touch;
    }
    
    EV3IRSensor getIR() {
    	return IRSensor;
    }

}
