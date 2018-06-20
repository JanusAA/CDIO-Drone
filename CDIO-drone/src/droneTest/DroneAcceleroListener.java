package droneTest;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.AcceleroListener;
import de.yadrone.base.navdata.AcceleroPhysData;
import de.yadrone.base.navdata.AcceleroRawData;

/**
 * Author: Morten
 * Drone Accelero Listener
 * a Listener used to calculate the acceleration of the drone
 * not yet implemented
 */
public class DroneAcceleroListener {

	public DroneAcceleroListener(IARDrone drone){
		
		drone.getNavDataManager().addAcceleroListener(new AcceleroListener() {

			@Override
			public void receivedPhysData(AcceleroPhysData data) {
				System.out.println("Accelero data: " + data);
				
			}

			@Override
			public void receivedRawData(AcceleroRawData arg0) {
				System.out.println("Raw Acc data: " + arg0);
				
			}
			
		}
				
				);
	}
}
