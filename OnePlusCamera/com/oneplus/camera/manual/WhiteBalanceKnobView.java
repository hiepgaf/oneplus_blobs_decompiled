package com.oneplus.camera.manual;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.drawable.ShadowTextDrawable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WhiteBalanceKnobView
  extends KnobView
{
  public WhiteBalanceKnobView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public WhiteBalanceKnobView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected boolean onSetupIcons()
  {
    CameraActivity localCameraActivity = (CameraActivity)getContext();
    int i = localCameraActivity.getResources().getInteger(2131427328);
    int j = localCameraActivity.getResources().getInteger(2131427334);
    setKnobInfo(new KnobInfo(-i, i, -2, 2, j));
    setIconPadding(localCameraActivity.getResources().getDimensionPixelSize(2131296549));
    Object localObject = (Camera)localCameraActivity.get(CameraActivity.PROP_CAMERA);
    if (localObject == null) {
      return false;
    }
    ArrayList localArrayList = new ArrayList();
    localObject = (List)((Camera)localObject).get(Camera.PROP_AWB_MODES);
    if ((localObject == null) || (((List)localObject).size() < 1)) {
      return false;
    }
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      switch (((Integer)((Iterator)localObject).next()).intValue())
      {
      case 4: 
      default: 
        break;
      case 1: 
        ShadowTextDrawable localShadowTextDrawable1 = new ShadowTextDrawable();
        localShadowTextDrawable1.setText(localCameraActivity.getString(2131558543));
        localShadowTextDrawable1.setTextAppearance(localCameraActivity, 2131492919);
        ShadowTextDrawable localShadowTextDrawable2 = new ShadowTextDrawable();
        localShadowTextDrawable2.setText(localCameraActivity.getString(2131558543));
        localShadowTextDrawable2.setTextAppearance(localCameraActivity, 2131492920);
        StateListDrawable localStateListDrawable = new StateListDrawable();
        localStateListDrawable.addState(new int[] { -16842913 }, localShadowTextDrawable1);
        localStateListDrawable.addState(SELECTED_STATE_SET, localShadowTextDrawable2);
        localArrayList.add(new KnobItemInfo(localStateListDrawable, localCameraActivity.getString(2131558543), 0, 1.0D));
        break;
      case 6: 
        localArrayList.add(new KnobItemInfo(localCameraActivity.getDrawable(2130837702), localCameraActivity.getString(2131558548), -2, 6.0D));
        break;
      case 5: 
        localArrayList.add(new KnobItemInfo(localCameraActivity.getDrawable(2130837705), localCameraActivity.getString(2131558547), -1, 5.0D));
        break;
      case 3: 
        localArrayList.add(new KnobItemInfo(localCameraActivity.getDrawable(2130837708), localCameraActivity.getString(2131558546), 1, 3.0D));
        break;
      case 2: 
        localArrayList.add(new KnobItemInfo(localCameraActivity.getDrawable(2130837711), localCameraActivity.getString(2131558545), 2, 2.0D));
      }
    }
    setKnobItems(localArrayList);
    invalidate();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/WhiteBalanceKnobView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */