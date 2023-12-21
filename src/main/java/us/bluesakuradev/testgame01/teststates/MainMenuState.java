package us.bluesakuradev.testgame01.teststates;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.bluesakuradev.testgame01.Main;

import javax.annotation.Nonnull;

public class MainMenuState extends BaseAppState implements ScreenController {
    static final Logger logger = LogManager.getLogger(MainMenuState.class.getName());

    Main app;
    Application application;

    NiftyJmeDisplay niftyDisplay;
    Nifty nifty;


    @Override
    protected void initialize(Application application) {
        app = (Main) application;
        this.application = application;
    }

    @Override
    protected void cleanup(Application application) {

    }

    @Override
    protected void onEnable() {
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(app.getAssetManager(), app.getInputManager(),
                app.getAudioRenderer(), app.getGuiViewPort());

        nifty = niftyDisplay.getNifty();
        nifty.registerScreenController(this);
        //nifty.fromXml("Interface/screen.xml", "start");
        nifty.addXml("Interface/screen.xml");

        app.getGuiViewPort().addProcessor(niftyDisplay);
        app.getFlyByCamera().setDragToRotate(true);

        nifty.gotoScreen("start");
    }

    @Override
    protected void onDisable() {
        nifty.exit();
        app.getFlyByCamera().setDragToRotate(false);
    }

    public void closeMenu(){
        logger.info("Closing Main Menu");
        getApplication().getStateManager().detach(this);
    }

    public void startSimpleAudioTest(){
        app.startSimpleAudioTest();
        closeMenu();
    }

    public void startFloatingCubeTest(){
        app.startFloatingCubeTest();
        closeMenu();
    }

    public void startModelLoadingTest(){
        app.startModelLoadingTest();
        closeMenu();
    }

    public void startShootableTest(){
        app.startShootableTest();
        closeMenu();
    }

    public void startMaze(){
        app.startMaze(false);
        closeMenu();
    }
    public void startMazeDebug(){
        app.startMaze(true);
        closeMenu();
    }

    public void startIsland(){
        app.startIsland();
        closeMenu();
    }

    public void startCave(){
        app.startCave();
        closeMenu();
    }

    public void quitBtnClick(){
        logger.info("Quitting the Game");
        getApplication().stop();
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }
}
