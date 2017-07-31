package com.android.server.accessibility;

import android.util.MathUtils;
import android.view.MotionEvent;

final class GestureUtils
{
  public static double computeDistance(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    return MathUtils.dist(paramMotionEvent1.getX(paramInt), paramMotionEvent1.getY(paramInt), paramMotionEvent2.getX(paramInt), paramMotionEvent2.getY(paramInt));
  }
  
  private static boolean eventsWithinTimeAndDistanceSlop(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt1, int paramInt2, int paramInt3)
  {
    if (isTimedOut(paramMotionEvent1, paramMotionEvent2, paramInt1)) {
      return false;
    }
    return computeDistance(paramMotionEvent1, paramMotionEvent2, paramInt3) < paramInt2;
  }
  
  public static boolean isDraggingGesture(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9)
  {
    paramFloat1 = paramFloat5 - paramFloat1;
    paramFloat2 = paramFloat6 - paramFloat2;
    if ((paramFloat1 == 0.0F) && (paramFloat2 == 0.0F)) {
      return true;
    }
    paramFloat5 = (float)Math.hypot(paramFloat1, paramFloat2);
    if (paramFloat5 > 0.0F)
    {
      paramFloat1 /= paramFloat5;
      if (paramFloat5 <= 0.0F) {
        break label85;
      }
      paramFloat2 /= paramFloat5;
    }
    label85:
    for (;;)
    {
      paramFloat3 = paramFloat7 - paramFloat3;
      paramFloat4 = paramFloat8 - paramFloat4;
      if ((paramFloat3 != 0.0F) || (paramFloat4 != 0.0F)) {
        break label88;
      }
      return true;
      break;
    }
    label88:
    paramFloat5 = (float)Math.hypot(paramFloat3, paramFloat4);
    if (paramFloat5 > 0.0F)
    {
      paramFloat3 /= paramFloat5;
      if (paramFloat5 <= 0.0F) {
        break label140;
      }
      paramFloat4 /= paramFloat5;
    }
    label140:
    for (;;)
    {
      if (paramFloat1 * paramFloat3 + paramFloat2 * paramFloat4 >= paramFloat9) {
        break label143;
      }
      return false;
      break;
    }
    label143:
    return true;
  }
  
  public static boolean isMultiTap(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt1, int paramInt2, int paramInt3)
  {
    return eventsWithinTimeAndDistanceSlop(paramMotionEvent1, paramMotionEvent2, paramInt1, paramInt2, paramInt3);
  }
  
  public static boolean isSamePointerContext(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramMotionEvent1.getPointerIdBits() == paramMotionEvent2.getPointerIdBits())
    {
      bool1 = bool2;
      if (paramMotionEvent1.getPointerId(paramMotionEvent1.getActionIndex()) == paramMotionEvent2.getPointerId(paramMotionEvent2.getActionIndex())) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isTap(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt1, int paramInt2, int paramInt3)
  {
    return eventsWithinTimeAndDistanceSlop(paramMotionEvent1, paramMotionEvent2, paramInt1, paramInt2, paramInt3);
  }
  
  public static boolean isTimedOut(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    return paramMotionEvent2.getEventTime() - paramMotionEvent1.getEventTime() >= paramInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/GestureUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */