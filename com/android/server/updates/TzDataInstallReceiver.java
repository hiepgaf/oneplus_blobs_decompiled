package com.android.server.updates;

import android.util.Slog;
import java.io.File;
import java.io.IOException;
import libcore.tzdata.update.TzDataBundleInstaller;

public class TzDataInstallReceiver
  extends ConfigUpdateInstallReceiver
{
  private static final String TAG = "TZDataInstallReceiver";
  private static final File TZ_DATA_DIR = new File("/data/misc/zoneinfo");
  private static final String UPDATE_CONTENT_FILE_NAME = "tzdata_bundle.zip";
  private static final String UPDATE_DIR_NAME = TZ_DATA_DIR.getPath() + "/updates/";
  private static final String UPDATE_METADATA_DIR_NAME = "metadata/";
  private static final String UPDATE_VERSION_FILE_NAME = "version";
  private final TzDataBundleInstaller installer = new TzDataBundleInstaller("TZDataInstallReceiver", TZ_DATA_DIR);
  
  public TzDataInstallReceiver()
  {
    super(UPDATE_DIR_NAME, "tzdata_bundle.zip", "metadata/", "version");
  }
  
  protected void install(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    boolean bool = this.installer.install(paramArrayOfByte);
    Slog.i("TZDataInstallReceiver", "Timezone data install valid for this device: " + bool);
    super.install(paramArrayOfByte, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/updates/TzDataInstallReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */