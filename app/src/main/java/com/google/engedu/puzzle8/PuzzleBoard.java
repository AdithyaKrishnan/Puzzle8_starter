package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard
{
    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS =
            {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
            };
    private ArrayList<PuzzleTile> tiles;
    private int steps = 0;
    private PuzzleBoard previousBoard = null;

    PuzzleBoard getPreviousBoard()
    {
        return previousBoard;
    }

    void setPreviousBoard(PuzzleBoard board)
    {
        previousBoard = board;
    }

    void setSteps(int steps)
    {
        this.steps = steps;
    }

    int getSteps()
    {
        return steps;
    }

    PuzzleBoard(Bitmap bitmap, int parentWidth)
    {
        tiles = new ArrayList<PuzzleTile>();

        bitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);
        Bitmap[] bmp = new Bitmap[NUM_TILES * NUM_TILES];
        int k = 0, i, j;
        for (i = 0; i < NUM_TILES; i++)
        {
            for (j = 0; j < NUM_TILES; j++)
            {
                bmp[k] = Bitmap.createBitmap(bitmap, (parentWidth * i) / NUM_TILES, (j * parentWidth) / NUM_TILES, parentWidth / NUM_TILES, parentWidth / NUM_TILES);
                if (!((i == NUM_TILES - 1) && (j == NUM_TILES - 1)))
                {
                    tiles.add(new PuzzleTile(bmp[k], k));
                } else
                    tiles.add(null);
                k++;
            }
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard)
    {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        previousBoard = otherBoard;
        steps = otherBoard.steps + 1;
    }

    public void reset()
    {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas)
    {
        if (tiles == null)
        {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++)
        {
            PuzzleTile tile = tiles.get(i);
            if (tile != null)
            {
                tile.draw(canvas, i / NUM_TILES, i % NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y)
    {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++)
        {
            PuzzleTile tile = tiles.get(i);
            if (tile != null)
            {
                if (tile.isClicked(x, y, i / NUM_TILES, i % NUM_TILES))
                {
                    return tryMoving(i / NUM_TILES, i % NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY)
    {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES && tiles.get(XYtoIndex(nullX, nullY)) == null)
            {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }
        }
        return false;
    }

    public boolean resolved()
    {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++)
        {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y)
    {
        return y + x * NUM_TILES;
    }

    protected void swapTiles(int i, int j)
    {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours()
    {
        ArrayList<PuzzleBoard> p = new ArrayList<PuzzleBoard>();

        int posofnull = 0;
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++)
        {
            if (tiles.get(i) == null)
                posofnull = i;
        }
        int nullX = posofnull / NUM_TILES;
        int nullY = posofnull % NUM_TILES;
        for (int[] delta : NEIGHBOUR_COORDS)
        {
            int neigX = nullX + delta[0];
            int neigY = nullY + delta[1];
            if (neigX >= 0 && neigX < NUM_TILES && neigY >= 0 && neigY < NUM_TILES)
            {
                PuzzleBoard movedBoard = new PuzzleBoard(this);
                movedBoard.tryMoving(neigX, neigY);
                p.add(movedBoard);
            }
        }
        return p;
    }

    public int priority()
    {
        int manhattanprio = 0;
        for (int i = 0; i < NUM_TILES; i++)
        {
            PuzzleTile tile = tiles.get(i);
            if (tile != null)
            {
                int destpos = tile.getNumber();
                int destX = destpos / NUM_TILES;
                int destY = destpos % NUM_TILES;
                manhattanprio += Math.abs(destX - i / NUM_TILES) + Math.abs(destY - i % NUM_TILES);
            }
        }
        return manhattanprio + steps;
    }
}