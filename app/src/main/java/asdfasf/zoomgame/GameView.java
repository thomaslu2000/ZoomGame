package asdfasf.zoomgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Thomas on 11/15/2017.
 */

public class GameView extends SurfaceView implements Runnable {
    volatile boolean playing;

    private Thread gameThread = null;
    private Context mContext;


    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private int max_x;
    private int max_y;
    private float unit;


    public GameView(Context context, int screenX, int screenY) {
        super(context);
        mContext=context;
        surfaceHolder = getHolder();
        paint = new Paint();


        max_x=screenX;
        max_y=screenY;

        unit=(screenX+screenY)/80;
        playerInit();
        brakeInit();
        blocks.add(new Block(0,500,500,200));

    }

    @Override
    public void run() {
        while (playing) { //This is the main loop for the game
            update(); //Move all objects

            draw(); //Actually draw them

            sleep(); //pause for a few millisecs before starting next frame
        }
    }

    private void update(){
        updatePlayer();
        moveBlocks(1);
    }
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas(); //You have to do this whenever you want to draw
            canvas.drawColor(Color.WHITE); //Just the background is now white
            //drawing in here
            drawPlayer();
            drawBlocks();
            drawBrake();
            surfaceHolder.unlockCanvasAndPost(canvas); //When you finished drawing the frame, you have to do this to save the changes
        }
    }

    private void sleep(){
        try {
            gameThread.sleep(17); //This is how long of a wait b/w frames
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) { //These are the touch sensores
        int x = (int) motionEvent.getX(); //These get the touch locations
        int y = (int) motionEvent.getY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) { //a switch block for different ways they can touch
            case MotionEvent.ACTION_DOWN://just pressing down
            case MotionEvent.ACTION_MOVE://dragging finger
                if (brakeButton.contains(x,y)) brakes=true;
                else pVx+=(x-pX)/100;
                break;
            case MotionEvent.ACTION_UP://letting go
                if (brakes) brakes=false;


        }
        return true;
    }


    public void pause() {
        playing = false; //paused-->not playing
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true; //resumed --> playing
        gameThread = new Thread(this);
        gameThread.start();
    }

    //Player Ball Stuff
    private float pX;
    private float pY;
    private float pVx;
    private float pRadius;
    private float pVxFriction=0.95f;
    private RectF pRect;
    private boolean brakes=false;

    private void playerInit(){
        pX=max_x/2;
        pY=max_y*3/4;
        pRadius=unit;
        pRect=new RectF(pX-pRadius,pY-pRadius,pX+pRadius,pY+pRadius);
    }

    private void drawPlayer(){
        paint.setColor(Color.BLUE);
        canvas.drawCircle(pX,pY,pRadius,paint);
    }
    private void updatePlayer(){
        if ((pX<pRadius&&pVx<0) || (pX>max_x-pRadius&&pVx>0)) pVx=-pVx;
        pX+=pVx;
        pRect.offset(pVx,0);
        pVx*=pVxFriction;
        if (brakes) pVx*=0.85f;
    }

    private void collision(Block block){

    }

    //Brake Button
    private RectF brakeButton;
    private void brakeInit(){
        brakeButton=new RectF(0,max_y*7/8,max_x,max_y);
    }
    private void drawBrake(){
        paint.setColor((brakes? Color.GRAY : Color.LTGRAY));
        canvas.drawRect(brakeButton,paint);
    }

    //Block Stuff
    private ArrayList<Block>  blocks = new ArrayList<>();
    private void drawBlocks(){
        paint.setColor(Color.MAGENTA);
        for (Block block : blocks) block.draw(canvas,paint);
    }
    private void moveBlocks(float dy){
        for (Block block : blocks) block.goDown(dy);
    }
}