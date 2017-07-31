package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import android.util.Slog;
import com.android.internal.app.ResolverActivity;
import com.android.internal.os.BackgroundThread;
import dalvik.system.DexFile;
import dalvik.system.VMRuntime;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public final class PinnerService
  extends SystemService
{
  private static final boolean DEBUG = false;
  private static final String TAG = "PinnerService";
  private final long MAX_CAMERA_PIN_SIZE = 52428800L;
  private BinderService mBinderService;
  private final Context mContext;
  private final ArrayList<PinnedFile> mPinnedCameraFiles = new ArrayList();
  private final ArrayList<PinnedFile> mPinnedFiles = new ArrayList();
  private PinnerHandler mPinnerHandler = null;
  private final boolean mShouldPinCamera;
  
  public PinnerService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mShouldPinCamera = paramContext.getResources().getBoolean(17957075);
    this.mPinnerHandler = new PinnerHandler(BackgroundThread.get().getLooper());
  }
  
  private boolean alreadyPinned(int paramInt)
  {
    ApplicationInfo localApplicationInfo = getCameraInfo(paramInt);
    if (localApplicationInfo == null) {
      return false;
    }
    paramInt = 0;
    while (paramInt < this.mPinnedCameraFiles.size())
    {
      if (((PinnedFile)this.mPinnedCameraFiles.get(paramInt)).mFilename.equals(localApplicationInfo.sourceDir)) {
        return true;
      }
      paramInt += 1;
    }
    return false;
  }
  
  private ApplicationInfo getCameraInfo(int paramInt)
  {
    Object localObject = new Intent("android.media.action.STILL_IMAGE_CAMERA");
    localObject = this.mContext.getPackageManager().resolveActivityAsUser((Intent)localObject, 851968, paramInt);
    if (localObject == null) {
      return null;
    }
    if (isResolverActivity(((ResolveInfo)localObject).activityInfo)) {
      return null;
    }
    return ((ResolveInfo)localObject).activityInfo.applicationInfo;
  }
  
  private void handlePinCamera(int paramInt)
  {
    if (this.mShouldPinCamera) {}
    try
    {
      boolean bool = pinCamera(paramInt);
      if (!bool) {}
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void handlePinOnStart()
  {
    String[] arrayOfString = this.mContext.getResources().getStringArray(17236058);
    int i = 0;
    for (;;)
    {
      try
      {
        if (i < arrayOfString.length)
        {
          PinnedFile localPinnedFile = pinFile(arrayOfString[i], 0L, 0L, 0L);
          if (localPinnedFile != null) {
            this.mPinnedFiles.add(localPinnedFile);
          } else {
            Slog.e("PinnerService", "Failed to pin file = " + arrayOfString[i]);
          }
        }
      }
      finally {}
      return;
      i += 1;
    }
  }
  
  private boolean isResolverActivity(ActivityInfo paramActivityInfo)
  {
    return ResolverActivity.class.getName().equals(paramActivityInfo.name);
  }
  
  private boolean pinCamera(int paramInt)
  {
    if (alreadyPinned(paramInt)) {
      return true;
    }
    ApplicationInfo localApplicationInfo = getCameraInfo(paramInt);
    if (localApplicationInfo == null) {
      return false;
    }
    unpinCameraApp();
    String str = localApplicationInfo.sourceDir;
    Object localObject2 = pinFile(str, 0L, 0L, 52428800L);
    if (localObject2 == null)
    {
      Slog.e("PinnerService", "Failed to pin " + str);
      return false;
    }
    this.mPinnedCameraFiles.add(localObject2);
    str = "arm";
    if ((localApplicationInfo.primaryCpuAbi != null) && (VMRuntime.is64BitAbi(localApplicationInfo.primaryCpuAbi))) {
      str = "arm" + "64";
    }
    for (;;)
    {
      localObject2 = localApplicationInfo.getBaseCodePath();
      localApplicationInfo = null;
      try
      {
        str = DexFile.getDexFileOutputPath((String)localObject2, str);
        if (str == null)
        {
          return true;
          if (!VMRuntime.is64BitAbi(android.os.Build.SUPPORTED_ABIS[0])) {
            continue;
          }
          str = "arm" + "64";
        }
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          localObject1 = localApplicationInfo;
        }
        Object localObject1 = pinFile((String)localObject1, 0L, 0L, 52428800L);
        if (localObject1 != null) {
          this.mPinnedCameraFiles.add(localObject1);
        }
      }
    }
    return true;
  }
  
  private static PinnedFile pinFile(String paramString, long paramLong1, long paramLong2, long paramLong3)
  {
    Object localObject1 = new FileDescriptor();
    try
    {
      Object localObject2 = Os.open(paramString, OsConstants.O_RDONLY | OsConstants.O_CLOEXEC | OsConstants.O_NOFOLLOW, OsConstants.O_RDONLY);
      localObject1 = localObject2;
      StructStat localStructStat = Os.fstat((FileDescriptor)localObject2);
      localObject1 = localObject2;
      if (paramLong1 + paramLong2 > localStructStat.st_size)
      {
        localObject1 = localObject2;
        Os.close((FileDescriptor)localObject2);
        localObject1 = localObject2;
        Slog.e("PinnerService", "Failed to pin file " + paramString + ", request extends beyond end of file.  offset + length =  " + (paramLong1 + paramLong2) + ", file length = " + localStructStat.st_size);
        return null;
      }
      long l = paramLong2;
      if (paramLong2 == 0L)
      {
        localObject1 = localObject2;
        l = localStructStat.st_size - paramLong1;
      }
      if ((paramLong3 > 0L) && (l > paramLong3))
      {
        localObject1 = localObject2;
        Slog.e("PinnerService", "Could not pin file " + paramString + ", size = " + l + ", maxSize = " + paramLong3);
        localObject1 = localObject2;
        Os.close((FileDescriptor)localObject2);
        return null;
      }
      localObject1 = localObject2;
      paramLong1 = Os.mmap(0L, l, OsConstants.PROT_READ, OsConstants.MAP_PRIVATE, (FileDescriptor)localObject2, paramLong1);
      localObject1 = localObject2;
      Os.close((FileDescriptor)localObject2);
      localObject1 = localObject2;
      Os.mlock(paramLong1, l);
      localObject1 = localObject2;
      localObject2 = new PinnedFile(paramLong1, l, paramString);
      return (PinnedFile)localObject2;
    }
    catch (ErrnoException localErrnoException)
    {
      Slog.e("PinnerService", "Could not pin file " + paramString + " with error " + localErrnoException.getMessage());
      if (!((FileDescriptor)localObject1).valid()) {}
    }
    try
    {
      Os.close((FileDescriptor)localObject1);
      return null;
    }
    catch (ErrnoException paramString)
    {
      for (;;)
      {
        Slog.e("PinnerService", "Failed to close fd, error = " + paramString.getMessage());
      }
    }
  }
  
  private void unpinCameraApp()
  {
    int i = 0;
    while (i < this.mPinnedCameraFiles.size())
    {
      unpinFile((PinnedFile)this.mPinnedCameraFiles.get(i));
      i += 1;
    }
    this.mPinnedCameraFiles.clear();
  }
  
  private static boolean unpinFile(PinnedFile paramPinnedFile)
  {
    try
    {
      Os.munlock(paramPinnedFile.mAddress, paramPinnedFile.mLength);
      return true;
    }
    catch (ErrnoException localErrnoException)
    {
      Slog.e("PinnerService", "Failed to unpin file " + paramPinnedFile.mFilename + " with error " + localErrnoException.getMessage());
    }
    return false;
  }
  
  public void onStart()
  {
    this.mBinderService = new BinderService(null);
    publishBinderService("pinner", this.mBinderService);
    this.mPinnerHandler.obtainMessage(4001).sendToTarget();
    this.mPinnerHandler.obtainMessage(4000, 0, 0).sendToTarget();
  }
  
  public void onSwitchUser(int paramInt)
  {
    this.mPinnerHandler.obtainMessage(4000, paramInt, 0).sendToTarget();
  }
  
  private final class BinderService
    extends Binder
  {
    private BinderService() {}
    
    protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      PinnerService.-get0(PinnerService.this).enforceCallingOrSelfPermission("android.permission.DUMP", "PinnerService");
      paramPrintWriter.println("Pinned Files:");
      for (int i = 0;; i = 0) {
        try
        {
          while (i < PinnerService.-get2(PinnerService.this).size())
          {
            paramPrintWriter.println(((PinnerService.PinnedFile)PinnerService.-get2(PinnerService.this).get(i)).mFilename);
            i += 1;
            continue;
            while (i < PinnerService.-get1(PinnerService.this).size())
            {
              paramPrintWriter.println(((PinnerService.PinnedFile)PinnerService.-get1(PinnerService.this).get(i)).mFilename);
              i += 1;
            }
            return;
          }
        }
        finally {}
      }
    }
  }
  
  private static class PinnedFile
  {
    long mAddress;
    String mFilename;
    long mLength;
    
    PinnedFile(long paramLong1, long paramLong2, String paramString)
    {
      this.mAddress = paramLong1;
      this.mLength = paramLong2;
      this.mFilename = paramString;
    }
  }
  
  final class PinnerHandler
    extends Handler
  {
    static final int PIN_CAMERA_MSG = 4000;
    static final int PIN_ONSTART_MSG = 4001;
    
    public PinnerHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        super.handleMessage(paramMessage);
        return;
      case 4000: 
        PinnerService.-wrap0(PinnerService.this, paramMessage.arg1);
        return;
      }
      PinnerService.-wrap1(PinnerService.this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/PinnerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */