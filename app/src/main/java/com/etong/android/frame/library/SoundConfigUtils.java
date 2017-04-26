package com.etong.android.frame.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.os.Vibrator;

import com.etong.android.frame.publisher.SharedPublisher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 声音配置工具类
 * (1)soundPool适合播放一些小音乐,文件最大不超过1M.
 * (2)MediaPlayer耗用比较大,一般操作大的文件.
 * http://blog.csdn.net/yzy1226466341/article/details/50949013
 * @author yzy
 *
 */
public class SoundConfigUtils implements OnBufferingUpdateListener,OnPreparedListener{
	
	private static SoundConfigUtils soundConfigUtils;
	private Context context;
	public static SoundConfigUtils getInstance(Context context){
		if(soundConfigUtils==null){
			soundConfigUtils=new SoundConfigUtils(context);
		}
		return soundConfigUtils;
	}
	private SoundConfigUtils(Context context) {
		super();
		this.context = context;
		sharedPublisher =SharedPublisher.getInstance();
	}


	public enum MusicType{
		NEW_VISTOR,NEW_MSG
	}

	SharedPublisher sharedPublisher =null;
	
	/**
	 * 播放声音
	 */
	@SuppressWarnings("deprecation")
	public void startSound(final MusicType musicType){
		boolean musicFlag= sharedPublisher.getBoolean("MUSIC_FLAG");
//		if(musicFlag){
//			stopSound();   //之前是打开音乐的,将音乐关闭
//		}
		AudioManager audioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);   //系统声音管理类
		Vibrator vibrator=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);   //震动服务
		long[] pattern={100,400,100,400};          //100表示等待时间,400表示震动时间   
		                                           //(1)vibrator.vibrate(pattern,-1); 表示不重复,只震动一个循环;
												   //(2)vibrator.vibrate(pattern,1),表示从下标为1的开始重复震动 ;
												   //(3)vibrator.vibrate(1000),表示震动多少秒
		switch (audioManager.getRingerMode()) {    //获取系统设置模式
		 //静音
		case AudioManager.RINGER_MODE_SILENT:      
			break;
		 //常规模式 两种情况:1.响铃不震动 2.响铃+震动
		case AudioManager.RINGER_MODE_NORMAL:     
			int flag=0;  //类型标志 0响铃以及震动 1响铃但是不震动 2.只在静音模式下震动
			//判断系统设置的震动类型
			if(audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER)==AudioManager.VIBRATE_SETTING_OFF){  //不震动
				flag=1;
			}else if(audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER)==AudioManager.VIBRATE_SETTING_ONLY_SILENT){//只在静音时,震动
				flag=2;
			}else{  //震动
				flag=0;
			}
			if(musicFlag){    //之前未打开音乐的话,提示音乐
				new Thread(){
					@Override
					public void run() {
						//soundPool播放
						initSoundPool();
						try {
							Thread.sleep(1000);   //线程休眠1秒,为系统加载音乐资源文件
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						playSound(musicType.ordinal(), 0);
					}
				}.start();
			}
			if(flag==0){
				vibrator.vibrate(pattern,-1);          //震动一次 
			}
			break;  
	   	//震动
		case AudioManager.RINGER_MODE_VIBRATE:    
			vibrator.vibrate(pattern,-1);         //震动一次
			break;
		default:
			break;
		}
	}
	
	/**
	 * soundPool提示音效
	 * (1)初始化播放音频的组件
	 * (2)初始化播放音效列表(HashMap=(ID,Sp.load(...))
	 * (3)获取系统声音设置(设置左右音道)
	 * (4)play
	 */
	private SoundPool sp;
	private Map<Integer, Integer> sources=null;
	private int streamId=0;
	
	/**
	 * 初始化音频
	 */
	@SuppressLint("UseSparseArrays") private void initSoundPool(){
		sp=new SoundPool(5, AudioManager.STREAM_MUSIC, 100);  //(最多同时播放的音乐,音频的类型,品质)
		sources=new HashMap<Integer, Integer>(); 
		//sources.put(0, sp.load(context, R.raw.tip_01, 1)); //sp.load(上下文,资源,优先级);  加载资源需要充足的时间,可使线程休眠,加载铃声
		//sources.put(1, sp.load(context, R.raw.tip_02, 1)); //sp.load(上下文,资源,优先级);
		try {
			sources.put(0, sp.load(context.getAssets().openFd("music/enter.mp3"),1)); 
			sources.put(1, sp.load(context.getAssets().openFd("music/msg.mp3"),1));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 播放音效
	 * @param id
	 * @param loop
	 */
	private void playSound(int id,int loop){
		AudioManager audioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);   //系统声音管理类
		float currentSound=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);                //最大音量
		float maxSound=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);                //当前音量
		streamId=sp.play(sources.get(id), currentSound/maxSound, currentSound/maxSound, 1, loop, 1.0f);                                            //加载资源id(sp.load(...)的返回值,0表示加载失败)
																								  //左声道,右声道,优先级,循环次数(0,播放一次,-1无限次,其他播放loop+1)
																								  //播放速率(0.5f-2.0f,1.0f表示正常,2.0f表示两倍速率) 
																								  //返回值为streamId可作为之后的pause,stop的参数
	}
    
	/**
	 * 关闭声音
	 */
	public void stopSound(){
		if(sp!=null){
			if(streamId!=0){
				sp.stop(streamId);
			}
		}
	}

	
	
	
	/**
	 * mediaplay提示音效
	 * 常用方法:
	 * reset():相当于重新实例化reset
	 * start(),pasue(),stop()
	 * prepare():缓冲
	 * isPlaying():是否在播放
	 * seekTo(position):从指定的位置开始播放
	 * release()：销毁
	 * setDataSource(file,startOffset,length)|(string):设置media的音乐源
	 */
	private MediaPlayer mediaPlayer;
	
	/**
	 * 播放mediaplayer
	 * playTypeId 播放media不同的方式
	 */
	private void playMediaPlayer(int playTypeId){
		switch (playTypeId) {
		//直接读取raw中的文件
		case 0:{
			//mediaPlayer=MediaPlayer.create(context, R.raw.tip_02);
			//mediaPlayer.start();
			break;
		}
		//读取Assert中的音乐文件 1.初始化AssetFileDescriptor 2.设置setDataSource 3.缓冲 4.开始播放
		case 1:{
			try {
				AssetFileDescriptor assetFileDescriptor=context.getAssets().openFd("music/tip_03.mp3");
				mediaPlayer=new MediaPlayer();
				mediaPlayer.setOnPreparedListener(this);
				mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
						assetFileDescriptor.getStartOffset(),
						assetFileDescriptor.getLength());    //设置播放源(数据源,开始点,总长度)
				mediaPlayer.prepare();       				    //缓冲
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			break;
		}
		//播放手机目录中的音乐
		case 2:{
			try {
				mediaPlayer=new MediaPlayer();
				mediaPlayer.setDataSource("");     //可以直接设置数据源的目录,此目录为手机上的目录
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} catch (SecurityException e) {
				
				e.printStackTrace();
			} catch (IllegalStateException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}         
			break;
		}
		default:
			break;
		}
//		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//				
//				mp.stop();
//			}
//		});
	}
    
	/**
	 * 停止播放
	 */
	private void stopMediaPlayer(){
		if(mediaPlayer!=null){
			if(mediaPlayer.isPlaying()){
				mediaPlayer.stop();
			}
			mediaPlayer.release();
			mediaPlayer=null;
		}
	}

	/**
	 * 播放网络音乐
	 */
    public void playUrlMediaPlayer(String mediaUrl){
    	mediaPlayer=new MediaPlayer();
    	try {
    		mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setDataSource(mediaUrl);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (SecurityException e) {
			
			e.printStackTrace();
		} catch (IllegalStateException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }

    /**
     * 播放进度
     */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		
		
	}

	/**
	 * 缓冲完执行的代码
	 */
	@Override
	public void onPrepared(MediaPlayer mp) {
		
		mp.start();
	}
}
