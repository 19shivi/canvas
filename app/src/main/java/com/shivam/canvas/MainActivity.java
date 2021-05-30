package com.shivam.canvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
 CanvasView canvasView;
    ImageView imageView;
 private AlertDialog builder;
    SeekBar seekBar_alpha;
    SeekBar seekBar_red;
    SeekBar seekBar_green;
    SeekBar seekBar_blue;
    View showColor;
    FloatingActionButton color_fab;
    FloatingActionButton pencil_fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        canvasView=findViewById(R.id.canvas);
        canvasView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        color_fab=findViewById(R.id.fab_color);
        pencil_fab=findViewById(R.id.fab_pencil);
        color_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorDialog();
            }
        });
        pencil_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWidthDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater menuInflater=getMenuInflater();
      menuInflater.inflate(R.menu.menu,menu);
      return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.undo)
           canvasView.onClickUndo();
       else if(item.getItemId()==R.id.redo)
           canvasView.onClickRedo();
        else if(item.getItemId()==R.id.clear)
            canvasView.clear();
        else if(item.getItemId()==R.id.erase)
            canvasView.setErase(true);
        else if(item.getItemId()==R.id.save) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                int PERMISSION_REQUEST_CODE = 1;
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE);
                    }
                } else
                    canvasView.saveImages();






        }
        }
       return  true;
    }
    void showWidthDialog()
    {
        Context context;
        builder=new AlertDialog.Builder(this).create();
        LayoutInflater inflater = null;
        ViewGroup root;
        canvasView.setErase(false);
        View view = getLayoutInflater().inflate(R.layout.width_dialog, null);
        SeekBar seekBar=view.findViewById(R.id.seekBar);
        Button setWidth=view.findViewById(R.id.button);
        imageView=view.findViewById(R.id.imageView);
        seekBar.setProgress((int) canvasView.get_width());
        setWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  canvasView.set_width(seekBar.getProgress());
                  builder.dismiss();
            }
        });
        Bitmap bitmap= Bitmap.createBitmap(400,100,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        Paint p=new Paint();
        p.setColor(canvasView.getColor());
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(canvasView.get_width());
        bitmap.eraseColor(Color.WHITE);
        canvas.drawLine(30,50,370,50,p);
        imageView.setImageBitmap(bitmap);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        builder.setView(view);

        builder.show();
    }

    void showColorDialog()
    {

        builder=new AlertDialog.Builder(this).create();

        View view = getLayoutInflater().inflate(R.layout.color_dialog, null);
         seekBar_alpha=view.findViewById(R.id.alpha_bar);
         seekBar_red=view.findViewById(R.id.red_bar);
         seekBar_green=view.findViewById(R.id.green_bar);
         seekBar_blue=view.findViewById(R.id.blue_bar);
         showColor=view.findViewById(R.id.color_show);
        Button setColor=view.findViewById(R.id.set_color);
        imageView=view.findViewById(R.id.imageView);
        setColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasView.setColor(Color.argb(seekBar_alpha.getProgress(),seekBar_red.getProgress(),seekBar_green.getProgress(),seekBar_blue.getProgress()));
            builder.dismiss();
            }
        });
        int color=canvasView.getColor();
        seekBar_alpha.setProgress(Color.alpha(color));
        seekBar_red.setProgress(Color.red(color));
        seekBar_green.setProgress(Color.green(color));
        seekBar_blue.setProgress(Color.blue(color));
        seekBar_alpha.setOnSeekBarChangeListener(seekBarChangeListener_color);
        seekBar_red.setOnSeekBarChangeListener(seekBarChangeListener_color);
        seekBar_green.setOnSeekBarChangeListener(seekBarChangeListener_color);
        seekBar_blue.setOnSeekBarChangeListener(seekBarChangeListener_color);
        builder.setView(view);
        builder.create();
        builder.show();
    }
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener=new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap= Bitmap.createBitmap(400,100,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

         Paint p=new Paint();
         p.setColor(canvasView.getColor());
         p.setStrokeCap(Paint.Cap.ROUND);
         p.setStrokeWidth(i);
         bitmap.eraseColor(Color.WHITE);
         canvas.drawLine(30,50,370,50,p);
         imageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener_color=new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            showColor.setBackgroundColor(Color.argb(seekBar_alpha.getProgress(),seekBar_red.getProgress(),seekBar_green.getProgress(),seekBar_blue.getProgress()));

        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            canvasView.saveImages();
        }
    }
}