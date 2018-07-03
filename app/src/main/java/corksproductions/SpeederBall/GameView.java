package corksproductions.SpeederBall;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static corksproductions.SpeederBall.MainActivity.prefName;

/**
 * Created by Thomas on 11/15/2017.
 */

public class GameView extends SurfaceView implements Runnable {
    volatile boolean playing;

    private Thread gameThread = null;
    private Context mContext;
    private boolean inProgress = true;


    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private int max_x;
    private int max_y;
    private float unit;
    private int distanceTraveled;
    private Random rand;
    private RectF exit;
    private float gameSpeed;



    public GameView(Context context, int screenX, int screenY) {
        super(context);
        mContext=context;
        surfaceHolder = getHolder();
        paint = new Paint();
        rand=new Random();

        max_x=screenX;
        max_y=screenY;

        unit=(screenX+screenY)/80;
        exit=new RectF(max_x/2-8*unit,max_y/2-4*unit,max_x/2+8*unit,max_y/2+4*unit);

        gameSpeed=3*unit/8f;
        playerInit();
        obstacleInit();
        healthInit();
        powerupsInit();

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(2.5f*unit);

    }

    @Override
    public void run() {
        while (playing) { //This is the main loop for the game
            music(); //Checks if music is playing and starts the music if it isn't

            update(); //Move all objects

            draw(); //Actually draw them

            sleep(); //pause for a few millisecs before starting next frame
        }
    }
    private void update(){
        if (inProgress&&hitPoints<0){
            lost();
        }
        updatePlayer();
        int dy = (int) gameSpeed;
        if (slowTime>0) dy/=2;
        moveThings(dy);
        if (inProgress)collisions();

    }
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas(); //Draw things here
            canvas.drawColor(Color.WHITE);
            drawPlayer();
            drawPowerups();
            drawBlocks();
            if (slowTime>0){
                canvas.drawARGB(50,160,250,255);
                slowTime--;
            }
            if (hitPoints>=0) {
                drawHealth();
                paint.setColor(Color.BLACK);
                canvas.drawText("Distance: "+distanceTraveled, max_x/2,15*max_y/16,paint);
            }
            else {drawEndWords();}

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

    private void music(){
        if (!GameActivity.music.isPlaying()) GameActivity.music.start();
    }

    private static void playSound(int a){ //hit=0, power-up=1, win=2, lose=3
        if (GameActivity.mediaArray.get(a).isPlaying()) {
            GameActivity.mediaArray.get(a).stop();
            try {
                GameActivity.mediaArray.get(a).prepare();
            } catch (Exception e) {
            }
        }
        GameActivity.mediaArray.get(a).start();
    }

    private boolean touchMove;

