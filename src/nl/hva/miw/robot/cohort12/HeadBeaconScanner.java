package nl.hva.miw.robot.cohort12;

/**
 * Subclass for thread to run a separate while loop for the head scanner
 * movement
 * 
 * @author Huy
 *
 */

public class HeadBeaconScanner extends FollowBeaconLauncher implements Runnable {

	public void run() {
		boolean running = true;
		try {
			System.out.println("Running head beacon scanner");
			while (running) {
				head.rotateTo(-45);
				head.rotateTo(90);
				Thread.sleep(500);
			}
		} catch (Exception e) {
		} 	
		finally {
			head.close();
		}
	}
}
