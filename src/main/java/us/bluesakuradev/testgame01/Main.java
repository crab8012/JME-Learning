package us.bluesakuradev.testgame01;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.bluesakuradev.testgame01.teststates.*;

public class Main extends SimpleApplication {
    static final Logger logger = LogManager.getLogger(Main.class.getName());
    AudioTestAppState ats = new AudioTestAppState("ats");
    FloatingCubeTestState fcts = new FloatingCubeTestState("fcts");
    ShootableTestState sts = new ShootableTestState("sts");
    MainMenuState menu = new MainMenuState();
    ModelLoadingTestState mlts = new ModelLoadingTestState("mlts");
    MazeState maze = new MazeState("maze");

    IslandState island = new IslandState("island");
    CaveState cave = new CaveState("cave");

    BaseAppState[] states = {ats, fcts, sts, mlts, maze, island, cave};

    final private ActionListener menuCallListener = new ActionListener(){
        @Override
        public void onAction(String name, boolean pressed, float tpf){
            if(name.equals("menu") && pressed){
                logger.info("Pressed Menu Key");

                for(BaseAppState s : states){
                    if(stateManager.hasState(s)){
                        logger.info("Closing State " + s.getId());
                        stateManager.detach(s);
                    }
                }

                logger.info("Opening Main Menu");
                stateManager.attach(menu);
            }
        }
    };

    public static void main(String[] args){
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Test Game 01");
        settings.setResolution(1024,768);
        app.setSettings(settings);
        java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.OFF);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        ScreenshotAppState screenShotState = new ScreenshotAppState();
        this.stateManager.attach(screenShotState);

        this.stateManager.attach(menu);

        this.getFlyByCamera().setMoveSpeed(20);

        initSceneGeometry();
        initSceneUI();
        setKeyMapping();
    }
    private void setKeyMapping(){
        // Remove "ESC" from closing the application
        inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_EXIT );

        // Create a listener and binding for the main menu
        inputManager.addListener(menuCallListener, "menu");
        inputManager.addMapping("menu", new KeyTrigger(KeyInput.KEY_ESCAPE));
    }

    private void initSceneGeometry(){

    }

    private void initSceneUI(){
        Node gui = getGuiNode();
        BitmapText hudTxt = new BitmapText(guiFont, false);
        hudTxt.setSize(guiFont.getCharSet().getRenderedSize());
        hudTxt.setColor(ColorRGBA.randomColor());
        hudTxt.setText("Test JME3 Game 01 - Blue Sakura Developer");
        hudTxt.setLocalTranslation(300, hudTxt.getLineHeight(), 0);
        gui.attachChild(hudTxt);
    }

    public void startSimpleAudioTest(){
        logger.info("Loading Simple Audio Test");
        stateManager.attach(ats);
    }

    public void startFloatingCubeTest(){
        logger.info("Loading Floating Cube Test");
        stateManager.attach(fcts);
    }

    public void startModelLoadingTest(){
        logger.info("Loading Model Loading Test");
        stateManager.attach(mlts);
    }

    public void startShootableTest(){
        logger.info("Loading Shootable Object Test");
        stateManager.attach(sts);
    }

    public void startMaze(boolean physDebug){
        logger.info("Loading Maze Test");
        stateManager.attach(maze);
        if(physDebug){
            maze.enablePhysicsDebug();
        }
    }

    public void startIsland(){
        logger.info("Loading Island Test");
        stateManager.attach(island);
    }
    public void startCave(){
        logger.info("Loading Cave Test");
        stateManager.attach(cave);
    }

    public static void disableFlyCamMovement(InputManager im){
        im.deleteMapping("FLYCAM_StrafeLeft");
        im.deleteMapping("FLYCAM_StrafeRight");
        im.deleteMapping("FLYCAM_Forward");
        im.deleteMapping("FLYCAM_Backward");
        im.deleteMapping("FLYCAM_Rise");
        im.deleteMapping("FLYCAM_Lower");
    }
    public static void enableFlyCamMovement(InputManager im){
        im.addMapping("FLYCAM_StrafeLeft", new KeyTrigger(KeyInput.KEY_A));
        im.addMapping("FLYCAM_StrafeRight", new KeyTrigger(KeyInput.KEY_D));
        im.addMapping("FLYCAM_Forward", new KeyTrigger(KeyInput.KEY_W));
        im.addMapping("FLYCAM_Backward", new KeyTrigger(KeyInput.KEY_S));
        im.addMapping("FLYCAM_Rise", new KeyTrigger(KeyInput.KEY_Q));
        im.addMapping("FLYCAM_Lower", new KeyTrigger(KeyInput.KEY_Z));
    }
}
