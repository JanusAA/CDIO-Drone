package droneTest;

/**
 * Author: Aleksander & Morten
 * Drone Gyro Listener
 * not yet implemented
 * Supposed to be used for coalition  
 */

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.AttitudeListener;
import de.yadrone.base.navdata.GyroListener;
import de.yadrone.base.navdata.GyroPhysData;
import de.yadrone.base.navdata.GyroRawData;

public class DroneGyroListener {

	public DroneGyroListener(IARDrone drone){

		drone.getNavDataManager().addGyroListener(new GyroListener() {
			

			@Override
			public void receivedOffsets(float[] arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void receivedPhysData(GyroPhysData data) {
				System.out.println("GyroData: "+ data);
			}

			@Override
			public void receivedRawData(GyroRawData arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
