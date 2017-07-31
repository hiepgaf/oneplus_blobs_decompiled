package com.oneplus.camera.ui;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.drawable.CameraPreviewGridDrawable;

final class CameraPreviewGridImpl
  extends CameraComponent
  implements CameraPreviewGrid, CameraPreviewOverlay.Renderer
{
  private CameraPreviewGridDrawable m_GridDrawable;
  private final Rect m_PreviewBounds = new Rect();
  
  CameraPreviewGridImpl(CameraActivity paramCameraActivity)
  {
    super("Grid", paramCameraActivity, false);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    Object localObject = (CameraPreviewOverlay)findComponent(CameraPreviewOverlay.class);
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_GridDrawable = new CameraPreviewGridDrawable(localCameraActivity);
    if (localObject != null) {
      ((CameraPreviewOverlay)localObject).addRenderer(this, 0);
    }
    Settings localSettings = getSettings();
    CameraPreviewGrid.GridType localGridType = (CameraPreviewGrid.GridType)localSettings.getEnum("Grid.Type", CameraPreviewGrid.GridType.class, null);
    localObject = localGridType;
    if (localGridType == null) {
      if (!localSettings.getBoolean("Grid.IsVisible")) {
        break label117;
      }
    }
    label117:
    for (localObject = CameraPreviewGrid.GridType.UNIFORM_3x3;; localObject = CameraPreviewGrid.GridType.NONE)
    {
      localSettings.set("Grid.Type", localObject);
      localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
        {
          if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.RESUMING) {
            CameraPreviewGridImpl.-wrap0(CameraPreviewGridImpl.this, CameraPreviewGridImpl.PROP_GRID_TYPE, (CameraPreviewGrid.GridType)CameraPreviewGridImpl.-wrap1(CameraPreviewGridImpl.this).getEnum("Grid.Type", CameraPreviewGrid.GridType.class, CameraPreviewGrid.GridType.NONE));
          }
        }
      });
      setReadOnly(PROP_GRID_TYPE, localObject);
      return;
    }
  }
  
  public void onRender(Canvas paramCanvas, CameraPreviewOverlay.RenderingParams paramRenderingParams)
  {
    CameraPreviewGrid.GridType localGridType = (CameraPreviewGrid.GridType)get(PROP_GRID_TYPE);
    if (localGridType == CameraPreviewGrid.GridType.NONE) {
      return;
    }
    paramRenderingParams.getPreviewBounds().round(this.m_PreviewBounds);
    this.m_GridDrawable.setGridType(localGridType);
    this.m_GridDrawable.setBounds(this.m_PreviewBounds);
    this.m_GridDrawable.draw(paramCanvas);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CameraPreviewGridImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */