package controllers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import org.opencv.core.Point;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import QR.QRCode;
import QR.QRListener;
import controllers.DroneStateController.Command;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;
import de.yadrone.base.exception.VideoException;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.video.ImageListener;
import droneTest.DroneCommander;
import droneTest.GUITest;
import imageDetection.Circle;
import imageDetection.CircleListener;


public class MainController extends AbstractDroneController implements QRListener, CircleListener, ImageListener {
	private int speed = 30;  // The base velocity in %
	private int slowspeed = 5;  //  The velocity used for centralizing in %
	private int slowtime = 200;	//	The time centralizing commands are done in ms
	private double ErrorMargin = 20;	// the Margin of Error in which the center of a circle can be fount
	private double midPoint_x = MainDroneStarter.IMAGE_WIDTH/2;	// Camera midpoint in x
	private double midPoint_y = MainDroneStarter.IMAGE_HEIGHT/2;	// camera midpoint in y
	private float tagOrientation;
	private double max_radius = 120;	// The size of the circle we want on the camera
	protected Result QRtag;
	private Circle[] circles;
	private int altitude;

	protected double lastImageTimer;
	protected int circleRadius = (int) (MainDroneStarter.IMAGE_HEIGHT * 0.45);
	private ArrayList<String> gates = new ArrayList<String>();
	private DroneStateController stateCon;

	public DroneStateController getStateCon() {
		return stateCon;
	}

	public MainController(IARDrone drone){
		super(drone);
		setupAltitudeListener();
		drone.addExceptionListener(new ExceptionListener());
	}

	@Override
	public void run() {
		this.doStop = false;
		stateCon = new DroneStateController(this, drone, drone.getCommandManager());
		stateCon.state = Command.TakeOff;
		while(!doStop)
		{
			try {
				stateCon.commands(stateCon.state);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}

	


	Circle[] getCircles() {
		return circles;
	}

	public void onTag(Result result, float orientation) {
		if (result == null)
			return;
		QRtag = result;
	}

	public boolean CircleIsCentered() throws InterruptedException {
		boolean centered = false;
		if(circles.length > 0){
			Circle[] circle = circles;
			double circle_x = Math.abs(circle[0].x);
			double circle_y = Math.abs(circle[0].y);
			double circle_r = Math.abs(circle[0].r);
			System.out.println(circle_r);
			double abs_dif_x = Math.abs(circle_x - midPoint_x);
			double abs_dif_y = Math.abs(circle_y - midPoint_y);
			double abs_dif_r = Math.abs(circle_r - max_radius);

			if(!(circle_x <= midPoint_x + (ErrorMargin) && circle_x >= midPoint_x - (ErrorMargin))){
			if(abs_dif_x > (abs_dif_y) && abs_dif_x > abs_dif_r){
				if (circle_x < (midPoint_x + (ErrorMargin/2)))
				{
					System.out.println("Go left");
					drone.getCommandManager().goLeft(slowspeed).doFor(1500);
					drone.getCommandManager().hover().doFor(200);
				}
				else if (circle_x > (midPoint_x + (ErrorMargin/2)))
				{
					System.out.println("Go right");
					drone.getCommandManager().goRight(slowspeed).doFor(800);
					drone.getCommandManager().hover().doFor(200);
				}
			}
			}
			if(!(circle_y <= midPoint_y + (ErrorMargin) && circle_y >= midPoint_y - (ErrorMargin))){
			if((abs_dif_y) > abs_dif_x && abs_dif_y > abs_dif_r){
				if (circle_y < (midPoint_y + (ErrorMargin/4)))
				{
					System.out.println("Go up");
					drone.getCommandManager().up(slowspeed).doFor(800);
					drone.getCommandManager().hover().doFor(200);
				}
				else if (circle_y > (midPoint_y + (ErrorMargin/4)))
				{
					System.out.println("Go down");
					drone.getCommandManager().down(slowspeed).doFor(800);
					drone.getCommandManager().hover().doFor(200);
				}
			}
			}
			if(!(circle_r <= max_radius + (ErrorMargin/2) && circle_r >= max_radius - (ErrorMargin/2))){
				if((abs_dif_r) > abs_dif_x && abs_dif_r > abs_dif_y){
			if (circle_r < max_radius){
				System.out.println("PaperChaseAutoController: Go forward");
				drone.getCommandManager().forward(slowspeed).doFor(800);
				drone.getCommandManager().hover().doFor(200);
			}
			else if (circle_r > max_radius){
				System.out.println("PaperChaseAutoController: Go backwards");
				drone.getCommandManager().backward(slowspeed).doFor(800);
				drone.getCommandManager().hover().doFor(200);
				}
			}
			}
			System.out.println("Hovering");
			drone.getCommandManager().hover().doFor(1000);
			System.out.println("Hovering ended");
			if(circle_x <= midPoint_x + (ErrorMargin) && circle_x >= midPoint_x - (ErrorMargin)){
				if(circle_y <= midPoint_y + (ErrorMargin) && circle_y >= midPoint_y - (ErrorMargin)){
					if(circle_r <= max_radius + (ErrorMargin/2) && circle_r >= max_radius - (ErrorMargin/2)){
					System.out.println("Centreret!!");
					centered = true;
					}
				}}
			return centered;
		}
		return centered;
	}

	public boolean isQRCentered(){
		if (QRtag == null)
			return false;

		Point QRcenter = getQRCenter(this.QRtag);

		int CenterX =  (int) midPoint_x;
		int CenterY = (int) midPoint_y;

		return (( QRcenter.x > (CenterX - ErrorMargin))
				&& (QRcenter.x < (CenterX + ErrorMargin))
				&& (QRcenter.y > (CenterY - ErrorMargin))
				&& (QRcenter.y < (CenterY + ErrorMargin)
						&& (getQRSize() < (MainDroneStarter.IMAGE_WIDTH / 14))));
	}

	@Override
	public void imageUpdated(BufferedImage image) {
		this.lastImageTimer = System.currentTimeMillis();
	}

	@Override
	public void circlesUpdated(Circle[] circle) {
		this.circles = circle;
	}

	public double getQRSize() {
		if (QRtag != null){
			ResultPoint[] points = QRtag.getResultPoints();
			return points[2].getX() - points[1].getX();
		}			
		else
			return 0.0;
	}

	private Point getQRCenter(Result tag) {
		ResultPoint[] points = tag.getResultPoints();
		double dy = (points[0].getY() + points[1].getY()) / 2; 
		double dx = (points[1].getX() + points[2].getX()) / 2; 
		return new Point(dx, dy);
	}

	public double getQRRelativeAngle(Result tag) {
		final double cameraAngle = 92;
		final double imgCenterX = (int) midPoint_x;
		double degPerPx = cameraAngle / (int) midPoint_x;

		synchronized (tag) {
			if (tag == null)
				return 0.0;
			Point qrCenter = getQRCenter(tag);
			return (qrCenter.x - imgCenterX) * degPerPx;
		}
	}

	public double getQRRelativeAngle() {
		return getQRRelativeAngle(this.QRtag);
	}

	private void setupAltitudeListener() {
		drone.getNavDataManager().addAltitudeListener(new AltitudeListener() {
			@Override
			public void receivedAltitude(int a) {
				altitude = a;
			}

			@Override
			public void receivedExtendedAltitude(Altitude d) {
			}
		});
	}

	public int getAltitude() {
		return this.altitude;
	}

	class ExceptionListener implements IExceptionListener {
		@Override
		public void exeptionOccurred(ARDroneException exc) {
			if (exc.getClass().equals(VideoException.class)) {
				System.out.println("Video issue, trying to establish videofeed");
				drone.getVideoManager().reinitialize();
			}
		}
	}
}