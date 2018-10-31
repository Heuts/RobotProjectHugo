package model;

import lejos.hardware.port.MotorPort;
import lejos.hardware.motor.EV3MediumRegulatedMotor;


public class RobotCannon {
	
	EV3MediumRegulatedMotor Cannon;
	
	public RobotCannon() {
        Cannon = new EV3MediumRegulatedMotor(MotorPort.C);
	}
    
    public void CannonFire() {
        fire();
		resetMotor();
    }

    private void resetMotor(){
        Cannon.rotate(890);
        Cannon.resetTachoCount();
     }       

    private void fire(){
        Cannon.setSpeed(1000);
        Cannon.setAcceleration(6000);
    	Cannon.rotate(190);

    }      

	

}