package com.oneplus.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutInfo.Builder;
import android.content.pm.ShortcutManager;
import android.os.Build.VERSION;
import android.os.PersistableBundle;
import com.oneplus.base.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocaleChangedReceiver
  extends BroadcastReceiver
{
  private static final String TAG = LocaleChangedReceiver.class.getSimpleName();
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (Build.VERSION.SDK_INT > 24)
    {
      paramIntent = (ShortcutManager)paramContext.getSystemService(ShortcutManager.class);
      if (paramIntent == null) {
        return;
      }
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramIntent.getDynamicShortcuts().iterator();
      int n;
      int m;
      int k;
      int j;
      int i;
      for (;;)
      {
        if (!localIterator.hasNext()) {
          break label343;
        }
        ShortcutInfo localShortcutInfo = (ShortcutInfo)localIterator.next();
        Object localObject = localShortcutInfo.getExtras();
        n = ((PersistableBundle)localObject).getInt("LongLabelResId", 0);
        m = ((PersistableBundle)localObject).getInt("ShortLabelResId", 0);
        k = ((PersistableBundle)localObject).getInt("DisabledMessageResId", 0);
        localObject = new ShortcutInfo.Builder(paramContext, localShortcutInfo.getId());
        j = 0;
        i = j;
        if (n > 0) {}
        try
        {
          ((ShortcutInfo.Builder)localObject).setLongLabel(paramContext.getString(n));
          i = 1;
        }
        catch (Throwable localThrowable1)
        {
          for (;;)
          {
            Log.w(TAG, "onReceive() - longLabelResId: " + n + ",e:", localThrowable1);
            i = j;
          }
        }
        j = i;
        if (m > 0) {}
        try
        {
          ((ShortcutInfo.Builder)localObject).setShortLabel(paramContext.getString(m));
          j = 1;
        }
        catch (Throwable localThrowable2)
        {
          for (;;)
          {
            Log.w(TAG, "onReceive() - shortLabelResId: " + m + ",e:", localThrowable2);
            j = i;
          }
        }
        i = j;
        if (k > 0) {}
        try
        {
          ((ShortcutInfo.Builder)localObject).setDisabledMessage(paramContext.getString(k));
          i = 1;
        }
        catch (Throwable localThrowable3)
        {
          for (;;)
          {
            Log.w(TAG, "onReceive() - disabledMessageResId: " + k + ",e:", localThrowable3);
            i = j;
          }
        }
        if (i != 0)
        {
          Log.v(TAG, "onReceive() - Update dynamic shortcut: ", localShortcutInfo.getId());
          localArrayList.add(((ShortcutInfo.Builder)localObject).build());
        }
      }
      label343:
      if (!localArrayList.isEmpty()) {
        paramIntent.updateShortcuts(localArrayList);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/receiver/LocaleChangedReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */