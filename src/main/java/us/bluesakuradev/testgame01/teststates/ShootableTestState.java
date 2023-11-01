package us.bluesakuradev.testgame01.teststates;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.bluesakuradev.testgame01.Main;

public class ShootableTestState extends BaseAppState {
    static final Logger logger = LogManager.getLogger(ShootableTestState.class.getName());
    Main app;
    AssetManager assetManager;
    InputManager inputManager;
    BitmapFont guiFont;
    Node sceneNode;
    Node guiNode;

    private Geometry mark;
    Node shootables;

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            if(name.equals("shoot") && !pressed){
                CollisionResults results = new CollisionResults();

                Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
                shootables.collideWith(ray, results);
                if(results.size() > 0){
                    for(CollisionResult c : results){
                        logger.info("Collision with " + c.getGeometry().getName());
                    }

                    CollisionResult closest = results.getClosestCollision();
                    mark.setLocalTranslation(closest.getContactPoint());
                    sceneNode.attachChild(mark);
                }else{
                    sceneNode.detachChild(mark);
                }
            }
        }
    };

    @Override
    protected void initialize(Application application) {
        logger.info("Init Shootable");
        this.app = (Main) application;
        this.assetManager = app.getAssetManager();
        this.guiFont = this.assetManager.loadFont("Interface/Fonts/Default.fnt");
        this.sceneNode = new Node("Shootable_Test_Node");
        this.guiNode = new Node("Shootable_Test_Node_GUI");
        this.inputManager = app.getInputManager();

        initMark();

        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");

        shootables = new Node("Shootables");
        sceneNode.attachChild(shootables);

        shootables.attachChild(makeCube("Dragon", new Vector3f(-2f, 0f, 1f)));
        shootables.attachChild(makeCube("Can", new Vector3f(1f, -2f, 0f)));
        shootables.attachChild(makeCube("Sheriff", new Vector3f(0f, 1f, -2f)));
        shootables.attachChild(makeCube("Deputy", new Vector3f(1f, 0f, -4f)));

        this.app.getRootNode().attachChild(sceneNode);
        this.app.getGuiNode().attachChild(guiNode);
    }

    @Override
    protected void cleanup(Application application) {
        logger.info("Clean Shootable");
        this.app.getRootNode().detachChild(sceneNode);
        this.app.getGuiNode().detachChild(guiNode);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    private Geometry makeCube(String name, Vector3f location){
        Box box = new Box(1,1,1);
        Geometry cube = new Geometry(name, box);
        cube.setLocalTranslation(location);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.randomColor());
        cube.setMaterial(mat1);
        return cube;
    }

    private void initMark(){
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("Hitmarker", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }
}
