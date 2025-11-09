package Render;

import Utility.*;
import File.*;
import SDFs.BlendedSDF;

import java.io.File;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Window extends javax.swing.JFrame {
       
    //Input constants
    private final int   LEFT_ARROW    = 37,
                        UP_ARROW      = 38,
                        RIGHT_ARROW   = 39,
                        DOWN_ARROW    = 40,
                        LEFT_CLICK    =  1,
                        MIDDLE_CLICK  =  2,
                        RIGHT_CLICK   =  3;

    public Window() {
        initComponents();
        core.mainLoop();
    }
    
    private void cameraMover(char input, boolean shift) {
        float   grain = Core.getCameraMoveGrain();          //Get the current camera grain (sensitivity)
        vec3[]  orien = core.scene.getCameraOrien();        //Get the three orientation vectors
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
        core.scene.moveCamera(move); //Call the core that'll move the camera
    }
    private void cameraRotater(int input) {
        float grain = Core.getCameraRotateGrain();
        switch (input) {
            case UP_ARROW:        core.scene.rotateCamera( 0.0f  , grain );    break;
            case DOWN_ARROW:      core.scene.rotateCamera( 0.0f  ,-grain );    break; 
            case RIGHT_ARROW:     core.scene.rotateCamera(-grain ,  0.0f );    break;  
            case LEFT_ARROW:      core.scene.rotateCamera( grain ,  0.0f );    break;  
        }
    }
    private void cameraZoomer(char input) {
        if (input == 'q') {
            core.scene.zoomCamera( 0.033f );
        } else {
            core.scene.zoomCamera(-0.033f );
        }
    }
    
    private String[] createOptionsPane(String title, String[] options, String[] defaults) {
        JPanel panel = new JPanel();    //Create the panel we will use
                
        int length = options.length;
        
        JTextField[] fields = new JTextField[length];
        
        for (int i = 0; length > i; i++) {
            fields[i] = new JTextField(defaults[i], 10);
            panel.add(new JLabel(options[i]));
            panel.add(fields[i]);
            panel.add(javax.swing.Box.createHorizontalStrut(10));
        }
        
        int result = JOptionPane.showConfirmDialog(
                null, 
                panel,
                title,
                JOptionPane.OK_CANCEL_OPTION
        );
                
        String[] inputs = new String[length];
        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; length > i; i++) {
                String input = fields[i].getText();
                inputs[i] = input;
            }
            return inputs;
        } else {
            return null;
        }
        
    }
    private String[] createOptionsPane(String title, String[] both) {
        int length = both.length;                       //Get the amount of settings (double the amount because the current values are stored too)
        
        String[] options  = new String[length / 2];     //Initialize the options  array
        String[] defaults = new String[length / 2];     //Initialize the defaults array
        
        for (int i = 0; length / 2 > i; i++)  options[i] = both[i];                 //Fill the options  array
        for (int i = 0; length / 2 > i; i++) defaults[i] = both[i + length / 2];    //Fill the defualts array
        
        return createOptionsPane(title, options, defaults);
    }
            
    private void rightClick(int x, int y, int w, int h) {
        HitInfo info = core.scene.marchRay(x, y, w, h);
        SDFs.SDF clickedObj = info.sdf;
        if (clickedObj == null) return; //If we didn't click an object just return
            
        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();

        JMenuItem delete = new JMenuItem("Delete");     //Define a menu item    
        delete.addActionListener(evt -> {               //Add an event on click
            deleteClicked(clickedObj, info.hit);
        });
        popup.add(delete);                              //Add the menu item to the popup menu
        
        JMenuItem edit = new JMenuItem("Edit Properties");
        edit.addActionListener(evt -> {
            editClicked(clickedObj, info.hit, true);
        });
        popup.add(edit);
        
        if (clickedObj instanceof SDFs.BlendedSDF) {
            JMenuItem blendedEdit = new JMenuItem("Edit Blended Properties");
            blendedEdit.addActionListener(evt -> {
                editClicked(clickedObj, info.hit, false);
            });
            popup.add(blendedEdit);
        }
        
        int nw = core.getWidth(),
            nh = core.getHeight(),
            nx = (int) Math.round((x + 0.5f) * (nw / (float) w)),
            ny = (int) Math.round((y + 0.5f) * (nh / (float) h));

        nx = Math.max(0, Math.min(nx, nw - 1));
        ny = Math.max(0, Math.min(ny, nh - 1));

        popup.show(core, nx, ny);
    }
    private void deleteClicked(SDFs.SDF obj, vec3 hit) {
        boolean blended = false;                        //This will keep track if our object is blended
        SDFs.SDF parent = null;                         //This will keep track of the parent incase it's blended
        
        if (obj instanceof SDFs.BlendedSDF) {           //If the object we clicked is a blended object
            parent = obj;                               //Save the parent
            obj = ((BlendedSDF) obj).getClosest(hit);   //Gets the child that we clicked in the blended group
            blended = true;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want " + obj.getType() + " to be deleted?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) return;  //If confirm wasn't pressed just return
        
        if (!blended) core.scene.removeSDF(obj);
        else ((BlendedSDF) parent).remove(obj);
        
    }
    private void editClicked(SDFs.SDF obj, vec3 hit, boolean isolateChild) {
        boolean blended = false;                        //This will keep track if our object is blended
        SDFs.SDF parent = null;                         //This will keep track of the parent incase it's blended
        
        //If the object we clicked is a blended object & we aren't skipping isolating the child
        if (obj instanceof SDFs.BlendedSDF) {
            parent = obj;                               //Save the parent
            obj = (isolateChild) ? ((BlendedSDF) obj).getClosest(hit) : obj;   //Gets the child that we clicked in the blended group
            blended = true;                             //Turn blended to true
        }
        
        //Get the new settings 
        String[] inputs = createOptionsPane("Enter New Settings: ", obj.getSettings());
        if (inputs == null) return;
        
        //Parse the settings into a new SDF (newObj)
        IntRef i = new IntRef(0);
        SDFs.SDF newObj = null;
        try { newObj = SDFs.SDFParser.getSDF(obj.getType(), inputs, i); }
        catch (Exception e) { ; }
        
        /*  
        If the object we pressed was blended, and we're trying to alter the 
            properties of it, and not the individual children of that BlendedSDF,
            just pass in the new settings to the BlendedSDF.
        Else we check if it's just an individual SDF we are trying to edit, if so
            we use the new SDF we had made with the new settings and replace the
            old one with it.
        Then finally, if it's neither of those, we can conclude it's a BlenedSDF
            where we are trying to alter one of it's children. We had previously 
            gotten its child and set it to obj, so we remove obj from the parent
            and add the newObj.
        */
        if (!isolateChild && blended) {
            ((BlendedSDF) parent).setK(Float.parseFloat(inputs[0]));
        } else if (!blended) { 
            core.scene.setSDF(obj, newObj);
        } else {        
            ((BlendedSDF) parent).remove(obj);
            ((BlendedSDF) parent).addChild(newObj);
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
        objectsMenu = new javax.swing.JMenu();
        renderMenu = new javax.swing.JMenu();
        cameraMenu = new javax.swing.JMenu();
        cameraPosition = new javax.swing.JMenuItem();
        cameraGrain = new javax.swing.JMenuItem();
        lightingMenu = new javax.swing.JMenu();
        sceneLighitng = new javax.swing.JMenuItem();
        ambientLighting = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        core.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                coreMouseClicked(evt);
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

        objectsMenu.setText("Objects");
        menuBar.add(objectsMenu);

        renderMenu.setText("Render");
        menuBar.add(renderMenu);

        cameraMenu.setText("Camera");

        cameraPosition.setText("Change Camera Position");
        cameraPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraPositionActionPerformed(evt);
            }
        });
        cameraMenu.add(cameraPosition);

        cameraGrain.setText("Change Camera Grain");
        cameraGrain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraGrainActionPerformed(evt);
            }
        });
        cameraMenu.add(cameraGrain);

        menuBar.add(cameraMenu);

        lightingMenu.setText("Lighting");

        sceneLighitng.setText("Change Scene Lighting");
        sceneLighitng.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sceneLighitngActionPerformed(evt);
            }
        });
        lightingMenu.add(sceneLighitng);

        ambientLighting.setText("Change Ambient Lighting");
        ambientLighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ambientLightingActionPerformed(evt);
            }
        });
        lightingMenu.add(ambientLighting);

        menuBar.add(lightingMenu);

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
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();  //New file chooser 
        
        File dir = FileManager.getCurrentDirectory();   //Get the current directory
        fileChooser.setCurrentDirectory(dir);           //Set the file choosers dir to the current dir
        
        int result = fileChooser.showOpenDialog(this);  //Display the chooser to the user   (OpenDialog)

        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {    //If the user ends up selecting a file
            File selectedFile = fileChooser
                                .getSelectedFile();     //Get the file the user picked
            FileManager.loadScene(selectedFile);
        }
    }//GEN-LAST:event_openSceneActionPerformed

    private void exportSceneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSceneActionPerformed
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();  //New file chooser 
        
        File dir = FileManager.getCurrentDirectory();   //Get the current directory
        fileChooser.setCurrentDirectory(dir);           //Set the file choosers dir to the current dir
        
        int result = fileChooser.showSaveDialog(this);  //Display the chooser to the user   (SaveDialog)

        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {    //If the user ends up selecting a file 
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
        String[] inputs = createOptionsPane("Enter new grains: ", options, defaults);
        
        if (inputs == null) return;
        
        Core.setCameraMoveGrain(Float.parseFloat(inputs[0]));
        Core.setCameraRotateGrain(Float.parseFloat(inputs[1]));        
        
    }//GEN-LAST:event_cameraGrainActionPerformed

    private void sceneLighitngActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sceneLighitngActionPerformed
        String[] options    = new String[] { "Scene Light Direction: " };
        String[] defaultDir = new String[] { core.scene.getSceneLighting().toStringParen() };
        String[] inputs     = createOptionsPane("Enter new Lighting Direction", options, defaultDir);
        
        if (inputs == null) return; 
        
        core.scene.setSceneLighting(new vec3(inputs[0]));
    }//GEN-LAST:event_sceneLighitngActionPerformed

    private void ambientLightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ambientLightingActionPerformed
        String[] options = new String[] { "New Level: " };
        String[] current = new String[] { ""+core.scene.getAmbientLighting() };
        String[] input  = createOptionsPane("Enter New Ambient Lighting Level", options, current);
        
        if (input == null) return;
        
        float f = Float.parseFloat(input[0]);   //Parse the new float given by the user
        f = (0 > f) ? 0 : f;
        f = (f > 1) ? 1 : f;    //Clamp to [0, 1]
        
        core.scene.setAmbientLighting(f);
    }//GEN-LAST:event_ambientLightingActionPerformed

    private void cameraPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraPositionActionPerformed
        String[] options    = new String[] { "New Camera Position: " };
        String[] defaultDir = new String[] { core.scene.getCameraPos().toStringParen() };
        String[] inputs     = createOptionsPane("Enter new Camera Position", options, defaultDir);
        
        if (inputs == null) return; 
        
        vec3 input = new vec3(inputs[0]);                   //Get where the user wants the camera
        vec3 currentPos = core.scene.getCameraPos();        //Current camera position
        core.scene.moveCamera(input.subtract(currentPos));  //Move the camera to the wanted position
    }//GEN-LAST:event_cameraPositionActionPerformed

    private void coreMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_coreMouseClicked
        int button  = evt.getButton(),
                x   = evt.getX(),
                y   = evt.getY(),
                w   = core.getWidth(),
                h   = core.getHeight();
        switch (button) {
            case LEFT_CLICK:
                
                break;
            case RIGHT_CLICK:   rightClick(x, y, w, h);   break;
            case MIDDLE_CLICK:
                
                break;
        }
    }//GEN-LAST:event_coreMouseClicked

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
    private javax.swing.JMenuItem ambientLighting;
    private javax.swing.JMenuItem cameraGrain;
    private javax.swing.JMenu cameraMenu;
    private javax.swing.JMenuItem cameraPosition;
    private Render.Core core;
    private javax.swing.JMenuItem exportScene;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu lightingMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu objectsMenu;
    private javax.swing.JMenuItem openScene;
    private javax.swing.JMenu renderMenu;
    private javax.swing.JMenuItem sceneLighitng;
    // End of variables declaration//GEN-END:variables
}
