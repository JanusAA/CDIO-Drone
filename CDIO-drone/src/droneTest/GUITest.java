package droneTest;

import org.opencv.core.Core;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import imageDetection.CircleScanner;

public class GUITest {
	
	public final static int IMAGE_WIDTH = 640; // 640 or 1280
	public final static int IMAGE_HEIGHT = 360; // 360 or 720
	
	public final static int TOLERANCE = 40;
	
	private IARDrone drone = null;
	private CircleScanner circles = null;
	
	public GUITest() {
		drone = new ARDrone();
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		
		//drone.getCommandManager().setVideoCodec(VideoCodec.H264_360P);

		
		
		
		droneGUI gui = new droneGUI(drone, this);
		drone.getVideoManager().addImageListener(gui);
		
		circles = new CircleScanner();
		circles.addListener(gui);
	}


	public void enableAutoControl(boolean selected) {

	}
	public static void main(String[] args)
	{
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		new GUITest();
	}
}
