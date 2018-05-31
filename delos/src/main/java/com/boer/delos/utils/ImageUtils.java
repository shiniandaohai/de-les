package com.boer.delos.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import com.boer.delos.constant.Constant;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageUtils {

	public static Bitmap readBitmap565FromFile(String filename) {
		Bitmap bitmap = null;
		File file = new File(filename);
		if (file.isFile()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			FileInputStream fis = null;
			try {
				File f = new File(filename);
				try {
					fis = new FileInputStream(f);
					bitmap = BitmapFactory.decodeStream(fis, null, options);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (bitmap == null) {
					file.delete();
				}
			} catch (OutOfMemoryError e) {
				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
				}
				System.gc();
			} finally {
				if (fis != null) {
					try {
						fis.close();
						fis = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return bitmap;
	}



	public static int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;

	}

	/**
	 * 将图片按照某个角度进行旋转
	 *
	 * @param mBitmap
	 *            需要旋转的图片
	 * @param degree
	 *            旋转角度
	 * @return 旋转后的图片
	 */

	public static Bitmap rotateBitmapByDegree(Bitmap mBitmap, int degree) {
		Bitmap returnBm = null;
		try {
			Matrix matrix = new Matrix();
			matrix.postRotate((float) 90.0);
			returnBm = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
					mBitmap.getHeight(), matrix, false);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = mBitmap;
		}
		if (mBitmap != returnBm) {
			mBitmap.recycle();
		}
		return returnBm;
	}

	/**
	 * 检查图片是否有旋转
	 *
	 * @param context
	 * @param filePath
	 * @return
	 */
	public static File checkBitmapDegree(Context context, File filePath) {
		// TODO Auto-generated method stub
		int degree = getBitmapDegree(filePath.getAbsolutePath());
		Bitmap bitmap = readBitmap565FromFile(filePath.getAbsolutePath());
		if (degree > 0) {

			if (bitmap != null) {
				Bitmap bitmapNew = rotateBitmapByDegree(bitmap, degree);
				if (bitmapNew != null) {
					String newfilePath = System.currentTimeMillis() + ".jpg";
					File file = new FileCache(context).getFile(newfilePath);
					saveBitmap2File(context, newfilePath, bitmapNew);
					return file;
				}
			}

		}
		return filePath;
	}

	public static boolean createLargeThumbImage(String path, String ccid,
												float density, String fileName) {
		FileInputStream fis = null;

		float scale = 1;
		float width = 0, height = 0;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false;
		options.inInputShareable = true;
		options.inPurgeable = true;
		options.inTempStorage = new byte[16 * 1024];

		try {
			try {
				boolean isLargeProportion = false;
				// bitmap = BitmapFactory.decodeStream(fis);
				Bitmap decodeFile = BitmapFactory.decodeFile(path, options);
				if (decodeFile != null) {
					// imageMemoryCache.putShort(path, decodeFile);
					width = options.outWidth;
					height = options.outHeight;
					if (!(width / height > 3 || height / width > 3)) {
						if (width > height) {
							scale = (float) (Math.round((float) width
									/ (float) 960 * 1000)) / 1000;
						} else {
							scale = (float) (Math.round((float) height
									/ (float)960 * 1000)) / 1000;
						}
					} else {
						isLargeProportion = true;
					}
					if (scale > 1) {
						options.inSampleSize = Math.round(scale);
					}
					final File f = new File(path);
					fis = new FileInputStream(f);
					Bitmap tempBitmapImage = BitmapFactory
							.decodeFileDescriptor(fis.getFD(), null, options);
					if (!decodeFile.isRecycled()) {
						decodeFile.recycle();
						decodeFile = null;
					}
					if (tempBitmapImage != null) {

						boolean isSaveOk = saveImage(tempBitmapImage, fileName,
								isLargeProportion, 80);

						if (!tempBitmapImage.isRecycled()) {
							tempBitmapImage.recycle();
							tempBitmapImage = null;
						}
						return isSaveOk;
					}
				}

			} catch (OutOfMemoryError e) {
				System.gc();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
					fis = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public static String createCacheImageFile() {
		String fileName = "";
		File filedir = new File(Constant.IMAGE_PATH);
		if (!filedir.exists()) {
			filedir.mkdirs();
		}
		fileName = Constant.IMAGE_PATH + System.currentTimeMillis() + ".jpg";
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}



	public static boolean saveImage(Bitmap newbitmap, String sNewImagePath) {
		return saveImage(newbitmap, sNewImagePath, false, 100);
	}

	/**
	 * 保存图片
	 * 
	 * @param newbitmap
	 * @param sNewImagePath
	 * @param isLargeProportion
	 *            是否为大比例图片
	 * @param quality
	 *            基本压缩比
	 * @return
	 */
	public static boolean saveImage(Bitmap newbitmap, String sNewImagePath,
									boolean isLargeProportion, int quality) {
		try {
			File filedir = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
			if (!filedir.exists()) {
				filedir.mkdirs();
			}
			File file = new File(sNewImagePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			final FileOutputStream fileout = new FileOutputStream(sNewImagePath);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int options = quality;
			newbitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
			if (!isLargeProportion) {
				while (baos.toByteArray().length / 1024 > 100) {
					baos.reset();
					options -= 10;
					newbitmap.compress(Bitmap.CompressFormat.JPEG, options,
							baos);
				}
			}
			baos.writeTo(fileout);
			baos.flush();
			baos.close();
			fileout.flush();
			fileout.close();

			return true;
		} catch (Exception e) {
			System.gc();
			return false;
		} finally {
			// if (newbitmap != null && !newbitmap.isRecycled()) {
			// newbitmap.recycle();
			// newbitmap = null;
			// }
		}
	}



	/**
	 * bitmap 转换 成 文件
	 * 
	 * @param context
	 * @param filename
	 * @param bitmap
	 */
	public static void saveBitmap2File(Context context, String filename,
									   Bitmap bitmap) {	try {
		File file = new FileCache(context).getImageFile(context,filename);

			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			bitmap.recycle();
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取缩放图片 至少一个边等于新的宽或高
	 * 
	 * @param imagePath
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap getSpecifiedBitmap(String imagePath, int newWidth,
											int newHeight) {
		Bitmap rawBitmap = null;
		Bitmap compressbm = null;
		try {
			rawBitmap = BitmapFactory.decodeStream(new FileInputStream(
					new File(imagePath)));
			int width = rawBitmap.getWidth();
			int height = rawBitmap.getHeight();

			if (Math.min(width, height) > newWidth) {
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				float scalematrix = Math.max(scaleWidth, scaleHeight);
				Matrix matrix = new Matrix();
				matrix.postScale(scalematrix, scalematrix);
				compressbm = Bitmap.createBitmap(rawBitmap, 0, 0, width,
						height, matrix, true);
				if (compressbm != rawBitmap) {
					rawBitmap.recycle();
					System.gc();
				}
				return compressbm;
			} else {
				return rawBitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (rawBitmap != null) {
				rawBitmap.recycle();
			}
			if (compressbm != null) {
				compressbm.recycle();
			}
			System.gc();
			return null;
		} catch (OutOfMemoryError e) {
			if (rawBitmap != null) {
				rawBitmap.recycle();
			}
			if (compressbm != null) {
				compressbm.recycle();
			}
			System.gc();
			return null;
		}
	}









}
