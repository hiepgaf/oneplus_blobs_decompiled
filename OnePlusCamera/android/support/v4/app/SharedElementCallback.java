package android.support.v4.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.List;
import java.util.Map;

public abstract class SharedElementCallback
{
  private static final String BUNDLE_SNAPSHOT_BITMAP = "sharedElement:snapshot:bitmap";
  private static final String BUNDLE_SNAPSHOT_IMAGE_MATRIX = "sharedElement:snapshot:imageMatrix";
  private static final String BUNDLE_SNAPSHOT_IMAGE_SCALETYPE = "sharedElement:snapshot:imageScaleType";
  private static int MAX_IMAGE_SIZE = 1048576;
  private Matrix mTempMatrix;
  
  private static Bitmap createDrawableBitmap(Drawable paramDrawable)
  {
    int i = paramDrawable.getIntrinsicWidth();
    int j = paramDrawable.getIntrinsicHeight();
    if (i <= 0) {}
    while (j <= 0) {
      return null;
    }
    float f = Math.min(1.0F, MAX_IMAGE_SIZE / (i * j));
    if (!(paramDrawable instanceof BitmapDrawable)) {}
    while (f != 1.0F)
    {
      i = (int)(i * f);
      j = (int)(j * f);
      Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas(localBitmap);
      Rect localRect = paramDrawable.getBounds();
      int k = localRect.left;
      int m = localRect.top;
      int n = localRect.right;
      int i1 = localRect.bottom;
      paramDrawable.setBounds(0, 0, i, j);
      paramDrawable.draw(localCanvas);
      paramDrawable.setBounds(k, m, n, i1);
      return localBitmap;
    }
    return ((BitmapDrawable)paramDrawable).getBitmap();
  }
  
  public Parcelable onCaptureSharedElementSnapshot(View paramView, Matrix paramMatrix, RectF paramRectF)
  {
    if (!(paramView instanceof ImageView))
    {
      j = Math.round(paramRectF.width());
      i = Math.round(paramRectF.height());
      if (j > 0) {
        break label142;
      }
    }
    label142:
    while (i <= 0)
    {
      return null;
      ImageView localImageView = (ImageView)paramView;
      Object localObject = localImageView.getDrawable();
      Drawable localDrawable = localImageView.getBackground();
      if ((localObject == null) || (localDrawable != null)) {
        break;
      }
      localObject = createDrawableBitmap((Drawable)localObject);
      if (localObject == null) {
        break;
      }
      paramView = new Bundle();
      paramView.putParcelable("sharedElement:snapshot:bitmap", (Parcelable)localObject);
      paramView.putString("sharedElement:snapshot:imageScaleType", localImageView.getScaleType().toString());
      if (localImageView.getScaleType() != ImageView.ScaleType.MATRIX) {
        return paramView;
      }
      paramMatrix = localImageView.getImageMatrix();
      paramRectF = new float[9];
      paramMatrix.getValues(paramRectF);
      paramView.putFloatArray("sharedElement:snapshot:imageMatrix", paramRectF);
      return paramView;
    }
    float f = Math.min(1.0F, MAX_IMAGE_SIZE / (j * i));
    int j = (int)(j * f);
    int i = (int)(i * f);
    if (this.mTempMatrix != null) {}
    for (;;)
    {
      this.mTempMatrix.set(paramMatrix);
      this.mTempMatrix.postTranslate(-paramRectF.left, -paramRectF.top);
      this.mTempMatrix.postScale(f, f);
      paramMatrix = Bitmap.createBitmap(j, i, Bitmap.Config.ARGB_8888);
      paramRectF = new Canvas(paramMatrix);
      paramRectF.concat(this.mTempMatrix);
      paramView.draw(paramRectF);
      return paramMatrix;
      this.mTempMatrix = new Matrix();
    }
  }
  
  public View onCreateSnapshotView(Context paramContext, Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof Bundle))
    {
      if (!(paramParcelable instanceof Bitmap)) {
        return null;
      }
    }
    else
    {
      paramParcelable = (Bundle)paramParcelable;
      Object localObject = (Bitmap)paramParcelable.getParcelable("sharedElement:snapshot:bitmap");
      if (localObject != null)
      {
        paramContext = new ImageView(paramContext);
        paramContext.setImageBitmap((Bitmap)localObject);
        paramContext.setScaleType(ImageView.ScaleType.valueOf(paramParcelable.getString("sharedElement:snapshot:imageScaleType")));
        if (paramContext.getScaleType() == ImageView.ScaleType.MATRIX) {
          break label76;
        }
      }
      for (;;)
      {
        return paramContext;
        return null;
        label76:
        paramParcelable = paramParcelable.getFloatArray("sharedElement:snapshot:imageMatrix");
        localObject = new Matrix();
        ((Matrix)localObject).setValues(paramParcelable);
        paramContext.setImageMatrix((Matrix)localObject);
      }
    }
    paramParcelable = (Bitmap)paramParcelable;
    paramContext = new ImageView(paramContext);
    paramContext.setImageBitmap(paramParcelable);
    return paramContext;
  }
  
  public void onMapSharedElements(List<String> paramList, Map<String, View> paramMap) {}
  
  public void onRejectSharedElements(List<View> paramList) {}
  
  public void onSharedElementEnd(List<String> paramList, List<View> paramList1, List<View> paramList2) {}
  
  public void onSharedElementStart(List<String> paramList, List<View> paramList1, List<View> paramList2) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/SharedElementCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */