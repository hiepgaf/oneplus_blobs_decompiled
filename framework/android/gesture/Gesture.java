package android.gesture;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Gesture
  implements Parcelable
{
  private static final boolean BITMAP_RENDERING_ANTIALIAS = true;
  private static final boolean BITMAP_RENDERING_DITHER = true;
  private static final int BITMAP_RENDERING_WIDTH = 2;
  public static final Parcelable.Creator<Gesture> CREATOR = new Parcelable.Creator()
  {
    public Gesture createFromParcel(Parcel paramAnonymousParcel)
    {
      localObject = null;
      long l = paramAnonymousParcel.readLong();
      localDataInputStream = new DataInputStream(new ByteArrayInputStream(paramAnonymousParcel.createByteArray()));
      try
      {
        paramAnonymousParcel = Gesture.deserialize(localDataInputStream);
      }
      catch (IOException paramAnonymousParcel)
      {
        for (;;)
        {
          Log.e("Gestures", "Error reading Gesture from parcel:", paramAnonymousParcel);
          GestureUtils.closeStream(localDataInputStream);
          paramAnonymousParcel = (Parcel)localObject;
        }
      }
      finally
      {
        GestureUtils.closeStream(localDataInputStream);
      }
      if (paramAnonymousParcel != null) {
        Gesture.-set0(paramAnonymousParcel, l);
      }
      return paramAnonymousParcel;
    }
    
    public Gesture[] newArray(int paramAnonymousInt)
    {
      return new Gesture[paramAnonymousInt];
    }
  };
  private static final long GESTURE_ID_BASE = ;
  private static final AtomicInteger sGestureCount = new AtomicInteger(0);
  private final RectF mBoundingBox = new RectF();
  private long mGestureID = GESTURE_ID_BASE + sGestureCount.incrementAndGet();
  private final ArrayList<GestureStroke> mStrokes = new ArrayList();
  
  static Gesture deserialize(DataInputStream paramDataInputStream)
    throws IOException
  {
    Gesture localGesture = new Gesture();
    localGesture.mGestureID = paramDataInputStream.readLong();
    int j = paramDataInputStream.readInt();
    int i = 0;
    while (i < j)
    {
      localGesture.addStroke(GestureStroke.deserialize(paramDataInputStream));
      i += 1;
    }
    return localGesture;
  }
  
  public void addStroke(GestureStroke paramGestureStroke)
  {
    this.mStrokes.add(paramGestureStroke);
    this.mBoundingBox.union(paramGestureStroke.boundingBox);
  }
  
  public Object clone()
  {
    Gesture localGesture = new Gesture();
    localGesture.mBoundingBox.set(this.mBoundingBox.left, this.mBoundingBox.top, this.mBoundingBox.right, this.mBoundingBox.bottom);
    int j = this.mStrokes.size();
    int i = 0;
    while (i < j)
    {
      GestureStroke localGestureStroke = (GestureStroke)this.mStrokes.get(i);
      localGesture.mStrokes.add((GestureStroke)localGestureStroke.clone());
      i += 1;
    }
    return localGesture;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public RectF getBoundingBox()
  {
    return this.mBoundingBox;
  }
  
  public long getID()
  {
    return this.mGestureID;
  }
  
  public float getLength()
  {
    int j = 0;
    ArrayList localArrayList = this.mStrokes;
    int k = localArrayList.size();
    int i = 0;
    while (i < k)
    {
      float f = j;
      j = (int)(((GestureStroke)localArrayList.get(i)).length + f);
      i += 1;
    }
    return j;
  }
  
  public ArrayList<GestureStroke> getStrokes()
  {
    return this.mStrokes;
  }
  
  public int getStrokesCount()
  {
    return this.mStrokes.size();
  }
  
  void serialize(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    ArrayList localArrayList = this.mStrokes;
    int j = localArrayList.size();
    paramDataOutputStream.writeLong(this.mGestureID);
    paramDataOutputStream.writeInt(j);
    int i = 0;
    while (i < j)
    {
      ((GestureStroke)localArrayList.get(i)).serialize(paramDataOutputStream);
      i += 1;
    }
  }
  
  void setID(long paramLong)
  {
    this.mGestureID = paramLong;
  }
  
  public Bitmap toBitmap(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint();
    localPaint.setAntiAlias(true);
    localPaint.setDither(true);
    localPaint.setColor(paramInt4);
    localPaint.setStyle(Paint.Style.STROKE);
    localPaint.setStrokeJoin(Paint.Join.ROUND);
    localPaint.setStrokeCap(Paint.Cap.ROUND);
    localPaint.setStrokeWidth(2.0F);
    Path localPath = toPath();
    RectF localRectF = new RectF();
    localPath.computeBounds(localRectF, true);
    float f1 = (paramInt1 - paramInt3 * 2) / localRectF.width();
    float f2 = (paramInt2 - paramInt3 * 2) / localRectF.height();
    if (f1 > f2) {
      f1 = f2;
    }
    for (;;)
    {
      localPaint.setStrokeWidth(2.0F / f1);
      localPath.offset(-localRectF.left + (paramInt1 - localRectF.width() * f1) / 2.0F, -localRectF.top + (paramInt2 - localRectF.height() * f1) / 2.0F);
      localCanvas.translate(paramInt3, paramInt3);
      localCanvas.scale(f1, f1);
      localCanvas.drawPath(localPath, localPaint);
      return localBitmap;
    }
  }
  
  public Bitmap toBitmap(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    localCanvas.translate(paramInt3, paramInt3);
    Paint localPaint = new Paint();
    localPaint.setAntiAlias(true);
    localPaint.setDither(true);
    localPaint.setColor(paramInt5);
    localPaint.setStyle(Paint.Style.STROKE);
    localPaint.setStrokeJoin(Paint.Join.ROUND);
    localPaint.setStrokeCap(Paint.Cap.ROUND);
    localPaint.setStrokeWidth(2.0F);
    ArrayList localArrayList = this.mStrokes;
    int i = localArrayList.size();
    paramInt5 = 0;
    while (paramInt5 < i)
    {
      localCanvas.drawPath(((GestureStroke)localArrayList.get(paramInt5)).toPath(paramInt1 - paramInt3 * 2, paramInt2 - paramInt3 * 2, paramInt4), localPaint);
      paramInt5 += 1;
    }
    return localBitmap;
  }
  
  public Path toPath()
  {
    return toPath(null);
  }
  
  public Path toPath(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return toPath(null, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public Path toPath(Path paramPath)
  {
    Path localPath = paramPath;
    if (paramPath == null) {
      localPath = new Path();
    }
    paramPath = this.mStrokes;
    int j = paramPath.size();
    int i = 0;
    while (i < j)
    {
      localPath.addPath(((GestureStroke)paramPath.get(i)).getPath());
      i += 1;
    }
    return localPath;
  }
  
  public Path toPath(Path paramPath, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Path localPath = paramPath;
    if (paramPath == null) {
      localPath = new Path();
    }
    paramPath = this.mStrokes;
    int j = paramPath.size();
    int i = 0;
    while (i < j)
    {
      localPath.addPath(((GestureStroke)paramPath.get(i)).toPath(paramInt1 - paramInt3 * 2, paramInt2 - paramInt3 * 2, paramInt4));
      i += 1;
    }
    return localPath;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.mGestureID);
    paramInt = 0;
    localByteArrayOutputStream = new ByteArrayOutputStream(32768);
    localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    try
    {
      serialize(localDataOutputStream);
      paramInt = 1;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Log.e("Gestures", "Error writing Gesture to parcel:", localIOException);
        GestureUtils.closeStream(localDataOutputStream);
        GestureUtils.closeStream(localByteArrayOutputStream);
      }
    }
    finally
    {
      GestureUtils.closeStream(localDataOutputStream);
      GestureUtils.closeStream(localByteArrayOutputStream);
    }
    if (paramInt != 0) {
      paramParcel.writeByteArray(localByteArrayOutputStream.toByteArray());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/Gesture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */