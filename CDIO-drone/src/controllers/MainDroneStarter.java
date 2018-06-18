package controllers;

import java.io.FileNotFoundException;
import java.util.Scanner;

import org.opencv.core.Core;

import QR.QRCode;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.navdata.AttitudeListener;
import imageDetection.CircleScanner;

public class MainDroneStarter {
	public final static int IMAGE_WIDTH = 1280 /2;
	public final static int IMAGE_HEIGHT = 720 /2;

	public final static int TOLERANCE = 35;

	private IARDrone drone = null;
	private MainController droneControl;
	private QRCode scanner = null;
	
	private boolean autoControlEnabled = false;
	
	public MainDroneStarter(){
		drone = new ARDrone();
		droneControl = new MainController(drone);
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().setVideoCodecFps(30);
		
		drone.getNavDataManager().addAttitudeListener(new AttitudeListener() {
			public void attitudeUpdated(float pitch, float roll, float yaw) {
			}

			@Override
			public void attitudeUpdated(float pitch, float roll) {
			}

			@Override
			public void windCompensation(float pitch, float roll) {
			}
		});
		
		GUI gui = new GUI(drone, this);

		scanner = new QRCode();
		scanner.addListener(gui);
		
		CircleScanner circlescan = new CircleScanner();
		
		circlescan.addListener(droneControl);
		circlescan.addListener(gui);

		drone.getVideoManager().addImageListener(droneControl);
		drone.getVideoManager().addImageListener(gui);
		drone.getVideoManager().addImageListener(circlescan);
		drone.getVideoManager().addImageListener(scanner);
		
		Scanner scan = new Scanner(System.in);
		scan.nextLine();
		droneControl.run();
		
		CommandManager cmd = drone.getCommandManager();
		
	}
	
	public MainController getDroneControl(){
		return droneControl;
	}
	
	public void enableAutoControl(boolean enable){
		if(enable){
			scanner.addListener(droneControl);
			new Thread(droneControl).start();
		}
		else {
			droneControl.stopControl();
			scanner.removeListener(droneControl);
		}
		this.autoControlEnabled = enable;
		}
	
	// Main program start
		public static void main(String[] args) throws FileNotFoundException {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Load OpenCV
			new MainDroneStarter();
		}

		public boolean getAutoControlEnabled() {
			return autoControlEnabled;
		}

		public int getAltitude() {
			return this.droneControl.getAltitude();
		}
	}
	


