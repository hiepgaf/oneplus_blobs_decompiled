package com.oneplus.camera.manual;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Range;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.drawable.ShadowTextDrawable;
import java.util.ArrayList;
import java.util.List;

public class EVKnobView
  extends KnobView
{
  private static final float EPSILON_PRECISION = 10000.0F;
  private static final String TAG = EVKnobView.class.getSimpleName();
  
  public EVKnobView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public EVKnobView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private boolean isInteger(float paramFloat)
  {
    boolean bool = false;
    int i = (int)(Math.abs(paramFloat) * 10000.0F) % 10000;
    if ((i == 0) || (i == 9999)) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isZero(float paramFloat)
  {
    return Math.abs(paramFloat) <= 0.001D;
  }
  
  protected void onExposureCompChanged()
  {
    onSetupIcons();
  }
  
  protected boolean onSetupIcons()
  {
    CameraActivity localCameraActivity = (CameraActivity)getContext();
    setIconPadding(localCameraActivity.getResources().getDimensionPixelSize(2131296549));
    Object localObject2 = (Camera)localCameraActivity.get(CameraActivity.PROP_CAMERA);
    if (localObject2 == null) {
      return false;
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = (Range)((Camera)localObject2).get(Camera.PROP_EXPOSURE_COMPENSATION_RANGE);
    if ((localObject1 == null) || ((((Float)((Range)localObject1).getLower()).floatValue() == 0.0F) && (((Float)((Range)localObject1).getUpper()).floatValue() == 0.0F)))
    {
      Log.d(TAG, "onSetupIcons() - evRange is not valid.");
      return false;
    }
    Object localObject3 = new ShadowTextDrawable();
    ((ShadowTextDrawable)localObject3).setText(localCameraActivity.getString(2131558543));
    ((ShadowTextDrawable)localObject3).setTextAppearance(localCameraActivity, 2131492919);
    ShadowTextDrawable localShadowTextDrawable = new ShadowTextDrawable();
    localShadowTextDrawable.setText(localCameraActivity.getString(2131558543));
    localShadowTextDrawable.setTextAppearance(localCameraActivity, 2131492920);
    Object localObject4 = new StateListDrawable();
    ((StateListDrawable)localObject4).addState(new int[] { -16842913 }, (Drawable)localObject3);
    ((StateListDrawable)localObject4).addState(SELECTED_STATE_SET, localShadowTextDrawable);
    localArrayList.add(new KnobItemInfo((Drawable)localObject4, localCameraActivity.getString(2131558543), 0, 0.0D));
    int j = 0;
    int i = 0;
    float f2 = ((Float)((Camera)localObject2).get(Camera.PROP_EXPOSURE_COMPENSATION_STEP)).floatValue();
    localObject3 = new ArrayList();
    float f1 = ((Float)((Range)localObject1).getUpper()).floatValue();
    if (f1 >= ((Float)((Range)localObject1).getLower()).floatValue())
    {
      float f3 = Math.round(10000.0F * f1) / 10000.0F;
      k = j;
      m = i;
      if (!isZero(f1))
      {
        if (f1 <= 0.0F) {
          break label366;
        }
        k = j + 1;
        m = i;
      }
      for (;;)
      {
        ((List)localObject3).add(Float.valueOf(f3));
        f1 -= f2;
        j = k;
        i = m;
        break;
        label366:
        m = i + 1;
        k = j;
      }
    }
    if (((List)localObject3).size() > 0) {
      ((List)localObject3).set(((List)localObject3).size() - 1, (Float)((Range)localObject1).getLower());
    }
    int k = 0;
    if (k < ((List)localObject3).size())
    {
      f1 = ((Float)((List)localObject3).get(k)).floatValue();
      if (isZero(f1)) {}
      for (;;)
      {
        k += 1;
        break;
        localShadowTextDrawable = new ShadowTextDrawable();
        localShadowTextDrawable.setTextAppearance(localCameraActivity, 2131492919);
        localObject4 = new ShadowTextDrawable();
        ((ShadowTextDrawable)localObject4).setTextAppearance(localCameraActivity, 2131492920);
        if (isInteger(f1))
        {
          localObject2 = String.valueOf((int)f1);
          localObject1 = localObject2;
          if (f1 > 0.0F) {
            localObject1 = "+" + (String)localObject2;
          }
          localShadowTextDrawable.setText((CharSequence)localObject1);
          ((ShadowTextDrawable)localObject4).setText((CharSequence)localObject1);
        }
        localObject1 = new StateListDrawable();
        ((StateListDrawable)localObject1).addState(new int[] { -16842913 }, localShadowTextDrawable);
        ((StateListDrawable)localObject1).addState(SELECTED_STATE_SET, (Drawable)localObject4);
        localObject2 = String.format("%.2f", new Object[] { Float.valueOf(f1) });
        if (f1 > 0.0F) {
          localArrayList.add(new KnobItemInfo((Drawable)localObject1, (String)localObject2, j - k, f1));
        } else {
          localArrayList.add(new KnobItemInfo((Drawable)localObject1, (String)localObject2, i - k, f1));
        }
      }
    }
    k = localCameraActivity.getResources().getInteger(2131427332);
    int m = localCameraActivity.getResources().getInteger(2131427338);
    setKnobInfo(new KnobInfo(-k, k, -i, j, m));
    setKnobItems(localArrayList);
    invalidate();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/EVKnobView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */