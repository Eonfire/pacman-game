package org.example.pacman;

import android.graphics.Bitmap;

import java.util.Random;

public class Enemy extends Entity {
    private Direction previousDirection;
    private MovementType movementType;
    private boolean goingUp;
    private int timesUp;
    private int timesDown;
    private int originalSpeed;

    public Enemy(int x, int y, Bitmap bitmap, int speed, Direction direction, MovementType movementType) {
        super(x, y, bitmap, speed, direction);
        this.movementType = movementType;
        this.originalSpeed = speed;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void straight() {
        setSpeed(this.originalSpeed + 4);
        super.setDirection(Direction.LEFT);
    }

    public void zigZagEnemy() {
        if (previousDirection != Direction.LEFT) {
            previousDirection = Direction.LEFT;
            super.setDirection(Direction.LEFT);
            return;
        }

        if (goingUp) {
            if (timesUp < 50) {
                super.setDirection(Direction.UP);
                previousDirection = Direction.UP;
                timesUp++;
            } else {
                goingUp = false;
                timesUp = 0;
            }
        } else {
            if (timesDown < 50) {
                super.setDirection(Direction.DOWN);
                previousDirection = Direction.DOWN;
                timesDown++;
            } else {
                goingUp = true;
                timesDown = 0;
            }
        }
    }

    // TODO: Needs fixing. Supposed to track the entity that is passed in
    public void trackSomething(Entity entity) {
        int x = 0;
        int y = 0;

        if (Math.abs(x) > Math.abs(y)) {
            if (x > 0) super.setDirection(Direction.RIGHT);
            if (x < 0) super.setDirection(Direction.LEFT);
        } else {
            if (y > 0) super.setDirection(Direction.DOWN);
            if (y < 0) super.setDirection(Direction.UP);
        }
    }
}
