package android.content.res;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;

class DrawableCache
  extends ThemedResourceCache<Drawable.ConstantState>
{
  public Drawable getInstance(long paramLong, Resources paramResources, Resources.Theme paramTheme)
  {
    Drawable.ConstantState localConstantState = (Drawable.ConstantState)get(paramLong, paramTheme);
    if (localConstantState != null) {
      return localConstantState.newDrawable(paramResources, paramTheme);
    }
    return null;
  }
  
  public boolean shouldInvalidateEntry(Drawable.ConstantState paramConstantState, int paramInt)
  {
    return Configuration.needNewResources(paramInt, paramConstantState.getChangingConfigurations());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/DrawableCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */