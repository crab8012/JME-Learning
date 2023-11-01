package us.bluesakuradev.testgame01.teststates;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
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
        logger.info("Main Menu INIT");
    }

    @Override
    protected void cleanup(Application application) {

    }

    @Override
    protected void onEnable() {
        logger.info("Main Menu ENABLE");
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(app.getAssetManager(), app.getInputManager(),
                app.getAudioRenderer(), app.getGuiViewPort());

        nifty = niftyDisplay.getNifty();
        nifty.registerScreenController(this);
        //nifty.fromXml("Interface/screen.xml", "start");
        nifty.addXml("Interface/screen.xml");

        app.getGuiViewPort().addProcessor(niftyDisplay);
        logger.info("Enabling FlyByCamera DragToRotate");
        app.getFlyByCamera().setDragToRotate(true);

        nifty.gotoScreen("start");
    }

    @Override
    protected void onDisable() {
        logger.info("Main Menu Disable");
        logger.info("Trying Nifty.exit()");
        nifty.exit();
        logger.info("Disabling FlyByCamera DragToRotate");
        app.getFlyByCamera().setDragToRotate(false);
    }


    // Nifty GUI
    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    public void startBtnClick(){
        logger.info("Closing Main Menu");
        getApplication().getStateManager().detach(this);
    }
    public void quitBtnClick(){
        logger.info("Quitting the Game");
        getApplication().stop();
    }
}
