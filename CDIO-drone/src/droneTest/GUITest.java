package droneTest;

import java.util.Scanner;

import org.opencv.core.Core;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import QR.QRCode;
import controllers.MainController;
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
	private QRCode scanner = null;
	private DroneStateController dsc = null;
	private Scanner scan=new Scanner(System.in);
	
	public GUITest() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		drone = new ARDrone();
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		
		droneGUI gui = new droneGUI(drone);
		
		DroneCommander cmd = new DroneCommander((ARDrone) drone, SPEED, gui);
		
		circles = new CircleScanner();
		circles.addListener(gui);
		scanner = new QRCode();
		circles.addListener(cmd);
		
		drone.getVideoManager().addImageListener(scanner);
		drone.getVideoManager().addImageListener(gui);
		drone.getVideoManager().addImageListener(circles);
		
		scan.nextLine();
		
		cmd.takeOff();
		cmd.moveToAltitude(1400);
		try {
			dsc.centralizeQR();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
			cmd.findCircleCenter();
		}
		
	}
	public static void main(String[] args)
	{
		new GUITest();
	}
}
