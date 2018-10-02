package org.example.pacman;

import android.graphics.Bitmap;

/**
 * This class should contain information about a single GoldCoin.
 * such as x and y coordinates (int) and whether or not the goldcoin
 * has been taken (boolean)
 */
public class GoldCoin extends Entity{

    public GoldCoin(int x, int y, Bitmap bitmap, int speed, Direction direction) {
        super(x, y, bitmap, speed, direction);
    }

    public GoldCoin(int x, int y, Bitmap bitmap) {
        super(x, y, bitmap);
    }
}
