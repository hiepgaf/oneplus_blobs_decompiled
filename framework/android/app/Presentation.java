package android.app;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Window;
import android.view.WindowManagerImpl;

public class Presentation
  extends Dialog
{
  private static final int MSG_CANCEL = 1;
  private static final String TAG = "Presentation";
  private final Display mDisplay;
  private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener()
  {
    public void onDisplayAdded(int paramAnonymousInt) {}
    
    public void onDisplayChanged(int paramAnonymousInt)
    {
      if (paramAnonymousInt == Presentation.-get0(Presentation.this).getDisplayId()) {
        Presentation.-wrap0(Presentation.this);
      }
    }
    
    public void onDisplayRemoved(int paramAnonymousInt)
    {
      if (paramAnonymousInt == Presentation.-get0(Presentation.this).getDisplayId()) {
        Presentation.-wrap1(Presentation.this);
      }
    }
  };
  private final DisplayManager mDisplayManager;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      Presentation.this.cancel();
    }
  };
  
  public Presentation(Context paramContext, Display paramDisplay)
  {
    this(paramContext, paramDisplay, 0);
  }
  
  public Presentation(Context paramContext, Display paramDisplay, int paramInt)
  {
    super(createPresentationContext(paramContext, paramDisplay, paramInt), paramInt, false);
    this.mDisplay = paramDisplay;
    this.mDisplayManager = ((DisplayManager)getContext().getSystemService("display"));
    getWindow().setGravity(119);
    setCanceledOnTouchOutside(false);
  }
  
  private static Context createPresentationContext(Context paramContext, Display paramDisplay, int paramInt)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("outerContext must not be null");
    }
    if (paramDisplay == null) {
      throw new IllegalArgumentException("display must not be null");
    }
    paramDisplay = paramContext.createDisplayContext(paramDisplay);
    int i = paramInt;
    if (paramInt == 0)
    {
      TypedValue localTypedValue = new TypedValue();
      paramDisplay.getTheme().resolveAttribute(16843712, localTypedValue, true);
      i = localTypedValue.resourceId;
    }
    new ContextThemeWrapper(paramDisplay, i)
    {
      public Object getSystemService(String paramAnonymousString)
      {
        if ("window".equals(paramAnonymousString)) {
          return this.val$displayWindowManager;
        }
        return super.getSystemService(paramAnonymousString);
      }
    };
  }
  
  private void handleDisplayChanged()
  {
    onDisplayChanged();
    if (!isConfigurationStillValid())
    {
      Log.i("Presentation", "Presentation is being dismissed because the display metrics have changed since it was created.");
      cancel();
    }
  }
  
  private void handleDisplayRemoved()
  {
    onDisplayRemoved();
    cancel();
  }
  
  private boolean isConfigurationStillValid()
  {
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    this.mDisplay.getMetrics(localDisplayMetrics);
    return localDisplayMetrics.equalsPhysical(getResources().getDisplayMetrics());
  }
  
  public Display getDisplay()
  {
    return this.mDisplay;
  }
  
  public Resources getResources()
  {
    return getContext().getResources();
  }
  
  public void onDisplayChanged() {}
  
  public void onDisplayRemoved() {}
  
  protected void onStart()
  {
    super.onStart();
    this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mHandler);
    if (!isConfigurationStillValid())
    {
      Log.i("Presentation", "Presentation is being dismissed because the display metrics have changed since it was created.");
      this.mHandler.sendEmptyMessage(1);
    }
  }
  
  protected void onStop()
  {
    this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
    super.onStop();
  }
  
  public void show()
  {
    super.show();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/Presentation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */