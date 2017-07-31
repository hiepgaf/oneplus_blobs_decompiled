package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadSystemException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.IWindowSession;
import android.view.WindowManagerGlobal;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;

public class WallpaperManager
{
  public static final String ACTION_CHANGE_LIVE_WALLPAPER = "android.service.wallpaper.CHANGE_LIVE_WALLPAPER";
  public static final String ACTION_CROP_AND_SET_WALLPAPER = "android.service.wallpaper.CROP_AND_SET_WALLPAPER";
  public static final String ACTION_LIVE_WALLPAPER_CHOOSER = "android.service.wallpaper.LIVE_WALLPAPER_CHOOSER";
  public static final String COMMAND_DROP = "android.home.drop";
  public static final String COMMAND_SECONDARY_TAP = "android.wallpaper.secondaryTap";
  public static final String COMMAND_TAP = "android.wallpaper.tap";
  private static boolean DEBUG = false;
  public static final String EXTRA_LIVE_WALLPAPER_COMPONENT = "android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT";
  public static final String EXTRA_NEW_WALLPAPER_ID = "android.service.wallpaper.extra.ID";
  public static final int FLAG_LOCK = 2;
  public static final int FLAG_SYSTEM = 1;
  private static final String PROP_LOCK_WALLPAPER = "ro.config.lock_wallpaper";
  private static final String PROP_WALLPAPER = "ro.config.wallpaper";
  private static final String PROP_WALLPAPER_COMPONENT = "ro.config.wallpaper_component";
  private static String TAG = "WallpaperManager";
  public static final String WALLPAPER_PREVIEW_META_DATA = "android.wallpaper.preview";
  private static Globals sGlobals;
  private static final Object sSync = new Object[0];
  private final Context mContext;
  private float mWallpaperXStep = -1.0F;
  private float mWallpaperYStep = -1.0F;
  
  WallpaperManager(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    initGlobals(paramContext.getMainLooper());
  }
  
