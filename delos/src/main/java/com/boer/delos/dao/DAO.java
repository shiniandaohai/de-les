package com.boer.delos.dao;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.boer.delos.dao.entity.DeviceBrandEntity;
import com.boer.delos.dao.entity.DeviceFormatsEntity;
import com.boer.delos.dao.entity.DeviceModelEntity;
import com.boer.delos.dao.entity.DeviceTypeEntity;
import com.boer.delos.utils.L2;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by dell on 2016/7/14.
 * //http://blog.csdn.net/jielundewode/article/details/44154445
 */
public class DAO {
    //数据库存储路径
    final String packageName = "com.boer.jiaweishi";
//    final String dbName = "BRANDS.sqlite"; //modify :2016/11/21
    final String dbName = "BRANDS.db";
    //final String filePath = "data/data/" + packageName + "/" + dbName;
    final String pathStr = Environment.getExternalStorageDirectory() + "/" + packageName + "/";
    final String filePath = pathStr + dbName;

    private Context mContex;
    private DbUtils mDbUtils;

    private boolean isConnected = false;

    private DAO(Context context) {
        this.mContex = context.getApplicationContext();
        createDbFile(mContex);
//        connectDb();
    }

    private volatile static DAO singleton;

    public static DAO getSingleton(Context context) {
        if (singleton == null) {
            synchronized (DAO.class) {
                if (singleton == null) {
                    singleton = new DAO(context);
                }
            }
        }
        return singleton;
    }


    /**
     * 复制DB文件 到data
     *
     * @param context
     * @return
     */
    public boolean createDbFile(Context context) {
        File jhPath = new File(filePath);
        //查看数据库文件是否存在
        if (jhPath.exists()) {
            //存在则直接返回打开的数据库
            return true;
        } else {
            //不存在先创建文件夹
            File path = new File(pathStr);
            if (path.mkdir()) {
                L2.d("fooo 创建成功");
            } else {
                L2.d("fooo 创建失败");
            }
            ;
            try {
                //得到资源
                AssetManager am = context.getAssets();
                //得到数据库的输入流
                InputStream is = am.open(dbName);
                //用输出流写到SDcard上面
                FileOutputStream fos = new FileOutputStream(jhPath);
                //创建byte数组  用于1KB写一次
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                //最后关闭就可以了
                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            //如果没有这个数据库  我们已经把他写到SD卡上了，然后在执行一次这个方法 就可以返回数据库了
            return createDbFile(context);
        }
    }

    /**
     * 连接数据库。
     */
    public void connectDb() {
        // DbUtils.create(this),提供了不同的重载方法
        DbUtils.DaoConfig dc = new DbUtils.DaoConfig(mContex);
        dc.setDbDir(pathStr);
        dc.setDbName(dbName);
        dc.setDbUpgradeListener(null);
        dc.setDbVersion(1);

        //mDbUtils = DbUtils.create(mContex, pathStr, dbName);
        if(mDbUtils == null) {
            mDbUtils = DbUtils.create(dc);
        }
        mDbUtils.configDebug(true);//打印sql语句

        isConnected = true;
    }

    public List<DeviceTypeEntity> getDeviceType() {
        List<DeviceTypeEntity> dts = null;
        try {
            dts = mDbUtils.findAll(DeviceTypeEntity.class);
            if (dts != null && dts.size() > 0) {
                for (DeviceTypeEntity d : dts) {
                    L2.d("fooo", "i = " + d.getId());
                    L2.d("fooo", "" + d.getDevice_name());
                }
            }
            //dbUtils.updateAll(children, "name");
        } catch (DbException e) {

        }
        return dts;
    }

    /**
     * 通过设备类型 - 查找对应设备
     * @return
     */
     public List<DeviceBrandEntity> getDeviceBrandByDeviceTypeID(int deviceTypeId) {
        List<DeviceBrandEntity> dts = null;
        try {
            dts = mDbUtils.findAll(Selector.from(DeviceBrandEntity.class).where("device_id","=", deviceTypeId));
            if (dts != null && dts.size() > 0) {
                for (DeviceBrandEntity d : dts) {
                    L2.d("fooo", "i = " + d.getId());
                    L2.d("fooo", "" + d.getBrandname());
                }
            }
            //dbUtils.updateAll(children, "name");
        } catch (DbException e) {

        }
        return dts;
    }

    /**
     * 读取设备型号
     * @param device_id
     * @param model_list
     * @return
     */
    public List<DeviceModelEntity> getDeviceModel(int device_id, String model_list) {
        List<DeviceModelEntity> dts = null;
        try {
            dts = mDbUtils.findAll(Selector.from(DeviceModelEntity.class).where("device_id","=", device_id).and("m_code", "=", model_list));
            if (dts != null && dts.size() > 0) {
                for (DeviceModelEntity d : dts) {
                    L2.d("fooo", "i = " + d.getId());
                    L2.d("fooo", "" + d.getM_label());
                }
            }
            //dbUtils.updateAll(children, "name");
        } catch (DbException e) {

        }
        return dts;

    }

    /**
     * 读取指令格式。
     * @param fid
     * @param deviceid
     * @return
     */
    public DeviceFormatsEntity getDeviceFormat(int fid, int deviceid) {
        List<DeviceFormatsEntity> dts = null;
        try {
            dts = mDbUtils.findAll(Selector.from(DeviceFormatsEntity.class).where("fid","=", fid).and("device_id", "=",deviceid));
            if (dts != null && dts.size() > 0) {
                for (DeviceFormatsEntity d : dts) {
                    L2.e("fooo " + d.getId());
                    L2.e("fooo " + d.getFormat_string());
                }
            } else {
                return new DeviceFormatsEntity(); //没查到。 可能性不大。
            }
            //dbUtils.updateAll(children, "name");
        } catch (DbException e) {
            e.printStackTrace();
            L2.e(e.getMessage());
            return new DeviceFormatsEntity(); //没查到。 可能性不大。
        }
        return dts.get(0);
    }

    public void closeDb() {
        mDbUtils.close();
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isClosed() {
        return !isConnected;
    }
    /* 经典查询代码
    List<Child> findChild = dbUtils.findAll(Selector.from(Child.class)
    .where("id", "<", "3")
    //执行至此处相当于WHERE id<54 AND (age>20 OR age<30)
    .and(WhereBuilder.b("score", ">", "70").or("age", "<", "10"))
    // op为"in"或"between"时，最后一个参数必须是数组或Iterable的实现类(例如List等)
    .where("id", "in", new int[]{1, 2, 3})
    .orderBy("id")
        .limit(pageSize)
        .offset(pageSize * pageIndex)) ;
     */

}
