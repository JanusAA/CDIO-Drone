package imageDetection;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import de.yadrone.base.video.ImageListener;

public class CircleScannerCMD implements ImageListener {

	private ArrayList<CircleListener> listeners = new ArrayList<CircleListener>();
	private long imageCounter = 0;
	private int framesWanted = 15; // Checks every 15th frame
	private static int blurLevel = 5; // Amount of blurring,
	public static Circle[] cir = new Circle[1];
	private static double dp = 1.1;
	private static int minDist = 50; // Distance between centers
	private static boolean saveCircle = false;
	private static boolean circleSaved = false;
	private static Circle[] lastCircle = null;
	
	//Method for scanning for circles in a matrix.
	public static Circle[] scanForCircles(Mat image){	
	    
		Size imgSize = new Size(0,0);
		if (image.size().height > 900)
			Imgproc.resize(image, image, imgSize, 0.5,0.5,1);
	
			Mat gray = image.clone();
			// Get the gray img
			Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
 		
			// Detect circles
			Mat circles = new Mat();
			Size s = new Size(blurLevel,blurLevel);
			Imgproc.GaussianBlur(gray, gray, s, 2);
			Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, dp, minDist);
    
			cir = new Circle[circles.cols()];
			double[] circlePoints;

			for(int i = 0; i < circles.cols(); i++){
				circlePoints = circles.get(0, i);
				//System.out.println("x: " + circlePoints[0] + " y: " + circlePoints[1] + " r: " + circlePoints[2]);
				cir[i] = new Circle(circlePoints[0], circlePoints[1], circlePoints[2]);
			}
		return cir;
		
		
	}
	
	
	//Convert bufferedImage to Mat
	// https://stackoverflow.com/questions/14958643/converting-bufferedimage-to-mat-in-opencv?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
	public static Circle[] scanForCirclesBuff(BufferedImage bi){
		 Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  
		  return scanForCircles(mat);
	}	
	
	//add listener method
	public void addListener(CircleListener listener) {
		this.listeners.add(listener);
	}
	
	
	//updated image method for when the image changes.
	@Override
	public void imageUpdated(BufferedImage img) {
		// We don't need to find circles in every frame
		if(saveCircle){
				if ((imageCounter++ % framesWanted) != 0)
					return;
				Circle[] circles = scanForCirclesBuff(img);
				for (CircleListener listener : listeners)
					listener.circlesUpdated(circles);
				setSaveCircle(false);
				setCircleSaved(true);
		}
	}
	
	public static void setSaveCircle(boolean TorF){
		saveCircle = TorF;
	}
	
	public static void setCircleSaved(boolean TorF){
		circleSaved = TorF;
	}
	
	public static void updateCurrentCircle(){
		setCircleSaved(false);
		setSaveCircle(true);
	}
	
	
	public static void setNullLastCircle(){
		lastCircle = null;
	}
	
	public static boolean getCircleSaved(){
		return circleSaved;
	}
	
	public static Circle[] getCircle(){
		return cir;
	}

}

