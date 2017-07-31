package com.android.server.updates;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.EventLog;
import android.util.Slog;
import com.android.internal.util.HexDump;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import libcore.io.IoUtils;
import libcore.io.Streams;

public class ConfigUpdateInstallReceiver
  extends BroadcastReceiver
{
  private static final String EXTRA_REQUIRED_HASH = "REQUIRED_HASH";
  private static final String EXTRA_VERSION_NUMBER = "VERSION";
  private static final String TAG = "ConfigUpdateInstallReceiver";
  protected final File updateContent;
  protected final File updateDir;
  protected final File updateVersion;
  
  public ConfigUpdateInstallReceiver(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    this.updateDir = new File(paramString1);
    this.updateContent = new File(paramString1, paramString2);
    this.updateVersion = new File(new File(paramString1, paramString3), paramString4);
  }
  
  private byte[] getAltContent(Context paramContext, Intent paramIntent)
    throws IOException
  {
    paramIntent = getContentFromIntent(paramIntent);
    paramContext = paramContext.getContentResolver().openInputStream(paramIntent);
    try
    {
      paramIntent = Streams.readFullyNoClose(paramContext);
      return paramIntent;
    }
    finally
    {
      paramContext.close();
    }
  }
  
  private Uri getContentFromIntent(Intent paramIntent)
  {
    paramIntent = paramIntent.getData();
    if (paramIntent == null) {
      throw new IllegalStateException("Missing required content path, ignoring.");
    }
    return paramIntent;
  }
  
  private byte[] getCurrentContent()
  {
    try
    {
      byte[] arrayOfByte = IoUtils.readFileAsByteArray(this.updateContent.getCanonicalPath());
      return arrayOfByte;
    }
    catch (IOException localIOException)
    {
      Slog.i("ConfigUpdateInstallReceiver", "Failed to read current content, assuming first update!");
    }
    return null;
  }
  
  private static String getCurrentHash(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return "0";
    }
    try
    {
      paramArrayOfByte = HexDump.toHexString(MessageDigest.getInstance("SHA512").digest(paramArrayOfByte), false);
      return paramArrayOfByte;
    }
    catch (NoSuchAlgorithmException paramArrayOfByte)
    {
      throw new AssertionError(paramArrayOfByte);
    }
  }
  
  private int getCurrentVersion()
    throws NumberFormatException
  {
    try
    {
      int i = Integer.parseInt(IoUtils.readFileAsString(this.updateVersion.getCanonicalPath()).trim());
      return i;
    }
    catch (IOException localIOException)
    {
      Slog.i("ConfigUpdateInstallReceiver", "Couldn't find current metadata, assuming first update");
    }
    return 0;
  }
  
  private String getRequiredHashFromIntent(Intent paramIntent)
  {
    paramIntent = paramIntent.getStringExtra("REQUIRED_HASH");
    if (paramIntent == null) {
      throw new IllegalStateException("Missing required previous hash, ignoring.");
    }
    return paramIntent.trim();
  }
  
  private int getVersionFromIntent(Intent paramIntent)
    throws NumberFormatException
  {
    paramIntent = paramIntent.getStringExtra("VERSION");
    if (paramIntent == null) {
      throw new IllegalStateException("Missing required version number, ignoring.");
    }
    return Integer.parseInt(paramIntent.trim());
  }
  
  private boolean verifyPreviousHash(String paramString1, String paramString2)
  {
    if (paramString2.equals("NONE")) {
      return true;
    }
    return paramString1.equals(paramString2);
  }
  
  private boolean verifyVersion(int paramInt1, int paramInt2)
  {
    return paramInt1 < paramInt2;
  }
  
  protected void install(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    writeUpdate(this.updateDir, this.updateContent, paramArrayOfByte);
    writeUpdate(this.updateDir, this.updateVersion, Long.toString(paramInt).getBytes());
  }
  
  public void onReceive(final Context paramContext, final Intent paramIntent)
  {
    new Thread()
    {
      public void run()
      {
        int i;
        Object localObject;
        try
        {
          byte[] arrayOfByte = ConfigUpdateInstallReceiver.-wrap2(ConfigUpdateInstallReceiver.this, paramContext, paramIntent);
          i = ConfigUpdateInstallReceiver.-wrap5(ConfigUpdateInstallReceiver.this, paramIntent);
          str1 = ConfigUpdateInstallReceiver.-wrap7(ConfigUpdateInstallReceiver.this, paramIntent);
          int j = ConfigUpdateInstallReceiver.-wrap4(ConfigUpdateInstallReceiver.this);
          String str2 = ConfigUpdateInstallReceiver.-wrap6(ConfigUpdateInstallReceiver.-wrap3(ConfigUpdateInstallReceiver.this));
          if (!ConfigUpdateInstallReceiver.-wrap1(ConfigUpdateInstallReceiver.this, j, i))
          {
            Slog.i("ConfigUpdateInstallReceiver", "Not installing, new version is <= current version");
            return;
          }
          if (!ConfigUpdateInstallReceiver.-wrap0(ConfigUpdateInstallReceiver.this, str2, str1))
          {
            EventLog.writeEvent(51300, "Current hash did not match required value");
            return;
          }
        }
        catch (Exception localException)
        {
          Slog.e("ConfigUpdateInstallReceiver", "Could not update content!", localException);
          String str1 = localException.toString();
          localObject = str1;
          if (str1.length() > 100) {
            localObject = str1.substring(0, 99);
          }
          EventLog.writeEvent(51300, (String)localObject);
          return;
        }
        Slog.i("ConfigUpdateInstallReceiver", "Found new update, installing...");
        ConfigUpdateInstallReceiver.this.install((byte[])localObject, i);
        Slog.i("ConfigUpdateInstallReceiver", "Installation successful");
        ConfigUpdateInstallReceiver.this.postInstall(paramContext, paramIntent);
      }
    }.start();
  }
  
  protected void postInstall(Context paramContext, Intent paramIntent) {}
  
  protected void writeUpdate(File paramFile1, File paramFile2, byte[] paramArrayOfByte)
    throws IOException
  {
    Object localObject2 = null;
    FileOutputStream localFileOutputStream = null;
    Object localObject1 = localFileOutputStream;
    for (;;)
    {
      try
      {
        File localFile = paramFile2.getParentFile();
        localObject1 = localFileOutputStream;
        localFile.mkdirs();
        localObject1 = localFileOutputStream;
        if (!localFile.exists())
        {
          localObject1 = localFileOutputStream;
          throw new IOException("Failed to create directory " + localFile.getCanonicalPath());
        }
      }
      finally
      {
        paramFile1 = (File)localObject1;
        paramFile2 = (File)localObject2;
        if (paramFile1 != null) {
          paramFile1.delete();
        }
        IoUtils.closeQuietly(paramFile2);
      }
      localObject1 = localFileOutputStream;
      paramFile1 = File.createTempFile("journal", "", paramFile1);
      localObject1 = paramFile1;
      paramFile1.setReadable(true, false);
      localObject1 = paramFile1;
      localFileOutputStream = new FileOutputStream(paramFile1);
      try
      {
        localFileOutputStream.write(paramArrayOfByte);
        localFileOutputStream.getFD().sync();
        if (paramFile1.renameTo(paramFile2)) {
          break;
        }
        throw new IOException("Failed to atomically rename " + paramFile2.getCanonicalPath());
      }
      finally {}
    }
    if (paramFile1 != null) {
      paramFile1.delete();
    }
    IoUtils.closeQuietly(localFileOutputStream);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/updates/ConfigUpdateInstallReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */