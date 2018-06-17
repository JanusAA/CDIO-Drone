package droneTest;

import java.util.Scanner;

import org.opencv.core.Core;

import QR.QRCode;
import controllers.DroneStateController;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import imageDetection.CircleScanner;

public class MainTestController {
	
	public final static int IMAGE_WIDTH = 640; // 640 or 1280
	public final static int IMAGE_HEIGHT = 360; // 360 or 720
	
	public final static int TOLERANCE = 40;
	
	public final static int SPEED = 30;
	
	private IARDrone drone = null;
	private CircleScanner circles = null;
	private AbstractTestController cmd;
	private QRCode scanner = null;
	private DroneStateController dsc = null;
	private Scanner scan = new Scanner(System.in);
	
	public MainTestController() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		drone = new ARDrone();
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().setVideoCodecFps(15);
		
		droneGUI gui = new droneGUI(drone);
		
		cmd = new DroneCommander((ARDrone) drone, SPEED, dsc);
		
		circles = new CircleScanner();
		circles.addListener(gui);
		scanner = new QRCode();
		circles.addListener(cmd);
		
		
		
		drone.getVideoManager().addImageListener(scanner);
		drone.getVideoManager().addImageListener(gui);
		drone.getVideoManager().addImageListener(circles);
		
		scan.nextLine();
		
		drone.getCommandManager().flatTrim();
		cmd.takeOff();
		cmd.hover();
//		cmd.moveToAltitude(1500);
		cmd.increaseAltitude(20, 1500);
		cmd.hover();
		cmd.findCircleCenter();
		scan.nextLine();
	
		
	}
	public static void main(String[] args)
	{
		new GUITest();
	}

}
