package us.bluesakuradev.testgame01.editor;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class Main extends SimpleApplication implements ScreenController {
    static final Logger logger = LogManager.getLogger(Main.class.getName());

    NiftyJmeDisplay niftyDisplay;
    Nifty nifty;
    String camPosTpl = "[%f %f %f] (%f %f %f)";
    Vector3f camPos = new Vector3f(0, 0, 0);
    Quaternion camRot = new Quaternion();

    boolean inMenu = false;
    final private ActionListener menuCallListener = new ActionListener(){
        @Override
        public void onAction(String name, boolean pressed, float tpf){
            if(name.equals("menu") && pressed){
                logger.info("Pressed Menu Key");
                if(!inMenu){
                    logger.info("Enabling Access to Menu");
                    getFlyByCamera().setDragToRotate(true);
                    nifty.gotoScreen("sampleMenu");
                }else if(inMenu){
                    logger.info("Disabling Access to Menu");
                    getFlyByCamera().setDragToRotate(false);
                    nifty.gotoScreen("start");
                }
                inMenu = !inMenu;
            }
        }
    };

    private void setKeyMapping(){
        // Remove "ESC" from closing the application
        inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_EXIT );

        // Create a listener and binding for the main menu
        inputManager.addListener(menuCallListener, "menu");
        inputManager.addMapping("menu", new KeyTrigger(KeyInput.KEY_ESCAPE));
    }

    @Override
    public void simpleUpdate(float tps){
        if(nifty.getCurrentScreen() == nifty.getScreen("start")){
            camPos = getCamera().getLocation();
            camRot = getCamera().getRotation();

            nifty.getScreen("start").findElementById("playerPosTxt").getRenderer(TextRenderer.class)
                    .setText(String.format(camPosTpl, camPos.getX(), camPos.getY(), camPos.getZ(),
                            camRot.getX(), camRot.getY(), camRot.getZ()));
        }
    }

    @Override
    public void simpleInitApp() {
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(getAssetManager(),
                getInputManager(), getAudioRenderer(), getGuiViewPort());

        nifty = niftyDisplay.getNifty();
        nifty.registerScreenController(this);
        nifty.addXml("GameEditor/menu.xml");

        getGuiViewPort().addProcessor(niftyDisplay);

        nifty.gotoScreen("start");

        setKeyMapping();

        logger.info("Initialized Game Logic");
    }

    public static void main(String[] args){
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Test JME3 Game Editor");
        settings.setResolution(1024,768);
        app.setSettings(settings);
        java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.OFF);
        logger.info("Initialized Game Window");
        app.start();
    }

    public void spawnObject(){
        logger.info("Spawn Object");
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }
}
