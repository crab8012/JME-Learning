package us.bluesakuradev.testgame01.teststates;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.SkyFactory;
import us.bluesakuradev.testgame01.Main;

public class FloatingCubeTestState extends BaseAppState {
    private final float boxMaxUp = 2f;
    private final float boxMaxDown = -2f;
    private final float boxMovementSnapping = 0.001f;
    private final float moveDir = -1f;
    private float boxMoveStep = 0.05f;

    Main app;
    AssetManager assetManager;
    BitmapFont guiFont;
    Node sceneNode;
    Node guiNode;

    Geometry geom;
    BitmapText boxPos;

    @Override
    protected void initialize(Application application) {
        this.app = (Main) application;
        this.assetManager = app.getAssetManager();
        this.guiFont = this.assetManager.loadFont("Interface/Fonts/Default.fnt");
        this.sceneNode = new Node("floatingCubeTestNode");
        this.guiNode = new Node("floatingCubeTestGUINode");

        initSceneGeometry();
        initUI();

        // Sky
        //rootNode.attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));
        sceneNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/PureSky.jpg", SkyFactory.EnvMapType.SphereMap));
        sceneNode.attachChild(geom);

        this.app.getRootNode().attachChild(sceneNode);
        this.app.getGuiNode().attachChild(guiNode);
    }

    @Override
    protected void cleanup(Application application) {
        this.app.getGuiNode().detachChild(guiNode);
        this.app.getRootNode().detachChild(sceneNode);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void update(float tps){
        moveGeometry();
        updateGUI();
    }

    private void initSceneGeometry(){
        Box b = new Box(1,1,1);
        Sphere s = new Sphere(32, 32, 10);
        geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
    }

    private void initUI(){
        boxPos = new BitmapText(guiFont, false);
        boxPos.setSize(guiFont.getCharSet().getRenderedSize());
        boxPos.setColor(ColorRGBA.Green);
        boxPos.setText("10");
        boxPos.setLocalTranslation(300, boxPos.getLineHeight()*2, 0);
        this.guiNode.attachChild(boxPos);
    }

    private float randomSmallFloat(){
        double x = (Math.random())*0.1;
        return Float.parseFloat(x + "");
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
    }
}
