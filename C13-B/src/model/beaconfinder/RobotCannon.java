package model.beaconfinder;

import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class RobotCannon {
	
	EV3MediumRegulatedMotor cannon;
	
	public RobotCannon(EV3MediumRegulatedMotor cannon) {
        this.cannon = cannon;
	}
    
    public void CannonFire() {
        fire();
		resetMotor();
    }
    
    private void resetMotor(){
        cannon.rotate(890);
        cannon.resetTachoCount();
    }       


    private void fire(){
        cannon.setSpeed(1000);
        cannon.setAcceleration(6000);
    	cannon.rotate(190);
    }      
}