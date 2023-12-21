package us.bluesakuradev.testgame01.teststates;

import com.jme3.anim.AnimComposer;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.water.WaterFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.bluesakuradev.testgame01.AudioManager;
import us.bluesakuradev.testgame01.FirstPersonPlayer;
import us.bluesakuradev.testgame01.Main;

import java.util.Random;

public class CaveState extends BaseAppState implements ActionListener, PhysicsCollisionListener{
    public CaveState(String id){
        super(id);
    }
    static final Logger logger = LogManager.getLogger(CaveState.class.getName());
    private Main app;
    private Node sceneNode;
    private AssetManager assetManager;
    private InputManager inputManager;
    private Camera cam;
    private AudioManager audioManager;


    static String cave_splatTexture = "Textures/Cave/Cave_SplatMap.png";
    static String cave_heightMapTexture = "Textures/Cave/Cave_HeightMap.png";
    static String cave_grassTexture = "Textures/Island/Island_Grass.png";
    static String cave_dirtTexture = "Textures/Island/Island_Sand.jpg";
    static String cave_roadTexture = "Textures/Island/Island_Road.png";


    Vector3f campfire_location = new Vector3f(0f,-100f,0f);
    Vector3f above_campfire_location = new Vector3f(0f, -95f, 0f);
    Vector3f torch_location = new Vector3f(-14f,-98f,-0.7f);

    Spatial campfire_model;
    Spatial torch_model;

    TerrainQuad terrain;

    BitmapFont guiFont;
    BitmapText camPosTxt;

    BulletAppState physicsState = new BulletAppState();
    FirstPersonPlayer player;

    PointLight fireLight;
    PointLight torchLight;

    ParticleEmitter fire;
    ParticleEmitter torch_particle;

    @Override
    protected void initialize(Application application) {
        app = (Main) application;
        sceneNode = new Node("islandScene");
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();

        audioManager = new AudioManager(this.assetManager);
        audioManager.init();

        cam = app.getCamera();
        app.getFlyByCamera().setMoveSpeed(50f);

        Main.disableFlyCamMovement(inputManager);

        initModels();
        initCave();
        initLighting();
        this.app.getStateManager().attach(physicsState);
        initPhysics();

        try{
            initParticles();
        }catch(Exception e){
            e.printStackTrace();
        }


        player = new FirstPersonPlayer(this.inputManager, this.physicsState.getPhysicsSpace(), this.cam);

        initCameraPosReadout();

        this.app.getRootNode().attachChild(this.sceneNode);
    }

