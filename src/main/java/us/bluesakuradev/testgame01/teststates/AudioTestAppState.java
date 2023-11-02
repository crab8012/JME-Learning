package us.bluesakuradev.testgame01.teststates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.Environment;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.bluesakuradev.testgame01.AudioManager;
import us.bluesakuradev.testgame01.Main;

public class AudioTestAppState extends BaseAppState {
    public AudioTestAppState(String id){
        super(id);
    }
    static final Logger logger = LogManager.getLogger(AudioTestAppState.class.getName());
    final private ActionListener customActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            triggerSoundActions(name, pressed, tpf);
        }
    };
    final private ActionListener audioEnvActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            audioEnvActions(name, pressed, tpf);
        }
    };

    private AudioManager am;
    private BitmapText audioInfo;

    private Main app;
    private Node sceneNode;
    private AssetManager assetManager;
    private InputManager inputManager;
    private AudioRenderer audioRenderer;
    private BitmapFont guiFont;

    private boolean reverb;
    private boolean music;
    private String audioFx;
    private int sfx = 0;
    private String[] sfxs;
    private int env = 0;
    private Environment[] envs;
    private String[] envNames;

    @Override
    protected void initialize(Application application) {
        envs = new Environment[]{new Environment(Environment.Cavern), new Environment(Environment.Closet),
               new Environment(Environment.Garage), new Environment(Environment.AcousticLab),
               new Environment(Environment.Dungeon)};
        envNames = new String[]{"Cavern", "Closet", "Garage", "AcousticLab", "Dungeon"};
        sfxs = new String[]{"ping1", "error", "notif", "thud"};

        // Initialize the appState
        // Set up the SimpleApplication managers
        this.app = (Main) application;
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.audioRenderer = app.getAudioRenderer();
        this.guiFont = this.assetManager.loadFont("Interface/Fonts/Default.fnt");
        this.sceneNode = new Node("audioTestNode");

        initUI();
        // Audio
        am = new AudioManager(assetManager);
        am.init();

        // Add commaxian song
        am.addMusic("commaxian", "commaxian.ogg");
        am.getMusic("commaxian").setPositional(false);
        am.getMusic("commaxian").setReverbEnabled(false);

        attachUserInput();
        this.app.getGuiNode().attachChild(audioInfo);
        this.app.getRootNode().attachChild(this.sceneNode);
    }

    @Override
    protected void cleanup(Application application) {
        logger.info("ATS.cleanup");
        // Clean up what you initialized in initialize()
        detachUserInput();
        this.app.getGuiNode().detachChild(audioInfo);
        this.app.getRootNode().detachChild(this.sceneNode);
    }

    @Override
    protected void onEnable() {
        logger.info("ATS.onEnable");
        // Manage things that only exist when enabled... Scene Graph Attachment or Input Listeners
    }

    @Override
    protected void onDisable() {
        logger.info("ATS.onDisable");
        // Disable things set in onEnable()
    }

    @Override
    public void update(float tpf){
        //super.update(tpf);
        // Update Logic

        audioInfo.setText("SFX: " + sfxs[sfx] + " Reverb: " + reverb + " Audio Effect: " + audioFx + " Music: " + music);
        audioInfo.setLocalTranslation(audioInfo.getLineWidth(), app.getCamera().getHeight(),0);
    }

    private void attachUserInput(){
        inputManager.addListener(audioEnvActionListener, new String[]{"change_env","toggle_reverb"});
        inputManager.addListener(customActionListener, new String[]{"change_sfx", "play_sfx", "toggle_music"});
        inputManager.addMapping("change_env", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("change_sfx", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("play_sfx", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("toggle_reverb", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping("toggle_music", new KeyTrigger(KeyInput.KEY_5));
    }
    private void detachUserInput(){
        inputManager.removeListener(customActionListener);
        inputManager.removeListener(audioEnvActionListener);
        inputManager.deleteMapping("change_env");
        inputManager.deleteMapping("change_sfx");
        inputManager.deleteMapping("play_sfx");
        inputManager.deleteMapping("toggle_reverb");
        inputManager.deleteMapping("toggle_music");
    }

    private void initUI(){
        audioInfo = new BitmapText(guiFont, false, false);
        audioInfo.setSize(guiFont.getCharSet().getRenderedSize());
        audioInfo.setColor(ColorRGBA.White);
        audioInfo.setText("Audio Information");
        audioInfo.setLocalTranslation(0, 0,0);
    }

    private void audioEnvActions(String name, boolean pressed, float tpf){
        if(name.equals("change_env") && pressed){
            env++;
            if(env >= envs.length){
                env = 0;
            }
            audioRenderer.setEnvironment(envs[env]);
            audioFx = envNames[env];
        }
        if(name.equals("toggle_reverb") && pressed){
            reverb = !reverb;
            logger.info("Reverb: " + reverb);
        }
    }
    private void triggerSoundActions(String name, boolean pressed, float tpf){
        if(name.equals("change_sfx") && pressed){
            sfx++;
            if(sfx >= sfxs.length) {
                sfx = 0;
            }
        }
        if(name.equals("play_sfx") && pressed){
            am.getSfx(sfxs[sfx]).setReverbEnabled(reverb);
            am.getSfx(sfxs[sfx]).playInstance();
        }
        if(name.equals("toggle_music") && pressed){
            if(music){
                am.getMusic("commaxian").stop();
            }else{
                am.getMusic("commaxian").play();
            }
            music = !music;
        }
    }
}
