package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.View;
import android.view.animation.Interpolator;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class ViewPropertyAnimatorCompat
{
  static final ViewPropertyAnimatorCompatImpl IMPL;
  static final int LISTENER_TAG_ID = 2113929216;
  private static final String TAG = "ViewAnimatorCompat";
  private Runnable mEndAction = null;
  private int mOldLayerType = -1;
  private Runnable mStartAction = null;
  private WeakReference<View> mView;
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 19)
    {
      if (i < 18)
      {
        if (i >= 16) {
          break label61;
        }
        if (i >= 14) {
          break label72;
        }
        IMPL = new BaseViewPropertyAnimatorCompatImpl();
      }
    }
    else
    {
      IMPL = new KitKatViewPropertyAnimatorCompatImpl();
      return;
    }
    IMPL = new JBMr2ViewPropertyAnimatorCompatImpl();
    return;
    label61:
    IMPL = new JBViewPropertyAnimatorCompatImpl();
    return;
    label72:
    IMPL = new ICSViewPropertyAnimatorCompatImpl();
  }
  
  ViewPropertyAnimatorCompat(View paramView)
  {
    this.mView = new WeakReference(paramView);
  }
  
  public ViewPropertyAnimatorCompat alpha(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.alpha(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat alphaBy(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.alphaBy(this, localView, paramFloat);
    return this;
  }
  
  public void cancel()
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return;
    }
    IMPL.cancel(this, localView);
  }
  
  public long getDuration()
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return 0L;
    }
    return IMPL.getDuration(this, localView);
  }
  
  public Interpolator getInterpolator()
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return null;
    }
    return IMPL.getInterpolator(this, localView);
  }
  
  public long getStartDelay()
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return 0L;
    }
    return IMPL.getStartDelay(this, localView);
  }
  
  public ViewPropertyAnimatorCompat rotation(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.rotation(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat rotationBy(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.rotationBy(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat rotationX(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.rotationX(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat rotationXBy(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.rotationXBy(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat rotationY(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.rotationY(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat rotationYBy(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.rotationYBy(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat scaleX(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.scaleX(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat scaleXBy(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.scaleXBy(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat scaleY(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.scaleY(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat scaleYBy(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.scaleYBy(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat setDuration(long paramLong)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.setDuration(this, localView, paramLong);
    return this;
  }
  
  public ViewPropertyAnimatorCompat setInterpolator(Interpolator paramInterpolator)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.setInterpolator(this, localView, paramInterpolator);
    return this;
  }
  
  public ViewPropertyAnimatorCompat setListener(ViewPropertyAnimatorListener paramViewPropertyAnimatorListener)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.setListener(this, localView, paramViewPropertyAnimatorListener);
    return this;
  }
  
  public ViewPropertyAnimatorCompat setStartDelay(long paramLong)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.setStartDelay(this, localView, paramLong);
    return this;
  }
  
  public ViewPropertyAnimatorCompat setUpdateListener(ViewPropertyAnimatorUpdateListener paramViewPropertyAnimatorUpdateListener)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.setUpdateListener(this, localView, paramViewPropertyAnimatorUpdateListener);
    return this;
  }
  
  public void start()
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return;
    }
    IMPL.start(this, localView);
  }
  
  public ViewPropertyAnimatorCompat translationX(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.translationX(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat translationXBy(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.translationXBy(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat translationY(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.translationY(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat translationYBy(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.translationYBy(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat withEndAction(Runnable paramRunnable)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.withEndAction(this, localView, paramRunnable);
    return this;
  }
  
  public ViewPropertyAnimatorCompat withLayer()
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.withLayer(this, localView);
    return this;
  }
  
  public ViewPropertyAnimatorCompat withStartAction(Runnable paramRunnable)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.withStartAction(this, localView, paramRunnable);
    return this;
  }
  
  public ViewPropertyAnimatorCompat x(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.x(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat xBy(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.xBy(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat y(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.y(this, localView, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimatorCompat yBy(float paramFloat)
  {
    View localView = (View)this.mView.get();
    if (localView == null) {
      return this;
    }
    IMPL.yBy(this, localView, paramFloat);
    return this;
  }
  
  static class BaseViewPropertyAnimatorCompatImpl
    implements ViewPropertyAnimatorCompat.ViewPropertyAnimatorCompatImpl
  {
    WeakHashMap<View, Runnable> mStarterMap = null;
    
    private void postStartMessage(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      if (this.mStarterMap == null) {}
      for (Object localObject = null; localObject != null; localObject = (Runnable)this.mStarterMap.get(paramView))
      {
        paramView.removeCallbacks((Runnable)localObject);
        paramView.post((Runnable)localObject);
        return;
      }
      localObject = new Starter(paramViewPropertyAnimatorCompat, paramView, null);
      if (this.mStarterMap != null) {}
      for (;;)
      {
        this.mStarterMap.put(paramView, localObject);
        break;
        this.mStarterMap = new WeakHashMap();
      }
    }
    
    private void removeStartMessage(View paramView)
    {
      if (this.mStarterMap == null) {}
      Runnable localRunnable;
      do
      {
        return;
        localRunnable = (Runnable)this.mStarterMap.get(paramView);
      } while (localRunnable == null);
      paramView.removeCallbacks(localRunnable);
    }
    
    private void startAnimation(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      Object localObject = paramView.getTag(2113929216);
      Runnable localRunnable;
      if (!(localObject instanceof ViewPropertyAnimatorListener))
      {
        localObject = null;
        localRunnable = paramViewPropertyAnimatorCompat.mStartAction;
        paramViewPropertyAnimatorCompat = paramViewPropertyAnimatorCompat.mEndAction;
        if (localRunnable != null) {
          break label56;
        }
        label32:
        if (localObject != null) {
          break label66;
        }
        label36:
        if (paramViewPropertyAnimatorCompat != null) {
          break label83;
        }
      }
      for (;;)
      {
        if (this.mStarterMap != null) {
          break label92;
        }
        return;
        localObject = (ViewPropertyAnimatorListener)localObject;
        break;
        label56:
        localRunnable.run();
        break label32;
        label66:
        ((ViewPropertyAnimatorListener)localObject).onAnimationStart(paramView);
        ((ViewPropertyAnimatorListener)localObject).onAnimationEnd(paramView);
        break label36;
        label83:
        paramViewPropertyAnimatorCompat.run();
      }
      label92:
      this.mStarterMap.remove(paramView);
    }
    
    public void alpha(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void alphaBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void cancel(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public long getDuration(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      return 0L;
    }
    
    public Interpolator getInterpolator(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      return null;
    }
    
    public long getStartDelay(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      return 0L;
    }
    
    public void rotation(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void rotationBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void rotationX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void rotationXBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void rotationY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void rotationYBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void scaleX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void scaleXBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void scaleY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void scaleYBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void setDuration(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, long paramLong) {}
    
    public void setInterpolator(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Interpolator paramInterpolator) {}
    
    public void setListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorListener paramViewPropertyAnimatorListener)
    {
      paramView.setTag(2113929216, paramViewPropertyAnimatorListener);
    }
    
    public void setStartDelay(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, long paramLong) {}
    
    public void setUpdateListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorUpdateListener paramViewPropertyAnimatorUpdateListener) {}
    
    public void start(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      removeStartMessage(paramView);
      startAnimation(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void translationX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void translationXBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void translationY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void translationYBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void withEndAction(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Runnable paramRunnable)
    {
      ViewPropertyAnimatorCompat.access$002(paramViewPropertyAnimatorCompat, paramRunnable);
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void withLayer(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView) {}
    
    public void withStartAction(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Runnable paramRunnable)
    {
      ViewPropertyAnimatorCompat.access$102(paramViewPropertyAnimatorCompat, paramRunnable);
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void x(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void xBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void y(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    public void yBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      postStartMessage(paramViewPropertyAnimatorCompat, paramView);
    }
    
    class Starter
      implements Runnable
    {
      WeakReference<View> mViewRef;
      ViewPropertyAnimatorCompat mVpa;
      
      private Starter(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
      {
        this.mViewRef = new WeakReference(paramView);
        this.mVpa = paramViewPropertyAnimatorCompat;
      }
      
      public void run()
      {
        View localView = (View)this.mViewRef.get();
        if (localView == null) {
          return;
        }
        ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl.this.startAnimation(this.mVpa, localView);
      }
    }
  }
  
  static class ICSViewPropertyAnimatorCompatImpl
    extends ViewPropertyAnimatorCompat.BaseViewPropertyAnimatorCompatImpl
  {
    WeakHashMap<View, Integer> mLayerMap = null;
    
    public void alpha(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.alpha(paramView, paramFloat);
    }
    
    public void alphaBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.alphaBy(paramView, paramFloat);
    }
    
    public void cancel(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      ViewPropertyAnimatorCompatICS.cancel(paramView);
    }
    
    public long getDuration(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      return ViewPropertyAnimatorCompatICS.getDuration(paramView);
    }
    
    public long getStartDelay(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      return ViewPropertyAnimatorCompatICS.getStartDelay(paramView);
    }
    
    public void rotation(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.rotation(paramView, paramFloat);
    }
    
    public void rotationBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.rotationBy(paramView, paramFloat);
    }
    
    public void rotationX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.rotationX(paramView, paramFloat);
    }
    
    public void rotationXBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.rotationXBy(paramView, paramFloat);
    }
    
    public void rotationY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.rotationY(paramView, paramFloat);
    }
    
    public void rotationYBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.rotationYBy(paramView, paramFloat);
    }
    
    public void scaleX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.scaleX(paramView, paramFloat);
    }
    
    public void scaleXBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.scaleXBy(paramView, paramFloat);
    }
    
    public void scaleY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.scaleY(paramView, paramFloat);
    }
    
    public void scaleYBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.scaleYBy(paramView, paramFloat);
    }
    
    public void setDuration(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, long paramLong)
    {
      ViewPropertyAnimatorCompatICS.setDuration(paramView, paramLong);
    }
    
    public void setInterpolator(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Interpolator paramInterpolator)
    {
      ViewPropertyAnimatorCompatICS.setInterpolator(paramView, paramInterpolator);
    }
    
    public void setListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorListener paramViewPropertyAnimatorListener)
    {
      paramView.setTag(2113929216, paramViewPropertyAnimatorListener);
      ViewPropertyAnimatorCompatICS.setListener(paramView, new MyVpaListener(paramViewPropertyAnimatorCompat));
    }
    
    public void setStartDelay(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, long paramLong)
    {
      ViewPropertyAnimatorCompatICS.setStartDelay(paramView, paramLong);
    }
    
    public void start(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      ViewPropertyAnimatorCompatICS.start(paramView);
    }
    
    public void translationX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.translationX(paramView, paramFloat);
    }
    
    public void translationXBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.translationXBy(paramView, paramFloat);
    }
    
    public void translationY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.translationY(paramView, paramFloat);
    }
    
    public void translationYBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.translationYBy(paramView, paramFloat);
    }
    
    public void withEndAction(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Runnable paramRunnable)
    {
      ViewPropertyAnimatorCompatICS.setListener(paramView, new MyVpaListener(paramViewPropertyAnimatorCompat));
      ViewPropertyAnimatorCompat.access$002(paramViewPropertyAnimatorCompat, paramRunnable);
    }
    
    public void withLayer(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      ViewPropertyAnimatorCompat.access$402(paramViewPropertyAnimatorCompat, ViewCompat.getLayerType(paramView));
      ViewPropertyAnimatorCompatICS.setListener(paramView, new MyVpaListener(paramViewPropertyAnimatorCompat));
    }
    
    public void withStartAction(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Runnable paramRunnable)
    {
      ViewPropertyAnimatorCompatICS.setListener(paramView, new MyVpaListener(paramViewPropertyAnimatorCompat));
      ViewPropertyAnimatorCompat.access$102(paramViewPropertyAnimatorCompat, paramRunnable);
    }
    
    public void x(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.x(paramView, paramFloat);
    }
    
    public void xBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.xBy(paramView, paramFloat);
    }
    
    public void y(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.y(paramView, paramFloat);
    }
    
    public void yBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat)
    {
      ViewPropertyAnimatorCompatICS.yBy(paramView, paramFloat);
    }
    
    static class MyVpaListener
      implements ViewPropertyAnimatorListener
    {
      ViewPropertyAnimatorCompat mVpa;
      
      MyVpaListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat)
      {
        this.mVpa = paramViewPropertyAnimatorCompat;
      }
      
      public void onAnimationCancel(View paramView)
      {
        Object localObject = paramView.getTag(2113929216);
        if (!(localObject instanceof ViewPropertyAnimatorListener)) {}
        for (localObject = null; localObject == null; localObject = (ViewPropertyAnimatorListener)localObject) {
          return;
        }
        ((ViewPropertyAnimatorListener)localObject).onAnimationCancel(paramView);
      }
      
      public void onAnimationEnd(View paramView)
      {
        if (this.mVpa.mOldLayerType < 0)
        {
          if (this.mVpa.mEndAction != null) {
            break label65;
          }
          label20:
          localObject = paramView.getTag(2113929216);
          if ((localObject instanceof ViewPropertyAnimatorListener)) {
            break label80;
          }
        }
        label65:
        label80:
        for (Object localObject = null;; localObject = (ViewPropertyAnimatorListener)localObject)
        {
          if (localObject != null) {
            break label88;
          }
          return;
          ViewCompat.setLayerType(paramView, this.mVpa.mOldLayerType, null);
          ViewPropertyAnimatorCompat.access$402(this.mVpa, -1);
          break;
          this.mVpa.mEndAction.run();
          break label20;
        }
        label88:
        ((ViewPropertyAnimatorListener)localObject).onAnimationEnd(paramView);
      }
      
      public void onAnimationStart(View paramView)
      {
        if (this.mVpa.mOldLayerType < 0)
        {
          if (this.mVpa.mStartAction != null) {
            break label50;
          }
          label20:
          localObject = paramView.getTag(2113929216);
          if ((localObject instanceof ViewPropertyAnimatorListener)) {
            break label65;
          }
        }
        label50:
        label65:
        for (Object localObject = null;; localObject = (ViewPropertyAnimatorListener)localObject)
        {
          if (localObject != null) {
            break label73;
          }
          return;
          ViewCompat.setLayerType(paramView, 2, null);
          break;
          this.mVpa.mStartAction.run();
          break label20;
        }
        label73:
        ((ViewPropertyAnimatorListener)localObject).onAnimationStart(paramView);
      }
    }
  }
  
  static class JBMr2ViewPropertyAnimatorCompatImpl
    extends ViewPropertyAnimatorCompat.JBViewPropertyAnimatorCompatImpl
  {
    public Interpolator getInterpolator(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      return ViewPropertyAnimatorCompatJellybeanMr2.getInterpolator(paramView);
    }
  }
  
  static class JBViewPropertyAnimatorCompatImpl
    extends ViewPropertyAnimatorCompat.ICSViewPropertyAnimatorCompatImpl
  {
    public void setListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorListener paramViewPropertyAnimatorListener)
    {
      ViewPropertyAnimatorCompatJB.setListener(paramView, paramViewPropertyAnimatorListener);
    }
    
    public void withEndAction(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Runnable paramRunnable)
    {
      ViewPropertyAnimatorCompatJB.withEndAction(paramView, paramRunnable);
    }
    
    public void withLayer(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView)
    {
      ViewPropertyAnimatorCompatJB.withLayer(paramView);
    }
    
    public void withStartAction(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Runnable paramRunnable)
    {
      ViewPropertyAnimatorCompatJB.withStartAction(paramView, paramRunnable);
    }
  }
  
  static class KitKatViewPropertyAnimatorCompatImpl
    extends ViewPropertyAnimatorCompat.JBMr2ViewPropertyAnimatorCompatImpl
  {
    public void setUpdateListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorUpdateListener paramViewPropertyAnimatorUpdateListener)
    {
      ViewPropertyAnimatorCompatKK.setUpdateListener(paramView, paramViewPropertyAnimatorUpdateListener);
    }
  }
  
  static abstract interface ViewPropertyAnimatorCompatImpl
  {
    public abstract void alpha(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void alphaBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void cancel(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView);
    
    public abstract long getDuration(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView);
    
    public abstract Interpolator getInterpolator(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView);
    
    public abstract long getStartDelay(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView);
    
    public abstract void rotation(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void rotationBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void rotationX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void rotationXBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void rotationY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void rotationYBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void scaleX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void scaleXBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void scaleY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void scaleYBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void setDuration(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, long paramLong);
    
    public abstract void setInterpolator(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Interpolator paramInterpolator);
    
    public abstract void setListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorListener paramViewPropertyAnimatorListener);
    
    public abstract void setStartDelay(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, long paramLong);
    
    public abstract void setUpdateListener(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, ViewPropertyAnimatorUpdateListener paramViewPropertyAnimatorUpdateListener);
    
    public abstract void start(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView);
    
    public abstract void translationX(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void translationXBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void translationY(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void translationYBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void withEndAction(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Runnable paramRunnable);
    
    public abstract void withLayer(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView);
    
    public abstract void withStartAction(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, Runnable paramRunnable);
    
    public abstract void x(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void xBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void y(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
    
    public abstract void yBy(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, View paramView, float paramFloat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/view/ViewPropertyAnimatorCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */