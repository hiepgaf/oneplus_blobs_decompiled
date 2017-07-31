package com.android.server.wm;

class DragResizeMode
{
  static final int DRAG_RESIZE_MODE_DOCKED_DIVIDER = 1;
  static final int DRAG_RESIZE_MODE_FREEFORM = 0;
  
  static boolean isModeAllowedForStack(int paramInt1, int paramInt2)
  {
    switch (paramInt2)
    {
    default: 
      return false;
    case 0: 
      return paramInt1 == 2;
    }
    if ((paramInt1 == 3) || (paramInt1 == 1)) {}
    while (paramInt1 == 0) {
      return true;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/DragResizeMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */