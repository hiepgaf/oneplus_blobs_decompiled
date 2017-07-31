package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Point;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import com.amap.api.maps2d.model.LatLng;

class at
  extends ViewGroup
{
  private w a;
  
  public at(Context paramContext, w paramw)
  {
    super(paramContext);
    this.a = paramw;
    setWillNotDraw(false);
  }
  
  private void a(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = paramInt5 & 0x7;
    paramInt5 &= 0x70;
    if (i != 5)
    {
      if (i == 1) {
        break label66;
      }
      if (paramInt5 == 80) {
        break label77;
      }
      if (paramInt5 == 16) {
        break label86;
      }
    }
    for (;;)
    {
      paramView.layout(paramInt3, paramInt4, paramInt3 + paramInt1, paramInt4 + paramInt2);
      return;
      paramInt3 -= paramInt1;
      break;
      label66:
      paramInt3 -= paramInt1 / 2;
      break;
      label77:
      paramInt4 -= paramInt2;
      continue;
      label86:
      paramInt4 -= paramInt2 / 2;
    }
  }
  
  private void a(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    if (!(paramView instanceof ListView))
    {
      if (paramInt1 > 0) {
        break label85;
      }
      label11:
      paramView.measure(0, 0);
      label17:
      if (paramInt1 == -2) {
        break label92;
      }
      if (paramInt1 == -1) {
        break label103;
      }
      paramArrayOfInt[0] = paramInt1;
    }
    for (;;)
    {
      if (paramInt2 == -2) {
        break label114;
      }
      if (paramInt2 == -1) {
        break label123;
      }
      paramArrayOfInt[1] = paramInt2;
      return;
      View localView = (View)paramView.getParent();
      if (localView == null) {
        break;
      }
      paramArrayOfInt[0] = localView.getWidth();
      paramArrayOfInt[1] = localView.getHeight();
      break;
      label85:
      if (paramInt2 <= 0) {
        break label11;
      }
      break label17;
      label92:
      paramArrayOfInt[0] = paramView.getMeasuredWidth();
      continue;
      label103:
      paramArrayOfInt[0] = getMeasuredWidth();
    }
    label114:
    paramArrayOfInt[1] = paramView.getMeasuredHeight();
    return;
    label123:
    paramArrayOfInt[1] = getMeasuredHeight();
  }
  
  private void a(View paramView, a parama)
  {
    int[] arrayOfInt = new int[2];
    a(paramView, parama.width, parama.height, arrayOfInt);
    a(paramView, arrayOfInt[0], arrayOfInt[1], parama.c, parama.d, parama.e);
  }
  
  private void a(cb paramcb, int[] paramArrayOfInt, int paramInt)
  {
    int i = paramcb.b();
    if (i != 1)
    {
      if (i == 0) {}
    }
    else
    {
      a(paramcb, paramArrayOfInt[0], paramArrayOfInt[1], getWidth() - paramArrayOfInt[0], (getHeight() + paramArrayOfInt[1]) / 2, paramInt);
      return;
    }
    a(paramcb, paramArrayOfInt[0], paramArrayOfInt[1], getWidth() - paramArrayOfInt[0], getHeight(), paramInt);
  }
  
  private void b(View paramView, a parama)
  {
    int[] arrayOfInt = new int[2];
    a(paramView, parama.width, parama.height, arrayOfInt);
    if (!(paramView instanceof cb))
    {
      if (!(paramView instanceof ao))
      {
        if ((paramView instanceof o)) {
          break label95;
        }
        if (parama.b != null) {
          break label115;
        }
      }
    }
    else
    {
      a((cb)paramView, arrayOfInt, parama.e);
      return;
    }
    a(paramView, arrayOfInt[0], arrayOfInt[1], getWidth() - arrayOfInt[0], arrayOfInt[1], parama.e);
    return;
    label95:
    a(paramView, arrayOfInt[0], arrayOfInt[1], 0, 0, parama.e);
    return;
    label115:
    Object localObject1 = new u((int)(parama.b.latitude * 1000000.0D), (int)(parama.b.longitude * 1000000.0D));
    try
    {
      localObject1 = this.a.s().a((u)localObject1, null);
      if (localObject1 != null)
      {
        ((Point)localObject1).x += parama.c;
        ((Point)localObject1).y += parama.d;
        a(paramView, arrayOfInt[0], arrayOfInt[1], ((Point)localObject1).x, ((Point)localObject1).y, parama.e);
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        cj.a(localRemoteException, "MapOverlayViewGroup", "layoutMap");
        Object localObject2 = null;
      }
    }
  }
  
  public void a()
  {
    onLayout(false, 0, 0, 0, 0);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = getChildCount();
    paramInt1 = 0;
    if (paramInt1 >= paramInt2) {
      return;
    }
    View localView = getChildAt(paramInt1);
    if (localView != null)
    {
      if ((localView.getLayoutParams() instanceof a)) {
        break label61;
      }
      a(localView, new a(localView.getLayoutParams()));
    }
    for (;;)
    {
      paramInt1 += 1;
      break;
      label61:
      a locala = (a)localView.getLayoutParams();
      if (locala.a != 0) {
        a(localView, locala);
      } else {
        b(localView, locala);
      }
    }
  }
  
  public static class a
    extends ViewGroup.LayoutParams
  {
    public int a = 1;
    public LatLng b = null;
    public int c = 0;
    public int d = 0;
    public int e = 51;
    
    public a(int paramInt1, int paramInt2, LatLng paramLatLng, int paramInt3, int paramInt4, int paramInt5)
    {
      super(paramInt2);
      this.a = 0;
      this.b = paramLatLng;
      this.c = paramInt3;
      this.d = paramInt4;
      this.e = paramInt5;
    }
    
    public a(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/at.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */