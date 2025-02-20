package com.example.homesync;

import android.content.Context;
import android.net.Uri;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryDataBase {

    private static final String CLOUD_NAME = "dlclglmr6";
    private static final String API_KEY = "449296721478799";
    private static final String API_SECRET = "66IxM3cickgBjgZnYspppj_Xat0";


    public static String SaveImage(Uri imageUri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            byte[] bytes = getBytes(inputStream);
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", CLOUD_NAME);
            config.put("api_key", API_KEY);
            config.put("api_secret", API_SECRET);
            Cloudinary cloudinary = new Cloudinary(config);

            Map uploadResult = cloudinary.uploader().upload(bytes, ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
