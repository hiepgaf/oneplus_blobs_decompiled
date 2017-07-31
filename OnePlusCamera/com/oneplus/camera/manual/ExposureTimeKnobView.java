package com.oneplus.camera.manual;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Range;
import android.util.Rational;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.drawable.ShadowTextDrawable;
import java.util.ArrayList;
import java.util.List;

public class ExposureTimeKnobView
  extends KnobView
{
  private static final int COUNT_PER_INTERVAL_MAX = 9;
  private static final int COUNT_PER_INTERVAL_MIN = 5;
  private static final int DEGREE_PER_INTERVAL = 25;
  public static final String[] EXPOSURE_TIME_CANDIDATES = { "1/8000", "1/6400", "1/5000", "1/4000", "1/3200", "1/2500", "1/2000", "1/1600", "1/1250", "1/1000", "1/800", "1/640", "1/500", "1/400", "1/320", "1/250", "1/200", "1/160", "1/125", "1/100", "1/80", "1/60", "1/50", "1/40", "1/30", "1/25", "1/20", "1/15", "1/13", "1/10", "1/8", "1/6", "1/5", "1/4", "1/3", "0.4", "0.5", "0.6", "0.8", "1", "1.3", "1.6", "2", "2.5", "3", "4", "5", "6", "8", "10", "13", "15", "20", "25", "30" };
  private static final int INDICATOR_COUNT_MAX = 7;
  
  public ExposureTimeKnobView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ExposureTimeKnobView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private int findPreferredIntervalCount(int paramInt)
  {
    int k = 9;
    int j = Integer.MAX_VALUE;
    int i = 9;
    for (;;)
    {
      if ((i < 5) || ((paramInt - 1) / i + 1.0F > 7.0F)) {
        return k;
      }
      int n = (paramInt % i + (i - 1)) % i;
      int m = j;
      if (j > n)
      {
        m = n;
        k = i;
      }
      i -= 1;
      j = m;
    }
  }
  
  private int findPreferredKnobViewAngle(int paramInt)
  {
    return (paramInt - 1) * 25;
  }
  
  protected boolean onSetupIcons()
  {
    CameraActivity localCameraActivity = (CameraActivity)getContext();
    setIconPadding(localCameraActivity.getResources().getDimensionPixelSize(2131296549));
    Object localObject1 = (Camera)localCameraActivity.get(CameraActivity.PROP_CAMERA);
    if (localObject1 == null) {
      return false;
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject4 = (Range)((Camera)localObject1).get(Camera.PROP_EXPOSURE_TIME_NANOS_RANGE);
    if ((localObject4 == null) || ((((Long)((Range)localObject4).getLower()).longValue() == 0L) && (((Long)((Range)localObject4).getUpper()).longValue() == 0L))) {
      return false;
    }
    localObject1 = new ShadowTextDrawable();
    ((ShadowTextDrawable)localObject1).setText(localCameraActivity.getString(2131558543));
    ((ShadowTextDrawable)localObject1).setTextAppearance(localCameraActivity, 2131492919);
    Object localObject2 = new ShadowTextDrawable();
    ((ShadowTextDrawable)localObject2).setText(localCameraActivity.getString(2131558543));
    ((ShadowTextDrawable)localObject2).setTextAppearance(localCameraActivity, 2131492920);
    Object localObject3 = new StateListDrawable();
    ((StateListDrawable)localObject3).addState(new int[] { -16842913 }, (Drawable)localObject1);
    ((StateListDrawable)localObject3).addState(SELECTED_STATE_SET, (Drawable)localObject2);
    localArrayList.add(new KnobItemInfo((Drawable)localObject3, localCameraActivity.getString(2131558543), 0, -1.0D));
    localObject2 = new ArrayList();
    localObject3 = new ArrayList();
    int i = 0;
    Object localObject5;
    if (i < EXPOSURE_TIME_CANDIDATES.length)
    {
      localObject5 = EXPOSURE_TIME_CANDIDATES[i];
      if (((String)localObject5).contains("/"))
      {
        localObject1 = Long.valueOf((Rational.parseRational((String)localObject5).doubleValue() * 1000.0D * 1000.0D * 1000.0D));
        label299:
        if ((((Long)localObject1).longValue() >= ((Long)((Range)localObject4).getLower()).longValue()) && (((Long)localObject1).longValue() <= ((Long)((Range)localObject4).getUpper()).longValue())) {
          break label372;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        localObject1 = Long.valueOf((Double.parseDouble((String)localObject5) * 1000.0D * 1000.0D * 1000.0D));
        break label299;
        label372:
        ((List)localObject2).add(localObject5);
        ((List)localObject3).add(localObject1);
      }
    }
    int j = 0;
    int n = findPreferredIntervalCount(((List)localObject2).size());
    i = 0;
    if (i < ((List)localObject2).size())
    {
      if (i == ((List)localObject2).size() - 1) {}
      for (int m = 1;; m = 0)
      {
        localObject1 = new ShadowTextDrawable();
        ((ShadowTextDrawable)localObject1).setTextAppearance(localCameraActivity, 2131492919);
        localObject4 = new ShadowTextDrawable();
        ((ShadowTextDrawable)localObject4).setTextAppearance(localCameraActivity, 2131492920);
        if (i % n != 0)
        {
          k = j;
          if (m == 0) {}
        }
        else
        {
          localObject5 = (String)((List)localObject2).get(i);
          ((ShadowTextDrawable)localObject1).setText((CharSequence)localObject5);
          ((ShadowTextDrawable)localObject4).setText((CharSequence)localObject5);
          k = j + 1;
        }
        localObject5 = new StateListDrawable();
        ((StateListDrawable)localObject5).addState(new int[] { -16842913 }, (Drawable)localObject1);
        ((StateListDrawable)localObject5).addState(SELECTED_STATE_SET, (Drawable)localObject4);
        localArrayList.add(new KnobItemInfo((Drawable)localObject5, (String)((List)localObject2).get(i), i - ((List)localObject2).size(), ((Long)((List)localObject3).get(i)).longValue()));
        localArrayList.add(new KnobItemInfo((Drawable)localObject5, (String)((List)localObject2).get(i), i + 1, ((Long)((List)localObject3).get(i)).longValue()));
        i += 1;
        j = k;
        break;
      }
    }
    j = findPreferredKnobViewAngle(j);
    int k = localCameraActivity.getResources().getInteger(2131427329);
    i = j;
    if (j > k) {
      i = k;
    }
    j = localCameraActivity.getResources().getInteger(2131427335);
    setKnobInfo(new KnobInfo(-i, i, -((List)localObject2).size(), ((List)localObject2).size(), j));
    setKnobItems(localArrayList);
    invalidate();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/ExposureTimeKnobView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */