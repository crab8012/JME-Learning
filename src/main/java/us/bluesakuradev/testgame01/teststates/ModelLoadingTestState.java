package us.bluesakuradev.testgame01.teststates;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.font.BitmapFont;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import us.bluesakuradev.testgame01.Main;

import java.util.Vector;

public class ModelLoadingTestState extends BaseAppState {
    public ModelLoadingTestState(String id){
        super(id);
    }
    Main app;
    AssetManager assetManager;
    Node sceneNode;

    Spatial model;
    DirectionalLight sun;

    @Override
    protected void initialize(Application application) {
        this.app = (Main) application;
        this.assetManager = getApplication().getAssetManager();
        this.sceneNode = new Node("Model Loading Test State");

        model = assetManager.loadModel("Models/newtpl_box.j3o");
        model.setMaterial(assetManager.loadMaterial("Materials/litpink.j3m"));
        this.sceneNode.attachChild(model);

        sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(0.5f, -.5f, -.5f).normalizeLocal());
        this.sceneNode.addLight(sun);

        this.app.getRootNode().attachChild(sceneNode);
    }

    @Override
    protected void cleanup(Application application) {
        this.app.getRootNode().detachChild(sceneNode);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
