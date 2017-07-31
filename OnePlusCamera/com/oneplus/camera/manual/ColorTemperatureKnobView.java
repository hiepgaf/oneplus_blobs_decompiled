package com.oneplus.camera.manual;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Range;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.drawable.ShadowTextDrawable;
import java.util.ArrayList;
import java.util.List;

public class ColorTemperatureKnobView
  extends KnobView
{
  private static final int COLOR_TEMPERATURE_LOWER_BOUND = 2300;
  private static final int COLOR_TEMPERATURE_STEP = 10;
  private static final int COLOR_TEMPERATURE_UPPER_BOUND = 7500;
  public static final int[] CT_CANDIDATES = { 2300, 3500, 4800, 6100, 7500 };
  
  public ColorTemperatureKnobView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ColorTemperatureKnobView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private boolean isCandidate(int paramInt)
  {
    int[] arrayOfInt = CT_CANDIDATES;
    int j = arrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      if (paramInt == arrayOfInt[i]) {
        return true;
      }
      i += 1;
    }
    return false;
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
    localObject1 = (Range)((Camera)localObject1).get(Camera.PROP_FOCUS_RANGE);
    if ((localObject1 == null) || (((Range)localObject1).getLower() == ((Range)localObject1).getUpper())) {
      return false;
    }
    localObject1 = new ShadowTextDrawable();
    ((ShadowTextDrawable)localObject1).setText(localCameraActivity.getString(2131558543));
    ((ShadowTextDrawable)localObject1).setTextAppearance(localCameraActivity, 2131492919);
    ShadowTextDrawable localShadowTextDrawable = new ShadowTextDrawable();
    localShadowTextDrawable.setText(localCameraActivity.getString(2131558543));
    localShadowTextDrawable.setTextAppearance(localCameraActivity, 2131492920);
    Object localObject2 = new StateListDrawable();
    ((StateListDrawable)localObject2).addState(new int[] { -16842913 }, (Drawable)localObject1);
    ((StateListDrawable)localObject2).addState(SELECTED_STATE_SET, localShadowTextDrawable);
    localArrayList.add(new KnobItemInfo((Drawable)localObject2, localCameraActivity.getString(2131558543), 0, 0.0D));
    localObject1 = new ArrayList();
    int i = 2300;
    while (i <= 7500)
    {
      ((List)localObject1).add(Integer.valueOf(i));
      i += 10;
    }
    i = 0;
    while (i < ((List)localObject1).size())
    {
      j = ((Integer)((List)localObject1).get(i)).intValue();
      localShadowTextDrawable = new ShadowTextDrawable();
      localShadowTextDrawable.setTextAppearance(localCameraActivity, 2131492919);
      localObject2 = new ShadowTextDrawable();
      ((ShadowTextDrawable)localObject2).setTextAppearance(localCameraActivity, 2131492920);
      if (isCandidate(j))
      {
        localShadowTextDrawable.setText(j + "K");
        ((ShadowTextDrawable)localObject2).setText(j + "K");
      }
      StateListDrawable localStateListDrawable = new StateListDrawable();
      localStateListDrawable.addState(new int[] { -16842913 }, localShadowTextDrawable);
      localStateListDrawable.addState(SELECTED_STATE_SET, (Drawable)localObject2);
      localArrayList.add(new KnobItemInfo(localStateListDrawable, j + "K", i - ((List)localObject1).size(), j));
      localArrayList.add(new KnobItemInfo(localStateListDrawable, j + "K", i + 1, j));
      i += 1;
    }
    setDashAroundAutoEnabled(true);
    i = localCameraActivity.getResources().getInteger(2131427333);
    int j = localCameraActivity.getResources().getInteger(2131427339);
    setKnobInfo(new KnobInfo(-i, i, -((List)localObject1).size(), ((List)localObject1).size(), j));
    setKnobItems(localArrayList);
    invalidate();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/ColorTemperatureKnobView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */