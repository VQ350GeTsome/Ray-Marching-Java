package Render;

import File.FileManager;
import Utility.vec3;
import java.io.File;
import javax.swing.JFileChooser;

public class Window extends javax.swing.JFrame {

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
        //note ... left = 37, up = 38, right = 39, down = 40 
        float grain = Core.getCameraRotateGrain();
        switch (input) {
            case 37:    core.rotateSceneCamera( 0.0f , grain );    break;   //Rotate left
            case 39:    core.rotateSceneCamera( 0.0f ,-grain );    break;   //Rotate right
            case 38:    core.rotateSceneCamera(-grain , 0.0f );    break;   //Rotate down
            case 40:    core.rotateSceneCamera( grain , 0.0f );    break;   //Rotate up
        }
    }
    private void cameraZoomer(char input) {
        if (input == 'q') {
            core.zoomSceneCamera( 0.033f );
        } else {
            core.zoomSceneCamera(-0.033f );
        }
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
        openScene.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openSceneMouseClicked(evt);
            }
        });
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

    private void openSceneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openSceneMouseClicked

    }//GEN-LAST:event_openSceneMouseClicked

    private void openSceneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSceneActionPerformed
        JFileChooser fileChooser = new JFileChooser();  //New file chooser 
        
        File dir = FileManager.getCurrentDirectory();   //Get the current directory
        fileChooser.setCurrentDirectory(dir);           //Set the file choosers dir to the current dir
        
        int result = fileChooser.showOpenDialog(this);  //Display the chooser to the user   (OpenDialog)

        if (result == JFileChooser.APPROVE_OPTION) {    //If the user ends up selecting a file
            File selectedFile = fileChooser
                                .getSelectedFile();     //Get the file the user picked
            
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
