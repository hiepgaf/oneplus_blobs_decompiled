package com.android.server.location;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.Slog;
import com.android.server.LocationManagerService;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public final class LocationBlacklist
  extends ContentObserver
{
  private static final String BLACKLIST_CONFIG_NAME = "locationPackagePrefixBlacklist";
  private static final boolean D = LocationManagerService.D;
  private static final String TAG = "LocationBlacklist";
  private static final String WHITELIST_CONFIG_NAME = "locationPackagePrefixWhitelist";
  private String[] mBlacklist = new String[0];
  private final Context mContext;
  private int mCurrentUserId = 0;
  private final Object mLock = new Object();
  private String[] mWhitelist = new String[0];
  
  public LocationBlacklist(Context paramContext, Handler paramHandler)
  {
    super(paramHandler);
    this.mContext = paramContext;
  }
  
  private String[] getStringArrayLocked(String paramString)
  {
    int i = 0;
    synchronized (this.mLock)
    {
      paramString = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), paramString, this.mCurrentUserId);
      if (paramString == null) {
        return new String[0];
      }
    }
    paramString = paramString.split(",");
    ??? = new ArrayList();
    int j = paramString.length;
    if (i < j)
    {
      String str = paramString[i].trim();
      if (str.isEmpty()) {}
      for (;;)
      {
        i += 1;
        break;
        ((ArrayList)???).add(str);
      }
    }
    return (String[])((ArrayList)???).toArray(new String[((ArrayList)???).size()]);
  }
  
  private boolean inWhitelist(String paramString)
  {
    synchronized (this.mLock)
    {
      String[] arrayOfString = this.mWhitelist;
      int j = arrayOfString.length;
      int i = 0;
      while (i < j)
      {
        boolean bool = paramString.startsWith(arrayOfString[i]);
        if (bool) {
          return true;
        }
        i += 1;
      }
      return false;
    }
  }
  
  private void reloadBlacklist()
  {
    synchronized (this.mLock)
    {
      reloadBlacklistLocked();
      return;
    }
  }
  
  private void reloadBlacklistLocked()
  {
    this.mWhitelist = getStringArrayLocked("locationPackagePrefixWhitelist");
    if (D) {
      Slog.d("LocationBlacklist", "whitelist: " + Arrays.toString(this.mWhitelist));
    }
    this.mBlacklist = getStringArrayLocked("locationPackagePrefixBlacklist");
    if (D) {
      Slog.d("LocationBlacklist", "blacklist: " + Arrays.toString(this.mBlacklist));
    }
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("mWhitelist=" + Arrays.toString(this.mWhitelist) + " mBlacklist=" + Arrays.toString(this.mBlacklist));
  }
  
  public void init()
  {
    this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("locationPackagePrefixBlacklist"), false, this, -1);
    reloadBlacklist();
  }
  
  public boolean isBlacklisted(String paramString)
  {
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        String[] arrayOfString = this.mBlacklist;
        int j = arrayOfString.length;
        i = 0;
        if (i < j)
        {
          String str = arrayOfString[i];
          if ((paramString.startsWith(str)) && (!inWhitelist(paramString)))
          {
            if (D) {
              Log.d("LocationBlacklist", "dropping location (blacklisted): " + paramString + " matches " + str);
            }
            return true;
          }
        }
        else
        {
          return false;
        }
      }
      i += 1;
    }
  }
  
  public void onChange(boolean paramBoolean)
  {
    reloadBlacklist();
  }
  
  public void switchUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mCurrentUserId = paramInt;
      reloadBlacklistLocked();
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/LocationBlacklist.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */