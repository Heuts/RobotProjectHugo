package model;

import lejos.hardware.Button;
import lejos.hardware.Sound;

public abstract class View {
	/**
	 * Hulpfunctie die groene led aanzet en piepend geluid maakt
	 * Indicatie dat userinput wordt verwacht
	 */
	public static void alertUp() {
		Button.LEDPattern(4);    // flash green led and 
        Sound.beepSequenceUp();  // make sound when ready.
	}
	
	public static void alertDown() {
		Sound.beepSequence();
	}
}
