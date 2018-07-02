package asdfasf.zoomgame;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.*;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    int stage;
    LinearLayout[] layouts=new LinearLayout[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        stage=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        float textSize=(size.x<size.y)?size.y/25:size.x/25;
        int[] ids = {R.id.textNum1,R.id.textNum2,R.id.textNum3,R.id.textNum4,R.id.textNum5};
        for (int i=0;i<ids.length;i++) {
            TextView textView = findViewById(ids[i]);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
            textView.setTextColor(Color.BLACK);
        }

        int[] a={R.id.Tut1,R.id.Tut2,R.id.Tut3,R.id.Tut4,R.id.Tut5};
        for (int i=0;i<a.length;i++) {
            layouts[i]=findViewById(a[i]);
            layouts[i].setVisibility(View.GONE);
        }
        layouts[0].setVisibility(View.VISIBLE);

        ConstraintLayout c = findViewById(R.id.HelpBack);
        c.setBackgroundColor(Color.WHITE);

        c.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (stage==4) {
                    end(v);
                }
                else{
                    layouts[stage].setVisibility(View.GONE);
                    stage++;
                    layouts[stage].setVisibility(View.VISIBLE);
                }
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void end(android.view.View view){
        finish();
    }
}
