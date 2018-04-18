package droneTest;


import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;

public class MainTest
{

	public static void main(String[] args)
	{
		IARDrone drone = null;
		try
		{
			// Tutorial Section 1
			drone = new ARDrone();

			drone.addExceptionListener(new IExceptionListener() {
				public void exeptionOccurred(ARDroneException exc)
				{
					exc.printStackTrace();
				}
			});

			drone.start();

			// Tutorial Section 2
//			new DroneAlttitudeListener(drone);



			// Tutorial Section 3
			new DroneVideoListener(drone);
			drone.getConfigurationManager().connect(25);
			drone.getCommandManager().isConnected();
		
		

			//			
			//			// Tutorial Section 4
			CommandManager cmd = drone.getCommandManager();
			int speed = 30;
			drone.setSpeed(speed);
			cmd.setVideoBitrate(256);
			cmd.setVideoCodecFps(30);
			cmd.takeOff().doFor(10000);
			System.out.println("Takeoff called");
			cmd.hover().doFor(10000);
			System.out.println("hover called");
			cmd.spinRight(10).doFor(20000);
			System.out.println("spinRight called");
//			cmd.forward(speed).doFor(10000);
//			cmd.backward(speed).doFor(10000);

			cmd.landing();
			System.out.println("landing called");



		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
		finally{
			if (drone != null);
		}
		drone.stop();

		System.exit(0);	}
}
