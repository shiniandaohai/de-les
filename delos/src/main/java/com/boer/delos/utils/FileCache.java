package com.boer.delos.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {
		// 如果有SD卡则在SD卡中建一个QXT的目录存放缓存的图片
		// 没有SD卡就放在系统的缓存目录中
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(
					Environment.getExternalStorageDirectory(),
					"jiaweishi");
			// DebugUtils.error("文件路径   cache 走的是外部存储");
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()) {
			// DebugUtils.error("文件路径   cacheDir.exists()：：yes");
			cacheDir.mkdirs();
		}
		if (!cacheDir.isDirectory()) {
			// DebugUtils.error("文件路径   cacheDir.isDirectory()：：yes");
			cacheDir.mkdirs();
		}
	}

	/**
	 * 获取sdcard/QXT/resource/或者系统缓存下的url.hascode命名的文件
	 *
	 * @param url
	 * @return
	 */
	public File getFile(String url) {
		// 将url的hashCode作为缓存的文件名

		String filename = String.valueOf(url.hashCode());
		// Another possible solution
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;
	}

	public File getImageFile(Context context, String filename) throws IOException {
		File cacheDir;
		// 如果有SD卡则在SD卡中建一个QXT的目录存放缓存的图片
		// 没有SD卡就放在系统的缓存目录中
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(
					Environment.getExternalStorageDirectory(),
					"jiaweishi/image/");
			// DebugUtils.error("文件路径   cache 走的是外部存储");
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()) {
			// DebugUtils.error("文件路径   cacheDir.exists()：：yes");
			cacheDir.mkdirs();
		}
		if (!cacheDir.isDirectory()) {
			// DebugUtils.error("文件路径   cacheDir.isDirectory()：：yes");
			cacheDir.mkdirs();
		}
		File f= new File(filename);
		if(!f.exists()){
			f.createNewFile();
		}
		return f;
	}


	/**
	 * 获取主缓存目录
	 *
	 * @return
	 */
	public String getCachePath() {
		return cacheDir.getAbsolutePath();
	}
}
