package imageDetection;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import de.yadrone.base.video.ImageListener;

public class Rectangle implements ImageListener {
	
	private ArrayList<RectangleListener> listeners = new ArrayList<RectangleListener>();
	private long imageCounter = 0;
	private int framesWanted = 5; // Checks every 5th frame
	private static int blurLevel = 5;
	
	public void findRectangle(Mat src) throws Exception {
		  Mat blurred = src.clone();
		  Imgproc.medianBlur(src, blurred, 9);

		  Mat gray0 = new Mat(blurred.size(), CvType.CV_8U), gray = new Mat();

		  List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		  List<Mat> blurredChannel = new ArrayList<Mat>();
		  blurredChannel.add(blurred);
		  List<Mat> gray0Channel = new ArrayList<Mat>();
		  gray0Channel.add(gray0);

		  MatOfPoint2f approxCurve;

		  double maxArea = 0;
		  int maxId = -1;

		  for (int c = 0; c < 3; c++) {
		   int ch[] = { c, 0 };
		   Core.mixChannels(blurredChannel, gray0Channel, new MatOfInt(ch));

		   int thresholdLevel = 1;
		   for (int t = 0; t < thresholdLevel; t++) {
		    if (t == 0) {
		     Imgproc.Canny(gray0, gray, 10, 20, 3, true); // true ?
		     Imgproc.dilate(gray, gray, new Mat(), new Point(-1, -1), 1); // 1
		                     // ?
		    } else {
		     Imgproc.adaptiveThreshold(gray0, gray, thresholdLevel,
		       Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
		       Imgproc.THRESH_BINARY,
		       (src.width() + src.height()) / 200, t);
		    }

		    Imgproc.findContours(gray, contours, new Mat(),
		      Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		    for (MatOfPoint contour : contours) {
		     MatOfPoint2f temp = new MatOfPoint2f(contour.toArray());

		     double area = Imgproc.contourArea(contour);
		     approxCurve = new MatOfPoint2f();
		     Imgproc.approxPolyDP(temp, approxCurve,
		       Imgproc.arcLength(temp, true) * 0.02, true);

		     if (approxCurve.total() == 4 && area >= maxArea) {
		      double maxCosine = 0;

		      List<Point> curves = approxCurve.toList();
		      for (int j = 2; j < 5; j++) {

		       double cosine = Math.abs(angle(curves.get(j % 4),
		         curves.get(j - 2), curves.get(j - 1)));
		       maxCosine = Math.max(maxCosine, cosine);
		      }

		      if (maxCosine < 0.3) {
		       maxArea = area;
		       maxId = contours.indexOf(contour);
		      }
		     }
		    }
		   }
		  }

		  if (maxId >= 0) {
		    Imgproc.drawContours(src, contours, maxId, new Scalar(255, 0, 0,
		    .8), 8);

		  }
		 }
		

		private double angle(Point p1, Point p2, Point p0) {
		  double dx1 = p1.x - p0.x;
		  double dy1 = p1.y - p0.y;
		  double dx2 = p2.x - p0.x;
		  double dy2 = p2.y - p0.y;
		  return (dx1 * dx2 + dy1 * dy2)
		    / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2)
		      + 1e-10);
		 }
		
		public void scanForRect(BufferedImage bi) throws Exception{
			 Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
			  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
			  mat.put(0, 0, data);
			  System.out.println("hello");
			  findRectangle(mat);
		}	



		
		private static ArrayList<Rect> getContourArea(Mat mat) {
			
			Size imgSize = new Size(0,0);
			if (mat.size().height > 1200)
					Imgproc.resize(mat, mat, imgSize, 0.5,0.5,1);
			Size s = new Size(blurLevel,blurLevel);
			
			Mat hierarchy = new Mat();
			Mat image = mat.clone();
			
			Imgproc.cvtColor(mat, image, Imgproc.COLOR_RGB2GRAY);
            Imgproc.equalizeHist(image, image);
            Imgproc.GaussianBlur(image, image, s, 9);
            //Imgproc.medianBlur(image, hierarchy, blurLevel);

            Imgproc.threshold(image, image, 1, 255, Imgproc.THRESH_BINARY);
			
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			Rect rect = null;
			double maxArea = 300;
			ArrayList<Rect> arr = new ArrayList<Rect>();
			for (int i = 0; i < contours.size(); i++) {
				Mat contour = contours.get(i);
				double contourArea = Imgproc.contourArea(contour);
				if (contourArea > maxArea) {
					rect = Imgproc.boundingRect(contours.get(i));
					System.out.println("Rectangle put in array");
					System.out.println("X:" + rect.x + " Y:" + rect.y + " W:" + rect.width + " H:" + rect.height);
					arr.add(rect);
				}
			}
			return arr;
		}
		
		public static ArrayList<Rect> getContourAreaBuff(BufferedImage bi){
			Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
			  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
			  mat.put(0, 0, data);
			  return getContourArea(mat);
		}
		
		public void addListener(RectangleListener listener) {
			this.listeners.add(listener);
		}
		
		@Override
		public void imageUpdated(BufferedImage img) {
			// TODO Auto-generated method stub
			if ((imageCounter++ % framesWanted) != 0)
				return;
			ArrayList<Rect> rectangles = getContourAreaBuff(img);
			for (RectangleListener listener : listeners)
				listener.rectanglesUpdated(rectangles);
		}
}
