package PacTheJumper;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;

import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class CoinBuildingControl extends AbstractControl {

	private PositionComponent position;

    private double lastCoin = 300;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (lastCoin - position.getX() < FXGL.getApp().getWidth()) {
            buildCoins();
        }
    }

    private void buildCoins() {
        double height = FXGL.getApp().getHeight();
        double distance = height / 2;

        for (int i = 1; i <= 10; i++) {
            double coinHeight = Math.random() * (height - distance) ;

            Texture view1 = FXGL.getAssetLoader().loadTexture("coin.png")
                    .toAnimatedTexture(10, Duration.seconds(0.5));
            Entities.builder()
            	.at( lastCoin + i*473, ( coinHeight + 200) )
            	.type(EntityType.COIN)
            	.bbox(new HitBox("BODY", BoundingShape.box(50, 50)))
            	.viewFromNode(view1)
            	.with(new CollidableComponent(true))
            	.buildAndAttach(FXGL.getApp().getGameWorld());
        }

        lastCoin += 10 * 500;
    }

}