package com.boer.delos.request;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.boer.delos.activity.OffLineActivity;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.commen.ActivityCustomManager;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.EncryResult;
import com.boer.delos.utils.Base64;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.utils.sign.iHomeUtils;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wangkai
 * @Description: OKHttp请求
 * create at 2015/11/12 16:06
 */
public class OKHttpRequest {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType X_WWW = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    /**
     * @param context  上下文
     * @param url      请求地址
     * @param tag      请求的标记
     * @param listener 回调接口
     */
    public static void RequestGet(final Context context, String url, String tag, final RequestResultListener listener) {
        final Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .build();
        deliveryResult(context, url, listener, request);
    }

    public static void RequestGet(final Context context, String url, String appcode, String tag, final RequestResultListener listener) {
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", appcode)
                //.tag(tag)
                .build();
        deliveryResult(context, url, listener, request);
    }

    /**
     * @param context  上下文
     * @param url      请求地址
     * @param tag      请求标记
     * @param listener 回调接口
     */
    public static void RequestPost(final Context context, String url, String tag,
                                   final Map<String, String> maps,
                                   final RequestResultListener listener) {
        Param[] paramsArr = map2Params(maps);
        Request request = buildPostRequest(url, paramsArr, tag);
        deliveryResult(context, url, listener, request);
    }

    public static void RequestPost(final Context context, String url,
                                   final Map<String, String> maps, final RequestResultListener listener) {
        RequestPost(context, url, null, maps, listener);
    }

    /**
     * application/json形式的Post接口(加密)
     *
     * @param context  上下文
     * @param url      请求地址
     * @param tag      请求标记
     * @param json     请求数据
     * @param listener 回调
     */
    public static void postWithNoKey(Context context, String url, String tag, String json, RequestResultListener listener) {
        String encryString = null;
        if (Constant.KEY == null || Constant.KEY.length == 0) {
            SharedPreferencesUtils.readKeyFromPreferences(context);
        }
        try {
            encryString = iHomeUtils.AESEncryData(json, Constant.KEY);
            encryString = URLEncoder.encode(encryString, "UTF-8") + "=";
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailed("");
        }
        if (encryString != null) {
            RequestBody body = RequestBody.create(X_WWW, encryString);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            deliveryResult(context, url, listener, request);
        }
    }

    /**
     * application/json形式的Post接口(非加密)
     *
     * @param context  上下文
     * @param url      请求地址
     * @param tag      请求标记
     * @param json     请求数据
     * @param listener 回调
     */
    public static void postWithNoKeyNonEncrypted(final Context context, final String url, String tag, String json, final RequestResultListener listener) {
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        BaseApplication.getOKHttpClient().newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        if (listener != null) {
                            listener.onFailed("");
                            Loger.d("哈哈 " + request.toString());
                        }
                    }

                    @Override
                    public void onResponse(final Response response) throws IOException {

                        L.v("postWithNoKeyNonEncrypted+++response===" + response);


                        final String jsonString = response.body().string();

                        L.v("jsonString====" + jsonString);


                        BaseApplication.getDelivery().post(new Runnable() {

                            @Override
                            public void run() {
                                if (response.code() != 200) {
                                    L.e(String.format("服务器%d出错", response.code()));
                                    listener.onFailed("");
                                    return;
                                }
                                try {
                                    //判断如果设备是控制,延迟3秒更新
                                    if (url.contains("device/cmd")) {
                                        Constant.IS_DEVICE_STATUS_UPDATE = true;
                                    }

                                    if (listener != null) {
                                        EncryResult result = new Gson().fromJson(jsonString, EncryResult.class);
                                        String json = new String(Base64.decode(result.getSresult()));
                                        L.i(url + ":++++++:" + json);
                                        listener.onSuccess(json);
                                    }
                                } catch (Exception e) {
                                    L.e(url + ":******:" + e);
                                    listener.onFailed("");
                                }
                            }
                        });

                    }
                });
    }

    public static void commonPostWithNoKeyNonEncrypted(final Context context, final String url, String tag, String json, final RequestResultListener listener) {
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        BaseApplication.getOKHttpClient().newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        if (listener != null) {
                            listener.onFailed("");
                            Loger.d("哈哈 " + request.toString());
                        }
                    }

                    @Override
                    public void onResponse(final Response response) throws IOException {

                        L.v("postWithNoKeyNonEncrypted+++response===" + response);


                        final String jsonString = response.body().string();

                        L.v("jsonString====" + jsonString);


                        BaseApplication.getDelivery().post(new Runnable() {

                            @Override
                            public void run() {
                                if (response.code() != 200) {
                                    L.e(String.format("服务器%d出错", response.code()));
                                    listener.onFailed("");
                                    return;
                                }
                                try {
                                    if (listener != null) {
                                        L.i(url + ":++++++:" + jsonString);
                                        listener.onSuccess(jsonString);
                                    }
                                } catch (Exception e) {
                                    L.e(url + ":******:" + e);
                                    listener.onFailed("");
                                }
                            }
                        });

                    }
                });
    }

    /**
     * 单图上传
     *
     * @param context  上下文
     * @param url      请求地址
     * @param tag      请求标记
     * @param map      post附带参数
     * @param fileName 文件名
     * @param file     上传的文件
     * @param listener 回调
     */
    public static void uploadFile(final Context context, String url, String tag, final HashMap<String, String> map, String fileName, File file, final RequestResultListener listener) {
        List<File> files = new ArrayList<>();
        files.add(file);
        List<String> fileNames = new ArrayList<>();
        fileNames.add(fileName);
        Request request = buildMultipartFormRequest(url, files, fileNames, map2Params(map), tag);
        deliveryResult(context, url, listener, request);
    }

    /**
     * 多图上传
     *
     * @param context   上下文
     * @param url       请求地址
     * @param tag       请求标记
     * @param map       post附带参数
     * @param fileNames 文件名
     * @param files     上传的文件
     * @param listener  回调
     */
    public static void uploadFiles(final Context context, String url, String tag, final HashMap<String, String> map, List<String> fileNames, List<File> files, final RequestResultListener listener) {
        Request request = buildMultipartFormRequest(url, files, fileNames, map2Params(map), tag);
        deliveryResult(context, url, listener, request);
    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     */
    public static void downloadFile(final String url, final String destFileDir, final String filename, final RequestResultListener listener) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = BaseApplication.getOKHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                e.printStackTrace();
                L.d("OKHttpRequest downloadFile() onFailure()" + e.toString());
                listener.onFailed("");
            }

            @Override
            public void onResponse(Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, filename);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    listener.onSuccess(file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    L.d("OKHttpRequest downloadFile() onResponse()" + e.toString());
                    listener.onFailed("");
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }

            }
        });
    }

    private static String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * 构建post请求
     *
     * @param url
     * @param params
     * @param tag
     * @return
     */
    private static Request buildPostRequest(String url, Param[] params, String tag) {
        if (params == null) {
            params = new Param[0];
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .tag(tag)
                .post(requestBody)
                .build();
    }

    /**
     * 自定义params实体
     */
    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

    /**
     * 处理请求
     *
     * @param listener
     * @param request
     */
    private static void deliveryResult(final Context context, final String url, final RequestResultListener listener, final Request request) {
        BaseApplication.getOKHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                BaseApplication.getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        e.printStackTrace();
                        L.d("OKHttpRequest deliveryResult() onFailure()" + url + "----" + e.toString());
                        L.d("listener==" + listener);
                        if (listener != null) {
                            listener.onFailed("");
                        }
                    }
                });
            }

            @Override
            public void onResponse(final com.squareup.okhttp.Response response) throws IOException {
                final String jsonString = response.body().string();

                BaseApplication.getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.code() != 200) {
                            String error = String.format("服务器%d出错", response.code());
                            listener.onFailed(error);
                            return;
                        }
                        try {
                            //判断如果设备是控制,延迟5秒更新
                            if (url.contains("device/cmd")) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //设置设备状态更新
                                        Constant.IS_DEVICE_STATUS_UPDATE = true;
                                    }
                                }, 3000);
                            }

                            if (jsonString.contains("sresult")) {
                                EncryResult encryResult = new Gson().fromJson(jsonString, EncryResult.class);
                                String result = iHomeUtils.AESDecryData(encryResult.getSresult(), Constant.KEY);
                                Log.v("gl", "result===" + result);

                                int ret = JsonUtil.parseInt(result, "ret");
                                if (ret == 0) {
                                    listener.onSuccess(result);
                                } else {
                                    String msg = "";
                                    try {
                                        msg = JsonUtil.parseString(result, "msg");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        msg = "出错";
                                    }
                                    listener.onFailed(msg);
                                }
                            } else {
                                int ret = JsonUtil.parseInt(jsonString, "ret");
                                if (ret == 10105) {
                                    L.i("当前账号已经在其他设备上登录");
                                    if (ActivityCustomManager.getAppManager().getCurrentActivity() == null
                                            || ActivityCustomManager.getAppManager().getCurrentActivity() instanceof OffLineActivity
                                            || ActivityCustomManager.getAppManager().getCurrentActivity() instanceof LoginActivity || ActivityCustomManager.getAppManager().getCurrentActivity() instanceof MainTabActivity) {
                                        if (listener != null) {
                                            listener.onFailed("");
                                        }
                                        if(url.contains("/auth/logout")||url.contains("/user/host_permissions")){
                                            Intent it = new Intent(context, OffLineActivity.class);
                                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(it);
                                        }
                                    } else {
                                        if (listener != null) {
                                            listener.onFailed("");
                                        }
                                        Intent it = new Intent(context, OffLineActivity.class);
                                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(it);
                                    }

                                } else if (ret == 10103 || ret == 10104) {
                                    Loger.d("token无法解析或过期|Json解析失败");
                                    context.sendBroadcast(new Intent(Constant.ACTION_EXCEPTION));
                                    if (listener != null) {
                                        listener.onFailed("");
                                    }
                                } else {
                                    listener.onSuccess(jsonString);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            L.d("OKHttpRequest deliveryResult() onFailure()" + url + "----" + e.toString());
                            if (listener != null) {
                                listener.onFailed("");
                            }
                        }
                    }
                });
            }

        });
    }


    /**
     * 拼接POST请求参数
     *
     * @param params
     * @return
     */
    private static Param[] map2Params(Map<String, String> params) {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    private static Request buildMultipartFormRequest(String url, List<File> files,
                                                     List<String> fileKeys, Param[] params, String tag) {
        params = validateParam(params);

        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);

        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files != null) {
            RequestBody fileBody = null;
            int size = files.size();
            for (int i = 0; i < size; i++) {
                File file = files.get(i);
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + fileKeys.get(i) + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .tag(tag)
                .post(requestBody)
                .build();
    }

    private static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    private static Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else return params;
    }


}