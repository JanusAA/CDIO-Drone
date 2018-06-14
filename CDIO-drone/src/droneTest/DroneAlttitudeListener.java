package droneTest;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.navdata.AttitudeListener;
import de.yadrone.base.navdata.BatteryListener;

public class DroneAlttitudeListener
{
	private int altitude;

	public DroneAlttitudeListener(IARDrone drone)
	{
		drone.getNavDataManager().addAttitudeListener(new AttitudeListener() {

			public void attitudeUpdated(float pitch, float roll, float yaw)
			{
				System.out.println("Pitch: " + pitch + " Roll: " + roll + " Yaw: " + yaw);
			}

			public void attitudeUpdated(float pitch, float roll) { }
			public void windCompensation(float pitch, float roll) { }
		});
		
		drone.getNavDataManager().addAltitudeListener(new AltitudeListener(){
			@Override
			public void receivedAltitude(int a) {
				altitude = a;
			}
			
			@Override
			public void receivedExtendedAltitude(Altitude b){
		
			}
			
		});

		drone.getNavDataManager().addBatteryListener(new BatteryListener() {

			public void batteryLevelChanged(int percentage)
			{
				System.out.println("Battery: " + percentage + " %");
			}

			public void voltageChanged(int vbat_raw) { }
		});
	}
	public int getAltitude() {
		return altitude;
	}

}