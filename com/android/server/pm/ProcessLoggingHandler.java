package com.android.server.pm;

import android.app.admin.SecurityLog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Slog;
import com.android.internal.os.BackgroundThread;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public final class ProcessLoggingHandler
  extends Handler
{
  static final int INVALIDATE_BASE_APK_HASH_MSG = 2;
  static final int LOG_APP_PROCESS_START_MSG = 1;
  private static final String TAG = "ProcessLoggingHandler";
  private final HashMap<String, String> mProcessLoggingBaseApkHashes = new HashMap();
  
  ProcessLoggingHandler()
  {
    super(BackgroundThread.getHandler().getLooper());
  }
  
  private byte[] computeHashOfApkFile(String paramString)
    throws IOException, NoSuchAlgorithmException
  {
    MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-256");
    paramString = new FileInputStream(new File(paramString));
    byte[] arrayOfByte = new byte[65536];
    for (;;)
    {
      int i = paramString.read(arrayOfByte);
      if (i <= 0) {
        break;
      }
      localMessageDigest.update(arrayOfByte, 0, i);
    }
    paramString.close();
    return localMessageDigest.digest();
  }
  
  private String computeStringHashOfApk(String paramString)
  {
    if (paramString == null) {
      return "No APK";
    }
    String str2 = (String)this.mProcessLoggingBaseApkHashes.get(paramString);
    String str1 = str2;
    if (str2 == null) {
      str1 = str2;
    }
    try
    {
      byte[] arrayOfByte = computeHashOfApkFile(paramString);
      str1 = str2;
      StringBuilder localStringBuilder = new StringBuilder();
      int i = 0;
      for (;;)
      {
        str1 = str2;
        if (i >= arrayOfByte.length) {
          break;
        }
        str1 = str2;
        localStringBuilder.append(String.format("%02x", new Object[] { Byte.valueOf(arrayOfByte[i]) }));
        i += 1;
      }
      str1 = str2;
      str2 = localStringBuilder.toString();
      str1 = str2;
      this.mProcessLoggingBaseApkHashes.put(paramString, str2);
      str1 = str2;
    }
    catch (IOException|NoSuchAlgorithmException paramString)
    {
      for (;;)
      {
        Slog.w("ProcessLoggingHandler", "computeStringHashOfApk() failed", paramString);
      }
    }
    if (str1 != null) {
      return str1;
    }
    return "Failed to count APK hash";
  }
  
  public void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return;
    case 1: 
      paramMessage = paramMessage.getData();
      String str1 = paramMessage.getString("processName");
      int i = paramMessage.getInt("uid");
      String str2 = paramMessage.getString("seinfo");
      String str3 = paramMessage.getString("apkFile");
      int j = paramMessage.getInt("pid");
      SecurityLog.writeEvent(210005, new Object[] { str1, Long.valueOf(paramMessage.getLong("startTimestamp")), Integer.valueOf(i), Integer.valueOf(j), str2, computeStringHashOfApk(str3) });
      return;
    }
    paramMessage = paramMessage.getData();
    this.mProcessLoggingBaseApkHashes.remove(paramMessage.getString("apkFile"));
  }
  
  void invalidateProcessLoggingBaseApkHash(String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("apkFile", paramString);
    paramString = obtainMessage(2);
    paramString.setData(localBundle);
    sendMessage(paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/ProcessLoggingHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */