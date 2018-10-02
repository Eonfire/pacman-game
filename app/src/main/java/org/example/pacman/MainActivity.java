package org.example.pacman;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    // Reference to the main view
    GameView gameView;
    // Reference to the game class.
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Saying we want the game to run only in landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);
        TextView levelTextView = findViewById(R.id.level);
        TextView oxygenTextView= findViewById(R.id.oxygen);
        TextView pointTextView = findViewById(R.id.points);

        game = new Game(this, levelTextView,  oxygenTextView, pointTextView);
        game.setGameView(gameView);
        gameView.setGame(game);

        this.initializeSwipeGestures();
        game.newGame();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_pause) {
            if(this.game.togglePause()){
                item.setTitle(this.getResources().getString(R.string.action_unpause));
            } else {
                item.setTitle(this.getResources().getString(R.string.action_pause));
            }
            return true;
        } else if (id == R.id.action_newGame) {
            game.newGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeSwipeGestures(){
        gameView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                game.getPlayer().setDirection((Direction.UP));
            }
            public void onSwipeRight() {
                game.getPlayer().setDirection((Direction.RIGHT));
            }
            public void onSwipeLeft() {
                game.getPlayer().setDirection((Direction.LEFT));
            }
            public void onSwipeBottom() {
                game.getPlayer().setDirection((Direction.DOWN));
            }
        });
    }
}
