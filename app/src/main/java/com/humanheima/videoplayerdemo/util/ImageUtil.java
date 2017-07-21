package com.humanheima.videoplayerdemo.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by chenchao on 16/9/27.
 * cc@cchao.org
 * 图片加载工具类
 */
public class ImageUtil {


    /**
     * 获取html中的src地址
     *
     * @param htmlStr
     * @return
     */
    public static List<String> getImgStr(String htmlStr) {
        List<String> pics = new ArrayList<>();
        String img = "";
        Pattern p_image;
        Matcher m_image;
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            // 得到<img />数据
            img = m_image.group();
            // 匹配<img>中的src数据
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                pics.add(m.group(1));
            }
        }
        return pics;
    }

    public static Observable<String> observableSaveImageToExternal(final Context context, final Bitmap cropBitmap) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(saveImageToExternal(context, cropBitmap));
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 保存图片到本地
     *
     * @param bm
     * @return
     */
    public static String saveImageToExternal(Context context, Bitmap bm) {
        File imageFile;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/PudongNews");
                path.mkdirs();
                imageFile = new File(path, String.valueOf(System.currentTimeMillis()) + ".png");
            } else {
                imageFile = new File(context.getExternalFilesDir(null), String.valueOf(System.currentTimeMillis()) + ".png");
            }
            FileOutputStream out = new FileOutputStream(imageFile);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            //TODO 还不能马上刷新
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(imageFile);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);
            } else {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + imageFile.getAbsoluteFile())));
            }
            if (imageFile.exists()) {
                return imageFile.getAbsolutePath();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
