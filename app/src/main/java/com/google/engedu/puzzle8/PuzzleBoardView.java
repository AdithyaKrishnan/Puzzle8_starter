package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View
{
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context)
    {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap)
    {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (puzzleBoard != null)
        {
            if (animation != null && animation.size() > 0)
            {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0)
                {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    this.postInvalidateDelayed(500);
                }
            }
            else
            {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle()
    {
        if (animation == null && puzzleBoard != null)
        {
            for(int i=0;i<5;i++) {
                ArrayList<PuzzleBoard> niegh = puzzleBoard.neighbours();
                puzzleBoard = niegh.get(random.nextInt(niegh.size()));
                puzzleBoard.reset();
                invalidate();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }
    public class boardcomparator implements Comparator<PuzzleBoard>
    {

        public int compare(PuzzleBoard b1, PuzzleBoard b2)
        {
            return b1.priority() - b2.priority();
        }

    }

    public void solve()
    {
        Comparator<PuzzleBoard> comparator = new boardcomparator();
        ArrayList<PuzzleBoard> visited=new  ArrayList<PuzzleBoard>();
        PriorityQueue<PuzzleBoard> queue=new PriorityQueue<PuzzleBoard>(10,comparator);
        puzzleBoard.setPreviousBoard(null);
        puzzleBoard.setSteps(0);
        queue.add(puzzleBoard);

        while(!queue.isEmpty())
        {
            PuzzleBoard lowestpriority=queue.poll();
            visited.add(lowestpriority);
            if(!lowestpriority.resolved())
            {
                for(PuzzleBoard p: lowestpriority.neighbours())
                {
                    if(!p.equals(lowestpriority.getPreviousBoard())&&!(visited.contains(p)));
                    queue.add(p);
                }
            }
            else
            {
                ArrayList<PuzzleBoard> sequence=new ArrayList<PuzzleBoard>();
                sequence.add(lowestpriority);
                while(lowestpriority.getPreviousBoard()!=null)
                {
                    lowestpriority=lowestpriority.getPreviousBoard();
                    sequence.add(lowestpriority);
                }

                sequence.remove(sequence.size() - 1);
                Collections.reverse(sequence);
                animation=sequence;
                invalidate();
                return;
            }
        }
    }
}
