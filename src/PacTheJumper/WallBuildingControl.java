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


public class WallBuildingControl extends AbstractControl {

    private PositionComponent position;

    private double lastWall = 300;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (lastWall - position.getX() < FXGL.getApp().getWidth()) {
            buildWalls();
        }
    }

    private Rectangle wallView(double width, double height) {
        Rectangle wall = new Rectangle(width, height);
        wall.setArcWidth(25);
        wall.setArcHeight(25);
        wall.fillProperty().bind(FXGL.<PacTheJumperApp>getAppCast().colorProperty());
        return wall;
    }

    private void buildWalls() {
        double height = FXGL.getApp().getHeight();
        double distance = height / 2;
        for (int i = 1; i <= 10; i++) {
            double topHeight = Math.random() * (height - distance) -100;
            Entities.builder()
                    .at(lastWall + i * 750, 0 + topHeight + distance + 25)
                    .type(EntityType.WALL)
                    .viewFromNodeWithBBox(wallView(75, height - distance - topHeight))
                    .with(new CollidableComponent(true))
                    .buildAndAttach(FXGL.getApp().getGameWorld());
        }
        lastWall += 10 * 500;
    }
}
