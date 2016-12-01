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


public class BombBuildingControl extends AbstractControl {

	private PositionComponent position;

    private double lastBomb = 300;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (lastBomb - position.getX() < FXGL.getApp().getWidth()) {
            buildBombs();
        }
    }

    private void buildBombs() {
        double height = FXGL.getApp().getHeight();
        double distance = height / 2;

        for (int i = 1; i <= 10; i++) {
            double bombHeight = Math.random() * (height - distance) - 100 ;
            if( i%2 == 0) {
            	Texture view = FXGL.getAssetLoader().loadTexture("bomb.png")
                        .toAnimatedTexture(3, Duration.seconds(0.5));

                Entities.builder()
            	.at( lastBomb + i*533, (0 + bombHeight + distance - 200) )
            	.type(EntityType.BOMB)
            	.bbox(new HitBox("BODY", BoundingShape.box(50, 15)))
            	.viewFromNode(view)
            	.with(new CollidableComponent(true))
            	.buildAndAttach(FXGL.getApp().getGameWorld());
            }
        }

        lastBomb += 10 * 500;
    }

}