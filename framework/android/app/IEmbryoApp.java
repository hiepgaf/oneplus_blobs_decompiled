package android.app;

import android.content.Context;
import android.view.View;

public abstract interface IEmbryoApp
{
  public abstract void attach(Context paramContext);
  
  public abstract View checkDecorLayout(Context paramContext, int paramInt);
  
  public abstract void checkHWUI(Context paramContext);
  
  public abstract View checkMainLayout(Context paramContext, int paramInt);
  
  public abstract Runnable getRunnable();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IEmbryoApp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */