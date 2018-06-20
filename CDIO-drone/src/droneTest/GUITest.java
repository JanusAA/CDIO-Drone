package droneTest;

/**
 * The First Main class
 * Used for early fly testing
 */

import java.util.Scanner;

import org.opencv.core.Core;

import QR.QRCode;
import controllers.DroneStateController;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import imageDetection.CircleScanner;

public class GUITest {
	
	public final static int IMAGE_WIDTH = 640; // 640 or 1280
	public final static int IMAGE_HEIGHT = 360; // 360 or 720
	
	public final static int TOLERANCE = 40;
	
	public final static int SPEED = 30;
	
	private IARDrone drone = null;
	private CircleScanner circles = null;
	private DroneCommander cmd;
	private QRCode scanner = null;
	private DroneStateController dsc = null;
	private Scanner scan = new Scanner(System.in);
	
	public GUITest() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		drone = new ARDrone();
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().setVideoCodecFps(15);
		
		droneGUI gui = new droneGUI(drone);
		
		cmd = new DroneCommander((ARDrone) drone, SPEED, dsc);
		
		circles = new CircleScanner();
		scanner = new QRCode();
		circles.addListener(gui);
		scanner.addListener(gui);
//		scanner.addListener(cmd);
		
		
		
		drone.getVideoManager().addImageListener(scanner);
		drone.getVideoManager().addImageListener(gui);
		drone.getVideoManager().addImageListener(circles);
		
		scan.nextLine();
		
		drone.getCommandManager().flatTrim();
		cmd.run();
	
		
	}
	public static void main(String[] args)
	{
		new GUITest();
	}
}
