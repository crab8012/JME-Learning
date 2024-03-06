package us.bluesakuradev.testgame01.editor;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.ListBoxSelectionChangedEvent;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Main extends SimpleApplication implements ScreenController {
    static final Logger logger = LogManager.getLogger(Main.class.getName());

    NiftyJmeDisplay niftyDisplay;
    Nifty nifty;
    String camPosTpl = "[%f %f %f] (%f %f %f) <%f>";
    Vector3f camPos = new Vector3f(0, 0, 0);
    Quaternion camRot = new Quaternion();

    ArrayList<String> modelNameList = new ArrayList<>();

    ArrayList<Light> spawnedLights = new ArrayList<>();
    ArrayList<Spatial> spawnedObjects = new ArrayList<>();

    ListBox loadableModelListBox;
    ListBox spawnedModelListBox;

    Material defaultMat;

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
            } else if (name.equals("speedUp") && pressed) {
                getFlyByCamera().setMoveSpeed(getFlyByCamera().getMoveSpeed()+2.0f);
            } else if (name.equals("slowDown") && pressed) {
                getFlyByCamera().setMoveSpeed(getFlyByCamera().getMoveSpeed()-2.0f);
            }
        }
    };

    private void setKeyMapping(){
        // Remove "ESC" from closing the application
        inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_EXIT );

        // Create a listener and binding for the main menu
        inputManager.addListener(menuCallListener, "menu");
        inputManager.addListener(menuCallListener, "speedUp");
        inputManager.addListener(menuCallListener, "slowDown");
        inputManager.addMapping("menu", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("speedUp", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("slowDown", new KeyTrigger(KeyInput.KEY_PGDN));
    }

    public void createModelList(){
        // Add the model names to the list
        modelNameList.add("BoxGuy.glb");
        modelNameList.add("bridge.glb");
        modelNameList.add("fire.glb");
        modelNameList.add("Island.glb");
        modelNameList.add("maze.glb");
        modelNameList.add("torch.glb");
        modelNameList.add("umbrella_tree.glb");

        loadableModelListBox = nifty.getScreen("sampleMenu").findNiftyControl("modelListBox", ListBox.class);
        for(String i : modelNameList){
            loadableModelListBox.addItem(i);
        }
        loadableModelListBox.selectItemByIndex(0);

        spawnedModelListBox = nifty.getScreen("sampleMenu").findNiftyControl("spawnedListBox", ListBox.class);
    }

    @NiftyEventSubscriber(id="spawnedListBox")
    public void onListBoxSelectionChanged(final String id, final ListBoxSelectionChangedEvent<String> event){
        List<String> selection = event.getSelection();
        for(String selectedItem : selection){
            logger.info("Selected Spawned Item " + selectedItem);
        }
    }

    @Override
    public void simpleUpdate(float tps){
        if(nifty.getCurrentScreen() == nifty.getScreen("start")){
            camPos = getCamera().getLocation();
            camRot = getCamera().getRotation();

            nifty.getScreen("start").findElementById("playerPosTxt").getRenderer(TextRenderer.class)
                    .setText(String.format(camPosTpl, camPos.getX(), camPos.getY(), camPos.getZ(),
                            camRot.getX(), camRot.getY(), camRot.getZ(), getFlyByCamera().getMoveSpeed()));
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

        defaultMat = assetManager.loadMaterial("Materials/litpink.j3m");
        defaultMat.setColor("Diffuse", ColorRGBA.randomColor());

        createModelList();
        logger.info("Added models to UI list");
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
        String modelFileName = loadableModelListBox.getSelection().get(0).toString();
        Spatial model = assetManager.loadModel("Models/"+modelFileName);
        model.setMaterial(defaultMat);
        model.setLocalTranslation(camPos);
        model.setLocalRotation(camRot);
        spawnedObjects.add(model);
        this.getRootNode().attachChild(spawnedObjects.get(spawnedObjects.size()-1));

        //spawnedModelListBox.addAllItems(spawnedObjects);
        spawnedModelListBox.addItem(model.getName());
    }

    public void spawnLight(){
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        //sun.setDirection(new Vector3f(0.5f, -.5f, -.5f).normalizeLocal());
        sun.setDirection(new Vector3f(camRot.getX(), camRot.getY(), camRot.getZ()));
        spawnedLights.add(sun);

        this.getRootNode().addLight(spawnedLights.get(spawnedLights.size()-1));
    }

    public void quitGame(){
        logger.info("Quitting the Game");
        stop();
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
