package us.bluesakuradev.testgame01;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
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

    public static void main(String[] args){
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Test Game 01");
        app.setSettings(settings);

        app.start();
    }
    @Override
    public void simpleInitApp() {
        initSceneGeometry();
        initSceneUI();

        // Sky
        //rootNode.attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));
        rootNode.attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/PureSky.jpg", SkyFactory.EnvMapType.SphereMap));
        rootNode.attachChild(geom);
        rootNode.attachChild(geom2);
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
    }
}
