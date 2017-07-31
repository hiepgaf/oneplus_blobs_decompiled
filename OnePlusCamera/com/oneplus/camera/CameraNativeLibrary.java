package com.oneplus.camera;

import com.oneplus.base.Log;

public final class CameraNativeLibrary
{
  private static volatile boolean m_IsLibraryLoaded;
  
  public static void load()
  {
    if (!m_IsLibraryLoaded) {}
    try
    {
      if (!m_IsLibraryLoaded)
      {
        Log.w("CameraNativeLibrary", "Load library");
        System.loadLibrary("opcamera");
        m_IsLibraryLoaded = true;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraNativeLibrary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */