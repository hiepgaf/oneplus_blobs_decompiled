package android.accessibilityservice;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import java.util.ArrayList;
import java.util.List;

public final class GestureDescription
{
  private static final long MAX_GESTURE_DURATION_MS = 60000L;
  private static final int MAX_STROKE_COUNT = 10;
  private final List<StrokeDescription> mStrokes = new ArrayList();
  private final float[] mTempPos = new float[2];
  
  private GestureDescription() {}
  
  private GestureDescription(List<StrokeDescription> paramList)
  {
    this.mStrokes.addAll(paramList);
  }
  
  public static long getMaxGestureDuration()
  {
    return 60000L;
  }
  
  public static int getMaxStrokeCount()
  {
    return 10;
  }
  
  private long getNextKeyPointAtLeast(long paramLong)
  {
    long l1 = Long.MAX_VALUE;
    int i = 0;
    while (i < this.mStrokes.size())
    {
      long l3 = ((StrokeDescription)this.mStrokes.get(i)).mStartTime;
      long l2 = l1;
      if (l3 < l1)
      {
        l2 = l1;
        if (l3 >= paramLong) {
          l2 = l3;
        }
      }
      l3 = ((StrokeDescription)this.mStrokes.get(i)).mEndTime;
      l1 = l2;
      if (l3 < l2)
      {
        l1 = l2;
        if (l3 >= paramLong) {
          l1 = l3;
        }
      }
      i += 1;
    }
    paramLong = l1;
    if (l1 == Long.MAX_VALUE) {
      paramLong = -1L;
    }
    return paramLong;
  }
  
  private int getPointsForTime(long paramLong, TouchPoint[] paramArrayOfTouchPoint)
  {
    int j = 0;
    int i = 0;
    if (i < this.mStrokes.size())
    {
      StrokeDescription localStrokeDescription = (StrokeDescription)this.mStrokes.get(i);
      int k = j;
      TouchPoint localTouchPoint;
      if (localStrokeDescription.hasPointForTime(paramLong))
      {
        paramArrayOfTouchPoint[j].mPathIndex = i;
        localTouchPoint = paramArrayOfTouchPoint[j];
        if (paramLong != localStrokeDescription.mStartTime) {
          break label174;
        }
        bool = true;
        label77:
        localTouchPoint.mIsStartOfPath = bool;
        localTouchPoint = paramArrayOfTouchPoint[j];
        if (paramLong != localStrokeDescription.mEndTime) {
          break label180;
        }
      }
      label174:
      label180:
      for (boolean bool = true;; bool = false)
      {
        localTouchPoint.mIsEndOfPath = bool;
        localStrokeDescription.getPosForTime(paramLong, this.mTempPos);
        paramArrayOfTouchPoint[j].mX = Math.round(this.mTempPos[0]);
        paramArrayOfTouchPoint[j].mY = Math.round(this.mTempPos[1]);
        k = j + 1;
        i += 1;
        j = k;
        break;
        bool = false;
        break label77;
      }
    }
    return j;
  }
  
  private static long getTotalDuration(List<StrokeDescription> paramList)
  {
    long l = Long.MIN_VALUE;
    int i = 0;
    while (i < paramList.size())
    {
      l = Math.max(l, ((StrokeDescription)paramList.get(i)).mEndTime);
      i += 1;
    }
    return Math.max(l, 0L);
  }
  
  public StrokeDescription getStroke(int paramInt)
  {
    return (StrokeDescription)this.mStrokes.get(paramInt);
  }
  
  public int getStrokeCount()
  {
    return this.mStrokes.size();
  }
  
  public static class Builder
  {
    private final List<GestureDescription.StrokeDescription> mStrokes = new ArrayList();
    
    public Builder addStroke(GestureDescription.StrokeDescription paramStrokeDescription)
    {
      if (this.mStrokes.size() >= 10) {
        throw new IllegalStateException("Attempting to add too many strokes to a gesture");
      }
      this.mStrokes.add(paramStrokeDescription);
      if (GestureDescription.-wrap2(this.mStrokes) > 60000L)
      {
        this.mStrokes.remove(paramStrokeDescription);
        throw new IllegalStateException("Gesture would exceed maximum duration with new stroke");
      }
      return this;
    }
    
    public GestureDescription build()
    {
      if (this.mStrokes.size() == 0) {
        throw new IllegalStateException("Gestures must have at least one stroke");
      }
      return new GestureDescription(this.mStrokes, null);
    }
  }
  
