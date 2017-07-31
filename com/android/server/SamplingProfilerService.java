package com.android.server;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.DropBoxManager;
import android.os.FileObserver;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.util.Slog;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;

public class SamplingProfilerService
  extends Binder
{
  private static final boolean LOCAL_LOGV = false;
  public static final String SNAPSHOT_DIR = "/data/snapshots";
  private static final String TAG = "SamplingProfilerService";
  private final Context mContext;
  private FileObserver snapshotObserver;
  
  public SamplingProfilerService(Context paramContext)
  {
    this.mContext = paramContext;
    registerSettingObserver(paramContext);
    startWorking(paramContext);
  }
  
  private void handleSnapshotFile(File paramFile, DropBoxManager paramDropBoxManager)
  {
    try
    {
      paramDropBoxManager.addFile("SamplingProfilerService", paramFile, 0);
      return;
    }
    catch (IOException paramDropBoxManager)
    {
      Slog.e("SamplingProfilerService", "Can't add " + paramFile.getPath() + " to dropbox", paramDropBoxManager);
      return;
    }
    finally
    {
      paramFile.delete();
    }
  }
  
  private void registerSettingObserver(Context paramContext)
  {
    paramContext = paramContext.getContentResolver();
    paramContext.registerContentObserver(Settings.Global.getUriFor("sampling_profiler_ms"), false, new SamplingProfilerSettingsObserver(paramContext));
  }
  
  private void startWorking(final Context paramContext)
  {
    paramContext = (DropBoxManager)paramContext.getSystemService("dropbox");
    File[] arrayOfFile = new File("/data/snapshots").listFiles();
    int i = 0;
    while ((arrayOfFile != null) && (i < arrayOfFile.length))
    {
      handleSnapshotFile(arrayOfFile[i], paramContext);
      i += 1;
    }
    this.snapshotObserver = new FileObserver("/data/snapshots", 4)
    {
      public void onEvent(int paramAnonymousInt, String paramAnonymousString)
      {
        SamplingProfilerService.-wrap0(SamplingProfilerService.this, new File("/data/snapshots", paramAnonymousString), paramContext);
      }
    };
    this.snapshotObserver.startWatching();
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "SamplingProfilerService");
    paramPrintWriter.println("SamplingProfilerService:");
    paramPrintWriter.println("Watching directory: /data/snapshots");
  }
  
  private class SamplingProfilerSettingsObserver
    extends ContentObserver
  {
    private ContentResolver mContentResolver;
    
    public SamplingProfilerSettingsObserver(ContentResolver paramContentResolver)
    {
      super();
      this.mContentResolver = paramContentResolver;
      onChange(false);
    }
    
    public void onChange(boolean paramBoolean)
    {
      SystemProperties.set("persist.sys.profiler_ms", Integer.valueOf(Settings.Global.getInt(this.mContentResolver, "sampling_profiler_ms", 0)).toString());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/SamplingProfilerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */