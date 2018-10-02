package org.example.pacman;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {
    Game game;
    int h, w;

    public void setGame(Game game) {
        this.game = game;
    }

    /* The next 3 constructors are needed for the Android view system,
    when we have a custom view.
     */
    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // In the onDraw we put all our code that should be
    // drawn whenever we update the screen.
    @Override
    protected void onDraw(Canvas canvas) {
        // Here we get the width and height
        w = canvas.getWidth();
        h = canvas.getHeight();
        // Update the size for the canvas to the game.
        game.setSize(w, h);
        game.spawnCoins(w, h);
        game.spawnEnemies(w, h);


        // Clear entire canvas to white color
        canvas.drawColor(Color.BLACK);

        // Draw the pacman
        game.getPacman().draw(canvas);
        // Draw the coins
        for (int i = 0; i < game.getCoins().size(); i++) {
            GoldCoin coin = game.getCoins().get(i);
            if (coin.isInGame()) coin.draw(canvas);
        }

        for (int i = 0; i < game.getEnemies().size(); i++) {
            if (game.getEnemies().get(i).isInGame()) game.getEnemies().get(i).draw(canvas);
        }
        super.onDraw(canvas);
    }

}
