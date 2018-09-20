package com.example.pulkit.dropcrop;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView imgView;
    Button buttonview;
    Bitmap bitmap;
    Bitmap img;
    public final static String APP_PATH = "/saved_images/";
    public final static String APP_THUMBNAIL_PATH ="Ship";
    Uri uri;
    static ImageLoader imageLoader;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView)findViewById(R.id.img);
        buttonview = (Button)findViewById(R.id.button);

//      progress bar set up--------->
        dialog=new ProgressDialog(this);
        dialog.setTitle("Wait");
        dialog.setMessage("Image is loading");
        dialog.setCancelable(false);

        dialog.show();

//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.image1);

        imageLoader=ImageLoader.getInstance();
        ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(this).build();
        imageLoader.init(config);

//        imgView.setImageResource(R.mipmap.image1);

//        uri = Uri.parse("android.resource://com.example.pulkit.dropcrop/drawable/pic");
//        uri = getUriToDrawable(this,R.mipmap.image1);

        imageLoader.loadImage("https://wallpaperbrowse.com/media/images/303836.jpg",new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
//                uri=saveImage(loadedImage);
                imgView.setImageBitmap(loadedImage);
                bitmap=loadedImage;
//                uri=Uri.parse(imageUri);
                Toast.makeText(MainActivity.this,"Image loaded",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
                Toast.makeText(MainActivity.this,"Cancelled!!",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                Toast.makeText(MainActivity.this,"failed!!",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


//        Picasso.with(this)
//                .load(R.mipmap.image1)
////                .transform(new ScaleToFitWidhtHeigthTransform(imgView.getWidth(),false))
//                .into(imgView);

        imgView.setDrawingCacheEnabled(true);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                100);

        buttonview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(MainActivity.this);
//                img=loadBitmapFromView(imgView);
//                if(saveImage(img))
//                Toast.makeText(MainActivity.this, "Image Cropped and Stored", Toast.LENGTH_SHORT).show();
//                else
//                Toast.makeText(MainActivity.this, "Image not stored: Error", Toast.LENGTH_SHORT).show();

//                performCrop(uri);

                Uri u=getImageUri(MainActivity.this,bitmap);
                CropImage.activity(u).start(MainActivity.this);
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public boolean saveImage(Bitmap image) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()+APP_PATH+APP_THUMBNAIL_PATH;
        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Log.e("saveImage()","after mkdirs()");
            OutputStream fOut = null;
            File file = new File(fullPath, "Ship.png");
            file.createNewFile();

            Log.e("saveImage()","after createNewFile()");
            fOut = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            Log.e("saveImage()","after compress()");
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

            return true;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return false;
        }
    }

//    private Uri saveImage(Bitmap bitmap) {
//        FileOutputStream outputStream;
//        try {
//            outputStream = openFileOutput("pic", Context.MODE_PRIVATE);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            outputStream.close();
//
//            File directory = getFilesDir();
//            File file = new File(directory, "pic");
//            return Uri.fromFile(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static Bitmap loadBitmapFromView(View v)
    {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    public class ScaleToFitWidhtHeigthTransform implements com.squareup.picasso.Transformation {

        int mSize;
        boolean isHeightScale;

        public ScaleToFitWidhtHeigthTransform(int size, boolean isHeightScale) {
            mSize = size;
            this.isHeightScale = isHeightScale;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            float scale;
            int newSize;
            Bitmap scaleBitmap;
            if (isHeightScale) {
                scale = (float) mSize / source.getHeight();
                newSize = Math.round(source.getWidth() * scale);
                scaleBitmap = Bitmap.createScaledBitmap(source, newSize, mSize, true);
            } else {
                scale = (float) mSize / source.getWidth();
                newSize = Math.round(source.getHeight() * scale);
                scaleBitmap = Bitmap.createScaledBitmap(source, mSize, newSize, true);
            }
            if (scaleBitmap != source) {
                source.recycle();
            }

            return scaleBitmap;
        }
        @Override
        public String key() {
            return "scaleRespectRatio" + Integer.toString(mSize) + Boolean.toString(isHeightScale);
        }
    }

//  perform crop--------------------------------->
    private void performCrop(Uri contentUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
//            cropIntent.putExtra("aspectX", 5);
//            cropIntent.putExtra("aspectY", 3);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 480);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 10);
        }
        // respond to users whose devices do not support the crop action
        catch (Exception anfe) {
            Log.d("my","Error: "+anfe.getMessage());
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode==RESULT_OK && resultCode==10 && data!=null){
//            Bundle extras = data.getExtras();
//            assert extras != null;
//            Bitmap bitmap = extras.getParcelable("data");
//            Uri uri=saveImage(bitmap);
//            Picasso.with(this).load(uri).into(imgView);
//        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    if(saveImage(bitmap)) Toast.makeText(this,"Image saved",Toast.LENGTH_SHORT).show();
                    else Toast.makeText(this,"Image not saved",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static final Uri getUriToDrawable(@NonNull Context context,
                                             @AnyRes int drawableId) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId) );
        return imageUri;
    }
}


