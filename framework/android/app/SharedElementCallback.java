package android.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.transition.TransitionUtils;
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
  static final SharedElementCallback NULL_CALLBACK = new SharedElementCallback() {};
  private Matrix mTempMatrix;
  
  public Parcelable onCaptureSharedElementSnapshot(View paramView, Matrix paramMatrix, RectF paramRectF)
  {
    if ((paramView instanceof ImageView))
    {
      ImageView localImageView = (ImageView)paramView;
      Object localObject = localImageView.getDrawable();
      Drawable localDrawable = localImageView.getBackground();
      if ((localObject != null) && ((localDrawable == null) || (localDrawable.getAlpha() == 0)))
      {
        localObject = TransitionUtils.createDrawableBitmap((Drawable)localObject);
        if (localObject != null)
        {
          paramView = new Bundle();
          paramView.putParcelable("sharedElement:snapshot:bitmap", (Parcelable)localObject);
          paramView.putString("sharedElement:snapshot:imageScaleType", localImageView.getScaleType().toString());
          if (localImageView.getScaleType() == ImageView.ScaleType.MATRIX)
          {
            paramMatrix = localImageView.getImageMatrix();
            paramRectF = new float[9];
            paramMatrix.getValues(paramRectF);
            paramView.putFloatArray("sharedElement:snapshot:imageMatrix", paramRectF);
          }
          return paramView;
        }
      }
    }
    if (this.mTempMatrix == null) {
      this.mTempMatrix = new Matrix(paramMatrix);
    }
    for (;;)
    {
      return TransitionUtils.createViewBitmap(paramView, this.mTempMatrix, paramRectF);
      this.mTempMatrix.set(paramMatrix);
    }
  }
  
  public View onCreateSnapshotView(Context paramContext, Parcelable paramParcelable)
  {
    Object localObject1 = null;
    if ((paramParcelable instanceof Bundle))
    {
      localObject2 = (Bundle)paramParcelable;
      localObject1 = (Bitmap)((Bundle)localObject2).getParcelable("sharedElement:snapshot:bitmap");
      if (localObject1 == null) {
        return null;
      }
      paramParcelable = new ImageView(paramContext);
      paramContext = paramParcelable;
      paramParcelable.setImageBitmap((Bitmap)localObject1);
      paramParcelable.setScaleType(ImageView.ScaleType.valueOf(((Bundle)localObject2).getString("sharedElement:snapshot:imageScaleType")));
      localObject1 = paramContext;
      if (paramParcelable.getScaleType() == ImageView.ScaleType.MATRIX)
      {
        localObject1 = ((Bundle)localObject2).getFloatArray("sharedElement:snapshot:imageMatrix");
        localObject2 = new Matrix();
        ((Matrix)localObject2).setValues((float[])localObject1);
        paramParcelable.setImageMatrix((Matrix)localObject2);
        localObject1 = paramContext;
      }
    }
    while (!(paramParcelable instanceof Bitmap))
    {
      Object localObject2;
      return (View)localObject1;
    }
    paramParcelable = (Bitmap)paramParcelable;
    localObject1 = new View(paramContext);
    ((View)localObject1).setBackground(new BitmapDrawable(paramContext.getResources(), paramParcelable));
    return (View)localObject1;
  }
  
  public void onMapSharedElements(List<String> paramList, Map<String, View> paramMap) {}
  
  public void onRejectSharedElements(List<View> paramList) {}
  
  public void onSharedElementEnd(List<String> paramList, List<View> paramList1, List<View> paramList2) {}
  
  public void onSharedElementStart(List<String> paramList, List<View> paramList1, List<View> paramList2) {}
  
  public void onSharedElementsArrived(List<String> paramList, List<View> paramList1, OnSharedElementsReadyListener paramOnSharedElementsReadyListener)
  {
    paramOnSharedElementsReadyListener.onSharedElementsReady();
  }
  
  public static abstract interface OnSharedElementsReadyListener
  {
    public abstract void onSharedElementsReady();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/SharedElementCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */