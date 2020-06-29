package androis.myapp.filter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;

public class MainActivity extends AppCompatActivity {

    private ImageView image;
    private   Bitmap  img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.image);
    }

    public void apply(Transformation<Bitmap> filtertype){
//        Bitmap img = ((BitmapDrawable) image.getDrawable()).getBitmap();

        Glide.with(getApplicationContext()).load(img).apply(RequestOptions.bitmapTransform(filtertype)).into(image);
    }

    public void choose(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent,100);
    }

    public void Save(View view) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);


        }else {

            Bitmap img = ((BitmapDrawable) image.getDrawable()).getBitmap();
            String imageurl = MediaStore.Images.Media.insertImage(getContentResolver(), img, "filter", "image filter");
            Toast.makeText(this, "saved image at/" + imageurl, Toast.LENGTH_SHORT).show();

        }



    }

    public void sepia(View view) {
        apply(new SepiaFilterTransformation());
    }

    public void sketch(View view) {
        apply(new SketchFilterTransformation());
    }

    public void blur(View view) {
        apply(new BlurTransformation());
    }

    public void pixel(View view) {
        apply(new PixelationFilterTransformation());
    }

    public void gray(View view) {
        apply(new GrayscaleTransformation());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == 100 && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            try {
                ParcelFileDescriptor  parcelfiledes = getContentResolver().openFileDescriptor(uri,"r");
                FileDescriptor  file =  parcelfiledes.getFileDescriptor();
                img = BitmapFactory.decodeFileDescriptor(file);
                image.setImageBitmap(img);
                parcelfiledes.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
