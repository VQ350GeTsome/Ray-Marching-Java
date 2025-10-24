package File;

import Render.Scene;
import java.io.File;

public class FileManager {
       
    /**
     * Gets the current directory where the program is stored
     * and returns it as a File
     * @return The File where this program is stored
     */
    public static File getCurrentDirectory(){
        String directory = System.getProperty("user.dir");
        int i = directory.length();
        String slashCheck = "";
        int slashIndex = 0;
        while (i > 0){
            slashCheck = directory.substring(i - 1, i);
            if (slashCheck.equals("\\")){
               slashIndex = i;
               i = 0;
                String finalDirectory = directory.substring(0, slashIndex);
                
            }
            i--;
        } 
        return new File(directory);
    }
    
    private static Scene scene;
    public static void setScene(Scene s) { scene = s; }
    
    public static void saveScene(File location) {
        String packedScene = scene.packageScene();
    }
    
    
}
