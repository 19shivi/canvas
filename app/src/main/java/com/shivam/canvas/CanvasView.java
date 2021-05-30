package com.shivam.canvas;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CanvasView  extends View {
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private HashMap<Path, Paint> previousPaintMap = new HashMap<>();
    private Bitmap bitmap;
    final String[] downloadURL = new String[1];
    Context context;
    boolean erase=false;
    ProgressDialog progressDialog ;
    StorageReference storageRef;

    public CanvasView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
        setFocusableInTouchMode(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        mCanvas = new Canvas();
        mPath = new Path();
        storageRef = FirebaseStorage.getInstance().getReference();


    }

    public float get_width() {
        return mPaint.getStrokeWidth();
    }

    public int getColor() {
        return mPaint.getColor();
    }

    public void set_width(float f) {
        mPaint.setStrokeWidth(f);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    public void clear() {
        undonePaths.clear();
        paths.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate();
    }
    public void setErase(boolean isErase){
        erase=isErase;
        if(erase) {

            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {


            mPaint.setXfermode(null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //mPath = new Path();
        //canvas.drawPath(mPath, mPaint);
        for (Path p : paths) {
            canvas.drawPath(p, previousPaintMap.get(p));

        }
        canvas.drawPath(mPath, mPaint);

    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        undonePaths.clear();
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        paths.add(mPath);
        Paint paint = new Paint(mPaint);
        previousPaintMap.put(mPath, paint);
        mPath = new Path();

    }

    public void onClickUndo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        } else {

        }

    }

    public void onClickRedo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        } else {

        }

    }

    void saveImages() {

        Bitmap saveBitmap = Bitmap.createBitmap(bitmap);
        Canvas c = new Canvas(saveBitmap);
        c.drawColor(0xFFFFFFFF);
        c.drawBitmap(bitmap,0,0,null);


        Uri uri = null;
        String filename = System.currentTimeMillis() + ".jpeg";


        OutputStream fos = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {




            ContentValues contentValues = new ContentValues();


            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);



            Uri imgUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            uri = imgUri;

            try {
                fos = getContext().getContentResolver().openOutputStream(imgUri);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else {

            String dir = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Canvas";
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File image = new File(fileDir, filename);

            try {
                fos = new FileOutputStream(image);
                uri = Uri.fromFile(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        saveBitmap.recycle();
        try {
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

           uploadfile(uri,filename);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void uploadfile(Uri data, String name) {
        Log.v("shivam", "shivamGupta");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.setTitle("Uploading...");
        progressDialog.setProgress(0);
        progressDialog.show();

        StorageReference storageReference = storageRef.child(name);

        storageReference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //  downloadURL=taskSnapshot.getStorage().getDownloadUrl().toString();
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadURL[0] = uri.toString();

                                progressDialog.dismiss();
                               FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String ,String > file=new HashMap<>();

                                file.put("url",downloadURL[0]);
                                file.put("creator", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                                db.collection("files")
                                        .add(file)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getContext(), "File Successfully uploaded", Toast.LENGTH_LONG).show();
                                                Log.v("shivam", downloadURL[0]);}
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                               // Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("error", e.toString());
                progressDialog.dismiss();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                Log.v("shivam", "shivam3");
                int progress = (int) ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                progressDialog.setProgress(progress);

            }
        });

    }
}