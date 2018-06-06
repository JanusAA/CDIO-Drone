package imageDetection;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import de.yadrone.base.video.ImageListener;

public class CircleScanner implements ImageListener {

	private ArrayList<CircleListener> listeners = new ArrayList<CircleListener>();
	private long imageCount = 0;
	private final int frameSkip = 5; // Only check every n frames. Must be > 0. 1 == no skip.
	
	
	public static Circle[] scanForCircles(Mat image){	
	Size imgSize = new Size(0,0);
	if (image.size().height > 1200)
			Imgproc.resize(image, image, imgSize, 0.5,0.5,1);
	
    Mat gray = image.clone();
    
    Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
    Imgproc.medianBlur(gray, gray, 5);
    Mat circles = new Mat();
    
    Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.1, 1); // change the last two parameters
            // (min_radius & max_radius) to detect larger circles
    
    Circle[] cir = new Circle[circles.cols()];
    double[] circlePoints;
    
    for(int i = 0; i < circles.cols(); i++){
    	circlePoints = circles.get(0, i);
    	System.out.println("Circle put in array");
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
	
	
	
	public void addListener(CircleListener listener) {
		this.listeners.add(listener);
	}
	
	
	
	
	
	@Override
	public void imageUpdated(BufferedImage img) {
		// We don't need to find circles in every frame
				if ((imageCount++ % frameSkip) != 0)
					return;
				Circle[] circles = scanForCirclesBuff(img);
				for (CircleListener listener : listeners)
					listener.circlesUpdated(circles);
	}

}


