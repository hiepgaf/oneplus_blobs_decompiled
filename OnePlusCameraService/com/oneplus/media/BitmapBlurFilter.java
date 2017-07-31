package com.oneplus.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.renderscript.Allocation;
import android.renderscript.Allocation.MipmapControl;
import android.renderscript.Element;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicResize;
import android.renderscript.Type;
import com.oneplus.base.BasicBaseObject;
import com.oneplus.base.Handle;
import com.oneplus.renderscript.RenderScriptManager;

public class BitmapBlurFilter
  extends BasicBaseObject
{
  private Allocation m_BlurAllocation;
  private Allocation m_InputAllocation;
  private Allocation m_OutputAllocation;
  private Handle m_RenderScriptHandle;
  private ScriptIntrinsicBlur m_ScriptBlur;
  private ScriptIntrinsicResize m_ScriptResize;
  
  public BitmapBlurFilter(Context paramContext)
  {
    this.m_RenderScriptHandle = RenderScriptManager.createRenderScript(paramContext);
  }
  
  public Bitmap applyBlurFilter(Bitmap paramBitmap1, Bitmap paramBitmap2, float paramFloat)
  {
    verifyAccess();
    verifyReleaseState();
    if (paramBitmap1 == null) {
      return paramBitmap2;
    }
    int i;
    if (paramBitmap2 != null)
    {
      i = paramBitmap2.getWidth();
      if (paramBitmap2 == null) {
        break label55;
      }
    }
    label55:
    for (int j = paramBitmap2.getHeight();; j = paramBitmap1.getHeight())
    {
      return applyBlurFilter(paramBitmap1, paramBitmap2, paramFloat, i, j);
      i = paramBitmap1.getWidth();
      break;
    }
  }
  
  public Bitmap applyBlurFilter(Bitmap paramBitmap1, Bitmap paramBitmap2, float paramFloat, int paramInt1, int paramInt2)
  {
    verifyAccess();
    verifyReleaseState();
    if (paramBitmap1 == null) {
      return paramBitmap2;
    }
    if (paramBitmap1.getConfig() != Bitmap.Config.ARGB_8888) {
      throw new IllegalArgumentException("Only support bitmap config ARGB_8888, current config: " + paramBitmap1.getConfig());
    }
    Bitmap localBitmap;
    if ((paramBitmap2 == null) || (paramBitmap2.getWidth() != paramInt1))
    {
      localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
      label79:
      paramBitmap2 = RenderScriptManager.getRenderScript(this.m_RenderScriptHandle);
      if ((this.m_InputAllocation != null) && (this.m_InputAllocation.getType().getX() == paramBitmap1.getWidth())) {
        break label319;
      }
      label111:
      this.m_InputAllocation = Allocation.createFromBitmap(paramBitmap2, paramBitmap1, Allocation.MipmapControl.MIPMAP_NONE, 1);
      this.m_BlurAllocation = Allocation.createTyped(paramBitmap2, this.m_InputAllocation.getType());
      label139:
      if ((this.m_OutputAllocation != null) && (this.m_OutputAllocation.getType().getX() == paramInt1)) {
        break label339;
      }
    }
    for (;;)
    {
      if ((paramBitmap1.getWidth() != paramInt1) || (paramBitmap1.getHeight() != paramInt2)) {
        this.m_OutputAllocation = Allocation.createTyped(paramBitmap2, Type.createXY(paramBitmap2, Element.RGBA_8888(paramBitmap2), paramInt1, paramInt2));
      }
      label319:
      label339:
      do
      {
        if (this.m_ScriptBlur == null) {
          this.m_ScriptBlur = ScriptIntrinsicBlur.create(paramBitmap2, Element.RGBA_8888(paramBitmap2));
        }
        if (this.m_ScriptResize == null) {
          this.m_ScriptResize = ScriptIntrinsicResize.create(paramBitmap2);
        }
        this.m_ScriptBlur.setRadius(paramFloat);
        this.m_ScriptBlur.setInput(this.m_InputAllocation);
        this.m_ScriptBlur.forEach(this.m_BlurAllocation);
        if (this.m_OutputAllocation == null) {
          break label357;
        }
        this.m_ScriptResize.setInput(this.m_BlurAllocation);
        this.m_ScriptResize.forEach_bicubic(this.m_OutputAllocation);
        this.m_OutputAllocation.copyTo(localBitmap);
        return localBitmap;
        localBitmap = paramBitmap2;
        if (paramBitmap2.getHeight() == paramInt2) {
          break label79;
        }
        break;
        if (this.m_InputAllocation.getType().getY() == paramBitmap1.getHeight()) {
          break label139;
        }
        break label111;
      } while (this.m_OutputAllocation.getType().getY() == paramInt2);
    }
    label357:
    this.m_BlurAllocation.copyTo(localBitmap);
    return localBitmap;
  }
  
  protected void onRelease()
  {
    this.m_RenderScriptHandle = Handle.close(this.m_RenderScriptHandle);
    if (this.m_InputAllocation != null)
    {
      this.m_InputAllocation.destroy();
      this.m_InputAllocation = null;
    }
    if (this.m_BlurAllocation != null)
    {
      this.m_BlurAllocation.destroy();
      this.m_BlurAllocation = null;
    }
    if (this.m_OutputAllocation != null)
    {
      this.m_OutputAllocation.destroy();
      this.m_OutputAllocation = null;
    }
    if (this.m_ScriptBlur != null)
    {
      this.m_ScriptBlur.destroy();
      this.m_ScriptBlur = null;
    }
    if (this.m_ScriptResize != null)
    {
      this.m_ScriptResize.destroy();
      this.m_ScriptResize = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/BitmapBlurFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */