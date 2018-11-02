package model.beaconfinder;

import lejos.hardware.motor.EV3MediumRegulatedMotor;

/* Secundaire motor voor kanon functie heeft 3 rotaties van 360 graden nodig om het volgende projectiel te 'laden' en op 'scherp te zetten'
 */

public class Cannon {
	
	EV3MediumRegulatedMotor cannon;
	
	public Cannon(EV3MediumRegulatedMotor cannon) {
        this.cannon = cannon;
	}
    
    void cannonFire() {
        fire();
		resetMotor();
    }

    /*
     * Om de motor te resetten voor het volgend projectiel, draait de motor de resterende 2,5 rotaties door.
     */
    private void resetMotor(){
        cannon.rotate(890);
        cannon.resetTachoCount();
    }       

    /*
     *  Met iets meer dan een 0,5 rotatie (190) zal het projectiel worden afgevuurd. 
     */
    *
    private void fire(){
        cannon.setSpeed(1000);
        cannon.setAcceleration(6000);
    	cannon.rotate(190);
    }      
}