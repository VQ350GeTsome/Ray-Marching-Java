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
    private final int   SPACE_BAR     =  32, LEFT_ARROW    =  37, UP_ARROW      =  38, RIGHT_ARROW   =  39,
                        DOWN_ARROW    =  40, LEFT_CLICK    =   1, MIDDLE_CLICK  =   2, RIGHT_CLICK   =   3,
                        W_KEY         =  87, A_KEY         =  65, S_KEY         =  83, D_KEY         =  68,
                        Q_KEY         =  81, E_KEY         =  69, R_KEY         =  82, F1_KEY        = 112,
                        M_WHEEL_UP    = - 1, M_WHEEL_DOWN  =   1;

    public Window() {
        initComponents();
        core.mainLoop();
    }
    
    private void cameraMover(int input, boolean shift) {
        float   grain = Core.cameraMoveGrain;               //Get the current camera grain (sensitivity)
        vec3[]  orien = core.scene.getCameraOrien();        //Get the three orientation vectors
        vec3    forward = orien[0],
                right   = orien[1],
                up      = orien[2],
                move    = new vec3();                       //Initalize what will be the move vector
       
        switch (input) { 
            case W_KEY:   move = (shift) ? up.scale(  grain ) : forward.scale(  grain );    break;  //Move forward ... unless shift is held, then move up
            case S_KEY:   move = (shift) ? up.scale( -grain ) : forward.scale( -grain );    break;  //Move down ... unless shift is held, then move down
            case A_KEY:   move = right.scale( -grain );    break;  //Move left
            case D_KEY:   move = right.scale(  grain );    break;  //Move right
        }
        core.scene.moveCamera(move); //Call the core that'll move the camera
    }
    private void cameraRotater(int input) {
        float grain = Core.cameraRotateGrain;
        switch (input) {
            case UP_ARROW:        core.scene.rotateCamera( 0.0f  , grain );    break;
            case DOWN_ARROW:      core.scene.rotateCamera( 0.0f  ,-grain );    break; 
            case RIGHT_ARROW:     core.scene.rotateCamera(-grain ,  0.0f );    break;  
            case LEFT_ARROW:      core.scene.rotateCamera( grain ,  0.0f );    break;  
        }
    }
    private void cameraRotater(int dx, int dy) {
        float cameraDYaw = dx * 0.1f;   // sensitivity factor
        float cameraDPitch = dy * 0.1f;
        core.scene.rotateCamera( -cameraDYaw,  -cameraDPitch );
    }
    private void cameraZoomer(int input) {
        float zoom = 0.0f;
        switch (input) {
            case M_WHEEL_DOWN:
            case Q_KEY:
                zoom = 0.033f;
                break;
            case M_WHEEL_UP:
            case E_KEY:
                zoom = -0.033f;
                break;
        }
        core.scene.zoomCamera(zoom);
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
    
    private int createButtonsPane(String prompt, String[] options) {
        int choice = JOptionPane.showOptionDialog(
            null,
            prompt,
            "Selector...",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
        if (choice >= 0) return choice;
        else return -1;
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
        
        JMenuItem color = new JMenuItem("Change Color");
        color.addActionListener(evt -> {
            colorClicked(clickedObj, info.hit);
        });
        popup.add(color);
        
        JMenuItem matEdit = new JMenuItem("Edit Material");
        matEdit.addActionListener(evt -> {
            matEditClicked(clickedObj, info.hit);
        });
        popup.add(matEdit);
        
        JMenuItem edit = new JMenuItem("Edit Properties");
        edit.addActionListener(evt -> {
            regEditClicked(clickedObj, info.hit, true);
        });
        popup.add(edit);
        
        if (clickedObj instanceof SDFs.BlendedSDF) {
            JMenuItem blendedEdit = new JMenuItem("Edit Blended Properties");
            blendedEdit.addActionListener(evt -> {
                regEditClicked(clickedObj, info.hit, false);
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
            "Are you sure you want " + ((obj.getName() == null) ? obj.getType() : obj.getName()) + " to be deleted?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) return;  //If confirm wasn't pressed just return
        
        if (!blended) core.scene.removeSDF(obj);
        else ((BlendedSDF) parent).remove(obj);
        
    }
    private void colorClicked(SDFs.SDF obj, vec3 hit) {
        SDFs.SDF parent = null;
        
        if (obj instanceof SDFs.BlendedSDF) {
            parent = obj;
            obj = ((SDFs.BlendedSDF) obj).getClosest(hit);
        }
    }
    private void matEditClicked(SDFs.SDF obj, vec3 hit) {
        
    }
    private void regEditClicked(SDFs.SDF obj, vec3 hit, boolean isolateChild) {
        boolean blended = false;                        //This will keep track if our object is blended
        SDFs.SDF parent = null;                         //This will keep track of the parent incase it's blended
        
        //If the object we clicked is a blended object & we aren't skipping isolating the child
        if (obj instanceof SDFs.BlendedSDF) {
            parent = obj;                               //Save the parent
            obj = (isolateChild) ? ((SDFs.BlendedSDF) obj).getClosest(hit) : obj;   //Gets the child that we clicked in the blended group
            blended = true;                             //Turn blended to true
        }
        
        //Get the new settings 
        String[] inputs = createOptionsPane("Enter New Settings for " + 
                 ((obj.getName() == null) ? obj.getType() : obj.getName()) 
                 + ": ", obj.getSettingsAndCurrent());
        if (inputs == null) return;

       
        
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
            try { 
                float k = Float.parseFloat(inputs[0]);
                ((BlendedSDF) parent).setK((k <= 0) ? Core.getEps() : k);
            } catch (Exception e) { System.err.println(e.getMessage()); }
            
        } else if (!blended)
            obj.parseNewParams(inputs);
        else     
            ((SDFs.BlendedSDF) parent).editChild(inputs, obj);
    }
    
    private void addSDF(int type) {
        final int   SPHERE = 0, CUBE = 1, TORUS = 2,
                    PLANE  = 3;
        
        //This will be applicable for primitives & repeating
        String[] choices = SDFs.SDFParser.getPrimitives();  
        int choice = createButtonsPane("Choose an SDF...", choices);
        
        java.util.ArrayList<String> placeHolder = new java.util.ArrayList<>(3);
        
        //We can fill the place holder with a color, as it always come first
        int r = (int) (Math.random() * 255), g = (int) (Math.random() * 255), b = (int) (Math.random() * 255);
        placeHolder.add(r + ":" + g + ":" + b);     
        
        //Then we can fill it with a random float for the shinyness
        float shiny = ((int) ((Math.random() * 1000))) / 1000.0f;
        placeHolder.add(""+shiny);
        
        //Gets the cameras orientation and uses the forward & positon vectors to get a 
        //vector n units infront of the camera
        int n = 25;
        vec3[] camOrien = core.scene.getCameraOrien();  
        vec3 forward    = camOrien[0];
        vec3 pos        = camOrien[3];
        placeHolder.add(vec3.round(pos.add(forward.scale(n))).toStringParen());

        //Now we fill the placeholder with more values and prompt the user for any changes
        String t = "";
        switch (choice) {
            case SPHERE:
                placeHolder.add("1.0"); //Radius
                t = "sphere";
                break;
            case CUBE:
                placeHolder.add("1.0"); //Size
                t = "cube";
                break;
            case TORUS:
                placeHolder.add("1.0"); //Major Radius
                placeHolder.add("0.5"); //Minor Radius
                t = "torus";
                break;
            case PLANE:
                placeHolder.add("(0.0, 0.0, 1.0)"); //Normal vector
                t = "plane";
                break;        
        }
        if (type == 1) t = "repeat" + t;
        String[] inputs = createOptionsPane("New " + t + "...", SDFs.SDFParser.getSettings(t, choice == 1), placeHolder.toArray(String[]::new));
        if (inputs == null) return;
        safeAddSDF(t, inputs); 
    }
    //Helper method to just parse an SDF safely using a try catch
    private void safeAddSDF(String type, String[] inputs) {
        try { core.scene.addSDF(SDFs.SDFParser.getSDF(type, inputs)); }
        catch (Exception e) { System.err.print(e.getMessage()); }
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
        addNewObj = new javax.swing.JMenuItem();
        renderMenu = new javax.swing.JMenu();
        changeRender = new javax.swing.JMenuItem();
        changeFPS = new javax.swing.JMenuItem();
        cameraMenu = new javax.swing.JMenu();
        cameraPosition = new javax.swing.JMenuItem();
        cameraGrain = new javax.swing.JMenuItem();
        lightingMenu = new javax.swing.JMenu();
        sceneLighitng = new javax.swing.JMenuItem();
        ambientLighting = new javax.swing.JMenuItem();
        backgroundColor = new javax.swing.JMenuItem();
        bloomToggle = new javax.swing.JCheckBoxMenuItem();
        bloomTypeToggle = new javax.swing.JCheckBoxMenuItem();
        bloomSettings = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        core.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                coreMouseDragged(evt);
            }
        });
        core.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                coreMouseWheelMoved(evt);
            }
        });
        core.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                coreMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                coreMousePressed(evt);
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

        addNewObj.setText("Add New Object");
        addNewObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewObjActionPerformed(evt);
            }
        });
        objectsMenu.add(addNewObj);

        menuBar.add(objectsMenu);

        renderMenu.setText("Render");

        changeRender.setText("Change Render Settings");
        changeRender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeRenderActionPerformed(evt);
            }
        });
        renderMenu.add(changeRender);

        changeFPS.setText("Change FPS Cap");
        changeFPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeFPSActionPerformed(evt);
            }
        });
        renderMenu.add(changeFPS);

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

        backgroundColor.setText("Change Background Colors");
        lightingMenu.add(backgroundColor);

        bloomToggle.setSelected(true);
        bloomToggle.setText("Toggle Bloom");
        bloomToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bloomToggleActionPerformed(evt);
            }
        });
        lightingMenu.add(bloomToggle);

        bloomTypeToggle.setSelected(true);
        bloomTypeToggle.setText("Toggle Gaussian Bloom");
        bloomTypeToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bloomTypeToggleActionPerformed(evt);
            }
        });
        lightingMenu.add(bloomTypeToggle);

        bloomSettings.setText("Bloom Settings");
        bloomSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bloomSettingsActionPerformed(evt);
            }
        });
        lightingMenu.add(bloomSettings);

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
        int  input = (int)evt.getKeyCode();                        //Keycode input

        switch (input) {
            case W_KEY:
            case A_KEY:
            case S_KEY:
            case D_KEY:
                cameraMover(input, evt.isShiftDown());
                break;
            case RIGHT_ARROW:
            case LEFT_ARROW:
            case UP_ARROW:
            case DOWN_ARROW:
                cameraRotater(input);
                break;
            case Q_KEY:
            case E_KEY:
                cameraZoomer(input);
                break;
            case SPACE_BAR: 
                core.startStopTimer();
                break;
            case R_KEY:
                core.refresh();
                break;
            case F1_KEY:
                core.screenShot();
                break;
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
        String defaultMove  = String.valueOf(Core.cameraMoveGrain);
        String defaultPan   = String.valueOf(Core.cameraRotateGrain);
        String[] defaults   = new String[] { defaultMove, defaultPan };
        String[] inputs = createOptionsPane("Enter new grains: ", options, defaults);
        
        if (inputs == null) return;
        
        try {
            Core.cameraMoveGrain = Float.parseFloat(inputs[0]);
            Core.cameraRotateGrain = Float.parseFloat(inputs[1]);   
        } catch (Exception e) { System.err.println("Error in parsing new grains: " + e.getMessage()); }
        
    }//GEN-LAST:event_cameraGrainActionPerformed

    private void sceneLighitngActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sceneLighitngActionPerformed
        String[] options    = new String[] { "Scene Light Direction: " };
        String[] defaultDir = new String[] { core.scene.getSceneLighting().toStringParen() };
        String[] inputs     = createOptionsPane("Enter new Lighting Direction", options, defaultDir);
        
        if (inputs == null) return; 
        
        try { core.scene.setSceneLighting(new vec3(inputs[0])); }
        catch (Exception e) { System.err.println(e.getMessage()); }
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
        String[] defaultDir = new String[] { core.scene.getCameraPos().round(1).toStringParen() };
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

    private void addNewObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewObjActionPerformed
        final int PRIMITIVE = 0,
                  REPEATING = 1;
        
        String[] options = SDFs.SDFParser.getTypes();                       //Get the currently implemented SDFs
        int typeChoice = createButtonsPane("Choose a type...", options);    //Prompt the user with which type they want
        if (typeChoice == -1) return;   //The user chose nothing
        
        addSDF(typeChoice);
    }//GEN-LAST:event_addNewObjActionPerformed

    private void bloomToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bloomToggleActionPerformed
        boolean checked = bloomToggle.getState();
        core.bloom = checked;
        if (!checked) bloomTypeToggle.setState(false);
    }//GEN-LAST:event_bloomToggleActionPerformed

    private void bloomTypeToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bloomTypeToggleActionPerformed
        
    }//GEN-LAST:event_bloomTypeToggleActionPerformed

    private void bloomSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bloomSettingsActionPerformed
        
    }//GEN-LAST:event_bloomSettingsActionPerformed

    private void changeRenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeRenderActionPerformed
        
    }//GEN-LAST:event_changeRenderActionPerformed

    private void changeFPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeFPSActionPerformed
        String[] options = new String[] { "Enter new FPS Target: " };
        String[] current = new String[] { ""+core.getWait() };
        
        String input = createOptionsPane("New FPS Cap...", options, current)[0];
        
        try { core.setWait(Integer.parseInt(input)); }
        catch (Exception e) { ; }
    }//GEN-LAST:event_changeFPSActionPerformed

    private void coreMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_coreMouseWheelMoved
        //Pass in the direction of the mouse wheel rotation
        cameraZoomer(evt.getWheelRotation());
    }//GEN-LAST:event_coreMouseWheelMoved

    private int mouseLastX=0, mouseLastY=0;   //Initialize mouse last position
    private void coreMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_coreMouseDragged
        int dx = evt.getX() - mouseLastX,
            dy = evt.getY() - mouseLastY; //Get the delta x & y.
        
        cameraRotater(dx, dy);
        
        mouseLastX = evt.getX(); mouseLastY = evt.getY();
    }//GEN-LAST:event_coreMouseDragged

    private void coreMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_coreMousePressed
        //Update the mouse starting positon x & y.
        mouseLastX = evt.getX(); mouseLastY = evt.getY();
    }//GEN-LAST:event_coreMousePressed

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
    private javax.swing.JMenuItem addNewObj;
    private javax.swing.JMenuItem ambientLighting;
    private javax.swing.JMenuItem backgroundColor;
    private javax.swing.JMenuItem bloomSettings;
    private javax.swing.JCheckBoxMenuItem bloomToggle;
    private javax.swing.JCheckBoxMenuItem bloomTypeToggle;
    private javax.swing.JMenuItem cameraGrain;
    private javax.swing.JMenu cameraMenu;
    private javax.swing.JMenuItem cameraPosition;
    private javax.swing.JMenuItem changeFPS;
    private javax.swing.JMenuItem changeRender;
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
