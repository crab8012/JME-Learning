package us.bluesakuradev.testgame01;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;

import java.util.HashMap;

public class AudioManager {
    HashMap<String, AudioNode> sfx;
    HashMap<String, AudioNode> mus;
    AssetManager assetManager;

    public AudioManager(AssetManager am){
        sfx = new HashMap<>();
        mus = new HashMap<>();

        this.assetManager = am;
    }

    public void init(){
        // Initialise all the audio
        sfx.put("error", new AudioNode(assetManager, "Sounds/error1.wav", AudioData.DataType.Buffer));
        sfx.put("good", new AudioNode(assetManager, "Sounds/good1.wav", AudioData.DataType.Buffer));
        sfx.put("jump", new AudioNode(assetManager, "Sounds/jump1.wav", AudioData.DataType.Buffer));
        sfx.put("laser1", new AudioNode(assetManager, "Sounds/laser1.wav", AudioData.DataType.Buffer));
        sfx.put("laser2", new AudioNode(assetManager, "Sounds/laser2.wav", AudioData.DataType.Buffer));
        sfx.put("notif", new AudioNode(assetManager, "Sounds/notif1.wav", AudioData.DataType.Buffer));
        sfx.put("ping1", new AudioNode(assetManager, "Sounds/ping1.wav", AudioData.DataType.Buffer));
        sfx.put("ping2", new AudioNode(assetManager, "Sounds/ping2.wav", AudioData.DataType.Buffer));
        sfx.put("thud", new AudioNode(assetManager, "Sounds/thud1.wav", AudioData.DataType.Buffer));

        /* Walking sounds for the Island Test */
        sfx.put("sand1", new AudioNode(assetManager, "Sounds/sand_walk_1.wav", AudioData.DataType.Buffer));
        sfx.put("sand2", new AudioNode(assetManager, "Sounds/sand_walk_2.wav", AudioData.DataType.Buffer));
        sfx.put("sand3", new AudioNode(assetManager, "Sounds/sand_walk_3.wav", AudioData.DataType.Buffer));
        sfx.put("wood1", new AudioNode(assetManager, "Sounds/wood_walk_1.wav", AudioData.DataType.Buffer));
        sfx.put("wood2", new AudioNode(assetManager, "Sounds/wood_walk_2.wav", AudioData.DataType.Buffer));
        sfx.put("wood3", new AudioNode(assetManager, "Sounds/wood_walk_3.wav", AudioData.DataType.Buffer));
    }

    public void addSfx(String keyName, String audioName){
        sfx.put(keyName, new AudioNode(assetManager, "Sounds/"+audioName, AudioData.DataType.Buffer));
    }

    public void addMusic(String keyName, String audioName){
        mus.put(keyName, new AudioNode(assetManager, "Music/"+audioName, AudioData.DataType.Stream));
    }

    public AudioNode getSfx(String keyName){
        return sfx.get(keyName);
    }
    public AudioNode getMusic(String keyName){
        return mus.get(keyName);
    }
}
