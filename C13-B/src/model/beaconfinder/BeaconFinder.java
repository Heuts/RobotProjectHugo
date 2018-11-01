package model.beaconfinder;

import java.util.Random;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;
import utility.Lcd;
import utility.TouchSensor;

public class BeaconFinder {
	
	// Declare the motors in use for movement by the programme
	private EV3LargeRegulatedMotor motorL, motorR;

    // Declare the sensors in use by the programme
	private TouchSensor touch;
	private EV3IRSensor sensorIR;
	
	SensorMode seek;
	float[] sample;
	
	// Declare a cannon object to use.
	Cannon cannon; 
	
	/* 
	 * The motors are not equally powerful
	 * Therefore motorA is running at a slower maximum speed.
	 */
    final int COMPENSATED_MAX_SPEED = 686;
    final int MAX_SPEED = 700;
    
    
    // Setup and start of programme
    public BeaconFinder(Launcher launcher) {
    	/*
    	 * Retrieve the sensor and motor slots from the launcher
    	 */
		this.motorL = launcher.getMotor('L');
		this.motorR = launcher.getMotor('R');
		this.sensorIR = launcher.getIR();
		this.touch = launcher.getTouch();
		this.cannon = launcher.getCannon();
		
		/*
		 *  Set the mode of the infrared sensor to seeking
		 */
		setSensorMode();
    	
		Lcd.print(3, "Start programme");
		
		// Make sound when ready.
        Sound.beepSequenceUp();  
        Button.waitForAnyPress();
        
        // Run main method
        basicProgramme();
        
        Sound.beepSequenceUp();  
        Sound.beepSequenceUp();  
        Sound.beepSequenceUp();
        Button.LEDPattern(2);
        Lcd.print(3, "Target destroyed!");
        Button.waitForAnyPress();
    }
    
    private void setSensorMode() {
        /*
         *  Declare that the sensor uses seek mode to find the beacon
         *  Declare that samples of the sensor can be taken and stored as a float
         */
    	seek = sensorIR.getSeekMode();
    	sample = new float[seek.sampleSize()];
		
	}

	/*
	 * Main programme
	 * Loops until the target is found and has been shot
	 */
    public void basicProgramme() {	
    	/* 
    	 * Declare and initialise boolean for argument
    	 * (Target is NOT destroyed)
    	*/
    	boolean targetDestroyed = false;
    	
    	/* 
    	 * If target is NOT destroyed, or the button is NOT pressed
    	 * Continue the loop in perpetuity
    	*/
    	while (!targetDestroyed || !Button.ESCAPE.isUp()) {
    		// Orange heart beat when in search mode
    		Button.LEDPattern(9); 
    		// Move the robot forward for x seconds, the x is determined in milliseconds
    		moveForward(500);
    		
    		// Attempt to detect the beacon, turn 360 degrees
            searchRotate();
            
            // Run followBeacon and its return value checks whether the target is destroyed
            targetDestroyed = followBeacon();
    	}
    	// When coming out of the loop, halt usage of the motors
    	robotStop();
    }
    
