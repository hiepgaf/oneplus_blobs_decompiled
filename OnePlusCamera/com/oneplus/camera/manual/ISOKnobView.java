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

public class ISOKnobView
  extends KnobView
{
  public static final String[] ISO_CANDIDATES = { "100", "125", "160", "200", "250", "320", "400", "500", "640", "800", "1000", "1250", "1600", "2000", "2500", "3200", "4000", "5000", "6400", "12800" };
  private static final int ISO_COUNT_PER_INDICATOR = 3;
  public static final int ISO_TOLERANT = 50;
  
  public ISOKnobView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ISOKnobView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
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
    localObject1 = (Range)((Camera)localObject1).get(Camera.PROP_ISO_RANGE);
    if ((localObject1 == null) || (((Range)localObject1).getLower() == ((Range)localObject1).getUpper())) {
      return false;
    }
    Object localObject2 = new ShadowTextDrawable();
    ((ShadowTextDrawable)localObject2).setText(localCameraActivity.getString(2131558543));
    ((ShadowTextDrawable)localObject2).setTextAppearance(localCameraActivity, 2131492919);
    Object localObject3 = new ShadowTextDrawable();
    ((ShadowTextDrawable)localObject3).setText(localCameraActivity.getString(2131558543));
    ((ShadowTextDrawable)localObject3).setTextAppearance(localCameraActivity, 2131492920);
    Object localObject4 = new StateListDrawable();
    ((StateListDrawable)localObject4).addState(new int[] { -16842913 }, (Drawable)localObject2);
    ((StateListDrawable)localObject4).addState(SELECTED_STATE_SET, (Drawable)localObject3);
    localArrayList.add(new KnobItemInfo((Drawable)localObject4, localCameraActivity.getString(2131558543), 0, -1.0D));
    localObject2 = new ArrayList();
    localObject3 = new ArrayList();
    int i = 0;
    if (i < ISO_CANDIDATES.length)
    {
      localObject4 = ISO_CANDIDATES[i];
      j = Integer.parseInt((String)localObject4);
      if ((j < ((Integer)((Range)localObject1).getLower()).intValue()) || (j - 50 > ((Integer)((Range)localObject1).getUpper()).intValue())) {}
      for (;;)
      {
        i += 1;
        break;
        ((List)localObject2).add(localObject4);
        ((List)localObject3).add(Integer.valueOf(j));
      }
    }
    i = 0;
    if (i < ((List)localObject2).size())
    {
      if (i == ((List)localObject2).size() - 1) {}
      for (j = 1;; j = 0)
      {
        localObject1 = new ShadowTextDrawable();
        ((ShadowTextDrawable)localObject1).setTextAppearance(localCameraActivity, 2131492919);
        localObject4 = new ShadowTextDrawable();
        ((ShadowTextDrawable)localObject4).setTextAppearance(localCameraActivity, 2131492920);
        if ((i % 3 == 0) || (j != 0))
        {
          ((ShadowTextDrawable)localObject1).setText((CharSequence)((List)localObject2).get(i));
          ((ShadowTextDrawable)localObject4).setText((CharSequence)((List)localObject2).get(i));
        }
        StateListDrawable localStateListDrawable = new StateListDrawable();
        localStateListDrawable.addState(new int[] { -16842913 }, (Drawable)localObject1);
        localStateListDrawable.addState(SELECTED_STATE_SET, (Drawable)localObject4);
        localArrayList.add(new KnobItemInfo(localStateListDrawable, (String)((List)localObject2).get(i), i - ((List)localObject2).size(), ((Integer)((List)localObject3).get(i)).intValue()));
        localArrayList.add(new KnobItemInfo(localStateListDrawable, (String)((List)localObject2).get(i), i + 1, ((Integer)((List)localObject3).get(i)).intValue()));
        i += 1;
        break;
      }
    }
    i = localCameraActivity.getResources().getInteger(2131427331);
    int j = localCameraActivity.getResources().getInteger(2131427337);
    setKnobInfo(new KnobInfo(-i, i, -((List)localObject2).size(), ((List)localObject2).size(), j));
    setKnobItems(localArrayList);
    invalidate();
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/ISOKnobView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */