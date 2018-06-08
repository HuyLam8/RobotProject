package nl.hva.miw.robot.cohort12;

import lejos.hardware.Button;
import lejos.hardware.Sound;

/* Mario.java
 * Trying to play Mario theme song.
 */

public class Mario implements Runnable {
	private final static int C4 = 262;
	private final static int D4 = 294;
	private final static int E4 = 330;
	private final static int F4 = 349;
	private final static int G4 = 392;
	private final static int A4 = 440;
	private final static int As4 = 466;
	private final static int B4 = 494;

	private final static int C5 = 523;
	private final static int Cs5 = 554;
	private final static int D5 = 587;
	private final static int Ds5 = 622;
	private final static int E5 = 659;
	private final static int F5 = 698;
	private final static int Fs5 = 740;
	private final static int G5 = 784;
	private final static int A5 = 880;

	private final static int C6 = 1047;
	boolean play = true;

	public Mario() {
		super();
	}

	public boolean isPlay() {
		return play;
	}

	public static void playMario() {

		int vol = Sound.getVolume();
		System.out.println("I have to find my beacon!");
		Sound.setVolume(20);

		play(E5, 100, 75);
		play(E5, 100, 150);
		play(E5, 100, 150);
		play(C5, 100, 50);
		play(E5, 100, 150);
		play(G5, 100, 275);
		play(G4, 100, 287);

		for (int i = 0; i < 4; i++) {
			play(C5, 100, 225);
			play(G4, 100, 200);
			play(E4, 100, 250);
			play(A4, 100, 150);
			play(B4, 80, 165);
			play(As4, 100, 75);
			play(A4, 100, 150);
			play(G4, 100, 100);
			play(E5, 80, 100);
			play(G5, 50, 75);
			play(A5, 100, 150);
			play(F5, 80, 75);
			play(G5, 50, 175);
			play(E5, 80, 150);
			play(C5, 80, 75);
			play(D5, 80, 75);
			play(B4, 80, 250);
		}

		Sound.setVolume(vol);
	}

	private static void play(int freq, int dur, int delay) {
		if (100 <= freq && freq <= 12000 && 10 <= dur && dur <= 10000 && 10 <= delay && delay <= 10000) {
			Sound.playTone(freq, dur);
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void run() {
		playMario();
	}
}
