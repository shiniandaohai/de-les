package com.boer.delos.utils;

import android.content.Context;
import android.text.TextUtils;

import com.boer.delos.constant.URLConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/7/14 0014.
 */
public class UploadFileManger {
    public static UploadFileManger instance = null;

    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static UploadFileManger getInstance() {
        if (instance == null) {
            synchronized (UploadFileManger.class) {
                if (instance == null) {
                    instance = new UploadFileManger();
                }
            }
        }
        return instance;
    }
    /* 上传文件至Server的方法 */
    public void   uploadAvatar(final Context context,final String filePath ,final  UploadFileListener listener)
    {
        if(!TextUtils.isEmpty(filePath)) {


                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            String uploadUrl = URLConfig.HTTP + "/image/upload";
                            String end = "\r\n";
                            String twoHyphens = "--";
                            String boundary = "******";
                        URL url = new URL(uploadUrl);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                                .openConnection();
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                        httpURLConnection.setRequestProperty("Charset", "UTF-8");
                        httpURLConnection.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);

                        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
                        dos.writeBytes(twoHyphens + boundary + end);
                        dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\""
                                + filePath.substring(filePath.lastIndexOf("/") + 1)
                                + "\"" + end);
                        dos.writeBytes(end);
                        //将SD 文件通过输入流读到Java代码中-++++++++++++++++++++++++++++++`````````````````````````
                        FileInputStream fis = new FileInputStream(filePath);
                        byte[] buffer = new byte[8192]; // 8k
                        int count = 0;
                        while ((count = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, count);

                        }
                        fis.close();
                        System.out.println("file send to server............");
                        dos.writeBytes(end);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
                        dos.flush();

                        //读取服务器返回结果
                        InputStream is = httpURLConnection.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is, "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        String result = br.readLine();
                        if(!TextUtils.isEmpty(result)){
                            String ret = JsonUtil.parseString(result, "ret");
                            if ("0".equals(ret)){
                                JSONObject jo = new JSONObject(result);
                                String imageUrl = jo.getString("url");
                                listener.uploadSuccess(imageUrl);
                                }else {
                                  listener.uploadFailed(1);
                                }
                        }
                        dos.close();
                        is.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.uploadFailed(2);
                        }
                    }
                }.start();

        }

    }

  public interface UploadFileListener{
      void  uploadSuccess(String url);
      void  uploadFailed(int status);
    }

}

