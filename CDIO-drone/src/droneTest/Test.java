package droneTest;

import de.yadrone.apps.controlcenter.plugins.qrcode.*;
import de.yadrone
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;

public class Test {

	public static void main(String[]args){
		
		IARDrone drone = null;
		try{
			drone = new ARDrone();
			drone.start();
			
			
			drone.getCommandManager().takeOff();
			drone.getCommandManager().waitFor(5000);
			drone.getCommandManager().goLeft(30);
			drone.getCommandManager().waitFor(5000);
			drone.getCommandManager().goRight(30);
			drone.getCommandManager().waitFor(5000);
			drone.getCommandManager().landing();
			
			
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
