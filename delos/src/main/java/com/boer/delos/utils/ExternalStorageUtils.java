package com.boer.delos.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/6/7.
 */
public class ExternalStorageUtils {
    public static final String CACHE_DIR = Environment.getExternalStorageDirectory() + "/delos";

    //检查SD卡是否已挂载
    public static boolean isMounted() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /*
    存储文件
     */
    public static void saveFile(String url, byte[] bytes) throws IOException {
        if (!isMounted())
            return;

        File dirFile = new File(CACHE_DIR);

        //级联式创建目录
        if (!dirFile.exists())
            dirFile.mkdirs();

        FileOutputStream fileOutputStream = new FileOutputStream(new File(dirFile, "/sdcard0/z/qqq.jpg"));
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    /*
    读取图片
	*绝对路径
    */
    public static Bitmap readImage(String fileName) {
        if (!isMounted())
            return null;

        File imageFile = new File(CACHE_DIR, fileName);
        if (imageFile.exists()) {
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }
        return null;
    }

    /*
    清除缓存
     */
    public static void clearCache() {
        if (!isMounted())
            return;

        File cacheDirFile = new File(CACHE_DIR);
        if (cacheDirFile.exists()) {
            File[] files = cacheDirFile.listFiles();
            for (File f : files) {
                f.delete();
            }
        }
    }

    /*
    判断剩余空间是否充足
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isSizeAvailable(long size_M) {
        if (!isMounted())
            return false;

        File rootFile = Environment.getExternalStorageDirectory();

        long freeSize = 0;

        if (Build.VERSION.SDK_INT >= 18) {
            freeSize = rootFile.getTotalSpace();
        } else {
            StatFs statFs = new StatFs(rootFile.getAbsolutePath());
            freeSize = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
        }

        return freeSize > size_M * 1024 * 1024;
    }

    /*
    得到某个目录的可用大小
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getFreeSpaceSize(File file) {
        long size = 0;
        if (!isMounted()) {
            return size;
        } else {
            if (Build.VERSION.SDK_INT >= 18) {
                size = file.getFreeSpace();
            } else {
                StatFs statFs = new StatFs(file.getAbsolutePath());
                size = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
            }
            return size / 1024 / 1024;
        }
    }

    /*
    获取SD卡的根目录
     */
    public static String getExternalStorageRootPath() {
        if (isMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }


    /*
       保存数据到SD卡
       注：dir为相对路径
     */
    public static boolean saveDataIntoSdcard(String dir, String fileName, byte[] data) {
        FileOutputStream fileOutputStream = null;
        if (isMounted()) {
            String path = getExternalStorageRootPath() + File.pathSeparator + dir;
            File parentFile = new File(path);
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            File file = new File(parentFile, fileName);
            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(data);
                fileOutputStream.flush();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /*
    从SD卡中读取数据
     */
    public static byte[] readDataFromSdcard(String dir, String fileName) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FileInputStream fileInputStream = null;

        if (isMounted()) {
            String path = getExternalStorageRootPath() + File.pathSeparator + dir;
            File file = new File(path, fileName);
            if (file != null && file.exists()) {
                byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    fileInputStream = new FileInputStream(file);
                    int length = 0;
                    byte[] buffer = new byte[1024];
                    while ((length = fileInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, length);
                    }
                    return byteArrayOutputStream.toByteArray();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    /*
    保存数据到SD的私有路径
    注：如果getExternalFilesDir(String type)中的type制定了具体的类型（Environment中的常量）
     那么路径就是 mnt/sdcard/Android/data/应用程序的包名/files/指定文件的类型/文件
     如果type没有指定则 mnt/sdcard/Android/data/应用程序的报名/files/文件
     */
    public static boolean savaDataIntoPrivate(String type, String fileName, String content, Context context) {
        //判断SD卡是否已经挂载
        if (isMounted()) {
            File parentFile = context.getExternalFilesDir(type);
            File file = new File(parentFile, fileName);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(content.getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    读取外部存储中的私有路径下的文件
     */
    public static byte[] readDataFromPrivate(String type, String fileName, Context context) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FileInputStream fileInputStream = null;

        if (isMounted()) {
            File parentFile = context.getExternalFilesDir(type);
            File file = new File(parentFile, fileName);
            byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                fileInputStream = new FileInputStream(file);
                int length = 0;
                byte[] buffer = new byte[1024];
                while ((length = fileInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                return byteArrayOutputStream.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /*
    根据传递的文件夹类型的字符串判断当前外部存储的公共路径下是否存在该文件夹
     */
    public static boolean hasThePublicDir(String type) {
        File file = Environment.getExternalStoragePublicDirectory(type);
        //判断当前生成的File对象是否存在
        if (file != null && file.exists()) {
            return true;
        }
        return false;
    }

    /*
        将Bitmap转化为文件保存到本地
     */
    public static boolean saveBitmap(Bitmap bitmap, String dir, String name) {
        try {
            if (isMounted()) {
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }

                File file = new File(dirFile.getAbsolutePath(), name);
                if (file.exists())
                    file.delete();

                FileOutputStream fileOutputStream = null;

                fileOutputStream = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream)) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
    读取图片
     */
    public static Bitmap getImageToBitmap(String dir, String name) {
        File file = new File(dir, name);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return bitmap;
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(java.io.File file) {

        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //return size/1048576;
        return size;
    }


    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }


    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }


    public static boolean clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
        return true;
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}
