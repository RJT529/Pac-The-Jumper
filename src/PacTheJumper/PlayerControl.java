package PacTheJumper;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.gameutils.math.Vec2;


public class PlayerControl extends AbstractControl {

    private Vec2 acceleration = new Vec2(4, 0);

    private PositionComponent position;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        acceleration.x += tpf * 0.2;

         acceleration.y += tpf * 30;

        if (acceleration.y > 100)
            acceleration.y = 100;

        if(position.getY() >= 525) {
        	acceleration.y = -3 ;
        }


        if(position.getY() < 0) {
        	acceleration.y = 5;
        }

        position.translate(acceleration.x, acceleration.y);

    }

    public void jump() {
        acceleration.addLocal(0, -10);

        FXGL.getAudioPlayer().playSound("jump.wav");
    }
}