    private float lastX;
    private float lastY;
    public boolean onTouchEvent(MotionEvent motionEvent) { //These are the touch sensores
        int x = (int) motionEvent.getX(); //These get the touch locations
        int y = (int) motionEvent.getY();
        if (inProgress) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) { //a switch block for different ways they can touch
                case MotionEvent.ACTION_DOWN://just pressing down
                    touchMove = true;
                case MotionEvent.ACTION_MOVE://dragging finger
                    lastX=x;
                    lastY=y;
                    break;
                case MotionEvent.ACTION_UP://letting go
                    touchMove=false;
            }
        } else{
            if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                if (exit.contains(x,y)) {
                    if (GameActivity.music.isPlaying()) GameActivity.music.stop();
                    leave();
                }
            }
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
    private float gameSpeedModifier = 12f;
    private void moveThings(int dy){
        moveBlocks(dy);
        movePows(dy);
        obstacleBound-=dy;
        if (obstacleBound<=0){
            generateObstacles(-max_y);
            obstacleBound+=2*max_y;
            gameSpeed+=unit/gameSpeedModifier;
            gameSpeedModifier+=4;
        }
        if (inProgress) distanceTraveled+=dy;
    }
    public void leave(){
        ((Activity) mContext).finish();
    }

    private int hitPoints = 2;

    private void collisions(){
        if (justGotHit<=0 && invisiblePower<=0) for(int i=0; i<4;i++) if (blocks.get(i).intersect(pRect)) hit();
        for(int i=0; i<powerups.size(); i++) if (powerups.get(i).intersect(pRect)) {
            gotPowerUp(powerups.get(i).getType());
            powerups.remove(i);
            i--;
        }
    }
    private void hit(){
        playSound(0);
        hitPoints--;
        System.out.println(invisiblePower+" "+justGotHit);
        justGotHit+=810;
    }
    private int blockNum = 8;
    private int numOfPowerUps = 2;
    private int obstacleBound;

    private void generateObstacles(int bottomBound){ //dif in bounds is 2*max_y
        //Block generation
        int blockBottom = bottomBound;
        int dBottom = 2*max_y/blockNum;
        for (int i =0; i<blockNum; i++){
            blocks.add(new Block(0+rand.nextInt(max_x/2),(max_x/2)+(rand.nextInt((int) (max_x/2-5*pRadius))), blockBottom,2*unit));
            blockBottom-=dBottom;
        }

        //Powerup generation
        for (int i=0;i<numOfPowerUps;i++){
            powerups.add(new Powerups(powerupRadius+rand.nextInt((int) (max_x-powerupRadius*2)),
                    bottomBound - rand.nextInt(2*max_y), powerupRadius,rand.nextInt(Powerups.POWERUP_TYPES)));
        }

    }

    private void gotPowerUp(int type){
        playSound(1);
        switch(type){
            case 0: //invisible
                invisiblePower=100;
                break;
            case 1: //heal
                if (hitPoints<2) hitPoints++;
                break;
            case 2: //slow
                slowTime=70;
                break;
        }
    }

    //Player Ball Stuff
    private float pX;
    private float pY;
    private float pVx;
    private float pVy;
    private float pRadius;
    private float pVFriction=0.85f;
    private float pVConstant=0.8f;
    private RectF pRect;
    private int justGotHit;
    private int invisiblePower;
    private int slowTime;

    private void playerInit(){
        pX=max_x/2;
        pY=max_y*3/4;
        pRadius=unit*1.5f;
        pRect=new RectF(pX-pRadius,pY-pRadius,pX+pRadius,pY+pRadius);
    }

    private void drawPlayer(){
        paint.setColor(Color.BLUE);

        if (invisiblePower>0){
            paint.setAlpha(120);
            invisiblePower--;
            if(invisiblePower<2) justGotHit+=810;
        }
        if (justGotHit>0){
            paint.setAlpha(Math.abs(((justGotHit%510)-255)/2));
            justGotHit-=30;
        }
        canvas.drawCircle(pX,pY,pRadius,paint);
    }
    private void updatePlayer(){
        if ((pX<pRadius&&pVx<0) || (pX>max_x-pRadius&&pVx>0)) {pVx*=-0.05f;}
        if ((pY<pRadius&&pVy<0) || (pY>max_y-pRadius&&pVy>0)) {pVy*=-0.05f;}
        if (touchMove){
            pVx=(lastX-pX)*pVConstant;
            pVy=(lastY-pY)*pVConstant;
        }
        else {pVx*=pVFriction; pVy=pVFriction;}
        pX+=pVx;
        pY+=pVy;
        pRect.offset(pVx,pVy);
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

    private void moveBlocks(float dy){
        for (Block block : blocks)block.goDown(dy);
        if (blocks.get(0).below(max_y)) blocks.remove(0);
    }

    //powerups
    private ArrayList<Powerups>  powerups = new ArrayList<>();
    private float powerupRadius;
    private void drawPowerups(){
        for (Powerups p : powerups) p.draw(canvas,paint);
    }

    private void movePows(float dy){
        for (Powerups powerUp : powerups) powerUp.goDown(dy);
        if (powerups.size()>0 && powerups.get(0).below(max_y)) powerups.remove(0);
    }

    private void powerupsInit(){
        powerupRadius=unit;
    }
    //The powerups are created in generate obstacles

    //Health bar
    private RectF[] healthBars = new RectF[3];
    private int[] colors = {Color.RED,Color.YELLOW,Color.GREEN};
    private void healthInit(){
        healthBars[0] = new RectF(max_x/16,max_y/16,5*max_x/16,max_y/8);
        healthBars[1] = new RectF(max_x/16,max_y/16,10*max_x/16,max_y/8);
        healthBars[2] = new RectF(max_x/16,max_y/16,15*max_x/16,max_y/8);
    }
    private void drawHealth(){
        int health = hitPoints>2?2:hitPoints;
        paint.setColor(colors[health]);
        canvas.drawRect(healthBars[health],paint);
    }

    //Other things
    String endText= "";
    private void drawEndWords(){
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(exit,paint);
        paint.setColor(Color.BLACK);

        canvas.drawText("Main Menu",max_x/2,max_y/2+max_y/64,paint);
        canvas.drawText(endText,max_x/2,exit.top-max_y/8,paint);
        canvas.drawText("Score: "+distanceTraveled,max_x/2,exit.bottom+max_y/8,paint);
    }

    public void lost(){
        inProgress=false;
        int a = updateHighScores(distanceTraveled);
        if (a==0){
            endText= "Game Over";
            playSound(3);
        }
        else{
            endText = GameActivity.wordPlace(a)+" Place!";
            playSound(2);
        }
    }
    public int updateHighScores(int a){
        SharedPreferences.Editor editor = mContext.getSharedPreferences(prefName,MODE_PRIVATE).edit();
        SharedPreferences prefs = mContext.getSharedPreferences(prefName,MODE_PRIVATE);
        ArrayList<Integer> scores = new ArrayList<>();
        int added=0;
        for(int i = 1; i<=10;i++){
            scores.add(prefs.getInt("place"+i,0));
            if (added==0&&a>scores.get(i-1)){
                added=i;
                scores.add(i-1,a);
            }
        }
        for (int i=1; i<=10;i++){
            editor.putInt("place"+i,scores.get(i-1));
        }
        editor.commit();
        return added;
    }

}