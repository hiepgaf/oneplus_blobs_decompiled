package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

public class ViewGroupCompat
{
  static final ViewGroupCompatImpl IMPL;
  public static final int LAYOUT_MODE_CLIP_BOUNDS = 0;
  public static final int LAYOUT_MODE_OPTICAL_BOUNDS = 1;
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 21)
    {
      if (i < 18)
      {
        if (i >= 14) {
          break label61;
        }
        if (i >= 11) {
          break label72;
        }
        IMPL = new ViewGroupCompatStubImpl();
      }
    }
    else
    {
      IMPL = new ViewGroupCompatApi21Impl();
      return;
    }
    IMPL = new ViewGroupCompatJellybeanMR2Impl();
    return;
    label61:
    IMPL = new ViewGroupCompatIcsImpl();
    return;
    label72:
    IMPL = new ViewGroupCompatHCImpl();
  }
  
  public static int getLayoutMode(ViewGroup paramViewGroup)
  {
    return IMPL.getLayoutMode(paramViewGroup);
  }
  
  public static boolean isTransitionGroup(ViewGroup paramViewGroup)
  {
    return IMPL.isTransitionGroup(paramViewGroup);
  }
  
  public static boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    return IMPL.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
  }
  
  public static void setLayoutMode(ViewGroup paramViewGroup, int paramInt)
  {
    IMPL.setLayoutMode(paramViewGroup, paramInt);
  }
  
  public static void setMotionEventSplittingEnabled(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    IMPL.setMotionEventSplittingEnabled(paramViewGroup, paramBoolean);
  }
  
  public static void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    IMPL.setTransitionGroup(paramViewGroup, paramBoolean);
  }
  
  static class ViewGroupCompatApi21Impl
    extends ViewGroupCompat.ViewGroupCompatJellybeanMR2Impl
  {
    public boolean isTransitionGroup(ViewGroup paramViewGroup)
    {
      return ViewGroupCompatApi21.isTransitionGroup(paramViewGroup);
    }
    
    public void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      ViewGroupCompatApi21.setTransitionGroup(paramViewGroup, paramBoolean);
    }
  }
  
  static class ViewGroupCompatHCImpl
    extends ViewGroupCompat.ViewGroupCompatStubImpl
  {
    public void setMotionEventSplittingEnabled(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      ViewGroupCompatHC.setMotionEventSplittingEnabled(paramViewGroup, paramBoolean);
    }
  }
  
  static class ViewGroupCompatIcsImpl
    extends ViewGroupCompat.ViewGroupCompatHCImpl
  {
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      return ViewGroupCompatIcs.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
    }
  }
  
  static abstract interface ViewGroupCompatImpl
  {
    public abstract int getLayoutMode(ViewGroup paramViewGroup);
    
    public abstract boolean isTransitionGroup(ViewGroup paramViewGroup);
    
    public abstract boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent);
    
    public abstract void setLayoutMode(ViewGroup paramViewGroup, int paramInt);
    
    public abstract void setMotionEventSplittingEnabled(ViewGroup paramViewGroup, boolean paramBoolean);
    
    public abstract void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean);
  }
  
  static class ViewGroupCompatJellybeanMR2Impl
    extends ViewGroupCompat.ViewGroupCompatIcsImpl
  {
    public int getLayoutMode(ViewGroup paramViewGroup)
    {
      return ViewGroupCompatJellybeanMR2.getLayoutMode(paramViewGroup);
    }
    
    public void setLayoutMode(ViewGroup paramViewGroup, int paramInt)
    {
      ViewGroupCompatJellybeanMR2.setLayoutMode(paramViewGroup, paramInt);
    }
  }
  
  static class ViewGroupCompatStubImpl
    implements ViewGroupCompat.ViewGroupCompatImpl
  {
    public int getLayoutMode(ViewGroup paramViewGroup)
    {
      return 0;
    }
    
    public boolean isTransitionGroup(ViewGroup paramViewGroup)
    {
      return false;
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      return true;
    }
    
    public void setLayoutMode(ViewGroup paramViewGroup, int paramInt) {}
    
    public void setMotionEventSplittingEnabled(ViewGroup paramViewGroup, boolean paramBoolean) {}
    
    public void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/view/ViewGroupCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */