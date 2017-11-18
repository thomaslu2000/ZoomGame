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
import java.util.Random;

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
    private int distanceTraveled;
    private Random rand;


    public GameView(Context context, int screenX, int screenY) {
        super(context);
        mContext=context;
        surfaceHolder = getHolder();
        paint = new Paint();
        rand=new Random();

        max_x=screenX;
        max_y=screenY;

        unit=(screenX+screenY)/80;
        playerInit();
        obstacleInit();

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
        moveThings(10);
    }
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas(); //You have to do this whenever you want to draw
            canvas.drawColor(Color.WHITE); //Background is white
            //drawing in here
            drawPlayer();
            drawBlocks();

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

    private boolean touchMove;
    private int lastX;
    public boolean onTouchEvent(MotionEvent motionEvent) { //These are the touch sensores
        int x = (int) motionEvent.getX(); //These get the touch locations
        int y = (int) motionEvent.getY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) { //a switch block for different ways they can touch
            case MotionEvent.ACTION_DOWN://just pressing down
                touchMove = true;
            case MotionEvent.ACTION_MOVE://dragging finger
                lastX=x;
                break;
            case MotionEvent.ACTION_UP://letting go
                touchMove=false;


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

    //General Functions
    private void moveThings(int dy){
        moveBlocks(dy);
        obstacleBound-=dy;
        if (obstacleBound<=0){
            generateObstacles(-max_y);
            obstacleBound+=2*max_y;
        }
        distanceTraveled+=dy;
    }


    //Player Ball Stuff
    private float pX;
    private float pY;
    private float pVx;
    private float pRadius;
    private float pVxFriction=0.85f;
    private float pVxConstant;
    private RectF pRect;

    private void playerInit(){
        pX=max_x/2;
        pY=max_y*3/4;
        pRadius=unit*1.5f;
        pRect=new RectF(pX-pRadius,pY-pRadius,pX+pRadius,pY+pRadius);
        pVxConstant = unit/10f;
    }

    private void drawPlayer(){
        paint.setColor(Color.BLUE);
        canvas.drawCircle(pX,pY,pRadius,paint);
    }
    private void updatePlayer(){
        if ((pX<pRadius&&pVx<0) || (pX>max_x-pRadius&&pVx>0)) {pVx*=-0.1f;}
        pX+=pVx;
        pRect.offset(pVx,0);
        if (touchMove) pVx+=(lastX-pX>0)? pVxConstant : -pVxConstant;
        else pVx*=pVxFriction;
    }

    private void collision(Block block){

    }

    //Block Stuff
    private void obstacleInit(){
        obstacleBound=max_y;
        generateObstacles(0);
    }

    private ArrayList<Block>  blocks = new ArrayList<>();
    private void drawBlocks(){
        paint.setColor(Color.MAGENTA);
        for (Block block : blocks) block.draw(canvas,paint);
    }
    private int blockNum = 8;
    private int obstacleBound;

    private void generateObstacles(int bottomBound){ //dif in bound is 2*max_y
        int blockBottom = bottomBound;
        int dBottom = 2*max_y/blockNum;
        for (int i =0; i<blockNum; i++){
            blocks.add(new Block(0+rand.nextInt(max_x/2),((int) max_x/2)+(rand.nextInt((int) (max_x/2-3*pRadius))), blockBottom,2*unit));
            blockBottom-=dBottom;
        }
    }
    private void moveBlocks(float dy){
        for (Block block : blocks)block.goDown(dy);
        if (blocks.get(0).below(max_y)) blocks.remove(0);
    }
}