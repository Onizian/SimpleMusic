package com.example.myapplication.ui.musichome.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;
import com.example.myapplication.ui.musichome.POJO.MusicRankInfo;
import com.example.myapplication.ui.musichome.POJO.MusicRecentInfo;
import com.example.myapplication.ui.musichome.utils.DateTimeUtils;
import com.example.myapplication.ui.setting.SettingFragment;
import com.example.myapplication.ui.setting.SettingViewModel;

import org.litepal.LitePal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    /**
     * 当前播放位置
     */
    private int mCurrentPosition = 0;
    /**
     * 播放音乐列表
     */
    private List<MusicInfo> musicInfoList = new ArrayList<>();
    private List<MusicRankInfo> musicRankInfoList = new ArrayList<>();
    /**
     * 播放器
     */
    private MediaPlayer mediaPlayer;
    /*
    * 定时器是否启动
    * */
    private boolean timeisplay = false;
    /*
    * 定时关闭app是否启动
    * */
    private boolean closeappplay = false;

    public MusicService() {
    }

    /**
     * 发送广播
     */
    private void sendMsg(int mCurrentPosition) {
        //发送当前位置
        Intent intent = new Intent("allmusic.msg");
        intent.putExtra("msg", mCurrentPosition);
        this.sendBroadcast(intent);
    }

    private void sendCurrentposition(int mtime) {
        //发送当前音乐的进度
        Intent intent = new Intent("songshow");
        intent.putExtra("musictime", mtime);
        this.sendBroadcast(intent);
    }

    private void sendtimeisplay(boolean t) {
        //发送当前音乐定时器是否启动
        Intent intent = new Intent("timerplay");
        intent.putExtra("timer", t);
        this.sendBroadcast(intent);
    }

    private void sendmediaisplay(boolean isplay) {
        //发送当前播放状态
        Intent intent = new Intent("mediaisplay");
        intent.putExtra("isplay", isplay);
        this.sendBroadcast(intent);
    }

    private void sendcloseapp(boolean t) {
        //发送当前关闭app定时器是否启动
        Intent intent = new Intent("closeappplay");
        intent.putExtra("closeapp", t);
        this.sendBroadcast(intent);
    }

    /**
     * 监听音乐播放
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        //下一首
        nextMusic();
        sendMsg(mCurrentPosition);

    }

    /**
    * 记录歌曲播放次数
    * */
    public void addLocalRankMusic(int mCurrentPosition) {
        List<MusicRankInfo> musicRankInfoList = LitePal.where("title=? and artist=?", musicInfoList.get(mCurrentPosition).getTitle() + "", musicInfoList.get(mCurrentPosition).getArtist() + "").find(MusicRankInfo.class);
        if (musicRankInfoList.size() != 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    MusicRankInfo musicRankInfo = LitePal.find(MusicRankInfo.class, musicRankInfoList.get(0).getId());
                    musicRankInfo.setPlaynum(musicRankInfo.getPlaynum()+1);
                    musicRankInfo.save();
                    System.out.println(musicRankInfo.getTitle() + "播放"+ musicRankInfo.getPlaynum() + "次");
                }
            });
        } else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    MusicRankInfo musicRankInfo = new MusicRankInfo();
                    musicRankInfo.setArtist(musicInfoList.get(mCurrentPosition).getArtist());
                    musicRankInfo.setAlbum(musicInfoList.get(mCurrentPosition).getAlbum());
                    musicRankInfo.setData(musicInfoList.get(mCurrentPosition).getData());
                    musicRankInfo.setDuration(musicInfoList.get(mCurrentPosition).getDuration());
                    musicRankInfo.setSize(musicInfoList.get(mCurrentPosition).getSize());
                    musicRankInfo.setCheck(false);
                    musicRankInfo.setTitle(musicInfoList.get(mCurrentPosition).getTitle());
                    musicRankInfo.setPlaynum(1);
                    musicRankInfo.save();
                    System.out.println(musicRankInfo.getTitle() + "播放"+ musicRankInfo.getPlaynum() + "次");
                }
            });
        }
    }
    /**
     * 设置播放的时间并将其更新到最近列表歌单中
     * */
    public void addLocalRecentMusic(int mCurrentPosition) {
        //查询数据表是否存在信息
        List<MusicRecentInfo> mrList = LitePal.where("title=? and artist=?", musicInfoList.get(mCurrentPosition).getTitle() + "", musicInfoList.get(mCurrentPosition).getArtist() + "").find(MusicRecentInfo.class);
        if (mrList.size() != 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    MusicRecentInfo musicRecentInfo = LitePal.find(MusicRecentInfo.class, mrList.get(0).getId());
                    try {
                        musicRecentInfo.setPlaytime(timechange());//设置最近播放时间
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicRecentInfo.save();//保存更新
                    System.out.println("当前歌曲播放的日期时间段："+musicRecentInfo.getPlaytime());
                }
            });
        }else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    MusicRecentInfo musicRecentInfo = new MusicRecentInfo();
                    musicRecentInfo.setArtist(musicInfoList.get(mCurrentPosition).getArtist());
                    musicRecentInfo.setAlbum(musicInfoList.get(mCurrentPosition).getAlbum());
                    musicRecentInfo.setData(musicInfoList.get(mCurrentPosition).getData());
                    musicRecentInfo.setDuration(musicInfoList.get(mCurrentPosition).getDuration());
                    musicRecentInfo.setSize(musicInfoList.get(mCurrentPosition).getSize());
                    musicRecentInfo.setCheck(false);
                    musicRecentInfo.setTitle(musicInfoList.get(mCurrentPosition).getTitle());
                    try {
                        musicRecentInfo.setPlaytime(timechange());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicRecentInfo.save();
                    System.out.println(musicRecentInfo.getTitle() + "最近播放时间："+ musicRecentInfo.getPlaytime() + "秒");
                }
            });
        }
    }

    /**
    * 获取当前播放歌单列表
    * */
    public List<MusicInfo> getMusicInfoList() {
        return musicInfoList;
    }

    /**
     * 时间转换+获取时间
     * */
    public long timechange() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = sdf.parse(DateTimeUtils.getNowDateTime());
        return time.getTime();
    }

    /**
    * 把目前歌曲播放时间传到进度条进行更新
    * */
    private Handler handler = new Handler();
    Runnable updataThread = new Runnable() {
        @Override
        public void run() {
            //发送歌曲现在播放位置的时间
            sendCurrentposition(mediaPlayer.getCurrentPosition());
            //每次延迟1000毫秒再启动线程
            handler.postDelayed(updataThread, 1000);
        }
    };




    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*showNotification();//启动状态栏*/
        System.out.println("onCreate");
    }

    /**
     * 使用bindService进行绑定
     */

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("onBind");
        return new MusicBinder();
    }

    /**
     * 播放音乐
     */
    public void playmusic(int position, List<MusicInfo> musicInfoList) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            /*监听音乐播放完毕事件，自动下一曲*/
            mediaPlayer.setOnCompletionListener(this);
        }
        this.musicInfoList = musicInfoList;
        System.out.println(musicInfoList.size());
        if (musicInfoList.size() <= 0) {
            System.out.println("musicInfoList is null");
            return;
        }
        try {
            Bundle bundle = new Bundle();
            /*切歌前重置，释放掉之前的资源*/
            mediaPlayer.reset();
            //设置播放音频的资源路径
            mediaPlayer.setDataSource(musicInfoList.get(position).getData());
            //开始播放前的准备工作，加载多媒体资源，获取相关信息
            mediaPlayer.prepare();
            //开始播放音乐
            mediaPlayer.start();
            //设置当前的播放位置
            mCurrentPosition = position;
            //启动线程
            handler.post(updataThread);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停|继续播放音乐
     */
    public void pauseOrcontinueMusic(int type) {
        /**
         1代表暂停播放------2代表开始播放
         */
        if (mediaPlayer != null) {
            switch (type) {
                case 1:
                    mediaPlayer.pause();
                    break;
                case 2:
                    mediaPlayer.start();
                    break;
            }
        }
    }
    /**
    * 调节音乐播放时间
    * */
    public void musicseeTo(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }



    /**
     * 关闭音乐
     */
    public void closeMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            //重置
            mediaPlayer.reset();
            //释放
            mediaPlayer.release();
            //关闭线程
            handler.removeCallbacks(updataThread);
        }
    }


    /**
     * 下一首
     */
    public void nextMusic() {
        if (mCurrentPosition >= musicInfoList.size()-1) {
            mCurrentPosition = 0;
        } else {
            mCurrentPosition += 1;
        }
        System.out.println("Service.mCurrentPosition==" + mCurrentPosition);
        playmusic(mCurrentPosition,musicInfoList);
        addLocalRankMusic(mCurrentPosition);
        sendmediaisplay(getisplay());
        addLocalRecentMusic(mCurrentPosition);
    }

    /**
     * 上一首
     */
    public void previousMusic() {
        if (mCurrentPosition <= 0) {
            mCurrentPosition = musicInfoList.size()-1;
        } else {
            mCurrentPosition -= 1;
        }

        playmusic(mCurrentPosition,musicInfoList);
        addLocalRankMusic(mCurrentPosition);
        addLocalRecentMusic(mCurrentPosition);
        sendmediaisplay(getisplay());
    }


    /**
     * 获得当前播放位置
     */
    public int getmCurrentPosition() {
        return mCurrentPosition;
    }

    /**
     * 获得当前播放状态
     */
    public boolean getisplay() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        } else {
            return false;
        }
    }

    /**
     * 获取当前是否存在mediaplayer对象
     */
    public boolean getisnull() {
        if (mediaPlayer == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 定时器
     * */
    private Timer timer = null;
    private TimerTask task = null;
    Handler hb = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1 && getisplay()) {
                pauseOrcontinueMusic(1);
                timeisplay = false;
                sendtimeisplay(timeisplay);
                hbtime.removeCallbacks(timethread);
            }

            System.out.println("定时器结束");
            super.handleMessage(msg);
        }
    };

    /*
    * 内部类---》为了重新创建一个timertask
    * */
    public class Tasktodo {
        public TimerTask getTasktodo() {
            return new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    hb.sendMessage(message);
                    System.out.println("-----------timertask----------");
                }
            };
        }
    }


    /*
    * 调用此方法执行定时器
    * */
    public void timetodo(long mtime) {
        /*
        *   java.util.Timer.schedule(TimerTask task, long delay):
        *   *这个方法是说，dalay/1000秒后执行task.只执行一次。
            java.util.Timer.schedule(TimerTask task, long delay, long period)：
            * 这个方法是说，delay/1000秒后执行task,然后进过period/1000秒再次执行task，
            * 这个用于循环任务，执行无数次
        * */
        System.out.println(timeisplay);
        if (!timeisplay) {
            timer = new Timer();
            task = new Tasktodo().getTasktodo();
            if (task != null && timer != null) {
                timer.schedule(task, mtime);
                timeisplay = true;
                System.out.println("启动定时器");
                //开启线程
                hbtime.post(timethread);
            }
        } else {
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
            if (task != null) {
                task.cancel();
                task = null;
            }
            timeisplay = false;
            sendtimeisplay(timeisplay);
            hbtime.removeCallbacks(timethread);
            System.out.println("结束定时器");
        }
    }
    public boolean gettimeisplay() {
        return timeisplay;
    }

    /**
     * 更新定时器ui信息
     * */
    private Handler hbtime = new Handler();
    Runnable timethread = new Runnable() {
        @Override
        public void run() {
            //发送目前的定时器状态
            sendtimeisplay(gettimeisplay());
            //每次延迟100毫秒再启动线程
            hbtime.postDelayed(timethread, 100);
        }
    };

    /*
    * 定时关闭app
    * */
    private Timer closetimer = null;
    private TimerTask closetask = null;

    Handler hb2 = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 2) {
                closeappplay = false;
                //android中所有的activity都在主进程中，
                // 在Androidmanifest.xml中可以设置成启动不同进程，
                // Service不是一个单独的进程也不是一个线程。
                // 当你Kill掉当前程序的进程时也就是说整个程序的所有线程都会结束，
                // Service也会停止，整个程序完全退出。
                closeappplay = false;
                hbcloseapp.removeCallbacks(closeappthread);
                android.os.Process.killProcess(android.os.Process.myPid());

            }
            super.handleMessage(msg);
        }
    };

    public void closeapptodo(long mtime) {
        /*
        *   java.util.Timer.schedule(TimerTask task, long delay):
        *   *这个方法是说，dalay/1000秒后执行task.只执行一次。
            java.util.Timer.schedule(TimerTask task, long delay, long period)：
            * 这个方法是说，delay/1000秒后执行task,然后进过period/1000秒再次执行task，
            * 这个用于循环任务，执行无数次
        * */
        if (!closeappplay) {
            closetimer = new Timer();
            closetask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 2;
                    hb2.sendMessage(message);
                }
            };
            if (closetask != null && closetimer != null && !closeappplay) {
                closetimer.schedule(closetask,mtime);
                closeappplay = true;
                System.out.println("开始倒计时关闭程序");
                //启动线程
                hbcloseapp.post(closeappthread);
            }
        }
    }
    /*
    * 结束关闭app
    * */
    public void stopcloseapp() {
        if (closetimer != null) {
            closetimer.cancel();
            closetimer.purge();
            closetimer = null;
        }
        if (closetask != null) {
            closetask.cancel();
            closetask = null;
        }
        closeappplay = false;
        sendcloseapp(closeappplay);
        hbcloseapp.removeCallbacks(closeappthread);
        System.out.println("结束定时器");
    }

    /**
    * 返回一个是否启动关闭程序的信号
    * */
    public boolean getclosesignal() {
        return closeappplay;
    }

    /**
     * 更新定时器ui信息
     * */
    private Handler hbcloseapp = new Handler();
    Runnable closeappthread = new Runnable() {
        @Override
        public void run() {
            //发送目前的定时器状态
            sendcloseapp(closeappplay);
            //每次延迟100毫秒再启动线程
            hbcloseapp.postDelayed(closeappthread, 100);
        }
    };

    /*
    * -------------------------状态栏，显示通知------------------------
    * *//*
    private static NotificationManagerCompat manager;

    private static RemoteViews remoteViews;

    *//**
    * 显示通知
    * *//*
    private void showNotification() {
        String channelId = "play_control";
        String channelName = "播放控制";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.song)
                .setContentTitle("Song Title")
                .setContentText("Song Artist")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCustomContentView(remoteViews);
        createNotificationChannel(channelId, channelName, importance);
        manager.notify(1, builder.build());
    }

    private void createNotificationChannel(String channelId, String channelName, int importance) {
        manager = NotificationManagerCompat.from(this);
        manager.createNotificationChannelGroup(new NotificationChannelGroup(channelId, channelName));
        manager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
    }*/


}

