package com.giousa.wifilocation;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = (Button) findViewById(R.id.btn_start);
        Button stop = (Button) findViewById(R.id.btn_stop);
        Button search = (Button) findViewById(R.id.btn_search);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //初始化定位
                        mLocationClient = new AMapLocationClient(getApplicationContext());
                        //初始化AMapLocationClientOption对象
                        mLocationOption = new AMapLocationClientOption();

                        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
                        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

                        //设置定位回调监听
                        mLocationClient.setLocationListener(mLocationListener);

                        //给定位客户端对象设置定位参数
                        mLocationClient.setLocationOption(mLocationOption);
                        //启动定位
                        mLocationClient.startLocation();
                    }
                }).start();

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = sHA1(MainActivity.this);
                Log.d(TAG,"SHA1="+s);
            }
        });
    }

    private AMapLocationListener mLocationListener = new AMapLocationListener(){

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Log.d(TAG,"我被定位了");
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                    int locationType = aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    double latitude = aMapLocation.getLatitude();//获取纬度
                    double longitude = aMapLocation.getLongitude();//获取经度
                    float accuracy = aMapLocation.getAccuracy();//获取精度信息
                    String address = aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    String country = aMapLocation.getCountry();//国家信息
                    String province = aMapLocation.getProvince();//省信息
                    String city = aMapLocation.getCity();//城市信息
                    String district = aMapLocation.getDistrict();//城区信息
                    String street = aMapLocation.getStreet();//街道信息
                    String streetNum = aMapLocation.getStreetNum();//街道门牌号信息
                    String cityCode = aMapLocation.getCityCode();//城市编码
                    String adCode = aMapLocation.getAdCode();//地区编码
                    String aoiName = aMapLocation.getAoiName();//获取当前定位点的AOI信息
                    String buildingId = aMapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                    String floor = aMapLocation.getFloor();//获取当前室内定位的楼层
//                    aMapLocation.getGpsStatus();//获取GPS的当前状态
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(aMapLocation.getTime());
                    df.format(date);
                    Log.d(TAG,"locationType="+locationType);
                    Log.d(TAG,"latitude="+latitude+"  longitude="+longitude+" accuracy="+accuracy);
                    Log.d(TAG,"address="+address+"  country="+country+" province="+province);
                    Log.d(TAG,"city="+city+"  district="+district+" street="+street);
                    Log.d(TAG,"streetNum="+streetNum+"  cityCode="+cityCode+" adCode="+adCode);
                    Log.d(TAG,"aoiName="+aoiName+"  buildingId="+buildingId+" floor="+floor);
                    Log.d(TAG,"df="+df);

                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(TAG,"AmapError location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    public String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
