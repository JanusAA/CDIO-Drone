package controllers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import QR.QRListener;
import de.yadrone.base.IARDrone;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;
import de.yadrone.base.exception.VideoException;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.StateListener;
import de.yadrone.base.video.ImageListener;
import imageDetection.Circle;
import imageDetection.CircleListener;

public class GUI extends JFrame implements ImageListener, CircleListener, QRListener{
	

	private static final long serialVersionUID = 45532;
	private IARDrone drone;
	private MainDroneStarter main;
	
	private Circle[] circles;
	private int imgScale = 1;
	
	private BufferedImage image = null;
	private Result result;
	private String orientation;
	
	
	private JPanel videoPanel;
	
	private boolean qr = true;
	private boolean circle = true;
	
	public GUI(final IARDrone drone, MainDroneStarter main)
	{
		super("CDIO-Drone");
		this.main = main;
		this.drone = drone;
	
		setSize(main.IMAGE_WIDTH, main.IMAGE_HEIGHT);
        setVisible(true);
        setResizable(false);
        
        addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				drone.stop();
				System.exit(0);
			}
		});
        
        setLayout(new GridBagLayout());
        
        add(createVideoPanel(), new GridBagConstraints(0, 0, 1, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
        
        // add listener to be notified once the drone takes off so that the game timer counter starts
        drone.getNavDataManager().addStateListener(new StateListener() {
			
			public void stateChanged(DroneState state)
			{
				if (state.isFlying())
				{
					drone.getNavDataManager().removeStateListener(this);
				}
			}
			
			public void controlStateChanged(ControlState state) { }
		});
        
        pack();
	}
	

	private JPanel createVideoPanel()
	{
		videoPanel = new JPanel() {

			private static final long serialVersionUID = 1L;
			private Font tagFont = new Font("SansSerif", Font.BOLD, 14);
			
        	public void paint(Graphics g)
        	{
        		if (image != null)
        		{
        			// now draw the camera image
        			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    				
        			
    				// draw triangle if tag is visible
        			if (result != null)
        			{
        				ResultPoint[] points = result.getResultPoints();
        				ResultPoint a = points[1]; // top-left
        				ResultPoint b = points[2]; // top-right
        				ResultPoint c = points[0]; // bottom-left
        				ResultPoint d = points.length == 4 ? points[3] : points[0]; // alignment point (bottom-right)
        				
        				g.setColor(Color.GREEN);
        				
        				g.drawPolygon(new int[] {(int)a.getX(),(int)b.getX(),(int)d.getX(),(int)c.getX()}, 
  						      new int[] {(int)a.getY(),(int)b.getY(),(int)d.getY(),(int)c.getY()}, 4);
        				
        				g.setColor(Color.RED);
        				g.setFont(tagFont);
        				g.drawString(result.getText(), (int)a.getX(), (int)a.getY());
        				g.drawString(orientation, (int)a.getX(), (int)a.getY() + 20);
        				
        				if ((System.currentTimeMillis() - result.getTimestamp()) > 1000)
        				{
        					result = null;
        				}
        			//}
        			
        			
        			//Draw circles
        			if (circles != null)
						for (Circle cir : circles) {
							g.setColor(Color.RED);
							g.drawRect((int) cir.x * imgScale, (int) cir.y * imgScale, 10, 10);
							g.setColor(Color.BLUE);
							g.drawOval((int) (cir.x - cir.r) * imgScale, (int) (cir.y - cir.r) * imgScale,
									(int) (2 * cir.r) * imgScale, (int) (2 * cir.r) * imgScale);
							g.drawString(cir.toString(), (int) cir.x * imgScale + 10, (int) cir.y * imgScale + 10);
						}
        			}}
        	
        		else
        		{
        			// draw "Waiting for video"
        			g.setColor(Color.RED);
    				g.setFont(tagFont);
        			g.drawString("Waiting for Video ...", 10, 20);
        		}
        		}
        };         
        videoPanel.setSize(main.IMAGE_WIDTH, main.IMAGE_HEIGHT);
        videoPanel.setMinimumSize(new Dimension(main.IMAGE_WIDTH, main.IMAGE_HEIGHT));
        videoPanel.setPreferredSize(new Dimension(main.IMAGE_WIDTH, main.IMAGE_HEIGHT));
        videoPanel.setMaximumSize(new Dimension(main.IMAGE_WIDTH, main.IMAGE_HEIGHT));
        
        return videoPanel;
	}

	private long imageCount = 0;
	
	public void imageUpdated(BufferedImage newImage)
    {
		if ((++imageCount % 5) == 0)
			return;
		
    	image = newImage;
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				videoPanel.repaint();
			}
		});
    }

	@Override
	public void circlesUpdated(Circle[] circles) {
		this.circles = circles;
	}
	
	class ExeptionListener implements IExceptionListener {
		@Override
		public void exeptionOccurred(ARDroneException exc) {
			if (exc.getClass().equals(VideoException.class)) {
				System.out.println("Got VideoException, trying to restart");
				drone.getVideoManager().reinitialize();
			}
		}
	}
	
	public boolean getQR(){
		return qr;
	}
	
	public void setQR(boolean TorF){
		qr = TorF;
	}
	
	public boolean getCircleBoolean(){
		return circle;
	}
	
	public void setCircleBoolean(boolean TorF){
		circle = TorF;
	}
	
	public void onTag(Result result, float orientation)
	{
		if (result != null)
		{
			this.result = result;
			this.orientation = orientation + "�";
			
		}}
	
}