    // 
    public boolean followBeacon() {
    	/* 
    	 * Declare and intialise whether the target is destroyed
    	 * The target is initialised as NOT destroyed
    	 */ 
    	boolean isDestroyed = false;
    	
    	/*
    	 * The minimum and maximum sight vision of the sensor.
    	 * is between the values of -25 and 25
    	*/ 
    	int minSightValue = -25;
    	int maxSightValue = 25;
    	
    	// Fetch a sample to determine whether the beacon is in sight
		seek.fetchSample(sample, 0);
		int direction = (int) sample[0];
		int distance = (int) sample[1];
		
		/* 
		 * First determines whether the beacon is in sight
		 * In case it is not: Skip this method (followBeacon) altogether
		 * Else run the method and attempt to approach the beacon
		 * Shoot at it when in sight
		 */
    	if (direction > minSightValue && direction < maxSightValue) {
    		Button.LEDPattern(1); //green light to indicate target was found and closing in on target
    		/* 
    		 * Set the shooting distance required for the robot
    		 * The range is set to be between 1 and 25
    		 */
    		int minShootingDistance = 1;
    		int maxShootingDistance = 25;
    		
    		/*
    		 * This value equals the maximum range of the robot
    		 * Its detection range is between 0 and 100
    		 */
    		int loseDistance = 100;
    	
    		/*
    		 * Set the range wherein the cannon is allowed to be shot
    		 * The range is set to be between -10 and 10, thus less than the
    		 * maximum range capable of the sensor (-25 and 25 respectively)
    		 */
    		int minShootingDirection = -10;
    		int maxShootingDirection = 10;
    		
    		/*
    		 * Set the value of 0 equal to 0 degrees    		
    		 * When direction is 0, go straight ahead 
    		 * If it's a negative number go RIGHT
    		 * If it's a positive number go LEFT
    		 */
    		int straightAhead = 0;
    		
	    	 	
			while (Button.ESCAPE.isUp()) {
				// Fetch a sample to determine whether the beacon is in sight
				seek.fetchSample(sample, 0);
				direction = (int) sample[0];
				distance = (int) sample[1];

				// Clear LCD
				Lcd.clear();
				
				// Print the direction and distance to the screen
				Lcd.print(2, "Direction: " + direction);
				Lcd.print(4, "Distance: " + distance);
				
				/* 
				 * Check whether the beacon is on the right of the sensor
				 * If so, start the left motor to adjust the angle
				*/ 
				if (direction > straightAhead) {
					motorL.forward();
					motorR.stop(true);
					
					// If while steering the bumper is touched activate the bumper method
					if (touch.isTouched()) {
						robotBumper();
					}
				/* 
				 * Check whether the beacon is on the left of the sensor
				 * If so, start the right motor to adjust the angle
				 */
				} else if (direction < straightAhead) {
					motorL.stop(true);
					motorR.forward();
					
					// If while steering the bumper is touched activate the bumper method
					if (touch.isTouched()) {
						robotBumper();
					}
				/*
				 *  If the distance of the beacon is in range of the integer
				 *  move forward with maximum speed for 1 millisecond
				 */
				} else if (distance < Integer.MAX_VALUE) {
					moveForward(1);
				} else if (distance == Integer.MAX_VALUE || distance == loseDistance) {
					break;
				}
					
				/* 
				 * If the distance is between 1 and 25
				 * AND direction is between -10 and 10
				 * Shoot the cannon at the target
				*/
				if (distance >= minShootingDistance && distance <= maxShootingDistance && 
						direction >= minShootingDirection && direction <= maxShootingDirection) {
					// Red pulse to indicate fire mode
					Button.LEDPattern(5); 
					motorL.stop(true);
					motorR.stop(true);
					Sound.beepSequenceUp();
					
					// Determine how often the cannon should fire a missile
					int shootAmount = 3;
					
					// Fire the cannon the allotted amount
					for (int shotsFired = 0; shotsFired < shootAmount; shotsFired++) {
						cannon.CannonFire();
					}
					
					/* 
					 * After the robot has shot at the target
					 * it is considered as destroyed
					 */
					isDestroyed = true;
					
					// Leave the loop
					break;
				}
			}
		}
    	
    	/* 
    	 * If the method is skipped, the return value is always false
    	 * If the method was launched but signal is lost, the return value is false
    	 * Only if the cannon was fired the target is deemed as destroyed and returns true
    	*/
    	return isDestroyed;
    }
    
	/* 
	 * Move robot backwards with maximum speed for a set amount of
	 * time in milliseconds (determined when the method is called)
    */
	public void moveBackward(int time) {
	        motorL.setSpeed(COMPENSATED_MAX_SPEED);
	        motorR.setSpeed(MAX_SPEED);
			motorL.backward();
			motorR.backward();
			Delay.msDelay(time);
	}
	
	/* 
	 * Move robot forwards with maximum speed for a set amount of
	 * time in milliseconds (determined when the method is called)
	 */
	public void moveForward(int time) {
		
		/* 
		 * Declare and initialise that the
		 * bumper starts at NOT being touched
		*/
		boolean isBumped = false;
		
		/* 
		 * Declare the secondary timer that counts to the time
		 * the robot was declared to move forward
		*/
		int secondaryTimer = 0;
		while (secondaryTimer < time)  {
			
			// Set the speed of the motors to maximum
	        motorL.setSpeed(COMPENSATED_MAX_SPEED);
	        motorR.setSpeed(MAX_SPEED);
	        
	        // Ensure that both motors move forwards
			motorL.forward();
			motorR.forward();
			
			/*
			 * If during this time the bumper is touched 
			 * initialise that the bumper is touched and leave the loop
			 */
			if (touch.isTouched()) {
				isBumped = true;
				break;
			}
			
			// The secondary timer goes up by 1 millisecond
			secondaryTimer++;
			
			// This delay allows the touch sensor to be registered in the loop
			Delay.msDelay(1);
		}
		
		/* 
		 * There are two ways to leave the loop:
		 * 1a: If the loop is left through the timer reaching the maximum
		 * 1b: isBumped is FALSE and therefore will skip the order
		 * 
		 * 2a: If the loop is left through the touch sensor being touched
		 * 2b: isBumped is TRUE and therefore will run the robotBumper() method
		 */
		if (isBumped) {
			robotBumper();
		}
	}

