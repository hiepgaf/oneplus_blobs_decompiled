package android.support.v13.app;

import android.app.Fragment;
import android.os.Build.VERSION;

public class FragmentCompat
{
  static final FragmentCompatImpl IMPL = new ICSFragmentCompatImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT < 15)
    {
      if (Build.VERSION.SDK_INT < 14) {
        IMPL = new BaseFragmentCompatImpl();
      }
    }
    else
    {
      IMPL = new ICSMR1FragmentCompatImpl();
      return;
    }
  }
  
  public static void setMenuVisibility(Fragment paramFragment, boolean paramBoolean)
  {
    IMPL.setMenuVisibility(paramFragment, paramBoolean);
  }
  
  public static void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
  {
    IMPL.setUserVisibleHint(paramFragment, paramBoolean);
  }
  
  static class BaseFragmentCompatImpl
    implements FragmentCompat.FragmentCompatImpl
  {
    public void setMenuVisibility(Fragment paramFragment, boolean paramBoolean) {}
    
    public void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean) {}
  }
  
  static abstract interface FragmentCompatImpl
  {
    public abstract void setMenuVisibility(Fragment paramFragment, boolean paramBoolean);
    
    public abstract void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean);
  }
  
  static class ICSFragmentCompatImpl
    extends FragmentCompat.BaseFragmentCompatImpl
  {
    public void setMenuVisibility(Fragment paramFragment, boolean paramBoolean)
    {
      FragmentCompatICS.setMenuVisibility(paramFragment, paramBoolean);
    }
  }
  
  static class ICSMR1FragmentCompatImpl
    extends FragmentCompat.ICSFragmentCompatImpl
  {
    public void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
    {
      FragmentCompatICSMR1.setUserVisibleHint(paramFragment, paramBoolean);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v13/app/FragmentCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */