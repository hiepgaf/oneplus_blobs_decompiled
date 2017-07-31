package com.android.server.am;

import android.content.pm.ActivityInfo.WindowLayout;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Slog;
import android.view.Display;
import java.util.ArrayList;

class LaunchingTaskPositioner
{
  private static final boolean ALLOW_RESTART = true;
  private static final int BOUNDS_CONFLICT_MIN_DISTANCE = 4;
  private static final int MARGIN_SIZE_DENOMINATOR = 4;
  private static final int MINIMAL_STEP = 1;
  private static final int SHIFT_POLICY_DIAGONAL_DOWN = 1;
  private static final int SHIFT_POLICY_HORIZONTAL_LEFT = 3;
  private static final int SHIFT_POLICY_HORIZONTAL_RIGHT = 2;
  private static final int STEP_DENOMINATOR = 16;
  private static final String TAG = "ActivityManager";
  private static final int WINDOW_SIZE_DENOMINATOR = 2;
  private final Rect mAvailableRect = new Rect();
  private int mDefaultFreeformHeight;
  private int mDefaultFreeformStartX;
  private int mDefaultFreeformStartY;
  private int mDefaultFreeformStepHorizontal;
  private int mDefaultFreeformStepVertical;
  private int mDefaultFreeformWidth;
  private boolean mDefaultStartBoundsConfigurationSet = false;
  private int mDisplayHeight;
  private int mDisplayWidth;
  private final Rect mTmpOriginal = new Rect();
  private final Rect mTmpProposal = new Rect();
  
  private static boolean boundsConflict(Rect paramRect, ArrayList<TaskRecord> paramArrayList)
  {
    int i = paramArrayList.size() - 1;
    while (i >= 0)
    {
      Object localObject = (TaskRecord)paramArrayList.get(i);
      if ((!((TaskRecord)localObject).mActivities.isEmpty()) && (((TaskRecord)localObject).mBounds != null))
      {
        localObject = ((TaskRecord)localObject).mBounds;
        if ((closeLeftTopCorner(paramRect, (Rect)localObject)) || (closeRightTopCorner(paramRect, (Rect)localObject)) || (closeLeftBottomCorner(paramRect, (Rect)localObject)) || (closeRightBottomCorner(paramRect, (Rect)localObject))) {
          return true;
        }
      }
      i -= 1;
    }
    return false;
  }
  
