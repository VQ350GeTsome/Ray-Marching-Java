package raymarching;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import objects.*;
import utilities.*;

public class RayMarching extends JPanel {
    
    private static int width = 750, height = 750; //Window arguments
    private static BufferedImage image; private static JFrame frame; //Variables for the window        
    
    public static void main(String[] args) { 
        openWindow();                                                //Opens a window with a JFrame (frame) and initilizes it. Then attaches the BufferedImage (image) to it.
        
        ObjectRM[] objs = getObjects();                              //Initilizes objects         --- currently hardcoded
        Vector3 light = new Vector3(.3, .6, -.3).normalize();        //Initilizes light           --- currently hardcoded
        Vector3[] cameraSettings = getCameraSettings();              //Initilizes camera settings --- currently hardcoded
        double[] settings = getSettings();
        double fovDegrees = 120, fovRadians = Math.PI * (fovDegrees / 180), scale = Math.tan(fovRadians / 2);                         //Calculates scale based on the fov in degrees
        
        marchScreen(objs, light, cameraSettings, settings, scale);   //Passes objects, light, and camera settings into the raymarcher
        frame.repaint();                                             //Repaints, showing user the end result
        
        
        System.out.println("Done");
    }
    
    private static void marchScreen(ObjectRM[] objs, Vector3 light, Vector3[] cameraSettings, double[] settings, double scale){
        Vector3 rayPos = new Vector3();
        
        // == Main Loop == //Tries to pack as much information into already declared variables / arrays / objects.
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++) if(!marchRay(x, y, objs, light, cameraSettings, settings, scale, rayPos)) image.setRGB(x, y, Color.GRAY.getRGB()); //image.setRGB(x, y, new Color(180 * x / width, 180 * y / height, 160).getRGB());
        
    }
    
    private static boolean marchRay(int x, int y, ObjectRM[] objs, Vector3 light, Vector3[] cameraInfo, double[] settings, double scale, Vector3 rayPos){
        
        double px = (2.0 * x / width - 1.0) * ((double)width / height) * scale, py = (1.0 - 2.0 * y / height) * scale;                  //Calculates px & py, which is the adjusted x, and y values based on the fov
        rayPos.setXYZ(px, py, 0);                                     //Resets current rays postions (rayPos) to (px, py, 0)
        Vector3 rayDir = cameraInfo[1]
	      .add(cameraInfo[2].multiply(px))
	      .add(cameraInfo[3].multiply(py))
	      .normalize();                                         //Gets the current rays direction (rayDir)... might be hard to follow. See: getCameraSettings()
        
        double totalDistance = 0.0;
        
        while(settings[0] > totalDistance) {                          //While max distance is greater than total distance traversed
	  
	  double closestD = settings[0], closest2D = settings[0], closestBD = settings[0], objD; 
	  
	  ObjectRM closestObj = null;                               //Clears cloest object
	   
	  for(ObjectRM obj : objs){                                 //Now we are looping through all objects handed to us
	       
	      objD = obj.sdf(rayPos);                               //Keeps track of the current objects distance
	      
	      if (closestD > objD){                                 
		closestObj = obj;  
		closestD = objD;                                  //If the current object is the closest so far, store it and the distance.
	      } 
	      if (closest2D > objD){                           //If the current object is not the closest, but the second closest:
		closest2D = objD;	
                }
	      double h = Math.max(settings[1] - Math.abs(closestBD - objD), 0.0) / settings[1];  //Calculates h. h is the blending amount (settings[1]) minus the distance between the current object and the closest object, all divided by blending amount
                double blendedDist = Math.min(closestBD, objD) - h * h * settings[1] * 0.25;       //Calculates the blended distance. It is the minimum of either the closest objects distance (dists[0]) or the current objects distance minus h^2 times 1/4 of the blending amount
	      if (closestBD > blendedDist){                         //If the blended distance is closer than the closest distance:                        
		closestBD = blendedDist;                          //Stores the previous closest to second closest, stores the blended distance accordingly
	      }
	  } 

	  if (settings[2] > closestBD && closestObj != null){
	      
	      double r = 0, g = 0, b = 0, sharpness = settings[3], totalWeight = 0.0;
	      
	      for (ObjectRM obj : objs) {
		double d = obj.sdf(rayPos);
		double w = Math.exp(-sharpness * d);
		Color c = obj.getColor();

		r += c.getRed()   * w;
		g += c.getGreen() * w;
		b += c.getBlue()  * w;
		totalWeight += w;
	      }

	      r /= totalWeight;
	      g /= totalWeight;
	      b /= totalWeight;

	      double shadowAmount = computeShadow(objs, rayPos, light);
	      r *= shadowAmount;
	      g *= shadowAmount;
	      b *= shadowAmount;

	      image.setRGB(x, y, new Color((int)r, (int)g, (int)b).getRGB());
	      return true;
	  }
	  rayPos = rayPos.add(rayDir.multiply(closestBD));
	  totalDistance += closestBD;  
        }
        return false;
    }
    
    // === Settings | Options === \\
    
    //Fills static array of size 4 with camera settings. 0 = camera positon, 1 = forward, 2 = right, 3 = up vector
    private static Vector3[] getCameraSettings(){
        Vector3 camPos = new Vector3(0, 0, -100);
        Vector3 target = new Vector3(0, 0, 1);
        Vector3 up     = new Vector3(0, 1, 0);
        
        Vector3 forward = target.subtract(camPos).normalize();
        Vector3 right   = forward.cross(up).normalize();
        Vector3 upVec   = right.cross(forward).normalize();
        
        return new Vector3[] { camPos, forward, right, upVec };
    }
    private static ObjectRM[] getObjects(){
        ObjectRM sphere1 = new Sphere(new Vector3( 000, -020,  200), 25, Color.RED);
        ObjectRM cube1   = new Cube(  new Vector3( 050,  100,  200), 25, Color.GREEN);
        ObjectRM torus1  = new Torus( new Vector3(-100, -025,  200), 10, 5, Color.PINK);
        ObjectRM sphere2 = new Sphere(new Vector3(-100,  070,  200), 15, Color.BLUE); 
        
        return new ObjectRM[] { cube1, sphere1, sphere2, torus1 };
    }
    //Fills static array of size 4 with scene settings. 0 = max render distance, 1 = blending amount, 2 = tolerance, 3 = color blending sharpness
    private static double[] getSettings(){
        double maxDist = 250, blendness = 125, tolerance = 0.005, sharpness = 1.75 / 100.0;
        return new double[] { maxDist, blendness, tolerance, sharpness };
    }
    
    // === Helper methods === \\
    
    //Returns a shadowAmount that is [0, 1]
    private static double computeShadow(ObjectRM[] objects, Vector3 origin, Vector3 light){
        double shadowAmount = 1.0;
        double t = 0.01;
        double maxDist = 500;
        
        double k = 1.5;
        
        for (int i = 0; i < 64 && maxDist > t; i++) {           //Iterate 64 times or until maxDist > t
	  
	  Vector3 p = origin.add(light.multiply(t));          //Move in the direction of light by the safe distance (t)
	  
	  double minDist = getSDF(objects, p);                //Get closest objects SDF

	  shadowAmount = Math.min(shadowAmount, k * minDist / t);
	  
	  if (minDist < 0.0000001) {                          //If the closest object is within tolerance then the object we bounced off of is in shadow
	      shadowAmount = 0.0;
	      break;
	  }

	  t += minDist;                                       //Adds the safe distance to t
        }
        
        return shadowAmount;
        
    }
    //Returns distance to closest object given a vector
    private static double getSDF(ObjectRM[] objects, Vector3 p) {
        double minDist = Double.MAX_VALUE;
        for (ObjectRM obj : objects) {
	  minDist = Math.min(minDist, obj.sdf(p));
        }
        return minDist;
    }
    
    // === Image initilizers === \\
    
    //Defines image & frame, initilized with width & height.
    private static void openWindow(){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        frame = new JFrame("RayMarch");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.add(new RayMarching());
        frame.setVisible(true);
    }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, width, height, null);
    }
    
}
