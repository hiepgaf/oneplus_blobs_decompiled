package com.oneplus.camera.ui.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.List;

public class ResolutionWindow
  extends PopupWindow
{
  private Adapter m_Adapter;
  private AnimatorListenerAdapter m_AnimatorListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      ResolutionWindow.this.dismiss();
      ResolutionWindow.this.setFocusable(false);
    }
  };
  private Context m_Context;
  private View m_MenuView;
  private final AdapterView.OnItemClickListener m_OnInternalItemClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      if (ResolutionWindow.-get4(ResolutionWindow.this) == paramAnonymousInt)
      {
        ResolutionWindow.this.hiddenListView(true);
        return;
      }
      ResolutionWindow.-set0(ResolutionWindow.this, paramAnonymousInt);
      ResolutionWindow.-get0(ResolutionWindow.this).notifyDataSetChanged();
      ResolutionWindow.-get2(ResolutionWindow.this).onItemClick(paramAnonymousAdapterView, paramAnonymousView, paramAnonymousInt, paramAnonymousLong);
      ResolutionWindow.this.hiddenListView(true);
    }
  };
  private AdapterView.OnItemClickListener m_OnItemClickListener;
  private View.OnTouchListener m_OnTouchListener = new View.OnTouchListener()
  {
    public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
    {
      int i = ResolutionWindow.-get3(ResolutionWindow.this).getTop();
      int j = (int)paramAnonymousMotionEvent.getY();
      if ((paramAnonymousMotionEvent.getAction() == 1) && (j < i)) {
        ResolutionWindow.this.hiddenListView(true);
      }
      return true;
    }
  };
  private ListView m_PopView;
  private float m_animation_translation;
  private int m_selecteditem;
  private ColorStateList m_text_color;
  private ColorStateList m_text_color_selected;
  
  public ResolutionWindow(Activity paramActivity, List<String> paramList, int paramInt)
  {
    super(paramActivity);
    this.m_Context = paramActivity;
    this.m_selecteditem = paramInt;
    this.m_MenuView = View.inflate(this.m_Context, 2130903043, null);
    this.m_PopView = ((ListView)this.m_MenuView.findViewById(2131361810));
    this.m_Adapter = new Adapter(paramList);
    this.m_PopView.setAdapter(this.m_Adapter);
    this.m_PopView.setChoiceMode(1);
    this.m_PopView.setOnItemClickListener(this.m_OnInternalItemClickListener);
    setContentView(this.m_MenuView);
    setWidth(-1);
    setHeight(-2);
    setBackgroundDrawable(null);
    this.m_MenuView.setBackgroundColor(Color.argb(205, 0, 0, 0));
    this.m_MenuView.setOnTouchListener(this.m_OnTouchListener);
    this.m_text_color_selected = this.m_Context.getResources().getColorStateList(2131230804);
    this.m_text_color = this.m_Context.getResources().getColorStateList(2131230805);
    this.m_animation_translation = this.m_Context.getResources().getDimensionPixelSize(2131296366);
    this.m_MenuView.setAlpha(0.0F);
    this.m_MenuView.animate().alpha(1.0F).setDuration(200L);
    this.m_PopView.setTranslationY(this.m_animation_translation);
    this.m_PopView.animate().translationY(0.0F).setDuration(200L);
  }
  
  public void hiddenListView(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.m_PopView.setTranslationY(0.0F);
      this.m_PopView.animate().translationY(this.m_animation_translation).setDuration(200L).start();
      this.m_MenuView.setAlpha(1.0F);
      this.m_MenuView.animate().setListener(this.m_AnimatorListener).alpha(0.0F).setDuration(200L).start();
      return;
    }
    this.m_PopView.setVisibility(8);
    this.m_MenuView.setVisibility(8);
  }
  
  public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener)
  {
    this.m_OnItemClickListener = paramOnItemClickListener;
  }
  
  private final class Adapter
    extends BaseAdapter
  {
    private ImageView m_ImageView;
    private List<String> m_Items;
    private TextView m_TextView;
    
    public Adapter()
    {
      List localList;
      this.m_Items = localList;
    }
    
    public int getCount()
    {
      return this.m_Items.size();
    }
    
    public Object getItem(int paramInt)
    {
      return this.m_Items.get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return ((String)this.m_Items.get(paramInt)).hashCode();
    }
    
    public View getItemView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramView = (String)getItem(paramInt);
      paramViewGroup = View.inflate(ResolutionWindow.-get1(ResolutionWindow.this), 2130903044, null);
      this.m_TextView = ((TextView)paramViewGroup.findViewById(2131361811));
      this.m_ImageView = ((ImageView)paramViewGroup.findViewById(2131361812));
      this.m_TextView.setText(paramView);
      if (paramInt == ResolutionWindow.-get4(ResolutionWindow.this))
      {
        this.m_TextView.setTextColor(ResolutionWindow.-get6(ResolutionWindow.this));
        this.m_ImageView.setVisibility(0);
        return paramViewGroup;
      }
      this.m_TextView.setTextColor(ResolutionWindow.-get5(ResolutionWindow.this));
      this.m_ImageView.setVisibility(8);
      return paramViewGroup;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      return getItemView(paramInt, paramView, paramViewGroup);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/menu/ResolutionWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */