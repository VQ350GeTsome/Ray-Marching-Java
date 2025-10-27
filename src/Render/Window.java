package Render;

import File.*;
import Utility.*;

import java.io.File;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Window extends javax.swing.JFrame {
    
    private final int   LEFT    = 37,
                        UP      = 38,
                        RIGHT   = 39,
                        DOWN    = 40;

    public Window() {
        initComponents();
        core.mainLoop();
    }
    
    private void cameraMover(char input, boolean shift) {
        float   grain = Core.getCameraMoveGrain();          //Get the current camera grain (sensitivity)
        vec3[]  orien = core.getSceneCameraOrien();         //Get the three orientation vectors
        vec3    forward = orien[0],
                right   = orien[1],
                up      = orien[2],
                move    = new vec3();                       //Initalize what will be the move vector
       
        switch (input) { 
            case 'w':   move = (shift) ? up.multiply(  grain ) : forward.multiply(  grain );    break;  //Move forward ... unless shift is held, then move up
            case 's':   move = (shift) ? up.multiply( -grain ) : forward.multiply( -grain );    break;  //Move down ... unless shift is held, then move down
            case 'a':   move = right.multiply( -grain );    break;  //Move left
            case 'd':   move = right.multiply(  grain );    break;  //Move right
        }
        core.moveSceneCamera(move); //Call the core that'll move the camera
    }
    private void cameraRotater(int input) {
        float grain = Core.getCameraRotateGrain();
        switch (input) {
            case UP:        core.rotateSceneCamera( 0.0f  , grain );    break;
            case DOWN:      core.rotateSceneCamera( 0.0f  ,-grain );    break; 
            case RIGHT:     core.rotateSceneCamera(-grain ,  0.0f );    break;  
            case LEFT:      core.rotateSceneCamera( grain ,  0.0f );    break;  
        }
    }
    private void cameraZoomer(char input) {
        if (input == 'q') {
            core.zoomSceneCamera( 0.033f );
        } else {
            core.zoomSceneCamera(-0.033f );
        }
    }
    
    private String[] createOptionsPane(String[] options, String[] defaults) {
        JPanel panel = new JPanel();    //Create the panel we will use
        
        int length = options.length;
        
        JTextField[] fields = new JTextField[length];
        
        for (int i = 0; length > i; i++) {
            fields[i] = new JTextField(defaults[i], 10);
            panel.add(new JLabel(options[i]));
            panel.add(fields[i]);
            panel.add(Box.createHorizontalStrut(10));
        }
        
        int result = JOptionPane.showConfirmDialog(null, panel);
        
        String[] inputs = new String[length];
        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; length > i; i++) {
                String input = fields[i].getText();
                inputs[i] = input;
            }
        }
        return inputs;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        core = new Render.Core();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openScene = new javax.swing.JMenuItem();
        exportScene = new javax.swing.JMenuItem();
        sceneMenu = new javax.swing.JMenu();
        cameraMenu = new javax.swing.JMenu();
        cameraGrain = new javax.swing.JMenuItem();
        renderMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout coreLayout = new javax.swing.GroupLayout(core);
        core.setLayout(coreLayout);
        coreLayout.setHorizontalGroup(
            coreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        coreLayout.setVerticalGroup(
            coreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );

        menuBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        fileMenu.setText("File");

        openScene.setText("Open Scene");
        openScene.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSceneActionPerformed(evt);
            }
        });
        fileMenu.add(openScene);

        exportScene.setText("Export / Save Scene");
        exportScene.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportSceneActionPerformed(evt);
            }
        });
        fileMenu.add(exportScene);

        menuBar.add(fileMenu);

        sceneMenu.setText("Scene");
        menuBar.add(sceneMenu);

        cameraMenu.setText("Camera");

        cameraGrain.setText("Change Camera Grain");
        cameraGrain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraGrainActionPerformed(evt);
            }
        });
        cameraMenu.add(cameraGrain);

        menuBar.add(cameraMenu);

        renderMenu.setText("Render");
        menuBar.add(renderMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(core, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(core, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        char inputC = Character.toLowerCase(evt.getKeyChar());      //Char key input
        int     inputI = (int)evt.getKeyCode();                     //Keycode input
        
        if (inputC == 'w' || inputC == 'a' || inputC == 's' || inputC == 'd') { //If input relates to movement keys
            cameraMover(inputC, evt.isShiftDown());
        }
        //note ... left = 37, up = 38, right = 39, down = 40 
        else if (inputI == 37 || inputI == 38 || inputI == 39 || inputI == 40) {
            cameraRotater(inputI);
        }        
        else if (inputC == 'q' || inputC == 'e') {      //Call cameraZoomer if keypress relates to zoom buttons
            cameraZoomer(inputC);
        }
        
    }//GEN-LAST:event_formKeyPressed

    private void openSceneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSceneActionPerformed
        JFileChooser fileChooser = new JFileChooser();  //New file chooser 
        
        File dir = FileManager.getCurrentDirectory();   //Get the current directory
        fileChooser.setCurrentDirectory(dir);           //Set the file choosers dir to the current dir
        
        int result = fileChooser.showOpenDialog(this);  //Display the chooser to the user   (OpenDialog)

        if (result == JFileChooser.APPROVE_OPTION) {    //If the user ends up selecting a file
            File selectedFile = fileChooser
                                .getSelectedFile();     //Get the file the user picked
            FileManager.loadScene(selectedFile);
        }
    }//GEN-LAST:event_openSceneActionPerformed

    private void exportSceneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSceneActionPerformed
        JFileChooser fileChooser = new JFileChooser();  //New file chooser 
        
        File dir = FileManager.getCurrentDirectory();   //Get the current directory
        fileChooser.setCurrentDirectory(dir);           //Set the file choosers dir to the current dir
        
        int result = fileChooser.showSaveDialog(this);  //Display the chooser to the user   (SaveDialog)

        if (result == JFileChooser.APPROVE_OPTION) {    //If the user ends up selecting a file 
            File selectedFile = fileChooser
                                .getSelectedFile();     //Get the file the user picked
            FileManager.saveScene(selectedFile);
        }
    }//GEN-LAST:event_exportSceneActionPerformed

    private void cameraGrainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraGrainActionPerformed
        String[] options    = new String[] { "Movement Grain: " , "Pan Grain: " };
        String defaultMove  = String.valueOf(Core.getCameraMoveGrain());
        String defaultPan   = String.valueOf(Core.getCameraRotateGrain());
        String[] defaults   = new String[] { defaultMove, defaultPan };
        String[] inputs = createOptionsPane(options, defaults);
        
        Core.setCameraMoveGrain(Float.parseFloat(inputs[0]));
        Core.setCameraRotateGrain(Float.parseFloat(inputs[1]));        
        
    }//GEN-LAST:event_cameraGrainActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
	  for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
	      if ("Nimbus".equals(info.getName())) {
		javax.swing.UIManager.setLookAndFeel(info.getClassName());
		break;
	      }
	  }
        } catch (ClassNotFoundException ex) {
	  java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
	  java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
	  java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
	  java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
	  public void run() {
	      new Window().setVisible(true);
	  }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem cameraGrain;
    private javax.swing.JMenu cameraMenu;
    private Render.Core core;
    private javax.swing.JMenuItem exportScene;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openScene;
    private javax.swing.JMenu renderMenu;
    private javax.swing.JMenu sceneMenu;
    // End of variables declaration//GEN-END:variables
}
