package com.android.server.updates;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Base64;
import android.util.Slog;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import libcore.io.IoUtils;

public class SELinuxPolicyInstallReceiver
  extends ConfigUpdateInstallReceiver
{
  private static final String TAG = "SELinuxPolicyInstallReceiver";
  private static final String fileContextsPath = "file_contexts.bin";
  private static final String macPermissionsPath = "mac_permissions.xml";
  private static final String propertyContextsPath = "property_contexts";
  private static final String seappContextsPath = "seapp_contexts";
  private static final String sepolicyPath = "sepolicy";
  private static final String serviceContextsPath = "service_contexts";
  private static final String versionPath = "selinux_version";
  
  public SELinuxPolicyInstallReceiver()
  {
    super("/data/security/bundle", "sepolicy_bundle", "metadata/", "version");
  }
  
  private void applyUpdate()
    throws IOException, ErrnoException
  {
    Slog.i("SELinuxPolicyInstallReceiver", "Applying SELinux policy");
    File localFile1 = new File(this.updateDir.getParentFile(), "backup");
    File localFile2 = new File(this.updateDir.getParentFile(), "current");
    File localFile3 = new File(this.updateDir.getParentFile(), "tmp");
    if (localFile2.exists())
    {
      deleteRecursive(localFile1);
      Os.rename(localFile2.getPath(), localFile1.getPath());
    }
    try
    {
      Os.rename(localFile3.getPath(), localFile2.getPath());
      SystemProperties.set("selinux.reload_policy", "1");
      return;
    }
    catch (ErrnoException localErrnoException)
    {
      do
      {
        Slog.e("SELinuxPolicyInstallReceiver", "Could not update selinux policy: ", localErrnoException);
      } while (!localFile1.exists());
      Os.rename(localFile1.getPath(), localFile2.getPath());
    }
  }
  
  private void deleteRecursive(File paramFile)
  {
    if (paramFile.isDirectory())
    {
      File[] arrayOfFile = paramFile.listFiles();
      int i = 0;
      int j = arrayOfFile.length;
      while (i < j)
      {
        deleteRecursive(arrayOfFile[i]);
        i += 1;
      }
    }
    paramFile.delete();
  }
  
  private void installFile(File paramFile, BufferedInputStream paramBufferedInputStream, int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramBufferedInputStream.read(arrayOfByte, 0, paramInt);
    writeUpdate(this.updateDir, paramFile, Base64.decode(arrayOfByte, 0));
  }
  
  private int[] readChunkLengths(BufferedInputStream paramBufferedInputStream)
    throws IOException
  {
    return new int[] { readInt(paramBufferedInputStream), readInt(paramBufferedInputStream), readInt(paramBufferedInputStream), readInt(paramBufferedInputStream), readInt(paramBufferedInputStream), readInt(paramBufferedInputStream), readInt(paramBufferedInputStream) };
  }
  
  private int readInt(BufferedInputStream paramBufferedInputStream)
    throws IOException
  {
    int j = 0;
    int i = 0;
    while (i < 4)
    {
      j = j << 8 | paramBufferedInputStream.read();
      i += 1;
    }
    return j;
  }
  
  private void unpackBundle()
    throws IOException
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(this.updateContent));
    File localFile = new File(this.updateDir.getParentFile(), "tmp");
    try
    {
      int[] arrayOfInt = readChunkLengths(localBufferedInputStream);
      deleteRecursive(localFile);
      localFile.mkdirs();
      installFile(new File(localFile, "selinux_version"), localBufferedInputStream, arrayOfInt[0]);
      installFile(new File(localFile, "mac_permissions.xml"), localBufferedInputStream, arrayOfInt[1]);
      installFile(new File(localFile, "seapp_contexts"), localBufferedInputStream, arrayOfInt[2]);
      installFile(new File(localFile, "property_contexts"), localBufferedInputStream, arrayOfInt[3]);
      installFile(new File(localFile, "file_contexts.bin"), localBufferedInputStream, arrayOfInt[4]);
      installFile(new File(localFile, "sepolicy"), localBufferedInputStream, arrayOfInt[5]);
      installFile(new File(localFile, "service_contexts"), localBufferedInputStream, arrayOfInt[6]);
      return;
    }
    finally
    {
      IoUtils.closeQuietly(localBufferedInputStream);
    }
  }
  
  protected void postInstall(Context paramContext, Intent paramIntent)
  {
    try
    {
      unpackBundle();
      applyUpdate();
      return;
    }
    catch (ErrnoException paramContext)
    {
      Slog.e("SELinuxPolicyInstallReceiver", "Could not update selinux policy: ", paramContext);
      return;
    }
    catch (IOException paramContext)
    {
      Slog.e("SELinuxPolicyInstallReceiver", "Could not update selinux policy: ", paramContext);
      return;
    }
    catch (IllegalArgumentException paramContext)
    {
      Slog.e("SELinuxPolicyInstallReceiver", "SELinux policy update malformed: ", paramContext);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/updates/SELinuxPolicyInstallReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */