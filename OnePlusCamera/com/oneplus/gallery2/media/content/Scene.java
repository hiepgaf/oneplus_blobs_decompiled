package com.oneplus.gallery2.media.content;

import android.content.Context;
import java.util.Set;

public abstract interface Scene
{
  public abstract String getId();
  
  public abstract void getKeywords(Context paramContext, Set<String> paramSet);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/content/Scene.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */