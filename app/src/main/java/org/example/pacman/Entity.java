package org.example.pacman;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class Entity {
    private int x;
    private int y;
    private int speed = 0;
    private Bitmap bitmap;
    private boolean inGame = true;
    private Direction direction = Direction.NONE;

    public Entity(int x, int y, Bitmap bitmap, int speed, Direction direction) {
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
        this.speed = speed;
        this.direction = direction;
    }

    public Entity(int x, int y, Bitmap bitmap) {
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void draw(Canvas canvas){
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public void move(int canvasWidth, int canvasHeight) {
        switch (this.direction) {
            case LEFT:
                if (x - speed > 0) {
                    x = x - speed;
                }
                break;
            case RIGHT:
                if (x + speed + bitmap.getWidth() < canvasWidth) {
                   x = x + speed;
                }
                break;
            case UP:
                if (y - speed > 0) {
                    y = y - speed;
                }
                break;
            case DOWN:
                if (y + speed + bitmap.getWidth() < canvasHeight) {
                   y = y + speed;
                }
                break;
        }
    }

    public boolean outOfBounds(){
        if(x - speed <= 0) {
            return true;
        }

        return false;
    }

    public boolean isColliding(Entity entity){
        Rect current = new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
        Rect toCheck = new Rect(entity.getX(), entity.getY(), entity.getX() + entity.getBitmap().getWidth(), entity.getY() + entity.getBitmap().getHeight());
        return current.intersect(toCheck);
    }

}
