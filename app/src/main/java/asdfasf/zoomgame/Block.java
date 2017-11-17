package asdfasf.zoomgame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by Thomas on 11/15/2017.
 */

public class Block {
    private RectF blockRect;
    public Block(float left, float width, float bottom, float height){
        blockRect=new RectF(left,bottom-height,left+width,bottom);
    }
    public void draw(Canvas canvas, Paint paint){
        canvas.drawRect(blockRect,paint);
    }
    public void goDown(float y){
        blockRect.offset(0,y);
    }
}
