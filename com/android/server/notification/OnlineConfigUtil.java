package com.android.server.notification;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import com.oneplus.config.ConfigGrabber;
import com.oneplus.config.ConfigObserver;
import com.oneplus.config.ConfigObserver.ConfigUpdater;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OnlineConfigUtil
{
  private static final boolean DEBUG = Build.DEBUG_ONEPLUS;
  private static final String DENOISE_NOTIFICATION = "DenoiseNotification";
  private static final String TAG = "OnlineConfigUtil";
  private static Context sContext;
  private static boolean sGetListFail;
  private static Handler sHandler;
  private static ConfigObserver sOnlineConfigObserver;
  private static Set<String> sWhiteList = new HashSet();
  
  static
  {
    sGetListFail = true;
    sWhiteList.add("com.tencent.mobileqq");
    sWhiteList.add("com.tencent.mm");
    sWhiteList.add("com.eg.android.AlipayGphone");
    sWhiteList.add("com.immomo.momo");
    sWhiteList.add("com.MobileTicket");
    sWhiteList.add("com.tencent.androidqqmail");
    sWhiteList.add("com.tencent.qqlite");
    sWhiteList.add("com.taobao.idlefish");
    sWhiteList.add("com.tencent.minihd.qq");
    sWhiteList.add("com.tencent.mobileqqi");
    sWhiteList.add("com.tencent.qq.kddi");
    sWhiteList.add("com.tencent.wework");
    sWhiteList.add("com.facebook.katana");
    sWhiteList.add("com.whatsapp");
    sWhiteList.add("jp.naver.line.android");
    sWhiteList.add("com.miracle.msnmobile");
    sWhiteList.add("com.facebook.orca");
    sWhiteList.add("com.google.android.apps.plus");
    sWhiteList.add("com.sina.weibo");
    sWhiteList.add("com.duowan.mobile");
    sWhiteList.add("com.skype.raider");
    sWhiteList.add("com.alibaba.mobileim");
    sWhiteList.add("cn.com.fetion");
    sWhiteList.add("com.eico.weico");
    sWhiteList.add("com.google.android.gm");
    sWhiteList.add("com.google.android.talk");
    sWhiteList.add("com.google.android.apps.messaging");
    sWhiteList.add("com.netease.mail");
    sWhiteList.add("com.kingsoft.email");
    sWhiteList.add("com.netease.mobimail");
    sWhiteList.add("com.cloudmagic.mail");
    sWhiteList.add("com.corp21cn.mail189");
    sWhiteList.add("com.asiainfo.android");
    sWhiteList.add("com.alibaba.cloudmail");
    sWhiteList.add("com.sina.mail");
    sWhiteList.add("com.yahoo.mobile.client.android.mail");
    sWhiteList.add("com.microsoft.office.outlook");
    sWhiteList.add("com.smartisan.email");
    sWhiteList.add("com.tencent.tim");
    sWhiteList.add("com.weico.international");
    sWhiteList.add("com.p1.mobile.putong");
    sWhiteList.add("com.xiaomi.channel");
    sWhiteList.add("com.skype.rover");
    sWhiteList.add("com.microsoft.teams");
    sWhiteList.add("com.oneplus.bbs");
  }
  
  private static void dumpList()
  {
    Object[] arrayOfObject = sWhiteList.toArray(new String[sWhiteList.size()]);
    int i = 0;
    while (i < arrayOfObject.length)
    {
      Log.i("OnlineConfigUtil", "idx=" + i + " pkg=" + arrayOfObject[i]);
      i += 1;
    }
  }
  
  private static void getWhiteList(JSONArray paramJSONArray)
  {
    int i;
    if (paramJSONArray != null) {
      i = 0;
    }
    for (;;)
    {
      try
      {
        if (i < paramJSONArray.length())
        {
          ??? = paramJSONArray.getJSONObject(i);
          if (!((JSONObject)???).getString("name").equals("default_reset_op_level_list")) {
            break label226;
          }
          JSONArray localJSONArray = ((JSONObject)???).getJSONArray("value");
          synchronized (sWhiteList)
          {
            sWhiteList.clear();
            int j = 0;
            if (j < localJSONArray.length())
            {
              sWhiteList.add(localJSONArray.getString(j));
              j += 1;
              continue;
            }
          }
        }
        Log.i("OnlineConfigUtil", "jsonArray is null");
      }
      catch (JSONException paramJSONArray)
      {
        Log.e("OnlineConfigUtil", "getWhiteList error. " + paramJSONArray);
        sGetListFail = true;
        if (DEBUG) {
          dumpList();
        }
        return;
        sGetListFail = false;
        Log.v("OnlineConfigUtil", "DenoiseNotification updated complete sWhiteList size=" + sWhiteList.size());
        continue;
      }
      catch (Exception paramJSONArray)
      {
        Log.e("OnlineConfigUtil", "getWhiteList error. " + paramJSONArray);
        sGetListFail = true;
        continue;
      }
      sGetListFail = true;
      continue;
      label226:
      i += 1;
    }
  }
  
  public static void init(Context paramContext, Handler paramHandler)
  {
    sContext = paramContext;
    sHandler = paramHandler;
    paramContext = new OnlineConfigUpdater(null);
    sOnlineConfigObserver = new ConfigObserver(sContext.getApplicationContext(), sHandler, paramContext, "DenoiseNotification");
    sOnlineConfigObserver.register();
    if (DEBUG) {
      Log.i("OnlineConfigUtil", "Register online config observer");
    }
    updateData();
  }
  
  public static boolean isInWhiteList(String paramString)
  {
    return sWhiteList.contains(paramString);
  }
  
  private static void updateData()
  {
    if (sGetListFail) {
      getWhiteList(new ConfigGrabber(sContext.getApplicationContext(), "DenoiseNotification").grabConfig());
    }
  }
  
  private static class OnlineConfigUpdater
    implements ConfigObserver.ConfigUpdater
  {
    public void updateConfig(JSONArray paramJSONArray)
    {
      Log.v("OnlineConfigUtil", "Receive online config update");
      OnlineConfigUtil.-wrap0(paramJSONArray);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/OnlineConfigUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */