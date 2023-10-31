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

import java.awt.*;

public class Main extends SimpleApplication {
    Geometry geom;
    Geometry geom2;
    private float boxMaxUp = 2f;
    private float boxMaxDown = -2f;
    private float boxMovementSnapping = 0.001f;
    private float moveDir = -1f;
    private float boxMoveStep = 0.05f;
    BitmapText boxPos;
    BitmapText camPos;
    BitmapText audioInfo;

    AudioManager am;
    boolean reverb = false;
    String audioFx = "NONE";

    final VideoRecorderAppState recState = new VideoRecorderAppState();

    final private ActionListener customActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            if(name.equals("sound1") && pressed){
                am.getSfx("ping1").setReverbEnabled(reverb);
                am.getSfx("ping1").play();
            }
            if(name.equals("sound2") && pressed){
                am.getSfx("error").setReverbEnabled(reverb);
                am.getSfx("error").play();
            }
            if(name.equals("sound3") && pressed){
                am.getSfx("notif").setReverbEnabled(reverb);
                am.getSfx("notif").play();
            }
            if(name.equals("sound4") && pressed){
                am.getSfx("thud").setReverbEnabled(reverb);
                am.getSfx("thud").play();
            }
            if(name.equals("mus1") && pressed){
                AudioNode mus = am.getMusic("commaxian");
                mus.setReverbEnabled(reverb);
                mus.setPositional(false);
                am.getMusic("commaxian").play();
            }
            if(name.equals("musStop") && pressed){
                am.getMusic("commaxian").stop();
            }
            if(name.equals("reverb") && pressed){
                reverb = !reverb;
                System.out.println("Reverb: " + reverb);
            }
        }
    };

    final private ActionListener audioEnvActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            if(name.equals("cavern") && pressed){
                audioRenderer.setEnvironment(new Environment(Environment.Cavern));
                audioFx = "Cavern";
            }
            if(name.equals("garage") && pressed){
                audioRenderer.setEnvironment(new Environment(Environment.Garage));
                audioFx = "Garage";
            }
            if(name.equals("lab") && pressed){
                audioRenderer.setEnvironment(new Environment(Environment.AcousticLab));
                audioFx = "Lab";
            }
            if(name.equals("closet") && pressed){
                audioRenderer.setEnvironment(new Environment(Environment.Closet));
                audioFx = "Closet";
            }
            if(name.equals("dungeon") && pressed){
                audioRenderer.setEnvironment(new Environment(Environment.Dungeon));
                audioFx = "Dungeon";
            }
        }
    };

    final private ActionListener stateListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            if(name.equals("startRec") && pressed){
                stateManager.attach(recState);
                System.out.println("Screen Recording Started");
            }
            if(name.equals("stopRec") && pressed){
                stateManager.detach(recState);
                System.out.println("Screen Recording Stopped");
            }
        }
    };

    public static void main(String[] args){
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Test Game 01");
        settings.setResolution(1024,768);
        app.setSettings(settings);
        BufferUtils.setTrackDirectMemoryEnabled(true);
        app.start();
    }
    @Override
    public void simpleInitApp() {
        ScreenshotAppState screenShotState = new ScreenshotAppState();
        this.stateManager.attach(screenShotState);
        initSceneGeometry();
        initSceneUI();
        setKeyMapping();

        // Audio
        am = new AudioManager(assetManager);
        am.init();

        // Add commaxian song
        am.addMusic("commaxian", "commaxian.ogg");

        // Sky
        //rootNode.attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));
        rootNode.attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/PureSky.jpg", SkyFactory.EnvMapType.SphereMap));
        rootNode.attachChild(geom);
        rootNode.attachChild(geom2);
    }
    private void setKeyMapping(){
        inputManager.addListener(customActionListener, new String[]{"sound1", "sound2", "sound3", "sound4", "mus1", "musStop", "reverb"});
        inputManager.addMapping("sound1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("sound2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("sound3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("sound4", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping("mus1", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("musStop", new KeyTrigger(KeyInput.KEY_PGDN));

        inputManager.addListener(audioEnvActionListener, new String[]{"cavern", "garage", "lab", "closet", "dungeon"});
        inputManager.addMapping("cavern", new KeyTrigger(KeyInput.KEY_0));
        inputManager.addMapping("garage", new KeyTrigger(KeyInput.KEY_9));
        inputManager.addMapping("lab", new KeyTrigger(KeyInput.KEY_8));
        inputManager.addMapping("closet", new KeyTrigger(KeyInput.KEY_7));
        inputManager.addMapping("dungeon", new KeyTrigger(KeyInput.KEY_6));
        inputManager.addMapping("reverb", new KeyTrigger(KeyInput.KEY_BACK));

        inputManager.addListener(stateListener, new String[]{"startRec", "stopRec"});
        inputManager.addMapping("startRec", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("stopRec", new KeyTrigger(KeyInput.KEY_K));
    }

    private void initSceneGeometry(){
        Box b = new Box(1,1,1);
        Sphere s = new Sphere(32, 32, 10);
        geom = new Geometry("Box", b);
        geom2 = new Geometry("Sphere", s);
        geom2.move(1f, 1f, 0);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Gray);
        geom2.setMaterial(mat2);
    }

    private void initSceneUI(){
        Node gui = getGuiNode();
        BitmapText hudTxt = new BitmapText(guiFont, false);
        hudTxt.setSize(guiFont.getCharSet().getRenderedSize());
        hudTxt.setColor(ColorRGBA.randomColor());
        hudTxt.setText("Test JME3 Game 01 - Blue Sakura Developer");
        hudTxt.setLocalTranslation(300, hudTxt.getLineHeight(), 0);
        gui.attachChild(hudTxt);

        boxPos = new BitmapText(guiFont, false);
        boxPos.setSize(guiFont.getCharSet().getRenderedSize());
        boxPos.setColor(ColorRGBA.Green);
        boxPos.setText("10");
        boxPos.setLocalTranslation(300, boxPos.getLineHeight()*2, 0);
        gui.attachChild(boxPos);

        camPos = new BitmapText(guiFont, false);
        camPos.setSize(guiFont.getCharSet().getRenderedSize()/2);
        camPos.setColor(ColorRGBA.White);
        camPos.setText("Camera Position");
        camPos.setLocalTranslation(20, camPos.getLineHeight()*40,0);
        gui.attachChild(camPos);

        audioInfo = new BitmapText(guiFont, false);
        audioInfo.setSize(guiFont.getCharSet().getRenderedSize());
        audioInfo.setColor(ColorRGBA.White);
        audioInfo.setText("Audio Information");
        audioInfo.setLocalTranslation(this.getCamera().getWidth()-audioInfo.getLineWidth(), this.getCamera().getHeight()-audioInfo.getLineHeight(),0);
        gui.attachChild(audioInfo);
    }

    private float randomSmallFloat(){
        double x = (Math.random())*0.1;
        return Float.parseFloat(x + "");
    }

    @Override
    public void simpleUpdate(float tpf){
        // TODO: Add update code
        moveGeometry();
        updateGUI();
        //cam.lookAt(geom.getWorldTranslation(), new Vector3f(0, 0, 1f));
    }

    public void moveGeometry(){
        geom.rotate(randomSmallFloat(), randomSmallFloat(), randomSmallFloat());

        if(geom.getLocalTransform().getTranslation().y >= boxMaxUp){
            boxMoveStep *= moveDir;
            geom.setLocalTranslation(geom.getLocalTranslation().getX(), boxMaxUp-boxMovementSnapping, geom.getLocalTranslation().getZ());
            geom.getMaterial().setColor("Color", ColorRGBA.randomColor());
        }
        if(geom.getLocalTransform().getTranslation().y <= boxMaxDown){
            boxMoveStep *= moveDir;
            geom.setLocalTranslation(geom.getLocalTranslation().getX(), boxMaxDown+boxMovementSnapping, geom.getLocalTranslation().getZ());
            geom.getMaterial().setColor("Color", ColorRGBA.randomColor());
        }

        geom.move(0, boxMoveStep, 0);
    }

    public void updateGUI(){
        boxPos.setText(geom.getLocalTransform().getTranslation().y + "");
        Vector3f cloc = cam.getLocation();
        Quaternion crot = cam.getRotation();
        String camPosOut = "Cam Position: [%fx - %fy - %fz] | Cam Rotation: [%fx - %fy - %fz - %fw]";

        camPos.setText(String.format(camPosOut, cloc.getX(), cloc.getY(), cloc.getZ(), crot.getX(), crot.getY(), crot.getZ(), crot.getW()));

        audioInfo.setText("Reverb: " + reverb + " Audio Effect: " + audioFx);
        audioInfo.setLocalTranslation(this.getCamera().getWidth()-audioInfo.getLineWidth(), this.getCamera().getHeight(),0);
    }
}