  public static class GestureStep
    implements Parcelable
  {
    public static final Parcelable.Creator<GestureStep> CREATOR = new Parcelable.Creator()
    {
      public GestureDescription.GestureStep createFromParcel(Parcel paramAnonymousParcel)
      {
        return new GestureDescription.GestureStep(paramAnonymousParcel);
      }
      
      public GestureDescription.GestureStep[] newArray(int paramAnonymousInt)
      {
        return new GestureDescription.GestureStep[paramAnonymousInt];
      }
    };
    public int numTouchPoints;
    public long timeSinceGestureStart;
    public GestureDescription.TouchPoint[] touchPoints;
    
    public GestureStep(long paramLong, int paramInt, GestureDescription.TouchPoint[] paramArrayOfTouchPoint)
    {
      this.timeSinceGestureStart = paramLong;
      this.numTouchPoints = paramInt;
      this.touchPoints = new GestureDescription.TouchPoint[paramInt];
      int i = 0;
      while (i < paramInt)
      {
        this.touchPoints[i] = new GestureDescription.TouchPoint(paramArrayOfTouchPoint[i]);
        i += 1;
      }
    }
    
    public GestureStep(Parcel paramParcel)
    {
      this.timeSinceGestureStart = paramParcel.readLong();
      paramParcel = paramParcel.readParcelableArray(GestureDescription.TouchPoint.class.getClassLoader());
      if (paramParcel == null) {}
      for (int i = 0;; i = paramParcel.length)
      {
        this.numTouchPoints = i;
        this.touchPoints = new GestureDescription.TouchPoint[this.numTouchPoints];
        i = 0;
        while (i < this.numTouchPoints)
        {
          this.touchPoints[i] = ((GestureDescription.TouchPoint)paramParcel[i]);
          i += 1;
        }
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.timeSinceGestureStart);
      paramParcel.writeParcelableArray(this.touchPoints, paramInt);
    }
  }
  
  public static class MotionEventGenerator
  {
    private static final int EVENT_BUTTON_STATE = 0;
    private static final int EVENT_DEVICE_ID = 0;
    private static final int EVENT_EDGE_FLAGS = 0;
    private static final int EVENT_FLAGS = 0;
    private static final int EVENT_META_STATE = 0;
    private static final int EVENT_SOURCE = 4098;
    private static final float EVENT_X_PRECISION = 1.0F;
    private static final float EVENT_Y_PRECISION = 1.0F;
    private static GestureDescription.TouchPoint[] sCurrentTouchPoints;
    private static GestureDescription.TouchPoint[] sLastTouchPoints;
    private static MotionEvent.PointerCoords[] sPointerCoords;
    private static MotionEvent.PointerProperties[] sPointerProps;
    
    private static int appendDownEvents(List<MotionEvent> paramList, GestureDescription.TouchPoint[] paramArrayOfTouchPoint1, int paramInt1, GestureDescription.TouchPoint[] paramArrayOfTouchPoint2, int paramInt2, long paramLong)
    {
      int i = 0;
      label43:
      long l;
      if (i < paramInt2)
      {
        if (!paramArrayOfTouchPoint2[i].mIsStartOfPath) {
          break label119;
        }
        int j = paramInt1 + 1;
        paramArrayOfTouchPoint1[paramInt1].copyFrom(paramArrayOfTouchPoint2[i]);
        if (j == 1)
        {
          paramInt1 = 0;
          if (paramInt1 != 0) {
            break label92;
          }
          l = paramLong;
          label51:
          paramList.add(obtainMotionEvent(l, paramLong, paramInt1 | i << 8, paramArrayOfTouchPoint1, j));
          paramInt1 = j;
        }
      }
      label92:
      label119:
      for (;;)
      {
        i += 1;
        break;
        paramInt1 = 5;
        break label43;
        l = ((MotionEvent)paramList.get(paramList.size() - 1)).getDownTime();
        break label51;
        return paramInt1;
      }
    }
    
    private static void appendMoveEventIfNeeded(List<MotionEvent> paramList, GestureDescription.TouchPoint[] paramArrayOfTouchPoint1, int paramInt1, GestureDescription.TouchPoint[] paramArrayOfTouchPoint2, int paramInt2, long paramLong)
    {
      int k = 0;
      int j = 0;
      if (j < paramInt2)
      {
        int m = findPointByPathIndex(paramArrayOfTouchPoint1, paramInt1, paramArrayOfTouchPoint2[j].mPathIndex);
        int i = k;
        if (m >= 0)
        {
          if (paramArrayOfTouchPoint1[m].mX != paramArrayOfTouchPoint2[j].mX) {
            break label106;
          }
          if (paramArrayOfTouchPoint1[m].mY == paramArrayOfTouchPoint2[j].mY) {
            break label112;
          }
          i = 1;
        }
        for (;;)
        {
          i = k | i;
          paramArrayOfTouchPoint1[m].copyFrom(paramArrayOfTouchPoint2[j]);
          j += 1;
          k = i;
          break;
          label106:
          i = 1;
          continue;
          label112:
          i = 0;
        }
      }
      if (k != 0) {
        paramList.add(obtainMotionEvent(((MotionEvent)paramList.get(paramList.size() - 1)).getDownTime(), paramLong, 2, paramArrayOfTouchPoint1, paramInt1));
      }
    }
    
    private static int appendUpEvents(List<MotionEvent> paramList, GestureDescription.TouchPoint[] paramArrayOfTouchPoint1, int paramInt1, GestureDescription.TouchPoint[] paramArrayOfTouchPoint2, int paramInt2, long paramLong)
    {
      int j = 0;
      int i = paramInt1;
      paramInt1 = j;
      if (paramInt1 < paramInt2)
      {
        j = i;
        int k;
        if (paramArrayOfTouchPoint2[paramInt1].mIsEndOfPath)
        {
          k = findPointByPathIndex(paramArrayOfTouchPoint1, i, paramArrayOfTouchPoint2[paramInt1].mPathIndex);
          if (k >= 0) {
            break label62;
          }
        }
        for (j = i;; j = i - 1)
        {
          paramInt1 += 1;
          i = j;
          break;
          label62:
          long l = ((MotionEvent)paramList.get(paramList.size() - 1)).getDownTime();
          if (i == 1) {}
          for (j = 1;; j = 6)
          {
            paramList.add(obtainMotionEvent(l, paramLong, j | k << 8, paramArrayOfTouchPoint1, i));
            j = k;
            while (j < i - 1)
            {
              paramArrayOfTouchPoint1[j].copyFrom(paramArrayOfTouchPoint1[(j + 1)]);
              j += 1;
            }
          }
        }
      }
      return i;
    }
    
    private static int findPointByPathIndex(GestureDescription.TouchPoint[] paramArrayOfTouchPoint, int paramInt1, int paramInt2)
    {
      int i = 0;
      while (i < paramInt1)
      {
        if (paramArrayOfTouchPoint[i].mPathIndex == paramInt2) {
          return i;
        }
        i += 1;
      }
      return -1;
    }
    
    private static GestureDescription.TouchPoint[] getCurrentTouchPoints(int paramInt)
    {
      if ((sCurrentTouchPoints == null) || (sCurrentTouchPoints.length < paramInt))
      {
        sCurrentTouchPoints = new GestureDescription.TouchPoint[paramInt];
        int i = 0;
        while (i < paramInt)
        {
          sCurrentTouchPoints[i] = new GestureDescription.TouchPoint();
          i += 1;
        }
      }
      return sCurrentTouchPoints;
    }
    
    static List<GestureDescription.GestureStep> getGestureStepsFromGestureDescription(GestureDescription paramGestureDescription, int paramInt)
    {
      ArrayList localArrayList = new ArrayList();
      GestureDescription.TouchPoint[] arrayOfTouchPoint = getCurrentTouchPoints(paramGestureDescription.getStrokeCount());
      int i = 0;
      long l2 = 0L;
      long l1 = GestureDescription.-wrap1(paramGestureDescription, 0L);
      if (l1 >= 0L)
      {
        if (i == 0) {}
        for (l2 = l1;; l2 = Math.min(l1, paramInt + l2))
        {
          i = GestureDescription.-wrap0(paramGestureDescription, l2, arrayOfTouchPoint);
          localArrayList.add(new GestureDescription.GestureStep(l2, i, arrayOfTouchPoint));
          l1 = GestureDescription.-wrap1(paramGestureDescription, 1L + l2);
          break;
        }
      }
      return localArrayList;
    }
    
    private static GestureDescription.TouchPoint[] getLastTouchPoints(int paramInt)
    {
      if ((sLastTouchPoints == null) || (sLastTouchPoints.length < paramInt))
      {
        sLastTouchPoints = new GestureDescription.TouchPoint[paramInt];
        int i = 0;
        while (i < paramInt)
        {
          sLastTouchPoints[i] = new GestureDescription.TouchPoint();
          i += 1;
        }
      }
      return sLastTouchPoints;
    }
    
    public static List<MotionEvent> getMotionEventsFromGestureSteps(List<GestureDescription.GestureStep> paramList)
    {
      ArrayList localArrayList = new ArrayList();
      int j = 0;
      int i = 0;
      while (i < paramList.size())
      {
        GestureDescription.GestureStep localGestureStep = (GestureDescription.GestureStep)paramList.get(i);
        int k = localGestureStep.numTouchPoints;
        GestureDescription.TouchPoint[] arrayOfTouchPoint = getLastTouchPoints(Math.max(j, k));
        appendMoveEventIfNeeded(localArrayList, arrayOfTouchPoint, j, localGestureStep.touchPoints, k, localGestureStep.timeSinceGestureStart);
        j = appendDownEvents(localArrayList, arrayOfTouchPoint, appendUpEvents(localArrayList, arrayOfTouchPoint, j, localGestureStep.touchPoints, k, localGestureStep.timeSinceGestureStart), localGestureStep.touchPoints, k, localGestureStep.timeSinceGestureStart);
        i += 1;
      }
      return localArrayList;
    }
    
    private static MotionEvent.PointerCoords[] getPointerCoords(int paramInt)
    {
      if ((sPointerCoords == null) || (sPointerCoords.length < paramInt))
      {
        sPointerCoords = new MotionEvent.PointerCoords[paramInt];
        int i = 0;
        while (i < paramInt)
        {
          sPointerCoords[i] = new MotionEvent.PointerCoords();
          i += 1;
        }
      }
      return sPointerCoords;
    }
    
    private static MotionEvent.PointerProperties[] getPointerProps(int paramInt)
    {
      if ((sPointerProps == null) || (sPointerProps.length < paramInt))
      {
        sPointerProps = new MotionEvent.PointerProperties[paramInt];
        int i = 0;
        while (i < paramInt)
        {
          sPointerProps[i] = new MotionEvent.PointerProperties();
          i += 1;
        }
      }
      return sPointerProps;
    }
    
    private static MotionEvent obtainMotionEvent(long paramLong1, long paramLong2, int paramInt1, GestureDescription.TouchPoint[] paramArrayOfTouchPoint, int paramInt2)
    {
      MotionEvent.PointerCoords[] arrayOfPointerCoords = getPointerCoords(paramInt2);
      MotionEvent.PointerProperties[] arrayOfPointerProperties = getPointerProps(paramInt2);
      int i = 0;
      while (i < paramInt2)
      {
        arrayOfPointerProperties[i].id = paramArrayOfTouchPoint[i].mPathIndex;
        arrayOfPointerProperties[i].toolType = 0;
        arrayOfPointerCoords[i].clear();
        arrayOfPointerCoords[i].pressure = 1.0F;
        arrayOfPointerCoords[i].size = 1.0F;
        arrayOfPointerCoords[i].x = paramArrayOfTouchPoint[i].mX;
        arrayOfPointerCoords[i].y = paramArrayOfTouchPoint[i].mY;
        i += 1;
      }
      return MotionEvent.obtain(paramLong1, paramLong2, paramInt1, paramInt2, arrayOfPointerProperties, arrayOfPointerCoords, 0, 0, 1.0F, 1.0F, 0, 0, 4098, 0);
    }
  }
  
  public static class StrokeDescription
  {
    long mEndTime;
    Path mPath;
    private PathMeasure mPathMeasure;
    long mStartTime;
    float[] mTapLocation;
    private float mTimeToLengthConversion;
    
    public StrokeDescription(Path paramPath, long paramLong1, long paramLong2)
    {
      if (paramLong2 <= 0L) {
        throw new IllegalArgumentException("Duration must be positive");
      }
      if (paramLong1 < 0L) {
        throw new IllegalArgumentException("Start time must not be negative");
      }
      RectF localRectF = new RectF();
      paramPath.computeBounds(localRectF, false);
      if ((localRectF.bottom < 0.0F) || (localRectF.top < 0.0F)) {}
      while ((localRectF.right < 0.0F) || (localRectF.left < 0.0F)) {
        throw new IllegalArgumentException("Path bounds must not be negative");
      }
      if (paramPath.isEmpty()) {
        throw new IllegalArgumentException("Path is empty");
      }
      this.mPath = new Path(paramPath);
      this.mPathMeasure = new PathMeasure(paramPath, false);
      if (this.mPathMeasure.getLength() == 0.0F)
      {
        paramPath = new Path(paramPath);
        paramPath.lineTo(-1.0F, -1.0F);
        this.mTapLocation = new float[2];
        new PathMeasure(paramPath, false).getPosTan(0.0F, this.mTapLocation, null);
      }
      if (this.mPathMeasure.nextContour()) {
        throw new IllegalArgumentException("Path has more than one contour");
      }
      this.mPathMeasure.setPath(this.mPath, false);
      this.mStartTime = paramLong1;
      this.mEndTime = (paramLong1 + paramLong2);
      this.mTimeToLengthConversion = (getLength() / (float)paramLong2);
    }
    
    public long getDuration()
    {
      return this.mEndTime - this.mStartTime;
    }
    
    float getLength()
    {
      return this.mPathMeasure.getLength();
    }
    
    public Path getPath()
    {
      return new Path(this.mPath);
    }
    
    boolean getPosForTime(long paramLong, float[] paramArrayOfFloat)
    {
      if (this.mTapLocation != null)
      {
        paramArrayOfFloat[0] = this.mTapLocation[0];
        paramArrayOfFloat[1] = this.mTapLocation[1];
        return true;
      }
      if (paramLong == this.mEndTime) {
        return this.mPathMeasure.getPosTan(getLength(), paramArrayOfFloat, null);
      }
      float f1 = this.mTimeToLengthConversion;
      float f2 = (float)(paramLong - this.mStartTime);
      return this.mPathMeasure.getPosTan(f1 * f2, paramArrayOfFloat, null);
    }
    
    public long getStartTime()
    {
      return this.mStartTime;
    }
    
    boolean hasPointForTime(long paramLong)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (paramLong >= this.mStartTime)
      {
        bool1 = bool2;
        if (paramLong <= this.mEndTime) {
          bool1 = true;
        }
      }
      return bool1;
    }
  }
  
  public static class TouchPoint
    implements Parcelable
  {
    public static final Parcelable.Creator<TouchPoint> CREATOR = new Parcelable.Creator()
    {
      public GestureDescription.TouchPoint createFromParcel(Parcel paramAnonymousParcel)
      {
        return new GestureDescription.TouchPoint(paramAnonymousParcel);
      }
      
      public GestureDescription.TouchPoint[] newArray(int paramAnonymousInt)
      {
        return new GestureDescription.TouchPoint[paramAnonymousInt];
      }
    };
    private static final int FLAG_IS_END_OF_PATH = 2;
    private static final int FLAG_IS_START_OF_PATH = 1;
    boolean mIsEndOfPath;
    boolean mIsStartOfPath;
    int mPathIndex;
    float mX;
    float mY;
    
    public TouchPoint() {}
    
    public TouchPoint(TouchPoint paramTouchPoint)
    {
      copyFrom(paramTouchPoint);
    }
    
    public TouchPoint(Parcel paramParcel)
    {
      this.mPathIndex = paramParcel.readInt();
      int i = paramParcel.readInt();
      if ((i & 0x1) != 0)
      {
        bool1 = true;
        this.mIsStartOfPath = bool1;
        if ((i & 0x2) == 0) {
          break label69;
        }
      }
      label69:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.mIsEndOfPath = bool1;
        this.mX = paramParcel.readFloat();
        this.mY = paramParcel.readFloat();
        return;
        bool1 = false;
        break;
      }
    }
    
    void copyFrom(TouchPoint paramTouchPoint)
    {
      this.mPathIndex = paramTouchPoint.mPathIndex;
      this.mIsStartOfPath = paramTouchPoint.mIsStartOfPath;
      this.mIsEndOfPath = paramTouchPoint.mIsEndOfPath;
      this.mX = paramTouchPoint.mX;
      this.mY = paramTouchPoint.mY;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mPathIndex);
      if (this.mIsStartOfPath)
      {
        paramInt = 1;
        if (!this.mIsEndOfPath) {
          break label55;
        }
      }
      label55:
      for (int i = 2;; i = 0)
      {
        paramParcel.writeInt(paramInt | i);
        paramParcel.writeFloat(this.mX);
        paramParcel.writeFloat(this.mY);
        return;
        paramInt = 0;
        break;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accessibilityservice/GestureDescription.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */