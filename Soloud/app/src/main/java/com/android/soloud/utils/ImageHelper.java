package com.android.soloud.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by f.stamopoulos on 12/2/2017.
 */

public class ImageHelper {

    private Context context;

    public ImageHelper(Context context) {
        this.context = context;
    }

    public Bitmap getBitmapFromUri(Uri pickedImage){
        //Uri pickedImage = data.getData();
        // Let's read picked image path using content resolver
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(pickedImage, filePath, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            if ( bitmap == null){
                return BitmapFactory.decodeFile(Uri.parse(imagePath).getPath());
            }
            cursor.close();
            return bitmap;
        }
        return null;
    }


    public Bitmap getResizedImage(Bitmap bitmap) {
        int scaleSize = 960;
        Bitmap resizedBitmap = null;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int newWidth = -1;
        int newHeight = -1;
        float multFactor = -1.0F;
        if(originalHeight > originalWidth) {
            newHeight = scaleSize ;
            multFactor = (float) originalWidth/(float) originalHeight;
            newWidth = (int) (newHeight*multFactor);
        } else if(originalWidth > originalHeight) {
            newWidth = scaleSize ;
            multFactor = (float) originalHeight/ (float)originalWidth;
            newHeight = (int) (newWidth*multFactor);
        } else if(originalHeight == originalWidth) {
            newHeight = scaleSize ;
            newWidth = scaleSize ;
        }
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        return resizedBitmap;
    }

    public File saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        // Create imageDir
        File photoPath = new File(directory, randomUUIDString + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(photoPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return photoPath;
    }

    public void deleteImageFromInternalStorage(String fileName){
        // path to /data/data/yourapp/app_data/imageDir
        ContextWrapper cw = new ContextWrapper(context);
        File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        //File dir = context.getFilesDir();
        File file = new File(dir, fileName);
        boolean deleted = file.delete();
    }
}
