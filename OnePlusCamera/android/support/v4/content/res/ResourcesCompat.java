package android.support.v4.content.res;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;

public class ResourcesCompat
{
  public static Drawable getDrawable(Resources paramResources, int paramInt, Resources.Theme paramTheme)
    throws Resources.NotFoundException
  {
    if (Build.VERSION.SDK_INT < 21) {
      return paramResources.getDrawable(paramInt);
    }
    return ResourcesCompatApi21.getDrawable(paramResources, paramInt, paramTheme);
  }
  
  public static Drawable getDrawableForDensity(Resources paramResources, int paramInt1, int paramInt2, Resources.Theme paramTheme)
    throws Resources.NotFoundException
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 21)
    {
      if (i < 15) {
        return paramResources.getDrawable(paramInt1);
      }
    }
    else {
      return ResourcesCompatApi21.getDrawableForDensity(paramResources, paramInt1, paramInt2, paramTheme);
    }
    return ResourcesCompatIcsMr1.getDrawableForDensity(paramResources, paramInt1, paramInt2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/content/res/ResourcesCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */