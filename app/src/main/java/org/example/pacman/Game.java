package org.example.pacman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.TextView;

import java.util.Random;
import java.util.ArrayList;

/**
 * This class should contain all your game logic
 */
public class Game {
    private Context context;
    private TextView pointsView;
    private TextView oxygenView;
    private TextView levelView;
    private GameView gameView;
    private int h, w;
    private int points = 0;
    private int oxygen = 500;
    private int level = 1;
    private int oxygenValue = 30;

    private Entity player;

    private boolean paused;

    private ArrayList<Enemy> enemies = new ArrayList<>();
    private int totalEnemies;
    private int enemiesOnScreen;

    private ArrayList<Entity> orbs = new ArrayList<>();
    private int totalOrbs;
    private int orbsOnScreen;

    private Handler gameHandler = new Handler();
    private Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            handleGameLogic();
            gameHandler.postDelayed(this, 30);
        }
    };

    public Game(Context context, TextView levelView, TextView oxygenView, TextView pointsView) {
        this.context = context;
        this.levelView = levelView;
        this.oxygenView = oxygenView;
        this.pointsView = pointsView;
        gameHandler.postDelayed(gameRunnable, 0);
    }

    public void setGameView(GameView view) {
        this.gameView = view;
    }

    public void newGame() {
        player = new Entity(50, 400, BitmapFactory.decodeResource(context.getResources(), R.drawable.astronaut), 12, Direction.NONE);
        enemies = new ArrayList<>();
        totalEnemies = 2;
        enemiesOnScreen = 0;

        orbs = new ArrayList<>();
        totalOrbs = 10;
        orbsOnScreen = 0;
        oxygenValue = 30;

        points = 0;
        oxygen = 500;
        level = 1;
        pointsView.setText(String.format(context.getResources().getString(R.string.points) + "%d", points));
        oxygenView.setText(String.format(context.getResources().getString(R.string.oxygen) + "%d", oxygen));
        levelView.setText(String.format(context.getResources().getString(R.string.level) + "%d", level));

        paused = false;
        // Redraw the screen
        gameView.invalidate();
    }

    public boolean togglePause() {
        paused = !paused;
        return paused;
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public Entity getPlayer() {
        return player;
    }

    public ArrayList<Entity> getOrbs() {
        return orbs;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void spawnOrbs(int canvasWidth, int canvasHeight) {
        if (orbsOnScreen < totalOrbs) {
            int i = 0;

            while (i < totalOrbs - orbsOnScreen) {
                spawnNewOrb(canvasWidth, canvasHeight);
                orbsOnScreen++;
                i++;
            }
        }
    }

    public void spawnEnemies(int canvasWidth, int canvasHeight) {
        if (enemiesOnScreen < totalEnemies) {
            int i = 0;

            while (i < totalEnemies - enemiesOnScreen) {
                spawnNewEnemy(canvasWidth, canvasHeight);
                enemiesOnScreen++;
                i++;
            }
        }
    }

    private void handleGameLogic() {
        if (paused) return;

        orbCollisionCheck();
        enemyCollisionCheck();
        player.move(w, h);

        oxygen--;
        points++;
        oxygenView.setText(String.format(context.getResources().getString(R.string.oxygen) + "%d", oxygen));
        pointsView.setText(String.format(context.getResources().getString(R.string.points) + "%d", points));
        if (oxygen == 0) newGame();
        handleLevels();

        gameView.invalidate();
    }

    private void spawnNewOrb(int canvasWidth, int canvasHeight) {
        Random r = new Random();
        Bitmap orbBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.oxygen);
        int x = r.nextInt(canvasWidth - orbBitmap.getWidth());
        int y = r.nextInt(canvasHeight - orbBitmap.getHeight());
        orbs.add(new Entity(x, y, orbBitmap, 4, Direction.LEFT));
    }

    private void spawnNewEnemy(int canvasWidth, int canvasHeight) {
        Random r = new Random();
        Bitmap enemyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        int x = canvasWidth - enemyBitmap.getWidth();
        int y = r.nextInt(canvasHeight - enemyBitmap.getHeight());

        int enemyType = r.nextInt(2);
        if(enemyType == 1){
            enemies.add(new Enemy(x, y, enemyBitmap, 7, Direction.LEFT, MovementType.ZIGZAG));
        } else {
            enemies.add(new Enemy(x, y, enemyBitmap, 7, Direction.LEFT, MovementType.STRAIGHT));
        }
    }


    private void orbCollisionCheck() {
        for (int i = 0; i < orbs.size(); i++) {
            Entity orb = orbs.get(i);

            if (orb.isInGame()) {
                if (orb.outOfBounds()) {
                    orb.setInGame(false);
                    orbsOnScreen--;
                } else if (player.isColliding(orb)) {
                    orbs.get(i).setInGame(false);
                    orbsOnScreen--;
                    oxygen += oxygenValue;
                    oxygenView.setText(String.format(context.getResources().getString(R.string.oxygen) + "%d", oxygen));
                }
                orb.move(w, h);
            }
        }
    }

    private void enemyCollisionCheck() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.isInGame()) {
                if(enemy.getMovementType() == MovementType.ZIGZAG){
                    enemy.zigZagEnemy();
                } else {
                    enemy.straight();
                }

                if (enemy.outOfBounds()) {
                    enemy.setInGame(false);
                    enemiesOnScreen--;
                } else if (player.isColliding(enemy)) {
                    newGame();
                }
                enemy.move(w, h);
            }
        }
    }

    private void handleLevels() {
        if (points == 300) {
            level = 2;
            levelView.setText(String.format(context.getResources().getString(R.string.level) + "%d", level));
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).setSpeed(this.enemies.get(i).getSpeed() + 2);
            }

            totalEnemies += 1;
        } else if (points == 600) {
            level = 3;
            levelView.setText(String.format(context.getResources().getString(R.string.level) + "%d", level));
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).setSpeed(this.enemies.get(i).getSpeed() + 2);
            }

            totalOrbs -= 1;
        } else if (points == 900) {
            level = 4;
            levelView.setText(String.format(context.getResources().getString(R.string.level) + "%d", level));
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).setSpeed(this.enemies.get(i).getSpeed() + 1);
            }
            for (int i = 0; i < orbs.size(); i++) {
                orbs.get(i).setSpeed(orbs.get(i).getSpeed() + 1);
            }

            totalEnemies += 1;
            totalOrbs -= 1;
            } else if (points == 1200) {
            level = 5;
            levelView.setText(String.format(context.getResources().getString(R.string.level) + "%d", level));

            totalEnemies += 1;
            totalOrbs -= 2;
            oxygenValue -= 10;
        }
    }

}
