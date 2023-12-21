package us.bluesakuradev.testgame01.teststates;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.bluesakuradev.testgame01.AudioManager;
import us.bluesakuradev.testgame01.Main;
import com.jme3.water.WaterFilter;

import java.util.Random;

public class IslandState extends BaseAppState implements ActionListener, PhysicsCollisionListener {
    public IslandState(String id){
        super(id);
    }

    static final Logger logger = LogManager.getLogger(IslandState.class.getName());

    private Main app;
    private Node sceneNode;
    private AssetManager assetManager;
    private InputManager inputManager;
    private Camera cam;
    private AudioManager audioManager;

    static String island_splatTexture = "Textures/Island/Island_SplatMap.png";
    static String island_grassTexture = "Textures/Island/Island_Grass.png";
    static String island_dirtTexture = "Textures/Island/Island_Sand.jpg";
    static String island_roadTexture = "Textures/Island/Island_Road.png";
    static String island_heightMapTexture = "Textures/Island/Island_HeightMap.png";

    static Vector3f bridgeLocation = new Vector3f(48f, -33f, -5f);
    static Vector3f treeLocation = new Vector3f(-26f, -32f, 6f);
    static Vector3f boxGuyLocation = new Vector3f(-20f, -31f, 0f);

    Spatial treeModel;
    Spatial bridgeModel;
    Spatial boxGuyModel;

    TerrainQuad terrain;

    BitmapFont guiFont;
    BitmapText camPosTxt;

    BulletAppState physicsState = new BulletAppState();
    private CharacterControl player;

    private boolean left=false, right=false, up=false, down=false;

    final private Vector3f camDir = new Vector3f();
    final private Vector3f camLeft = new Vector3f();
    final private Vector3f walkDirection = new Vector3f();

    String player_standing_material = "";


    // Water Stuff
    FilterPostProcessor fpp;
    WaterFilter water;
    float initialWaterHeight = -33f;
    Vector3f lightDir = new Vector3f(-4.9f, -1.3f, 5.9f);

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

        initIsland();
        initModels();
        initLighting();
        this.app.getStateManager().attach(physicsState);
        initPhysics();
        initPlayer();

        initCameraPosReadout();

        initWater();

        initBoxGuy();

        this.app.getRootNode().attachChild(this.sceneNode);
    }

    private void initBoxGuy() {
        /*   Dock/Bridge at edge of island */
        boxGuyModel = assetManager.loadModel("Models/BoxGuy.glb");
        Material boxGuyMat = assetManager.loadMaterial("Materials/litpink.j3m");
        boxGuyMat.setColor("Diffuse", ColorRGBA.White);
        boxGuyMat.setColor("Ambient", ColorRGBA.White);
        // We need to use the TextureKey form because the UVMap loads in upside-down
        boxGuyMat.setTexture("DiffuseMap", assetManager.loadTexture(new TextureKey("Textures/Models/boxGuy.png", false)));
        boxGuyModel.setMaterial(boxGuyMat);

        boxGuyModel.setLocalTranslation(boxGuyLocation);
        boxGuyModel.scale(2f);
        //boxGuyModel.rotate(0f, 1.5708f, 0f); // Angles are in radians
        boxGuyModel.setName("BoxGuy");
        sceneNode.attachChild(boxGuyModel);

        logger.info("Adding BoxGuy Animations");
        //AnimMigrationUtils.migrate(boxGuyModel);
        //AnimComposer animComposer = boxGuyModel.getControl(AnimComposer.class);
        Node charNode = (Node)boxGuyModel;
        Node armature = (Node)charNode.getChild("Armature");
        AnimComposer animComposer = armature.getControl(AnimComposer.class);
        if(animComposer != null){
            animComposer.setCurrentAction("Wave");
        }else{
            logger.error("Animation Composer is null");
        }
    }

    public void initWater(){
        fpp = new FilterPostProcessor(assetManager);
        water = new WaterFilter(sceneNode, lightDir);
        water.setWaterHeight(initialWaterHeight);
        fpp.addFilter(water);
        app.getViewPort().addProcessor(fpp);
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

    public void initPlayer(){
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, new String[]{"Left", "Right", "Up", "Down", "Jump"});
    }

    public void initPhysics(){
        // Setup collision and physics for our environment
        //MeshCollisionShape mazeEnvCollisionShape = (MeshCollisionShape) CollisionShapeFactory.createMeshShape(mazeModel);
        terrain.addControl(new RigidBodyControl(0));

        // Setup collision and physics for our player
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(0, 10, 0));

        CompoundCollisionShape dockCollisionShape = (CompoundCollisionShape) CollisionShapeFactory.createMeshShape(bridgeModel);
        CompoundCollisionShape treeCollisionShape = (CompoundCollisionShape) CollisionShapeFactory.createMeshShape(treeModel);
        RigidBodyControl dockRB = new RigidBodyControl(dockCollisionShape, 0);
        RigidBodyControl treeRB = new RigidBodyControl(treeCollisionShape, 0);
        bridgeModel.addControl(dockRB);
        treeModel.addControl(treeRB);

        physicsState.getPhysicsSpace().add(player);
        physicsState.getPhysicsSpace().add(terrain);
        physicsState.getPhysicsSpace().add(dockRB);
        physicsState.getPhysicsSpace().add(treeRB);

        physicsState.getPhysicsSpace().addCollisionListener(this);
    }

    public void initIsland(){
        Material mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        mat_terrain.setTexture("Alpha", assetManager.loadTexture(island_splatTexture));

        // Grass Layer (RED)
        Texture grass = assetManager.loadTexture(island_grassTexture);
        grass.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex1", grass);
        mat_terrain.setFloat("Tex1Scale", 64f);

        // Dirt Layer (GREEN)
        Texture dirt = assetManager.loadTexture(island_dirtTexture);
        dirt.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex2", dirt);
        mat_terrain.setFloat("Tex2Scale", 32f);

        // Road Layer (Blue)
        Texture rock = assetManager.loadTexture(island_roadTexture);
        rock.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex3", rock);
        mat_terrain.setFloat("Tex3Scale", 128f);

        // Height Map From Image
        AbstractHeightMap heightMap = null;
        Texture heightMapImage = assetManager.loadTexture(island_heightMapTexture);
        heightMap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightMap.load();


        int patchSize = 65; // 64x64 tiles +1 - Try changing to 32x32 tiles + 1 - Try changing to 16x16 tiles +1
        // Heightmap is of size 512x512, so we supply 512+1 = 513
        terrain = new TerrainQuad("Island", patchSize, 513, heightMap.getHeightMap());

        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(0.25f, 0.5f, 0.25f); // Originally 2f 1f 2f
        terrain.setName("Island");
        sceneNode.attachChild(terrain);

        // Level of Detail calculations
        TerrainLodControl control = new TerrainLodControl(terrain, cam);
        control.setLodCalculator(new DistanceLodCalculator(patchSize, 2.7f)); // Patch Size and a Multiplier
        terrain.addControl(control);
    }

    private void initModels(){
        /*   Dock/Bridge at edge of island */
        bridgeModel = assetManager.loadModel("Models/bridge.glb");
        Material bridgeMat = assetManager.loadMaterial("Materials/litpink.j3m");
        bridgeMat.setColor("Diffuse", ColorRGBA.White);
        bridgeMat.setColor("Ambient", ColorRGBA.White);
        // We need to use the TextureKey form because the UVMap loads in upside-down
        bridgeMat.setTexture("DiffuseMap", assetManager.loadTexture(new TextureKey("Textures/Models/bridge.png", false)));
        bridgeModel.setMaterial(bridgeMat);

        bridgeModel.setLocalTranslation(bridgeLocation);
        bridgeModel.scale(2f);
        bridgeModel.rotate(0f, 1.5708f, 0f); // Angles are in radians
        bridgeModel.setName("Dock");
        sceneNode.attachChild(bridgeModel);

        /* Tree in middle of island */
        treeModel = assetManager.loadModel("Models/umbrella_tree.glb");
        Material treeMat = assetManager.loadMaterial("Materials/litpink.j3m");
        treeMat.setColor("Diffuse", ColorRGBA.White);
        treeMat.setColor("Ambient", ColorRGBA.White);
        // We need to use the TextureKey form because the UVMap loads in upside-down
        treeMat.setTexture("DiffuseMap", assetManager.loadTexture(new TextureKey("Textures/Models/tree.png", false)));
        treeModel.setMaterial(treeMat);

        treeModel.scale(2);

        treeModel.setLocalTranslation(treeLocation);
        treeModel.setName("Tree");
        sceneNode.attachChild(treeModel);
    }

    private void initLighting(){
        AmbientLight sceneLight = new AmbientLight();
        sceneLight.setColor(ColorRGBA.White.mult(0.5f));

        sceneNode.addLight(sceneLight);
    }

    private void randomWalkSound(String type){
        Random random = new Random();
        Boolean playableSound = true;
        AudioNode sfx = null;
        if(type.equalsIgnoreCase("sand")){
            int sound = random.nextInt(3);
            switch(sound){
                case 0:
                    logger.debug("Selecting sound sand1");
                    sfx = audioManager.getSfx("sand1");
                    break;
                case 1:
                    logger.debug("Selecting sound sand2");
                    sfx = audioManager.getSfx("sand2");
                    break;
                case 2:
                    logger.debug("Selecting sound sand3");
                    sfx = audioManager.getSfx("sand3");
                    break;
            }
            playableSound = true;
        }else if(type.equalsIgnoreCase("wood")){
            int sound = random.nextInt(3);
            switch(sound){
                case 0:
                    logger.debug("Selecting sound wood1");
                    sfx = audioManager.getSfx("wood1");
                    break;
                case 1:
                    logger.debug("Selecting sound wood2");
                    sfx = audioManager.getSfx("wood2");
                    break;
                case 2:
                    logger.debug("Selecting sound wood3");
                    sfx = audioManager.getSfx("wood3");
                    break;
            }
            playableSound = true;
        }

        if(playableSound){
            if(sfx == null){
                logger.error("Selected Step Sound is Null");
            }else{
                sfx.play();
            }
        }

    }

    @Override
    public void update(float tps){
        Vector3f camLoc = this.cam.getLocation();
        Vector3f camRot = this.cam.getDirection();
        String txt = "[%f, %f, %f](%f, %f, %f)";
        this.camPosTxt.setText(String.format(txt, camLoc.getX(), camLoc.getY(), camLoc.getZ(),
                                                  camRot.getX(), camRot.getY(), camRot.getZ()));


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

        // If the player is moving
        if(left | right | up | down){
            if(player.onGround()){
                logger.debug("Player moved on ground");
                randomWalkSound(player_standing_material);
            }
        }

        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
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

        this.app.getViewPort().removeProcessor(fpp);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

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

    @Override
    public void collision(PhysicsCollisionEvent physicsCollisionEvent) {
        //logger.info("Collision: " + physicsCollisionEvent.getNodeA().getName() + " " + physicsCollisionEvent.getNodeB().getName());
        Boolean isNodeNull = false;
        if(physicsCollisionEvent.getNodeA() == null){
            logger.debug("Physics Node A is Null");
            isNodeNull = true;
        }else{
            logger.debug("Physics Node A is " + physicsCollisionEvent.getNodeA());
        }
        if(physicsCollisionEvent.getNodeB() == null){
            logger.debug("Physics Node B is Null");
            isNodeNull = true;
        }else{
            logger.debug("Physics Node B is " + physicsCollisionEvent.getNodeB());
        }

        //if(isNodeNull){
        //    return;
        //}

        if(physicsCollisionEvent.getNodeB().getName().equals("Island")){
            player_standing_material = "sand";
        }
        if(physicsCollisionEvent.getNodeB().getName().equals("Dock")){
            player_standing_material = "wood";
        }
        if(physicsCollisionEvent.getNodeB().getName().equals("Tree")) {
            player_standing_material = "wood";
        }
    }
}
