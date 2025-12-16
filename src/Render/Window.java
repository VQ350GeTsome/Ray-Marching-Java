package Render;

import Util.*;
import Vectors.vec3;
import ComplexNumbers.Quaternion;
import File.*;
import SDFs.BlendedSDF;

import java.io.File;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Window extends javax.swing.JFrame {
       
    // Input constants
    private final int   SPACE_BAR     =   32, LEFT_ARROW    =   37, UP_ARROW      =   38, RIGHT_ARROW   =   39,
                        DOWN_ARROW    =   40, LEFT_CLICK    =    1, MIDDLE_CLICK  =    2, RIGHT_CLICK   =    3,
                        W_KEY         =   87, A_KEY         =   65, S_KEY         =   83, D_KEY         =   68,
                        Q_KEY         =   81, E_KEY         =   69, R_KEY         =   82, F1_KEY        =  112,
                        M_WHEEL_UP    = -  1, M_WHEEL_DOWN  =    1, M_LEFT_HOLD   = 1024, M_RIGHT_HOLD  = 4096,
                        M_MIDDLE_HOLD = 2048, F2_KEY        =  113;

    public Window() {
        initComponents();
        core.mainLoop();
    }
    
    //<editor-fold defaultstate="collapsed" desc=" Camera Modifiers ">
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
    private void cameraMover(int dx, int dy, boolean middle) {
        float  grain = Core.cameraMoveGrain;
        vec3[] orien = core.scene.getCameraOrien();   
                                     //        -y        
        vec3    forward = orien[0],  //         |
                right   = orien[1],  //   -x  --+-- +x
                up      = orien[2],  //         |
                move;                //        +y
        
        float sens = 0.10f;
        
        if (middle) {
            move = up.scale(-dy);
            move = move.add(right.scale(dx));
        } else {
            move = forward.scale(-dy);
        }

        core.scene.moveCamera(move.normalize().scale(sens));
        
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
    //</editor-fold>
    
    //<editor-fold defaultstate="collpased" desc=" Pane Creators ">
    private String[] createInputsPane(String title, String[] options, String[] defaults, int inputsPerRow) {  
        int length = options.length;
        JTextField[] fields = new JTextField[length];               //Initialize the feilds we will use
        int rows = (int) Math.ceil((double) length / inputsPerRow); //Calculate the amount of rows we'll need
        
         // Create the panel we will use & input a grid layout with the right amount of rows
         // & inputs per row ( double since we do label field + input field )
        JPanel panel = new JPanel(new java.awt.GridLayout(rows, inputsPerRow * 2, 5, 5));
        
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
    private String[] createInputsPane(String title, String[] both, int inputsPerRow) {
        int length = both.length;                       //Get the amount of settings (double the amount because the current values are stored too)
        
        String[] options  = new String[length / 2];     //Initialize the options  array
        String[] defaults = new String[length / 2];     //Initialize the defaults array
        
        for (int i = 0; length / 2 > i; i++)  options[i] = both[i];                 //Fill the options  array
        for (int i = 0; length / 2 > i; i++) defaults[i] = both[i + length / 2];    //Fill the defualts array
        
        return createInputsPane(title, options, defaults, inputsPerRow);
    }
    private int createButtonsPane(String prompt, String[] options, int buttonsPerRow) {
        // Create our panel
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        
        // Add our prompt ( with extra spacing ) .
        JLabel label = new JLabel(prompt);
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panel.add(label, java.awt.BorderLayout.NORTH);

        // Calculate the row amount.
        int rows = (int) Math.ceil((double) options.length / buttonsPerRow);
        JPanel buttonsPanel = new JPanel(new java.awt.GridLayout(rows, buttonsPerRow, 5, 10));

        // Create an array of buttons
        javax.swing.JButton[] buttons = new javax.swing.JButton[options.length];
        final int[] choice = {-1};

        // Fill the buttons array and add them to the sub panel ( buttonsPanel ) .
        for (int i = 0; i < options.length; i++) {
            buttons[i] = new javax.swing.JButton(options[i]);
            final int index = i;
            buttons[i].addActionListener(e -> {
                choice[0] = index;
                javax.swing.SwingUtilities.getWindowAncestor(panel).dispose();
            });
            buttonsPanel.add(buttons[i]);
        }

        // Add the sub panel ( buttonsPanel ) to the main panel.
        panel.add(buttonsPanel, java.awt.BorderLayout.CENTER);

        // Show the panel
        JOptionPane.showOptionDialog(
            null,
            panel,
            "Selector...",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[]{}, // no default options, we use our custom buttons
            null
        );

        // Return the choice.
        // If no choice was made return -1.
        int finalChoice = choice[0];
        if (finalChoice >= 0) return finalChoice;
        else return -1;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Clicks ">
    private void rightClick(int x, int y, int w, int h) {
        HitInfo info = core.scene.marchRay(x, y, w, h);
        SDFs.SDF clickedObj = info.sdf;
        
        javax.swing.JPopupMenu popup = null;
        if (clickedObj == null) popup = nothingClicked();
        else popup = this.objectClicked(clickedObj, info);
            
        int nw = this.core.getWidth(),
            nh = this.core.getHeight(),
            nx = (int) Math.round((x + 0.5f) * (nw / (float) w)),
            ny = (int) Math.round((y + 0.5f) * (nh / (float) h));

        nx = Math.max(0, Math.min(nx, nw - 1));
        ny = Math.max(0, Math.min(ny, nh - 1));

        popup.show(core, nx, ny);
    }
    private javax.swing.JPopupMenu nothingClicked(){
        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
        
        JMenuItem changeBG = new JMenuItem("Change Background");
        changeBG.addActionListener(evt -> {
            changeBG();
        });
        popup.add(changeBG);
        
        JMenuItem changeSBG = new JMenuItem("Change Secondary Background");
        changeSBG.addActionListener(evt -> {
            changeSecBG();
        });
        popup.add(changeSBG);
        
        return popup;
    }
    private javax.swing.JPopupMenu objectClicked(SDFs.SDF clickedObj, HitInfo info) {
        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();

        JMenuItem delete = new JMenuItem("Delete");     //Define a menu item    
        delete.addActionListener(evt -> {               //Add an event on click
            this.deleteClicked(clickedObj, info.hit);
        });
        popup.add(delete);                              //Add the menu item to the popup menu
        
        JMenuItem color = new JMenuItem("Change Color");
        color.addActionListener(evt -> {
            this.colorClicked(clickedObj, info.hit);
        });
        popup.add(color);
        
        JMenuItem matEdit = new JMenuItem("Edit Material");
        matEdit.addActionListener(evt -> {
            this.matEditClicked(clickedObj, info.hit);
        });
        popup.add(matEdit);
        
        JMenuItem edit = new JMenuItem("Edit Properties");
        edit.addActionListener(evt -> {
            this.regEditClicked(clickedObj, info.hit, true);
        });
        popup.add(edit);
        
        JMenuItem rotate = new JMenuItem("Edit Rotation");
        rotate.addActionListener(evt -> {
            this.rotateClicked(clickedObj, info.hit);
        });
        popup.add(rotate);
        
        if (clickedObj instanceof SDFs.BlendedSDF) {
            JMenuItem blendedEdit = new JMenuItem("Edit Blended Properties");
            blendedEdit.addActionListener(evt -> {
                this.regEditClicked(clickedObj, info.hit, false);
            });
            popup.add(blendedEdit);
        }
        
        return popup;
    }
    private void deleteClicked(SDFs.SDF obj, vec3 hit) {
        // Keep track if the object is blended.
        // If it is we need to keep track of the parent.
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
        
        // Return if unconfirmed.
        if (confirm != JOptionPane.YES_OPTION) return;  
        
        // If the object isn't blended we can just inform the scene.
        // Else we need to inform the parent instead.
        if (!blended) core.scene.removeSDF(obj);
        else ((BlendedSDF) parent).remove(obj);
        
    }
    private void colorClicked(SDFs.SDF obj, vec3 hit) {
        // If the object is blended get the closest child.
        if (obj instanceof SDFs.BlendedSDF) obj = ((SDFs.BlendedSDF) obj).getClosest(hit);
        
        // Prompt the user for either base or highlight color
        // as well as what color.
        promptChangeColor(obj);
    }
    private void matEditClicked(SDFs.SDF obj, vec3 hit) {
        SDFs.SDF parent = null;
        boolean blended = false;
        if (obj instanceof SDFs.BlendedSDF) {
            parent = obj;
            obj = ((SDFs.BlendedSDF) obj).getClosest(hit);
            blended = true;
        }
        
        Util.Material defaultArgs = obj.getMaterial(hit),
                      newMaterial = this.getMaterial(defaultArgs);
        
        // If the new material is null return.
        if (newMaterial == null) return;
         
        if (blended) ((SDFs.BlendedSDF) parent).setMaterial(obj, newMaterial);
        else obj.setMaterial(newMaterial);
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
        String[] inputs = createInputsPane
            (
                "Enter New Settings for " + 
                 ((obj.getName() == null) ? obj.getType() : obj.getName()) 
                 + ": ", obj.getSettingsAndCurrent(), 3
            );
        if (inputs == null) return;

        if (!isolateChild && blended) {
            try { 
                float k = Float.parseFloat(inputs[0]);
                ((BlendedSDF) parent).setK((k <= 0) ? Core.EPS : k);
            } catch (Exception e) { System.err.println(e.getMessage()); }
            
        } else if (!blended)
            obj.parseNewParams(inputs);
        else     
            ((SDFs.BlendedSDF) parent).editChild(inputs, obj);
    }
    private void rotateClicked(SDFs.SDF obj, vec3 hit) {
        if (obj instanceof SDFs.BlendedSDF) obj = ((SDFs.BlendedSDF) obj).getClosest(hit);
        
        String[] options = new String[] { "i: ", "j: ", "k: " };
        String quat = obj.getRotQuat().toStringImag();
        quat = quat.substring(1, quat.length() - 1);
        String[] defaults = quat.split(":");
        String[] inputs = createInputsPane("Enter New Rotation (will be normalized): ", options, defaults, 1);
        
        if (inputs == null) return;
        
        try {
            Quaternion q = new Quaternion(ArrayMath.add(new String[] { "1" } , inputs));
            obj.setRotQuat(q.normalize());
        } catch (Exception e) {
            System.err.println("Error in parsing new roatation.");
            System.err.println(e.getMessage());
        }
    }
    //</editor-fold>
    
    private void generalEditSDF(SDFs.SDF sdf) {
        // Make a list that'll be later turnt into an array
        // to be used to create a button panel.
        java.util.List<String> choices = new java.util.ArrayList<>();

        // Get the sdf specific options, & the current settings.
        String[] options = sdf.getSettingsAndCurrent(),
                 current = Util.ArrayMath.subArray(options, options.length / 2, options.length);
        options = Util.ArrayMath.subArray(options, 0, options.length / 2);
        
        // Add the sdf specific options to the choices list.
        for (String s : options) {
            String trim = s.trim();
            trim = trim.substring(0, trim.length() - 1);
            choices.add(trim);
        }
        
        // Univeral options
        choices.add("Color");       final int COLOR = 0;
        choices.add("Name");        final int NAME = 1;    
        choices.add("Material");    final int MATERIAL = 2;
        choices.add("Delete");      final int DELETE = 3;
        
        // If it's a blended SDF we will add the option to view the children.
        if (sdf instanceof SDFs.BlendedSDF) choices.add("View Children");
        
        // Prompt the user & check input.
        int input = this.createButtonsPane("Select an Option ...", choices.toArray(String[]::new), 1);
        if (input == -1) return;
        
        // If the input is within the SDF specific options parse it as such.
        // Else parse it as a special or univeral option.
        if (input < current.length) {
            // Prompt the user for a new parameter.
            String[] option = new String[] { choices.get(input) };
            String[] currentArg = new String[] { current[input] };
            String newParam = createInputsPane("New Parameter: ", option, currentArg, 1)[0];
            
            // If nothing was inputted return.
            if (newParam == null) return;
            
            // Pass new parameter into the sdf.
            current[input] = newParam.trim();
            sdf.parseNewParams(current);
        } else {
            // Adjust the input.
            input -= current.length;
            switch (input) {
                case COLOR:
                    this.promptChangeColor(sdf);
                    break;
                case NAME:
                    this.promptChangeName(sdf);
                    break;
                case MATERIAL:
                    this.promptChangeMaterial(sdf);
                    break;
                case DELETE:
                    this.promptDelete(sdf);
            }
        }
    }
    
    private void addSDF(int type) {
        final int   SPHERE = 0, CUBE = 1, TORUS = 2,
                    PLANE  = 3, CYLINDER = 4;
        
        //This will be applicable for primitives & repeating
        String[] choices = SDFs.SDFParser.getImplementedPrimitives();  
        int choice = createButtonsPane("Choose an SDF...", choices, 1);
        
        java.util.ArrayList<String> placeHolder = new java.util.ArrayList<>(3);
        
        //We can fill the place holder with a color, as it always come first
        float r = (float) (Math.random() * 255), g = (float) (Math.random() * 255), b = (float) (Math.random() * 255);
        Material mat = new Material(new vec3(r,g,b));
        
        //Gets the cameras orientation and uses the forward & positon vectors to get a 
        //vector n units infront of the camera
        int n = 25;
        vec3[] camOrien = core.scene.getCameraOrien();  
        vec3 forward    = camOrien[0];
        vec3 pos        = core.scene.getCameraPos();
        placeHolder.add((pos.add(forward.scale(n))).round().toString());

        //Now we fill the placeholder with more values and prompt the user for any changes
        String t = "";
        switch (choice) {
            case SPHERE:
                placeHolder.add("1.0"); //Radius
                t = "sphere"; break;
            case CUBE:
                placeHolder.add("1.0"); //Size
                t = "cube"; break;
            case TORUS:
                placeHolder.add("1.0"); //Major Radius
                placeHolder.add("0.5"); //Minor Radius
                t = "torus"; break;
            case PLANE:
                placeHolder.add("(0.0, 0.0, 1.0)"); //Normal vector
                t = "plane"; break;      
            case CYLINDER:
                placeHolder.add("1.0"); //Radius
                placeHolder.add("1.0"); //Height
                t = "cylinder"; break;
        }
        if (type == 1) t = "repeat" + t;
        String[] inputs = createInputsPane("New " + t + "...", SDFs.SDFParser.getSettings(t, type == 1), placeHolder.toArray(String[]::new), 1);
        if (inputs == null) return;
        this.safeAddSDF(mat, t, inputs); 
    }
    private void safeAddSDF(Material mat, String type, String[] inputs) {
        try { core.scene.addSDF(SDFs.SDFParser.getSDF(mat, type, inputs)); }
        catch (Exception e) { System.err.print(e.getMessage()); }
    }
    
    private java.awt.Color getColor() {
        java.awt.Color arg = new java.awt.Color( (int) (255 * Math.random()), (int) (255 * Math.random()), (int) (255 * Math.random()));
        return getColor(arg);
    }
    private java.awt.Color getColor(java.awt.Color defaultArg) {
        java.awt.Color color = JColorChooser.showDialog(rootPane, "Choose a Color: ", defaultArg);
        return color;
    }
    private Util.Material getMaterial(Util.Material defaultArgs) {
        String[] settings = new String[] { "Reflectivity: ", "Specular: ", "Shinyness: ", "Roughness: ",
                                            "Metalness: ", "Opacity: ", "IOR: ", "Texture: ", "Textureness: " };
        String[] defaults = ArrayMath.subArray(defaultArgs.toStringArray(), 2, Material.FIELDS); //Use subarray to not include the first two colors
        String[] inputs = createInputsPane("Enter New Material Settings: ", settings, defaults, 1);
        
        // If no input / user exit return null.
        if (inputs == null) return null; 
        
        // Parse the new material settings into a NEW material obj
        Util.Material newMaterial = new Util.Material();
        newMaterial.color         = defaultArgs.color;
        newMaterial.specularColor = defaultArgs.specularColor;
        try {
            int i = 0;
            newMaterial.reflectivity  = Float.parseFloat(inputs[i++].trim());
            newMaterial.specular      = Float.parseFloat(inputs[i++].trim());
            newMaterial.shinyness     = Float.parseFloat(inputs[i++].trim());
            newMaterial.roughness     = Float.parseFloat(inputs[i++].trim());
            newMaterial.metalness     = Float.parseFloat(inputs[i++].trim());
            newMaterial.opacity       = Float.parseFloat(inputs[i++].trim());
            newMaterial.ior           = Float.parseFloat(inputs[i++].trim()); 
            newMaterial.texture       = Float.parseFloat(inputs[i++].trim()); 
            newMaterial.textureness   = Float.parseFloat(inputs[i++].trim()); 
        } catch (NumberFormatException e) {
            System.err.println("Error Parsing New Material ...");
            System.err.println(e.getMessage());
            return null;
        }
        return newMaterial;
    }
    
    private void promptChangeColor(SDFs.SDF obj) {
        int choice = createButtonsPane("Base Color or Highlight Color ?", new String[] { "Base", "Highlight" }, 1 );
        if (choice == -1) return;
        boolean baseColor = choice == 0;
        
        // Prompts the user with a color chooser with a random color to start
        java.awt.Color color = getColor();
        if (color == null) return;
        
        if (baseColor) obj.setColor(new vec3(color));
        else obj.setHighlightColor(new vec3(color));
    }
    private void promptChangeName(SDFs.SDF obj) {
        String name = obj.getName();
        if (name == null) name = obj.getType();
        String newName = this.createInputsPane(
                "Enter a New Name...", new String[] { "Enter New Name: " }, new String[] { name }, 1
        )[0];
        obj.setName(newName);
    }
    private void promptChangeMaterial(SDFs.SDF obj) {
        
    }
    private void promptDelete(SDFs.SDF obj) {
        
    }
    
    private void changeBG() {
        java.awt.Color input = getColor(core.scene.getBackground().toAwtColor());
        if (input == null) return;
        
        vec3 newBG = new vec3(input);
        
        core.scene.setBackground(newBG);
    }
    private void changeSecBG() {
        java.awt.Color input = getColor(core.scene.getSecondaryBG().toAwtColor());
        if (input == null) return;
        
        vec3 newBG = new vec3(input);
        
        core.scene.setSecondaryBG(newBG);
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
        seeAllObj = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        bloomToggle = new javax.swing.JCheckBoxMenuItem();
        bloomSettings = new javax.swing.JMenuItem();
        renderMenu = new javax.swing.JMenu();
        resolutionChange = new javax.swing.JMenuItem();
        changeRender = new javax.swing.JMenuItem();
        changeFPS = new javax.swing.JMenuItem();
        f2SSChange = new javax.swing.JMenuItem();
        cameraMenu = new javax.swing.JMenu();
        cameraPosition = new javax.swing.JMenuItem();
        cameraGrain = new javax.swing.JMenuItem();
        cameraObjCheck = new javax.swing.JCheckBoxMenuItem();
        lightingMenu = new javax.swing.JMenu();
        sceneLighitng = new javax.swing.JMenuItem();
        setLightColor = new javax.swing.JMenuItem();
        ambientLighting = new javax.swing.JMenuItem();
        shadowAmount = new javax.swing.JMenuItem();
        skyboxMenu = new javax.swing.JMenu();
        seeLightCheck = new javax.swing.JCheckBoxMenuItem();
        setSkyboxLightAmount = new javax.swing.JMenuItem();
        backgroundColor = new javax.swing.JMenuItem();
        secondarybgColor = new javax.swing.JMenuItem();
        gradientCheck = new javax.swing.JCheckBoxMenuItem();
        useLightForGradCheck = new javax.swing.JCheckBoxMenuItem();
        customSkybox = new javax.swing.JMenuItem();

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

        seeAllObj.setText("View All Objects");
        seeAllObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeAllObjActionPerformed(evt);
            }
        });
        objectsMenu.add(seeAllObj);

        menuBar.add(objectsMenu);

        jMenu1.setText("Post Processing");

        bloomToggle.setSelected(true);
        bloomToggle.setText("Toggle Bloom");
        bloomToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bloomToggleActionPerformed(evt);
            }
        });
        jMenu1.add(bloomToggle);

        bloomSettings.setText("Bloom Settings");
        bloomSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bloomSettingsActionPerformed(evt);
            }
        });
        jMenu1.add(bloomSettings);

        menuBar.add(jMenu1);

        renderMenu.setText("Render");

        resolutionChange.setText("Change Resolution");
        resolutionChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resolutionChangeActionPerformed(evt);
            }
        });
        renderMenu.add(resolutionChange);

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

        f2SSChange.setText("Change F2 Screen Shot Resolution");
        f2SSChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                f2SSChangeActionPerformed(evt);
            }
        });
        renderMenu.add(f2SSChange);

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

        cameraObjCheck.setText("Camera as Object");
        cameraObjCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraObjCheckActionPerformed(evt);
            }
        });
        cameraMenu.add(cameraObjCheck);

        menuBar.add(cameraMenu);

        lightingMenu.setText("Lighting");

        sceneLighitng.setText("Change Scene Lighting");
        sceneLighitng.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sceneLighitngActionPerformed(evt);
            }
        });
        lightingMenu.add(sceneLighitng);

        setLightColor.setText("Change Scene Lighting Color");
        setLightColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLightColorActionPerformed(evt);
            }
        });
        lightingMenu.add(setLightColor);

        ambientLighting.setText("Change Ambient Lighting");
        ambientLighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ambientLightingActionPerformed(evt);
            }
        });
        lightingMenu.add(ambientLighting);

        shadowAmount.setText("Change Shadow Amount");
        shadowAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shadowAmountActionPerformed(evt);
            }
        });
        lightingMenu.add(shadowAmount);

        menuBar.add(lightingMenu);

        skyboxMenu.setText("Skybox");

        seeLightCheck.setSelected(true);
        seeLightCheck.setText("See Skybox Light");
        seeLightCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeLightCheckActionPerformed(evt);
            }
        });
        skyboxMenu.add(seeLightCheck);

        setSkyboxLightAmount.setText("Change Skybox Light Amount");
        setSkyboxLightAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setSkyboxLightAmountActionPerformed(evt);
            }
        });
        skyboxMenu.add(setSkyboxLightAmount);

        backgroundColor.setText("Change Background Color (Primary for Gradient)");
        backgroundColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backgroundColorActionPerformed(evt);
            }
        });
        skyboxMenu.add(backgroundColor);

        secondarybgColor.setText("Change Secondary Background (For Gradient)");
        secondarybgColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secondarybgColorActionPerformed(evt);
            }
        });
        skyboxMenu.add(secondarybgColor);

        gradientCheck.setText("Gradient");
        gradientCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradientCheckActionPerformed(evt);
            }
        });
        skyboxMenu.add(gradientCheck);

        useLightForGradCheck.setText("Use Light for Gradient");
        useLightForGradCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLightForGradCheckActionPerformed(evt);
            }
        });
        skyboxMenu.add(useLightForGradCheck);

        customSkybox.setText("Input Custom Skybox");
        skyboxMenu.add(customSkybox);

        menuBar.add(skyboxMenu);

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
                if (core.timer.isRunning()) core.timer.stop();
                else core.timer.start();
                break;
            case R_KEY:
                core.refresh();
                break;
            case F1_KEY:
                core.screenShotAsIs();
                break;
            case F2_KEY:
                core.screenShotHiRes();
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
        String[] inputs = createInputsPane("Enter new grains: ", options, defaults, 3);
        
        if (inputs == null) return;
        
        try {
            Core.cameraMoveGrain = Float.parseFloat(inputs[0]);
            Core.cameraRotateGrain = Float.parseFloat(inputs[1]);   
        } catch (Exception e) { System.err.println("Error in parsing new grains: " + e.getMessage()); }
    }//GEN-LAST:event_cameraGrainActionPerformed

    private void sceneLighitngActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sceneLighitngActionPerformed
        String[] options    = new String[] { "Scene Light Direction: " };
        String[] defaultDir = new String[] { core.scene.getSceneLighting().round(2).toString() };
        String[] inputs     = createInputsPane("Enter new Lighting Direction", options, defaultDir, 3);
        
        if (inputs == null) return; 
        
        try { core.scene.setSceneLighting(new vec3(inputs[0])); }
        catch (Exception e) { System.err.println(e.getMessage()); }
    }//GEN-LAST:event_sceneLighitngActionPerformed

    private void ambientLightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ambientLightingActionPerformed
        String[] options = new String[] { "New Level: " };
        String[] current = new String[] { ""+core.scene.getAmbientLighting() };
        String[] input  = createInputsPane("Enter New Ambient Lighting Level", options, current, 3);
        
        if (input == null) return;
        
        float f = Float.parseFloat(input[0]);   //Parse the new float given by the user
        f = (0 > f) ? 0 : f;
        f = (f > 1) ? 1 : f;    //Clamp to [0, 1]
        
        core.scene.setAmbientLighting(f);
    }//GEN-LAST:event_ambientLightingActionPerformed

    private void cameraPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraPositionActionPerformed
        String[] options    = new String[] { "New Camera Position: " };
        String[] defaultDir = new String[] { core.scene.getCameraPos().round(2).toString() };
        String[] inputs     = createInputsPane("Enter new Camera Position", options, defaultDir, 3);
        
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
        int typeChoice = createButtonsPane("Choose a type...", options, 1);    //Prompt the user with which type they want
        if (typeChoice == -1) return;   //The user chose nothing
        
        addSDF(typeChoice);
    }//GEN-LAST:event_addNewObjActionPerformed

    private void bloomToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bloomToggleActionPerformed
        boolean checked = bloomToggle.getState();
        core.bloom = checked;
    }//GEN-LAST:event_bloomToggleActionPerformed

    private void bloomSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bloomSettingsActionPerformed
        String[] options  = new String[] { "Bloom Sensitivity: " , "Bloom Radius: " };
        String[] defaults = PostProcessor.getBloomSettings();
        String[] inputs   = createInputsPane("Enter New Bloom Settings: ", options, defaults, 3);
        
        PostProcessor.setBloomSettings(inputs);
    }//GEN-LAST:event_bloomSettingsActionPerformed

    private void changeRenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeRenderActionPerformed
        String[] options = new String[] { "Max Steps: ", "Max Distance: ", "Shadow Max Steps: " };
        String[] defaults = core.scene.getMarchParams();
        String[] inputs = createInputsPane("Enter New March Settings: ", options, defaults, 3);
        
        core.scene.setMarchParams(inputs);
    }//GEN-LAST:event_changeRenderActionPerformed

    private void changeFPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeFPSActionPerformed
        String[] options = new String[] { "Enter new FPS Target: " };
        String[] current = new String[] { ""+(int) (1000.0f / core.timer.getDelay()) };
        String inputs[] = createInputsPane("New FPS Cap...", options, current, 1);
        
        if (inputs == null) return;
        
        try { 
            int newTarget = Integer.parseInt(inputs[0]);
            if (0 > newTarget) {
                System.err.println("FPS cannot be negative!");
                return;
            }
            core.timer.setDelay((int) 1000.0f / newTarget);
        }
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
        
        int button = evt.getModifiersEx();
        if (button == M_LEFT_HOLD)   cameraRotater(dx, dy);
        if (button == M_RIGHT_HOLD)  cameraMover(dx, dy, true);
        if (button == M_MIDDLE_HOLD) cameraMover(dx, dy, false);
        
        mouseLastX = evt.getX(); mouseLastY = evt.getY();
    }//GEN-LAST:event_coreMouseDragged

    private void coreMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_coreMousePressed
        //Update the mouse starting positon x & y.
        mouseLastX = evt.getX(); mouseLastY = evt.getY();
    }//GEN-LAST:event_coreMousePressed

    private void resolutionChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resolutionChangeActionPerformed
        String[] settings = new String[] { "Width: ", "Height: " };
        String[] defaults = core.getResoultion();
        String[] inputs   = createInputsPane("Enter New Dimensions: ", settings, defaults, 1);
        
        if (inputs == null) return;
        
        try {
            int w = Integer.parseInt(inputs[0].trim()),
                h = Integer.parseInt(inputs[1].trim());
            core.timer.stop();
            core.changeResolution(w, h);
            core.timer.start();
        } catch (NumberFormatException e) {
            System.err.println("Error parsing new dimensions ...");
            System.err.println(e.getMessage());
        }
    }//GEN-LAST:event_resolutionChangeActionPerformed

    private void f2SSChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_f2SSChangeActionPerformed
        String[] settings = new String[] { "Width: ", "Height: " };
        String[] defaults = core.getF2Res();
        String[] inputs   = createInputsPane("Enter High Resolution Screen Shot Dimensions: ", settings, defaults, 1);
        
        if (inputs == null) return;
        
        try {
            int w = Integer.parseInt(inputs[0].trim()),
                h = Integer.parseInt(inputs[1].trim());
            core.changeF2Res(w, h);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing new dimensions ...");
            System.err.println(e.getMessage());
        }
    }//GEN-LAST:event_f2SSChangeActionPerformed

    private void cameraObjCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraObjCheckActionPerformed
        core.scene.cameraObj(cameraObjCheck.getState());
    }//GEN-LAST:event_cameraObjCheckActionPerformed

    private void backgroundColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backgroundColorActionPerformed
        changeBG();
    }//GEN-LAST:event_backgroundColorActionPerformed

    private void shadowAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shadowAmountActionPerformed
        String[] settings = new String[] { "New Shadow Amount: " };
        String[] defaults = new String[] { ""+core.scene.getShadowAmount() };
        String[] inputs = createInputsPane("Setting a New Shadow Amount ...", settings, defaults, 1);
        
        if (inputs == null) return;
        
        try {
            float f = Float.parseFloat(inputs[0]);
            if (0 > f) throw new NumberFormatException("Shadow Amount cannot be negative ...");
            core.scene.setShadowAmount(f);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing new shadow amount ...");
            System.err.println(e.getMessage());
        }
    }//GEN-LAST:event_shadowAmountActionPerformed

    private void gradientCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradientCheckActionPerformed
        core.scene.setUseGradient(gradientCheck.getState());
        if (!gradientCheck.getState()) {
            useLightForGradCheck.setState(false);
            core.scene.setGradUseZ(true);
        }
    }//GEN-LAST:event_gradientCheckActionPerformed

    private void useLightForGradCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLightForGradCheckActionPerformed
        core.scene.setGradUseZ(!useLightForGradCheck.getState());
    }//GEN-LAST:event_useLightForGradCheckActionPerformed

    private void seeLightCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLightCheckActionPerformed
        core.scene.setSeeLight(seeLightCheck.getState());
    }//GEN-LAST:event_seeLightCheckActionPerformed

    private void secondarybgColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secondarybgColorActionPerformed
        changeSecBG();
    }//GEN-LAST:event_secondarybgColorActionPerformed

    private void setSkyboxLightAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setSkyboxLightAmountActionPerformed
        String[] options  = new String[] { "New Skybox Light Amount: " };
        String[] defaults = new String[] { ""+core.scene.getSkyboxLightAmount() };
        String[] inputs   = createInputsPane("Setting New Skybox Light Amount ...", options, defaults, 1);
        
        if (inputs == null) return;
        
        try {
            core.scene.setSkyboxLightAmount(Float.parseFloat(inputs[0].trim()));
        } catch (NumberFormatException e) {
            System.err.println("Error parsing new skybox light amount ...");
            System.err.println(e.getMessage());
        }
        
    }//GEN-LAST:event_setSkyboxLightAmountActionPerformed

    private void setLightColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setLightColorActionPerformed
        core.scene.setLightColor(new vec3(getColor(core.scene.getLightColor().toAwtColor())));
    }//GEN-LAST:event_setLightColorActionPerformed

    private void seeAllObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeAllObjActionPerformed
        // Get all objects and display them to the user so that they can then select from that
        // and delete them, merge them, or edit them.
        
        // Get the list of sdfs and make an array that'll store the names of them all.
        java.util.List<SDFs.SDF> sdfs = core.scene.getSDFsList();
        String[] sdfNames = new String[sdfs.size()];
        
        // Fill the name array with the names.
        for (int i = 0; sdfNames.length > i; i++) {
            SDFs.SDF sdf = sdfs.get(i);
            String name = sdf.getName();
            if (name == null) name = sdf.getType();
            sdfNames[i] = name;
        }
        
        // Get the user input and open the edit pane for that SDF.
        // If the user selects nothing return.
        int input = this.createButtonsPane("Select an object: ", sdfNames, 1);
        if (input == -1) return;
        this.generalEditSDF(sdfs.get(input));
    }//GEN-LAST:event_seeAllObjActionPerformed

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
	      if ("Windows Classic".equals(info.getName())) {
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
    private javax.swing.JMenuItem cameraGrain;
    private javax.swing.JMenu cameraMenu;
    private javax.swing.JCheckBoxMenuItem cameraObjCheck;
    private javax.swing.JMenuItem cameraPosition;
    private javax.swing.JMenuItem changeFPS;
    private javax.swing.JMenuItem changeRender;
    private Render.Core core;
    private javax.swing.JMenuItem customSkybox;
    private javax.swing.JMenuItem exportScene;
    private javax.swing.JMenuItem f2SSChange;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JCheckBoxMenuItem gradientCheck;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu lightingMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu objectsMenu;
    private javax.swing.JMenuItem openScene;
    private javax.swing.JMenu renderMenu;
    private javax.swing.JMenuItem resolutionChange;
    private javax.swing.JMenuItem sceneLighitng;
    private javax.swing.JMenuItem secondarybgColor;
    private javax.swing.JMenuItem seeAllObj;
    private javax.swing.JCheckBoxMenuItem seeLightCheck;
    private javax.swing.JMenuItem setLightColor;
    private javax.swing.JMenuItem setSkyboxLightAmount;
    private javax.swing.JMenuItem shadowAmount;
    private javax.swing.JMenu skyboxMenu;
    private javax.swing.JCheckBoxMenuItem useLightForGradCheck;
    // End of variables declaration//GEN-END:variables
}
