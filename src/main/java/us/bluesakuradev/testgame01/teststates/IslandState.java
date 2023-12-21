package us.bluesakuradev.testgame01.teststates;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.bluesakuradev.testgame01.Main;

public class IslandState extends BaseAppState implements ActionListener {
    public IslandState(String id){
        super(id);
    }

    static final Logger logger = LogManager.getLogger(IslandState.class.getName());

    private Main app;
    private Node sceneNode;
    private AssetManager assetManager;
    private InputManager inputManager;
    private Camera cam;

    static String island_splatTexture = "Textures/Island/Island_SplatMap.png";
    static String island_grassTexture = "Textures/Island/Island_Grass.png";
    static String island_dirtTexture = "Textures/Island/Island_Sand.jpg";
    static String island_roadTexture = "Textures/Island/Island_Road.jpg";
    static String island_heightMapTexture = "Textures/Island/Island_HeightMap.png";

    @Override
    protected void initialize(Application application) {
        app = (Main) application;
        sceneNode = new Node("islandScene");
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        cam = app.getCamera();
        app.getFlyByCamera().setMoveSpeed(50f);
        initIsland();

        this.app.getRootNode().attachChild(this.sceneNode);
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
        TerrainQuad terrain = new TerrainQuad("Island", patchSize, 513, heightMap.getHeightMap());

        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(0.25f, 0.5f, 0.25f); // Originally 2f 1f 2f
        sceneNode.attachChild(terrain);

        // Level of Detail calculations
        TerrainLodControl control = new TerrainLodControl(terrain, cam);
        control.setLodCalculator(new DistanceLodCalculator(patchSize, 2.7f)); // Patch Size and a Multiplier
        terrain.addControl(control);
    }

    @Override
    protected void cleanup(Application application) {
        this.app.getRootNode().detachChild(this.sceneNode);
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
}
