package droneTest;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;

public class GUITest {
	
	public final static int IMAGE_WIDTH = 640; // 640 or 1280
	public final static int IMAGE_HEIGHT = 360; // 360 or 720
	
	public final static int TOLERANCE = 40;
	
	private IARDrone drone = null;
	
	
	public GUITest() {
		drone = new ARDrone();
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		
		droneGUI gui = new droneGUI(drone, this);
		drone.getVideoManager().addImageListener(gui);
	}


	public void enableAutoControl(boolean selected) {

	}
	public static void main(String[] args)
	{
		new GUITest();
	}
}
