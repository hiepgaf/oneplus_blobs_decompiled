package com.oneplus.gallery;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.oneplus.base.BaseActivity.ThemeMode;
import com.oneplus.base.ComponentOwnerActivity;
import com.oneplus.base.Device;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.ThreadMonitor;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class GalleryActivity
  extends ComponentOwnerActivity
{
  private static final long DURATION_CHECK_INSTANCES_DELAY = 3000L;
  public static final String EXTRA_SHARED_GALLERY_ID = "com.oneplus.gallery.GalleryActivity.extra.SHARED_GALLERY_ID";
  private static final int MIN_BRIGHTNESS_VALUE_FOR_GALLERY = 230;
  private static final String[] PERMISSION_REQUEST_LIST;
  public static final PropertyKey<ScreenSize> PROP_SCREEN_SIZE = new PropertyKey("ScreenSize", ScreenSize.class, GalleryActivity.class, 1, null);
  private static final int REQUEST_CODE_COUNT = 64;
  private static final String STATE_KEY_PID;
  private static final String STATE_KEY_PREFIX;
  private static final Runnable m_CheckInstancesRunnable = new Runnable()
  {
    public void run()
    {
      GalleryActivity.checkInstances(0L);
    }
  };
  private static final List<WeakReference<GalleryActivity>> m_TrackingInstances;
  private SparseArray<ActivityResultHandle> m_ActivityResultHandles;
  private Gallery m_Gallery;
  private Handle m_GalleryAttachHandle;
  private boolean m_IsInstanceStateSaved;
  private boolean m_IsSharedGallery;
  private final PropertyChangedCallback<Boolean> m_NavBarVisibilityCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      GalleryActivity.this.onNavigationBarVisibilityChanged(((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
    }
  };
  private ScreenSize m_ScreenSize;
  private final LinkedList<StatusBarStyleHandle> m_StatusBarStyleHandles = new LinkedList();
  private final PropertyChangedCallback<Boolean> m_StatusBarVisibilityCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      GalleryActivity.this.onStatusBarVisibilityChanged(((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
    }
  };
  private final Runnable m_StopThreadMonitorRunnable = new Runnable()
  {
    public void run()
    {
      GalleryActivity.this.m_ThreadMonitorHandle = Handle.close(GalleryActivity.this.m_ThreadMonitorHandle);
    }
  };
  private Handle m_ThreadMonitorHandle;
  
  static
  {
    PERMISSION_REQUEST_LIST = new String[] { "android.permission.READ_EXTERNAL_STORAGE" };
    STATE_KEY_PREFIX = GalleryActivity.class.getName() + ".";
    STATE_KEY_PID = STATE_KEY_PREFIX + "PID";
    m_TrackingInstances = new ArrayList();
  }
  
  private void checkBacklight()
  {
    String str = Device.getSystemProperty("ro.boot.project_name");
    if (TextUtils.isEmpty(str)) {}
    while (!str.contains("15801"))
    {
      Log.v(this.TAG, "checkBacklight() - device not supported");
      return;
    }
    int i = getCurrentBrightness();
    if (i <= 230)
    {
      if (i > 0) {}
    }
    else
    {
      updateBacklightBrightness(i);
      return;
    }
    updateBacklightBrightness(230);
  }
  
  private static void checkInstances(long paramLong)
  {
    int i = 0;
    Handler localHandler = GalleryApplication.current().getHandler();
    localHandler.removeCallbacks(m_CheckInstancesRunnable);
    if (paramLong > 0L) {
      i = 1;
    }
    if (i == 0)
    {
      i = m_TrackingInstances.size() - 1;
      if (i >= 0)
      {
        if (((WeakReference)m_TrackingInstances.get(i)).get() != null) {}
        for (;;)
        {
          i -= 1;
          break;
          m_TrackingInstances.remove(i);
        }
      }
      Log.w("GalleryActivity", "checkInstances() - Alive instances : " + m_TrackingInstances.size());
      return;
    }
    localHandler.postDelayed(m_CheckInstancesRunnable, paramLong);
  }
  
  /* Error */
  private int getCurrentBrightness()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: iconst_m1
    //   4: istore_1
    //   5: iload_1
    //   6: istore_2
    //   7: new 254	java/io/BufferedReader
    //   10: dup
    //   11: new 256	java/io/FileReader
    //   14: dup
    //   15: ldc_w 258
    //   18: invokespecial 259	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   21: invokespecial 262	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   24: astore_3
    //   25: aload_3
    //   26: invokevirtual 265	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   29: astore 5
    //   31: aload 5
    //   33: ifnull +39 -> 72
    //   36: aload_0
    //   37: getfield 182	com/oneplus/gallery/GalleryActivity:TAG	Ljava/lang/String;
    //   40: new 92	java/lang/StringBuilder
    //   43: dup
    //   44: ldc_w 267
    //   47: invokespecial 105	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   50: aload 5
    //   52: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   55: invokevirtual 114	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   58: invokestatic 270	com/oneplus/base/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   61: aload 5
    //   63: invokestatic 276	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   66: istore_2
    //   67: iload_2
    //   68: istore_1
    //   69: goto -44 -> 25
    //   72: aload_3
    //   73: ifnonnull +5 -> 78
    //   76: iload_1
    //   77: ireturn
    //   78: iload_1
    //   79: istore_2
    //   80: aload_3
    //   81: invokevirtual 279	java/io/BufferedReader:close	()V
    //   84: iload_1
    //   85: ireturn
    //   86: astore_3
    //   87: iload_2
    //   88: istore_1
    //   89: aload 4
    //   91: ifnull +42 -> 133
    //   94: aload 4
    //   96: aload_3
    //   97: if_acmpne +42 -> 139
    //   100: aload 4
    //   102: athrow
    //   103: astore_3
    //   104: aload_0
    //   105: getfield 182	com/oneplus/gallery/GalleryActivity:TAG	Ljava/lang/String;
    //   108: ldc_w 281
    //   111: aload_3
    //   112: invokestatic 285	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   115: iload_1
    //   116: ireturn
    //   117: astore 4
    //   119: aload_3
    //   120: ifnonnull +6 -> 126
    //   123: aload 4
    //   125: athrow
    //   126: aload_3
    //   127: invokevirtual 279	java/io/BufferedReader:close	()V
    //   130: goto -7 -> 123
    //   133: aload_3
    //   134: astore 4
    //   136: goto -36 -> 100
    //   139: aload 4
    //   141: aload_3
    //   142: invokevirtual 289	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   145: goto -45 -> 100
    //   148: astore_3
    //   149: goto -60 -> 89
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	152	0	this	GalleryActivity
    //   4	112	1	i	int
    //   6	82	2	j	int
    //   24	57	3	localBufferedReader	java.io.BufferedReader
    //   86	11	3	localObject1	Object
    //   103	39	3	localThrowable	Throwable
    //   148	1	3	localObject2	Object
    //   1	100	4	localObject3	Object
    //   117	7	4	localObject4	Object
    //   134	6	4	localObject5	Object
    //   29	33	5	str	String
    // Exception table:
    //   from	to	target	type
    //   7	25	86	finally
    //   80	84	86	finally
    //   100	103	103	java/lang/Throwable
    //   139	145	103	java/lang/Throwable
    //   25	31	117	finally
    //   36	67	117	finally
    //   123	126	148	finally
    //   126	130	148	finally
  }
  
  private void restoreStatusBarStyle(StatusBarStyleHandle paramStatusBarStyleHandle)
  {
    int i = 0;
    verifyAccess();
    if (!this.m_StatusBarStyleHandles.isEmpty())
    {
      if (this.m_StatusBarStyleHandles.getLast() == paramStatusBarStyleHandle) {
        break label40;
      }
      if (this.m_StatusBarStyleHandles.remove(paramStatusBarStyleHandle)) {
        break label45;
      }
    }
    label40:
    label45:
    while (i == 0)
    {
      return;
      return;
      i = 1;
      break;
    }
    updateStatusBarStyle();
  }
  
  private static void trackInstance(GalleryActivity paramGalleryActivity)
  {
    m_TrackingInstances.add(new WeakReference(paramGalleryActivity));
    checkInstances(0L);
  }
  
  private void updateBacklightBrightness(int paramInt)
  {
    Log.v(this.TAG, "updateBacklightBrightness() - Update brightness : " + paramInt);
    float f = paramInt / 255.0F;
    Window localWindow = getWindow();
    WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
    localLayoutParams.screenBrightness = f;
    localWindow.setAttributes(localLayoutParams);
  }
  
  private void updateScreenSize()
  {
    ScreenSize localScreenSize = this.m_ScreenSize;
    this.m_ScreenSize = new ScreenSize(this, true);
    notifyPropertyChanged(PROP_SCREEN_SIZE, localScreenSize, this.m_ScreenSize);
  }
  
  private void updateStatusBarStyle()
  {
    int i = getWindow().getDecorView().getSystemUiVisibility();
    if (this.m_StatusBarStyleHandles.isEmpty())
    {
      if (((Boolean)get(PROP_IS_BLACK_MODE)).booleanValue()) {
        break label97;
      }
      i |= 0x2000;
    }
    for (;;)
    {
      getWindow().getDecorView().setSystemUiVisibility(i);
      getWindow().setStatusBarColor(0);
      return;
      if (((StatusBarStyleHandle)this.m_StatusBarStyleHandles.getLast()).isLightStyle)
      {
        i |= 0x2000;
      }
      else
      {
        i &= 0xDFFF;
        continue;
        label97:
        i &= 0xDFFF;
      }
    }
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    try
    {
      if (((Boolean)get(PROP_IS_RUNNING)).booleanValue()) {}
      for (;;)
      {
        return super.dispatchTouchEvent(paramMotionEvent);
        paramMotionEvent.setAction(3);
      }
      return false;
    }
    catch (Throwable paramMotionEvent)
    {
      Log.e(this.TAG, "dispatchTouchEvent() - Error when dispatch touch event", paramMotionEvent);
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_IS_BLACK_MODE)
    {
      if (paramPropertyKey != PROP_SCREEN_SIZE)
      {
        if (paramPropertyKey == PROP_THEME_MODE) {
          break label37;
        }
        return (TValue)super.get(paramPropertyKey);
      }
    }
    else {
      return Boolean.valueOf(true);
    }
    return this.m_ScreenSize;
    label37:
    return BaseActivity.ThemeMode.DARK;
  }
  
  public final Gallery getGallery()
  {
    return this.m_Gallery;
  }
  
  protected ActivityLaunchType getLaunchType()
  {
    return ActivityLaunchType.UNKNOWN;
  }
  
  protected void getRequestPermissions(List<String> paramList)
  {
    int i = 0;
    super.getRequestPermissions(paramList);
    String[] arrayOfString = PERMISSION_REQUEST_LIST;
    int j = arrayOfString.length;
    if (i < j)
    {
      String str = arrayOfString[i];
      if (paramList.contains(str)) {}
      for (;;)
      {
        i += 1;
        break;
        Log.v(this.TAG, "getRequestPermissions() - Add request permission: ", str);
        paramList.add(str);
      }
    }
  }
  
  public boolean goBack()
  {
    return false;
  }
  
  public boolean isServiceMode()
  {
    boolean bool = false;
    if (!this.m_IsSharedGallery) {
      bool = true;
    }
    return bool;
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    ActivityResultHandle localActivityResultHandle = (ActivityResultHandle)this.m_ActivityResultHandles.get(paramInt1);
    if (localActivityResultHandle == null)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      return;
    }
    this.m_ActivityResultHandles.delete(paramInt1);
    if (!Handle.isValid(localActivityResultHandle)) {}
    while (localActivityResultHandle.callback == null) {
      return;
    }
    localActivityResultHandle.callback.onActivityResult(localActivityResultHandle, paramInt2, paramIntent);
  }
  
  public void onBackPressed()
  {
    if (goBack()) {
      return;
    }
    super.onBackPressed();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    updateScreenSize();
  }
  
  protected final void onCreate(Bundle paramBundle)
  {
    Object localObject2 = null;
    trackInstance(this);
    Intent localIntent;
    if (paramBundle == null)
    {
      localObject1 = null;
      localIntent = getIntent();
      if (localObject1 == null) {
        break label235;
      }
      label22:
      this.m_Gallery = Gallery.fromId((String)localObject1);
      if (this.m_Gallery == null) {
        break label256;
      }
      Log.w(this.TAG, "onCreate() - Use shared Gallery : " + this.m_Gallery.getId());
      if (localIntent != null) {
        break label284;
      }
      label72:
      this.m_GalleryAttachHandle = this.m_Gallery.attachActivity(this);
      if (!Handle.isValid(this.m_GalleryAttachHandle)) {
        break label329;
      }
      this.m_Gallery.addCallback(Gallery.PROP_IS_NAVIGATION_BAR_VISIBLE, this.m_NavBarVisibilityCallback);
      this.m_Gallery.addCallback(Gallery.PROP_IS_STATUS_BAR_VISIBLE, this.m_StatusBarVisibilityCallback);
      updateScreenSize();
      super.onCreate(paramBundle);
      localObject1 = (InstanceStateFragment)getFragmentManager().findFragmentByTag("GalleryActivity.InstanceState");
      if (localObject1 != null) {
        break label344;
      }
      label149:
      Log.w(this.TAG, "onCreate() - new sparse array");
      this.m_ActivityResultHandles = new SparseArray();
      label170:
      if (localObject1 != null) {
        break label372;
      }
    }
    label235:
    label256:
    label284:
    label329:
    label344:
    label372:
    for (Object localObject1 = localObject2;; localObject1 = ((InstanceStateFragment)localObject1).extras)
    {
      onCreate(paramBundle, (Map)localObject1);
      return;
      if (paramBundle.getInt(STATE_KEY_PID) == Process.myPid())
      {
        localObject1 = paramBundle.getString("com.oneplus.gallery.GalleryActivity.extra.SHARED_GALLERY_ID", null);
        Log.v(this.TAG, "onCreate() - activity is recreate, sharedGalleryId: ", localObject1);
        break;
      }
      Log.w(this.TAG, "onCreate() - Different Pid, clear saved instance state");
      localObject1 = null;
      paramBundle = null;
      break;
      if (localIntent == null)
      {
        localObject1 = null;
        break label22;
      }
      localObject1 = localIntent.getStringExtra("com.oneplus.gallery.GalleryActivity.extra.SHARED_GALLERY_ID");
      break label22;
      Log.w(this.TAG, "onCreate() - Create new Gallery");
      this.m_Gallery = GalleryApplication.current().createGallery();
      this.m_IsSharedGallery = false;
      break label72;
      if (localIntent.getStringExtra("com.oneplus.gallery.GalleryActivity.extra.SHARED_GALLERY_ID") == null) {
        break label72;
      }
      this.m_IsSharedGallery = true;
      Log.w(this.TAG, "onCreate() - m_IsSharedGallery:" + this.m_IsSharedGallery);
      break label72;
      Log.e(this.TAG, "onCreate() - Fail to attach to Gallery");
      finish();
      return;
      if (((InstanceStateFragment)localObject1).activityResultHandles == null) {
        break label149;
      }
      Log.w(this.TAG, "onCreate() - Use existent stateFragment activityResultHandles.");
      this.m_ActivityResultHandles = ((InstanceStateFragment)localObject1).activityResultHandles;
      break label170;
    }
  }
  
  protected void onCreate(Bundle paramBundle, Map<String, Object> paramMap)
  {
    if (this.m_IsSharedGallery) {
      return;
    }
    GalleryApplication.current().notifyActivityLaunched(this, getLaunchType());
  }
  
  protected void onDestroy()
  {
    this.m_Gallery.removeCallback(Gallery.PROP_IS_NAVIGATION_BAR_VISIBLE, this.m_NavBarVisibilityCallback);
    this.m_Gallery.removeCallback(Gallery.PROP_IS_STATUS_BAR_VISIBLE, this.m_StatusBarVisibilityCallback);
    this.m_GalleryAttachHandle = Handle.close(this.m_GalleryAttachHandle);
    if (this.m_IsInstanceStateSaved) {}
    while (this.m_IsInstanceStateSaved)
    {
      this.m_ActivityResultHandles = null;
      super.onDestroy();
      checkInstances(3000L);
      return;
      if (!this.m_IsSharedGallery)
      {
        Log.w(this.TAG, "onDestroy() - Release Gallery");
        this.m_Gallery.release();
      }
    }
    int i = 64;
    label99:
    ActivityResultHandle localActivityResultHandle;
    if (i > 0)
    {
      localActivityResultHandle = (ActivityResultHandle)this.m_ActivityResultHandles.get(i);
      if (localActivityResultHandle != null) {
        break label126;
      }
    }
    for (;;)
    {
      i -= 1;
      break label99;
      break;
      label126:
      this.m_ActivityResultHandles.delete(i);
      if ((Handle.isValid(localActivityResultHandle)) && (localActivityResultHandle.callback != null)) {
        localActivityResultHandle.callback.onActivityResult(localActivityResultHandle, 0, null);
      }
    }
  }
  
  protected boolean onInitialPermissionsRequestCompleted(String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramArrayOfString == null) {}
    while (paramArrayOfInt == null) {
      return false;
    }
    GalleryApplication localGalleryApplication = GalleryApplication.current();
    int i = paramArrayOfString.length - 1;
    int j = 1;
    if (i >= 0)
    {
      Log.v(this.TAG, "onInitialPermissionsRequestCompleted() - Permission : ", paramArrayOfString[i], ", result : ", Integer.valueOf(paramArrayOfInt[i]));
      if (paramArrayOfInt[i] != -1) {
        localGalleryApplication.notifyPermissionGranted(paramArrayOfString[i]);
      }
      for (;;)
      {
        i -= 1;
        break;
        localGalleryApplication.notifyPermissionDenied(paramArrayOfString[i]);
        j = 0;
      }
    }
    if (j != 0) {
      return true;
    }
    Log.w(this.TAG, "onInitialPermissionsRequestCompleted() - Some permissions are not granted");
    finish();
    return false;
  }
  
  protected void onNavigationBarVisibilityChanged(boolean paramBoolean) {}
  
  protected final void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    Object localObject = (InstanceStateFragment)getFragmentManager().findFragmentByTag("GalleryActivity.InstanceState");
    if (localObject == null) {}
    for (localObject = null;; localObject = ((InstanceStateFragment)localObject).extras)
    {
      onRestoreInstanceState(paramBundle, (Map)localObject);
      return;
    }
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle, Map<String, Object> paramMap) {}
  
  protected void onResume()
  {
    super.onResume();
    this.m_IsInstanceStateSaved = false;
    updateStatusBarStyle();
  }
  
  protected final void onSaveInstanceState(Bundle paramBundle)
  {
    InstanceStateFragment localInstanceStateFragment = (InstanceStateFragment)getFragmentManager().findFragmentByTag("GalleryActivity.InstanceState");
    if (localInstanceStateFragment != null)
    {
      paramBundle.putInt(STATE_KEY_PID, Process.myPid());
      if (this.m_Gallery != null) {
        break label92;
      }
    }
    for (;;)
    {
      localInstanceStateFragment.activityResultHandles = this.m_ActivityResultHandles;
      onSaveInstanceState(paramBundle, localInstanceStateFragment.extras);
      super.onSaveInstanceState(paramBundle);
      this.m_IsInstanceStateSaved = true;
      return;
      localInstanceStateFragment = new InstanceStateFragment();
      getFragmentManager().beginTransaction().add(localInstanceStateFragment, "GalleryActivity.InstanceState").commit();
      break;
      label92:
      paramBundle.putString("com.oneplus.gallery.GalleryActivity.extra.SHARED_GALLERY_ID", this.m_Gallery.getId());
    }
  }
  
  protected void onSaveInstanceState(Bundle paramBundle, Map<String, Object> paramMap) {}
  
  protected void onStart()
  {
    super.onStart();
    if (Handle.isValid(this.m_ThreadMonitorHandle))
    {
      GalleryApplication.current().getHandler().removeCallbacks(this.m_StopThreadMonitorRunnable);
      return;
    }
    this.m_ThreadMonitorHandle = ThreadMonitor.startMonitorCurrentThread();
  }
  
  protected void onStatusBarVisibilityChanged(boolean paramBoolean) {}
  
  protected void onStop()
  {
    GalleryApplication.current().getHandler().postDelayed(this.m_StopThreadMonitorRunnable, 3000L);
    super.onStop();
  }
  
  public Handle setStatusBarStyle(boolean paramBoolean)
  {
    verifyAccess();
    StatusBarStyleHandle localStatusBarStyleHandle = new StatusBarStyleHandle(paramBoolean);
    this.m_StatusBarStyleHandles.add(localStatusBarStyleHandle);
    updateStatusBarStyle();
    return localStatusBarStyleHandle;
  }
  
  protected void setSystemUiVisibility(boolean paramBoolean)
  {
    Gallery localGallery = getGallery();
    if (localGallery != null)
    {
      Log.v(this.TAG, "setSystemUiVisibility() - Visible: ", Boolean.valueOf(paramBoolean));
      if (!paramBoolean)
      {
        localGallery.setNavigationBarVisibility(false);
        localGallery.setStatusBarVisibility(false);
      }
    }
    else
    {
      Log.e(this.TAG, "setSystemUiVisibility() - No gallery");
      return;
    }
    localGallery.setNavigationBarVisibility(true);
    localGallery.setStatusBarVisibility(true);
  }
  
  public Handle startActivityForResult(Intent paramIntent, ActivityResultCallback paramActivityResultCallback)
  {
    int i;
    if (paramIntent != null)
    {
      verifyAccess();
      i = 64;
      while ((i > 0) && (this.m_ActivityResultHandles.get(i) != null)) {
        i -= 1;
      }
    }
    Log.e(this.TAG, "startActivityForResult() - No intent");
    return null;
    if (i > 0)
    {
      paramActivityResultCallback = new ActivityResultHandle(paramActivityResultCallback);
      this.m_ActivityResultHandles.put(i, paramActivityResultCallback);
    }
    try
    {
      startActivityForResult(paramIntent, i);
      return paramActivityResultCallback;
    }
    catch (Throwable paramIntent)
    {
      Log.e(this.TAG, "startActivityForResult() - Fail to start activity", paramIntent);
      this.m_ActivityResultHandles.delete(i);
    }
    Log.e(this.TAG, "startActivityForResult() - No available request code");
    return null;
    return null;
  }
  
  public static abstract interface ActivityResultCallback
  {
    public abstract void onActivityResult(Handle paramHandle, int paramInt, Intent paramIntent);
  }
  
  private static final class ActivityResultHandle
    extends Handle
  {
    public final GalleryActivity.ActivityResultCallback callback;
    
    public ActivityResultHandle(GalleryActivity.ActivityResultCallback paramActivityResultCallback)
    {
      super();
      this.callback = paramActivityResultCallback;
    }
    
    protected void onClose(int paramInt) {}
  }
  
  public static final class InstanceStateFragment
    extends Fragment
  {
    static final String TAG = "GalleryActivity.InstanceState";
    public SparseArray<GalleryActivity.ActivityResultHandle> activityResultHandles;
    public final Map<String, Object> extras = new HashMap();
    
    public InstanceStateFragment()
    {
      setRetainInstance(true);
    }
  }
  
  private final class StatusBarStyleHandle
    extends Handle
  {
    public final boolean isLightStyle;
    
    public StatusBarStyleHandle(boolean paramBoolean)
    {
      super();
      this.isLightStyle = paramBoolean;
    }
    
    protected void onClose(int paramInt)
    {
      GalleryActivity.this.restoreStatusBarStyle(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/GalleryActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */