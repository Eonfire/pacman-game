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

    private Entity pacman;

    private boolean paused;

    private ArrayList<Enemy> enemies = new ArrayList<>();
    private int totalEnemies;
    private int enemiesOnScreen;

    private ArrayList<GoldCoin> coins = new ArrayList<>();
    private int totalCoins;
    private int coinsOnScreen;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            handleGameLogic();
            timerHandler.postDelayed(this, 30);
        }
    };

    public Game(Context context, TextView levelView, TextView oxygenView, TextView pointsView) {
        this.context = context;
        this.levelView = levelView;
        this.oxygenView = oxygenView;
        this.pointsView = pointsView;
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void setGameView(GameView view) {
        this.gameView = view;
    }

    public void newGame() {
        pacman = new Entity(50, 400, BitmapFactory.decodeResource(context.getResources(), R.drawable.astronaut), 12, Direction.NONE);
        enemies = new ArrayList<>();
        totalEnemies = 2;
        enemiesOnScreen = 0;

        coins = new ArrayList<>();
        totalCoins = 10;
        coinsOnScreen = 0;
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

    public Entity getPacman() {
        return pacman;
    }

    public ArrayList<GoldCoin> getCoins() {
        return coins;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void spawnCoins(int canvasWidth, int canvasHeight) {
        if (coinsOnScreen < totalCoins) {
            int i = 0;

            while (i < totalCoins - coinsOnScreen) {
                spawnNewCoin(canvasWidth, canvasHeight);
                coinsOnScreen++;
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

        coinCollisionCheck();
        enemyCollisionCheck();
        pacman.move(w, h);

        oxygen--;
        points++;
        oxygenView.setText(String.format(context.getResources().getString(R.string.oxygen) + "%d", oxygen));
        pointsView.setText(String.format(context.getResources().getString(R.string.points) + "%d", points));
        if (oxygen == 0) newGame();
        handleLevels();

        gameView.invalidate();
    }

    private void spawnNewCoin(int canvasWidth, int canvasHeight) {
        Random r = new Random();
        Bitmap coinBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.oxygen);
        int x = r.nextInt(canvasWidth - coinBitmap.getWidth());
        int y = r.nextInt(canvasHeight - coinBitmap.getHeight());
        coins.add(new GoldCoin(x, y, coinBitmap, 4, Direction.LEFT));
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


    private void coinCollisionCheck() {
        for (int i = 0; i < coins.size(); i++) {
            GoldCoin coin = coins.get(i);

            if (coin.isInGame()) {
                if (coin.outOfBounds()) {
                    coin.setInGame(false);
                    coinsOnScreen--;
                } else if (pacman.isColliding(coin)) {
                    coins.get(i).setInGame(false);
                    coinsOnScreen--;
                    oxygen += oxygenValue;
                    oxygenView.setText(String.format(context.getResources().getString(R.string.oxygen) + "%d", oxygen));
                }
                coin.move(w, h);
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
                } else if (pacman.isColliding(enemy)) {
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

            totalCoins -= 1;
        } else if (points == 900) {
            level = 4;
            levelView.setText(String.format(context.getResources().getString(R.string.level) + "%d", level));
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).setSpeed(this.enemies.get(i).getSpeed() + 1);
            }
            for (int i = 0; i < coins.size(); i++) {
                coins.get(i).setSpeed(coins.get(i).getSpeed() + 1);
            }

            totalEnemies += 1;
            totalCoins -= 1;
            } else if (points == 1200) {
            level = 5;
            levelView.setText(String.format(context.getResources().getString(R.string.level) + "%d", level));

            totalEnemies += 1;
            totalCoins -= 2;
            oxygenValue -= 10;
        }
    }

}
