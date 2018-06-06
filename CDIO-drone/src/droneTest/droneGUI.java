package droneTest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.StateListener;
import de.yadrone.base.video.ImageListener;
import imageDetection.Circle;
import imageDetection.CircleListener;

public class droneGUI extends JFrame implements ImageListener, CircleListener{
	

	private static final long serialVersionUID = 45532;
	private GUITest main;
	private IARDrone drone;
	
	private Circle[] circles; //testing this
	private int imgScale = 2;
	
	
	private BufferedImage image = null;
	private Result result;
	private String orientation;
	
	private String[] shredsToFind = new String[] {"Shred 1", "Shred 2"};
	private boolean[] shredsFound = new boolean[] {false, false};
	
	private JPanel videoPanel;
	
	private Timer timer = new Timer();
	private long gameStartTimestamp = System.currentTimeMillis();
	private String gameTime = "0:00";
	
	private boolean gameOver = false;
	
	public droneGUI(final IARDrone drone, GUITest main)
	{
		super("CDIO-Drone");
        
		this.main = main;
		this.drone = drone;
		
		setSize(GUITest.IMAGE_WIDTH, GUITest.IMAGE_HEIGHT);
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
					startGameTimeCounter();
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
			
			private Font tagFont = new Font("SansSerif", Font.BOLD, 14);
			private Font timeFont = new Font("SansSerif", Font.BOLD, 18);
			private Font gameOverFont = new Font("SansSerif", Font.BOLD, 36);
			
        	public void paint(Graphics g)
        	{
        		if (image != null)
        		{
        			// now draw the camera image
        			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        			
        			// draw "Shreds to find"
    				g.setColor(Color.RED);
    				g.setFont(tagFont);
    				g.drawString("Shreds to find", 10, 20);
    				for (int i=0; i < shredsToFind.length; i++)
    				{
    					if (shredsFound[i])
    						g.setColor(Color.GREEN.darker());
    					else
    						g.setColor(Color.RED);
    					g.drawString(shredsToFind[i], 30, 40 + (i*20));
    				}
    				
        			// draw tolerance field (rectangle)
        			g.setColor(Color.RED);
    				
    				int imgCenterX = GUITest.IMAGE_WIDTH / 2;
    				int imgCenterY = GUITest.IMAGE_HEIGHT / 2;
    				int tolerance = GUITest.TOLERANCE;
    				
    				g.drawPolygon(new int[] {imgCenterX-tolerance, imgCenterX+tolerance, imgCenterX+tolerance, imgCenterX-tolerance}, 
						      		  new int[] {imgCenterY-tolerance, imgCenterY-tolerance, imgCenterY+tolerance, imgCenterY+tolerance}, 4);
    				
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
        			}
        			
        			//Draw circles
        			if (circles != null)
						for (Circle c : circles) {
							g.setColor(Color.RED);
							g.drawRect((int) c.x * imgScale, (int) c.y * imgScale, 10, 10);
							g.setColor(Color.BLUE);
							g.drawOval((int) (c.x - c.r) * imgScale, (int) (c.y - c.r) * imgScale,
									(int) (2 * c.r) * imgScale, (int) (2 * c.r) * imgScale);
							g.drawString(c.toString(), (int) c.x * imgScale + 10, (int) c.y * imgScale + 10);
						}
        			
        			// draw "Congrats" if all tags have been detected
        			if (gameOver)
        			{
        				String str = "Congratulation !";
        				
        				g.setColor(Color.GREEN.darker());
        				g.setFont(gameOverFont);
        				
        				FontMetrics metrics = g.getFontMetrics(gameOverFont);
        				int hgt = metrics.getHeight();
        				int adv = metrics.stringWidth(str);
        				
        				g.drawString(str, (getWidth() / 2) - (adv / 2), (getHeight() / 2) - (hgt / 2) - 50); // draw text centered
        			}
        			
        			// draw the time
    				g.setColor(Color.RED);
    				g.setFont(timeFont);
    				g.drawString(gameTime, getWidth() - 50, 20);
        		}
        		else
        		{
        			// draw "Waiting for video"
        			g.setColor(Color.RED);
    				g.setFont(tagFont);
        			g.drawString("Waiting for Video ...", 10, 20);
        		}
        	}
        }; 
        
        // a click on the video shall toggle the camera (from vertical to horizontal and vice versa)
		videoPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) 
			{
				drone.toggleCamera();
			}
		});
        
        videoPanel.setSize(GUITest.IMAGE_WIDTH, GUITest.IMAGE_HEIGHT);
        videoPanel.setMinimumSize(new Dimension(GUITest.IMAGE_WIDTH, GUITest.IMAGE_HEIGHT));
        videoPanel.setPreferredSize(new Dimension(GUITest.IMAGE_WIDTH, GUITest.IMAGE_HEIGHT));
        videoPanel.setMaximumSize(new Dimension(GUITest.IMAGE_WIDTH, GUITest.IMAGE_HEIGHT));
        
        return videoPanel;
	}
	
	
	private long imageCount = 0;
	
	public void imageUpdated(BufferedImage newImage)
    {
		if ((++imageCount % 2) == 0)
			return;
		
    	image = newImage;
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				videoPanel.repaint();
			}
		});
    }
	
	public void onTag(Result result, float orientation)
	{
		if (result != null)
		{
			this.result = result;
			this.orientation = orientation + "°";
			
			// check if that's a tag (shred) which has not be seen before and mark it as 'found'
			for (int i=0; i < shredsToFind.length; i++)
			{
				if (shredsToFind[i].equals(result.getText()))
				{
					shredsToFind[i] = shredsToFind[i] + " - " + gameTime;
					shredsFound[i] = true;
				}
			}
			
			// now check if all shreds have been found and if so, set the gameOver flag
			boolean isGameOver = true;
			for (int i=0; i < shredsFound.length; i++)
			{
				if (shredsFound[i] == false)
					isGameOver = false;
			}
			
			if (isGameOver) // all shreds found ?
			{
				gameOver = true;
				stopGameTimeCounter();
			}
		}
	}
	
	private void startGameTimeCounter()
	{
		gameStartTimestamp = System.currentTimeMillis();
		
		TimerTask timerTask = new TimerTask() {

			public void run()
			{
				long time = System.currentTimeMillis() - gameStartTimestamp;
				
				int minutes = (int)(time / (60 * 1000));
				int seconds = (int)((time / 1000) % 60);
				gameTime = String.format("%d:%02d", minutes, seconds);
			}
		};
		
		timer.schedule(timerTask, 0, 1000);		
	}
	
	private void stopGameTimeCounter()
	{
		timer.cancel();
	}


	@Override
	public void circlesUpdated(Circle[] circle) {
		this.circles = circles;
	}


}
