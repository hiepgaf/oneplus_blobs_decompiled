package com.android.server.wallpaper;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.IUserSwitchObserver.Stub;
import android.app.IWallpaperManager.Stub;
import android.app.IWallpaperManagerCallback;
import android.app.PendingIntent;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.System;
import android.service.wallpaper.IWallpaperConnection.Stub;
import android.service.wallpaper.IWallpaperEngine;
import android.service.wallpaper.IWallpaperService;
import android.service.wallpaper.IWallpaperService.Stub;
import android.system.ErrnoException;
import android.system.Os;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import android.view.WindowManager;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.JournaledFile;
import com.android.server.FgThread;
import com.android.server.SystemService;
import com.android.server.am.OnePlusAppBootManager;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class WallpaperManagerService
  extends IWallpaperManager.Stub
{
  static final boolean DEBUG = false;
  static final boolean DEBUG_ONEPLUS = true;
  static final int MAX_WALLPAPER_COMPONENT_LOG_LENGTH = 128;
  static final long MIN_WALLPAPER_CRASH_TIME = 10000L;
  static final String TAG = "WallpaperManagerService";
  static final String WALLPAPER = "wallpaper_orig";
  static final String WALLPAPER_CROP = "wallpaper";
  static final String WALLPAPER_INFO = "wallpaper_info.xml";
  static final String WALLPAPER_LOCK_CROP = "wallpaper_lock";
  static final String WALLPAPER_LOCK_ORIG = "wallpaper_lock_orig";
  static final String[] sPerUserFiles = { "wallpaper_orig", "wallpaper", "wallpaper_lock_orig", "wallpaper_lock", "wallpaper_info.xml" };
  final AppOpsManager mAppOpsManager;
  final Context mContext;
  int mCurrentUserId;
  final IPackageManager mIPackageManager;
  final IWindowManager mIWindowManager;
  final ComponentName mImageWallpaper;
  IWallpaperManagerCallback mKeyguardListener;
  WallpaperData mLastWallpaper;
  final Object mLock = new Object();
  final SparseArray<WallpaperData> mLockWallpaperMap = new SparseArray();
  final MyPackageMonitor mMonitor;
  protected Runnable mReadyToBeResetRunnable = null;
  boolean mShuttingDown;
  final SparseArray<Boolean> mUserRestorecon = new SparseArray();
  boolean mWaitingForUnlock;
  int mWallpaperId;
  final SparseArray<WallpaperData> mWallpaperMap = new SparseArray();
  
  public WallpaperManagerService(Context paramContext)
  {
    Slog.v("WallpaperManagerService", "WallpaperService startup");
    this.mContext = paramContext;
    this.mShuttingDown = false;
    this.mImageWallpaper = ComponentName.unflattenFromString(paramContext.getResources().getString(17039422));
    this.mIWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    this.mIPackageManager = AppGlobals.getPackageManager();
    this.mAppOpsManager = ((AppOpsManager)this.mContext.getSystemService("appops"));
    this.mMonitor = new MyPackageMonitor();
    this.mMonitor.register(paramContext, null, UserHandle.ALL, true);
    getWallpaperDir(0).mkdirs();
    loadSettingsLocked(0, false);
  }
  
  private void checkPermission(String paramString)
  {
    if (this.mContext.checkCallingOrSelfPermission(paramString) != 0) {
      throw new SecurityException("Access denied to process: " + Binder.getCallingPid() + ", must have permission " + paramString);
    }
  }
  
  private void ensureSaneWallpaperData(WallpaperData paramWallpaperData)
  {
    int i = getMaximumSizeDimension();
    if (paramWallpaperData.width < i) {
      paramWallpaperData.width = i;
    }
    if (paramWallpaperData.height < i) {
      paramWallpaperData.height = i;
    }
    if ((paramWallpaperData.cropHint.width() <= 0) || (paramWallpaperData.cropHint.height() <= 0)) {
      paramWallpaperData.cropHint.set(0, 0, paramWallpaperData.width, paramWallpaperData.height);
    }
  }
  
  /* Error */
  private void generateCrop(WallpaperData paramWallpaperData)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 6
    //   3: iconst_0
    //   4: istore 5
    //   6: new 291	android/graphics/Rect
    //   9: dup
    //   10: aload_1
    //   11: getfield 289	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropHint	Landroid/graphics/Rect;
    //   14: invokespecial 304	android/graphics/Rect:<init>	(Landroid/graphics/Rect;)V
    //   17: astore 14
    //   19: new 306	android/graphics/BitmapFactory$Options
    //   22: dup
    //   23: invokespecial 307	android/graphics/BitmapFactory$Options:<init>	()V
    //   26: astore 7
    //   28: aload 7
    //   30: iconst_1
    //   31: putfield 310	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   34: aload_1
    //   35: getfield 314	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperFile	Ljava/io/File;
    //   38: invokevirtual 317	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   41: aload 7
    //   43: invokestatic 323	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   46: pop
    //   47: aload 7
    //   49: getfield 326	android/graphics/BitmapFactory$Options:outWidth	I
    //   52: ifle +11 -> 63
    //   55: aload 7
    //   57: getfield 329	android/graphics/BitmapFactory$Options:outHeight	I
    //   60: ifgt +59 -> 119
    //   63: ldc 48
    //   65: ldc_w 331
    //   68: invokestatic 334	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   71: pop
    //   72: iconst_0
    //   73: istore 5
    //   75: iload 5
    //   77: ifne +20 -> 97
    //   80: ldc 48
    //   82: ldc_w 336
    //   85: invokestatic 334	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   88: pop
    //   89: aload_1
    //   90: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   93: invokevirtual 342	java/io/File:delete	()Z
    //   96: pop
    //   97: aload_1
    //   98: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   101: invokevirtual 345	java/io/File:exists	()Z
    //   104: ifeq +14 -> 118
    //   107: aload_1
    //   108: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   111: invokevirtual 349	java/io/File:getAbsoluteFile	()Ljava/io/File;
    //   114: invokestatic 355	android/os/SELinux:restorecon	(Ljava/io/File;)Z
    //   117: pop
    //   118: return
    //   119: iconst_0
    //   120: istore_3
    //   121: aload 14
    //   123: invokevirtual 358	android/graphics/Rect:isEmpty	()Z
    //   126: ifeq +119 -> 245
    //   129: aload 14
    //   131: iconst_0
    //   132: putfield 361	android/graphics/Rect:top	I
    //   135: aload 14
    //   137: iconst_0
    //   138: putfield 364	android/graphics/Rect:left	I
    //   141: aload 14
    //   143: aload 7
    //   145: getfield 326	android/graphics/BitmapFactory$Options:outWidth	I
    //   148: putfield 367	android/graphics/Rect:right	I
    //   151: aload 14
    //   153: aload 7
    //   155: getfield 329	android/graphics/BitmapFactory$Options:outHeight	I
    //   158: putfield 370	android/graphics/Rect:bottom	I
    //   161: aload_1
    //   162: getfield 285	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:height	I
    //   165: aload 14
    //   167: invokevirtual 295	android/graphics/Rect:height	()I
    //   170: if_icmpeq +214 -> 384
    //   173: iconst_1
    //   174: istore 4
    //   176: iload_3
    //   177: ifne +8 -> 185
    //   180: iload 4
    //   182: ifeq +208 -> 390
    //   185: aconst_null
    //   186: astore 11
    //   188: aconst_null
    //   189: astore 8
    //   191: aconst_null
    //   192: astore 13
    //   194: aconst_null
    //   195: astore 10
    //   197: aconst_null
    //   198: astore 9
    //   200: aconst_null
    //   201: astore 12
    //   203: aload_1
    //   204: getfield 314	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperFile	Ljava/io/File;
    //   207: invokevirtual 317	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   210: iconst_0
    //   211: invokestatic 376	android/graphics/BitmapRegionDecoder:newInstance	(Ljava/lang/String;Z)Landroid/graphics/BitmapRegionDecoder;
    //   214: astore 15
    //   216: aload 14
    //   218: invokevirtual 295	android/graphics/Rect:height	()I
    //   221: aload_1
    //   222: getfield 285	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:height	I
    //   225: idiv
    //   226: istore 4
    //   228: iconst_1
    //   229: istore_3
    //   230: iload_3
    //   231: iconst_2
    //   232: imul
    //   233: iload 4
    //   235: if_icmpge +192 -> 427
    //   238: iload_3
    //   239: iconst_2
    //   240: imul
    //   241: istore_3
    //   242: goto -12 -> 230
    //   245: aload 14
    //   247: getfield 367	android/graphics/Rect:right	I
    //   250: aload 7
    //   252: getfield 326	android/graphics/BitmapFactory$Options:outWidth	I
    //   255: if_icmple +108 -> 363
    //   258: aload 7
    //   260: getfield 326	android/graphics/BitmapFactory$Options:outWidth	I
    //   263: aload 14
    //   265: getfield 367	android/graphics/Rect:right	I
    //   268: isub
    //   269: istore_3
    //   270: aload 14
    //   272: getfield 370	android/graphics/Rect:bottom	I
    //   275: aload 7
    //   277: getfield 329	android/graphics/BitmapFactory$Options:outHeight	I
    //   280: if_icmple +88 -> 368
    //   283: aload 7
    //   285: getfield 329	android/graphics/BitmapFactory$Options:outHeight	I
    //   288: aload 14
    //   290: getfield 370	android/graphics/Rect:bottom	I
    //   293: isub
    //   294: istore 4
    //   296: aload 14
    //   298: iload_3
    //   299: iload 4
    //   301: invokevirtual 380	android/graphics/Rect:offset	(II)V
    //   304: aload 14
    //   306: getfield 364	android/graphics/Rect:left	I
    //   309: ifge +9 -> 318
    //   312: aload 14
    //   314: iconst_0
    //   315: putfield 364	android/graphics/Rect:left	I
    //   318: aload 14
    //   320: getfield 361	android/graphics/Rect:top	I
    //   323: ifge +9 -> 332
    //   326: aload 14
    //   328: iconst_0
    //   329: putfield 361	android/graphics/Rect:top	I
    //   332: aload 7
    //   334: getfield 329	android/graphics/BitmapFactory$Options:outHeight	I
    //   337: aload 14
    //   339: invokevirtual 295	android/graphics/Rect:height	()I
    //   342: if_icmpgt +32 -> 374
    //   345: aload 7
    //   347: getfield 326	android/graphics/BitmapFactory$Options:outWidth	I
    //   350: aload 14
    //   352: invokevirtual 293	android/graphics/Rect:width	()I
    //   355: if_icmple +24 -> 379
    //   358: iconst_1
    //   359: istore_3
    //   360: goto -199 -> 161
    //   363: iconst_0
    //   364: istore_3
    //   365: goto -95 -> 270
    //   368: iconst_0
    //   369: istore 4
    //   371: goto -75 -> 296
    //   374: iconst_1
    //   375: istore_3
    //   376: goto -215 -> 161
    //   379: iconst_0
    //   380: istore_3
    //   381: goto -220 -> 161
    //   384: iconst_0
    //   385: istore 4
    //   387: goto -211 -> 176
    //   390: aload_1
    //   391: getfield 314	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperFile	Ljava/io/File;
    //   394: aload_1
    //   395: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   398: invokestatic 386	android/os/FileUtils:copyFile	(Ljava/io/File;Ljava/io/File;)Z
    //   401: istore 6
    //   403: iload 6
    //   405: istore 5
    //   407: iload 6
    //   409: ifne -334 -> 75
    //   412: aload_1
    //   413: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   416: invokevirtual 342	java/io/File:delete	()Z
    //   419: pop
    //   420: iload 6
    //   422: istore 5
    //   424: goto -349 -> 75
    //   427: iload_3
    //   428: iconst_1
    //   429: if_icmple +73 -> 502
    //   432: new 306	android/graphics/BitmapFactory$Options
    //   435: dup
    //   436: invokespecial 307	android/graphics/BitmapFactory$Options:<init>	()V
    //   439: astore 7
    //   441: aload 7
    //   443: iload_3
    //   444: putfield 389	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   447: aload 15
    //   449: aload 14
    //   451: aload 7
    //   453: invokevirtual 393	android/graphics/BitmapRegionDecoder:decodeRegion	(Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   456: astore 7
    //   458: aload 15
    //   460: invokevirtual 396	android/graphics/BitmapRegionDecoder:recycle	()V
    //   463: aload 7
    //   465: ifnonnull +43 -> 508
    //   468: ldc 48
    //   470: ldc_w 398
    //   473: invokestatic 334	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   476: pop
    //   477: iload 6
    //   479: istore 5
    //   481: aload 13
    //   483: astore 8
    //   485: aload 12
    //   487: astore 7
    //   489: aload 7
    //   491: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   494: aload 8
    //   496: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   499: goto -424 -> 75
    //   502: aconst_null
    //   503: astore 7
    //   505: goto -58 -> 447
    //   508: aload 14
    //   510: iconst_0
    //   511: iconst_0
    //   512: invokevirtual 407	android/graphics/Rect:offsetTo	(II)V
    //   515: aload 14
    //   517: aload 14
    //   519: getfield 367	android/graphics/Rect:right	I
    //   522: iload_3
    //   523: idiv
    //   524: putfield 367	android/graphics/Rect:right	I
    //   527: aload 14
    //   529: aload 14
    //   531: getfield 370	android/graphics/Rect:bottom	I
    //   534: iload_3
    //   535: idiv
    //   536: putfield 370	android/graphics/Rect:bottom	I
    //   539: aload_1
    //   540: getfield 285	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:height	I
    //   543: i2f
    //   544: aload 14
    //   546: invokevirtual 295	android/graphics/Rect:height	()I
    //   549: i2f
    //   550: fdiv
    //   551: fstore_2
    //   552: aload 7
    //   554: aload 14
    //   556: invokevirtual 293	android/graphics/Rect:width	()I
    //   559: i2f
    //   560: fload_2
    //   561: fmul
    //   562: f2i
    //   563: aload_1
    //   564: getfield 285	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:height	I
    //   567: iconst_1
    //   568: invokestatic 413	android/graphics/Bitmap:createScaledBitmap	(Landroid/graphics/Bitmap;IIZ)Landroid/graphics/Bitmap;
    //   571: astore 12
    //   573: ldc 48
    //   575: ldc_w 415
    //   578: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   581: pop
    //   582: ldc 48
    //   584: new 251	java/lang/StringBuilder
    //   587: dup
    //   588: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   591: ldc_w 417
    //   594: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   597: aload_1
    //   598: getfield 282	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:width	I
    //   601: invokevirtual 267	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   604: ldc_w 419
    //   607: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   610: aload_1
    //   611: getfield 285	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:height	I
    //   614: invokevirtual 267	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   617: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   620: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   623: pop
    //   624: ldc 48
    //   626: new 251	java/lang/StringBuilder
    //   629: dup
    //   630: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   633: ldc_w 421
    //   636: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   639: aload 12
    //   641: invokevirtual 424	android/graphics/Bitmap:getWidth	()I
    //   644: invokevirtual 267	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   647: ldc_w 419
    //   650: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   653: aload 12
    //   655: invokevirtual 427	android/graphics/Bitmap:getHeight	()I
    //   658: invokevirtual 267	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   661: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   664: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   667: pop
    //   668: new 429	java/io/FileOutputStream
    //   671: dup
    //   672: aload_1
    //   673: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   676: invokespecial 432	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   679: astore 7
    //   681: new 434	java/io/BufferedOutputStream
    //   684: dup
    //   685: aload 7
    //   687: ldc_w 435
    //   690: invokespecial 438	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;I)V
    //   693: astore 8
    //   695: aload 12
    //   697: getstatic 444	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   700: bipush 100
    //   702: aload 8
    //   704: invokevirtual 448	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   707: pop
    //   708: aload 8
    //   710: invokevirtual 451	java/io/BufferedOutputStream:flush	()V
    //   713: iconst_1
    //   714: istore 5
    //   716: aload 7
    //   718: astore 9
    //   720: aload 8
    //   722: astore 7
    //   724: aload 9
    //   726: astore 8
    //   728: goto -239 -> 489
    //   731: astore 7
    //   733: aload 11
    //   735: astore 7
    //   737: aload 10
    //   739: astore 8
    //   741: aload 8
    //   743: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   746: aload 7
    //   748: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   751: goto -676 -> 75
    //   754: astore 7
    //   756: aload 8
    //   758: astore_1
    //   759: aload 9
    //   761: astore 8
    //   763: aload 8
    //   765: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   768: aload_1
    //   769: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   772: aload 7
    //   774: athrow
    //   775: astore 10
    //   777: aload 7
    //   779: astore_1
    //   780: aload 9
    //   782: astore 8
    //   784: aload 10
    //   786: astore 7
    //   788: goto -25 -> 763
    //   791: astore 9
    //   793: aload 7
    //   795: astore_1
    //   796: aload 9
    //   798: astore 7
    //   800: goto -37 -> 763
    //   803: astore 8
    //   805: aload 10
    //   807: astore 8
    //   809: goto -68 -> 741
    //   812: astore 9
    //   814: goto -73 -> 741
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	817	0	this	WallpaperManagerService
    //   0	817	1	paramWallpaperData	WallpaperData
    //   551	10	2	f	float
    //   120	416	3	i	int
    //   174	212	4	j	int
    //   4	711	5	k	int
    //   1	477	6	bool	boolean
    //   26	697	7	localObject1	Object
    //   731	1	7	localException1	Exception
    //   735	12	7	localObject2	Object
    //   754	24	7	localObject3	Object
    //   786	13	7	localObject4	Object
    //   189	594	8	localObject5	Object
    //   803	1	8	localException2	Exception
    //   807	1	8	localObject6	Object
    //   198	583	9	localObject7	Object
    //   791	6	9	localObject8	Object
    //   812	1	9	localException3	Exception
    //   195	543	10	localObject9	Object
    //   775	31	10	localObject10	Object
    //   186	548	11	localObject11	Object
    //   201	495	12	localBitmap	Bitmap
    //   192	290	13	localObject12	Object
    //   17	538	14	localRect	Rect
    //   214	245	15	localBitmapRegionDecoder	android.graphics.BitmapRegionDecoder
    // Exception table:
    //   from	to	target	type
    //   203	228	731	java/lang/Exception
    //   432	447	731	java/lang/Exception
    //   447	463	731	java/lang/Exception
    //   468	477	731	java/lang/Exception
    //   508	681	731	java/lang/Exception
    //   203	228	754	finally
    //   432	447	754	finally
    //   447	463	754	finally
    //   468	477	754	finally
    //   508	681	754	finally
    //   681	695	775	finally
    //   695	713	791	finally
    //   681	695	803	java/lang/Exception
    //   695	713	812	java/lang/Exception
  }
  
  private int getAttributeInt(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramInt;
    }
    return Integer.parseInt(paramXmlPullParser);
  }
  
  private Point getDefaultDisplaySize()
  {
    Point localPoint = new Point();
    ((WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay().getRealSize(localPoint);
    return localPoint;
  }
  
  private int getMaximumSizeDimension()
  {
    return ((WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay().getMaximumSizeDimension();
  }
  
  private static File getWallpaperDir(int paramInt)
  {
    return Environment.getUserSystemDirectory(paramInt);
  }
  
  private WallpaperData getWallpaperSafeLocked(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 2) {}
    for (Object localObject2 = this.mLockWallpaperMap;; localObject2 = this.mWallpaperMap)
    {
      localObject1 = (WallpaperData)((SparseArray)localObject2).get(paramInt1);
      if ((localObject1 == null) || (this.mWaitingForUnlock))
      {
        if (this.mWaitingForUnlock) {
          Slog.v("WallpaperManagerService", "Force to generate wallpaper information from setting file while waiting for unlock");
        }
        loadSettingsLocked(paramInt1, false);
        localObject2 = (WallpaperData)((SparseArray)localObject2).get(paramInt1);
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          if (paramInt2 != 2) {
            break;
          }
          localObject1 = new WallpaperData(paramInt1, "wallpaper_lock_orig", "wallpaper_lock");
          this.mLockWallpaperMap.put(paramInt1, localObject1);
          ensureSaneWallpaperData((WallpaperData)localObject1);
        }
      }
      return (WallpaperData)localObject1;
    }
    Slog.wtf("WallpaperManagerService", "Didn't find wallpaper in non-lock case!");
    Object localObject1 = new WallpaperData(paramInt1, "wallpaper_orig", "wallpaper");
    this.mWallpaperMap.put(paramInt1, localObject1);
    ensureSaneWallpaperData((WallpaperData)localObject1);
    return (WallpaperData)localObject1;
  }
  
  /* Error */
  private void loadSettingsLocked(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: iload_1
    //   1: invokestatic 527	com/android/server/wallpaper/WallpaperManagerService:makeJournaledFile	(I)Lcom/android/internal/util/JournaledFile;
    //   4: astore 5
    //   6: aconst_null
    //   7: astore 8
    //   9: aconst_null
    //   10: astore 9
    //   12: aconst_null
    //   13: astore 10
    //   15: aconst_null
    //   16: astore 11
    //   18: aconst_null
    //   19: astore 12
    //   21: aconst_null
    //   22: astore 7
    //   24: aload 5
    //   26: invokevirtual 532	com/android/internal/util/JournaledFile:chooseForRead	()Ljava/io/File;
    //   29: astore 13
    //   31: aload 13
    //   33: invokevirtual 345	java/io/File:exists	()Z
    //   36: ifne +7 -> 43
    //   39: aload_0
    //   40: invokespecial 535	com/android/server/wallpaper/WallpaperManagerService:migrateFromOld	()V
    //   43: aload_0
    //   44: getfield 147	com/android/server/wallpaper/WallpaperManagerService:mWallpaperMap	Landroid/util/SparseArray;
    //   47: iload_1
    //   48: invokevirtual 493	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   51: checkcast 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   54: astore 5
    //   56: aload 5
    //   58: astore 6
    //   60: aload 5
    //   62: ifnonnull +55 -> 117
    //   65: new 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   68: dup
    //   69: iload_1
    //   70: ldc 51
    //   72: ldc 54
    //   74: invokespecial 500	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:<init>	(ILjava/lang/String;Ljava/lang/String;)V
    //   77: astore 5
    //   79: aload 5
    //   81: iconst_1
    //   82: putfield 538	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:allowBackup	Z
    //   85: aload_0
    //   86: getfield 147	com/android/server/wallpaper/WallpaperManagerService:mWallpaperMap	Landroid/util/SparseArray;
    //   89: iload_1
    //   90: aload 5
    //   92: invokevirtual 504	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   95: aload 5
    //   97: astore 6
    //   99: aload 5
    //   101: invokevirtual 541	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropExists	()Z
    //   104: ifne +13 -> 117
    //   107: aload_0
    //   108: aload 5
    //   110: invokespecial 107	com/android/server/wallpaper/WallpaperManagerService:generateCrop	(Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;)V
    //   113: aload 5
    //   115: astore 6
    //   117: iconst_0
    //   118: istore_3
    //   119: new 543	java/io/FileInputStream
    //   122: dup
    //   123: aload 13
    //   125: invokespecial 544	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   128: astore 5
    //   130: invokestatic 550	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   133: astore 9
    //   135: aload 9
    //   137: aload 5
    //   139: getstatic 556	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   142: invokevirtual 561	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   145: invokeinterface 565 3 0
    //   150: aload 9
    //   152: invokeinterface 568 1 0
    //   157: istore 4
    //   159: iload 4
    //   161: iconst_2
    //   162: if_icmpne +98 -> 260
    //   165: aload 9
    //   167: invokeinterface 571 1 0
    //   172: astore 7
    //   174: ldc_w 573
    //   177: aload 7
    //   179: invokevirtual 577	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   182: ifeq +184 -> 366
    //   185: aload_0
    //   186: aload 9
    //   188: aload 6
    //   190: iload_2
    //   191: invokespecial 581	com/android/server/wallpaper/WallpaperManagerService:parseWallpaperAttributes	(Lorg/xmlpull/v1/XmlPullParser;Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;Z)V
    //   194: aload 9
    //   196: aconst_null
    //   197: ldc_w 583
    //   200: invokeinterface 459 3 0
    //   205: astore 7
    //   207: aload 7
    //   209: ifnull +151 -> 360
    //   212: aload 7
    //   214: invokestatic 184	android/content/ComponentName:unflattenFromString	(Ljava/lang/String;)Landroid/content/ComponentName;
    //   217: astore 7
    //   219: aload 6
    //   221: aload 7
    //   223: putfield 586	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:nextWallpaperComponent	Landroid/content/ComponentName;
    //   226: aload 6
    //   228: getfield 586	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:nextWallpaperComponent	Landroid/content/ComponentName;
    //   231: ifnull +20 -> 251
    //   234: ldc_w 588
    //   237: aload 6
    //   239: getfield 586	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:nextWallpaperComponent	Landroid/content/ComponentName;
    //   242: invokevirtual 591	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   245: invokevirtual 577	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   248: ifeq +12 -> 260
    //   251: aload 6
    //   253: aload_0
    //   254: getfield 186	com/android/server/wallpaper/WallpaperManagerService:mImageWallpaper	Landroid/content/ComponentName;
    //   257: putfield 586	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:nextWallpaperComponent	Landroid/content/ComponentName;
    //   260: iload 4
    //   262: iconst_1
    //   263: if_icmpne -113 -> 150
    //   266: iconst_1
    //   267: istore_3
    //   268: aload 5
    //   270: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   273: iload_3
    //   274: ifne +410 -> 684
    //   277: aload 6
    //   279: iconst_m1
    //   280: putfield 282	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:width	I
    //   283: aload 6
    //   285: iconst_m1
    //   286: putfield 285	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:height	I
    //   289: aload 6
    //   291: getfield 289	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropHint	Landroid/graphics/Rect;
    //   294: iconst_0
    //   295: iconst_0
    //   296: iconst_0
    //   297: iconst_0
    //   298: invokevirtual 299	android/graphics/Rect:set	(IIII)V
    //   301: aload 6
    //   303: getfield 594	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:padding	Landroid/graphics/Rect;
    //   306: iconst_0
    //   307: iconst_0
    //   308: iconst_0
    //   309: iconst_0
    //   310: invokevirtual 299	android/graphics/Rect:set	(IIII)V
    //   313: aload 6
    //   315: ldc_w 596
    //   318: putfield 598	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:name	Ljava/lang/String;
    //   321: aload_0
    //   322: getfield 149	com/android/server/wallpaper/WallpaperManagerService:mLockWallpaperMap	Landroid/util/SparseArray;
    //   325: iload_1
    //   326: invokevirtual 601	android/util/SparseArray:remove	(I)V
    //   329: aload_0
    //   330: aload 6
    //   332: invokespecial 506	com/android/server/wallpaper/WallpaperManagerService:ensureSaneWallpaperData	(Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;)V
    //   335: aload_0
    //   336: getfield 149	com/android/server/wallpaper/WallpaperManagerService:mLockWallpaperMap	Landroid/util/SparseArray;
    //   339: iload_1
    //   340: invokevirtual 493	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   343: checkcast 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   346: astore 5
    //   348: aload 5
    //   350: ifnull +9 -> 359
    //   353: aload_0
    //   354: aload 5
    //   356: invokespecial 506	com/android/server/wallpaper/WallpaperManagerService:ensureSaneWallpaperData	(Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;)V
    //   359: return
    //   360: aconst_null
    //   361: astore 7
    //   363: goto -144 -> 219
    //   366: ldc_w 603
    //   369: aload 7
    //   371: invokevirtual 577	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   374: ifeq -114 -> 260
    //   377: aload_0
    //   378: getfield 149	com/android/server/wallpaper/WallpaperManagerService:mLockWallpaperMap	Landroid/util/SparseArray;
    //   381: iload_1
    //   382: invokevirtual 493	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   385: checkcast 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   388: astore 8
    //   390: aload 8
    //   392: astore 7
    //   394: aload 8
    //   396: ifnonnull +27 -> 423
    //   399: new 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   402: dup
    //   403: iload_1
    //   404: ldc 63
    //   406: ldc 60
    //   408: invokespecial 500	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:<init>	(ILjava/lang/String;Ljava/lang/String;)V
    //   411: astore 7
    //   413: aload_0
    //   414: getfield 149	com/android/server/wallpaper/WallpaperManagerService:mLockWallpaperMap	Landroid/util/SparseArray;
    //   417: iload_1
    //   418: aload 7
    //   420: invokevirtual 504	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   423: aload_0
    //   424: aload 9
    //   426: aload 7
    //   428: iconst_0
    //   429: invokespecial 581	com/android/server/wallpaper/WallpaperManagerService:parseWallpaperAttributes	(Lorg/xmlpull/v1/XmlPullParser;Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;Z)V
    //   432: goto -172 -> 260
    //   435: astore 7
    //   437: ldc 48
    //   439: ldc_w 605
    //   442: invokestatic 608	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   445: pop
    //   446: goto -178 -> 268
    //   449: astore 7
    //   451: aload 8
    //   453: astore 5
    //   455: ldc 48
    //   457: new 251	java/lang/StringBuilder
    //   460: dup
    //   461: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   464: ldc_w 610
    //   467: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   470: aload 13
    //   472: invokevirtual 613	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   475: ldc_w 615
    //   478: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   481: aload 7
    //   483: invokevirtual 613	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   486: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   489: invokestatic 608	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   492: pop
    //   493: goto -225 -> 268
    //   496: astore 7
    //   498: aload 9
    //   500: astore 5
    //   502: ldc 48
    //   504: new 251	java/lang/StringBuilder
    //   507: dup
    //   508: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   511: ldc_w 610
    //   514: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   517: aload 13
    //   519: invokevirtual 613	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   522: ldc_w 615
    //   525: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   528: aload 7
    //   530: invokevirtual 613	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   533: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   536: invokestatic 608	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   539: pop
    //   540: goto -272 -> 268
    //   543: astore 7
    //   545: aload 10
    //   547: astore 5
    //   549: ldc 48
    //   551: new 251	java/lang/StringBuilder
    //   554: dup
    //   555: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   558: ldc_w 610
    //   561: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   564: aload 13
    //   566: invokevirtual 613	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   569: ldc_w 615
    //   572: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   575: aload 7
    //   577: invokevirtual 613	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   580: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   583: invokestatic 608	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   586: pop
    //   587: goto -319 -> 268
    //   590: astore 7
    //   592: aload 11
    //   594: astore 5
    //   596: ldc 48
    //   598: new 251	java/lang/StringBuilder
    //   601: dup
    //   602: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   605: ldc_w 610
    //   608: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   611: aload 13
    //   613: invokevirtual 613	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   616: ldc_w 615
    //   619: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   622: aload 7
    //   624: invokevirtual 613	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   627: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   630: invokestatic 608	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   633: pop
    //   634: goto -366 -> 268
    //   637: astore 7
    //   639: aload 12
    //   641: astore 5
    //   643: ldc 48
    //   645: new 251	java/lang/StringBuilder
    //   648: dup
    //   649: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   652: ldc_w 610
    //   655: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   658: aload 13
    //   660: invokevirtual 613	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   663: ldc_w 615
    //   666: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   669: aload 7
    //   671: invokevirtual 613	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   674: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   677: invokestatic 608	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   680: pop
    //   681: goto -413 -> 268
    //   684: aload 6
    //   686: getfield 618	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperId	I
    //   689: ifgt -360 -> 329
    //   692: aload 6
    //   694: aload_0
    //   695: invokevirtual 621	com/android/server/wallpaper/WallpaperManagerService:makeWallpaperIdLocked	()I
    //   698: putfield 618	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperId	I
    //   701: goto -372 -> 329
    //   704: astore 5
    //   706: aload 7
    //   708: astore 5
    //   710: goto -273 -> 437
    //   713: astore 7
    //   715: goto -72 -> 643
    //   718: astore 7
    //   720: goto -124 -> 596
    //   723: astore 7
    //   725: goto -176 -> 549
    //   728: astore 7
    //   730: goto -228 -> 502
    //   733: astore 7
    //   735: goto -280 -> 455
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	738	0	this	WallpaperManagerService
    //   0	738	1	paramInt	int
    //   0	738	2	paramBoolean	boolean
    //   118	156	3	i	int
    //   157	107	4	j	int
    //   4	638	5	localObject1	Object
    //   704	1	5	localFileNotFoundException1	FileNotFoundException
    //   708	1	5	localObject2	Object
    //   58	635	6	localObject3	Object
    //   22	405	7	localObject4	Object
    //   435	1	7	localFileNotFoundException2	FileNotFoundException
    //   449	33	7	localIndexOutOfBoundsException1	IndexOutOfBoundsException
    //   496	33	7	localIOException1	IOException
    //   543	33	7	localXmlPullParserException1	XmlPullParserException
    //   590	33	7	localNumberFormatException1	NumberFormatException
    //   637	70	7	localNullPointerException1	NullPointerException
    //   713	1	7	localNullPointerException2	NullPointerException
    //   718	1	7	localNumberFormatException2	NumberFormatException
    //   723	1	7	localXmlPullParserException2	XmlPullParserException
    //   728	1	7	localIOException2	IOException
    //   733	1	7	localIndexOutOfBoundsException2	IndexOutOfBoundsException
    //   7	445	8	localWallpaperData	WallpaperData
    //   10	489	9	localXmlPullParser	XmlPullParser
    //   13	533	10	localObject5	Object
    //   16	577	11	localObject6	Object
    //   19	621	12	localObject7	Object
    //   29	630	13	localFile	File
    // Exception table:
    //   from	to	target	type
    //   130	150	435	java/io/FileNotFoundException
    //   150	159	435	java/io/FileNotFoundException
    //   165	207	435	java/io/FileNotFoundException
    //   212	219	435	java/io/FileNotFoundException
    //   219	251	435	java/io/FileNotFoundException
    //   251	260	435	java/io/FileNotFoundException
    //   366	390	435	java/io/FileNotFoundException
    //   399	423	435	java/io/FileNotFoundException
    //   423	432	435	java/io/FileNotFoundException
    //   119	130	449	java/lang/IndexOutOfBoundsException
    //   119	130	496	java/io/IOException
    //   119	130	543	org/xmlpull/v1/XmlPullParserException
    //   119	130	590	java/lang/NumberFormatException
    //   119	130	637	java/lang/NullPointerException
    //   119	130	704	java/io/FileNotFoundException
    //   130	150	713	java/lang/NullPointerException
    //   150	159	713	java/lang/NullPointerException
    //   165	207	713	java/lang/NullPointerException
    //   212	219	713	java/lang/NullPointerException
    //   219	251	713	java/lang/NullPointerException
    //   251	260	713	java/lang/NullPointerException
    //   366	390	713	java/lang/NullPointerException
    //   399	423	713	java/lang/NullPointerException
    //   423	432	713	java/lang/NullPointerException
    //   130	150	718	java/lang/NumberFormatException
    //   150	159	718	java/lang/NumberFormatException
    //   165	207	718	java/lang/NumberFormatException
    //   212	219	718	java/lang/NumberFormatException
    //   219	251	718	java/lang/NumberFormatException
    //   251	260	718	java/lang/NumberFormatException
    //   366	390	718	java/lang/NumberFormatException
    //   399	423	718	java/lang/NumberFormatException
    //   423	432	718	java/lang/NumberFormatException
    //   130	150	723	org/xmlpull/v1/XmlPullParserException
    //   150	159	723	org/xmlpull/v1/XmlPullParserException
    //   165	207	723	org/xmlpull/v1/XmlPullParserException
    //   212	219	723	org/xmlpull/v1/XmlPullParserException
    //   219	251	723	org/xmlpull/v1/XmlPullParserException
    //   251	260	723	org/xmlpull/v1/XmlPullParserException
    //   366	390	723	org/xmlpull/v1/XmlPullParserException
    //   399	423	723	org/xmlpull/v1/XmlPullParserException
    //   423	432	723	org/xmlpull/v1/XmlPullParserException
    //   130	150	728	java/io/IOException
    //   150	159	728	java/io/IOException
    //   165	207	728	java/io/IOException
    //   212	219	728	java/io/IOException
    //   219	251	728	java/io/IOException
    //   251	260	728	java/io/IOException
    //   366	390	728	java/io/IOException
    //   399	423	728	java/io/IOException
    //   423	432	728	java/io/IOException
    //   130	150	733	java/lang/IndexOutOfBoundsException
    //   150	159	733	java/lang/IndexOutOfBoundsException
    //   165	207	733	java/lang/IndexOutOfBoundsException
    //   212	219	733	java/lang/IndexOutOfBoundsException
    //   219	251	733	java/lang/IndexOutOfBoundsException
    //   251	260	733	java/lang/IndexOutOfBoundsException
    //   366	390	733	java/lang/IndexOutOfBoundsException
    //   399	423	733	java/lang/IndexOutOfBoundsException
    //   423	432	733	java/lang/IndexOutOfBoundsException
  }
  
  private static JournaledFile makeJournaledFile(int paramInt)
  {
    String str = new File(getWallpaperDir(paramInt), "wallpaper_info.xml").getAbsolutePath();
    return new JournaledFile(new File(str), new File(str + ".tmp"));
  }
  
  private void migrateFromOld()
  {
    File localFile1 = new File("/data/data/com.android.settings/files/wallpaper");
    File localFile2 = new File("/data/system/wallpaper_info.xml");
    if (localFile1.exists()) {
      localFile1.renameTo(new File(getWallpaperDir(0), "wallpaper_orig"));
    }
    if (localFile2.exists()) {
      localFile2.renameTo(new File(getWallpaperDir(0), "wallpaper_info.xml"));
    }
  }
  
  private void migrateKeyguardWallpaper(int paramInt)
  {
    i = 0;
    if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "migrate_lockscreen_wallpaper", 0, paramInt) == 1) {
      i = 1;
    }
    if ((i != 0) || (this.mLockWallpaperMap.get(paramInt) != null)) {
      return;
    }
    WallpaperManager localWallpaperManager = (WallpaperManager)this.mContext.getSystemService("wallpaper");
    File localFile = new File(getWallpaperDir(paramInt), "keyguard_wallpaper");
    k = 0;
    j = 0;
    for (i = k;; i = j)
    {
      try
      {
        if (!localFile.exists()) {
          break;
        }
        i = k;
        Slog.d("WallpaperManagerService", "migrate old lockscreen wallpaper, " + paramInt);
        i = k;
        Object localObject = new BitmapFactory.Options();
        i = k;
        localObject = BitmapFactory.decodeFile(localFile.getAbsolutePath(), (BitmapFactory.Options)localObject);
        if (localObject == null) {
          break label219;
        }
        i = k;
        j = localWallpaperManager.setBitmap((Bitmap)localObject, null, true, 2);
        i = j;
        if (j != 0)
        {
          i = j;
          localFile.delete();
          i = j;
        }
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          label219:
          Slog.w("WallpaperManagerService", localIOException);
          continue;
          i = k;
          Slog.d("WallpaperManagerService", "set to default lockscreen wallpaper, " + paramInt + ", " + 84017154);
          i = k;
          j = localIOException.setResource(84017154, 2);
          i = j;
        }
      }
      Settings.System.putIntForUser(this.mContext.getContentResolver(), "migrate_lockscreen_wallpaper", 1, paramInt);
      Slog.d("WallpaperManagerService", "migrate result:" + i);
      return;
      i = k;
      Slog.w("WallpaperManagerService", "no bitmap");
    }
  }
  
  private void migrateSystemToLockWallpaperLocked(int paramInt)
  {
    WallpaperData localWallpaperData2 = (WallpaperData)this.mWallpaperMap.get(paramInt);
    if (localWallpaperData2 == null) {
      return;
    }
    WallpaperData localWallpaperData1 = new WallpaperData(paramInt, "wallpaper_lock_orig", "wallpaper_lock");
    localWallpaperData1.wallpaperId = localWallpaperData2.wallpaperId;
    localWallpaperData1.cropHint.set(localWallpaperData2.cropHint);
    localWallpaperData1.width = localWallpaperData2.width;
    localWallpaperData1.height = localWallpaperData2.height;
    localWallpaperData1.allowBackup = localWallpaperData2.allowBackup;
    try
    {
      Os.rename(localWallpaperData2.wallpaperFile.getAbsolutePath(), localWallpaperData1.wallpaperFile.getAbsolutePath());
      Os.rename(localWallpaperData2.cropFile.getAbsolutePath(), localWallpaperData1.cropFile.getAbsolutePath());
      this.mLockWallpaperMap.put(paramInt, localWallpaperData1);
      return;
    }
    catch (ErrnoException localErrnoException)
    {
      Slog.e("WallpaperManagerService", "Can't migrate system wallpaper: " + localErrnoException.getMessage());
      localWallpaperData1.wallpaperFile.delete();
      localWallpaperData1.cropFile.delete();
    }
  }
  
  private void notifyCallbacksLocked(WallpaperData paramWallpaperData)
  {
    int j = WallpaperData.-get0(paramWallpaperData).beginBroadcast();
    int i = 0;
    for (;;)
    {
      if (i < j) {}
      try
      {
        ((IWallpaperManagerCallback)WallpaperData.-get0(paramWallpaperData).getBroadcastItem(i)).onWallpaperChanged();
        i += 1;
        continue;
        WallpaperData.-get0(paramWallpaperData).finishBroadcast();
        paramWallpaperData = new Intent("android.intent.action.WALLPAPER_CHANGED");
        this.mContext.sendBroadcastAsUser(paramWallpaperData, new UserHandle(this.mCurrentUserId));
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  private void parseWallpaperAttributes(XmlPullParser paramXmlPullParser, WallpaperData paramWallpaperData, boolean paramBoolean)
  {
    String str = paramXmlPullParser.getAttributeValue(null, "id");
    if (str != null)
    {
      int i = Integer.parseInt(str);
      paramWallpaperData.wallpaperId = i;
      if (i > this.mWallpaperId) {
        this.mWallpaperId = i;
      }
    }
    for (;;)
    {
      if (!paramBoolean)
      {
        paramWallpaperData.width = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "width"));
        paramWallpaperData.height = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "height"));
      }
      paramWallpaperData.cropHint.left = getAttributeInt(paramXmlPullParser, "cropLeft", 0);
      paramWallpaperData.cropHint.top = getAttributeInt(paramXmlPullParser, "cropTop", 0);
      paramWallpaperData.cropHint.right = getAttributeInt(paramXmlPullParser, "cropRight", 0);
      paramWallpaperData.cropHint.bottom = getAttributeInt(paramXmlPullParser, "cropBottom", 0);
      paramWallpaperData.padding.left = getAttributeInt(paramXmlPullParser, "paddingLeft", 0);
      paramWallpaperData.padding.top = getAttributeInt(paramXmlPullParser, "paddingTop", 0);
      paramWallpaperData.padding.right = getAttributeInt(paramXmlPullParser, "paddingRight", 0);
      paramWallpaperData.padding.bottom = getAttributeInt(paramXmlPullParser, "paddingBottom", 0);
      paramWallpaperData.name = paramXmlPullParser.getAttributeValue(null, "name");
      paramWallpaperData.allowBackup = "true".equals(paramXmlPullParser.getAttributeValue(null, "backup"));
      return;
      paramWallpaperData.wallpaperId = makeWallpaperIdLocked();
    }
  }
  
  /* Error */
  private void saveSettingsLocked(int paramInt)
  {
    // Byte code:
    //   0: iload_1
    //   1: invokestatic 527	com/android/server/wallpaper/WallpaperManagerService:makeJournaledFile	(I)Lcom/android/internal/util/JournaledFile;
    //   4: astore 4
    //   6: aconst_null
    //   7: astore_3
    //   8: new 765	com/android/internal/util/FastXmlSerializer
    //   11: dup
    //   12: invokespecial 766	com/android/internal/util/FastXmlSerializer:<init>	()V
    //   15: astore 5
    //   17: new 429	java/io/FileOutputStream
    //   20: dup
    //   21: aload 4
    //   23: invokevirtual 769	com/android/internal/util/JournaledFile:chooseForWrite	()Ljava/io/File;
    //   26: iconst_0
    //   27: invokespecial 772	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
    //   30: astore 6
    //   32: new 434	java/io/BufferedOutputStream
    //   35: dup
    //   36: aload 6
    //   38: invokespecial 775	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   41: astore_2
    //   42: aload 5
    //   44: aload_2
    //   45: getstatic 556	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   48: invokevirtual 561	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   51: invokeinterface 781 3 0
    //   56: aload 5
    //   58: aconst_null
    //   59: iconst_1
    //   60: invokestatic 787	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   63: invokeinterface 791 3 0
    //   68: aload_0
    //   69: getfield 147	com/android/server/wallpaper/WallpaperManagerService:mWallpaperMap	Landroid/util/SparseArray;
    //   72: iload_1
    //   73: invokevirtual 493	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   76: checkcast 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   79: astore_3
    //   80: aload_3
    //   81: ifnull +13 -> 94
    //   84: aload_0
    //   85: aload 5
    //   87: ldc_w 573
    //   90: aload_3
    //   91: invokespecial 795	com/android/server/wallpaper/WallpaperManagerService:writeWallpaperAttributes	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;)V
    //   94: aload_0
    //   95: getfield 149	com/android/server/wallpaper/WallpaperManagerService:mLockWallpaperMap	Landroid/util/SparseArray;
    //   98: iload_1
    //   99: invokevirtual 493	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   102: checkcast 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   105: astore_3
    //   106: aload_3
    //   107: ifnull +13 -> 120
    //   110: aload_0
    //   111: aload 5
    //   113: ldc_w 603
    //   116: aload_3
    //   117: invokespecial 795	com/android/server/wallpaper/WallpaperManagerService:writeWallpaperAttributes	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;)V
    //   120: aload 5
    //   122: invokeinterface 798 1 0
    //   127: aload_2
    //   128: invokevirtual 451	java/io/BufferedOutputStream:flush	()V
    //   131: aload 6
    //   133: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   136: pop
    //   137: aload_2
    //   138: invokevirtual 805	java/io/BufferedOutputStream:close	()V
    //   141: aload 4
    //   143: invokevirtual 808	com/android/internal/util/JournaledFile:commit	()V
    //   146: return
    //   147: astore_2
    //   148: aload_3
    //   149: astore_2
    //   150: aload_2
    //   151: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   154: aload 4
    //   156: invokevirtual 811	com/android/internal/util/JournaledFile:rollback	()V
    //   159: return
    //   160: astore_2
    //   161: aload_3
    //   162: astore_2
    //   163: goto -13 -> 150
    //   166: astore_3
    //   167: goto -17 -> 150
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	170	0	this	WallpaperManagerService
    //   0	170	1	paramInt	int
    //   41	97	2	localBufferedOutputStream	java.io.BufferedOutputStream
    //   147	1	2	localIOException1	IOException
    //   149	2	2	localWallpaperData1	WallpaperData
    //   160	1	2	localIOException2	IOException
    //   162	1	2	localWallpaperData2	WallpaperData
    //   7	155	3	localWallpaperData3	WallpaperData
    //   166	1	3	localIOException3	IOException
    //   4	151	4	localJournaledFile	JournaledFile
    //   15	106	5	localFastXmlSerializer	com.android.internal.util.FastXmlSerializer
    //   30	102	6	localFileOutputStream	java.io.FileOutputStream
    // Exception table:
    //   from	to	target	type
    //   8	32	147	java/io/IOException
    //   32	42	160	java/io/IOException
    //   42	80	166	java/io/IOException
    //   84	94	166	java/io/IOException
    //   94	106	166	java/io/IOException
    //   110	120	166	java/io/IOException
    //   120	146	166	java/io/IOException
  }
  
  private void setWallpaperComponent(ComponentName paramComponentName, int paramInt)
  {
    paramInt = ActivityManager.handleIncomingUser(getCallingPid(), getCallingUid(), paramInt, false, true, "changing live wallpaper", null);
    checkPermission("android.permission.SET_WALLPAPER_COMPONENT");
    WallpaperData localWallpaperData;
    synchronized (this.mLock)
    {
      localWallpaperData = (WallpaperData)this.mWallpaperMap.get(paramInt);
      if (localWallpaperData == null) {
        throw new IllegalStateException("Wallpaper not yet initialized for user " + paramInt);
      }
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      localWallpaperData.imageWallpaperPending = false;
      if (bindWallpaperComponentLocked(paramComponentName, false, true, localWallpaperData, null))
      {
        localWallpaperData.wallpaperId = makeWallpaperIdLocked();
        notifyCallbacksLocked(localWallpaperData);
      }
      Binder.restoreCallingIdentity(l);
      return;
    }
    finally
    {
      paramComponentName = finally;
      Binder.restoreCallingIdentity(l);
      throw paramComponentName;
    }
  }
  
  private void writeWallpaperAttributes(XmlSerializer paramXmlSerializer, String paramString, WallpaperData paramWallpaperData)
    throws IllegalArgumentException, IllegalStateException, IOException
  {
    paramXmlSerializer.startTag(null, paramString);
    paramXmlSerializer.attribute(null, "id", Integer.toString(paramWallpaperData.wallpaperId));
    paramXmlSerializer.attribute(null, "width", Integer.toString(paramWallpaperData.width));
    paramXmlSerializer.attribute(null, "height", Integer.toString(paramWallpaperData.height));
    paramXmlSerializer.attribute(null, "cropLeft", Integer.toString(paramWallpaperData.cropHint.left));
    paramXmlSerializer.attribute(null, "cropTop", Integer.toString(paramWallpaperData.cropHint.top));
    paramXmlSerializer.attribute(null, "cropRight", Integer.toString(paramWallpaperData.cropHint.right));
    paramXmlSerializer.attribute(null, "cropBottom", Integer.toString(paramWallpaperData.cropHint.bottom));
    if (paramWallpaperData.padding.left != 0) {
      paramXmlSerializer.attribute(null, "paddingLeft", Integer.toString(paramWallpaperData.padding.left));
    }
    if (paramWallpaperData.padding.top != 0) {
      paramXmlSerializer.attribute(null, "paddingTop", Integer.toString(paramWallpaperData.padding.top));
    }
    if (paramWallpaperData.padding.right != 0) {
      paramXmlSerializer.attribute(null, "paddingRight", Integer.toString(paramWallpaperData.padding.right));
    }
    if (paramWallpaperData.padding.bottom != 0) {
      paramXmlSerializer.attribute(null, "paddingBottom", Integer.toString(paramWallpaperData.padding.bottom));
    }
    paramXmlSerializer.attribute(null, "name", paramWallpaperData.name);
    if ((paramWallpaperData.wallpaperComponent == null) || (paramWallpaperData.wallpaperComponent.equals(this.mImageWallpaper))) {}
    for (;;)
    {
      if (paramWallpaperData.allowBackup) {
        paramXmlSerializer.attribute(null, "backup", "true");
      }
      paramXmlSerializer.endTag(null, paramString);
      return;
      paramXmlSerializer.attribute(null, "component", paramWallpaperData.wallpaperComponent.flattenToShortString());
    }
  }
  
  void attachServiceLocked(WallpaperConnection paramWallpaperConnection, WallpaperData paramWallpaperData)
  {
    try
    {
      paramWallpaperConnection.mService.attach(paramWallpaperConnection, paramWallpaperConnection.mToken, 2013, false, paramWallpaperData.width, paramWallpaperData.height, paramWallpaperData.padding);
      return;
    }
    catch (RemoteException paramWallpaperConnection)
    {
      do
      {
        Slog.w("WallpaperManagerService", "Failed attaching wallpaper; clearing", paramWallpaperConnection);
      } while (paramWallpaperData.wallpaperUpdating);
      bindWallpaperComponentLocked(null, false, false, paramWallpaperData, null);
    }
  }
  
  boolean bindWallpaperComponentLocked(ComponentName paramComponentName, boolean paramBoolean1, boolean paramBoolean2, WallpaperData paramWallpaperData, IRemoteCallback paramIRemoteCallback)
  {
    if ((!paramBoolean1) && (paramWallpaperData.connection != null)) {
      if (paramWallpaperData.wallpaperComponent == null)
      {
        if (paramComponentName == null) {
          return true;
        }
      }
      else if (paramWallpaperData.wallpaperComponent.equals(paramComponentName)) {
        return true;
      }
    }
    Object localObject1 = paramComponentName;
    if (paramComponentName == null) {}
    int j;
    ServiceInfo localServiceInfo;
    try
    {
      localObject2 = WallpaperManager.getDefaultWallpaperComponent(this.mContext);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        paramComponentName = (ComponentName)localObject2;
        localObject1 = this.mImageWallpaper;
      }
      paramComponentName = (ComponentName)localObject1;
      j = paramWallpaperData.userId;
      paramComponentName = (ComponentName)localObject1;
      localServiceInfo = this.mIPackageManager.getServiceInfo((ComponentName)localObject1, 4224, j);
      if (localServiceInfo == null)
      {
        paramComponentName = (ComponentName)localObject1;
        Slog.w("WallpaperManagerService", "Attempted wallpaper " + localObject1 + " is unavailable");
        return false;
      }
      paramComponentName = (ComponentName)localObject1;
      if ("android.permission.BIND_WALLPAPER".equals(localServiceInfo.permission)) {
        break label268;
      }
      paramComponentName = (ComponentName)localObject1;
      paramWallpaperData = "Selected service does not require android.permission.BIND_WALLPAPER: " + localObject1;
      if (!paramBoolean2) {
        break label255;
      }
      paramComponentName = (ComponentName)localObject1;
      throw new SecurityException(paramWallpaperData);
    }
    catch (RemoteException paramWallpaperData)
    {
      paramComponentName = "Remote exception for " + paramComponentName + "\n" + paramWallpaperData;
      if (!paramBoolean2) {
        break label898;
      }
    }
    throw new IllegalArgumentException(paramComponentName);
    label255:
    paramComponentName = (ComponentName)localObject1;
    Slog.w("WallpaperManagerService", paramWallpaperData);
    return false;
    label268:
    Object localObject4 = null;
    Object localObject2 = null;
    paramComponentName = (ComponentName)localObject1;
    Intent localIntent = new Intent("android.service.wallpaper.WallpaperService");
    Object localObject3 = localObject2;
    if (localObject1 != null)
    {
      paramComponentName = (ComponentName)localObject1;
      if (((ComponentName)localObject1).equals(this.mImageWallpaper)) {
        localObject3 = localObject2;
      }
    }
    else
    {
      paramComponentName = (ComponentName)localObject1;
      localObject2 = new WallpaperConnection((WallpaperInfo)localObject3, paramWallpaperData);
      paramComponentName = (ComponentName)localObject1;
      localIntent.setComponent((ComponentName)localObject1);
      paramComponentName = (ComponentName)localObject1;
      localIntent.putExtra("android.intent.extra.client_label", 17040515);
      paramComponentName = (ComponentName)localObject1;
      localIntent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivityAsUser(this.mContext, 0, Intent.createChooser(new Intent("android.intent.action.SET_WALLPAPER"), this.mContext.getText(17040516)), 0, null, new UserHandle(j)));
      paramComponentName = (ComponentName)localObject1;
      if (this.mContext.bindServiceAsUser(localIntent, (ServiceConnection)localObject2, 570425345, new UserHandle(j))) {
        break label774;
      }
      paramComponentName = (ComponentName)localObject1;
      paramWallpaperData = "Unable to bind service: " + localObject1;
      if (!paramBoolean2) {
        break label761;
      }
      paramComponentName = (ComponentName)localObject1;
      throw new IllegalArgumentException(paramWallpaperData);
    }
    paramComponentName = (ComponentName)localObject1;
    localObject3 = this.mIPackageManager.queryIntentServices(localIntent, localIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 128, j).getList();
    int i = 0;
    for (;;)
    {
      paramComponentName = (ComponentName)localObject1;
      localObject2 = localObject4;
      if (i < ((List)localObject3).size())
      {
        paramComponentName = (ComponentName)localObject1;
        localObject2 = ((ResolveInfo)((List)localObject3).get(i)).serviceInfo;
        paramComponentName = (ComponentName)localObject1;
        if (((ServiceInfo)localObject2).name.equals(localServiceInfo.name))
        {
          paramComponentName = (ComponentName)localObject1;
          paramBoolean1 = ((ServiceInfo)localObject2).packageName.equals(localServiceInfo.packageName);
          if (paramBoolean1) {
            paramComponentName = (ComponentName)localObject1;
          }
        }
      }
      else
      {
        try
        {
          localObject2 = new WallpaperInfo(this.mContext, (ResolveInfo)((List)localObject3).get(i));
          localObject3 = localObject2;
          if (localObject2 != null) {
            break;
          }
          paramComponentName = (ComponentName)localObject1;
          paramWallpaperData = "Selected service is not a wallpaper: " + localObject1;
          if (paramBoolean2)
          {
            paramComponentName = (ComponentName)localObject1;
            throw new SecurityException(paramWallpaperData);
          }
        }
        catch (IOException paramWallpaperData)
        {
          if (paramBoolean2)
          {
            paramComponentName = (ComponentName)localObject1;
            throw new IllegalArgumentException(paramWallpaperData);
          }
          paramComponentName = (ComponentName)localObject1;
          Slog.w("WallpaperManagerService", paramWallpaperData);
          return false;
        }
        catch (XmlPullParserException paramWallpaperData)
        {
          if (paramBoolean2)
          {
            paramComponentName = (ComponentName)localObject1;
            throw new IllegalArgumentException(paramWallpaperData);
          }
          paramComponentName = (ComponentName)localObject1;
          Slog.w("WallpaperManagerService", paramWallpaperData);
          return false;
        }
        paramComponentName = (ComponentName)localObject1;
        Slog.w("WallpaperManagerService", paramWallpaperData);
        return false;
        label761:
        paramComponentName = (ComponentName)localObject1;
        Slog.w("WallpaperManagerService", paramWallpaperData);
        return false;
        label774:
        paramComponentName = (ComponentName)localObject1;
        if (OnePlusAppBootManager.IN_USING)
        {
          paramComponentName = (ComponentName)localObject1;
          OnePlusAppBootManager.getInstance(null).setCurrentWallpaperPackage((ComponentName)localObject1);
        }
        paramComponentName = (ComponentName)localObject1;
        if (paramWallpaperData.userId == this.mCurrentUserId)
        {
          paramComponentName = (ComponentName)localObject1;
          if (this.mLastWallpaper != null)
          {
            paramComponentName = (ComponentName)localObject1;
            detachWallpaperLocked(this.mLastWallpaper);
          }
        }
        paramComponentName = (ComponentName)localObject1;
        paramWallpaperData.wallpaperComponent = ((ComponentName)localObject1);
        paramComponentName = (ComponentName)localObject1;
        paramWallpaperData.connection = ((WallpaperConnection)localObject2);
        paramComponentName = (ComponentName)localObject1;
        ((WallpaperConnection)localObject2).mReply = paramIRemoteCallback;
        try
        {
          if (paramWallpaperData.userId == this.mCurrentUserId)
          {
            this.mIWindowManager.addWindowToken(((WallpaperConnection)localObject2).mToken, 2013);
            this.mLastWallpaper = paramWallpaperData;
          }
          return true;
          label898:
          Slog.w("WallpaperManagerService", paramComponentName);
          return false;
        }
        catch (RemoteException paramComponentName)
        {
          for (;;) {}
        }
      }
      i += 1;
    }
  }
  
  public void clearWallpaper(String arg1, int paramInt1, int paramInt2)
  {
    checkPermission("android.permission.SET_WALLPAPER");
    if ((isWallpaperSupported(???)) && (isSetWallpaperAllowed(???))) {
      paramInt2 = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramInt2, false, true, "clearWallpaper", null);
    }
    synchronized (this.mLock)
    {
      clearWallpaperLocked(false, paramInt1, paramInt2, null);
      return;
      return;
    }
  }
  
  void clearWallpaperComponentLocked(WallpaperData paramWallpaperData)
  {
    paramWallpaperData.wallpaperComponent = null;
    detachWallpaperLocked(paramWallpaperData);
  }
  
  /* Error */
  void clearWallpaperLocked(boolean paramBoolean, int paramInt1, int paramInt2, IRemoteCallback paramIRemoteCallback)
  {
    // Byte code:
    //   0: iload_2
    //   1: iconst_1
    //   2: if_icmpeq +19 -> 21
    //   5: iload_2
    //   6: iconst_2
    //   7: if_icmpeq +14 -> 21
    //   10: new 851	java/lang/IllegalArgumentException
    //   13: dup
    //   14: ldc_w 1064
    //   17: invokespecial 931	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   20: athrow
    //   21: iload_2
    //   22: iconst_2
    //   23: if_icmpne +26 -> 49
    //   26: aload_0
    //   27: getfield 149	com/android/server/wallpaper/WallpaperManagerService:mLockWallpaperMap	Landroid/util/SparseArray;
    //   30: iload_3
    //   31: invokevirtual 493	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   34: checkcast 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   37: astore 8
    //   39: aload 8
    //   41: astore 7
    //   43: aload 8
    //   45: ifnonnull +45 -> 90
    //   48: return
    //   49: aload_0
    //   50: getfield 147	com/android/server/wallpaper/WallpaperManagerService:mWallpaperMap	Landroid/util/SparseArray;
    //   53: iload_3
    //   54: invokevirtual 493	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   57: checkcast 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   60: astore 8
    //   62: aload 8
    //   64: astore 7
    //   66: aload 8
    //   68: ifnonnull +22 -> 90
    //   71: aload_0
    //   72: iload_3
    //   73: iconst_0
    //   74: invokespecial 113	com/android/server/wallpaper/WallpaperManagerService:loadSettingsLocked	(IZ)V
    //   77: aload_0
    //   78: getfield 147	com/android/server/wallpaper/WallpaperManagerService:mWallpaperMap	Landroid/util/SparseArray;
    //   81: iload_3
    //   82: invokevirtual 493	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   85: checkcast 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   88: astore 7
    //   90: aload 7
    //   92: ifnonnull +4 -> 96
    //   95: return
    //   96: invokestatic 838	android/os/Binder:clearCallingIdentity	()J
    //   99: lstore 5
    //   101: aload 7
    //   103: getfield 314	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperFile	Ljava/io/File;
    //   106: invokevirtual 345	java/io/File:exists	()Z
    //   109: ifeq +68 -> 177
    //   112: aload 7
    //   114: getfield 314	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperFile	Ljava/io/File;
    //   117: invokevirtual 342	java/io/File:delete	()Z
    //   120: pop
    //   121: aload 7
    //   123: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   126: invokevirtual 342	java/io/File:delete	()Z
    //   129: pop
    //   130: iload_2
    //   131: iconst_2
    //   132: if_icmpne +45 -> 177
    //   135: aload_0
    //   136: getfield 149	com/android/server/wallpaper/WallpaperManagerService:mLockWallpaperMap	Landroid/util/SparseArray;
    //   139: iload_3
    //   140: invokevirtual 601	android/util/SparseArray:remove	(I)V
    //   143: aload_0
    //   144: getfield 1066	com/android/server/wallpaper/WallpaperManagerService:mKeyguardListener	Landroid/app/IWallpaperManagerCallback;
    //   147: astore 4
    //   149: aload 4
    //   151: ifnull +10 -> 161
    //   154: aload 4
    //   156: invokeinterface 718 1 0
    //   161: aload_0
    //   162: iload_3
    //   163: invokespecial 127	com/android/server/wallpaper/WallpaperManagerService:saveSettingsLocked	(I)V
    //   166: lload 5
    //   168: invokestatic 849	android/os/Binder:restoreCallingIdentity	(J)V
    //   171: return
    //   172: astore 4
    //   174: goto -13 -> 161
    //   177: aconst_null
    //   178: astore 9
    //   180: aload 7
    //   182: iconst_0
    //   183: putfield 841	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:imageWallpaperPending	Z
    //   186: aload_0
    //   187: getfield 728	com/android/server/wallpaper/WallpaperManagerService:mCurrentUserId	I
    //   190: istore_2
    //   191: iload_3
    //   192: iload_2
    //   193: if_icmpeq +9 -> 202
    //   196: lload 5
    //   198: invokestatic 849	android/os/Binder:restoreCallingIdentity	(J)V
    //   201: return
    //   202: iload_1
    //   203: ifeq +36 -> 239
    //   206: aload_0
    //   207: getfield 186	com/android/server/wallpaper/WallpaperManagerService:mImageWallpaper	Landroid/content/ComponentName;
    //   210: astore 8
    //   212: aload_0
    //   213: aload 8
    //   215: iconst_1
    //   216: iconst_0
    //   217: aload 7
    //   219: aload 4
    //   221: invokevirtual 845	com/android/server/wallpaper/WallpaperManagerService:bindWallpaperComponentLocked	(Landroid/content/ComponentName;ZZLcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;Landroid/os/IRemoteCallback;)Z
    //   224: istore_1
    //   225: aload 9
    //   227: astore 8
    //   229: iload_1
    //   230: ifeq +17 -> 247
    //   233: lload 5
    //   235: invokestatic 849	android/os/Binder:restoreCallingIdentity	(J)V
    //   238: return
    //   239: aconst_null
    //   240: astore 8
    //   242: goto -30 -> 212
    //   245: astore 8
    //   247: ldc 48
    //   249: ldc_w 1068
    //   252: aload 8
    //   254: invokestatic 1070	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   257: pop
    //   258: aload_0
    //   259: aload 7
    //   261: invokevirtual 1072	com/android/server/wallpaper/WallpaperManagerService:clearWallpaperComponentLocked	(Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;)V
    //   264: aload 4
    //   266: ifnull +11 -> 277
    //   269: aload 4
    //   271: aconst_null
    //   272: invokeinterface 1078 2 0
    //   277: lload 5
    //   279: invokestatic 849	android/os/Binder:restoreCallingIdentity	(J)V
    //   282: return
    //   283: astore 4
    //   285: goto -8 -> 277
    //   288: astore 4
    //   290: lload 5
    //   292: invokestatic 849	android/os/Binder:restoreCallingIdentity	(J)V
    //   295: aload 4
    //   297: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	298	0	this	WallpaperManagerService
    //   0	298	1	paramBoolean	boolean
    //   0	298	2	paramInt1	int
    //   0	298	3	paramInt2	int
    //   0	298	4	paramIRemoteCallback	IRemoteCallback
    //   99	192	5	l	long
    //   41	219	7	localObject1	Object
    //   37	204	8	localObject2	Object
    //   245	8	8	localIllegalArgumentException	IllegalArgumentException
    //   178	48	9	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   154	161	172	android/os/RemoteException
    //   180	191	245	java/lang/IllegalArgumentException
    //   206	212	245	java/lang/IllegalArgumentException
    //   212	225	245	java/lang/IllegalArgumentException
    //   269	277	283	android/os/RemoteException
    //   101	130	288	finally
    //   135	149	288	finally
    //   154	161	288	finally
    //   161	166	288	finally
    //   180	191	288	finally
    //   206	212	288	finally
    //   212	225	288	finally
    //   247	264	288	finally
    //   269	277	288	finally
  }
  
  void detachWallpaperLocked(WallpaperData paramWallpaperData)
  {
    if ((paramWallpaperData.connection == null) || (paramWallpaperData.connection.mReply != null)) {}
    try
    {
      paramWallpaperData.connection.mReply.sendResult(null);
      paramWallpaperData.connection.mReply = null;
      if (paramWallpaperData.connection.mEngine != null) {}
      try
      {
        paramWallpaperData.connection.mEngine.destroy();
        this.mContext.unbindService(paramWallpaperData.connection);
        try
        {
          this.mIWindowManager.removeWindowToken(paramWallpaperData.connection.mToken);
          paramWallpaperData.connection.mService = null;
          paramWallpaperData.connection.mEngine = null;
          paramWallpaperData.connection = null;
          return;
        }
        catch (RemoteException localRemoteException1)
        {
          for (;;) {}
        }
      }
      catch (RemoteException localRemoteException2)
      {
        for (;;) {}
      }
    }
    catch (RemoteException localRemoteException3)
    {
      for (;;) {}
    }
  }
  
  protected void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump wallpaper service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        paramPrintWriter.println("System wallpaper state:");
        i = 0;
        if (i < this.mWallpaperMap.size())
        {
          paramArrayOfString = (WallpaperData)this.mWallpaperMap.valueAt(i);
          paramPrintWriter.print(" User ");
          paramPrintWriter.print(paramArrayOfString.userId);
          paramPrintWriter.print(": id=");
          paramPrintWriter.println(paramArrayOfString.wallpaperId);
          paramPrintWriter.print("  mWidth=");
          paramPrintWriter.print(paramArrayOfString.width);
          paramPrintWriter.print(" mHeight=");
          paramPrintWriter.println(paramArrayOfString.height);
          paramPrintWriter.print("  mCropHint=");
          paramPrintWriter.println(paramArrayOfString.cropHint);
          paramPrintWriter.print("  mPadding=");
          paramPrintWriter.println(paramArrayOfString.padding);
          paramPrintWriter.print("  mName=");
          paramPrintWriter.println(paramArrayOfString.name);
          paramPrintWriter.print("  mWallpaperComponent=");
          paramPrintWriter.println(paramArrayOfString.wallpaperComponent);
          if (paramArrayOfString.connection != null)
          {
            WallpaperConnection localWallpaperConnection = paramArrayOfString.connection;
            paramPrintWriter.print("  Wallpaper connection ");
            paramPrintWriter.print(localWallpaperConnection);
            paramPrintWriter.println(":");
            if (localWallpaperConnection.mInfo != null)
            {
              paramPrintWriter.print("    mInfo.component=");
              paramPrintWriter.println(localWallpaperConnection.mInfo.getComponent());
            }
            paramPrintWriter.print("    mToken=");
            paramPrintWriter.println(localWallpaperConnection.mToken);
            paramPrintWriter.print("    mService=");
            paramPrintWriter.println(localWallpaperConnection.mService);
            paramPrintWriter.print("    mEngine=");
            paramPrintWriter.println(localWallpaperConnection.mEngine);
            paramPrintWriter.print("    mLastDiedTime=");
            paramPrintWriter.println(paramArrayOfString.lastDiedTime - SystemClock.uptimeMillis());
          }
        }
        else
        {
          paramPrintWriter.println("Lock wallpaper state:");
          i = 0;
          if (i < this.mLockWallpaperMap.size())
          {
            paramArrayOfString = (WallpaperData)this.mLockWallpaperMap.valueAt(i);
            paramPrintWriter.print(" User ");
            paramPrintWriter.print(paramArrayOfString.userId);
            paramPrintWriter.print(": id=");
            paramPrintWriter.println(paramArrayOfString.wallpaperId);
            paramPrintWriter.print("  mWidth=");
            paramPrintWriter.print(paramArrayOfString.width);
            paramPrintWriter.print(" mHeight=");
            paramPrintWriter.println(paramArrayOfString.height);
            paramPrintWriter.print("  mCropHint=");
            paramPrintWriter.println(paramArrayOfString.cropHint);
            paramPrintWriter.print("  mPadding=");
            paramPrintWriter.println(paramArrayOfString.padding);
            paramPrintWriter.print("  mName=");
            paramPrintWriter.println(paramArrayOfString.name);
            i += 1;
            continue;
          }
          return;
        }
      }
      i += 1;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    super.finalize();
    int i = 0;
    while (i < this.mWallpaperMap.size())
    {
      ((WallpaperData)this.mWallpaperMap.valueAt(i)).wallpaperObserver.stopWatching();
      i += 1;
    }
  }
  
  public int getHeightHint()
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      WallpaperData localWallpaperData = (WallpaperData)this.mWallpaperMap.get(UserHandle.getCallingUserId());
      if (localWallpaperData != null)
      {
        int i = localWallpaperData.height;
        return i;
      }
      return 0;
    }
  }
  
  public String getName()
  {
    if (Binder.getCallingUid() != 1000) {
      throw new RuntimeException("getName() can only be called from the system process");
    }
    synchronized (this.mLock)
    {
      String str = ((WallpaperData)this.mWallpaperMap.get(0)).name;
      return str;
    }
  }
  
  public boolean getWaitingForUnLock()
  {
    return this.mWaitingForUnlock;
  }
  
  /* Error */
  public ParcelFileDescriptor getWallpaper(IWallpaperManagerCallback paramIWallpaperManagerCallback, int paramInt1, Bundle paramBundle, int paramInt2)
  {
    // Byte code:
    //   0: invokestatic 264	android/os/Binder:getCallingPid	()I
    //   3: invokestatic 1056	android/os/Binder:getCallingUid	()I
    //   6: iload 4
    //   8: iconst_0
    //   9: iconst_1
    //   10: ldc_w 1201
    //   13: aconst_null
    //   14: invokestatic 825	android/app/ActivityManager:handleIncomingUser	(IIIZZLjava/lang/String;Ljava/lang/String;)I
    //   17: istore 4
    //   19: iload_2
    //   20: iconst_1
    //   21: if_icmpeq +19 -> 40
    //   24: iload_2
    //   25: iconst_2
    //   26: if_icmpeq +14 -> 40
    //   29: new 851	java/lang/IllegalArgumentException
    //   32: dup
    //   33: ldc_w 1064
    //   36: invokespecial 931	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   39: athrow
    //   40: aload_0
    //   41: getfield 142	com/android/server/wallpaper/WallpaperManagerService:mLock	Ljava/lang/Object;
    //   44: astore 8
    //   46: aload 8
    //   48: monitorenter
    //   49: iload_2
    //   50: iconst_2
    //   51: if_icmpne +89 -> 140
    //   54: aload_0
    //   55: getfield 149	com/android/server/wallpaper/WallpaperManagerService:mLockWallpaperMap	Landroid/util/SparseArray;
    //   58: astore 6
    //   60: aload 6
    //   62: iload 4
    //   64: invokevirtual 493	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   67: checkcast 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   70: astore 7
    //   72: aload 7
    //   74: astore 5
    //   76: aload 7
    //   78: ifnonnull +71 -> 149
    //   81: aload_0
    //   82: iload 4
    //   84: iconst_0
    //   85: invokespecial 113	com/android/server/wallpaper/WallpaperManagerService:loadSettingsLocked	(IZ)V
    //   88: aload 6
    //   90: iload 4
    //   92: invokevirtual 493	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   95: checkcast 28	com/android/server/wallpaper/WallpaperManagerService$WallpaperData
    //   98: astore 6
    //   100: aload 6
    //   102: astore 5
    //   104: aload 6
    //   106: ifnonnull +43 -> 149
    //   109: ldc 48
    //   111: new 251	java/lang/StringBuilder
    //   114: dup
    //   115: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   118: ldc_w 1203
    //   121: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: iload_2
    //   125: invokevirtual 267	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   128: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   131: invokestatic 658	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   134: pop
    //   135: aload 8
    //   137: monitorexit
    //   138: aconst_null
    //   139: areturn
    //   140: aload_0
    //   141: getfield 147	com/android/server/wallpaper/WallpaperManagerService:mWallpaperMap	Landroid/util/SparseArray;
    //   144: astore 6
    //   146: goto -86 -> 60
    //   149: aload_3
    //   150: ifnull +27 -> 177
    //   153: aload_3
    //   154: ldc_w 739
    //   157: aload 5
    //   159: getfield 282	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:width	I
    //   162: invokevirtual 1209	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   165: aload_3
    //   166: ldc_w 740
    //   169: aload 5
    //   171: getfield 285	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:height	I
    //   174: invokevirtual 1209	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   177: aload_1
    //   178: ifnull +13 -> 191
    //   181: aload 5
    //   183: invokestatic 704	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:-get0	(Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;)Landroid/os/RemoteCallbackList;
    //   186: aload_1
    //   187: invokevirtual 1212	android/os/RemoteCallbackList:register	(Landroid/os/IInterface;)Z
    //   190: pop
    //   191: aload 5
    //   193: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   196: invokevirtual 345	java/io/File:exists	()Z
    //   199: ifne +34 -> 233
    //   202: ldc 48
    //   204: new 251	java/lang/StringBuilder
    //   207: dup
    //   208: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   211: ldc_w 1214
    //   214: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   217: iload_2
    //   218: invokevirtual 267	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   221: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   224: invokestatic 658	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   227: pop
    //   228: aload 8
    //   230: monitorexit
    //   231: aconst_null
    //   232: areturn
    //   233: aload 5
    //   235: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   238: ldc_w 1215
    //   241: invokestatic 1221	android/os/ParcelFileDescriptor:open	(Ljava/io/File;I)Landroid/os/ParcelFileDescriptor;
    //   244: astore_1
    //   245: aload 8
    //   247: monitorexit
    //   248: aload_1
    //   249: areturn
    //   250: astore_1
    //   251: ldc 48
    //   253: ldc_w 1223
    //   256: aload_1
    //   257: invokestatic 893	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   260: pop
    //   261: aload 8
    //   263: monitorexit
    //   264: aconst_null
    //   265: areturn
    //   266: astore_1
    //   267: aload 8
    //   269: monitorexit
    //   270: aload_1
    //   271: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	272	0	this	WallpaperManagerService
    //   0	272	1	paramIWallpaperManagerCallback	IWallpaperManagerCallback
    //   0	272	2	paramInt1	int
    //   0	272	3	paramBundle	Bundle
    //   0	272	4	paramInt2	int
    //   74	160	5	localObject1	Object
    //   58	87	6	localObject2	Object
    //   70	7	7	localWallpaperData	WallpaperData
    //   44	224	8	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   153	177	250	java/io/FileNotFoundException
    //   181	191	250	java/io/FileNotFoundException
    //   191	228	250	java/io/FileNotFoundException
    //   233	245	250	java/io/FileNotFoundException
    //   54	60	266	finally
    //   60	72	266	finally
    //   81	100	266	finally
    //   109	135	266	finally
    //   140	146	266	finally
    //   153	177	266	finally
    //   181	191	266	finally
    //   191	228	266	finally
    //   233	245	266	finally
    //   251	261	266	finally
  }
  
  public int getWallpaperIdForUser(int paramInt1, int paramInt2)
  {
    paramInt2 = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramInt2, false, true, "getWallpaperIdForUser", null);
    if ((paramInt1 != 1) && (paramInt1 != 2)) {
      throw new IllegalArgumentException("Must specify exactly one kind of wallpaper");
    }
    Object localObject1;
    if (paramInt1 == 2) {
      localObject1 = this.mLockWallpaperMap;
    }
    synchronized (this.mLock)
    {
      for (;;)
      {
        localObject1 = (WallpaperData)((SparseArray)localObject1).get(paramInt2);
        if (localObject1 == null) {
          break;
        }
        paramInt1 = ((WallpaperData)localObject1).wallpaperId;
        return paramInt1;
        localObject1 = this.mWallpaperMap;
      }
      return -1;
    }
  }
  
  public WallpaperInfo getWallpaperInfo(int paramInt)
  {
    paramInt = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramInt, false, true, "getWallpaperIdForUser", null);
    synchronized (this.mLock)
    {
      Object localObject2 = (WallpaperData)this.mWallpaperMap.get(paramInt);
      if ((localObject2 != null) && (((WallpaperData)localObject2).connection != null))
      {
        localObject2 = ((WallpaperData)localObject2).connection.mInfo;
        return (WallpaperInfo)localObject2;
      }
      return null;
    }
  }
  
  public int getWidthHint()
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      WallpaperData localWallpaperData = (WallpaperData)this.mWallpaperMap.get(UserHandle.getCallingUserId());
      if (localWallpaperData != null)
      {
        int i = localWallpaperData.width;
        return i;
      }
      return 0;
    }
  }
  
  public boolean hasNamedWallpaper(String paramString)
  {
    synchronized (this.mLock)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        Object localObject1 = ((UserManager)this.mContext.getSystemService("user")).getUsers();
        Binder.restoreCallingIdentity(l);
        Iterator localIterator = ((Iterable)localObject1).iterator();
        while (localIterator.hasNext())
        {
          UserInfo localUserInfo = (UserInfo)localIterator.next();
          if (!localUserInfo.isManagedProfile())
          {
            WallpaperData localWallpaperData = (WallpaperData)this.mWallpaperMap.get(localUserInfo.id);
            localObject1 = localWallpaperData;
            if (localWallpaperData == null)
            {
              loadSettingsLocked(localUserInfo.id, false);
              localObject1 = (WallpaperData)this.mWallpaperMap.get(localUserInfo.id);
            }
            if (localObject1 != null)
            {
              boolean bool = paramString.equals(((WallpaperData)localObject1).name);
              if (bool) {
                return true;
              }
            }
          }
        }
      }
      finally
      {
        paramString = finally;
        Binder.restoreCallingIdentity(l);
        throw paramString;
      }
    }
    return false;
  }
  
  public boolean isSetWallpaperAllowed(String paramString)
  {
    if (!Arrays.asList(this.mContext.getPackageManager().getPackagesForUid(Binder.getCallingUid())).contains(paramString)) {
      return false;
    }
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)this.mContext.getSystemService(DevicePolicyManager.class);
    if ((localDevicePolicyManager.isDeviceOwnerApp(paramString)) || (localDevicePolicyManager.isProfileOwnerApp(paramString))) {
      return true;
    }
    return !((UserManager)this.mContext.getSystemService("user")).hasUserRestriction("no_set_wallpaper");
  }
  
  public boolean isWallpaperBackupEligible(int paramInt1, int paramInt2)
  {
    if (Binder.getCallingUid() != 1000) {
      throw new SecurityException("Only the system may call isWallpaperBackupEligible");
    }
    if (paramInt1 == 2) {}
    for (WallpaperData localWallpaperData = (WallpaperData)this.mLockWallpaperMap.get(paramInt2); localWallpaperData != null; localWallpaperData = (WallpaperData)this.mWallpaperMap.get(paramInt2)) {
      return localWallpaperData.allowBackup;
    }
    return false;
  }
  
  public boolean isWallpaperSupported(String paramString)
  {
    boolean bool = false;
    if (this.mAppOpsManager.checkOpNoThrow(48, Binder.getCallingUid(), paramString) == 0) {
      bool = true;
    }
    return bool;
  }
  
  int makeWallpaperIdLocked()
  {
    do
    {
      this.mWallpaperId += 1;
    } while (this.mWallpaperId == 0);
    return this.mWallpaperId;
  }
  
  void notifyLockWallpaperChanged()
  {
    IWallpaperManagerCallback localIWallpaperManagerCallback = this.mKeyguardListener;
    if (localIWallpaperManagerCallback != null) {}
    try
    {
      localIWallpaperManagerCallback.onWallpaperChanged();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  void onRemoveUser(int paramInt)
  {
    if (paramInt < 1) {
      return;
    }
    File localFile = getWallpaperDir(paramInt);
    synchronized (this.mLock)
    {
      stopObserversLocked(paramInt);
      String[] arrayOfString = sPerUserFiles;
      paramInt = 0;
      int i = arrayOfString.length;
      while (paramInt < i)
      {
        new File(localFile, arrayOfString[paramInt]).delete();
        paramInt += 1;
      }
      return;
    }
  }
  
  void onUnlockUser(final int paramInt)
  {
    synchronized (this.mLock)
    {
      if (this.mCurrentUserId == paramInt)
      {
        if (this.mWaitingForUnlock) {
          switchUser(paramInt, null);
        }
        if (this.mUserRestorecon.get(paramInt) != Boolean.TRUE)
        {
          this.mUserRestorecon.put(paramInt, Boolean.TRUE);
          Runnable local4 = new Runnable()
          {
            public void run()
            {
              File localFile1 = WallpaperManagerService.-wrap0(paramInt);
              String[] arrayOfString = WallpaperManagerService.sPerUserFiles;
              int i = 0;
              int j = arrayOfString.length;
              while (i < j)
              {
                File localFile2 = new File(localFile1, arrayOfString[i]);
                if (localFile2.exists()) {
                  SELinux.restorecon(localFile2);
                }
                i += 1;
              }
            }
          };
          BackgroundThread.getHandler().post(local4);
        }
      }
      return;
    }
  }
  
  /* Error */
  boolean restoreNamedResourceLocked(WallpaperData paramWallpaperData)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 598	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:name	Ljava/lang/String;
    //   4: invokevirtual 1338	java/lang/String:length	()I
    //   7: iconst_4
    //   8: if_icmple +665 -> 673
    //   11: ldc_w 1340
    //   14: aload_1
    //   15: getfield 598	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:name	Ljava/lang/String;
    //   18: iconst_0
    //   19: iconst_4
    //   20: invokevirtual 1344	java/lang/String:substring	(II)Ljava/lang/String;
    //   23: invokevirtual 577	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   26: ifeq +647 -> 673
    //   29: aload_1
    //   30: getfield 598	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:name	Ljava/lang/String;
    //   33: iconst_4
    //   34: invokevirtual 1346	java/lang/String:substring	(I)Ljava/lang/String;
    //   37: astore 26
    //   39: aconst_null
    //   40: astore 12
    //   42: aload 26
    //   44: bipush 58
    //   46: invokevirtual 1350	java/lang/String:indexOf	(I)I
    //   49: istore_2
    //   50: iload_2
    //   51: ifle +12 -> 63
    //   54: aload 26
    //   56: iconst_0
    //   57: iload_2
    //   58: invokevirtual 1344	java/lang/String:substring	(II)Ljava/lang/String;
    //   61: astore 12
    //   63: aconst_null
    //   64: astore 4
    //   66: aload 26
    //   68: bipush 47
    //   70: invokevirtual 1353	java/lang/String:lastIndexOf	(I)I
    //   73: istore_3
    //   74: iload_3
    //   75: ifle +13 -> 88
    //   78: aload 26
    //   80: iload_3
    //   81: iconst_1
    //   82: iadd
    //   83: invokevirtual 1346	java/lang/String:substring	(I)Ljava/lang/String;
    //   86: astore 4
    //   88: aconst_null
    //   89: astore 6
    //   91: aload 6
    //   93: astore 5
    //   95: iload_2
    //   96: ifle +33 -> 129
    //   99: aload 6
    //   101: astore 5
    //   103: iload_3
    //   104: ifle +25 -> 129
    //   107: aload 6
    //   109: astore 5
    //   111: iload_3
    //   112: iload_2
    //   113: isub
    //   114: iconst_1
    //   115: if_icmple +14 -> 129
    //   118: aload 26
    //   120: iload_2
    //   121: iconst_1
    //   122: iadd
    //   123: iload_3
    //   124: invokevirtual 1344	java/lang/String:substring	(II)Ljava/lang/String;
    //   127: astore 5
    //   129: aload 12
    //   131: ifnull +542 -> 673
    //   134: aload 4
    //   136: ifnull +537 -> 673
    //   139: aload 5
    //   141: ifnull +532 -> 673
    //   144: iconst_m1
    //   145: istore_3
    //   146: aconst_null
    //   147: astore 23
    //   149: aconst_null
    //   150: astore 24
    //   152: aconst_null
    //   153: astore 25
    //   155: aconst_null
    //   156: astore 22
    //   158: aconst_null
    //   159: astore 18
    //   161: aconst_null
    //   162: astore 19
    //   164: aconst_null
    //   165: astore 20
    //   167: aconst_null
    //   168: astore 17
    //   170: aconst_null
    //   171: astore 13
    //   173: aconst_null
    //   174: astore 14
    //   176: aconst_null
    //   177: astore 21
    //   179: aconst_null
    //   180: astore 16
    //   182: aconst_null
    //   183: astore 15
    //   185: aload 22
    //   187: astore 10
    //   189: aload 23
    //   191: astore 11
    //   193: iload_3
    //   194: istore_2
    //   195: aload 21
    //   197: astore 7
    //   199: aload 20
    //   201: astore 8
    //   203: aload 24
    //   205: astore 6
    //   207: aload 25
    //   209: astore 9
    //   211: aload_0
    //   212: getfield 163	com/android/server/wallpaper/WallpaperManagerService:mContext	Landroid/content/Context;
    //   215: aload 12
    //   217: iconst_4
    //   218: invokevirtual 1357	android/content/Context:createPackageContext	(Ljava/lang/String;I)Landroid/content/Context;
    //   221: invokevirtual 171	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   224: astore 27
    //   226: aload 22
    //   228: astore 10
    //   230: aload 23
    //   232: astore 11
    //   234: iload_3
    //   235: istore_2
    //   236: aload 21
    //   238: astore 7
    //   240: aload 20
    //   242: astore 8
    //   244: aload 24
    //   246: astore 6
    //   248: aload 25
    //   250: astore 9
    //   252: aload 27
    //   254: aload 26
    //   256: aconst_null
    //   257: aconst_null
    //   258: invokevirtual 1361	android/content/res/Resources:getIdentifier	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
    //   261: istore_3
    //   262: iload_3
    //   263: ifne +92 -> 355
    //   266: aload 22
    //   268: astore 10
    //   270: aload 23
    //   272: astore 11
    //   274: iload_3
    //   275: istore_2
    //   276: aload 21
    //   278: astore 7
    //   280: aload 20
    //   282: astore 8
    //   284: aload 24
    //   286: astore 6
    //   288: aload 25
    //   290: astore 9
    //   292: ldc 48
    //   294: new 251	java/lang/StringBuilder
    //   297: dup
    //   298: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   301: ldc_w 1363
    //   304: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   307: aload 12
    //   309: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   312: ldc_w 1365
    //   315: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   318: aload 5
    //   320: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   323: ldc_w 1367
    //   326: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   329: aload 4
    //   331: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   334: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   337: invokestatic 334	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   340: pop
    //   341: aconst_null
    //   342: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   345: aconst_null
    //   346: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   349: aconst_null
    //   350: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   353: iconst_0
    //   354: ireturn
    //   355: aload 22
    //   357: astore 10
    //   359: aload 23
    //   361: astore 11
    //   363: iload_3
    //   364: istore_2
    //   365: aload 21
    //   367: astore 7
    //   369: aload 20
    //   371: astore 8
    //   373: aload 24
    //   375: astore 6
    //   377: aload 25
    //   379: astore 9
    //   381: aload 27
    //   383: iload_3
    //   384: invokevirtual 1371	android/content/res/Resources:openRawResource	(I)Ljava/io/InputStream;
    //   387: astore 4
    //   389: aload 4
    //   391: astore 10
    //   393: aload 4
    //   395: astore 11
    //   397: iload_3
    //   398: istore_2
    //   399: aload 21
    //   401: astore 7
    //   403: aload 20
    //   405: astore 8
    //   407: aload 4
    //   409: astore 6
    //   411: aload 4
    //   413: astore 9
    //   415: aload_1
    //   416: getfield 314	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperFile	Ljava/io/File;
    //   419: invokevirtual 345	java/io/File:exists	()Z
    //   422: ifeq +71 -> 493
    //   425: aload 4
    //   427: astore 10
    //   429: aload 4
    //   431: astore 11
    //   433: iload_3
    //   434: istore_2
    //   435: aload 21
    //   437: astore 7
    //   439: aload 20
    //   441: astore 8
    //   443: aload 4
    //   445: astore 6
    //   447: aload 4
    //   449: astore 9
    //   451: aload_1
    //   452: getfield 314	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperFile	Ljava/io/File;
    //   455: invokevirtual 342	java/io/File:delete	()Z
    //   458: pop
    //   459: aload 4
    //   461: astore 10
    //   463: aload 4
    //   465: astore 11
    //   467: iload_3
    //   468: istore_2
    //   469: aload 21
    //   471: astore 7
    //   473: aload 20
    //   475: astore 8
    //   477: aload 4
    //   479: astore 6
    //   481: aload 4
    //   483: astore 9
    //   485: aload_1
    //   486: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   489: invokevirtual 342	java/io/File:delete	()Z
    //   492: pop
    //   493: aload 4
    //   495: astore 10
    //   497: aload 4
    //   499: astore 11
    //   501: iload_3
    //   502: istore_2
    //   503: aload 21
    //   505: astore 7
    //   507: aload 20
    //   509: astore 8
    //   511: aload 4
    //   513: astore 6
    //   515: aload 4
    //   517: astore 9
    //   519: new 429	java/io/FileOutputStream
    //   522: dup
    //   523: aload_1
    //   524: getfield 314	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperFile	Ljava/io/File;
    //   527: invokespecial 432	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   530: astore 5
    //   532: new 429	java/io/FileOutputStream
    //   535: dup
    //   536: aload_1
    //   537: getfield 339	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:cropFile	Ljava/io/File;
    //   540: invokespecial 432	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   543: astore_1
    //   544: ldc_w 435
    //   547: newarray <illegal type>
    //   549: astore 6
    //   551: aload 4
    //   553: aload 6
    //   555: invokevirtual 1377	java/io/InputStream:read	([B)I
    //   558: istore_2
    //   559: iload_2
    //   560: ifle +115 -> 675
    //   563: aload 5
    //   565: aload 6
    //   567: iconst_0
    //   568: iload_2
    //   569: invokevirtual 1381	java/io/FileOutputStream:write	([BII)V
    //   572: aload_1
    //   573: aload 6
    //   575: iconst_0
    //   576: iload_2
    //   577: invokevirtual 1381	java/io/FileOutputStream:write	([BII)V
    //   580: goto -29 -> 551
    //   583: astore 6
    //   585: aload_1
    //   586: astore 6
    //   588: aload 5
    //   590: astore_1
    //   591: aload 6
    //   593: astore 5
    //   595: aload 5
    //   597: astore 7
    //   599: aload_1
    //   600: astore 8
    //   602: aload 4
    //   604: astore 6
    //   606: ldc 48
    //   608: new 251	java/lang/StringBuilder
    //   611: dup
    //   612: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   615: ldc_w 1383
    //   618: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   621: aload 12
    //   623: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   626: ldc_w 1385
    //   629: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   632: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   635: invokestatic 334	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   638: pop
    //   639: aload 4
    //   641: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   644: aload_1
    //   645: ifnull +8 -> 653
    //   648: aload_1
    //   649: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   652: pop
    //   653: aload 5
    //   655: ifnull +9 -> 664
    //   658: aload 5
    //   660: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   663: pop
    //   664: aload_1
    //   665: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   668: aload 5
    //   670: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   673: iconst_0
    //   674: ireturn
    //   675: ldc 48
    //   677: new 251	java/lang/StringBuilder
    //   680: dup
    //   681: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   684: ldc_w 1387
    //   687: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   690: aload 26
    //   692: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   695: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   698: invokestatic 161	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   701: pop
    //   702: aload 4
    //   704: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   707: aload 5
    //   709: ifnull +9 -> 718
    //   712: aload 5
    //   714: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   717: pop
    //   718: aload_1
    //   719: ifnull +8 -> 727
    //   722: aload_1
    //   723: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   726: pop
    //   727: aload 5
    //   729: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   732: aload_1
    //   733: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   736: iconst_1
    //   737: ireturn
    //   738: astore 9
    //   740: aload 10
    //   742: astore 4
    //   744: aload 18
    //   746: astore_1
    //   747: aload 13
    //   749: astore 5
    //   751: aload 5
    //   753: astore 7
    //   755: aload_1
    //   756: astore 8
    //   758: aload 4
    //   760: astore 6
    //   762: ldc 48
    //   764: ldc_w 1389
    //   767: aload 9
    //   769: invokestatic 1070	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   772: pop
    //   773: aload 4
    //   775: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   778: aload_1
    //   779: ifnull +8 -> 787
    //   782: aload_1
    //   783: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   786: pop
    //   787: aload 5
    //   789: ifnull +9 -> 798
    //   792: aload 5
    //   794: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   797: pop
    //   798: aload_1
    //   799: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   802: aload 5
    //   804: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   807: goto -134 -> 673
    //   810: astore_1
    //   811: aload 11
    //   813: astore 4
    //   815: aload 19
    //   817: astore_1
    //   818: aload 14
    //   820: astore 5
    //   822: aload 5
    //   824: astore 7
    //   826: aload_1
    //   827: astore 8
    //   829: aload 4
    //   831: astore 6
    //   833: ldc 48
    //   835: new 251	java/lang/StringBuilder
    //   838: dup
    //   839: invokespecial 252	java/lang/StringBuilder:<init>	()V
    //   842: ldc_w 1391
    //   845: invokevirtual 258	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   848: iload_2
    //   849: invokevirtual 267	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   852: invokevirtual 273	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   855: invokestatic 334	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   858: pop
    //   859: aload 4
    //   861: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   864: aload_1
    //   865: ifnull +8 -> 873
    //   868: aload_1
    //   869: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   872: pop
    //   873: aload 5
    //   875: ifnull +9 -> 884
    //   878: aload 5
    //   880: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   883: pop
    //   884: aload_1
    //   885: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   888: aload 5
    //   890: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   893: goto -220 -> 673
    //   896: astore_1
    //   897: aload 6
    //   899: astore 4
    //   901: aload 4
    //   903: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   906: aload 8
    //   908: ifnull +9 -> 917
    //   911: aload 8
    //   913: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   916: pop
    //   917: aload 7
    //   919: ifnull +9 -> 928
    //   922: aload 7
    //   924: invokestatic 802	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   927: pop
    //   928: aload 8
    //   930: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   933: aload 7
    //   935: invokestatic 404	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   938: aload_1
    //   939: athrow
    //   940: astore_1
    //   941: aload 16
    //   943: astore 7
    //   945: aload 5
    //   947: astore 8
    //   949: goto -48 -> 901
    //   952: astore 6
    //   954: aload_1
    //   955: astore 7
    //   957: aload 5
    //   959: astore 8
    //   961: aload 6
    //   963: astore_1
    //   964: goto -63 -> 901
    //   967: astore_1
    //   968: aload 15
    //   970: astore 5
    //   972: aload 17
    //   974: astore_1
    //   975: aload 9
    //   977: astore 4
    //   979: goto -384 -> 595
    //   982: astore_1
    //   983: aload 5
    //   985: astore_1
    //   986: aload 15
    //   988: astore 5
    //   990: goto -395 -> 595
    //   993: astore_1
    //   994: aload 5
    //   996: astore_1
    //   997: aload 14
    //   999: astore 5
    //   1001: iload_3
    //   1002: istore_2
    //   1003: goto -181 -> 822
    //   1006: astore 6
    //   1008: aload 5
    //   1010: astore 6
    //   1012: aload_1
    //   1013: astore 5
    //   1015: aload 6
    //   1017: astore_1
    //   1018: iload_3
    //   1019: istore_2
    //   1020: goto -198 -> 822
    //   1023: astore 9
    //   1025: aload 5
    //   1027: astore_1
    //   1028: aload 13
    //   1030: astore 5
    //   1032: goto -281 -> 751
    //   1035: astore 9
    //   1037: aload_1
    //   1038: astore 6
    //   1040: aload 5
    //   1042: astore_1
    //   1043: aload 6
    //   1045: astore 5
    //   1047: goto -296 -> 751
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1050	0	this	WallpaperManagerService
    //   0	1050	1	paramWallpaperData	WallpaperData
    //   49	971	2	i	int
    //   73	946	3	j	int
    //   64	914	4	localObject1	Object
    //   93	953	5	localObject2	Object
    //   89	485	6	localObject3	Object
    //   583	1	6	localNameNotFoundException	PackageManager.NameNotFoundException
    //   586	312	6	localObject4	Object
    //   952	10	6	localObject5	Object
    //   1006	1	6	localNotFoundException	android.content.res.Resources.NotFoundException
    //   1010	34	6	localObject6	Object
    //   197	759	7	localObject7	Object
    //   201	759	8	localObject8	Object
    //   209	309	9	localObject9	Object
    //   738	238	9	localIOException1	IOException
    //   1023	1	9	localIOException2	IOException
    //   1035	1	9	localIOException3	IOException
    //   187	554	10	localObject10	Object
    //   191	621	11	localObject11	Object
    //   40	582	12	str1	String
    //   171	858	13	localObject12	Object
    //   174	824	14	localObject13	Object
    //   183	804	15	localObject14	Object
    //   180	762	16	localObject15	Object
    //   168	805	17	localObject16	Object
    //   159	586	18	localObject17	Object
    //   162	654	19	localObject18	Object
    //   165	343	20	localObject19	Object
    //   177	327	21	localObject20	Object
    //   156	200	22	localObject21	Object
    //   147	213	23	localObject22	Object
    //   150	224	24	localObject23	Object
    //   153	225	25	localObject24	Object
    //   37	654	26	str2	String
    //   224	158	27	localResources	Resources
    // Exception table:
    //   from	to	target	type
    //   544	551	583	android/content/pm/PackageManager$NameNotFoundException
    //   551	559	583	android/content/pm/PackageManager$NameNotFoundException
    //   563	580	583	android/content/pm/PackageManager$NameNotFoundException
    //   675	702	583	android/content/pm/PackageManager$NameNotFoundException
    //   211	226	738	java/io/IOException
    //   252	262	738	java/io/IOException
    //   292	341	738	java/io/IOException
    //   381	389	738	java/io/IOException
    //   415	425	738	java/io/IOException
    //   451	459	738	java/io/IOException
    //   485	493	738	java/io/IOException
    //   519	532	738	java/io/IOException
    //   211	226	810	android/content/res/Resources$NotFoundException
    //   252	262	810	android/content/res/Resources$NotFoundException
    //   292	341	810	android/content/res/Resources$NotFoundException
    //   381	389	810	android/content/res/Resources$NotFoundException
    //   415	425	810	android/content/res/Resources$NotFoundException
    //   451	459	810	android/content/res/Resources$NotFoundException
    //   485	493	810	android/content/res/Resources$NotFoundException
    //   519	532	810	android/content/res/Resources$NotFoundException
    //   211	226	896	finally
    //   252	262	896	finally
    //   292	341	896	finally
    //   381	389	896	finally
    //   415	425	896	finally
    //   451	459	896	finally
    //   485	493	896	finally
    //   519	532	896	finally
    //   606	639	896	finally
    //   762	773	896	finally
    //   833	859	896	finally
    //   532	544	940	finally
    //   544	551	952	finally
    //   551	559	952	finally
    //   563	580	952	finally
    //   675	702	952	finally
    //   211	226	967	android/content/pm/PackageManager$NameNotFoundException
    //   252	262	967	android/content/pm/PackageManager$NameNotFoundException
    //   292	341	967	android/content/pm/PackageManager$NameNotFoundException
    //   381	389	967	android/content/pm/PackageManager$NameNotFoundException
    //   415	425	967	android/content/pm/PackageManager$NameNotFoundException
    //   451	459	967	android/content/pm/PackageManager$NameNotFoundException
    //   485	493	967	android/content/pm/PackageManager$NameNotFoundException
    //   519	532	967	android/content/pm/PackageManager$NameNotFoundException
    //   532	544	982	android/content/pm/PackageManager$NameNotFoundException
    //   532	544	993	android/content/res/Resources$NotFoundException
    //   544	551	1006	android/content/res/Resources$NotFoundException
    //   551	559	1006	android/content/res/Resources$NotFoundException
    //   563	580	1006	android/content/res/Resources$NotFoundException
    //   675	702	1006	android/content/res/Resources$NotFoundException
    //   532	544	1023	java/io/IOException
    //   544	551	1035	java/io/IOException
    //   551	559	1035	java/io/IOException
    //   563	580	1035	java/io/IOException
    //   675	702	1035	java/io/IOException
  }
  
  public void setDimensionHints(int paramInt1, int paramInt2, String arg3)
    throws RemoteException
  {
    checkPermission("android.permission.SET_WALLPAPER_HINTS");
    if (!isWallpaperSupported(???)) {
      return;
    }
    int i;
    synchronized (this.mLock)
    {
      i = UserHandle.getCallingUserId();
      WallpaperData localWallpaperData1 = getWallpaperSafeLocked(i, 1);
      if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
        throw new IllegalArgumentException("width and height must be > 0");
      }
    }
    Object localObject = getDefaultDisplaySize();
    paramInt1 = Math.max(paramInt1, ((Point)localObject).x);
    paramInt2 = Math.max(paramInt2, ((Point)localObject).y);
    if ((paramInt1 != localWallpaperData2.width) || (paramInt2 != localWallpaperData2.height))
    {
      localWallpaperData2.width = paramInt1;
      localWallpaperData2.height = paramInt2;
      saveSettingsLocked(i);
      int j = this.mCurrentUserId;
      if (j != i) {
        return;
      }
      if (localWallpaperData2.connection != null)
      {
        localObject = localWallpaperData2.connection.mEngine;
        if (localObject == null) {
          break label188;
        }
      }
    }
    try
    {
      localWallpaperData2.connection.mEngine.setDesiredSize(paramInt1, paramInt2);
      notifyCallbacksLocked(localWallpaperData2);
      for (;;)
      {
        return;
        label188:
        if (localWallpaperData2.connection.mService != null) {
          localWallpaperData2.connection.mDimensionsChanged = true;
        }
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public void setDisplayPadding(Rect paramRect, String arg2)
  {
    checkPermission("android.permission.SET_WALLPAPER_HINTS");
    if (!isWallpaperSupported(???)) {
      return;
    }
    int i;
    WallpaperData localWallpaperData;
    int j;
    IWallpaperEngine localIWallpaperEngine;
    synchronized (this.mLock)
    {
      i = UserHandle.getCallingUserId();
      localWallpaperData = getWallpaperSafeLocked(i, 1);
      if ((paramRect.left < 0) || (paramRect.top < 0)) {
        throw new IllegalArgumentException("padding must be positive: " + paramRect);
      }
    }
  }
  
  public boolean setLockWallpaperCallback(IWallpaperManagerCallback paramIWallpaperManagerCallback)
  {
    checkPermission("android.permission.INTERNAL_SYSTEM_WINDOW");
    synchronized (this.mLock)
    {
      this.mKeyguardListener = paramIWallpaperManagerCallback;
      return true;
    }
  }
  
  public ParcelFileDescriptor setWallpaper(String paramString1, String paramString2, Rect arg3, boolean paramBoolean, Bundle paramBundle, int paramInt1, IWallpaperManagerCallback paramIWallpaperManagerCallback, int paramInt2)
  {
    paramInt2 = ActivityManager.handleIncomingUser(getCallingPid(), getCallingUid(), paramInt2, false, true, "changing wallpaper", null);
    checkPermission("android.permission.SET_WALLPAPER");
    if ((paramInt1 & 0x3) == 0)
    {
      Slog.e("WallpaperManagerService", "Must specify a valid wallpaper category to set");
      throw new IllegalArgumentException("Must specify a valid wallpaper category to set");
    }
    Rect localRect;
    if ((isWallpaperSupported(paramString2)) && (isSetWallpaperAllowed(paramString2)))
    {
      if (??? != null) {
        break label269;
      }
      localRect = new Rect(0, 0, 0, 0);
    }
    long l;
    synchronized (this.mLock)
    {
      Slog.v("WallpaperManagerService", "setWallpaper which=0x" + Integer.toHexString(paramInt1) + ", " + paramString1 + ", " + paramString2 + ", " + paramInt2 + ", " + this.mCurrentUserId);
      if ((paramInt1 == 1) && (this.mLockWallpaperMap.get(paramInt2) == null)) {
        migrateSystemToLockWallpaperLocked(paramInt2);
      }
      if (paramInt1 == 2)
      {
        paramString2 = getWallpaperSafeLocked(this.mCurrentUserId, paramInt1);
        l = Binder.clearCallingIdentity();
      }
      try
      {
        paramString1 = updateWallpaperBitmapLocked(paramString1, paramString2, paramBundle);
        if (paramString1 != null)
        {
          paramString2.imageWallpaperPending = true;
          paramString2.whichPending = paramInt1;
          paramString2.setComplete = paramIWallpaperManagerCallback;
          paramString2.cropHint.set(localRect);
          paramString2.allowBackup = paramBoolean;
        }
        Binder.restoreCallingIdentity(l);
        return paramString1;
      }
      finally
      {
        label269:
        Binder.restoreCallingIdentity(l);
      }
      return null;
      if ((???.isEmpty()) || (???.left < 0)) {}
      for (;;)
      {
        throw new IllegalArgumentException("Invalid crop rect supplied: " + ???);
        localRect = ???;
        if (???.top >= 0) {
          break;
        }
      }
      paramString2 = getWallpaperSafeLocked(paramInt2, paramInt1);
    }
  }
  
  public void setWallpaperComponent(ComponentName paramComponentName)
  {
    setWallpaperComponent(paramComponentName, UserHandle.getCallingUserId());
  }
  
  public void setWallpaperComponentChecked(ComponentName paramComponentName, String paramString, int paramInt)
  {
    if ((isWallpaperSupported(paramString)) && (isSetWallpaperAllowed(paramString))) {
      setWallpaperComponent(paramComponentName, paramInt);
    }
  }
  
  public void settingsRestored()
  {
    if (Binder.getCallingUid() != 1000) {
      throw new RuntimeException("settingsRestored() can only be called from the system process");
    }
    for (;;)
    {
      WallpaperData localWallpaperData;
      boolean bool1;
      synchronized (this.mLock)
      {
        loadSettingsLocked(0, false);
        localWallpaperData = (WallpaperData)this.mWallpaperMap.get(0);
        localWallpaperData.wallpaperId = makeWallpaperIdLocked();
        localWallpaperData.allowBackup = true;
        if ((localWallpaperData.nextWallpaperComponent == null) || (localWallpaperData.nextWallpaperComponent.equals(this.mImageWallpaper)))
        {
          if (!"".equals(localWallpaperData.name)) {
            break label233;
          }
          bool1 = true;
          bool2 = bool1;
          if (bool1)
          {
            generateCrop(localWallpaperData);
            bindWallpaperComponentLocked(localWallpaperData.nextWallpaperComponent, true, false, localWallpaperData, null);
            bool2 = bool1;
          }
          if (!bool2)
          {
            Slog.e("WallpaperManagerService", "Failed to restore wallpaper: '" + localWallpaperData.name + "'");
            localWallpaperData.name = "";
            getWallpaperDir(0).delete();
          }
        }
      }
      synchronized (this.mLock)
      {
        saveSettingsLocked(0);
        return;
        if (!bindWallpaperComponentLocked(localWallpaperData.nextWallpaperComponent, false, false, localWallpaperData, null))
        {
          bindWallpaperComponentLocked(null, false, false, localWallpaperData, null);
          break label257;
          label233:
          bool1 = restoreNamedResourceLocked(localWallpaperData);
          continue;
          localObject2 = finally;
          throw ((Throwable)localObject2);
        }
      }
      label257:
      boolean bool2 = true;
    }
  }
  
  void stopObserver(WallpaperData paramWallpaperData)
  {
    if ((paramWallpaperData != null) && (paramWallpaperData.wallpaperObserver != null))
    {
      paramWallpaperData.wallpaperObserver.stopWatching();
      paramWallpaperData.wallpaperObserver = null;
    }
  }
  
  void stopObserversLocked(int paramInt)
  {
    stopObserver((WallpaperData)this.mWallpaperMap.get(paramInt));
    stopObserver((WallpaperData)this.mLockWallpaperMap.get(paramInt));
    this.mWallpaperMap.remove(paramInt);
    this.mLockWallpaperMap.remove(paramInt);
  }
  
  void switchUser(int paramInt, IRemoteCallback paramIRemoteCallback)
  {
    synchronized (this.mLock)
    {
      this.mCurrentUserId = paramInt;
      WallpaperData localWallpaperData = getWallpaperSafeLocked(paramInt, 1);
      if (localWallpaperData.wallpaperObserver == null)
      {
        localWallpaperData.wallpaperObserver = new WallpaperObserver(localWallpaperData);
        localWallpaperData.wallpaperObserver.startWatching();
      }
      switchWallpaper(localWallpaperData, paramIRemoteCallback);
      BackgroundThread.getHandler().post(new Runnable()
      {
        public void run()
        {
          WallpaperManagerService.-wrap3(WallpaperManagerService.this, WallpaperManagerService.this.mCurrentUserId);
        }
      });
      return;
    }
  }
  
  void switchWallpaper(WallpaperData paramWallpaperData, IRemoteCallback paramIRemoteCallback)
  {
    for (;;)
    {
      Object localObject1;
      Object localObject3;
      synchronized (this.mLock)
      {
        this.mWaitingForUnlock = false;
        if (paramWallpaperData.wallpaperComponent != null)
        {
          localObject1 = paramWallpaperData.wallpaperComponent;
          boolean bool = bindWallpaperComponentLocked((ComponentName)localObject1, true, false, paramWallpaperData, paramIRemoteCallback);
          if (!bool) {
            localObject3 = null;
          }
        }
      }
      try
      {
        localObject1 = this.mIPackageManager.getServiceInfo((ComponentName)localObject1, 262144, paramWallpaperData.userId);
        if (localObject1 == null)
        {
          Slog.w("WallpaperManagerService", "Failure starting previous wallpaper; clearing");
          clearWallpaperLocked(false, 1, paramWallpaperData.userId, paramIRemoteCallback);
        }
        for (;;)
        {
          return;
          localObject1 = paramWallpaperData.nextWallpaperComponent;
          break;
          Slog.w("WallpaperManagerService", "Wallpaper isn't direct boot aware; using fallback until unlocked");
          paramWallpaperData.wallpaperComponent = paramWallpaperData.nextWallpaperComponent;
          paramWallpaperData = new WallpaperData(paramWallpaperData.userId, "wallpaper_lock_orig", "wallpaper_lock");
          ensureSaneWallpaperData(paramWallpaperData);
          bindWallpaperComponentLocked(this.mImageWallpaper, true, false, paramWallpaperData, paramIRemoteCallback);
          this.mWaitingForUnlock = true;
        }
        paramWallpaperData = finally;
        throw paramWallpaperData;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Object localObject2 = localObject3;
        }
      }
    }
  }
  
  void systemReady()
  {
    Slog.v("WallpaperManagerService", "systemReady");
    Object localObject = (WallpaperData)this.mWallpaperMap.get(0);
    if (this.mImageWallpaper.equals(((WallpaperData)localObject).nextWallpaperComponent))
    {
      if (!((WallpaperData)localObject).cropExists()) {
        generateCrop((WallpaperData)localObject);
      }
      if (!((WallpaperData)localObject).cropExists()) {
        clearWallpaperLocked(false, 1, 0, null);
      }
    }
    localObject = new IntentFilter();
    ((IntentFilter)localObject).addAction("android.intent.action.USER_REMOVED");
    this.mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if ("android.intent.action.USER_REMOVED".equals(paramAnonymousIntent.getAction())) {
          WallpaperManagerService.this.onRemoveUser(paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536));
        }
      }
    }, (IntentFilter)localObject);
    localObject = new IntentFilter("android.intent.action.ACTION_SHUTDOWN");
    this.mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context arg1, Intent paramAnonymousIntent)
      {
        if ("android.intent.action.ACTION_SHUTDOWN".equals(paramAnonymousIntent.getAction())) {}
        synchronized (WallpaperManagerService.this.mLock)
        {
          WallpaperManagerService.this.mShuttingDown = true;
          return;
        }
      }
    }, (IntentFilter)localObject);
    try
    {
      ActivityManagerNative.getDefault().registerUserSwitchObserver(new IUserSwitchObserver.Stub()
      {
        public void onForegroundProfileSwitch(int paramAnonymousInt) {}
        
        public void onUserSwitchComplete(int paramAnonymousInt)
          throws RemoteException
        {}
        
        public void onUserSwitching(int paramAnonymousInt, IRemoteCallback paramAnonymousIRemoteCallback)
        {
          WallpaperManagerService.this.switchUser(paramAnonymousInt, paramAnonymousIRemoteCallback);
        }
      }, "WallpaperManagerService");
      return;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowAsRuntimeException();
    }
  }
  
  ParcelFileDescriptor updateWallpaperBitmapLocked(String paramString, WallpaperData paramWallpaperData, Bundle paramBundle)
  {
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    try
    {
      paramString = getWallpaperDir(paramWallpaperData.userId);
      if (!paramString.exists())
      {
        paramString.mkdir();
        FileUtils.setPermissions(paramString.getPath(), 505, -1, -1);
      }
      paramString = ParcelFileDescriptor.open(paramWallpaperData.wallpaperFile, 1006632960);
      if (!SELinux.restorecon(paramWallpaperData.wallpaperFile)) {
        return null;
      }
      paramWallpaperData.name = str;
      paramWallpaperData.wallpaperId = makeWallpaperIdLocked();
      if (paramBundle != null) {
        paramBundle.putInt("android.service.wallpaper.extra.ID", paramWallpaperData.wallpaperId);
      }
      Slog.v("WallpaperManagerService", "updateWallpaperBitmapLocked() : id=" + paramWallpaperData.wallpaperId + " name=" + str + " file=" + paramWallpaperData.wallpaperFile.getName());
      return paramString;
    }
    catch (FileNotFoundException paramString)
    {
      Slog.w("WallpaperManagerService", "Error setting wallpaper", paramString);
    }
    return null;
  }
  
  public static class Lifecycle
    extends SystemService
  {
    private WallpaperManagerService mService;
    
    public Lifecycle(Context paramContext)
    {
      super();
    }
    
    public void onBootPhase(int paramInt)
    {
      if (paramInt == 550) {
        this.mService.systemReady();
      }
      while (paramInt != 600) {
        return;
      }
      this.mService.switchUser(0, null);
    }
    
    public void onStart()
    {
      this.mService = new WallpaperManagerService(getContext());
      publishBinderService("wallpaper", this.mService);
    }
    
    public void onUnlockUser(int paramInt)
    {
      this.mService.onUnlockUser(paramInt);
    }
  }
  
  class MyPackageMonitor
    extends PackageMonitor
  {
    MyPackageMonitor() {}
    
    boolean doPackagesChangedLocked(boolean paramBoolean, WallpaperManagerService.WallpaperData paramWallpaperData)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      int i;
      if (paramWallpaperData.wallpaperComponent != null)
      {
        i = isPackageDisappearing(paramWallpaperData.wallpaperComponent.getPackageName());
        if (i != 3)
        {
          bool1 = bool2;
          if (i != 2) {}
        }
        else
        {
          bool2 = true;
          bool1 = bool2;
          if (paramBoolean)
          {
            Slog.w("WallpaperManagerService", "Wallpaper uninstalled, removing: " + paramWallpaperData.wallpaperComponent);
            WallpaperManagerService.this.clearWallpaperLocked(false, 1, paramWallpaperData.userId, null);
            bool1 = bool2;
          }
        }
      }
      if (paramWallpaperData.nextWallpaperComponent != null)
      {
        i = isPackageDisappearing(paramWallpaperData.nextWallpaperComponent.getPackageName());
        if ((i == 3) || (i == 2)) {
          paramWallpaperData.nextWallpaperComponent = null;
        }
      }
      if ((paramWallpaperData.wallpaperComponent != null) && (isPackageModified(paramWallpaperData.wallpaperComponent.getPackageName()))) {}
      try
      {
        WallpaperManagerService.this.mContext.getPackageManager().getServiceInfo(paramWallpaperData.wallpaperComponent, 786432);
        if ((paramWallpaperData.nextWallpaperComponent == null) || (!isPackageModified(paramWallpaperData.nextWallpaperComponent.getPackageName()))) {}
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException1)
      {
        for (;;)
        {
          try
          {
            WallpaperManagerService.this.mContext.getPackageManager().getServiceInfo(paramWallpaperData.nextWallpaperComponent, 786432);
            return bool1;
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException2)
          {
            paramWallpaperData.nextWallpaperComponent = null;
          }
          localNameNotFoundException1 = localNameNotFoundException1;
          Slog.w("WallpaperManagerService", "Wallpaper component gone, removing: " + paramWallpaperData.wallpaperComponent);
          WallpaperManagerService.this.clearWallpaperLocked(false, 1, paramWallpaperData.userId, null);
        }
      }
      return bool1;
    }
    
    public boolean onHandleForceStop(Intent paramIntent, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
    {
      paramIntent = WallpaperManagerService.this.mLock;
      boolean bool = false;
      try
      {
        paramInt = WallpaperManagerService.this.mCurrentUserId;
        int i = getChangingUserId();
        if (paramInt != i) {
          return false;
        }
        paramArrayOfString = (WallpaperManagerService.WallpaperData)WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
        if (paramArrayOfString != null) {
          bool = doPackagesChangedLocked(paramBoolean, paramArrayOfString);
        }
        return bool;
      }
      finally {}
    }
    
    public void onPackageModified(String paramString)
    {
      synchronized (WallpaperManagerService.this.mLock)
      {
        int i = WallpaperManagerService.this.mCurrentUserId;
        int j = getChangingUserId();
        if (i != j) {
          return;
        }
        WallpaperManagerService.WallpaperData localWallpaperData = (WallpaperManagerService.WallpaperData)WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
        if (localWallpaperData != null)
        {
          if ((localWallpaperData.wallpaperComponent != null) && (localWallpaperData.wallpaperComponent.getPackageName().equals(paramString))) {
            doPackagesChangedLocked(true, localWallpaperData);
          }
        }
        else {
          return;
        }
        return;
      }
    }
    
    public void onPackageUpdateFinished(String paramString, int paramInt)
    {
      synchronized (WallpaperManagerService.this.mLock)
      {
        paramInt = WallpaperManagerService.this.mCurrentUserId;
        int i = getChangingUserId();
        if (paramInt != i) {
          return;
        }
        WallpaperManagerService.WallpaperData localWallpaperData = (WallpaperManagerService.WallpaperData)WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
        if ((localWallpaperData != null) && (localWallpaperData.wallpaperComponent != null) && (localWallpaperData.wallpaperComponent.getPackageName().equals(paramString)))
        {
          localWallpaperData.wallpaperUpdating = false;
          paramString = localWallpaperData.wallpaperComponent;
          WallpaperManagerService.this.clearWallpaperComponentLocked(localWallpaperData);
          if (!WallpaperManagerService.this.bindWallpaperComponentLocked(paramString, false, false, localWallpaperData, null))
          {
            Slog.w("WallpaperManagerService", "Wallpaper no longer available; reverting to default");
            WallpaperManagerService.this.clearWallpaperLocked(false, 1, localWallpaperData.userId, null);
          }
        }
        return;
      }
    }
    
    public void onPackageUpdateStarted(String paramString, int paramInt)
    {
      synchronized (WallpaperManagerService.this.mLock)
      {
        paramInt = WallpaperManagerService.this.mCurrentUserId;
        int i = getChangingUserId();
        if (paramInt != i) {
          return;
        }
        WallpaperManagerService.WallpaperData localWallpaperData = (WallpaperManagerService.WallpaperData)WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
        if ((localWallpaperData != null) && (localWallpaperData.wallpaperComponent != null) && (localWallpaperData.wallpaperComponent.getPackageName().equals(paramString)))
        {
          localWallpaperData.wallpaperUpdating = true;
          if ((localWallpaperData.connection != null) && (WallpaperManagerService.this.mReadyToBeResetRunnable != null))
          {
            FgThread.getHandler().removeCallbacks(WallpaperManagerService.this.mReadyToBeResetRunnable);
            WallpaperManagerService.this.mReadyToBeResetRunnable = null;
          }
        }
        return;
      }
    }
    
    public void onSomePackagesChanged()
    {
      synchronized (WallpaperManagerService.this.mLock)
      {
        int i = WallpaperManagerService.this.mCurrentUserId;
        int j = getChangingUserId();
        if (i != j) {
          return;
        }
        WallpaperManagerService.WallpaperData localWallpaperData = (WallpaperManagerService.WallpaperData)WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
        if (localWallpaperData != null) {
          doPackagesChangedLocked(true, localWallpaperData);
        }
        return;
      }
    }
  }
  
  class WallpaperConnection
    extends IWallpaperConnection.Stub
    implements ServiceConnection
  {
    private static final long WALLPAPER_RECONNECT_TIMEOUT_MS = 5000L;
    boolean mDimensionsChanged = false;
    IWallpaperEngine mEngine;
    final WallpaperInfo mInfo;
    boolean mPaddingChanged = false;
    IRemoteCallback mReply;
    private Runnable mResetRunnable = new -void__init__com_android_server_wallpaper_WallpaperManagerService_this.0_android_app_WallpaperInfo_info_com_android_server_wallpaper_WallpaperManagerService.WallpaperData_wallpaper_LambdaImpl0();
    IWallpaperService mService;
    final Binder mToken = new Binder();
    WallpaperManagerService.WallpaperData mWallpaper;
    
    public WallpaperConnection(WallpaperInfo paramWallpaperInfo, WallpaperManagerService.WallpaperData paramWallpaperData)
    {
      this.mInfo = paramWallpaperInfo;
      this.mWallpaper = paramWallpaperData;
    }
    
    /* Error */
    public void attachEngine(IWallpaperEngine paramIWallpaperEngine)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 39	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   4: getfield 66	com/android/server/wallpaper/WallpaperManagerService:mLock	Ljava/lang/Object;
      //   7: astore_3
      //   8: aload_3
      //   9: monitorenter
      //   10: aload_0
      //   11: aload_1
      //   12: putfield 101	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:mEngine	Landroid/service/wallpaper/IWallpaperEngine;
      //   15: aload_0
      //   16: getfield 49	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:mDimensionsChanged	Z
      //   19: istore_2
      //   20: iload_2
      //   21: ifeq +31 -> 52
      //   24: aload_0
      //   25: getfield 101	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:mEngine	Landroid/service/wallpaper/IWallpaperEngine;
      //   28: aload_0
      //   29: getfield 60	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:mWallpaper	Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;
      //   32: getfield 104	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:width	I
      //   35: aload_0
      //   36: getfield 60	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:mWallpaper	Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;
      //   39: getfield 107	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:height	I
      //   42: invokeinterface 113 3 0
      //   47: aload_0
      //   48: iconst_0
      //   49: putfield 49	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:mDimensionsChanged	Z
      //   52: aload_0
      //   53: getfield 51	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:mPaddingChanged	Z
      //   56: istore_2
      //   57: iload_2
      //   58: ifeq +24 -> 82
      //   61: aload_0
      //   62: getfield 101	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:mEngine	Landroid/service/wallpaper/IWallpaperEngine;
      //   65: aload_0
      //   66: getfield 60	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:mWallpaper	Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;
      //   69: getfield 117	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:padding	Landroid/graphics/Rect;
      //   72: invokeinterface 121 2 0
      //   77: aload_0
      //   78: iconst_0
      //   79: putfield 51	com/android/server/wallpaper/WallpaperManagerService$WallpaperConnection:mPaddingChanged	Z
      //   82: aload_3
      //   83: monitorexit
      //   84: return
      //   85: astore_1
      //   86: ldc 83
      //   88: ldc 123
      //   90: aload_1
      //   91: invokestatic 126	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   94: pop
      //   95: goto -48 -> 47
      //   98: astore_1
      //   99: aload_3
      //   100: monitorexit
      //   101: aload_1
      //   102: athrow
      //   103: astore_1
      //   104: ldc 83
      //   106: ldc -128
      //   108: aload_1
      //   109: invokestatic 126	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   112: pop
      //   113: goto -36 -> 77
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	116	0	this	WallpaperConnection
      //   0	116	1	paramIWallpaperEngine	IWallpaperEngine
      //   19	39	2	bool	boolean
      //   7	93	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   24	47	85	android/os/RemoteException
      //   10	20	98	finally
      //   24	47	98	finally
      //   47	52	98	finally
      //   52	57	98	finally
      //   61	77	98	finally
      //   77	82	98	finally
      //   86	95	98	finally
      //   104	113	98	finally
      //   61	77	103	android/os/RemoteException
    }
    
    public void engineShown(IWallpaperEngine arg1)
    {
      synchronized (WallpaperManagerService.this.mLock)
      {
        long l;
        if (this.mReply != null) {
          l = Binder.clearCallingIdentity();
        }
        try
        {
          this.mReply.sendResult(null);
          this.mReply = null;
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Binder.restoreCallingIdentity(l);
          }
        }
      }
    }
    
    public void onServiceConnected(ComponentName arg1, IBinder paramIBinder)
    {
      synchronized (WallpaperManagerService.this.mLock)
      {
        if (this.mWallpaper.connection == this)
        {
          this.mService = IWallpaperService.Stub.asInterface(paramIBinder);
          WallpaperManagerService.this.attachServiceLocked(this, this.mWallpaper);
          WallpaperManagerService.-wrap5(WallpaperManagerService.this, this.mWallpaper.userId);
          if (WallpaperManagerService.this.mReadyToBeResetRunnable != null)
          {
            FgThread.getHandler().removeCallbacks(WallpaperManagerService.this.mReadyToBeResetRunnable);
            WallpaperManagerService.this.mReadyToBeResetRunnable = null;
          }
        }
        return;
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      synchronized (WallpaperManagerService.this.mLock)
      {
        this.mService = null;
        this.mEngine = null;
        if (this.mWallpaper.connection == this)
        {
          Slog.w("WallpaperManagerService", "Wallpaper service gone: " + this.mWallpaper.wallpaperComponent);
          if ((!this.mWallpaper.wallpaperUpdating) && (this.mWallpaper.userId == WallpaperManagerService.this.mCurrentUserId))
          {
            if ((this.mWallpaper.lastDiedTime == 0L) || (this.mWallpaper.lastDiedTime + 10000L <= SystemClock.uptimeMillis())) {
              break label173;
            }
            Slog.w("WallpaperManagerService", "Reverting to built-in wallpaper!");
            WallpaperManagerService.this.clearWallpaperLocked(true, 1, this.mWallpaper.userId, null);
            paramComponentName = paramComponentName.flattenToString();
            EventLog.writeEvent(33000, paramComponentName.substring(0, Math.min(paramComponentName.length(), 128)));
          }
        }
        return;
        label173:
        this.mWallpaper.lastDiedTime = SystemClock.uptimeMillis();
        if (WallpaperManagerService.this.mReadyToBeResetRunnable != null) {
          FgThread.getHandler().removeCallbacks(WallpaperManagerService.this.mReadyToBeResetRunnable);
        }
        WallpaperManagerService.this.mReadyToBeResetRunnable = this.mResetRunnable;
        FgThread.getHandler().postDelayed(WallpaperManagerService.this.mReadyToBeResetRunnable, 5000L);
      }
    }
    
    public ParcelFileDescriptor setWallpaper(String paramString)
    {
      synchronized (WallpaperManagerService.this.mLock)
      {
        if (this.mWallpaper.connection == this)
        {
          paramString = WallpaperManagerService.this.updateWallpaperBitmapLocked(paramString, this.mWallpaper, null);
          return paramString;
        }
        return null;
      }
    }
  }
  
  static class WallpaperData
  {
    boolean allowBackup;
    private RemoteCallbackList<IWallpaperManagerCallback> callbacks = new RemoteCallbackList();
    WallpaperManagerService.WallpaperConnection connection;
    final File cropFile;
    final Rect cropHint = new Rect(0, 0, 0, 0);
    int height = -1;
    boolean imageWallpaperPending;
    long lastDiedTime;
    String name = "";
    ComponentName nextWallpaperComponent;
    final Rect padding = new Rect(0, 0, 0, 0);
    IWallpaperManagerCallback setComplete;
    int userId;
    ComponentName wallpaperComponent;
    final File wallpaperFile;
    int wallpaperId;
    WallpaperManagerService.WallpaperObserver wallpaperObserver;
    boolean wallpaperUpdating;
    int whichPending;
    int width = -1;
    
    WallpaperData(int paramInt, String paramString1, String paramString2)
    {
      this.userId = paramInt;
      File localFile = WallpaperManagerService.-wrap0(paramInt);
      this.wallpaperFile = new File(localFile, paramString1);
      this.cropFile = new File(localFile, paramString2);
    }
    
    boolean cropExists()
    {
      return this.cropFile.exists();
    }
  }
  
  private class WallpaperObserver
    extends FileObserver
  {
    final int mUserId;
    final WallpaperManagerService.WallpaperData mWallpaper;
    final File mWallpaperDir;
    final File mWallpaperFile;
    final File mWallpaperLockFile;
    
    public WallpaperObserver(WallpaperManagerService.WallpaperData paramWallpaperData)
    {
      super(1672);
      this.mUserId = paramWallpaperData.userId;
      this.mWallpaperDir = WallpaperManagerService.-wrap0(paramWallpaperData.userId);
      this.mWallpaper = paramWallpaperData;
      this.mWallpaperFile = new File(this.mWallpaperDir, "wallpaper_orig");
      this.mWallpaperLockFile = new File(this.mWallpaperDir, "wallpaper_lock_orig");
    }
    
    private WallpaperManagerService.WallpaperData dataForEvent(boolean paramBoolean1, boolean paramBoolean2)
    {
      WallpaperManagerService.WallpaperData localWallpaperData1 = null;
      Object localObject2 = WallpaperManagerService.this.mLock;
      if (paramBoolean2) {}
      try
      {
        localWallpaperData1 = (WallpaperManagerService.WallpaperData)WallpaperManagerService.this.mLockWallpaperMap.get(this.mUserId);
        WallpaperManagerService.WallpaperData localWallpaperData2 = localWallpaperData1;
        if (localWallpaperData1 == null) {
          localWallpaperData2 = (WallpaperManagerService.WallpaperData)WallpaperManagerService.this.mWallpaperMap.get(this.mUserId);
        }
        if (localWallpaperData2 != null) {
          return localWallpaperData2;
        }
      }
      finally {}
      return this.mWallpaper;
    }
    
    /* Error */
    public void onEvent(int paramInt, String paramString)
    {
      // Byte code:
      //   0: aload_2
      //   1: ifnonnull +4 -> 5
      //   4: return
      //   5: iload_1
      //   6: sipush 128
      //   9: if_icmpne +83 -> 92
      //   12: iconst_1
      //   13: istore_3
      //   14: iload_1
      //   15: bipush 8
      //   17: if_icmpeq +80 -> 97
      //   20: iload_3
      //   21: istore 4
      //   23: new 32	java/io/File
      //   26: dup
      //   27: aload_0
      //   28: getfield 43	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:mWallpaperDir	Ljava/io/File;
      //   31: aload_2
      //   32: invokespecial 50	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   35: astore 9
      //   37: aload_0
      //   38: getfield 52	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:mWallpaperFile	Ljava/io/File;
      //   41: aload 9
      //   43: invokevirtual 84	java/io/File:equals	(Ljava/lang/Object;)Z
      //   46: istore 5
      //   48: aload_0
      //   49: getfield 56	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:mWallpaperLockFile	Ljava/io/File;
      //   52: aload 9
      //   54: invokevirtual 84	java/io/File:equals	(Ljava/lang/Object;)Z
      //   57: istore 6
      //   59: aload_0
      //   60: iload 5
      //   62: iload 6
      //   64: invokespecial 86	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:dataForEvent	(ZZ)Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;
      //   67: astore 8
      //   69: iload_3
      //   70: ifeq +33 -> 103
      //   73: iload 6
      //   75: ifeq +28 -> 103
      //   78: aload 9
      //   80: invokestatic 92	android/os/SELinux:restorecon	(Ljava/io/File;)Z
      //   83: pop
      //   84: aload_0
      //   85: getfield 21	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   88: invokevirtual 96	com/android/server/wallpaper/WallpaperManagerService:notifyLockWallpaperChanged	()V
      //   91: return
      //   92: iconst_0
      //   93: istore_3
      //   94: goto -80 -> 14
      //   97: iconst_1
      //   98: istore 4
      //   100: goto -77 -> 23
      //   103: aload_0
      //   104: getfield 21	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   107: getfield 63	com/android/server/wallpaper/WallpaperManagerService:mLock	Ljava/lang/Object;
      //   110: astore_2
      //   111: aload_2
      //   112: monitorenter
      //   113: iload 5
      //   115: ifne +8 -> 123
      //   118: iload 6
      //   120: ifeq +202 -> 322
      //   123: aload_0
      //   124: getfield 21	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   127: aload 8
      //   129: invokestatic 99	com/android/server/wallpaper/WallpaperManagerService:-wrap4	(Lcom/android/server/wallpaper/WallpaperManagerService;Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;)V
      //   132: aload 8
      //   134: getfield 103	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:wallpaperComponent	Landroid/content/ComponentName;
      //   137: ifnull +9 -> 146
      //   140: iload_1
      //   141: bipush 8
      //   143: if_icmpeq +182 -> 325
      //   146: iload 4
      //   148: ifeq +174 -> 322
      //   151: aload 9
      //   153: invokestatic 92	android/os/SELinux:restorecon	(Ljava/io/File;)Z
      //   156: pop
      //   157: iload_3
      //   158: ifeq +22 -> 180
      //   161: aload 9
      //   163: invokestatic 92	android/os/SELinux:restorecon	(Ljava/io/File;)Z
      //   166: pop
      //   167: aload_0
      //   168: getfield 21	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   171: aload 8
      //   173: getfield 26	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:userId	I
      //   176: iconst_1
      //   177: invokestatic 107	com/android/server/wallpaper/WallpaperManagerService:-wrap2	(Lcom/android/server/wallpaper/WallpaperManagerService;IZ)V
      //   180: aload_0
      //   181: getfield 21	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   184: aload 8
      //   186: invokestatic 110	com/android/server/wallpaper/WallpaperManagerService:-wrap1	(Lcom/android/server/wallpaper/WallpaperManagerService;Lcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;)V
      //   189: aload 8
      //   191: iconst_0
      //   192: putfield 114	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:imageWallpaperPending	Z
      //   195: aload 8
      //   197: getfield 118	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:setComplete	Landroid/app/IWallpaperManagerCallback;
      //   200: astore 9
      //   202: aload 9
      //   204: ifnull +13 -> 217
      //   207: aload 8
      //   209: getfield 118	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:setComplete	Landroid/app/IWallpaperManagerCallback;
      //   212: invokeinterface 123 1 0
      //   217: iload 5
      //   219: ifeq +23 -> 242
      //   222: aload_0
      //   223: getfield 21	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   226: aload_0
      //   227: getfield 21	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   230: getfield 126	com/android/server/wallpaper/WallpaperManagerService:mImageWallpaper	Landroid/content/ComponentName;
      //   233: iconst_1
      //   234: iconst_0
      //   235: aload 8
      //   237: aconst_null
      //   238: invokevirtual 130	com/android/server/wallpaper/WallpaperManagerService:bindWallpaperComponentLocked	(Landroid/content/ComponentName;ZZLcom/android/server/wallpaper/WallpaperManagerService$WallpaperData;Landroid/os/IRemoteCallback;)Z
      //   241: pop
      //   242: iload 6
      //   244: ifne +13 -> 257
      //   247: aload 8
      //   249: getfield 133	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:whichPending	I
      //   252: iconst_2
      //   253: iand
      //   254: ifeq +56 -> 310
      //   257: ldc -121
      //   259: new 137	java/lang/StringBuilder
      //   262: dup
      //   263: invokespecial 139	java/lang/StringBuilder:<init>	()V
      //   266: ldc -115
      //   268: invokevirtual 145	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   271: iload 6
      //   273: invokevirtual 148	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
      //   276: invokevirtual 151	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   279: invokestatic 157	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   282: pop
      //   283: iload 6
      //   285: ifne +18 -> 303
      //   288: aload_0
      //   289: getfield 21	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   292: getfield 67	com/android/server/wallpaper/WallpaperManagerService:mLockWallpaperMap	Landroid/util/SparseArray;
      //   295: aload 8
      //   297: getfield 26	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:userId	I
      //   300: invokevirtual 161	android/util/SparseArray:remove	(I)V
      //   303: aload_0
      //   304: getfield 21	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   307: invokevirtual 96	com/android/server/wallpaper/WallpaperManagerService:notifyLockWallpaperChanged	()V
      //   310: aload_0
      //   311: getfield 21	com/android/server/wallpaper/WallpaperManagerService$WallpaperObserver:this$0	Lcom/android/server/wallpaper/WallpaperManagerService;
      //   314: aload 8
      //   316: getfield 26	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:userId	I
      //   319: invokestatic 165	com/android/server/wallpaper/WallpaperManagerService:-wrap5	(Lcom/android/server/wallpaper/WallpaperManagerService;I)V
      //   322: aload_2
      //   323: monitorexit
      //   324: return
      //   325: aload 8
      //   327: getfield 114	com/android/server/wallpaper/WallpaperManagerService$WallpaperData:imageWallpaperPending	Z
      //   330: istore 7
      //   332: iload 7
      //   334: ifeq -12 -> 322
      //   337: goto -191 -> 146
      //   340: astore 8
      //   342: aload_2
      //   343: monitorexit
      //   344: aload 8
      //   346: athrow
      //   347: astore 9
      //   349: goto -132 -> 217
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	352	0	this	WallpaperObserver
      //   0	352	1	paramInt	int
      //   0	352	2	paramString	String
      //   13	145	3	i	int
      //   21	126	4	j	int
      //   46	172	5	bool1	boolean
      //   57	227	6	bool2	boolean
      //   330	3	7	bool3	boolean
      //   67	259	8	localWallpaperData	WallpaperManagerService.WallpaperData
      //   340	5	8	localObject1	Object
      //   35	168	9	localObject2	Object
      //   347	1	9	localRemoteException	RemoteException
      // Exception table:
      //   from	to	target	type
      //   123	140	340	finally
      //   151	157	340	finally
      //   161	180	340	finally
      //   180	202	340	finally
      //   207	217	340	finally
      //   222	242	340	finally
      //   247	257	340	finally
      //   257	283	340	finally
      //   288	303	340	finally
      //   303	310	340	finally
      //   310	322	340	finally
      //   325	332	340	finally
      //   207	217	347	android/os/RemoteException
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wallpaper/WallpaperManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */