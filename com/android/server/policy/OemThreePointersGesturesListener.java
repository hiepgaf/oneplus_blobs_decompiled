package com.android.server.policy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class OemThreePointersGesturesListener
  implements WindowManagerPolicy.PointerEventListener
{
  private static final boolean DEBUG = OemPhoneWindowManager.DEBUG;
  private static final int MAX_POINT_NUM = 200;
  private static final int MSG_OEM_PAUSE_DELIVER_POINTER = 3;
  private static final int MSG_OEM_RESUME_DELIVER_POINTER = 4;
  private static final int MSG_OEM_TAKE_SCREEN_SHOT = 5;
  private static final int SCREEN_SHOT_PRECISE = 5;
  private static final String TAG = "OemGestures";
  private ActivityManager mActivityManager;
  private Context mContext;
  private boolean mCurDown;
  private int mCurNumPointers;
  private int mCurrentUserId = 0;
  private int mFirstShotY = -1;
  private Handler mHandler;
  private boolean mIsShotScreenEnable = false;
  private Point mLast1ShotY;
  private Point mLast2ShotY;
  private int mMaxNumPointers;
  private ArrayList<Point> mPointers0 = new ArrayList();
  private ArrayList<Point> mPointers1 = new ArrayList();
  private ArrayList<Point> mPointers2 = new ArrayList();
  private int mScreenHeight = -1;
  int mScreenLength_1_3 = -1;
  private int mScreenWidth = -1;
  int mScreenWidth_1_3 = -1;
  
  public OemThreePointersGesturesListener(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    paramHandler = new DisplayMetrics();
    WindowManager localWindowManager = (WindowManager)this.mContext.getSystemService("window");
    this.mActivityManager = ((ActivityManager)paramContext.getSystemService("activity"));
    localWindowManager.getDefaultDisplay().getRealMetrics(paramHandler);
    this.mScreenLength_1_3 = (Math.max(paramHandler.widthPixels, paramHandler.heightPixels) / 3);
    this.mScreenWidth_1_3 = (Math.min(paramHandler.widthPixels, paramHandler.heightPixels) / 3);
    this.mScreenHeight = Math.max(paramHandler.widthPixels, paramHandler.heightPixels);
    this.mScreenWidth = Math.min(paramHandler.widthPixels, paramHandler.heightPixels);
    this.mContext.registerReceiver(new BroadcastReceiver()new IntentFilter
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        OemThreePointersGesturesListener.-set0(OemThreePointersGesturesListener.this, ActivityManager.getCurrentUser());
        Log.d("OemGestures", "mCurrentUserId = " + OemThreePointersGesturesListener.-get0(OemThreePointersGesturesListener.this));
      }
    }, new IntentFilter("android.intent.action.USER_SWITCHED"));
    this.mCurrentUserId = ActivityManager.getCurrentUser();
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
    int m = getAngle(this.mPointers2);
    int i = getDeltaAngle(k, j);
    j = getDeltaAngle(j, m);
    k = getDeltaAngle(k, m);
    if (i > j) {}
    while (i > k)
    {
      return i;
      i = j;
    }
    return k;
  }
  
  private int getScreenHeight_1_3()
  {
    if (2 == this.mContext.getResources().getConfiguration().orientation) {
      return this.mScreenWidth_1_3;
    }
    return this.mScreenLength_1_3;
  }
  
  private int getScreenWidth_1_3()
  {
    if (2 == this.mContext.getResources().getConfiguration().orientation) {
      return this.mScreenLength_1_3;
    }
    return this.mScreenWidth_1_3;
  }
  
  private void recordPointers(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
  {
    Point localPoint;
    if (paramInt2 == 3)
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
    } while (paramInt2 != 2);
    localPoint.x = ((int)paramMotionEvent.getX(paramInt1));
    localPoint.y = ((int)paramMotionEvent.getY(paramInt1));
    if (this.mPointers2.size() > 200) {
      this.mPointers2.remove(0);
    }
    this.mPointers2.add(localPoint);
  }
  
  private boolean shouldShot()
  {
    int j;
    int i;
    if (this.mPointers0.size() > 0)
    {
      j = this.mPointers0.size() - 1;
      i = j - 5;
      if (i <= 0) {
        break label73;
      }
      if (j <= 0) {
        break label78;
      }
    }
    for (;;)
    {
      this.mLast1ShotY = ((Point)this.mPointers0.get(i));
      this.mLast2ShotY = ((Point)this.mPointers0.get(j));
      if (getMaxDeltaAngle() < 30) {
        break label83;
      }
      return false;
      label73:
      i = 0;
      break;
      label78:
      j = 0;
    }
    label83:
    if ((shouldShotByPoints(this.mPointers0)) && (shouldShotByPoints(this.mPointers1))) {
      return shouldShotByPoints(this.mPointers2);
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
      Log.d("OemGestures", " maxPCY.y" + localPoint1.y + " minPCY.y" + ((Point)localObject1).y + "pointers" + paramArrayList.size());
    }
    Object localObject2 = new XComparator();
    Collections.sort(paramArrayList, (Comparator)localObject2);
    Point localPoint2 = (Point)Collections.max(paramArrayList, (Comparator)localObject2);
    localObject2 = (Point)Collections.min(paramArrayList, (Comparator)localObject2);
    if (DEBUG) {
      Log.d("OemGestures", " maxPCX.x" + localPoint2.x + " minPCX.x" + ((Point)localObject2).x + "pointers" + paramArrayList.size());
    }
    return (Math.abs(localPoint1.y - ((Point)localObject1).y) > getScreenHeight_1_3() / 2) && (Math.abs(localPoint2.x - ((Point)localObject2).x) < getScreenWidth_1_3() * 2);
  }
  
  public void onPointerEvent(MotionEvent paramMotionEvent)
  {
    if (this.mCurrentUserId != 0) {
      return;
    }
    if (!this.mIsShotScreenEnable) {
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
      }
      this.mCurNumPointers += 1;
      if (this.mCurNumPointers == 3)
      {
        if (this.mFirstShotY == -1) {
          this.mFirstShotY = ((int)paramMotionEvent.getY(0));
        }
        pauseDeliverPointer();
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
      resumeDeliverPointer();
      this.mCurDown = false;
      this.mCurNumPointers = 0;
      do
      {
        return;
      } while ((j & 0xFF) != 6);
    }
    if ((this.mCurNumPointers == 3) && (shouldShot())) {
      this.mHandler.sendEmptyMessage(5);
    }
    this.mCurNumPointers -= 1;
    this.mPointers0.clear();
    this.mPointers1.clear();
    this.mPointers2.clear();
  }
  
  public void pauseDeliverPointer()
  {
    if (DEBUG) {
      Log.i("OemGestures", "pauseDeliverPointer");
    }
    if (this.mHandler.hasMessages(4)) {
      this.mHandler.removeMessages(4);
    }
    if (!this.mHandler.hasMessages(3)) {
      this.mHandler.sendEmptyMessage(3);
    }
  }
  
  public void resumeDeliverPointer()
  {
    if (this.mHandler.hasMessages(3)) {
      this.mHandler.removeMessages(3);
    }
    if (!this.mHandler.hasMessages(4)) {
      this.mHandler.sendEmptyMessage(4);
    }
  }
  
  public void setScreenShotEnable(boolean paramBoolean)
  {
    this.mIsShotScreenEnable = paramBoolean;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/OemThreePointersGesturesListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */