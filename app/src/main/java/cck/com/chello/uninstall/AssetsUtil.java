package cck.com.chello.uninstall;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetsUtil {
	//标记是否正在解压，初始化为false
	private static boolean isDecoding = false;
	private static final String IS_UNZIP_DONE = "is_unzip_done";
	private static Context sContext;
	private static UnzipAsset7zListener unzipAsset7zListener;
	
	public static void init(Context context){
		sContext = context.getApplicationContext();
	}
	
	public interface UnzipAsset7zListener{
		public void onDone();
	}
	
	public static void setUnzipAsset7zListener(
			UnzipAsset7zListener unzipAsset7zListener) {
		AssetsUtil.unzipAsset7zListener = unzipAsset7zListener;
	}
	
	public static InputStream open(String filename) {
		//获取上下文
		Context context = sContext;
		
		LogUtils.i("cxh", "用系统方法查找:"+filename);
		AssetManager assetManager = context.getAssets();
		InputStream in = null;
		try {
			//先尝试用系统方法获取资源
			in = assetManager.open(filename);
		} catch (IOException e) {
			LogUtils.i("cxh", "从files目录中查找:"+filename);
			//如果系统方法获取不到文件，则从手机Files目录中读取文件
			in = openFromFileDir(context,filename);
		}
		return in;
	}
	
	private static synchronized InputStream openFromFileDir(Context context, String filename){
		InputStream in = null;
		String fileDirPath = context.getApplicationContext().getFilesDir().getAbsolutePath();
		
		boolean isUnzipDone = SharePreferenceHelper.getBoolean(IS_UNZIP_DONE, false);
		//如果已经解压过，则从files文件夹里再找一次
		if(isUnzipDone){
			try {
				in = new FileInputStream(fileDirPath+"/assets/"+filename);
				return in;
			} catch (FileNotFoundException e) {
				LogUtils.e("cxh", filename+"--不存在");
				e.printStackTrace();
			}
		}
		long maxExtractTime = 1500;
		long perSleepTime = 50;
		long curSleepTime = 0 ;
		//当正在解压时有资源请求，则阻塞200ms
		while (isDecoding) {
			LogUtils.i("cxh", "assets is unziping,please wait:"+filename);
			try {
				Thread.sleep(perSleepTime);
				curSleepTime += perSleepTime;
				in = new FileInputStream(fileDirPath+"/assets/"+filename);
				return in;
			} catch (Exception e) {
				//e.printStackTrace();
                if (curSleepTime >= maxExtractTime) {
                    // TODO 长时间解压不出来时,尝试先中断后,重新解压
                    LogUtils.i("cxh", filename + ",解压时间过长,放弃读取");
                    break;
                }
			}
		}
		
		//解压完成仍然访问不到资源，提示资源不存在
		LogUtils.i("cxh", filename+"--不存在");
		return in;
	}
	
	
	//子线程中解压assets7z文件
//	public static void unzipAsset7z(Context context) {
//		new Task().execute(context);
//	}
//
//
//	static class Task extends AsyncTask<Object,Object,Object> {
//
//		@Override
//		protected Object doInBackground(Object... params) {
//			Context context = (Context) params[0];
//			long start = System.currentTimeMillis();
//			//设置正在解压
//			isDecoding = true;
//			//先将assets中文件读到Files目录，准备解压
//			try {
//				InputStream ins = context.getAssets().open("assets.7z");
//				String filepath = context.getFilesDir()+"/assets.7z";
//
//				IO.copy(ins, filepath);
//
//				LogUtils.i("cxh", "start unzip");
//				SevenZipUtils.extract7z(filepath, context.getFilesDir().getAbsolutePath()+"/assets");
//				isDecoding = false;
//				LogUtils.i("cxh", "end unzip");
//
//			    SharePreferenceHelper.save(IS_UNZIP_DONE, true);
//			    if(unzipAsset7zListener != null)
//			    	unzipAsset7zListener.onDone();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			LogUtils.i("cxh", "unzip time:"+(System.currentTimeMillis()-start));
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Object object) {
//
//		}
//
//    }
	public static void copyAssetDirToFiles(Context context, String dirname)
			throws IOException {
		File dir = new File(context.getFilesDir() + "/" + dirname);
		dir.mkdir();

		AssetManager assetManager = context.getAssets();
		String[] children = assetManager.list(dirname);
		for (String child : children) {
			child = dirname + '/' + child;
			String[] grandChildren = assetManager.list(child);
			if (0 == grandChildren.length)
				copyAssetFileToFiles(context, child);
			else
				copyAssetDirToFiles(context, child);
		}
	}
	public static void copyAssetFileToFiles(Context context, String filename)
			throws IOException {
		InputStream is = context.getAssets().open(filename);
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		is.close();

		File of = new File(context.getFilesDir() + "/" + filename);
		of.createNewFile();
		FileOutputStream os = new FileOutputStream(of);
		os.write(buffer);
		os.close();
	}





//	public static void copyData(Context context)
//	{
//		InputStream in = null;
//		FileOutputStream out = null;
//		String path = context.getApplicationContext().getFilesDir()
//				.getAbsolutePath() + "/filterengine/patterns.ini"; // data/data目录
//
//		File file = new File(path);
//		if (!file.exists()) {
//			try
//			{
//				in = context.getAssets().open("filterengine/patterns.ini"); // 从assets目录下复制
//				out = new FileOutputStream(file);
//				int length = -1;
//				byte[] buf = new byte[1024];
//				while ((length = in.read(buf)) != -1)
//				{
//					out.write(buf, 0, length);
//				}
//				out.flush();
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//			finally{
//				if (in != null)
//				{
//					try {
//
//						in.close();
//
//					} catch (IOException e1) {
//
//						// TODO Auto-generated catch block
//
//						e1.printStackTrace();
//					}
//				}
//				if (out != null)
//				{
//					try {
//						out.close();
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				}
//			}
//		}
//	}
}















