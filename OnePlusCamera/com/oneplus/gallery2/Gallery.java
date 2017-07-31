package com.oneplus.gallery2;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.Window;
import android.view.WindowManager;
import com.oneplus.base.BaseActivity;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.component.ComponentOwnerObject;
import com.oneplus.gallery.media.MediaType;
import com.oneplus.gallery2.media.Media;
import com.oneplus.gallery2.media.MediaSet;
import com.oneplus.util.ListUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Gallery
  extends ComponentOwnerObject
{
  private static final long DURATION_CHECK_INSTANCES_DELAY = 3000L;
  public static final int FLAG_ALWAYS_SHOW_UI = 2;
  public static final int FLAG_CANCELABLE = 1;
  public static final int FLAG_NO_CONFIRMATION_UI = 4;
  public static final PropertyKey<GalleryActivity> PROP_ACTIVITY = new PropertyKey("Activity", GalleryActivity.class, Gallery.class, 1, null);
  public static final PropertyKey<BaseActivity.State> PROP_ACTIVITY_STATE = new PropertyKey("ActivityState", BaseActivity.State.class, Gallery.class, 1, null);
  public static final PropertyKey<MediaSet> PROP_CURRENT_MEDIA_SET = new PropertyKey("CurrentMediaSet", MediaSet.class, Gallery.class, 1, null);
  public static final PropertyKey<Boolean> PROP_HAS_DIALOG = new PropertyKey("HasDialog", Boolean.class, Gallery.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_DELETING_MEDIA = new PropertyKey("IsDeletingMedia", Boolean.class, Gallery.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_NAVIGATION_BAR_VISIBLE = new PropertyKey("IsNavigationBarVisible", Boolean.class, Gallery.class, Boolean.valueOf(true));
  public static final PropertyKey<Boolean> PROP_IS_SHARING_MEDIA = new PropertyKey("IsSharingMedia", Boolean.class, Gallery.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_STATUS_BAR_VISIBLE = new PropertyKey("IsStatusBarVisible", Boolean.class, Gallery.class, Boolean.valueOf(true));
  public static final PropertyKey<MediaType> PROP_TARGET_MEDIA_TYPE = new PropertyKey("TargetMediaType", MediaType.class, Gallery.class, 1, null);
  private static final Runnable m_CheckInstancesRunnable = new Runnable()
  {
    public void run()
    {
      Gallery.checkInstances(0L);
    }
  };
  private static final Map<String, Gallery> m_Galleries = new HashMap();
  private static final List<WeakReference<Gallery>> m_TrackingInstances = new ArrayList();
  protected final String TAG;
  private GalleryActivity m_Activity;
  private View m_ActivityDecorView;
  private final PropertyChangedCallback<Boolean> m_ActivityMultiWindowModeChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      Gallery.this.checkSystemNavigationBarState(Gallery.this.m_Activity);
    }
  };
  private final PropertyChangedCallback<Boolean> m_ActivityRunningStateCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
        return;
      }
      Gallery.this.checkSystemNavigationBarState(Gallery.this.m_Activity);
      Gallery.this.setSystemUiVisibility();
    }
  };
  private final PropertyChangedCallback<BaseActivity.State> m_ActivityStateCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
    {
      Gallery.this.setReadOnly(Gallery.PROP_ACTIVITY_STATE, (BaseActivity.State)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final List<ActivityHandle> m_AttachedActivityHandles = new ArrayList();
  private final List<Handle> m_GalleryDialogHandles = new ArrayList();
  private boolean m_HasNavigationBar;
  private final String m_Id;
  private final List<NavBarVisibilityHandle> m_NavBarVisibilityHandles = new ArrayList();
  private final LinkedList<StatusBarColorHandle> m_StatusBarColorHandles = new LinkedList();
  private final List<StatusBarVisibilityHandle> m_StatusBarVisibilityHandles = new ArrayList();
  private final View.OnSystemUiVisibilityChangeListener m_SystemUiVisibilityListener = new View.OnSystemUiVisibilityChangeListener()
  {
    public void onSystemUiVisibilityChange(int paramAnonymousInt)
    {
      if (Gallery.this.m_Activity == null) {}
      while (!((Boolean)Gallery.this.m_Activity.get(GalleryActivity.PROP_IS_RUNNING)).booleanValue()) {
        return;
      }
      Gallery.this.onSystemUiVisibilityChanged(paramAnonymousInt);
    }
  };
  
  Gallery()
  {
    super(true);
    trackInstance(this);
    char[] arrayOfChar;
    if (BaseApplication.current().isDependencyThread()) {
      arrayOfChar = new char[4];
    }
    String str;
    do
    {
      int i = arrayOfChar.length;
      for (;;)
      {
        i -= 1;
        if (i < 0) {
          break;
        }
        int j = (int)(Math.random() * 36.0D);
        if (j >= 10)
        {
          arrayOfChar[i] = ((char)(char)(j - 10 + 97));
          continue;
          throw new RuntimeException("Can only create in main thread");
        }
        else
        {
          arrayOfChar[i] = ((char)(char)(j + 48));
        }
      }
      str = new String(arrayOfChar);
    } while (m_Galleries.containsKey(str));
    this.m_Id = str;
    this.TAG = ("Gallery(" + str + ")");
    m_Galleries.put(str, this);
    Log.w(this.TAG, "Create, total instance count : " + m_Galleries.size());
    enablePropertyLogs(PROP_ACTIVITY, 1);
  }
  
  private void attachToActivity(GalleryActivity paramGalleryActivity)
  {
    Log.d(this.TAG, "attachToActivity() - activity : " + paramGalleryActivity);
    if (paramGalleryActivity != null)
    {
      this.m_Activity = paramGalleryActivity;
      this.m_Activity.addCallback(GalleryActivity.PROP_IS_RUNNING, this.m_ActivityRunningStateCallback);
      this.m_Activity.addCallback(GalleryActivity.PROP_IS_MULTI_WINDOW_MODE, this.m_ActivityMultiWindowModeChangedCallback);
      this.m_Activity.addCallback(GalleryActivity.PROP_STATE, this.m_ActivityStateCallback);
      setReadOnly(PROP_ACTIVITY_STATE, (BaseActivity.State)this.m_Activity.get(GalleryActivity.PROP_STATE));
      paramGalleryActivity = paramGalleryActivity.getWindow();
      if (paramGalleryActivity != null)
      {
        this.m_ActivityDecorView = paramGalleryActivity.getDecorView();
        this.m_ActivityDecorView.setOnSystemUiVisibilityChangeListener(this.m_SystemUiVisibilityListener);
        updateStatusBarColor();
      }
    }
    else
    {
      return;
    }
    Log.e(this.TAG, "attachToActivity() - No window");
  }
  
  private static void checkInstances(long paramLong)
  {
    int i = 0;
    Handler localHandler = BaseApplication.current().getHandler();
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
      Log.w("Gallery", "checkInstances() - Alive instances : " + m_TrackingInstances.size());
      return;
    }
    localHandler.postDelayed(m_CheckInstancesRunnable, paramLong);
  }
  
  private void checkSystemNavigationBarState(Activity paramActivity)
  {
    boolean bool2 = false;
    Point localPoint1;
    Point localPoint2;
    if (paramActivity != null)
    {
      if ((paramActivity instanceof BaseActivity)) {
        break label132;
      }
      bool1 = false;
      if (bool1) {
        break label152;
      }
      Display localDisplay = paramActivity.getWindowManager().getDefaultDisplay();
      localPoint1 = new Point();
      localPoint2 = new Point();
      localDisplay.getSize(localPoint1);
      localDisplay.getRealSize(localPoint2);
      paramActivity = new ScreenSize(paramActivity, true);
      if (paramActivity.getWidth() <= paramActivity.getHeight()) {
        break label160;
      }
      if (localPoint2.x > localPoint1.x) {
        break label193;
      }
    }
    label132:
    label152:
    label160:
    label193:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      for (this.m_HasNavigationBar = bool1;; this.m_HasNavigationBar = false)
      {
        Log.v(this.TAG, "checkSystemNavigationBarState() - Has navigation bar : ", Boolean.valueOf(this.m_HasNavigationBar));
        return;
        Log.w(this.TAG, "checkSystemNavigationBarState() - No activity to check");
        return;
        bool1 = ((Boolean)((BaseActivity)paramActivity).get(BaseActivity.PROP_IS_MULTI_WINDOW_MODE)).booleanValue();
        break;
      }
      if (localPoint2.y - localPoint1.y <= paramActivity.getStatusBarSize()) {}
      for (bool1 = false;; bool1 = true)
      {
        this.m_HasNavigationBar = bool1;
        break;
      }
    }
  }
  
  private void detachActivity(ActivityHandle paramActivityHandle)
  {
    Object localObject = null;
    verifyAccess();
    boolean bool = ListUtils.isLastObject(this.m_AttachedActivityHandles, paramActivityHandle);
    if (!this.m_AttachedActivityHandles.remove(paramActivityHandle)) {}
    while (!bool) {
      return;
    }
    GalleryActivity localGalleryActivity = paramActivityHandle.activity;
    detachFromActivity();
    paramActivityHandle = (ActivityHandle)localObject;
    if (!this.m_AttachedActivityHandles.isEmpty()) {
      paramActivityHandle = ((ActivityHandle)this.m_AttachedActivityHandles.get(this.m_AttachedActivityHandles.size() - 1)).activity;
    }
    attachToActivity(paramActivityHandle);
    notifyPropertyChanged(PROP_ACTIVITY, localGalleryActivity, paramActivityHandle);
    onActivityChanged(localGalleryActivity, paramActivityHandle);
    if (paramActivityHandle == null) {
      return;
    }
    checkSystemNavigationBarState(paramActivityHandle);
    paramActivityHandle = (Boolean)get(PROP_IS_STATUS_BAR_VISIBLE);
    if (!((Boolean)get(PROP_IS_NAVIGATION_BAR_VISIBLE)).booleanValue()) {}
    for (bool = false;; bool = true)
    {
      setSystemUiVisibility(paramActivityHandle, Boolean.valueOf(bool));
      return;
      if (!this.m_HasNavigationBar) {
        break;
      }
    }
  }
  
  private void detachFromActivity()
  {
    Log.d(this.TAG, "detachFromActivity() - m_Activity : " + this.m_Activity);
    if (this.m_Activity != null)
    {
      this.m_Activity.removeCallback(GalleryActivity.PROP_IS_RUNNING, this.m_ActivityRunningStateCallback);
      this.m_Activity.removeCallback(GalleryActivity.PROP_IS_MULTI_WINDOW_MODE, this.m_ActivityMultiWindowModeChangedCallback);
      this.m_Activity.removeCallback(GalleryActivity.PROP_STATE, this.m_ActivityStateCallback);
      if (this.m_ActivityDecorView != null) {
        break label90;
      }
    }
    for (;;)
    {
      this.m_Activity = null;
      return;
      return;
      label90:
      this.m_ActivityDecorView.setOnSystemUiVisibilityChangeListener(null);
      this.m_ActivityDecorView = null;
    }
  }
  
  public static Gallery fromId(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return (Gallery)m_Galleries.get(paramString);
  }
  
  private void onSystemUiVisibilityChanged(int paramInt)
  {
    Boolean localBoolean2 = null;
    boolean bool1;
    label18:
    boolean bool2;
    label20:
    Boolean localBoolean1;
    label35:
    Object localObject;
    if ((paramInt & 0x4) != 0)
    {
      bool1 = false;
      if (this.m_HasNavigationBar) {
        break label75;
      }
      bool2 = false;
      paramInt = this.m_StatusBarVisibilityHandles.size() - 1;
      localBoolean1 = null;
      if (paramInt < 0) {
        break label128;
      }
      localObject = (StatusBarVisibilityHandle)this.m_StatusBarVisibilityHandles.get(paramInt);
      if (((StatusBarVisibilityHandle)localObject).isVisible != bool1) {
        break label86;
      }
    }
    for (;;)
    {
      paramInt -= 1;
      break label35;
      bool1 = true;
      break;
      label75:
      if ((paramInt & 0x2) != 0) {
        break label18;
      }
      bool2 = true;
      break label20;
      label86:
      if ((((StatusBarVisibilityHandle)localObject).flags & 0x1) != 0)
      {
        ((StatusBarVisibilityHandle)localObject).drop();
        this.m_StatusBarVisibilityHandles.remove(paramInt);
      }
      else
      {
        localBoolean1 = Boolean.valueOf(((StatusBarVisibilityHandle)localObject).isVisible);
      }
    }
    label128:
    paramInt = this.m_NavBarVisibilityHandles.size() - 1;
    if (paramInt >= 0)
    {
      localObject = (NavBarVisibilityHandle)this.m_NavBarVisibilityHandles.get(paramInt);
      if (((NavBarVisibilityHandle)localObject).isVisible == bool2) {}
      for (;;)
      {
        paramInt -= 1;
        break;
        if ((((NavBarVisibilityHandle)localObject).flags & 0x1) != 0)
        {
          ((NavBarVisibilityHandle)localObject).drop();
          this.m_NavBarVisibilityHandles.remove(paramInt);
        }
        else
        {
          localBoolean2 = Boolean.valueOf(((NavBarVisibilityHandle)localObject).isVisible);
        }
      }
    }
    Log.v(this.TAG, "onSystemUiVisibilityChanged() - Status bar: ", new Object[] { Boolean.valueOf(bool1), ", nav bar:", Boolean.valueOf(bool2), ", show status bar: ", localBoolean1, ", show nav bar: ", localBoolean2 });
    if (localBoolean2 == null)
    {
      setReadOnly(PROP_IS_NAVIGATION_BAR_VISIBLE, Boolean.valueOf(bool2));
      label292:
      if (localBoolean1 != null) {
        break label341;
      }
      label297:
      setReadOnly(PROP_IS_STATUS_BAR_VISIBLE, Boolean.valueOf(bool1));
      label309:
      if (localBoolean1 == null) {
        break label358;
      }
    }
    label341:
    label358:
    while (localBoolean2 != null)
    {
      setSystemUiVisibility(localBoolean1, localBoolean2);
      return;
      if (localBoolean2 == null) {
        break label292;
      }
      if (localBoolean2.booleanValue() == bool2) {
        break;
      }
      break label292;
      if (localBoolean1 == null) {
        break label309;
      }
      if (localBoolean1.booleanValue() == bool1) {
        break label297;
      }
      break label309;
    }
  }
  
  private void restoreNavigationBarVisibility(NavBarVisibilityHandle paramNavBarVisibilityHandle)
  {
    boolean bool = ListUtils.isLastObject(this.m_NavBarVisibilityHandles, paramNavBarVisibilityHandle);
    if (!this.m_NavBarVisibilityHandles.remove(paramNavBarVisibilityHandle)) {}
    while (!bool) {
      return;
    }
    if (!this.m_NavBarVisibilityHandles.isEmpty())
    {
      setSystemUiVisibility(null, Boolean.valueOf(((NavBarVisibilityHandle)this.m_NavBarVisibilityHandles.get(this.m_NavBarVisibilityHandles.size() - 1)).isVisible));
      return;
    }
    setSystemUiVisibility(null, Boolean.valueOf(this.m_HasNavigationBar));
  }
  
  private void restoreStatusBarColor(StatusBarColorHandle paramStatusBarColorHandle)
  {
    int i = 0;
    verifyAccess();
    if (!this.m_StatusBarColorHandles.isEmpty())
    {
      if (this.m_StatusBarColorHandles.getLast() == paramStatusBarColorHandle) {
        break label40;
      }
      if (this.m_StatusBarColorHandles.remove(paramStatusBarColorHandle)) {
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
    updateStatusBarColor();
  }
  
  private void restoreStatusBarVisibility(StatusBarVisibilityHandle paramStatusBarVisibilityHandle)
  {
    boolean bool = ListUtils.isLastObject(this.m_StatusBarVisibilityHandles, paramStatusBarVisibilityHandle);
    if (!this.m_StatusBarVisibilityHandles.remove(paramStatusBarVisibilityHandle)) {}
    while (!bool) {
      return;
    }
    if (!this.m_StatusBarVisibilityHandles.isEmpty())
    {
      setSystemUiVisibility(Boolean.valueOf(((StatusBarVisibilityHandle)this.m_StatusBarVisibilityHandles.get(this.m_StatusBarVisibilityHandles.size() - 1)).isVisible), null);
      return;
    }
    setSystemUiVisibility(Boolean.valueOf(true), null);
  }
  
  private void setSystemUiVisibility()
  {
    boolean bool1;
    if (!this.m_StatusBarVisibilityHandles.isEmpty())
    {
      bool1 = ((StatusBarVisibilityHandle)this.m_StatusBarVisibilityHandles.get(this.m_StatusBarVisibilityHandles.size() - 1)).isVisible;
      if (this.m_NavBarVisibilityHandles.isEmpty()) {
        break label97;
      }
    }
    label97:
    for (boolean bool2 = ((NavBarVisibilityHandle)this.m_NavBarVisibilityHandles.get(this.m_NavBarVisibilityHandles.size() - 1)).isVisible;; bool2 = this.m_HasNavigationBar)
    {
      setSystemUiVisibility(Boolean.valueOf(bool1), Boolean.valueOf(bool2));
      return;
      bool1 = true;
      break;
    }
  }
  
  private boolean setSystemUiVisibility(Boolean paramBoolean1, Boolean paramBoolean2)
  {
    int i;
    if (this.m_ActivityDecorView != null)
    {
      if (!this.m_HasNavigationBar) {
        break label64;
      }
      i = this.m_ActivityDecorView.getSystemUiVisibility();
      if (paramBoolean1 != null) {
        break label72;
      }
      label26:
      if (paramBoolean2 != null) {
        break label94;
      }
      label30:
      this.m_ActivityDecorView.setSystemUiVisibility(i | 0xE00);
      if (paramBoolean1 != null) {
        break label116;
      }
      label46:
      if (paramBoolean2 != null) {
        break label128;
      }
    }
    for (;;)
    {
      return true;
      Log.e(this.TAG, "setSystemUiVisibility() - No window");
      return false;
      label64:
      paramBoolean2 = Boolean.valueOf(false);
      break;
      label72:
      if (!paramBoolean1.booleanValue())
      {
        i |= 0x4;
        break label26;
      }
      i &= 0xFFFFFFFB;
      break label26;
      label94:
      if (!paramBoolean2.booleanValue())
      {
        i |= 0x2;
        break label30;
      }
      i &= 0xFFFFFFFD;
      break label30;
      label116:
      setReadOnly(PROP_IS_STATUS_BAR_VISIBLE, paramBoolean1);
      break label46;
      label128:
      setReadOnly(PROP_IS_NAVIGATION_BAR_VISIBLE, paramBoolean2);
    }
  }
  
  private static void trackInstance(Gallery paramGallery)
  {
    m_TrackingInstances.add(new WeakReference(paramGallery));
    checkInstances(0L);
  }
  
  private void updateStatusBarColor()
  {
    if (this.m_Activity == null) {}
    Window localWindow;
    do
    {
      do
      {
        return;
      } while (this.m_StatusBarColorHandles.isEmpty());
      localWindow = this.m_Activity.getWindow();
    } while (localWindow == null);
    localWindow.setStatusBarColor(((StatusBarColorHandle)this.m_StatusBarColorHandles.getLast()).color);
  }
  
  public Handle attachActivity(GalleryActivity paramGalleryActivity)
  {
    verifyAccess();
    ActivityHandle localActivityHandle;
    if (!((Boolean)get(PROP_IS_RELEASED)).booleanValue())
    {
      if (paramGalleryActivity == null) {
        break label136;
      }
      GalleryActivity localGalleryActivity = this.m_Activity;
      detachFromActivity();
      attachToActivity(paramGalleryActivity);
      localActivityHandle = new ActivityHandle(paramGalleryActivity);
      this.m_AttachedActivityHandles.add(localActivityHandle);
      notifyPropertyChanged(PROP_ACTIVITY, localGalleryActivity, paramGalleryActivity);
      onActivityChanged(localGalleryActivity, paramGalleryActivity);
      checkSystemNavigationBarState(paramGalleryActivity);
      paramGalleryActivity = (Boolean)get(PROP_IS_STATUS_BAR_VISIBLE);
      if (((Boolean)get(PROP_IS_NAVIGATION_BAR_VISIBLE)).booleanValue()) {
        break label148;
      }
    }
    for (boolean bool = false;; bool = true)
    {
      setSystemUiVisibility(paramGalleryActivity, Boolean.valueOf(bool));
      return localActivityHandle;
      Log.e(this.TAG, "attachActivity() - Instance has been released");
      return null;
      label136:
      Log.e(this.TAG, "attachActivity() - No activity");
      return null;
      label148:
      if (!this.m_HasNavigationBar) {
        break;
      }
    }
  }
  
  public abstract boolean attachMedia(Media paramMedia);
  
  public boolean deleteMedia(MediaSet paramMediaSet, Media paramMedia)
  {
    return deleteMedia(paramMediaSet, paramMedia, null);
  }
  
  public boolean deleteMedia(MediaSet paramMediaSet, Media paramMedia, MediaDeletionCallback paramMediaDeletionCallback)
  {
    if (paramMedia != null) {
      return deleteMedia(paramMediaSet, Arrays.asList(new Media[] { paramMedia }), 0, paramMediaDeletionCallback);
    }
    Log.w(this.TAG, "deleteMedia() - No media to delete");
    return false;
  }
  
  public boolean deleteMedia(MediaSet paramMediaSet, Collection<Media> paramCollection)
  {
    return deleteMedia(paramMediaSet, paramCollection, 0, null);
  }
  
  public abstract boolean deleteMedia(MediaSet paramMediaSet, Collection<Media> paramCollection, int paramInt, MediaDeletionCallback paramMediaDeletionCallback);
  
  public boolean deleteMediaSet(Collection<MediaSet> paramCollection)
  {
    return deleteMediaSet(paramCollection, null);
  }
  
  public abstract boolean deleteMediaSet(Collection<MediaSet> paramCollection, MediaSetDeletionCallback paramMediaSetDeletionCallback);
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_ACTIVITY) {
      return (TValue)super.get(paramPropertyKey);
    }
    return this.m_Activity;
  }
  
  public final String getId()
  {
    return this.m_Id;
  }
  
  final Handle notifyShowDialog()
  {
    verifyAccess();
    Handle local6 = new Handle("Gallery Dialog Handle")
    {
      protected void onClose(int paramAnonymousInt)
      {
        Gallery.this.m_GalleryDialogHandles.remove(this);
        if (!Gallery.this.m_GalleryDialogHandles.isEmpty()) {
          return;
        }
        Gallery.this.setReadOnly(Gallery.PROP_HAS_DIALOG, Boolean.valueOf(false));
      }
    };
    this.m_GalleryDialogHandles.add(local6);
    setReadOnly(PROP_HAS_DIALOG, Boolean.valueOf(true));
    return local6;
  }
  
  protected void onActivityChanged(GalleryActivity paramGalleryActivity1, GalleryActivity paramGalleryActivity2) {}
  
  protected void onRelease()
  {
    super.onRelease();
    if (this.m_ActivityDecorView == null) {}
    for (;;)
    {
      this.m_Activity = null;
      this.m_AttachedActivityHandles.clear();
      this.m_GalleryDialogHandles.clear();
      m_Galleries.remove(this.m_Id);
      Log.w(this.TAG, "Release, total instance count : " + m_Galleries.size());
      checkInstances(3000L);
      return;
      this.m_ActivityDecorView.setOnSystemUiVisibilityChangeListener(null);
      this.m_ActivityDecorView = null;
    }
  }
  
  public Handle setNavigationBarVisibility(boolean paramBoolean)
  {
    return setNavigationBarVisibility(paramBoolean, 1);
  }
  
  public Handle setNavigationBarVisibility(boolean paramBoolean, int paramInt)
  {
    verifyAccess();
    int i = this.m_NavBarVisibilityHandles.size() - 1;
    if (i >= 0)
    {
      localNavBarVisibilityHandle = (NavBarVisibilityHandle)this.m_NavBarVisibilityHandles.get(i);
      if (localNavBarVisibilityHandle.isVisible == paramBoolean) {}
      for (;;)
      {
        i -= 1;
        break;
        if ((localNavBarVisibilityHandle.flags & 0x1) != 0)
        {
          localNavBarVisibilityHandle.drop();
          this.m_NavBarVisibilityHandles.remove(i);
        }
      }
    }
    NavBarVisibilityHandle localNavBarVisibilityHandle = new NavBarVisibilityHandle(paramBoolean, paramInt);
    this.m_NavBarVisibilityHandles.add(localNavBarVisibilityHandle);
    setSystemUiVisibility(null, Boolean.valueOf(paramBoolean));
    return localNavBarVisibilityHandle;
  }
  
  public Handle setStatusBarColor(int paramInt)
  {
    verifyAccess();
    StatusBarColorHandle localStatusBarColorHandle = new StatusBarColorHandle(paramInt);
    this.m_StatusBarColorHandles.add(localStatusBarColorHandle);
    updateStatusBarColor();
    return localStatusBarColorHandle;
  }
  
  public Handle setStatusBarVisibility(boolean paramBoolean)
  {
    return setStatusBarVisibility(paramBoolean, 1);
  }
  
  public Handle setStatusBarVisibility(boolean paramBoolean, int paramInt)
  {
    verifyAccess();
    int i = this.m_StatusBarVisibilityHandles.size() - 1;
    if (i >= 0)
    {
      localStatusBarVisibilityHandle = (StatusBarVisibilityHandle)this.m_StatusBarVisibilityHandles.get(i);
      if (localStatusBarVisibilityHandle.isVisible == paramBoolean) {}
      for (;;)
      {
        i -= 1;
        break;
        if ((localStatusBarVisibilityHandle.flags & 0x1) != 0)
        {
          localStatusBarVisibilityHandle.drop();
          this.m_StatusBarVisibilityHandles.remove(i);
        }
      }
    }
    StatusBarVisibilityHandle localStatusBarVisibilityHandle = new StatusBarVisibilityHandle(paramBoolean, paramInt);
    this.m_StatusBarVisibilityHandles.add(localStatusBarVisibilityHandle);
    setSystemUiVisibility(Boolean.valueOf(paramBoolean), null);
    return localStatusBarVisibilityHandle;
  }
  
  public boolean shareMedia(Media paramMedia)
  {
    if (paramMedia != null) {
      return shareMedia(Arrays.asList(new Media[] { paramMedia }), null);
    }
    Log.e(this.TAG, "shareMedia() - No media to share");
    return false;
  }
  
  public boolean shareMedia(Media paramMedia, ShareMediaResultCallback paramShareMediaResultCallback)
  {
    if (paramMedia != null) {
      return shareMedia(Arrays.asList(new Media[] { paramMedia }), paramShareMediaResultCallback);
    }
    Log.e(this.TAG, "shareMedia() - No media to share");
    return false;
  }
  
  public abstract boolean shareMedia(Collection<Media> paramCollection, ShareMediaResultCallback paramShareMediaResultCallback);
  
  public boolean startCamera()
  {
    return startCamera(null);
  }
  
  public boolean startCamera(MediaType paramMediaType)
  {
    verifyAccess();
    Log.v(this.TAG, "startCamera() - Media type : ", paramMediaType);
    Object localObject = this.m_Activity;
    Intent localIntent;
    if (localObject != null)
    {
      localIntent = new Intent();
      if (paramMediaType != null) {
        break label83;
      }
      localIntent.setAction("android.intent.action.MAIN");
      if (!(localObject instanceof Activity)) {
        break label160;
      }
    }
    for (;;)
    {
      localIntent.setComponent(new ComponentName("com.oneplus.camera", "com.oneplus.camera.OPCameraActivity"));
      try
      {
        ((Context)localObject).startActivity(localIntent);
        return true;
      }
      catch (ActivityNotFoundException paramMediaType)
      {
        Log.w(this.TAG, "startCamera() - No OnePlus Camera on this device", paramMediaType);
        if ("android.intent.action.MAIN".equals(localIntent.getAction())) {
          break label209;
        }
        for (;;)
        {
          localIntent.setComponent(null);
          try
          {
            ((Context)localObject).startActivity(localIntent);
            return true;
          }
          catch (ActivityNotFoundException paramMediaType)
          {
            Log.w(this.TAG, "startCamera() - Fail to start camera", paramMediaType);
          }
          localIntent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        }
      }
      localObject = BaseApplication.current();
      break;
      label83:
      switch ($SWITCH_TABLE$com$oneplus$gallery$media$MediaType()[paramMediaType.ordinal()])
      {
      default: 
        Log.e(this.TAG, "startCamera() - Unknown media type : " + paramMediaType);
        return false;
      case 2: 
        localIntent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        break;
      case 3: 
        localIntent.setAction("android.media.action.VIDEO_CAMERA");
        break;
        label160:
        localIntent.addFlags(268435456);
      }
    }
    label209:
    return false;
  }
  
  public String toString()
  {
    return "Gallery(" + this.m_Id + ")";
  }
  
  private final class ActivityHandle
    extends Handle
  {
    public final GalleryActivity activity;
    
    public ActivityHandle(GalleryActivity paramGalleryActivity)
    {
      super();
      this.activity = paramGalleryActivity;
    }
    
    protected void onClose(int paramInt)
    {
      Gallery.this.detachActivity(this);
    }
  }
  
  public static abstract class MediaDeletionCallback
  {
    public void onDeletionCompleted(Media paramMedia, boolean paramBoolean) {}
    
    public void onDeletionProcessCompleted() {}
    
    public void onDeletionProcessStarted() {}
    
    public void onDeletionStarted(Media paramMedia) {}
  }
  
  public static abstract class MediaSetDeletionCallback
  {
    public void onDeletionCompleted(MediaSet paramMediaSet, boolean paramBoolean) {}
    
    public void onDeletionProcessCompleted() {}
    
    public void onDeletionProcessStarted() {}
    
    public void onDeletionStarted(MediaSet paramMediaSet) {}
  }
  
  private final class NavBarVisibilityHandle
    extends Gallery.SystemUiVisibilityHandle
  {
    public NavBarVisibilityHandle(boolean paramBoolean, int paramInt)
    {
      super("NavBarVisibility", paramBoolean, paramInt);
    }
    
    protected void onClose(int paramInt)
    {
      Gallery.this.restoreNavigationBarVisibility(this);
    }
  }
  
  public static abstract interface ShareMediaResultCallback
  {
    public abstract void onShareActivityClicked(String paramString);
    
    public abstract void onShareCompleted(int paramInt);
    
    public abstract void onShareStarted(boolean paramBoolean);
  }
  
  private final class StatusBarColorHandle
    extends Handle
  {
    public final int color;
    
    public StatusBarColorHandle(int paramInt)
    {
      super();
      this.color = paramInt;
    }
    
    protected void onClose(int paramInt)
    {
      Gallery.this.restoreStatusBarColor(this);
    }
  }
  
  private final class StatusBarVisibilityHandle
    extends Gallery.SystemUiVisibilityHandle
  {
    public StatusBarVisibilityHandle(boolean paramBoolean, int paramInt)
    {
      super("StatusBarVisibility", paramBoolean, paramInt);
    }
    
    protected void onClose(int paramInt)
    {
      Gallery.this.restoreStatusBarVisibility(this);
    }
  }
  
  private abstract class SystemUiVisibilityHandle
    extends Handle
  {
    public final int flags;
    public final boolean isVisible;
    
    protected SystemUiVisibilityHandle(String paramString, boolean paramBoolean, int paramInt)
    {
      super();
      this.isVisible = paramBoolean;
      this.flags = paramInt;
    }
    
    public final void drop()
    {
      closeDirectly();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/Gallery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */