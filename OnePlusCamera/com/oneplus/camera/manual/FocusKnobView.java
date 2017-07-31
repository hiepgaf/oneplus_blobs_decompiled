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

public class FocusKnobView
  extends KnobView
{
  public FocusKnobView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public FocusKnobView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
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
    Object localObject1 = (Range)((Camera)localObject2).get(Camera.PROP_FOCUS_RANGE);
    if ((localObject1 == null) || (((Range)localObject1).getLower() == ((Range)localObject1).getUpper())) {
      return false;
    }
    Object localObject3 = new ShadowTextDrawable();
    ((ShadowTextDrawable)localObject3).setText(localCameraActivity.getString(2131558543));
    ((ShadowTextDrawable)localObject3).setTextAppearance(localCameraActivity, 2131492919);
    ShadowTextDrawable localShadowTextDrawable = new ShadowTextDrawable();
    localShadowTextDrawable.setText(localCameraActivity.getString(2131558543));
    localShadowTextDrawable.setTextAppearance(localCameraActivity, 2131492920);
    StateListDrawable localStateListDrawable = new StateListDrawable();
    localStateListDrawable.addState(new int[] { -16842913 }, (Drawable)localObject3);
    localStateListDrawable.addState(SELECTED_STATE_SET, localShadowTextDrawable);
    localArrayList.add(new KnobItemInfo(localStateListDrawable, localCameraActivity.getString(2131558543), 0, -1.0D));
    float f2 = ((Float)((Camera)localObject2).get(Camera.PROP_FOCUS_STEP)).floatValue();
    localObject2 = new ArrayList();
    for (float f1 = ((Float)((Range)localObject1).getUpper()).floatValue(); f1 >= ((Float)((Range)localObject1).getLower()).floatValue(); f1 -= f2) {
      ((List)localObject2).add(Float.valueOf(f1));
    }
    if (((List)localObject2).size() > 0) {
      ((List)localObject2).set(((List)localObject2).size() - 1, (Float)((Range)localObject1).getLower());
    }
    int i = 0;
    if (i < ((List)localObject2).size())
    {
      if (i == 0) {
        localObject1 = localCameraActivity.getDrawable(2130837722);
      }
      for (;;)
      {
        localObject3 = localCameraActivity.getString(2131558544);
        localArrayList.add(new KnobItemInfo((Drawable)localObject1, (String)localObject3, i - ((List)localObject2).size(), ((Float)((List)localObject2).get(i)).floatValue()));
        localArrayList.add(new KnobItemInfo((Drawable)localObject1, (String)localObject3, i + 1, ((Float)((List)localObject2).get(i)).floatValue()));
        i += 1;
        break;
        if (i == ((List)localObject2).size() - 1) {
          localObject1 = localCameraActivity.getDrawable(2130837721);
        } else {
          localObject1 = new ShadowTextDrawable();
        }
      }
    }
    setDashAroundAutoEnabled(false);
    i = localCameraActivity.getResources().getInteger(2131427330);
    int j = localCameraActivity.getResources().getInteger(2131427336);
    setKnobInfo(new KnobInfo(-i, i, -((List)localObject2).size(), ((List)localObject2).size(), j));
    setKnobItems(localArrayList);
    invalidate();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/FocusKnobView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */