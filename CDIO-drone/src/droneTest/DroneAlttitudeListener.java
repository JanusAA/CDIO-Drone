package droneTest;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.navdata.AttitudeListener;
import de.yadrone.base.navdata.BatteryListener;


/**
 * Author: Janus
 * Altitude Listener
 * Used to control the altitude of the drone
 */
public class DroneAlttitudeListener
{
	private int altitude;

	/**
	 * returns the altitude
	 * @return
	 */
	public int getAltitude() {
		return altitude;
	}

	/**
	 * adds a Altitudelistener for the drone
	 * @param drone
	 */
	public void addAltListener(IARDrone drone){
		drone.getNavDataManager().addAltitudeListener(new AltitudeListener(){
			@Override
			public void receivedAltitude(int a) {
				altitude = a;
			}
			
			@Override
			public void receivedExtendedAltitude(Altitude b){
		
			}
			
		});
	}
	/**
	 * removes the altitude listener from the drone
	 * @param drone
	 */
	public void removeAltListener(IARDrone drone){
		drone.getNavDataManager().removeAltitudeListener(new AltitudeListener(){

			@Override
			public void receivedAltitude(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void receivedExtendedAltitude(Altitude arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});

	}

}