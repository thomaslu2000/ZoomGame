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
    private float x;
    private float y; //These are so the object will actually remember their data.
    private float radius; // They have to be outside a function because of those environment diagrams we did in class
    private int type; //0 is going invisible, 1 is heal
    public Powerups(float cx, float cy, float radius, int type) {
        x=cx;
        y=cy;
        this.radius=radius;
        this.type=type;
        hitBox = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);
    }

    public void draw(Canvas canvas, Paint paint){ //Since we know its coordinates, we don't need to take that in
        switch (type) {
            case 0: //invisible
                paint.setColor(Color.CYAN);
                paint.setAlpha(150);
                canvas.drawCircle(x, y, radius, paint);
                paint.setARGB(100, 100, 150, 230);
                canvas.drawCircle(x - radius / 2, y, radius / 2, paint);
                canvas.drawCircle(x + radius / 2, y, radius / 2, paint);
                break;

            case 1: //heal
                paint.setColor(Color.GREEN);
                canvas.drawCircle(x, y, radius, paint);
                break;
        }
    }

    public void goDown(float dy){
        y+=dy;
        hitBox.offset(0,dy);
    }

    public boolean below(int y){return hitBox.top>y;}
    public boolean intersect(RectF player){return RectF.intersects(hitBox,player);}
    public int getType(){return type;}


}
