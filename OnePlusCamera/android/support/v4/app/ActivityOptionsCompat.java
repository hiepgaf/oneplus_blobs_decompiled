package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;

public class ActivityOptionsCompat
{
  public static ActivityOptionsCompat makeCustomAnimation(Context paramContext, int paramInt1, int paramInt2)
  {
    if (Build.VERSION.SDK_INT < 16) {
      return new ActivityOptionsCompat();
    }
    return new ActivityOptionsImplJB(ActivityOptionsCompatJB.makeCustomAnimation(paramContext, paramInt1, paramInt2));
  }
  
  public static ActivityOptionsCompat makeScaleUpAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (Build.VERSION.SDK_INT < 16) {
      return new ActivityOptionsCompat();
    }
    return new ActivityOptionsImplJB(ActivityOptionsCompatJB.makeScaleUpAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public static ActivityOptionsCompat makeSceneTransitionAnimation(Activity paramActivity, View paramView, String paramString)
  {
    if (Build.VERSION.SDK_INT < 21) {
      return new ActivityOptionsCompat();
    }
    return new ActivityOptionsImpl21(ActivityOptionsCompat21.makeSceneTransitionAnimation(paramActivity, paramView, paramString));
  }
  
  public static ActivityOptionsCompat makeSceneTransitionAnimation(Activity paramActivity, Pair<View, String>... paramVarArgs)
  {
    String[] arrayOfString = null;
    if (Build.VERSION.SDK_INT < 21) {
      return new ActivityOptionsCompat();
    }
    if (paramVarArgs == null)
    {
      arrayOfView = null;
      paramVarArgs = arrayOfString;
      return new ActivityOptionsImpl21(ActivityOptionsCompat21.makeSceneTransitionAnimation(paramActivity, arrayOfView, paramVarArgs));
    }
    View[] arrayOfView = new View[paramVarArgs.length];
    arrayOfString = new String[paramVarArgs.length];
    int i = 0;
    for (;;)
    {
      if (i >= paramVarArgs.length)
      {
        paramVarArgs = arrayOfString;
        break;
      }
      arrayOfView[i] = ((View)paramVarArgs[i].first);
      arrayOfString[i] = ((String)paramVarArgs[i].second);
      i += 1;
    }
  }
  
  public static ActivityOptionsCompat makeThumbnailScaleUpAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    if (Build.VERSION.SDK_INT < 16) {
      return new ActivityOptionsCompat();
    }
    return new ActivityOptionsImplJB(ActivityOptionsCompatJB.makeThumbnailScaleUpAnimation(paramView, paramBitmap, paramInt1, paramInt2));
  }
  
  public Bundle toBundle()
  {
    return null;
  }
  
  public void update(ActivityOptionsCompat paramActivityOptionsCompat) {}
  
  private static class ActivityOptionsImpl21
    extends ActivityOptionsCompat
  {
    private final ActivityOptionsCompat21 mImpl;
    
    ActivityOptionsImpl21(ActivityOptionsCompat21 paramActivityOptionsCompat21)
    {
      this.mImpl = paramActivityOptionsCompat21;
    }
    
    public Bundle toBundle()
    {
      return this.mImpl.toBundle();
    }
    
    public void update(ActivityOptionsCompat paramActivityOptionsCompat)
    {
      if (!(paramActivityOptionsCompat instanceof ActivityOptionsImpl21)) {
        return;
      }
      paramActivityOptionsCompat = (ActivityOptionsImpl21)paramActivityOptionsCompat;
      this.mImpl.update(paramActivityOptionsCompat.mImpl);
    }
  }
  
  private static class ActivityOptionsImplJB
    extends ActivityOptionsCompat
  {
    private final ActivityOptionsCompatJB mImpl;
    
    ActivityOptionsImplJB(ActivityOptionsCompatJB paramActivityOptionsCompatJB)
    {
      this.mImpl = paramActivityOptionsCompatJB;
    }
    
    public Bundle toBundle()
    {
      return this.mImpl.toBundle();
    }
    
    public void update(ActivityOptionsCompat paramActivityOptionsCompat)
    {
      if (!(paramActivityOptionsCompat instanceof ActivityOptionsImplJB)) {
        return;
      }
      paramActivityOptionsCompat = (ActivityOptionsImplJB)paramActivityOptionsCompat;
      this.mImpl.update(paramActivityOptionsCompat.mImpl);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/ActivityOptionsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */