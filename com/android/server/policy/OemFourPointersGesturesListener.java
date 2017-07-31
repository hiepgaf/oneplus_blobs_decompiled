package com.android.server.policy;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManagerPolicy.PointerEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OemFourPointersGesturesListener
  implements WindowManagerPolicy.PointerEventListener
{
  private static final boolean DEBUG = OemPhoneWindowManager.DEBUG;
  private static final int MAX_POINT_NUM = 200;
  private static final int MSG_OEM_TAKE_OPBUGREPORT = 6;
  private static final int SCREEN_SHOT_PRECISE = 5;
  private static final String TAG = "OemFourPointersGesturesListener";
  private Context mContext;
  private boolean mCurDown;
  private int mCurNumPointers;
  private int mFirstShotY = -1;
  private Handler mHandler;
  private boolean mIsOPBugreportEnable = false;
  private Point mLast1ShotY;
  private Point mLast2ShotY;
  private Point mLast3ShotY;
  private int mMaxNumPointers;
  private ArrayList<Point> mPointers0 = new ArrayList();
  private ArrayList<Point> mPointers1 = new ArrayList();
  private ArrayList<Point> mPointers2 = new ArrayList();
  private ArrayList<Point> mPointers3 = new ArrayList();
  private int mScreenHeight = -1;
  int mScreenLength_1_4 = -1;
  private int mScreenWidth = -1;
  int mScreenWidth_1_4 = -1;
  
  public OemFourPointersGesturesListener(Context paramContext, Handler paramHandler)
  {
    Log.d("OemFourPointersGesturesListener", "OemFourPointersGesturesListener IN");
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    paramContext = new DisplayMetrics();
    ((WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(paramContext);
    this.mScreenLength_1_4 = (Math.max(paramContext.widthPixels, paramContext.heightPixels) / 4);
    this.mScreenWidth_1_4 = (Math.min(paramContext.widthPixels, paramContext.heightPixels) / 4);
    this.mScreenHeight = Math.max(paramContext.widthPixels, paramContext.heightPixels);
    this.mScreenWidth = Math.min(paramContext.widthPixels, paramContext.heightPixels);
  }
  
  private static <T> T checkNull(String paramString, T paramT)
  {
    if (paramT == null) {
      throw new IllegalArgumentException(paramString + " must not be null");
    }
    return paramT;
  }
  
  private int getAngle(ArrayList<Point> paramArrayList)
  {
    if ((paramArrayList.size() < 0) || (paramArrayList.size() == 0)) {
      return 0;
    }
    int j = paramArrayList.size();
    int i = ((Point)paramArrayList.get(j - 1)).x - ((Point)paramArrayList.get(0)).x;
    j = ((Point)paramArrayList.get(j - 1)).y - ((Point)paramArrayList.get(0)).y;
    if (i == 0)
    {
      if (j >= 0) {
        return 90;
      }
      return -90;
    }
    double d2 = Math.toDegrees(Math.atan(j / i));
    double d1 = d2;
    if (i < 0) {
      d1 = d2 + 180.0D;
    }
    return (int)d1;
  }
  
  private int getDeltaAngle(int paramInt1, int paramInt2)
  {
    int i = 0;
    if (paramInt1 > paramInt2) {
      i = paramInt1 - paramInt2;
    }
    for (;;)
    {
      paramInt1 = i;
      if (i > 180) {
        paramInt1 = 360 - i;
      }
      return paramInt1;
      if (paramInt1 < paramInt2) {
        i = paramInt2 - paramInt1;
      }
    }
  }
  
  private int getMaxDeltaAngle()
  {
    int k = getAngle(this.mPointers0);
    int j = getAngle(this.mPointers1);
    int i1 = getAngle(this.mPointers2);
    int i2 = getAngle(this.mPointers3);
    int i = getDeltaAngle(k, j);
    int n = getDeltaAngle(j, i1);
    int m = getDeltaAngle(k, i1);
    k = getDeltaAngle(k, i2);
    j = getDeltaAngle(j, i2);
    i1 = getDeltaAngle(i1, i2);
    if (i > n)
    {
      if (i <= m) {
        break label125;
      }
      label101:
      if (i <= k) {
        break label131;
      }
      label106:
      if (i <= j) {
        break label136;
      }
    }
    for (;;)
    {
      if (i <= i1) {
        break label141;
      }
      return i;
      i = n;
      break;
      label125:
      i = m;
      break label101;
      label131:
      i = k;
      break label106;
      label136:
      i = j;
    }
    label141:
    return i1;
  }
  
  private int getScreenHeight_1_4()
  {
    if (2 == this.mContext.getResources().getConfiguration().orientation) {
      return this.mScreenWidth_1_4;
    }
    return this.mScreenLength_1_4;
  }
  
  private int getScreenWidth_1_4()
  {
    if (2 == this.mContext.getResources().getConfiguration().orientation) {
      return this.mScreenLength_1_4;
    }
    return this.mScreenWidth_1_4;
  }
  
  private void recordPointers(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
  {
    Point localPoint;
    if (paramInt2 == 4)
    {
      localPoint = new Point();
      paramInt2 = paramMotionEvent.getPointerId(paramInt1);
      if (paramInt2 != 0) {
        break label79;
      }
      localPoint.x = ((int)paramMotionEvent.getX(paramInt1));
      localPoint.y = ((int)paramMotionEvent.getY(paramInt1));
      if (this.mPointers0.size() > 200) {
        this.mPointers0.remove(0);
      }
      this.mPointers0.add(localPoint);
    }
    label79:
    do
    {
      return;
      if (paramInt2 == 1)
      {
        localPoint.x = ((int)paramMotionEvent.getX(paramInt1));
        localPoint.y = ((int)paramMotionEvent.getY(paramInt1));
        if (this.mPointers1.size() > 200) {
          this.mPointers1.remove(0);
        }
        this.mPointers1.add(localPoint);
        return;
      }
      if (paramInt2 == 2)
      {
        localPoint.x = ((int)paramMotionEvent.getX(paramInt1));
        localPoint.y = ((int)paramMotionEvent.getY(paramInt1));
        if (this.mPointers2.size() > 200) {
          this.mPointers2.remove(0);
        }
        this.mPointers2.add(localPoint);
        return;
      }
    } while (paramInt2 != 3);
    localPoint.x = ((int)paramMotionEvent.getX(paramInt1));
    localPoint.y = ((int)paramMotionEvent.getY(paramInt1));
    if (this.mPointers3.size() > 200) {
      this.mPointers3.remove(0);
    }
    this.mPointers3.add(localPoint);
  }
  
  private boolean shouldShot()
  {
    int k;
    int j;
    int i;
    if (this.mPointers0.size() > 0)
    {
      k = this.mPointers0.size() - 1;
      j = this.mPointers0.size() - 2;
      i = this.mPointers0.size() - 3;
      if (i <= 0) {
        break label108;
      }
      if (j <= 0) {
        break label113;
      }
      label48:
      if (k <= 0) {
        break label118;
      }
    }
    for (;;)
    {
      this.mLast1ShotY = ((Point)this.mPointers0.get(i));
      this.mLast2ShotY = ((Point)this.mPointers0.get(j));
      this.mLast3ShotY = ((Point)this.mPointers0.get(k));
      if (getMaxDeltaAngle() < 30) {
        break label123;
      }
      return false;
      label108:
      i = 0;
      break;
      label113:
      j = 0;
      break label48;
      label118:
      k = 0;
    }
    label123:
    if ((shouldShotByPoints(this.mPointers0)) && (shouldShotByPoints(this.mPointers1)) && (shouldShotByPoints(this.mPointers2))) {
      return shouldShotByPoints(this.mPointers3);
    }
    return false;
  }
  
  private boolean shouldShotByPoints(ArrayList<Point> paramArrayList)
  {
    if ((paramArrayList.size() < 0) || (paramArrayList.size() == 0)) {
      return false;
    }
    Object localObject1 = new YComparator();
    Collections.sort(paramArrayList, (Comparator)localObject1);
    Point localPoint1 = (Point)Collections.max(paramArrayList, (Comparator)localObject1);
    localObject1 = (Point)Collections.min(paramArrayList, (Comparator)localObject1);
    if (DEBUG) {
      Log.d("OemFourPointersGesturesListener", " maxPCY.y" + localPoint1.y + " minPCY.y" + ((Point)localObject1).y + "pointers" + paramArrayList.size());
    }
    Object localObject2 = new XComparator();
    Collections.sort(paramArrayList, (Comparator)localObject2);
    Point localPoint2 = (Point)Collections.max(paramArrayList, (Comparator)localObject2);
    localObject2 = (Point)Collections.min(paramArrayList, (Comparator)localObject2);
    if (DEBUG) {
      Log.d("OemFourPointersGesturesListener", " maxPCX.x" + localPoint2.x + " minPCX.x" + ((Point)localObject2).x + "pointers" + paramArrayList.size());
    }
    return (Math.abs(localPoint1.y - ((Point)localObject1).y) > getScreenHeight_1_4() / 2) && (Math.abs(localPoint2.x - ((Point)localObject2).x) < getScreenWidth_1_4() * 2);
  }
  
  public void onPointerEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mIsOPBugreportEnable) {
      return;
    }
    int j = paramMotionEvent.getAction();
    if ((j == 0) || ((j & 0xFF) == 5))
    {
      if (j == 0)
      {
        this.mCurDown = true;
        this.mCurNumPointers = 0;
        this.mMaxNumPointers = 0;
        this.mPointers0.clear();
        this.mPointers1.clear();
        this.mPointers2.clear();
        this.mPointers3.clear();
      }
      this.mCurNumPointers += 1;
      if ((this.mCurNumPointers == 4) && (this.mFirstShotY == -1)) {
        this.mFirstShotY = ((int)paramMotionEvent.getY(0));
      }
      if (this.mMaxNumPointers < this.mCurNumPointers) {
        this.mMaxNumPointers = this.mCurNumPointers;
      }
    }
    int k = paramMotionEvent.getPointerCount();
    int i = 0;
    while (i < k)
    {
      recordPointers(paramMotionEvent, i, this.mMaxNumPointers);
      i += 1;
    }
    if ((j == 1) || (j == 3)) {}
    while ((j == 1) || (j == 3))
    {
      this.mCurDown = false;
      this.mCurNumPointers = 0;
      do
      {
        return;
      } while ((j & 0xFF) != 6);
    }
    if ((this.mCurNumPointers == 4) && (shouldShot())) {
      this.mHandler.sendEmptyMessage(6);
    }
    this.mCurNumPointers -= 1;
    this.mPointers0.clear();
    this.mPointers1.clear();
    this.mPointers2.clear();
    this.mPointers3.clear();
  }
  
  public void setOPBugreporttEnable(boolean paramBoolean)
  {
    this.mIsOPBugreportEnable = paramBoolean;
  }
  
  class XComparator
    implements Comparator<Point>
  {
    XComparator() {}
    
    public int compare(Point paramPoint1, Point paramPoint2)
    {
      if (paramPoint1.x < paramPoint2.x) {
        return -1;
      }
      if (paramPoint1.x == paramPoint2.x) {
        return 0;
      }
      return 1;
    }
  }
  
  class YComparator
    implements Comparator<Point>
  {
    YComparator() {}
    
    public int compare(Point paramPoint1, Point paramPoint2)
    {
      if (paramPoint1.y < paramPoint2.y) {
        return -1;
      }
      if (paramPoint1.y == paramPoint2.y) {
        return 0;
      }
      return 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/OemFourPointersGesturesListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */