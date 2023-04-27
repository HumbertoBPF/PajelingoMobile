package com.example.pajelingo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Files {
    /**
     * Gets a Bitmap from a base 64 string. The Bitmap instance can be used, for instance, to
     * display or save the picture as a file.
     * @param base64String base 64 string encoding a picture.
     * @return a Bitmap instance representing the encoded picture.
     */
    public static Bitmap getPictureFromBase64String(String base64String){
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }

    /**
     * Deletes the content of the specified folder, that is its sub folders and files.
     * @param file File instance corresponding to the file whose content must be deleted.
     */
    public static void clearFolder(File file){
        // Specified file can't be null
        if (file == null){
            return;
        }

        File[] subFiles = file.listFiles();
        // Check if the file has subFiles
        if (subFiles != null) {
            for (File subFile: subFiles){
                clearFolder(subFile);
                subFile.delete();
            }
        }
    }

    public static void saveImage(Context context, String base64String, String path) throws IOException {
        String[] subPaths = path.split("/");

        int nbPaths = subPaths.length;
        File parentFile = context.getFilesDir();

        for (int i = 0;i < nbPaths-1;i++){
            String subDirectory = subPaths[i];
            File file = new File(parentFile, subDirectory);
            file.mkdir();
            parentFile = file;
        }

        String filename = subPaths[nbPaths - 1];

        File imageFile = new File(parentFile,filename);

        FileOutputStream fos = new FileOutputStream(imageFile);
        // Use the compress method on the BitMap object to write image to the OutputStream
        getPictureFromBase64String(base64String).compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
    }

    public static void setImageResourceFromFile(ImageView imageView, String path) {
        File languageImageFile = new File(path);
        if(languageImageFile.exists()){
            Bitmap image = BitmapFactory.decodeFile(languageImageFile.getAbsolutePath());
            imageView.setImageBitmap(image);
        }else{
            imageView.setImageBitmap(null);
        }
    }
}