	/*
	 *  Rotate the robot on its axis up to 180 degrees left or right (input as 100 or -100)
	 *  Positive (1 to 100) will turn the robot to the left
	 *  Negative (-1 to -100) will turn the robot to the right
	 */
	public void rotate(int degrees) {
			
			// Clear LCD
			Lcd.clear();
			
			// Produce sound for auditory feedback
			Sound.beepSequenceUp();
			
			// Print the location of the programme and its direction
			Lcd.print(4, "Rotate " + degrees);
			
			// Declare and initialise the secondary timer as 0
			int secondaryTimer = 0;
			
			/* 
			 * To determine the degrees, the movement of the rotation
			 * is measured in time units, as such it needs to become an
			 * absolute (positive) number while degrees is the direction of the rotation
			*/
			int time = (int) Math.abs((float) (degrees * 3D));
			
			while (secondaryTimer < time)  {
				
				/*
				 *  If the degrees/direction is a negative number
				 *  The robot makes a LEFT turn
				 */
				if (degrees < 0) {
					
					// Set the speed of the motors to maximum
			        motorL.rotate(COMPENSATED_MAX_SPEED, true);
			        motorR.rotate(MAX_SPEED, true);
			        
			        /* 
			         * To make a left turn, motorA requires backward thrust
			         * and motorD requires forward thrust
			        */ 
			        motorL.backward();
			        motorR.forward();
			        
				/*
				 * If the degrees/direction is a positive number
				 * The robot makes a RIGHT turn
				 */
				} else if (degrees > 0)  {
					
					// Set the speed of the motors to maximum
			        motorL.rotate(COMPENSATED_MAX_SPEED, true);
			        motorR.rotate(MAX_SPEED, true);
			        
			        /* 
			         * To make a right turn, motorA requires forward thrust
			         * and motorD requires backwards thrust
			         */
			        motorL.forward();
			        motorR.backward();
				}
				// The secondary timer goes up by 1 millisecond
				secondaryTimer++;
				
				// This delay allows the touch sensor to be registered in the loop
				Delay.msDelay(1);				
			}
		}
			
		public void searchRotate() {
			Sound.beepSequenceUp();

			// Declare the timer and initialise its start to 0
			int secondaryTimer = 0;
			
			// Time of 250 roughly translates to 360 degree turn
			int time = 250;
			
			// Set the speed for the rotation
			int speedRotate = 500;
			
			
			// Enter the loop where its maximum turn is 360 degrees (time of 250)
			while (secondaryTimer < time)  {

				if (Button.ESCAPE.isUp()) {
					
					// Take a sample of measurements
					seek.fetchSample(sample, 0);
					int direction = (int) sample[0];
					int distance = (int) sample[1];
					
					/* 
					 * Declare and intialise the minimum and and maximum direction
					 * Additionally declare the 0 value that is not allowed
					*/
					int minDirection = -25;
					int maxDirection = 25;
					int forbiddenDirection = 0;
					
					// Clear LCD
					Lcd.clear();
					
					// Print the direction and distance measured by the IR sensor
					Lcd.print(2, "Direction: " + direction);
					Lcd.print(4, "Distance: " + distance);
					
					// Set the speed of the motors to maximum
			        motorL.setSpeed(speedRotate);
			        motorR.setSpeed(speedRotate);
			        
			        /* 
			         * To make a left turn, motorA requires backward thrust
			         * and motorD requires forward thrust
			         */
			        motorL.backward();
			        motorR.forward();
			        
			        /* 
			         * Determine that the direction is between the minimum and maximum parameters
			         * Also check whether the direction is forbidden
			         * if all requirements are met: Exit the loop
			         */
			        if (direction > minDirection && direction < maxDirection && 
			        		direction != forbiddenDirection) {
			        	break;
			        }
				}
				// The secondary timer goes up by 1 millisecond
				secondaryTimer++;
				
				// This delay allows the touch sensor to be registered in the loop
				Delay.msDelay(1);
			}
		}
	
	// Stop the robot and wait for new instructions
	public void robotStop() {
			// Clear LCD
			Lcd.clear();
					
			// Halt usage of motors
			motorL.stop();
			motorR.stop();
	}
	
	/* 
	 * When bumper is touched, drive backwards, make a left/right turn
	 * then drive forwards and have the opposite corresponding left/right turn
	*/
	public void robotBumper() {
		
		// Declare and initialise the degrees of the left and right turn
		int left = 20;
		int right = -20;
		
		// Declare how much the robot will have to move
		int moveTimer = 300;
		
		// Clear LCD
		Lcd.clear();
		
		// Rewrite LCD
        Sound.beepSequenceUp();
		Lcd.print(4, "Bumper Touched");
        Lcd.print(5, "Stop!");
        
        // Move backwards
		moveBackward(moveTimer);
		
		/* 
		 * Choose left or right through a random number
		 * The random number is either 0 or 1
		*/
		Random randomizer = new Random();
		int direction = randomizer.nextInt(2);
		
		/* 
		 * Check whether the random number is left or right
		 * Then proceed to rotate to that direction
		 */
		if (direction == 0)
			rotate(left);
		else
			rotate(right);
		
		// Move forward
		moveForward(moveTimer);
		
		// Turn the opposite direction of the preceding rotation
		if (direction == 0)
			rotate(right);
		else 
			rotate(left);
	}
}