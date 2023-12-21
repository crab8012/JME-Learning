package us.bluesakuradev.testgame01.teststates;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.Environment;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.bluesakuradev.testgame01.AudioManager;
import us.bluesakuradev.testgame01.Main;

public class MazeState extends BaseAppState implements ActionListener{
    public MazeState(String id){
        super(id);
    }
    static final Logger logger = LogManager.getLogger(MazeState.class.getName());

    private Main app;
    private Node sceneNode;
    private AssetManager assetManager;
    private InputManager inputManager;

    Spatial mazeModel;
    AmbientLight sceneLight;
    SpotLight cameraLight;
    BulletAppState physicsState = new BulletAppState();
    private CharacterControl player;
    Camera cam;
    private boolean left=false, right=false, up=false, down=false;

    final private Vector3f camDir = new Vector3f();
    final private Vector3f camLeft = new Vector3f();
    final private Vector3f walkDirection = new Vector3f();

    @Override
    protected void initialize(Application application) {
        app = (Main) application;
        sceneNode = new Node("mazeScene");
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        cam = app.getCamera();
        Main.disableFlyCamMovement(inputManager);

        mazeModel = assetManager.loadModel("Models/maze.glb");
        Material mat1 = assetManager.loadMaterial("Materials/litpink.j3m");
        mat1.setColor("Diffuse", ColorRGBA.White);
        //mat1.setTexture("ColorMap", assetManager.loadTexture("Textures/Maze-UV.png"));
        // We need to use the TextureKey form because the UVMap loads in upside-down
        mat1.setTexture("DiffuseMap", assetManager.loadTexture(new TextureKey("Textures/Maze-UV.png", false)));
        mazeModel.setMaterial(mat1);
        Vector3f mazeLocation = new Vector3f(0f,0f, 0f);
        mazeModel.setLocalTranslation(mazeLocation);
        mazeModel.scale(8.0f);
        sceneNode.attachChild(mazeModel);

        sceneLight = new AmbientLight();
        sceneLight.setColor(ColorRGBA.White.mult(0.5f));
        sceneNode.addLight(sceneLight);

        cameraLight = new SpotLight();
        cameraLight.setSpotRange(100f);
        cameraLight.setSpotInnerAngle(13f* FastMath.DEG_TO_RAD);
        cameraLight.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD);
        cameraLight.setColor(ColorRGBA.White.mult(1.3f));
        cameraLight.setPosition(app.getCamera().getLocation());
        cameraLight.setDirection(app.getCamera().getDirection());
        sceneNode.addLight(cameraLight);

        this.app.getStateManager().attach(physicsState);

        initPhysics();
        initPlayer();

        this.app.getRootNode().attachChild(this.sceneNode);
    }

    private void initPhysics(){
        // Setup collision and physics for our environment
        //MeshCollisionShape mazeEnvCollisionShape = (MeshCollisionShape) CollisionShapeFactory.createMeshShape(mazeModel);
        CollisionShape mazeEnvCollisionShape = CollisionShapeFactory.createMeshShape(mazeModel);
        RigidBodyControl envRB = new RigidBodyControl(mazeEnvCollisionShape, 0);
        mazeModel.addControl(envRB);

        // Setup collision and physics for our player
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(0, 10, 0));

        physicsState.getPhysicsSpace().add(envRB);
        physicsState.getPhysicsSpace().add(player);
    }

    private void initPlayer(){
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, new String[]{"Left", "Right", "Up", "Down", "Jump"});
    }

    @Override
    protected void cleanup(Application application) {
        if(physicsState.isDebugEnabled()){
            physicsState.setDebugEnabled(false);
        }
        Main.enableFlyCamMovement(inputManager);
        this.app.getStateManager().detach(physicsState);
        this.app.getRootNode().detachChild(this.sceneNode);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void update(float tpf){
        cameraLight.setPosition(app.getCamera().getLocation());
        cameraLight.setDirection(app.getCamera().getDirection());

        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft().multLocal(0.4f));
        walkDirection.set(0,0,0);

        if(left){
            walkDirection.addLocal(camLeft);
        }
        if(right){
            walkDirection.addLocal(camLeft.negate());
        }
        if(up){
            walkDirection.addLocal(camDir);
        }
        if(down){
            walkDirection.addLocal(camDir.negate());
        }

        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }

    @Override
    public void onAction(String s, boolean b, float v) {
        if(s.equals("Left")){
            left=b;
        }else if(s.equals("Right")){
            right=b;
        } else if (s.equals("Up")) {
            up=b;
        } else if (s.equals("Down")) {
            down=b;
        } else if (s.equals("Jump")) {
            player.jump();
        }
    }

    public void enablePhysicsDebug(){
        // Draw Collider Outlines
        physicsState.setDebugEnabled(true);
    }
}
