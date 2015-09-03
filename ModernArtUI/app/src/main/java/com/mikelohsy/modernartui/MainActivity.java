package com.mikelohsy.modernartui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.preference.DialogPreference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends Activity{
    private static String TAG = "MAU";

    private SeekBar mSeekBar;
    private ImageView mCenterRect;
    private ArrayList<ColorRectangle> mRects;
    private Random r = new Random(System.currentTimeMillis());

    private String MOMA_URL = "http://www.moma.org";
    private String CHOOSER_TEXT = "Open URL with:";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCenterRect = (ImageView) findViewById(R.id.center);

        mRects = new ArrayList<ColorRectangle>();
        mRects.add(new ColorRectangle((ImageView) findViewById(R.id.rect1)));
        mRects.add(new ColorRectangle((ImageView) findViewById(R.id.rect2)));
        mRects.add(new ColorRectangle((ImageView) findViewById(R.id.rect3)));
        mRects.add(new ColorRectangle((ImageView) findViewById(R.id.rect4)));
        mRects.add(new ColorRectangle((ImageView) findViewById(R.id.rect5)));
        mRects.add(new ColorRectangle((ImageView) findViewById(R.id.rect6)));
        mRects.add(new ColorRectangle((ImageView) findViewById(R.id.rect7)));
        mRects.add(new ColorRectangle((ImageView) findViewById(R.id.rect8)));
        mRects.add(new ColorRectangle((ImageView) findViewById(R.id.rect9)));

        mSeekBar = (SeekBar) findViewById(R.id.seeker);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.i(TAG, "" + progress);
                for(ColorRectangle rect : mRects){
                    rect.setRectangleColor(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.more_information) {
            //Toast.makeText(getApplicationContext(), "More information", Toast.LENGTH_SHORT).show();
            fireDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fireDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.dialog_layout, null))
            .setNegativeButton(R.string.moma_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(MainActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                }
            })
            .setPositiveButton(R.string.moma_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(MainActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();

                    Intent baseIntent = new Intent(Intent.ACTION_VIEW);
                    baseIntent.setData(Uri.parse(MOMA_URL));

                    Intent chooserIntent = Intent.createChooser(baseIntent, CHOOSER_TEXT);
                    startActivity(chooserIntent);
                }
            }).create().show();
    }

    class ColorRectangle {
        ImageView mView;
        int mStartR, mStartG, mStartB;
        int mEndR, mEndG, mEndB;
        double stepR, stepG, stepB;
        boolean isGray;

        ColorRectangle(ImageView imageView){
            mView = imageView;
            initColors();
        }

        public void setRectangleColor (int progressBarValue) {
            if(!isGray)
                mView.setBackgroundColor(getColor(progressBarValue));
        }

        private void initColors () {

            mStartR = r.nextInt(256);
            mStartG = r.nextInt(256);
            mStartB = r.nextInt(256);
            mEndR = r.nextInt(256);
            mEndG = r.nextInt(256);
            mEndB = r.nextInt(256);
            stepR = ((double)(mEndR - mStartR))/100.0;
            stepG = ((double)(mEndG - mStartG))/100.0;
            stepB = ((double)(mEndB - mStartB))/100.0;

            mView.setBackgroundColor(getColor(0));

            // lt gray is #ffcccccc
            // gray is #ff888888
            // dark gray is #ff444444
            //#44 = 68
            //#cc = 204
            //r,g,b values closet to each other

            double mean = getMean(mStartR, mStartG, mStartB);
            double sd = getMean(mStartR, mStartG, mStartB);

            isGray = (mean<= 204 && mean >= 68 && sd <= 4.2);
        }

        private double getMean (int red, int blue, int green){
            double sum = red + blue + green;
            return sum/3.0;
        }

        private double getSD (int red, int blue, int green){
            double mean = getMean(red, blue, green);
            double sum = (mean - red)*(mean - red) +
                    (mean - blue)*(mean - blue) +
                    (mean - green)*(mean - green);
            double sd = Math.sqrt(sum/(3.0-1.0));
            return sd;
        }

        private int getColor (int progressBarValue) {
            return Color.rgb(mStartR + (int) (stepR * progressBarValue),
                    mStartG + (int) (stepG * progressBarValue),
                    mStartB + (int) (stepB * progressBarValue));
        }
    }
}
