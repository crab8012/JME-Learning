package us.bluesakuradev.testgame01;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class FirstPersonPlayer implements ActionListener {
    private CharacterControl player;

    private boolean left=false, right=false, up=false, down=false;
    final private Vector3f camDir = new Vector3f();
    final private Vector3f camLeft = new Vector3f();
    final private Vector3f walkDirection = new Vector3f();

    private InputManager inputManager;
    private PhysicsSpace physicsSpace;
    private Camera camera;

    public FirstPersonPlayer(InputManager im, PhysicsSpace ps, Camera cam){
        this.inputManager = im;
        this.physicsSpace = ps;
        this.camera = cam;

        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, new String[]{"Left", "Right", "Up", "Down", "Jump"});

        // Setup collision and physics for our player
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(0, 10, 0));

        physicsSpace.add(player);
    }

    public void update(float tps){
        camDir.set(camera.getDirection()).multLocal(0.6f);
        camLeft.set(camera.getLeft().multLocal(0.4f));
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
        camera.setLocation(player.getPhysicsLocation());
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
}