    private void initParticles() {
        fire = new ParticleEmitter("Fire", ParticleMesh.Type.Triangle, 30);
        Material fire_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fire_mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        fire.setMaterial(fire_mat);
        fire.setImagesX(2);
        fire.setImagesY(2);
        fire.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
        fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0,2,0));
        fire.setStartSize(1.5f);
        fire.setEndSize(0.1f);
        fire.setGravity(0,0,0);
        fire.setLowLife(0.5f);
        fire.setHighLife(3f);
        fire.getParticleInfluencer().setVelocityVariation(0.3f);
        fire.setInWorldSpace(false);
        sceneNode.attachChild(fire);
        fire.setLocalTranslation(campfire_location);
        fire.emitAllParticles();

        torch_particle = new ParticleEmitter("TorchFire", ParticleMesh.Type.Triangle, 30);
        Material torch_mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        torch_mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        torch_particle.setMaterial(torch_mat);
        torch_particle.setImagesX(2);
        torch_particle.setImagesY(2);
        torch_particle.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
        torch_particle.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        torch_particle.getParticleInfluencer().setInitialVelocity(new Vector3f(0,0.5f,0));
        torch_particle.setStartSize(1.5f);
        torch_particle.setEndSize(0.1f);
        torch_particle.setGravity(0,0,0);
        torch_particle.setLowLife(0.5f);
        torch_particle.setHighLife(3f);
        torch_particle.getParticleInfluencer().setVelocityVariation(0.3f);
        torch_particle.setInWorldSpace(false);
        sceneNode.attachChild(torch_particle);
        torch_particle.setLocalTranslation(torch_location.add(new Vector3f(1f,3f,0f)));
        torch_particle.emitAllParticles();
    }

    private void initCameraPosReadout() {
        this.guiFont = this.assetManager.loadFont("Interface/Fonts/Default.fnt");

        camPosTxt = new BitmapText(guiFont, false);
        camPosTxt.setSize(guiFont.getCharSet().getRenderedSize());
        camPosTxt.setColor(ColorRGBA.Green);
        camPosTxt.setText("0, 0, 0 (0, 0, 0)");
        camPosTxt.setLocalTranslation(300, camPosTxt.getLineHeight()*2, 0);

        app.getGuiNode().attachChild(this.camPosTxt);

    }


    public void initPhysics(){
        // Setup collision and physics for our environment
        //MeshCollisionShape mazeEnvCollisionShape = (MeshCollisionShape) CollisionShapeFactory.createMeshShape(mazeModel);
        terrain.addControl(new RigidBodyControl(0));


        physicsState.getPhysicsSpace().add(terrain);
        physicsState.getPhysicsSpace().addCollisionListener(this);
    }

    public void initCave(){
        Material mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        mat_terrain.setTexture("AlphaMap", assetManager.loadTexture(cave_splatTexture));

        // Grass Layer (RED)
        Texture grass = assetManager.loadTexture(cave_grassTexture);
        grass.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("DiffuseMap", grass);
        mat_terrain.setFloat("DiffuseMap_0_scale", 64f);

        // Dirt Layer (GREEN)
        Texture dirt = assetManager.loadTexture(cave_dirtTexture);
        dirt.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("DiffuseMap_1", dirt);
        mat_terrain.setFloat("DiffuseMap_1_scale", 32f);

        // Road Layer (Blue)
        Texture rock = assetManager.loadTexture(cave_roadTexture);
        rock.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("DiffuseMap_2", rock);
        mat_terrain.setFloat("DiffuseMap_2_scale", 128f);

        // Height Map From Image
        AbstractHeightMap heightMap = null;
        Texture heightMapImage = assetManager.loadTexture(cave_heightMapTexture);
        heightMap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightMap.load();


        int patchSize = 65; // 64x64 tiles +1 - Try changing to 32x32 tiles + 1 - Try changing to 16x16 tiles +1
        // Heightmap is of size 512x512, so we supply 512+1 = 513
        terrain = new TerrainQuad("Cave", patchSize, 129, heightMap.getHeightMap());

        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(0.25f, 0.15f, 0.25f); // Originally 2f 1f 2f
        terrain.setName("Cave");
        sceneNode.attachChild(terrain);

        // Level of Detail calculations
        TerrainLodControl control = new TerrainLodControl(terrain, cam);
        control.setLodCalculator(new DistanceLodCalculator(patchSize, 2.7f)); // Patch Size and a Multiplier
        terrain.addControl(control);
    }

    private void initModels(){
        /*   Campfire in the center of the bowl */
        campfire_model = assetManager.loadModel("Models/fire.glb");
        Material campfire_mat = assetManager.loadMaterial("Materials/litpink.j3m");
        campfire_mat.setColor("Diffuse", ColorRGBA.White);
        campfire_mat.setColor("Ambient", ColorRGBA.White);
        // We need to use the TextureKey form because the UVMap loads in upside-down
        campfire_mat.setTexture("DiffuseMap", assetManager.loadTexture(new TextureKey("Textures/Models/fire.png", false)));
        campfire_model.setMaterial(campfire_mat);

        campfire_model.setLocalTranslation(campfire_location);
        campfire_model.scale(2f);
        campfire_model.rotate(0f, 1.5708f, 0f); // Angles are in radians
        campfire_model.setName("Dock");
        sceneNode.attachChild(campfire_model);

        /* Tree in middle of island */
        torch_model = assetManager.loadModel("Models/torch.glb");
        Material treeMat = assetManager.loadMaterial("Materials/litpink.j3m");
        treeMat.setColor("Diffuse", ColorRGBA.White);
        treeMat.setColor("Ambient", ColorRGBA.White);
        // We need to use the TextureKey form because the UVMap loads in upside-down
        treeMat.setTexture("DiffuseMap", assetManager.loadTexture(new TextureKey("Textures/Models/torch.png", false)));
        torch_model.setMaterial(treeMat);

        torch_model.scale(2);
        torch_model.rotate(0,0,-0.261799f);

        torch_model.setLocalTranslation(torch_location);
        torch_model.setName("Tree");
        sceneNode.attachChild(torch_model);
    }

    private void initLighting(){
        //AmbientLight sceneLight = new AmbientLight();
        //sceneLight.setColor(ColorRGBA.White.mult(0.1f));

        //sceneNode.addLight(sceneLight);

        fireLight = new PointLight(campfire_location.add(new Vector3f(0, 3f, 0)), ColorRGBA.Orange.mult(1f), 20f);
        sceneNode.addLight(fireLight);

        torchLight = new PointLight(torch_location.add(new Vector3f(2f,3f,0f)), ColorRGBA.Yellow.mult(5f), 10f);
        sceneNode.addLight(torchLight);
    }

    @Override
    public void update(float tps){
        player.update(tps);
        Vector3f camLoc = this.cam.getLocation();
        Vector3f camRot = this.cam.getDirection();
        String txt = "[%f, %f, %f](%f, %f, %f)";
        this.camPosTxt.setText(String.format(txt, camLoc.getX(), camLoc.getY(), camLoc.getZ(),
                camRot.getX(), camRot.getY(), camRot.getZ()));
    }

    @Override
    protected void cleanup(Application application) {
        if(physicsState.isDebugEnabled()){
            physicsState.setDebugEnabled(false);
        }
        Main.enableFlyCamMovement(inputManager);
        this.app.getStateManager().detach(physicsState);

        this.app.getRootNode().detachChild(this.sceneNode);
        this.app.getGuiNode().detachChild(this.camPosTxt);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void onAction(String s, boolean b, float v) {

    }

    @Override
    public void collision(PhysicsCollisionEvent physicsCollisionEvent) {

    }
}
