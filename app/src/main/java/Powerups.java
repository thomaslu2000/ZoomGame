package asdfasf.zoomgame;

/**
 * Created by Ankur on 11/25/2017.
 */
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Powerups {
    private RectF hitBox;
    public Powerups(float cx, float cy, float radius, int type){
        hitBox=new RectF (cx-radius, cy-radius, cx+radius, cy+radius);
    }
    public void draw(float x, float y, float radius, int type, Canvas canvas, Paint paint){
        switch (type) {
            case 0: paint.setColor(Color.GRAY);
                    break;
            case 1: paint.setColor(Color.GREEN);
                    break;
            //case 2: //tbd
        }
        canvas.drawCircle(x, y, radius, paint);
    }
    public void goDown(float y){
        hitBox.offset(0,y);
    }
    public boolean below(int y){return hitBox.top>y;}
    public boolean intersect(RectF player){return RectF.intersects(hitBox,player);}


}
