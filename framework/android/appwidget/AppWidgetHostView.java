package android.appwidget;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RemoteViews;
import android.widget.RemoteViews.OnClickHandler;
import android.widget.RemoteViews.OnViewAppliedListener;
import android.widget.RemoteViews.RemoteView;
import android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback;
import android.widget.TextView;
import java.util.concurrent.Executor;

public class AppWidgetHostView
  extends FrameLayout
{
  static final boolean CROSSFADE = false;
  static final int FADE_DURATION = 1000;
  static final boolean LOGD = false;
  static final String TAG = "AppWidgetHostView";
  static final int VIEW_MODE_CONTENT = 1;
  static final int VIEW_MODE_DEFAULT = 3;
  static final int VIEW_MODE_ERROR = 2;
  static final int VIEW_MODE_NOINIT = 0;
  static final LayoutInflater.Filter sInflaterFilter = new LayoutInflater.Filter()
  {
    public boolean onLoadClass(Class paramAnonymousClass)
    {
      return paramAnonymousClass.isAnnotationPresent(RemoteViews.RemoteView.class);
    }
  };
  int mAppWidgetId;
  private Executor mAsyncExecutor;
  Context mContext;
  long mFadeStartTime = -1L;
  AppWidgetProviderInfo mInfo;
  private CancellationSignal mLastExecutionSignal;
  int mLayoutId = -1;
  Bitmap mOld;
  Paint mOldPaint = new Paint();
  private RemoteViews.OnClickHandler mOnClickHandler;
  Context mRemoteContext;
  View mView;
  int mViewMode = 0;
  
  public AppWidgetHostView(Context paramContext)
  {
    this(paramContext, 17432576, 17432577);
  }
  
  public AppWidgetHostView(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext);
    this.mContext = paramContext;
    setIsRootNamespace(true);
  }
  
  public AppWidgetHostView(Context paramContext, RemoteViews.OnClickHandler paramOnClickHandler)
  {
    this(paramContext, 17432576, 17432577);
    this.mOnClickHandler = paramOnClickHandler;
  }
  
  private void applyContent(View paramView, boolean paramBoolean, Exception paramException)
  {
    View localView = paramView;
    if (paramView == null)
    {
      if (this.mViewMode == 2) {
        return;
      }
      Log.w("AppWidgetHostView", "updateAppWidget couldn't find any view, using error view", paramException);
      localView = getErrorView();
      this.mViewMode = 2;
    }
    if (!paramBoolean)
    {
      prepareView(localView);
      addView(localView);
    }
    if (this.mView != localView)
    {
      removeView(this.mView);
      this.mView = localView;
    }
  }
  
  private int generateId()
  {
    int j = getId();
    int i = j;
    if (j == -1) {
      i = this.mAppWidgetId;
    }
    return i;
  }
  
  public static Rect getDefaultPaddingForWidget(Context paramContext, ComponentName paramComponentName, Rect paramRect)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    if (paramRect == null) {
      paramRect = new Rect(0, 0, 0, 0);
    }
    for (;;)
    {
      try
      {
        paramComponentName = localPackageManager.getApplicationInfo(paramComponentName.getPackageName(), 0);
        if (paramComponentName.targetSdkVersion >= 14)
        {
          paramContext = paramContext.getResources();
          paramRect.left = paramContext.getDimensionPixelSize(17105002);
          paramRect.right = paramContext.getDimensionPixelSize(17105004);
          paramRect.top = paramContext.getDimensionPixelSize(17105003);
          paramRect.bottom = paramContext.getDimensionPixelSize(17105005);
        }
        return paramRect;
      }
      catch (PackageManager.NameNotFoundException paramContext) {}
      paramRect.set(0, 0, 0, 0);
    }
    return paramRect;
  }
  
  private void inflateAsync(RemoteViews paramRemoteViews)
  {
    this.mRemoteContext = getRemoteContext();
    int i = paramRemoteViews.getLayoutId();
    if ((i == this.mLayoutId) && (this.mView != null)) {}
    try
    {
      this.mLastExecutionSignal = paramRemoteViews.reapplyAsync(this.mContext, this.mView, this.mAsyncExecutor, new ViewApplyListener(paramRemoteViews, i, true), this.mOnClickHandler);
      if (this.mLastExecutionSignal == null) {
        this.mLastExecutionSignal = paramRemoteViews.applyAsync(this.mContext, this, this.mAsyncExecutor, new ViewApplyListener(paramRemoteViews, i, false), this.mOnClickHandler);
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  private void updateContentDescription(AppWidgetProviderInfo paramAppWidgetProviderInfo)
  {
    if (paramAppWidgetProviderInfo != null)
    {
      ApplicationInfo localApplicationInfo = ((LauncherApps)getContext().getSystemService(LauncherApps.class)).getApplicationInfo(paramAppWidgetProviderInfo.provider.getPackageName(), 0, paramAppWidgetProviderInfo.getProfile());
      if ((localApplicationInfo != null) && ((localApplicationInfo.flags & 0x40000000) != 0)) {
        setContentDescription(Resources.getSystem().getString(17040918, new Object[] { paramAppWidgetProviderInfo.label }));
      }
    }
    else
    {
      return;
    }
    setContentDescription(paramAppWidgetProviderInfo.label);
  }
  
  protected void applyRemoteViews(RemoteViews paramRemoteViews)
  {
    bool2 = false;
    bool1 = false;
    localObject3 = null;
    localObject4 = null;
    localObject1 = null;
    if (this.mLastExecutionSignal != null)
    {
      this.mLastExecutionSignal.cancel();
      this.mLastExecutionSignal = null;
    }
    if (paramRemoteViews == null)
    {
      if (this.mViewMode == 3) {
        return;
      }
      localObject4 = getDefaultView();
      this.mLayoutId = -1;
      this.mViewMode = 3;
      localObject3 = localObject1;
    }
    for (;;)
    {
      applyContent((View)localObject4, bool1, (Exception)localObject3);
      updateContentDescription(this.mInfo);
      return;
      if (this.mAsyncExecutor != null)
      {
        inflateAsync(paramRemoteViews);
        return;
      }
      this.mRemoteContext = getRemoteContext();
      int i = paramRemoteViews.getLayoutId();
      localObject1 = localObject3;
      Object localObject2 = localObject4;
      bool1 = bool2;
      if (i == this.mLayoutId) {}
      try
      {
        paramRemoteViews.reapply(this.mContext, this.mView, this.mOnClickHandler);
        localObject1 = this.mView;
        bool1 = true;
        localObject2 = localObject4;
      }
      catch (RuntimeException localRuntimeException1)
      {
        for (;;)
        {
          localObject1 = localObject3;
          bool1 = bool2;
        }
      }
      localObject4 = localObject1;
      localObject3 = localObject2;
      if (localObject1 == null) {}
      try
      {
        localObject4 = paramRemoteViews.apply(this.mContext, this, this.mOnClickHandler);
        localObject3 = localObject2;
      }
      catch (RuntimeException localRuntimeException2)
      {
        for (;;)
        {
          localObject4 = localObject1;
        }
      }
      this.mLayoutId = i;
      this.mViewMode = 1;
    }
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    Object localObject2 = (Parcelable)paramSparseArray.get(generateId());
    Object localObject1 = null;
    paramSparseArray = (SparseArray<Parcelable>)localObject1;
    if (localObject2 != null)
    {
      paramSparseArray = (SparseArray<Parcelable>)localObject1;
      if ((localObject2 instanceof ParcelableSparseArray)) {
        paramSparseArray = (ParcelableSparseArray)localObject2;
      }
    }
    localObject1 = paramSparseArray;
    if (paramSparseArray == null) {
      localObject1 = new ParcelableSparseArray(null);
    }
    try
    {
      super.dispatchRestoreInstanceState((SparseArray)localObject1);
      return;
    }
    catch (Exception localException)
    {
      localObject2 = new StringBuilder().append("failed to restoreInstanceState for widget id: ").append(this.mAppWidgetId).append(", ");
      if (this.mInfo != null) {}
    }
    for (paramSparseArray = "null";; paramSparseArray = this.mInfo.provider)
    {
      Log.e("AppWidgetHostView", paramSparseArray, localException);
      return;
    }
  }
  
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    ParcelableSparseArray localParcelableSparseArray = new ParcelableSparseArray(null);
    super.dispatchSaveInstanceState(localParcelableSparseArray);
    paramSparseArray.put(generateId(), localParcelableSparseArray);
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    return super.drawChild(paramCanvas, paramView, paramLong);
  }
  
  public FrameLayout.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    if (this.mRemoteContext != null) {}
    for (Context localContext = this.mRemoteContext;; localContext = this.mContext) {
      return new FrameLayout.LayoutParams(localContext, paramAttributeSet);
    }
  }
  
  public int getAppWidgetId()
  {
    return this.mAppWidgetId;
  }
  
  public AppWidgetProviderInfo getAppWidgetInfo()
  {
    return this.mInfo;
  }
  
  protected View getDefaultView()
  {
    Object localObject1 = null;
    Object localObject2 = null;
    for (;;)
    {
      try
      {
        if (this.mInfo == null) {
          continue;
        }
        Object localObject3 = getRemoteContext();
        this.mRemoteContext = ((Context)localObject3);
        localObject3 = ((LayoutInflater)((Context)localObject3).getSystemService("layout_inflater")).cloneInContext((Context)localObject3);
        ((LayoutInflater)localObject3).setFilter(sInflaterFilter);
        Bundle localBundle = AppWidgetManager.getInstance(this.mContext).getAppWidgetOptions(this.mAppWidgetId);
        int j = this.mInfo.initialLayout;
        int i = j;
        if (localBundle.containsKey("appWidgetCategory"))
        {
          i = j;
          if (localBundle.getInt("appWidgetCategory") == 2)
          {
            i = this.mInfo.initialKeyguardLayout;
            if (i != 0) {
              continue;
            }
            i = j;
          }
        }
        localObject3 = ((LayoutInflater)localObject3).inflate(i, this, false);
        localObject1 = localObject3;
      }
      catch (RuntimeException localRuntimeException)
      {
        continue;
      }
      if (localObject2 != null) {
        Log.w("AppWidgetHostView", "Error inflating AppWidget " + this.mInfo + ": " + ((Exception)localObject2).toString());
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = getErrorView();
      }
      return (View)localObject2;
      continue;
      Log.w("AppWidgetHostView", "can't inflate defaultView because mInfo is missing");
    }
  }
  
  protected View getErrorView()
  {
    TextView localTextView = new TextView(this.mContext);
    localTextView.setText(17040493);
    localTextView.setBackgroundColor(Color.argb(127, 0, 0, 0));
    return localTextView;
  }
  
  protected Context getRemoteContext()
  {
    try
    {
      Context localContext = this.mContext.createApplicationContext(this.mInfo.providerInfo.applicationInfo, 4);
      return localContext;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e("AppWidgetHostView", "Package name " + this.mInfo.providerInfo.packageName + " not found");
    }
    return this.mContext;
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(AppWidgetHostView.class.getName());
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      Log.e("AppWidgetHostView", "Remote provider threw runtime exception, using error view instead.", localRuntimeException);
      removeViewInLayout(this.mView);
      View localView = getErrorView();
      prepareView(localView);
      addViewInLayout(localView, 0, localView.getLayoutParams());
      measureChild(localView, View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      localView.layout(0, 0, localView.getMeasuredWidth() + this.mPaddingLeft + this.mPaddingRight, localView.getMeasuredHeight() + this.mPaddingTop + this.mPaddingBottom);
      this.mView = localView;
      this.mViewMode = 2;
    }
  }
  
  protected void prepareView(View paramView)
  {
    FrameLayout.LayoutParams localLayoutParams2 = (FrameLayout.LayoutParams)paramView.getLayoutParams();
    FrameLayout.LayoutParams localLayoutParams1 = localLayoutParams2;
    if (localLayoutParams2 == null) {
      localLayoutParams1 = new FrameLayout.LayoutParams(-1, -1);
    }
    localLayoutParams1.gravity = 17;
    paramView.setLayoutParams(localLayoutParams1);
  }
  
  void resetAppWidget(AppWidgetProviderInfo paramAppWidgetProviderInfo)
  {
    this.mInfo = paramAppWidgetProviderInfo;
    this.mViewMode = 0;
    updateAppWidget(null);
  }
  
  public void setAppWidget(int paramInt, AppWidgetProviderInfo paramAppWidgetProviderInfo)
  {
    this.mAppWidgetId = paramInt;
    this.mInfo = paramAppWidgetProviderInfo;
    if (paramAppWidgetProviderInfo != null)
    {
      Rect localRect = getDefaultPaddingForWidget(this.mContext, paramAppWidgetProviderInfo.provider, null);
      setPadding(localRect.left, localRect.top, localRect.right, localRect.bottom);
      updateContentDescription(paramAppWidgetProviderInfo);
    }
  }
  
  public void setAsyncExecutor(Executor paramExecutor)
  {
    if (this.mLastExecutionSignal != null)
    {
      this.mLastExecutionSignal.cancel();
      this.mLastExecutionSignal = null;
    }
    this.mAsyncExecutor = paramExecutor;
  }
  
  public void setOnClickHandler(RemoteViews.OnClickHandler paramOnClickHandler)
  {
    this.mOnClickHandler = paramOnClickHandler;
  }
  
  public void updateAppWidget(RemoteViews paramRemoteViews)
  {
    applyRemoteViews(paramRemoteViews);
  }
  
  public void updateAppWidgetOptions(Bundle paramBundle)
  {
    AppWidgetManager.getInstance(this.mContext).updateAppWidgetOptions(this.mAppWidgetId, paramBundle);
  }
  
  public void updateAppWidgetSize(Bundle paramBundle, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    updateAppWidgetSize(paramBundle, paramInt1, paramInt2, paramInt3, paramInt4, false);
  }
  
  public void updateAppWidgetSize(Bundle paramBundle, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    Rect localRect = new Rect();
    paramBundle = localRect;
    if (this.mInfo != null) {
      paramBundle = getDefaultPaddingForWidget(this.mContext, this.mInfo.provider, localRect);
    }
    float f = getResources().getDisplayMetrics().density;
    int j = (int)((paramBundle.left + paramBundle.right) / f);
    int i = (int)((paramBundle.top + paramBundle.bottom) / f);
    int k;
    if (paramBoolean)
    {
      k = 0;
      k = paramInt1 - k;
      if (!paramBoolean) {
        break label250;
      }
      paramInt1 = 0;
      label117:
      paramInt2 -= paramInt1;
      if (paramBoolean) {
        j = 0;
      }
      paramInt3 -= j;
      if (paramBoolean) {
        i = 0;
      }
      paramInt4 -= i;
      paramBundle = AppWidgetManager.getInstance(this.mContext).getAppWidgetOptions(this.mAppWidgetId);
      paramInt1 = 0;
      if ((k == paramBundle.getInt("appWidgetMinWidth")) && (paramInt2 == paramBundle.getInt("appWidgetMinHeight"))) {
        break label256;
      }
    }
    for (;;)
    {
      label191:
      paramInt1 = 1;
      label250:
      label256:
      do
      {
        if (paramInt1 != 0)
        {
          localBundle.putInt("appWidgetMinWidth", k);
          localBundle.putInt("appWidgetMinHeight", paramInt2);
          localBundle.putInt("appWidgetMaxWidth", paramInt3);
          localBundle.putInt("appWidgetMaxHeight", paramInt4);
          updateAppWidgetOptions(localBundle);
        }
        return;
        k = j;
        break;
        paramInt1 = i;
        break label117;
        if (paramInt3 != paramBundle.getInt("appWidgetMaxWidth")) {
          break label191;
        }
      } while (paramInt4 == paramBundle.getInt("appWidgetMaxHeight"));
    }
  }
  
  void viewDataChanged(int paramInt)
  {
    Object localObject = findViewById(paramInt);
    Adapter localAdapter;
    if ((localObject != null) && ((localObject instanceof AdapterView)))
    {
      localObject = (AdapterView)localObject;
      localAdapter = ((AdapterView)localObject).getAdapter();
      if (!(localAdapter instanceof BaseAdapter)) {
        break label42;
      }
      ((BaseAdapter)localAdapter).notifyDataSetChanged();
    }
    label42:
    while ((localAdapter != null) || (!(localObject instanceof RemoteViewsAdapter.RemoteAdapterConnectionCallback))) {
      return;
    }
    ((RemoteViewsAdapter.RemoteAdapterConnectionCallback)localObject).deferNotifyDataSetChanged();
  }
  
  private static class ParcelableSparseArray
    extends SparseArray<Parcelable>
    implements Parcelable
  {
    public static final Parcelable.Creator<ParcelableSparseArray> CREATOR = new Parcelable.Creator()
    {
      public AppWidgetHostView.ParcelableSparseArray createFromParcel(Parcel paramAnonymousParcel)
      {
        AppWidgetHostView.ParcelableSparseArray localParcelableSparseArray = new AppWidgetHostView.ParcelableSparseArray(null);
        ClassLoader localClassLoader = localParcelableSparseArray.getClass().getClassLoader();
        int j = paramAnonymousParcel.readInt();
        int i = 0;
        while (i < j)
        {
          localParcelableSparseArray.put(paramAnonymousParcel.readInt(), paramAnonymousParcel.readParcelable(localClassLoader));
          i += 1;
        }
        return localParcelableSparseArray;
      }
      
      public AppWidgetHostView.ParcelableSparseArray[] newArray(int paramAnonymousInt)
      {
        return new AppWidgetHostView.ParcelableSparseArray[paramAnonymousInt];
      }
    };
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = size();
      paramParcel.writeInt(i);
      paramInt = 0;
      while (paramInt < i)
      {
        paramParcel.writeInt(keyAt(paramInt));
        paramParcel.writeParcelable((Parcelable)valueAt(paramInt), 0);
        paramInt += 1;
      }
    }
  }
  
  private class ViewApplyListener
    implements RemoteViews.OnViewAppliedListener
  {
    private final boolean mIsReapply;
    private final int mLayoutId;
    private final RemoteViews mViews;
    
    public ViewApplyListener(RemoteViews paramRemoteViews, int paramInt, boolean paramBoolean)
    {
      this.mViews = paramRemoteViews;
      this.mLayoutId = paramInt;
      this.mIsReapply = paramBoolean;
    }
    
    public void onError(Exception paramException)
    {
      if (this.mIsReapply)
      {
        AppWidgetHostView.-set0(AppWidgetHostView.this, this.mViews.applyAsync(AppWidgetHostView.this.mContext, AppWidgetHostView.this, AppWidgetHostView.-get0(AppWidgetHostView.this), new ViewApplyListener(AppWidgetHostView.this, this.mViews, this.mLayoutId, false), AppWidgetHostView.-get1(AppWidgetHostView.this)));
        return;
      }
      AppWidgetHostView.-wrap0(AppWidgetHostView.this, null, false, paramException);
    }
    
    public void onViewApplied(View paramView)
    {
      AppWidgetHostView.this.mLayoutId = this.mLayoutId;
      AppWidgetHostView.this.mViewMode = 1;
      AppWidgetHostView.-wrap0(AppWidgetHostView.this, paramView, this.mIsReapply, null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/appwidget/AppWidgetHostView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */