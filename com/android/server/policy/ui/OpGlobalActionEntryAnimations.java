package com.android.server.policy.ui;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import java.util.ArrayList;

public class OpGlobalActionEntryAnimations
{
  private final ArrayList<AnimationSet> mAdvSelectedAnimSets = new ArrayList();
  private final ArrayList<AnimationSet> mAdvShowAnimSets = new ArrayList();
  private AnimationSet mHideIconAnimationSet = null;
  private final ArrayList<AnimationSet> mSelectedAnimSets = new ArrayList();
  private final ArrayList<AnimationSet> mShowAnimSets = new ArrayList();
  
  public OpGlobalActionEntryAnimations()
  {
    initAnimations();
  }
  
  private void initAdvSelectedAnimations()
  {
    AnimationSet localAnimationSet = new AnimationSet(true);
    this.mAdvSelectedAnimSets.add(localAnimationSet);
    localAnimationSet = new AnimationSet(true);
    TranslateAnimation localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, -342.0F, 0.0F);
    localAnimationSet.setDuration(400L);
    localAnimationSet.setInterpolator(new DecelerateInterpolator());
    localAnimationSet.addAnimation(localTranslateAnimation);
    localAnimationSet.setFillAfter(true);
    localAnimationSet.setStartOffset(400L);
    this.mAdvSelectedAnimSets.add(localAnimationSet);
    localAnimationSet = new AnimationSet(true);
    localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, 0.0F, 0.0F);
    localAnimationSet.setDuration(400L);
    localAnimationSet.setInterpolator(new DecelerateInterpolator());
    localAnimationSet.addAnimation(localTranslateAnimation);
    localAnimationSet.setFillAfter(true);
    localAnimationSet.setStartOffset(400L);
    this.mAdvSelectedAnimSets.add(localAnimationSet);
    localAnimationSet = new AnimationSet(true);
    localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, 342.0F, 0.0F);
    localAnimationSet.setDuration(400L);
    localAnimationSet.setInterpolator(new DecelerateInterpolator());
    localAnimationSet.addAnimation(localTranslateAnimation);
    localAnimationSet.setFillAfter(true);
    localAnimationSet.setStartOffset(400L);
    this.mAdvSelectedAnimSets.add(localAnimationSet);
  }
  
  private void initAdvShowAnimations()
  {
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.0F, 1.0F);
    AnimationSet localAnimationSet = new AnimationSet(true);
    this.mAdvShowAnimSets.add(localAnimationSet);
    localAnimationSet = new AnimationSet(true);
    TranslateAnimation localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, 549.0F, 0.0F);
    localAnimationSet.setDuration(400L);
    localAnimationSet.setInterpolator(new DecelerateInterpolator());
    localAnimationSet.addAnimation(localTranslateAnimation);
    localAnimationSet.setFillAfter(true);
    localAnimationSet.setStartOffset(400L);
    this.mAdvShowAnimSets.add(localAnimationSet);
    localAnimationSet = new AnimationSet(true);
    localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, -342.0F, 0.0F);
    localAnimationSet.setDuration(400L);
    localAnimationSet.setInterpolator(new DecelerateInterpolator());
    localAnimationSet.addAnimation(localTranslateAnimation);
    localAnimationSet.setFillAfter(true);
    localAnimationSet.addAnimation(localAlphaAnimation);
    this.mAdvShowAnimSets.add(localAnimationSet);
    localAnimationSet = new AnimationSet(true);
    localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, -684.0F, 0.0F);
    localAnimationSet.setDuration(400L);
    localAnimationSet.setInterpolator(new DecelerateInterpolator());
    localAnimationSet.addAnimation(localTranslateAnimation);
    localAnimationSet.setFillAfter(true);
    localAnimationSet.addAnimation(localAlphaAnimation);
    this.mAdvShowAnimSets.add(localAnimationSet);
  }
  
  private void initAnimations()
  {
    initShowAnimations();
    initSelectedAnimations();
    initAdvShowAnimations();
    initAdvSelectedAnimations();
    initHideAnimations();
  }
  
  private void initHideAnimations()
  {
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
    this.mHideIconAnimationSet = new AnimationSet(true);
    this.mHideIconAnimationSet.setDuration(400L);
    this.mHideIconAnimationSet.setInterpolator(new DecelerateInterpolator());
    this.mHideIconAnimationSet.setFillAfter(true);
    this.mHideIconAnimationSet.addAnimation(localAlphaAnimation);
  }
  
  private void initSelectedAnimations()
  {
    AnimationSet localAnimationSet = new AnimationSet(true);
    TranslateAnimation localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, -207.0F, 0.0F);
    localAnimationSet.setDuration(400L);
    localAnimationSet.setInterpolator(new DecelerateInterpolator());
    localAnimationSet.addAnimation(localTranslateAnimation);
    localAnimationSet.setFillAfter(true);
    localAnimationSet.setStartOffset(400L);
    this.mSelectedAnimSets.add(localAnimationSet);
    localAnimationSet = new AnimationSet(true);
    localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, 207.0F, 0.0F);
    localAnimationSet.setDuration(400L);
    localAnimationSet.setInterpolator(new DecelerateInterpolator());
    localAnimationSet.addAnimation(localTranslateAnimation);
    localAnimationSet.setFillAfter(true);
    localAnimationSet.setStartOffset(400L);
    this.mSelectedAnimSets.add(localAnimationSet);
    localAnimationSet = new AnimationSet(true);
    this.mSelectedAnimSets.add(localAnimationSet);
    localAnimationSet = new AnimationSet(true);
    this.mSelectedAnimSets.add(localAnimationSet);
  }
  
  private void initShowAnimations()
  {
    Object localObject = new AlphaAnimation(0.0F, 1.0F);
    AnimationSet localAnimationSet = new AnimationSet(true);
    TranslateAnimation localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, 270.0F, 0.0F);
    localAnimationSet.setDuration(400L);
    localAnimationSet.setInterpolator(new DecelerateInterpolator());
    localAnimationSet.addAnimation(localTranslateAnimation);
    localAnimationSet.addAnimation((Animation)localObject);
    localAnimationSet.setFillAfter(true);
    this.mShowAnimSets.add(localAnimationSet);
    localAnimationSet = new AnimationSet(true);
    localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, -270.0F, 0.0F);
    localAnimationSet.setDuration(400L);
    localAnimationSet.setInterpolator(new DecelerateInterpolator());
    localAnimationSet.addAnimation(localTranslateAnimation);
    localAnimationSet.addAnimation((Animation)localObject);
    localAnimationSet.setFillAfter(true);
    this.mShowAnimSets.add(localAnimationSet);
    localObject = new AnimationSet(true);
    this.mShowAnimSets.add(localObject);
    localObject = new AnimationSet(true);
    this.mShowAnimSets.add(localObject);
  }
  
  public void clearAnimations()
  {
    this.mShowAnimSets.clear();
    this.mSelectedAnimSets.clear();
    this.mAdvShowAnimSets.clear();
    this.mAdvSelectedAnimSets.clear();
    this.mHideIconAnimationSet = null;
  }
  
  public ArrayList<AnimationSet> getAdvSelectedAnimSets()
  {
    return this.mAdvSelectedAnimSets;
  }
  
  public ArrayList<AnimationSet> getAdvShowAnimSets()
  {
    return this.mAdvShowAnimSets;
  }
  
  public AnimationSet getHideAnimSet()
  {
    return this.mHideIconAnimationSet;
  }
  
  public ArrayList<AnimationSet> getSelectedAnimSets()
  {
    return this.mSelectedAnimSets;
  }
  
  public ArrayList<AnimationSet> getShowAnimSets()
  {
    return this.mShowAnimSets;
  }
  
  public void refreshAnimations()
  {
    this.mShowAnimSets.clear();
    this.mSelectedAnimSets.clear();
    this.mAdvShowAnimSets.clear();
    this.mAdvSelectedAnimSets.clear();
    this.mHideIconAnimationSet = null;
    initShowAnimations();
    initSelectedAnimations();
    initAdvShowAnimations();
    initAdvSelectedAnimations();
    initHideAnimations();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/ui/OpGlobalActionEntryAnimations.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */