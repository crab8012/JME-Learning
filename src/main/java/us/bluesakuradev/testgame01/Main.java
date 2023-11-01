package us.bluesakuradev.testgame01;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Environment;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.util.BufferUtils;
import com.jme3.util.SkyFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.bluesakuradev.testgame01.teststates.*;

import java.awt.*;

public class Main extends SimpleApplication {
    static final Logger logger = LogManager.getLogger(Main.class.getName());
    AudioTestAppState ats = new AudioTestAppState();
    FloatingCubeTestState fcts = new FloatingCubeTestState();
    ShootableTestState sts = new ShootableTestState();
    MainMenuState menu = new MainMenuState();
    ModelLoadingTestState mlts = new ModelLoadingTestState();
    boolean audioState = false;
    boolean floatingCubeState = false;
    boolean shootableState = false;

    final private ActionListener stateListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            if(name.equals("audio_state") && pressed){
                logger.info("Audio State");
                if(audioState){
                    stateManager.detach(ats);
                }else{
                    stateManager.attach(ats);
                }
                audioState = !audioState;
            }
            if(name.equals("cube_state") && pressed){
                logger.info("Cube State");
                if(floatingCubeState){
                    stateManager.detach(fcts);
                }else{
                    stateManager.attach(fcts);
                }
                floatingCubeState = !floatingCubeState;
            }
            if(name.equals("shoot_state") && pressed){
                logger.info("Shootable State");
                if(shootableState){
                    stateManager.detach(sts);
                }else{
                    stateManager.attach(sts);
                }
                shootableState = !shootableState;
            }
            if(name.equals("model_state") && pressed){
                logger.info("Model Test State");
                if(getStateManager().hasState(mlts)){
                    stateManager.detach(mlts);
                }else{
                    stateManager.attach(mlts);
                }
            }
        }
    };

    public static void main(String[] args){
        logger.info("Application Entry");
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Test Game 01");
        settings.setResolution(1024,768);
        app.setSettings(settings);
        logger.info("Settings Added");
        app.start();
    }
    @Override
    public void simpleInitApp() {
        logger.info("Init App");
        ScreenshotAppState screenShotState = new ScreenshotAppState();
        this.stateManager.attach(screenShotState);

        this.stateManager.attach(menu);

        this.getFlyByCamera().setMoveSpeed(20);

        initSceneGeometry();
        initSceneUI();
        setKeyMapping();
    }
    private void setKeyMapping(){
        inputManager.addListener(stateListener, new String[]{"cube_state", "audio_state", "shoot_state", "model_state"});
        inputManager.addMapping("cube_state", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("audio_state", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("shoot_state", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("model_state", new KeyTrigger(KeyInput.KEY_H));
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

    @Override
    public void simpleUpdate(float tpf){
        // TODO: Add update code
        moveGeometry();
        updateGUI();
    }

    public void moveGeometry(){

    }

    public void updateGUI(){

    }
}
