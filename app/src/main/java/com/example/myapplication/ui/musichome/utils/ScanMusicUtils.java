package com.example.myapplication.ui.musichome.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;

import java.util.ArrayList;
import java.util.List;

public class ScanMusicUtils {
    /*
    * 扫描系统里面的音频文件，返回一个list集合
    * */
    //MediaStore.Audio 获取音频信息的类

    private static final Uri URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final String[] MUSIC_PROJECTION = new String[] {
            MediaStore.Audio.Media._ID,//音频唯一标识
            MediaStore.Audio.Media.TITLE,//音乐名称
            MediaStore.Audio.Media.DATA,//歌曲数据
            MediaStore.Audio.Media.ALBUM,//专辑名
            MediaStore.Audio.Media.ARTIST,//歌手名
            MediaStore.Audio.Media.DURATION,//歌曲时长
            MediaStore.Audio.Media.SIZE//歌曲大小
    };
    //设置条件，相当于SQL语句中的where。null表示不进行筛选。
    private static final String SELECTION = "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0 ";
    /*String selection = MediaStore.Audio.Media.DATA + " like ? ";*/
//    //按照什么进行排序，相当于SQL语句中的Order by
    private static final String SORT_ORDER =MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    public List<MusicInfo> getMusicInfo(Context context) {
        List<MusicInfo> musicInfos = new ArrayList<>();
        /*String[] selectionArgs = new String[]{"%mp3%"};*/
        Cursor cursor = context.getContentResolver().query(URI, MUSIC_PROJECTION, SELECTION, null, SORT_ORDER);
        /*----------->测试-----------》*/
        if (cursor.moveToFirst() == false) {
            System.out.println("没找到数据");
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                /*筛选小于30秒和1m以下*/
                if (!checkIsMusic(duration, size)) {
                    continue;
                }

                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setId(id);
                musicInfo.setTitle(title);
                musicInfo.setData(data);
                musicInfo.setAlbum(album);
                musicInfo.setArtist(artist);
                musicInfo.setDuration(duration);
                musicInfo.setSize(size);
                /*---------->测试-----------<*/
                System.out.println(musicInfo.toString());
                musicInfos.add(musicInfo);

            }
            //释放资源
            cursor.close();
        }
        return musicInfos;
    }

    /*
    * 根据时间和大小，来判断所筛选的media是否为音乐文件，具体规则为筛选小于30秒和1m以下的
    * */
    public static boolean checkIsMusic (int time, long size) {
        if (time <= 0 || size <= 0) {
            return false;
        }

        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        /*minute %= 60;*/
        if (minute <= 0 && second <= 30) {
            return false;
        }
        if (size <= 1024 * 1024) {
            return false;
        }
        return true;
    }

    /*
    * 获取专辑封面
    *
    * */
    public static Bitmap getAlbumPicture(Context context, String path, int type) {
        //歌曲检索
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        //设置数据源
        mmr.setDataSource(path);
        //获取图片数据
        byte[] data = mmr.getEmbeddedPicture();
        Bitmap albumPicture = null;
        if (data != null) {
            //获取bitmap对象
            albumPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
            //获取宽高
            int width = albumPicture.getWidth();
            int height = albumPicture.getHeight();
            //创建操作图片用的Matrix对象
            Matrix matrix = new Matrix();
            //计算缩放比例
            float sx = ((float) 120 / width);
            float sy = ((float) 120 / height);
            //设置缩放比例
            matrix.postScale(sx, sy);
            //建立新的bitmap，其内容是对原bitmap的缩放后的图
            albumPicture = Bitmap.createBitmap(albumPicture, 0, 0, width, height);
        } else {
            //从歌曲文件读取不出来专辑图片时用来代替的默认专辑图片
            if (type == 1) {
                //Activity中显示
                albumPicture = BitmapFactory.decodeResource(context.getResources(), R.drawable.song);
            } else {
                //通知栏显示
                albumPicture = BitmapFactory.decodeResource(context.getResources(), R.drawable.song);
            }

            int width = albumPicture.getWidth();
            int height = albumPicture.getHeight();
            //创建操作图片用的Matrix对象
            Matrix matrix = new Matrix();
            //计算缩放比例
            float sx = ((float) 120 / width);
            float sy = ((float) 120 / height);
            //设置缩放比例
            matrix.postScale(sx, sy);
            //建立新的bitmap,其内容是对原bitmap的缩放后的图
            albumPicture = Bitmap.createBitmap(albumPicture, 0, 0, width, height);
        }
        return albumPicture;
    }
}
