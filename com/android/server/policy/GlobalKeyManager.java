package com.android.server.policy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.PrintWriter;
import org.xmlpull.v1.XmlPullParserException;

final class GlobalKeyManager
{
  private static final String ATTR_COMPONENT = "component";
  private static final String ATTR_KEY_CODE = "keyCode";
  private static final String ATTR_VERSION = "version";
  private static final int GLOBAL_KEY_FILE_VERSION = 1;
  private static final String TAG = "GlobalKeyManager";
  private static final String TAG_GLOBAL_KEYS = "global_keys";
  private static final String TAG_KEY = "key";
  private SparseArray<ComponentName> mKeyMapping = new SparseArray();
  
  public GlobalKeyManager(Context paramContext)
  {
    loadGlobalKeys(paramContext);
  }
  
  private void loadGlobalKeys(Context paramContext)
  {
    localContext2 = null;
    localContext3 = null;
    localObject = null;
    localContext1 = null;
    for (;;)
    {
      try
      {
        paramContext = paramContext.getResources().getXml(17891334);
        localContext1 = paramContext;
        localContext2 = paramContext;
        localContext3 = paramContext;
        localObject = paramContext;
        XmlUtils.beginDocument(paramContext, "global_keys");
        localContext1 = paramContext;
        localContext2 = paramContext;
        localContext3 = paramContext;
        localObject = paramContext;
        if (1 == paramContext.getAttributeIntValue(null, "version", 0))
        {
          localContext1 = paramContext;
          localContext2 = paramContext;
          localContext3 = paramContext;
          localObject = paramContext;
          XmlUtils.nextElement(paramContext);
          localContext1 = paramContext;
          localContext2 = paramContext;
          localContext3 = paramContext;
          localObject = paramContext;
          str1 = paramContext.getName();
          if (str1 != null) {}
        }
        else
        {
          if (paramContext != null) {
            paramContext.close();
          }
          return;
        }
      }
      catch (Resources.NotFoundException paramContext)
      {
        String str1;
        String str2;
        int i;
        localObject = localContext1;
        Log.w("GlobalKeyManager", "global keys file not found", paramContext);
        if (localContext1 == null) {
          continue;
        }
        localContext1.close();
        return;
      }
      catch (IOException paramContext)
      {
        localObject = localContext2;
        Log.w("GlobalKeyManager", "I/O exception reading global keys file", paramContext);
        if (localContext2 == null) {
          continue;
        }
        localContext2.close();
        return;
      }
      catch (XmlPullParserException paramContext)
      {
        localObject = localContext3;
        Log.w("GlobalKeyManager", "XML parser exception reading global keys file", paramContext);
        if (localContext3 == null) {
          continue;
        }
        localContext3.close();
        return;
      }
      finally
      {
        if (localObject == null) {
          continue;
        }
        ((XmlResourceParser)localObject).close();
      }
      localContext1 = paramContext;
      localContext2 = paramContext;
      localContext3 = paramContext;
      localObject = paramContext;
      if ("key".equals(str1))
      {
        localContext1 = paramContext;
        localContext2 = paramContext;
        localContext3 = paramContext;
        localObject = paramContext;
        str1 = paramContext.getAttributeValue(null, "keyCode");
        localContext1 = paramContext;
        localContext2 = paramContext;
        localContext3 = paramContext;
        localObject = paramContext;
        str2 = paramContext.getAttributeValue(null, "component");
        localContext1 = paramContext;
        localContext2 = paramContext;
        localContext3 = paramContext;
        localObject = paramContext;
        i = KeyEvent.keyCodeFromString(str1);
        if (i != 0)
        {
          localContext1 = paramContext;
          localContext2 = paramContext;
          localContext3 = paramContext;
          localObject = paramContext;
          this.mKeyMapping.put(i, ComponentName.unflattenFromString(str2));
        }
      }
    }
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    int j = this.mKeyMapping.size();
    if (j == 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("mKeyMapping.size=0");
      return;
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("mKeyMapping={");
    int i = 0;
    while (i < j)
    {
      paramPrintWriter.print("  ");
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(KeyEvent.keyCodeToString(this.mKeyMapping.keyAt(i)));
      paramPrintWriter.print("=");
      paramPrintWriter.println(((ComponentName)this.mKeyMapping.valueAt(i)).flattenToString());
      i += 1;
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("}");
  }
  
  boolean handleGlobalKey(Context paramContext, int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mKeyMapping.size() > 0)
    {
      ComponentName localComponentName = (ComponentName)this.mKeyMapping.get(paramInt);
      if (localComponentName != null)
      {
        paramContext.sendBroadcastAsUser(new Intent("android.intent.action.GLOBAL_BUTTON").setComponent(localComponentName).setFlags(268435456).putExtra("android.intent.extra.KEY_EVENT", paramKeyEvent), UserHandle.CURRENT, null);
        return true;
      }
    }
    return false;
  }
  
  boolean shouldHandleGlobalKey(int paramInt, KeyEvent paramKeyEvent)
  {
    return this.mKeyMapping.get(paramInt) != null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/GlobalKeyManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */