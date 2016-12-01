package PacTheJumper;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.FXGLListener;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.ScrollingBackgroundView;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.scene.SceneFactory;
import com.almasb.fxgl.scene.menu.FXGLDefaultMenu;
import com.almasb.fxgl.scene.menu.MenuType;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.UIFactory;


import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;


public class PacTheJumperApp extends GameApplication {

    private PlayerControl playerControl;

    private IntegerProperty score  = new SimpleIntegerProperty();

    private StringProperty endGame = new SimpleStringProperty();

    private ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private ObjectProperty<Color> scoreColor = new SimpleObjectProperty<>();

    private boolean requestNewGame = false;
    private boolean reset = false;
    private Image image;

    private double time = 0;

    private Music bgm = null;

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

  @Override
    protected SceneFactory initSceneFactory() {
        return new SceneFactory() {

            // 2. override main menu and things you need

            @NotNull
            @Override
            public FXGLMenu newMainMenu(@NotNull GameApplication app) {
            	return new PacMenu(app, MenuType.MAIN_MENU);
            }

            @NotNull
            @Override
            public FXGLMenu newGameMenu(@NotNull GameApplication app) {
            	return new PacMenu(app, MenuType.GAME_MENU) ;
            }

        };
    }


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(900);
        settings.setHeight(650);
        settings.setTitle("Pac The Jumper");
        settings.setVersion("0.2");
        settings.setProfilingEnabled(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(true);
        settings.setFullScreen(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                playerControl.jump();
            }
        }, KeyCode.SPACE);

        getInput().addAction(new UserAction("Exit") {
            @Override
            protected void onActionBegin() {
                pause();
                exit();
            }
        }, KeyCode.L);

       getInput().addAction(new UserAction("Restart") {
            @Override
            protected void onActionBegin() {
                resume();
            }
        }, KeyCode.R);

        getInput().addAction(new UserAction("Pause") {
            @Override
            protected void onActionBegin() {
                pause();
            }
        }, KeyCode.S);

    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        color.setValue(Color.BLACK);
       // endColor.setValue(Color.BLACK);

        getGameScene().addGameView(new ScrollingBackgroundView(getAssetLoader().loadTexture("background11.png"),
                Orientation.HORIZONTAL));

        getGameScene().setUIMouseTransparent(true);
        //initBackground();
        initPlayer();

        initBackgroundMusic();
    }



    @Override
    protected void initPhysics() {

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALL) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                if (reset)
                    return;

                 image = getGameScene().getRoot().getScene().snapshot(null);

                endGame.set("Game Over\n"+"Score: " + score.get()) ;


                reset = true;
                score.set(0);

            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.COIN) {
        	 @Override
             protected void onCollisionBegin(Entity a, Entity b) {

        		 FXGL.getAudioPlayer().playSound("coin.wav");
        		 b.removeFromWorld();
        		 score.set(score.get() + 100);
        	 }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.BOMB) {
       	 @Override
            protected void onCollisionBegin(Entity a, Entity b) {
       		image = getGameScene().getRoot().getScene().snapshot(null);
       		if (reset)
                return;
       		FXGL.getAudioPlayer().playSound("bomb.wav");
       		 reset = true;
       		endGame.set("Game Over\n"+"Score: " + score.get()) ;
       		 score.set(0);
       	 }
       });


    }

    @Override
    protected void initUI() {

    	scoreColor.setValue(Color.WHITE);

        Text uiScore = getUIFactory().newText("", 70);
        uiScore.setTranslateX(getWidth() - 200);
        uiScore.setTranslateY(50);
        uiScore.fillProperty().bind(scoreColor );
        uiScore.textProperty().bind(score.asString());


        Text endScore = getUIFactory().newText("", 200);
        endScore.setTranslateX(100);
        endScore.setTranslateY( getHeight()/3 + 50);
        endScore.fillProperty().bind(scoreColor );
        endScore.textProperty().bind(endGame);


        getGameScene().addUINode(uiScore);
        getGameScene().addUINode(endScore);
    }



    @Override
    protected void onUpdate(double tpf) {
        GraphicsContext g = getGameScene().getGraphicsContext();

        if(reset == true) {
        	g.drawImage(image, 0, 0, getWidth(),getHeight());
             time += tpf;
             if ( time >=  .75) {
            	 score.set(0);
        		 requestNewGame = true;
        		 endGame.set("") ;
        		 getGameWorld().reset();
                 reset = false;

        	 }
        }
    }

    @Override
    protected void onPostUpdate(double tpf) {
        if (requestNewGame) {
            requestNewGame = false;
            time = 0;
            startNewGame();
        }
    }

    private void initPlayer() {
        playerControl = new PlayerControl();

        Texture view = getAssetLoader().loadTexture("ninjaRun.png")
                .toAnimatedTexture(6, Duration.seconds(0.5));

        GameEntity player = Entities.builder()
                .at(100, 100)
                .type(EntityType.PLAYER)
                .bbox(new HitBox("BODY", BoundingShape.box(70, 110)))
                .viewFromNode(view)
                .with(new CollidableComponent(true))
                .with(playerControl, new WallBuildingControl(),new CoinBuildingControl(),new BombBuildingControl())
                .buildAndAttach(getGameWorld());

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, (int) getHeight());
        getGameScene().getViewport().bindToEntity(player, getWidth() / 3, 0);
    }



    private void initBackgroundMusic() {
        // already initialized
        if (bgm != null)
            return;

        bgm = getAssetLoader().loadMusic("bgm.mp3");
        bgm.setCycleCount(Integer.MAX_VALUE);

        getAudioPlayer().playMusic(bgm);

        addFXGLListener(new FXGLListener() {
            @Override
            public void onPause() {}

            @Override
            public void onResume() {}

            @Override
            public void onReset() {}

            @Override
            public void onExit() {
                getAudioPlayer().stopMusic(bgm);
                bgm.dispose();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
