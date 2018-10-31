package utility;

import lejos.utility.Delay;

///speelclass als ik per ongeluk gepushed wordt mag je mij verwijderen
public class toy {
	public static void main(String[] args) {
		System.out.println("hello");
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		Delay.msDelay(500);
		System.out.println(stopwatch.getElapsedTime());
		Delay.msDelay(100);
		System.out.println(stopwatch.getElapsedTime());
		Delay.msDelay(500);
		stopwatch.stop();
		System.out.println(stopwatch.getElapsedTime());
		Delay.msDelay(100);
		System.out.println(stopwatch.getElapsedTime()/100.0);
		
	}
}
