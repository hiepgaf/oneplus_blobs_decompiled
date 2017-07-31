package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.InsetDrawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

@Deprecated
public class ActionBarDrawerToggle
  implements DrawerLayout.DrawerListener
{
  private static final int ID_HOME = 16908332;
  private static final ActionBarDrawerToggleImpl IMPL = new ActionBarDrawerToggleImplHC(null);
  private static final float TOGGLE_DRAWABLE_OFFSET = 0.33333334F;
  private final Activity mActivity;
  private final Delegate mActivityImpl;
  private final int mCloseDrawerContentDescRes;
  private Drawable mDrawerImage;
  private final int mDrawerImageResource;
  private boolean mDrawerIndicatorEnabled = true;
  private final DrawerLayout mDrawerLayout;
  private boolean mHasCustomUpIndicator;
  private Drawable mHomeAsUpIndicator;
  private final int mOpenDrawerContentDescRes;
  private Object mSetIndicatorInfo;
  private SlideDrawable mSlider;
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 18)
    {
      if (i < 11) {
        IMPL = new ActionBarDrawerToggleImplBase(null);
      }
    }
    else
    {
      IMPL = new ActionBarDrawerToggleImplJellybeanMR2(null);
      return;
    }
  }
  
  public ActionBarDrawerToggle(Activity paramActivity, DrawerLayout paramDrawerLayout, int paramInt1, int paramInt2, int paramInt3) {}
  
  public ActionBarDrawerToggle(Activity paramActivity, DrawerLayout paramDrawerLayout, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mActivity = paramActivity;
    if (!(paramActivity instanceof DelegateProvider))
    {
      this.mActivityImpl = null;
      this.mDrawerLayout = paramDrawerLayout;
      this.mDrawerImageResource = paramInt1;
      this.mOpenDrawerContentDescRes = paramInt2;
      this.mCloseDrawerContentDescRes = paramInt3;
      this.mHomeAsUpIndicator = getThemeUpIndicator();
      this.mDrawerImage = ContextCompat.getDrawable(paramActivity, paramInt1);
      this.mSlider = new SlideDrawable(this.mDrawerImage, null);
      paramActivity = this.mSlider;
      if (paramBoolean) {
        break label119;
      }
    }
    label119:
    for (float f = 0.0F;; f = 0.33333334F)
    {
      paramActivity.setOffset(f);
      return;
      this.mActivityImpl = ((DelegateProvider)paramActivity).getDrawerToggleDelegate();
      break;
    }
  }
  
  private static boolean assumeMaterial(Context paramContext)
  {
    if (paramContext.getApplicationInfo().targetSdkVersion < 21) {}
    while (Build.VERSION.SDK_INT < 21) {
      return false;
    }
    return true;
  }
  
  Drawable getThemeUpIndicator()
  {
    if (this.mActivityImpl == null) {
      return IMPL.getThemeUpIndicator(this.mActivity);
    }
    return this.mActivityImpl.getThemeUpIndicator();
  }
  
  public boolean isDrawerIndicatorEnabled()
  {
    return this.mDrawerIndicatorEnabled;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (this.mHasCustomUpIndicator) {}
    for (;;)
    {
      this.mDrawerImage = ContextCompat.getDrawable(this.mActivity, this.mDrawerImageResource);
      syncState();
      return;
      this.mHomeAsUpIndicator = getThemeUpIndicator();
    }
  }
  
  public void onDrawerClosed(View paramView)
  {
    this.mSlider.setPosition(0.0F);
    if (!this.mDrawerIndicatorEnabled) {
      return;
    }
    setActionBarDescription(this.mOpenDrawerContentDescRes);
  }
  
  public void onDrawerOpened(View paramView)
  {
    this.mSlider.setPosition(1.0F);
    if (!this.mDrawerIndicatorEnabled) {
      return;
    }
    setActionBarDescription(this.mCloseDrawerContentDescRes);
  }
  
  public void onDrawerSlide(View paramView, float paramFloat)
  {
    float f = this.mSlider.getPosition();
    if (paramFloat > 0.5F) {}
    for (paramFloat = Math.max(f, Math.max(0.0F, paramFloat - 0.5F) * 2.0F);; paramFloat = Math.min(f, paramFloat * 2.0F))
    {
      this.mSlider.setPosition(paramFloat);
      return;
    }
  }
  
  public void onDrawerStateChanged(int paramInt) {}
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem == null) {}
    while ((paramMenuItem.getItemId() != 16908332) || (!this.mDrawerIndicatorEnabled)) {
      return false;
    }
    if (!this.mDrawerLayout.isDrawerVisible(8388611)) {
      this.mDrawerLayout.openDrawer(8388611);
    }
    for (;;)
    {
      return true;
      this.mDrawerLayout.closeDrawer(8388611);
    }
  }
  
  void setActionBarDescription(int paramInt)
  {
    if (this.mActivityImpl == null)
    {
      this.mSetIndicatorInfo = IMPL.setActionBarDescription(this.mSetIndicatorInfo, this.mActivity, paramInt);
      return;
    }
    this.mActivityImpl.setActionBarDescription(paramInt);
  }
  
  void setActionBarUpIndicator(Drawable paramDrawable, int paramInt)
  {
    if (this.mActivityImpl == null)
    {
      this.mSetIndicatorInfo = IMPL.setActionBarUpIndicator(this.mSetIndicatorInfo, this.mActivity, paramDrawable, paramInt);
      return;
    }
    this.mActivityImpl.setActionBarUpIndicator(paramDrawable, paramInt);
  }
  
  public void setDrawerIndicatorEnabled(boolean paramBoolean)
  {
    if (paramBoolean == this.mDrawerIndicatorEnabled) {
      return;
    }
    if (!paramBoolean)
    {
      setActionBarUpIndicator(this.mHomeAsUpIndicator, 0);
      this.mDrawerIndicatorEnabled = paramBoolean;
      return;
    }
    SlideDrawable localSlideDrawable = this.mSlider;
    if (!this.mDrawerLayout.isDrawerOpen(8388611)) {}
    for (int i = this.mOpenDrawerContentDescRes;; i = this.mCloseDrawerContentDescRes)
    {
      setActionBarUpIndicator(localSlideDrawable, i);
      break;
    }
  }
  
  public void setHomeAsUpIndicator(int paramInt)
  {
    Drawable localDrawable = null;
    if (paramInt == 0) {}
    for (;;)
    {
      setHomeAsUpIndicator(localDrawable);
      return;
      localDrawable = ContextCompat.getDrawable(this.mActivity, paramInt);
    }
  }
  
  public void setHomeAsUpIndicator(Drawable paramDrawable)
  {
    if (paramDrawable != null) {
      this.mHomeAsUpIndicator = paramDrawable;
    }
    for (this.mHasCustomUpIndicator = true; this.mDrawerIndicatorEnabled; this.mHasCustomUpIndicator = false)
    {
      return;
      this.mHomeAsUpIndicator = getThemeUpIndicator();
    }
    setActionBarUpIndicator(this.mHomeAsUpIndicator, 0);
  }
  
  public void syncState()
  {
    if (!this.mDrawerLayout.isDrawerOpen(8388611)) {
      this.mSlider.setPosition(0.0F);
    }
    while (!this.mDrawerIndicatorEnabled)
    {
      return;
      this.mSlider.setPosition(1.0F);
    }
    SlideDrawable localSlideDrawable = this.mSlider;
    if (!this.mDrawerLayout.isDrawerOpen(8388611)) {}
    for (int i = this.mOpenDrawerContentDescRes;; i = this.mCloseDrawerContentDescRes)
    {
      setActionBarUpIndicator(localSlideDrawable, i);
      return;
    }
  }
  
  private static abstract interface ActionBarDrawerToggleImpl
  {
    public abstract Drawable getThemeUpIndicator(Activity paramActivity);
    
    public abstract Object setActionBarDescription(Object paramObject, Activity paramActivity, int paramInt);
    
    public abstract Object setActionBarUpIndicator(Object paramObject, Activity paramActivity, Drawable paramDrawable, int paramInt);
  }
  
  private static class ActionBarDrawerToggleImplBase
    implements ActionBarDrawerToggle.ActionBarDrawerToggleImpl
  {
    public Drawable getThemeUpIndicator(Activity paramActivity)
    {
      return null;
    }
    
    public Object setActionBarDescription(Object paramObject, Activity paramActivity, int paramInt)
    {
      return paramObject;
    }
    
    public Object setActionBarUpIndicator(Object paramObject, Activity paramActivity, Drawable paramDrawable, int paramInt)
    {
      return paramObject;
    }
  }
  
  private static class ActionBarDrawerToggleImplHC
    implements ActionBarDrawerToggle.ActionBarDrawerToggleImpl
  {
    public Drawable getThemeUpIndicator(Activity paramActivity)
    {
      return ActionBarDrawerToggleHoneycomb.getThemeUpIndicator(paramActivity);
    }
    
    public Object setActionBarDescription(Object paramObject, Activity paramActivity, int paramInt)
    {
      return ActionBarDrawerToggleHoneycomb.setActionBarDescription(paramObject, paramActivity, paramInt);
    }
    
    public Object setActionBarUpIndicator(Object paramObject, Activity paramActivity, Drawable paramDrawable, int paramInt)
    {
      return ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(paramObject, paramActivity, paramDrawable, paramInt);
    }
  }
  
  private static class ActionBarDrawerToggleImplJellybeanMR2
    implements ActionBarDrawerToggle.ActionBarDrawerToggleImpl
  {
    public Drawable getThemeUpIndicator(Activity paramActivity)
    {
      return ActionBarDrawerToggleJellybeanMR2.getThemeUpIndicator(paramActivity);
    }
    
    public Object setActionBarDescription(Object paramObject, Activity paramActivity, int paramInt)
    {
      return ActionBarDrawerToggleJellybeanMR2.setActionBarDescription(paramObject, paramActivity, paramInt);
    }
    
    public Object setActionBarUpIndicator(Object paramObject, Activity paramActivity, Drawable paramDrawable, int paramInt)
    {
      return ActionBarDrawerToggleJellybeanMR2.setActionBarUpIndicator(paramObject, paramActivity, paramDrawable, paramInt);
    }
  }
  
  public static abstract interface Delegate
  {
    @Nullable
    public abstract Drawable getThemeUpIndicator();
    
    public abstract void setActionBarDescription(int paramInt);
    
    public abstract void setActionBarUpIndicator(Drawable paramDrawable, int paramInt);
  }
  
  public static abstract interface DelegateProvider
  {
    @Nullable
    public abstract ActionBarDrawerToggle.Delegate getDrawerToggleDelegate();
  }
  
  private class SlideDrawable
    extends InsetDrawable
    implements Drawable.Callback
  {
    private final boolean mHasMirroring;
    private float mOffset;
    private float mPosition;
    private final Rect mTmpRect;
    
    private SlideDrawable(Drawable paramDrawable)
    {
      super(0);
      if (Build.VERSION.SDK_INT <= 18) {}
      for (;;)
      {
        this.mHasMirroring = bool;
        this.mTmpRect = new Rect();
        return;
        bool = true;
      }
    }
    
    public void draw(Canvas paramCanvas)
    {
      int j = 1;
      int i = 0;
      copyBounds(this.mTmpRect);
      paramCanvas.save();
      label44:
      int k;
      if (ViewCompat.getLayoutDirection(ActionBarDrawerToggle.this.mActivity.getWindow().getDecorView()) != 1)
      {
        if (i != 0) {
          break label105;
        }
        k = this.mTmpRect.width();
        float f1 = -this.mOffset;
        float f2 = k;
        float f3 = this.mPosition;
        paramCanvas.translate(j * (f1 * f2 * f3), 0.0F);
        if (i != 0) {
          break label111;
        }
      }
      for (;;)
      {
        super.draw(paramCanvas);
        paramCanvas.restore();
        return;
        i = 1;
        break;
        label105:
        j = -1;
        break label44;
        label111:
        if (!this.mHasMirroring)
        {
          paramCanvas.translate(k, 0.0F);
          paramCanvas.scale(-1.0F, 1.0F);
        }
      }
    }
    
    public float getPosition()
    {
      return this.mPosition;
    }
    
    public void setOffset(float paramFloat)
    {
      this.mOffset = paramFloat;
      invalidateSelf();
    }
    
    public void setPosition(float paramFloat)
    {
      this.mPosition = paramFloat;
      invalidateSelf();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/ActionBarDrawerToggle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */