package com.oneplus.camera.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.media.Resolution;
import com.oneplus.camera.media.ResolutionManager;
import com.oneplus.util.AspectRatio;
import java.util.Iterator;
import java.util.List;

public class BusinessCardUI
  extends UIComponent
{
  private View m_BackButton;
  private View m_BaseView;
  private Camera.LensFacing m_LastCameraFacing;
  private ResolutionManager m_ResolutionManager;
  private SavingLastSettingsHandle m_SavingLastSettingsHandle;
  private View m_ScanningArea;
  private Handle m_SelfTimeDisableHandle;
  private Settings m_Settings;
  
  public BusinessCardUI(CameraActivity paramCameraActivity)
  {
    super("Business Card UI", paramCameraActivity, false);
  }
  
  private void changePhotoResolution()
  {
    if (this.m_ResolutionManager == null)
    {
      Log.w(this.TAG, "saveLastSettings - ResolutionManager is Null");
      return;
    }
    Iterator localIterator = ((List)this.m_ResolutionManager.get(ResolutionManager.PROP_PHOTO_RESOLUTION_LIST)).iterator();
    while (localIterator.hasNext())
    {
      Resolution localResolution = (Resolution)localIterator.next();
      if (localResolution.getAspectRatio() == AspectRatio.RATIO_4x3) {
        this.m_ResolutionManager.set(ResolutionManager.PROP_PHOTO_RESOLUTION, localResolution);
      }
    }
  }
  
  private void restoreLastSettings(Resolution paramResolution)
  {
    if (this.m_ResolutionManager != null) {
      this.m_ResolutionManager.set(ResolutionManager.PROP_PHOTO_RESOLUTION, paramResolution);
    }
  }
  
  private void saveLastSettings()
  {
    if (!Handle.isValid(this.m_SavingLastSettingsHandle))
    {
      if (this.m_ResolutionManager == null)
      {
        Log.w(this.TAG, "saveLastSettings - ResolutionManager is Null");
        return;
      }
      this.m_SavingLastSettingsHandle = new SavingLastSettingsHandle((Resolution)this.m_ResolutionManager.get(ResolutionManager.PROP_PHOTO_RESOLUTION));
    }
  }
  
  private void setupUI()
  {
    if (this.m_BaseView != null)
    {
      setViewVisibility(this.m_BaseView, true, 0L, null);
      return;
    }
    Log.v(this.TAG, "setupUI()");
    this.m_BaseView = ((ViewStub)((OPCameraActivity)getCameraActivity()).getCaptureUIContainer().findViewById(2131361959)).inflate();
    this.m_BackButton = this.m_BaseView.findViewById(2131361868);
    this.m_BackButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        BusinessCardUI.this.getCameraActivity().finish();
      }
    });
    addAutoRotateView(this.m_BackButton);
    this.m_ScanningArea = this.m_BaseView.findViewById(2131361869);
    Object localObject = getCameraActivity().getResources();
    Rect localRect = new Rect(((Resources)localObject).getDimensionPixelSize(2131296646), ((Resources)localObject).getDimensionPixelSize(2131296645), ((Resources)localObject).getDimensionPixelSize(2131296646) + ((Resources)localObject).getDimensionPixelSize(2131296644), ((Resources)localObject).getDimensionPixelSize(2131296645) + ((Resources)localObject).getDimensionPixelSize(2131296643));
    localObject = new ScanningAreaDrawable(((Resources)localObject).getColor(2131230830), 0, localRect);
    this.m_ScanningArea.setBackground((Drawable)localObject);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_Settings = ((Settings)localCameraActivity.get(CameraActivity.PROP_SETTINGS));
    this.m_LastCameraFacing = ((Camera.LensFacing)this.m_Settings.getEnum("CameraLensFacing", Camera.LensFacing.class));
    localCameraActivity.addCallback(CameraActivity.PROP_AVAILABLE_CAMERAS, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera>> paramAnonymousPropertyChangeEventArgs)
      {
        if (BusinessCardUI.-get0(BusinessCardUI.this) != Camera.LensFacing.BACK) {
          BusinessCardUI.-get3(BusinessCardUI.this).set("CameraLensFacing", Camera.LensFacing.BACK);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
        {
        }
        do
        {
          return;
          if (!Handle.isValid(BusinessCardUI.-get2(BusinessCardUI.this))) {
            BusinessCardUI.-set1(BusinessCardUI.this, BusinessCardUI.this.getCameraActivity().disableSelfTimer());
          }
          BusinessCardUI.-wrap2(BusinessCardUI.this);
          BusinessCardUI.-wrap0(BusinessCardUI.this);
          return;
          if (Handle.isValid(BusinessCardUI.-get2(BusinessCardUI.this))) {
            Handle.close(BusinessCardUI.-get2(BusinessCardUI.this));
          }
        } while (!Handle.isValid(BusinessCardUI.-get1(BusinessCardUI.this)));
        Handle.close(BusinessCardUI.-get1(BusinessCardUI.this));
      }
    });
    findComponent(ResolutionManager.class, new ComponentSearchCallback()
    {
      public void onComponentFound(ResolutionManager paramAnonymousResolutionManager)
      {
        BusinessCardUI.-set0(BusinessCardUI.this, paramAnonymousResolutionManager);
        BusinessCardUI.-wrap2(BusinessCardUI.this);
        BusinessCardUI.-wrap0(BusinessCardUI.this);
      }
    });
    if (((Boolean)localCameraActivity.get(OPCameraActivity.PROP_IS_CAPTURE_UI_INFLATED)).booleanValue())
    {
      setupUI();
      return;
    }
    localCameraActivity.addCallback(OPCameraActivity.PROP_IS_CAPTURE_UI_INFLATED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        BusinessCardUI.-wrap3(BusinessCardUI.this);
      }
    });
  }
  
  private final class SavingLastSettingsHandle
    extends Handle
  {
    private Resolution photoReslution;
    
    public SavingLastSettingsHandle(Resolution paramResolution)
    {
      super();
      this.photoReslution = paramResolution;
    }
    
    protected void onClose(int paramInt)
    {
      BusinessCardUI.-wrap1(BusinessCardUI.this, this.photoReslution);
    }
    
    public String toString()
    {
      return super.toString() + "{ Resolution = " + this.photoReslution.getAspectRatio().toString() + " }";
    }
  }
  
  private class ScanningAreaDrawable
    extends Drawable
  {
    private final Paint m_BackgroundPaint;
    private final Rect m_Center;
    private Paint m_HollowPaint;
    private Paint m_InnerCornerPaint;
    private Paint m_InnerFramePaint;
    
    public ScanningAreaDrawable(int paramInt1, int paramInt2, Rect paramRect)
    {
      this.m_Center = paramRect;
      this.m_BackgroundPaint = new Paint();
      this.m_BackgroundPaint.setStyle(Paint.Style.FILL);
      this.m_BackgroundPaint.setColor(paramInt1);
      this.m_HollowPaint = new Paint();
      this.m_HollowPaint.setColor(paramInt2);
      this.m_HollowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
      this.m_HollowPaint.setAntiAlias(true);
      this.m_InnerFramePaint = new Paint();
      this.m_InnerFramePaint.setStyle(Paint.Style.STROKE);
      this.m_InnerFramePaint.setAntiAlias(true);
      this.m_InnerFramePaint.setColor(-1);
      this.m_InnerFramePaint.setStrokeWidth(3.0F);
      this.m_InnerCornerPaint = new Paint();
      this.m_InnerCornerPaint.setStyle(Paint.Style.STROKE);
      this.m_InnerCornerPaint.setAntiAlias(true);
      this.m_InnerCornerPaint.setColor(-1);
      this.m_InnerCornerPaint.setStrokeWidth(6.0F);
    }
    
    public void draw(Canvas paramCanvas)
    {
      Rect localRect = getBounds();
      Bitmap localBitmap = Bitmap.createBitmap(paramCanvas.getWidth(), paramCanvas.getHeight(), Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas(localBitmap);
      localCanvas.drawRect(localRect.left, localRect.top, localRect.right, localRect.bottom, this.m_BackgroundPaint);
      localCanvas.drawRect(this.m_Center.left, this.m_Center.top, this.m_Center.right, this.m_Center.bottom, this.m_HollowPaint);
      localCanvas.drawRect(this.m_Center.left, this.m_Center.top, this.m_Center.right, this.m_Center.bottom, this.m_InnerFramePaint);
      localCanvas.drawLine(this.m_Center.left, this.m_Center.top + 3, this.m_Center.left + 60, this.m_Center.top + 3, this.m_InnerCornerPaint);
      localCanvas.drawLine(this.m_Center.left + 3, this.m_Center.top, this.m_Center.left + 3, this.m_Center.top + 60, this.m_InnerCornerPaint);
      localCanvas.drawLine(this.m_Center.right - 60, this.m_Center.top + 3, this.m_Center.right, this.m_Center.top + 3, this.m_InnerCornerPaint);
      localCanvas.drawLine(this.m_Center.right - 3, this.m_Center.top + 60, this.m_Center.right - 3, this.m_Center.top, this.m_InnerCornerPaint);
      localCanvas.drawLine(this.m_Center.right - 60, this.m_Center.bottom - 3, this.m_Center.right, this.m_Center.bottom - 3, this.m_InnerCornerPaint);
      localCanvas.drawLine(this.m_Center.right - 3, this.m_Center.bottom - 60, this.m_Center.right - 3, this.m_Center.bottom, this.m_InnerCornerPaint);
      localCanvas.drawLine(this.m_Center.left + 3, this.m_Center.bottom - 60, this.m_Center.left + 3, this.m_Center.bottom, this.m_InnerCornerPaint);
      localCanvas.drawLine(this.m_Center.left, this.m_Center.bottom - 3, this.m_Center.left + 60, this.m_Center.bottom - 3, this.m_InnerCornerPaint);
      paramCanvas.drawBitmap(localBitmap, 0.0F, 0.0F, this.m_BackgroundPaint);
    }
    
    public int getOpacity()
    {
      return 0;
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/BusinessCardUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */