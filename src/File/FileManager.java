package File;

import Render.Scene;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
        try (FileWriter writer = new FileWriter(location)) {
            writer.write(packedScene);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadScene(File newScene) {
        StringBuilder pack = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(newScene))) {
            String line;
            while ((line = reader.readLine()) != null) {
                pack.append(line).append("\n"); // Preserve line breaks if needed
            }
            scene.unpackageScene(pack.toString().trim());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load scene from: " + newScene.getAbsolutePath());
        }
    }   
    
}