  private void copyStreamToWallpaperFile(InputStream paramInputStream, FileOutputStream paramFileOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[32768];
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i <= 0) {
        break;
      }
      paramFileOutputStream.write(arrayOfByte, 0, i);
    }
  }
  
  public static ComponentName getDefaultWallpaperComponent(Context paramContext)
  {
    Object localObject = SystemProperties.get("ro.config.wallpaper_component");
    if (!TextUtils.isEmpty((CharSequence)localObject))
    {
      localObject = ComponentName.unflattenFromString((String)localObject);
      if (localObject != null) {
        return (ComponentName)localObject;
      }
    }
    paramContext = paramContext.getString(17039421);
    if (!TextUtils.isEmpty(paramContext))
    {
      paramContext = ComponentName.unflattenFromString(paramContext);
      if (paramContext != null) {
        return paramContext;
      }
    }
    return null;
  }
  
  public static WallpaperManager getInstance(Context paramContext)
  {
    return (WallpaperManager)paramContext.getSystemService("wallpaper");
  }
  
  private static RectF getMaxCropRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, float paramFloat2)
  {
    RectF localRectF = new RectF();
    if (paramInt1 / paramInt2 > paramInt3 / paramInt4)
    {
      localRectF.top = 0.0F;
      localRectF.bottom = paramInt2;
      paramFloat2 = paramInt3 * (paramInt2 / paramInt4);
      localRectF.left = ((paramInt1 - paramFloat2) * paramFloat1);
      localRectF.right = (localRectF.left + paramFloat2);
      return localRectF;
    }
    localRectF.left = 0.0F;
    localRectF.right = paramInt1;
    paramFloat1 = paramInt4 * (paramInt1 / paramInt3);
    localRectF.top = ((paramInt2 - paramFloat1) * paramFloat2);
    localRectF.bottom = (localRectF.top + paramFloat1);
    return localRectF;
  }
  
  static void initGlobals(Looper paramLooper)
  {
    synchronized (sSync)
    {
      if (sGlobals == null) {
        sGlobals = new Globals(paramLooper);
      }
      return;
    }
  }
  
  public static InputStream openDefaultWallpaper(Context paramContext, int paramInt)
  {
    if (paramInt == 2) {
      return null;
    }
    Object localObject = SystemProperties.get("ro.config.wallpaper");
    if (!TextUtils.isEmpty((CharSequence)localObject))
    {
      localObject = new File((String)localObject);
      if (((File)localObject).exists()) {
        try
        {
          localObject = new FileInputStream((File)localObject);
          return (InputStream)localObject;
        }
        catch (IOException localIOException) {}
      }
    }
    try
    {
      paramContext = paramContext.getResources().openRawResource(17302107);
      return paramContext;
    }
    catch (Resources.NotFoundException paramContext) {}
    return null;
  }
  
  private final void validateRect(Rect paramRect)
  {
    if ((paramRect != null) && (paramRect.isEmpty())) {
      throw new IllegalArgumentException("visibleCrop rectangle must be valid and non-empty");
    }
  }
  
  public void clear()
    throws IOException
  {
    setStream(openDefaultWallpaper(this.mContext, 1), null, false);
  }
  
  public void clear(int paramInt)
    throws IOException
  {
    if ((paramInt & 0x1) != 0) {
      clear();
    }
    if ((paramInt & 0x2) != 0) {
      clearWallpaper(2, this.mContext.getUserId());
    }
  }
  
  public void clearWallpaper()
  {
    clearWallpaper(2, this.mContext.getUserId());
    clearWallpaper(1, this.mContext.getUserId());
  }
  
  public void clearWallpaper(int paramInt1, int paramInt2)
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    try
    {
      Globals.-get0(sGlobals).clearWallpaper(this.mContext.getOpPackageName(), paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void clearWallpaperOffsets(IBinder paramIBinder)
  {
    try
    {
      WindowManagerGlobal.getWindowSession().setWallpaperPosition(paramIBinder, -1.0F, -1.0F, -1.0F, -1.0F);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw paramIBinder.rethrowFromSystemServer();
    }
  }
  
  public void forgetLoadedWallpaper()
  {
    sGlobals.forgetLoadedWallpaper();
  }
  
  public Bitmap getBitmap()
  {
    return getBitmapAsUser(this.mContext.getUserId());
  }
  
  public Bitmap getBitmapAsUser(int paramInt)
  {
    return sGlobals.peekWallpaperBitmap(this.mContext, true, 1, paramInt);
  }
  
  public Drawable getBuiltInDrawable()
  {
    return getBuiltInDrawable(0, 0, false, 0.0F, 0.0F, 1);
  }
  
  public Drawable getBuiltInDrawable(int paramInt)
  {
    return getBuiltInDrawable(0, 0, false, 0.0F, 0.0F, paramInt);
  }
  
  public Drawable getBuiltInDrawable(int paramInt1, int paramInt2, boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    return getBuiltInDrawable(paramInt1, paramInt2, paramBoolean, paramFloat1, paramFloat2, 1);
  }
  
  public Drawable getBuiltInDrawable(int paramInt1, int paramInt2, boolean paramBoolean, float paramFloat1, float paramFloat2, int paramInt3)
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    if ((paramInt3 != 1) && (paramInt3 != 2)) {
      throw new IllegalArgumentException("Must request exactly one kind of wallpaper");
    }
    localResources = this.mContext.getResources();
    float f = Math.max(0.0F, Math.min(1.0F, paramFloat1));
    paramFloat1 = Math.max(0.0F, Math.min(1.0F, paramFloat2));
    localObject1 = openDefaultWallpaper(this.mContext, paramInt3);
    if (localObject1 == null)
    {
      if (DEBUG) {
        Log.w(TAG, "default wallpaper stream " + paramInt3 + " is null");
      }
      return null;
    }
    localObject1 = new BufferedInputStream((InputStream)localObject1);
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      return new BitmapDrawable(localResources, BitmapFactory.decodeStream((InputStream)localObject1, null, null));
    }
    Object localObject2 = new BitmapFactory.Options();
    ((BitmapFactory.Options)localObject2).inJustDecodeBounds = true;
    BitmapFactory.decodeStream((InputStream)localObject1, null, (BitmapFactory.Options)localObject2);
    int j;
    if ((((BitmapFactory.Options)localObject2).outWidth != 0) && (((BitmapFactory.Options)localObject2).outHeight != 0))
    {
      i = ((BitmapFactory.Options)localObject2).outWidth;
      j = ((BitmapFactory.Options)localObject2).outHeight;
      localObject2 = new BufferedInputStream(openDefaultWallpaper(this.mContext, paramInt3));
      paramInt1 = Math.min(i, paramInt1);
      paramInt2 = Math.min(j, paramInt2);
      if (!paramBoolean) {
        break label344;
      }
    }
    for (localObject1 = getMaxCropRect(i, j, paramInt1, paramInt2, f, paramFloat1);; localObject1 = new RectF(paramFloat2, paramFloat1, paramFloat2 + f, paramFloat1 + paramInt2))
    {
      localObject4 = new Rect();
      ((RectF)localObject1).roundOut((Rect)localObject4);
      if ((((Rect)localObject4).width() > 0) && (((Rect)localObject4).height() > 0)) {
        break;
      }
      Log.w(TAG, "crop has bad values for full size image");
      return null;
      Log.e(TAG, "default wallpaper dimensions are 0");
      return null;
      label344:
      paramFloat2 = (i - paramInt1) * f;
      f = paramInt1;
      paramFloat1 = (j - paramInt2) * paramFloat1;
    }
    int i = Math.min(((Rect)localObject4).width() / paramInt1, ((Rect)localObject4).height() / paramInt2);
    localObject1 = null;
    try
    {
      localObject2 = BitmapRegionDecoder.newInstance((InputStream)localObject2, true);
      localObject1 = localObject2;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Log.w(TAG, "cannot open region decoder for default wallpaper");
      }
      Object localObject3 = localObject1;
      if (paramInt1 <= 0) {
        break label769;
      }
      localObject3 = localObject1;
      if (paramInt2 <= 0) {
        break label769;
      }
      if (((Bitmap)localObject1).getWidth() != paramInt1) {
        break label639;
      }
      localObject3 = localObject1;
      if (((Bitmap)localObject1).getHeight() == paramInt2) {
        break label769;
      }
      Object localObject5 = new Matrix();
      localObject3 = new RectF(0.0F, 0.0F, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight());
      localObject4 = new RectF(0.0F, 0.0F, paramInt1, paramInt2);
      ((Matrix)localObject5).setRectToRect((RectF)localObject3, (RectF)localObject4, Matrix.ScaleToFit.FILL);
      localObject4 = Bitmap.createBitmap((int)((RectF)localObject4).width(), (int)((RectF)localObject4).height(), Bitmap.Config.ARGB_8888);
      localObject3 = localObject1;
      if (localObject4 == null) {
        break label769;
      }
      localObject3 = new Canvas((Bitmap)localObject4);
      Paint localPaint = new Paint();
      localPaint.setFilterBitmap(true);
      ((Canvas)localObject3).drawBitmap((Bitmap)localObject1, (Matrix)localObject5, localPaint);
      localObject3 = localObject4;
      return new BitmapDrawable(localResources, (Bitmap)localObject3);
    }
    localObject2 = null;
    if (localObject1 != null)
    {
      localObject2 = new BitmapFactory.Options();
      if (i > 1) {
        ((BitmapFactory.Options)localObject2).inSampleSize = i;
      }
      localObject2 = ((BitmapRegionDecoder)localObject1).decodeRegion((Rect)localObject4, (BitmapFactory.Options)localObject2);
      ((BitmapRegionDecoder)localObject1).recycle();
    }
    localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = new BufferedInputStream(openDefaultWallpaper(this.mContext, paramInt3));
      localObject5 = new BitmapFactory.Options();
      if (i > 1) {
        ((BitmapFactory.Options)localObject5).inSampleSize = i;
      }
      localObject5 = BitmapFactory.decodeStream((InputStream)localObject1, null, (BitmapFactory.Options)localObject5);
      localObject1 = localObject2;
      if (localObject5 != null) {
        localObject1 = Bitmap.createBitmap((Bitmap)localObject5, ((Rect)localObject4).left, ((Rect)localObject4).top, ((Rect)localObject4).width(), ((Rect)localObject4).height());
      }
    }
    if (localObject1 == null)
    {
      Log.w(TAG, "cannot decode default wallpaper");
      return null;
    }
  }
  
  public Intent getCropAndSetWallpaperIntent(Uri paramUri)
  {
    if (paramUri == null) {
      throw new IllegalArgumentException("Image URI must not be null");
    }
    if (!"content".equals(paramUri.getScheme())) {
      throw new IllegalArgumentException("Image URI must be of the content scheme type");
    }
    PackageManager localPackageManager = this.mContext.getPackageManager();
    paramUri = new Intent("android.service.wallpaper.CROP_AND_SET_WALLPAPER", paramUri);
    paramUri.addFlags(1);
    ResolveInfo localResolveInfo = localPackageManager.resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 65536);
    if (localResolveInfo != null)
    {
      paramUri.setPackage(localResolveInfo.activityInfo.packageName);
      if (localPackageManager.queryIntentActivities(paramUri, 0).size() > 0) {
        return paramUri;
      }
    }
    paramUri.setPackage(this.mContext.getString(17039474));
    if (localPackageManager.queryIntentActivities(paramUri, 0).size() > 0) {
      return paramUri;
    }
    throw new IllegalArgumentException("Cannot use passed URI to set wallpaper; check that the type returned by ContentProvider matches image/*");
  }
  
  public int getDesiredMinimumHeight()
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    try
    {
      int i = Globals.-get0(sGlobals).getHeightHint();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getDesiredMinimumWidth()
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    try
    {
      int i = Globals.-get0(sGlobals).getWidthHint();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Drawable getDrawable()
  {
    Object localObject = sGlobals.peekWallpaperBitmap(this.mContext, true, 1);
    if (localObject != null)
    {
      localObject = new BitmapDrawable(this.mContext.getResources(), (Bitmap)localObject);
      ((Drawable)localObject).setDither(false);
      return (Drawable)localObject;
    }
    return null;
  }
  
  public Drawable getFastDrawable()
  {
    Bitmap localBitmap = sGlobals.peekWallpaperBitmap(this.mContext, true, 1);
    if (localBitmap != null) {
      return new FastBitmapDrawable(localBitmap, null);
    }
    return null;
  }
  
  public IWallpaperManager getIWallpaperManager()
  {
    return Globals.-get0(sGlobals);
  }
  
  public Bitmap getKeyguardBitmap()
  {
    Bitmap localBitmap = null;
    ParcelFileDescriptor localParcelFileDescriptor = getWallpaperFile(2);
    if (localParcelFileDescriptor != null) {
      try
      {
        FileDescriptor localFileDescriptor = localParcelFileDescriptor.getFileDescriptor();
        if (localFileDescriptor != null) {
          localBitmap = BitmapFactory.decodeFileDescriptor(localFileDescriptor);
        }
        for (;;)
        {
          return localBitmap;
          Log.e(TAG, "fileDescriptor is null");
        }
        Log.e(TAG, "parcelFileDescriptor is null");
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        Log.w(TAG, "Can't decode file", localOutOfMemoryError);
        return null;
      }
      finally
      {
        IoUtils.closeQuietly(localParcelFileDescriptor);
      }
    }
    return null;
  }
  
  public ParcelFileDescriptor getWallpaperFile(int paramInt)
  {
    return getWallpaperFile(paramInt, this.mContext.getUserId());
  }
  
  public ParcelFileDescriptor getWallpaperFile(int paramInt1, int paramInt2)
  {
    if ((paramInt1 != 1) && (paramInt1 != 2)) {
      throw new IllegalArgumentException("Must request exactly one kind of wallpaper");
    }
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    try
    {
      Object localObject = new Bundle();
      localObject = Globals.-get0(sGlobals).getWallpaper(null, paramInt1, (Bundle)localObject, paramInt2);
      return (ParcelFileDescriptor)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getWallpaperId(int paramInt)
  {
    return getWallpaperIdForUser(paramInt, this.mContext.getUserId());
  }
  
  public int getWallpaperIdForUser(int paramInt1, int paramInt2)
  {
    try
    {
      if (Globals.-get0(sGlobals) == null)
      {
        Log.w(TAG, "WallpaperService not running");
        throw new RuntimeException(new DeadSystemException());
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    paramInt1 = Globals.-get0(sGlobals).getWallpaperIdForUser(paramInt1, paramInt2);
    return paramInt1;
  }
  
  public WallpaperInfo getWallpaperInfo()
  {
    try
    {
      if (Globals.-get0(sGlobals) == null)
      {
        Log.w(TAG, "WallpaperService not running");
        throw new RuntimeException(new DeadSystemException());
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    WallpaperInfo localWallpaperInfo = Globals.-get0(sGlobals).getWallpaperInfo(UserHandle.myUserId());
    return localWallpaperInfo;
  }
  
  public boolean hasResourceWallpaper(int paramInt)
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    try
    {
      Object localObject = this.mContext.getResources();
      localObject = "res:" + ((Resources)localObject).getResourceName(paramInt);
      boolean bool = Globals.-get0(sGlobals).hasNamedWallpaper((String)localObject);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isSetWallpaperAllowed()
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    try
    {
      boolean bool = Globals.-get0(sGlobals).isSetWallpaperAllowed(this.mContext.getOpPackageName());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isWallpaperBackupEligible(int paramInt)
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    try
    {
      boolean bool = Globals.-get0(sGlobals).isWallpaperBackupEligible(paramInt, this.mContext.getUserId());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(TAG, "Exception querying wallpaper backup eligibility: " + localRemoteException.getMessage());
    }
    return false;
  }
  
  public boolean isWallpaperSupported()
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    try
    {
      boolean bool = Globals.-get0(sGlobals).isWallpaperSupported(this.mContext.getOpPackageName());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Drawable peekDrawable()
  {
    Object localObject = sGlobals.peekWallpaperBitmap(this.mContext, false, 1);
    if (localObject != null)
    {
      localObject = new BitmapDrawable(this.mContext.getResources(), (Bitmap)localObject);
      ((Drawable)localObject).setDither(false);
      return (Drawable)localObject;
    }
    return null;
  }
  
  public Drawable peekFastDrawable()
  {
    Bitmap localBitmap = sGlobals.peekWallpaperBitmap(this.mContext, false, 1);
    if (localBitmap != null) {
      return new FastBitmapDrawable(localBitmap, null);
    }
    return null;
  }
  
  public void sendWallpaperCommand(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle)
  {
    try
    {
      WindowManagerGlobal.getWindowSession().sendWallpaperCommand(paramIBinder, paramString, paramInt1, paramInt2, paramInt3, paramBundle, false);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw paramIBinder.rethrowFromSystemServer();
    }
  }
  
  public int setBitmap(Bitmap paramBitmap, Rect paramRect, boolean paramBoolean)
    throws IOException
  {
    return setBitmap(paramBitmap, paramRect, paramBoolean, 3);
  }
  
  public int setBitmap(Bitmap paramBitmap, Rect paramRect, boolean paramBoolean, int paramInt)
    throws IOException
  {
    return setBitmap(paramBitmap, paramRect, paramBoolean, paramInt, UserHandle.myUserId());
  }
  
  public int setBitmap(Bitmap paramBitmap, Rect paramRect, boolean paramBoolean, int paramInt1, int paramInt2)
    throws IOException
  {
    validateRect(paramRect);
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    Bundle localBundle = new Bundle();
    WallpaperSetCompletion localWallpaperSetCompletion = new WallpaperSetCompletion();
    try
    {
      localObject = Globals.-get0(sGlobals).setWallpaper(null, this.mContext.getOpPackageName(), paramRect, paramBoolean, localBundle, paramInt1, localWallpaperSetCompletion, paramInt2);
      if (localObject != null) {
        paramRect = null;
      }
      try
      {
        localObject = new ParcelFileDescriptor.AutoCloseOutputStream((ParcelFileDescriptor)localObject);
        IoUtils.closeQuietly(paramRect);
      }
      finally
      {
        try
        {
          paramBitmap.compress(Bitmap.CompressFormat.PNG, 90, (OutputStream)localObject);
          ((FileOutputStream)localObject).close();
          localWallpaperSetCompletion.waitForCompletion();
          IoUtils.closeQuietly((AutoCloseable)localObject);
          return localBundle.getInt("android.service.wallpaper.extra.ID", 0);
        }
        finally
        {
          paramRect = (Rect)localObject;
        }
        paramBitmap = finally;
      }
      throw paramBitmap;
    }
    catch (RemoteException paramBitmap)
    {
      throw paramBitmap.rethrowFromSystemServer();
    }
  }
  
  public void setBitmap(Bitmap paramBitmap)
    throws IOException
  {
    setBitmap(paramBitmap, null, true);
  }
  
  public void setDisplayOffset(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    try
    {
      WindowManagerGlobal.getWindowSession().setWallpaperDisplayOffset(paramIBinder, paramInt1, paramInt2);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw paramIBinder.rethrowFromSystemServer();
    }
  }
  
  public void setDisplayPadding(Rect paramRect)
  {
    try
    {
      if (Globals.-get0(sGlobals) == null)
      {
        Log.w(TAG, "WallpaperService not running");
        throw new RuntimeException(new DeadSystemException());
      }
    }
    catch (RemoteException paramRect)
    {
      throw paramRect.rethrowFromSystemServer();
    }
    Globals.-get0(sGlobals).setDisplayPadding(paramRect, this.mContext.getOpPackageName());
  }
  
  public void setKeyguardStream(InputStream paramInputStream)
    throws IOException
  {
    setStream(paramInputStream, null, true, 2);
  }
  
  public boolean setLockWallpaperCallback(IWallpaperManagerCallback paramIWallpaperManagerCallback)
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    try
    {
      boolean bool = Globals.-get0(sGlobals).setLockWallpaperCallback(paramIWallpaperManagerCallback);
      return bool;
    }
    catch (RemoteException paramIWallpaperManagerCallback)
    {
      throw paramIWallpaperManagerCallback.rethrowFromSystemServer();
    }
  }
  
  public int setResource(int paramInt1, int paramInt2)
    throws IOException
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    Bundle localBundle = new Bundle();
    WallpaperSetCompletion localWallpaperSetCompletion = new WallpaperSetCompletion();
    try
    {
      Resources localResources = this.mContext.getResources();
      Object localObject1 = Globals.-get0(sGlobals).setWallpaper("res:" + localResources.getResourceName(paramInt1), this.mContext.getOpPackageName(), null, false, localBundle, paramInt2, localWallpaperSetCompletion, UserHandle.myUserId());
      if (localObject1 != null) {
        localObject4 = null;
      }
      try
      {
        localObject1 = new ParcelFileDescriptor.AutoCloseOutputStream((ParcelFileDescriptor)localObject1);
        IoUtils.closeQuietly((AutoCloseable)localObject4);
      }
      finally
      {
        try
        {
          copyStreamToWallpaperFile(localResources.openRawResource(paramInt1), (FileOutputStream)localObject1);
          ((FileOutputStream)localObject1).close();
          localWallpaperSetCompletion.waitForCompletion();
          IoUtils.closeQuietly((AutoCloseable)localObject1);
          return localBundle.getInt("android.service.wallpaper.extra.ID", 0);
        }
        finally
        {
          localObject4 = localRemoteException;
          Object localObject3 = localObject5;
        }
        localObject2 = finally;
      }
      throw ((Throwable)localObject2);
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setResource(int paramInt)
    throws IOException
  {
    setResource(paramInt, 3);
  }
  
  public int setStream(InputStream paramInputStream, Rect paramRect, boolean paramBoolean)
    throws IOException
  {
    return setStream(paramInputStream, paramRect, paramBoolean, 3);
  }
  
  public int setStream(InputStream paramInputStream, Rect paramRect, boolean paramBoolean, int paramInt)
    throws IOException
  {
    validateRect(paramRect);
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    Bundle localBundle = new Bundle();
    WallpaperSetCompletion localWallpaperSetCompletion = new WallpaperSetCompletion();
    try
    {
      localObject = Globals.-get0(sGlobals).setWallpaper(null, this.mContext.getOpPackageName(), paramRect, paramBoolean, localBundle, paramInt, localWallpaperSetCompletion, UserHandle.myUserId());
      if (localObject != null) {
        paramRect = null;
      }
      try
      {
        localObject = new ParcelFileDescriptor.AutoCloseOutputStream((ParcelFileDescriptor)localObject);
        IoUtils.closeQuietly(paramRect);
      }
      finally
      {
        try
        {
          copyStreamToWallpaperFile(paramInputStream, (FileOutputStream)localObject);
          ((FileOutputStream)localObject).close();
          localWallpaperSetCompletion.waitForCompletion();
          IoUtils.closeQuietly((AutoCloseable)localObject);
          return localBundle.getInt("android.service.wallpaper.extra.ID", 0);
        }
        finally
        {
          paramRect = (Rect)localObject;
        }
        paramInputStream = finally;
      }
      throw paramInputStream;
    }
    catch (RemoteException paramInputStream)
    {
      throw paramInputStream.rethrowFromSystemServer();
    }
  }
  
  public void setStream(InputStream paramInputStream)
    throws IOException
  {
    setStream(paramInputStream, null, true);
  }
  
  public boolean setWallpaperComponent(ComponentName paramComponentName)
  {
    return setWallpaperComponent(paramComponentName, UserHandle.myUserId());
  }
  
  public boolean setWallpaperComponent(ComponentName paramComponentName, int paramInt)
  {
    if (Globals.-get0(sGlobals) == null)
    {
      Log.w(TAG, "WallpaperService not running");
      throw new RuntimeException(new DeadSystemException());
    }
    try
    {
      Globals.-get0(sGlobals).setWallpaperComponentChecked(paramComponentName, this.mContext.getOpPackageName(), paramInt);
      return true;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setWallpaperOffsetSteps(float paramFloat1, float paramFloat2)
  {
    this.mWallpaperXStep = paramFloat1;
    this.mWallpaperYStep = paramFloat2;
  }
  
  public void setWallpaperOffsets(IBinder paramIBinder, float paramFloat1, float paramFloat2)
  {
    try
    {
      WindowManagerGlobal.getWindowSession().setWallpaperPosition(paramIBinder, paramFloat1, paramFloat2, this.mWallpaperXStep, this.mWallpaperYStep);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw paramIBinder.rethrowFromSystemServer();
    }
  }
  
  public void suggestDesiredDimensions(int paramInt1, int paramInt2)
  {
    try
    {
      i = SystemProperties.getInt("sys.max_texture_size", 0);
      j = paramInt1;
      k = paramInt2;
      if (i > 0) {
        if (paramInt1 <= i)
        {
          j = paramInt1;
          k = paramInt2;
          if (paramInt2 <= i) {}
        }
        else
        {
          f = paramInt2 / paramInt1;
          if (paramInt1 <= paramInt2) {
            break label115;
          }
          j = i;
          k = (int)(i * f + 0.5D);
        }
      }
      if (Globals.-get0(sGlobals) == null)
      {
        Log.w(TAG, "WallpaperService not running");
        throw new RuntimeException(new DeadSystemException());
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    catch (Exception localException)
    {
      int j;
      int k;
      for (;;)
      {
        float f;
        int i = 0;
        continue;
        label115:
        k = i;
        j = (int)(i / f + 0.5D);
      }
      Globals.-get0(sGlobals).setDimensionHints(j, k, this.mContext.getOpPackageName());
    }
  }
  
  static class FastBitmapDrawable
    extends Drawable
  {
    private final Bitmap mBitmap;
    private int mDrawLeft;
    private int mDrawTop;
    private final int mHeight;
    private final Paint mPaint;
    private final int mWidth;
    
    private FastBitmapDrawable(Bitmap paramBitmap)
    {
      this.mBitmap = paramBitmap;
      this.mWidth = paramBitmap.getWidth();
      this.mHeight = paramBitmap.getHeight();
      setBounds(0, 0, this.mWidth, this.mHeight);
      this.mPaint = new Paint();
      this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }
    
    public void draw(Canvas paramCanvas)
    {
      paramCanvas.drawBitmap(this.mBitmap, this.mDrawLeft, this.mDrawTop, this.mPaint);
    }
    
    public int getIntrinsicHeight()
    {
      return this.mHeight;
    }
    
    public int getIntrinsicWidth()
    {
      return this.mWidth;
    }
    
    public int getMinimumHeight()
    {
      return this.mHeight;
    }
    
    public int getMinimumWidth()
    {
      return this.mWidth;
    }
    
    public int getOpacity()
    {
      return -1;
    }
    
    public void setAlpha(int paramInt)
    {
      throw new UnsupportedOperationException("Not supported with this drawable");
    }
    
    public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mDrawLeft = ((paramInt3 - paramInt1 - this.mWidth) / 2 + paramInt1);
      this.mDrawTop = ((paramInt4 - paramInt2 - this.mHeight) / 2 + paramInt2);
    }
    
    public void setColorFilter(ColorFilter paramColorFilter)
    {
      throw new UnsupportedOperationException("Not supported with this drawable");
    }
    
    public void setDither(boolean paramBoolean)
    {
      throw new UnsupportedOperationException("Not supported with this drawable");
    }
    
    public void setFilterBitmap(boolean paramBoolean)
    {
      throw new UnsupportedOperationException("Not supported with this drawable");
    }
  }
  
  static class Globals
    extends IWallpaperManagerCallback.Stub
  {
    private Bitmap mCachedWallpaper;
    private int mCachedWallpaperUserId;
    private Bitmap mDefaultWallpaper;
    private final IWallpaperManager mService = IWallpaperManager.Stub.asInterface(ServiceManager.getService("wallpaper"));
    
    Globals(Looper paramLooper)
    {
      forgetLoadedWallpaper();
    }
    
    private Bitmap getCurrentWallpaperLocked(int paramInt)
    {
      if (this.mService == null)
      {
        Log.w(WallpaperManager.-get0(), "WallpaperService not running");
        return null;
      }
      try
      {
        Object localObject1 = new Bundle();
        localObject1 = this.mService.getWallpaper(this, 1, (Bundle)localObject1, paramInt);
        if (localObject1 != null) {
          try
          {
            Object localObject2 = new BitmapFactory.Options();
            localObject2 = BitmapFactory.decodeFileDescriptor(((ParcelFileDescriptor)localObject1).getFileDescriptor(), null, (BitmapFactory.Options)localObject2);
            return (Bitmap)localObject2;
          }
          catch (OutOfMemoryError localOutOfMemoryError)
          {
            Log.w(WallpaperManager.-get0(), "Can't decode file", localOutOfMemoryError);
            return null;
          }
          finally
          {
            IoUtils.closeQuietly((AutoCloseable)localObject1);
          }
        }
        return null;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    private Bitmap getDefaultWallpaper(Context paramContext, int paramInt)
    {
      paramContext = WallpaperManager.openDefaultWallpaper(paramContext, paramInt);
      if (paramContext != null) {}
      try
      {
        Bitmap localBitmap = BitmapFactory.decodeStream(paramContext, null, new BitmapFactory.Options());
        return localBitmap;
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        Log.w(WallpaperManager.-get0(), "Can't decode stream", localOutOfMemoryError);
        return null;
      }
      finally
      {
        IoUtils.closeQuietly(paramContext);
      }
    }
    
    void forgetLoadedWallpaper()
    {
      try
      {
        this.mCachedWallpaper = null;
        this.mCachedWallpaperUserId = 0;
        this.mDefaultWallpaper = null;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void onWallpaperChanged()
    {
      forgetLoadedWallpaper();
    }
    
    public Bitmap peekWallpaperBitmap(Context paramContext, boolean paramBoolean, int paramInt)
    {
      return peekWallpaperBitmap(paramContext, paramBoolean, paramInt, paramContext.getUserId());
    }
    
    public Bitmap peekWallpaperBitmap(Context paramContext, boolean paramBoolean, int paramInt1, int paramInt2)
    {
      if (this.mService != null) {
        try
        {
          if (!this.mService.isWallpaperSupported(paramContext.getOpPackageName())) {
            return null;
          }
          if (this.mService.getWaitingForUnLock())
          {
            Log.i(WallpaperManager.-get0(), "Still waiting for unlock, return black wallpaper");
            paramContext = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            paramContext.eraseColor(-16777216);
            return paramContext;
          }
        }
        catch (RemoteException paramContext)
        {
          throw paramContext.rethrowFromSystemServer();
        }
      }
      try
      {
        if ((this.mCachedWallpaper != null) && (this.mCachedWallpaperUserId == paramInt2))
        {
          paramContext = this.mCachedWallpaper;
          return paramContext;
        }
        this.mCachedWallpaper = null;
        this.mCachedWallpaperUserId = 0;
        try
        {
          this.mCachedWallpaper = getCurrentWallpaperLocked(paramInt2);
          this.mCachedWallpaperUserId = paramInt2;
          if (this.mCachedWallpaper != null)
          {
            paramContext = this.mCachedWallpaper;
            return paramContext;
          }
        }
        catch (OutOfMemoryError localOutOfMemoryError)
        {
          for (;;)
          {
            Log.w(WallpaperManager.-get0(), "No memory load current wallpaper", localOutOfMemoryError);
          }
        }
      }
      finally {}
      if (paramBoolean)
      {
        Bitmap localBitmap = this.mDefaultWallpaper;
        Object localObject = localBitmap;
        if (localBitmap == null) {
          localObject = getDefaultWallpaper(paramContext, paramInt1);
        }
        try
        {
          this.mDefaultWallpaper = ((Bitmap)localObject);
          return (Bitmap)localObject;
        }
        finally
        {
          paramContext = finally;
          throw paramContext;
        }
      }
      return null;
    }
  }
  
  private class WallpaperSetCompletion
    extends IWallpaperManagerCallback.Stub
  {
    final CountDownLatch mLatch = new CountDownLatch(1);
    
    public WallpaperSetCompletion() {}
    
    public void onWallpaperChanged()
      throws RemoteException
    {
      this.mLatch.countDown();
    }
    
    public void waitForCompletion()
    {
      try
      {
        this.mLatch.await(30L, TimeUnit.SECONDS);
        return;
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/WallpaperManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */