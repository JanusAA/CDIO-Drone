package droneTest;

/**
 * Really early testing
 * about same time as maintest the tutorial
 * used for learning the basics of ya drone 
 * and understading how the drone works
 */

import de.yadrone.apps.controlcenter.plugins.qrcode.*;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;

public class Test {

	public static void main(String[]args){

		IARDrone drone = null;
		try{
			drone = new ARDrone();
			drone.start();

			new DroneVideoListener(drone);
			
			drone.getVideoManager().start();
			drone.setSpeed(30);
			drone.getCommandManager().takeOff();
			System.out.println("Takeoff Called");
			drone.getCommandManager().hover().doFor(10000);
			System.out.println("Hover called");
			drone.getCommandManager().spinRight(10).doFor(20000);
			System.out.println("SpinRight called");
			drone.getCommandManager().landing();
			System.out.println("Landing called");

		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			if (drone != null)
				drone.stop();
			System.exit(0);
		}
	}

}
