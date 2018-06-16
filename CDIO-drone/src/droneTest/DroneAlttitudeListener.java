package droneTest;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.navdata.AttitudeListener;
import de.yadrone.base.navdata.BatteryListener;

public class DroneAlttitudeListener
{
	private int altitude;

	
	public int getAltitude() {
		return altitude;
	}

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