  private static final boolean closeLeftBottomCorner(Rect paramRect1, Rect paramRect2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (Math.abs(paramRect1.left - paramRect2.left) < 4)
    {
      bool1 = bool2;
      if (Math.abs(paramRect1.bottom - paramRect2.bottom) < 4) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static final boolean closeLeftTopCorner(Rect paramRect1, Rect paramRect2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (Math.abs(paramRect1.left - paramRect2.left) < 4)
    {
      bool1 = bool2;
      if (Math.abs(paramRect1.top - paramRect2.top) < 4) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static final boolean closeRightBottomCorner(Rect paramRect1, Rect paramRect2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (Math.abs(paramRect1.right - paramRect2.right) < 4)
    {
      bool1 = bool2;
      if (Math.abs(paramRect1.bottom - paramRect2.bottom) < 4) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static final boolean closeRightTopCorner(Rect paramRect1, Rect paramRect2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (Math.abs(paramRect1.right - paramRect2.right) < 4)
    {
      bool1 = bool2;
      if (Math.abs(paramRect1.top - paramRect2.top) < 4) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private int getFinalHeight(ActivityInfo.WindowLayout paramWindowLayout)
  {
    int i = this.mDefaultFreeformHeight;
    if (paramWindowLayout.height > 0) {
      i = paramWindowLayout.height;
    }
    if (paramWindowLayout.heightFraction > 0.0F) {
      i = (int)(this.mAvailableRect.height() * paramWindowLayout.heightFraction);
    }
    return i;
  }
  
  private int getFinalWidth(ActivityInfo.WindowLayout paramWindowLayout)
  {
    int i = this.mDefaultFreeformWidth;
    if (paramWindowLayout.width > 0) {
      i = paramWindowLayout.width;
    }
    if (paramWindowLayout.widthFraction > 0.0F) {
      i = (int)(this.mAvailableRect.width() * paramWindowLayout.widthFraction);
    }
    return i;
  }
  
  private void position(TaskRecord paramTaskRecord, ArrayList<TaskRecord> paramArrayList, Rect paramRect, boolean paramBoolean, int paramInt)
  {
    this.mTmpOriginal.set(paramRect);
    int i = 0;
    int j;
    if (boundsConflict(paramRect, paramArrayList))
    {
      shiftStartingPoint(paramRect, paramInt);
      j = i;
      if (!shiftedToFar(paramRect, paramInt)) {
        break label105;
      }
      if (paramBoolean) {
        break label60;
      }
      paramRect.set(this.mTmpOriginal);
    }
    for (;;)
    {
      paramTaskRecord.updateOverrideConfiguration(paramRect);
      return;
      label60:
      paramRect.set(this.mAvailableRect.left, this.mAvailableRect.top, this.mAvailableRect.left + paramRect.width(), this.mAvailableRect.top + paramRect.height());
      j = 1;
      label105:
      i = j;
      if (j == 0) {
        break;
      }
      if (paramRect.left <= this.mDefaultFreeformStartX)
      {
        i = j;
        if (paramRect.top <= this.mDefaultFreeformStartY) {
          break;
        }
      }
      paramRect.set(this.mTmpOriginal);
    }
  }
  
  private void positionBottomLeft(TaskRecord paramTaskRecord, ArrayList<TaskRecord> paramArrayList, int paramInt1, int paramInt2)
  {
    this.mTmpProposal.set(this.mAvailableRect.left, this.mAvailableRect.bottom - paramInt2, this.mAvailableRect.left + paramInt1, this.mAvailableRect.bottom);
    position(paramTaskRecord, paramArrayList, this.mTmpProposal, false, 2);
  }
  
  private void positionBottomRight(TaskRecord paramTaskRecord, ArrayList<TaskRecord> paramArrayList, int paramInt1, int paramInt2)
  {
    this.mTmpProposal.set(this.mAvailableRect.right - paramInt1, this.mAvailableRect.bottom - paramInt2, this.mAvailableRect.right, this.mAvailableRect.bottom);
    position(paramTaskRecord, paramArrayList, this.mTmpProposal, false, 3);
  }
  
  private void positionCenter(TaskRecord paramTaskRecord, ArrayList<TaskRecord> paramArrayList, int paramInt1, int paramInt2)
  {
    this.mTmpProposal.set(this.mDefaultFreeformStartX, this.mDefaultFreeformStartY, this.mDefaultFreeformStartX + paramInt1, this.mDefaultFreeformStartY + paramInt2);
    position(paramTaskRecord, paramArrayList, this.mTmpProposal, true, 1);
  }
  
  private void positionTopLeft(TaskRecord paramTaskRecord, ArrayList<TaskRecord> paramArrayList, int paramInt1, int paramInt2)
  {
    this.mTmpProposal.set(this.mAvailableRect.left, this.mAvailableRect.top, this.mAvailableRect.left + paramInt1, this.mAvailableRect.top + paramInt2);
    position(paramTaskRecord, paramArrayList, this.mTmpProposal, false, 2);
  }
  
  private void positionTopRight(TaskRecord paramTaskRecord, ArrayList<TaskRecord> paramArrayList, int paramInt1, int paramInt2)
  {
    this.mTmpProposal.set(this.mAvailableRect.right - paramInt1, this.mAvailableRect.top, this.mAvailableRect.right, this.mAvailableRect.top + paramInt2);
    position(paramTaskRecord, paramArrayList, this.mTmpProposal, false, 3);
  }
  
  private void shiftStartingPoint(Rect paramRect, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      paramRect.offset(this.mDefaultFreeformStepHorizontal, this.mDefaultFreeformStepVertical);
      return;
    case 3: 
      paramRect.offset(-this.mDefaultFreeformStepHorizontal, 0);
      return;
    }
    paramRect.offset(this.mDefaultFreeformStepHorizontal, 0);
  }
  
  private boolean shiftedToFar(Rect paramRect, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      if ((paramRect.right > this.mAvailableRect.right) || (paramRect.bottom > this.mAvailableRect.bottom)) {
        return true;
      }
      break;
    case 3: 
      return paramRect.left < this.mAvailableRect.left;
    case 2: 
      return paramRect.right > this.mAvailableRect.right;
    }
    return false;
  }
  
  void configure(Rect paramRect)
  {
    if (paramRect == null) {
      this.mAvailableRect.set(0, 0, this.mDisplayWidth, this.mDisplayHeight);
    }
    for (;;)
    {
      int i = this.mAvailableRect.width();
      int j = this.mAvailableRect.height();
      this.mDefaultFreeformStartX = (this.mAvailableRect.left + i / 4);
      this.mDefaultFreeformStartY = (this.mAvailableRect.top + j / 4);
      this.mDefaultFreeformWidth = (i / 2);
      this.mDefaultFreeformHeight = (j / 2);
      this.mDefaultFreeformStepHorizontal = Math.max(i / 16, 1);
      this.mDefaultFreeformStepVertical = Math.max(j / 16, 1);
      this.mDefaultStartBoundsConfigurationSet = true;
      return;
      this.mAvailableRect.set(paramRect);
    }
  }
  
  void reset()
  {
    this.mDefaultStartBoundsConfigurationSet = false;
  }
  
  void setDisplay(Display paramDisplay)
  {
    Point localPoint = new Point();
    paramDisplay.getSize(localPoint);
    this.mDisplayWidth = localPoint.x;
    this.mDisplayHeight = localPoint.y;
  }
  
  void updateDefaultBounds(TaskRecord paramTaskRecord, ArrayList<TaskRecord> paramArrayList, ActivityInfo.WindowLayout paramWindowLayout)
  {
    if (!this.mDefaultStartBoundsConfigurationSet) {
      return;
    }
    if (paramWindowLayout == null)
    {
      positionCenter(paramTaskRecord, paramArrayList, this.mDefaultFreeformWidth, this.mDefaultFreeformHeight);
      return;
    }
    int i = getFinalWidth(paramWindowLayout);
    int j = getFinalHeight(paramWindowLayout);
    int k = paramWindowLayout.gravity & 0x70;
    int m = paramWindowLayout.gravity & 0x7;
    if (k == 48)
    {
      if (m == 5)
      {
        positionTopRight(paramTaskRecord, paramArrayList, i, j);
        return;
      }
      positionTopLeft(paramTaskRecord, paramArrayList, i, j);
      return;
    }
    if (k == 80)
    {
      if (m == 5)
      {
        positionBottomRight(paramTaskRecord, paramArrayList, i, j);
        return;
      }
      positionBottomLeft(paramTaskRecord, paramArrayList, i, j);
      return;
    }
    Slog.w(TAG, "Received unsupported gravity: " + paramWindowLayout.gravity + ", positioning in the center instead.");
    positionCenter(paramTaskRecord, paramArrayList, i, j);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/LaunchingTaskPositioner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */