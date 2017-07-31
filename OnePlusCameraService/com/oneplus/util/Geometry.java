package com.oneplus.util;

import android.graphics.Rect;
import android.graphics.RectF;

public final class Geometry
{
  public static final int FLAG_OFFSET = 1;
  public static final int FLAG_OFFSET_SCALING = 3;
  public static final int FLAG_PRESERVE_BOTTOM = 1048576;
  public static final int FLAG_PRESERVE_CENTER = 6291456;
  public static final int FLAG_PRESERVE_CENTER_X = 2097152;
  public static final int FLAG_PRESERVE_CENTER_Y = 4194304;
  public static final int FLAG_PRESERVE_LEFT = 131072;
  public static final int FLAG_PRESERVE_LEFT_RIGHT = 655360;
  public static final int FLAG_PRESERVE_RIGHT = 524288;
  public static final int FLAG_PRESERVE_TOP = 262144;
  public static final int FLAG_PRESERVE_TOP_BOTTOM = 1310720;
  public static final int FLAG_PRESERVE_WIDTH_HEIGHT_RATIO = 65536;
  public static final int FLAG_SCALING = 2;
  private static final float FLOAT_ACCURACY_TOLERANCE = 1.0E-4F;
  
  public static boolean adjustPointIntoRect(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, RectF paramRectF, float paramFloat1, float paramFloat2, int paramInt3)
  {
    if ((Float.isNaN(paramFloat1)) || (Float.isNaN(paramFloat2))) {}
    for (int i = 0;; i = 1)
    {
      if ((paramArrayOfFloat1 != paramArrayOfFloat2) || (paramInt1 != paramInt2)) {
        System.arraycopy(paramArrayOfFloat2, paramInt2, paramArrayOfFloat1, paramInt1, 2);
      }
      if (!containsAll(paramRectF, paramArrayOfFloat1, paramInt1, 1)) {
        break;
      }
      return true;
    }
    float f3 = paramArrayOfFloat1[paramInt1];
    float f2 = paramArrayOfFloat1[(paramInt1 + 1)];
    if (((paramInt3 & 0x1) != 1) || (i != 0))
    {
      if ((paramInt3 & 0x2) != 2) {
        break label469;
      }
      if ((i == 0) || (areSimilarCoordinates(f3, paramFloat1)) || (areSimilarCoordinates(f2, paramFloat2))) {
        return false;
      }
    }
    else
    {
      if (f3 < paramRectF.left)
      {
        paramArrayOfFloat1[paramInt1] = paramRectF.left;
        if (f2 >= paramRectF.top) {
          break label180;
        }
        paramArrayOfFloat1[(paramInt1 + 1)] = paramRectF.top;
      }
      for (;;)
      {
        return true;
        if (f3 <= paramRectF.right) {
          break;
        }
        paramArrayOfFloat1[paramInt1] = paramRectF.right;
        break;
        label180:
        if (f2 > paramRectF.bottom) {
          paramArrayOfFloat1[(paramInt1 + 1)] = paramRectF.bottom;
        }
      }
    }
    float f1;
    if (areSimilarCoordinates(paramFloat1, paramRectF.left))
    {
      f1 = paramRectF.left;
      if (!areSimilarCoordinates(paramFloat2, paramRectF.top)) {
        break label352;
      }
      paramFloat1 = paramRectF.top;
      label244:
      if (f3 >= paramRectF.left) {
        break label379;
      }
      paramFloat2 = (paramRectF.left - f1) / (f3 - f1);
      label271:
      if (f2 >= paramRectF.top) {
        break label415;
      }
      f2 = (paramRectF.top - paramFloat1) / (f2 - paramFloat1);
    }
    for (;;)
    {
      paramFloat2 = Math.min(paramFloat2, f2);
      if ((paramFloat2 <= 1.0F) && (!areSimilarCoordinates(paramFloat2, 1.0F))) {
        break label451;
      }
      return false;
      f1 = paramFloat1;
      if (!areSimilarCoordinates(paramFloat1, paramRectF.right)) {
        break;
      }
      f1 = paramRectF.right;
      break;
      label352:
      paramFloat1 = paramFloat2;
      if (!areSimilarCoordinates(paramFloat2, paramRectF.bottom)) {
        break label244;
      }
      paramFloat1 = paramRectF.bottom;
      break label244;
      label379:
      if (f3 > paramRectF.right)
      {
        paramFloat2 = (paramRectF.right - f1) / (f3 - f1);
        break label271;
      }
      paramFloat2 = 1.0F;
      break label271;
      label415:
      if (f2 > paramRectF.bottom) {
        f2 = (paramRectF.bottom - paramFloat1) / (f2 - paramFloat1);
      } else {
        f2 = 1.0F;
      }
    }
    label451:
    scalePoints(paramFloat2, paramFloat2, f1, paramFloat1, paramArrayOfFloat1, paramInt1, paramArrayOfFloat1, paramInt1, 1);
    return true;
    label469:
    return false;
  }
  
  public static boolean adjustPointsIntoRect(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, int paramInt3, RectF paramRectF, float paramFloat1, float paramFloat2, int paramInt4)
  {
    if (paramInt3 <= 0) {
      return false;
    }
    if (paramInt3 == 1) {
      return adjustPointIntoRect(paramArrayOfFloat1, paramInt1, paramArrayOfFloat2, paramInt2, paramRectF, paramFloat1, paramFloat2, paramInt4);
    }
    if ((Float.isNaN(paramFloat1)) || (Float.isNaN(paramFloat2))) {}
    for (int i = 0;; i = 1)
    {
      if ((paramArrayOfFloat1 != paramArrayOfFloat2) || (paramInt1 != paramInt2)) {
        System.arraycopy(paramArrayOfFloat2, paramInt2, paramArrayOfFloat1, paramInt1, paramInt3 << 1);
      }
      if (!containsAll(paramRectF, paramArrayOfFloat1, paramInt1, paramInt3)) {
        break;
      }
      return true;
    }
    paramArrayOfFloat2 = new RectF();
    getBoundingBox(paramArrayOfFloat2, paramArrayOfFloat1, paramInt1, paramInt3);
    if (((paramInt4 & 0x1) != 1) || (i != 0))
    {
      if ((paramInt4 & 0x2) != 2) {
        break label726;
      }
      f2 = paramFloat1;
      f1 = paramFloat2;
      if (i == 0)
      {
        float[] arrayOfFloat = new float[2];
        getCentroid(arrayOfFloat, 0, paramArrayOfFloat1, paramInt1, paramInt3);
        f2 = arrayOfFloat[0];
        f1 = arrayOfFloat[1];
      }
      if (!areSimilarCoordinates(f2, paramRectF.left)) {
        break label436;
      }
      paramFloat1 = paramRectF.left;
      label186:
      if (!areSimilarCoordinates(f1, paramRectF.top)) {
        break label463;
      }
      paramFloat2 = paramRectF.top;
      label206:
      if ((paramFloat1 >= paramRectF.left) && (paramFloat1 <= paramRectF.right)) {
        break label490;
      }
    }
    label369:
    label425:
    label436:
    label463:
    label490:
    while ((paramFloat2 < paramRectF.top) || (paramFloat2 > paramRectF.bottom))
    {
      return false;
      f1 = 0.0F;
      f2 = 0.0F;
      if (paramArrayOfFloat2.width() >= paramRectF.width())
      {
        f1 = paramRectF.centerX() - paramArrayOfFloat2.centerX();
        if (paramArrayOfFloat2.height() < paramRectF.height()) {
          break label369;
        }
        f2 = paramRectF.centerY() - paramArrayOfFloat2.centerY();
      }
      for (;;)
      {
        offsetPoints(f1, f2, paramArrayOfFloat1, paramInt1, paramArrayOfFloat1, paramInt1, paramInt3);
        if (!containsAll(paramRectF, paramArrayOfFloat1, paramInt1, paramInt3)) {
          break label425;
        }
        return true;
        if (paramArrayOfFloat2.left < paramRectF.left)
        {
          f1 = paramRectF.left - paramArrayOfFloat2.left;
          break;
        }
        if (paramArrayOfFloat2.right <= paramRectF.right) {
          break;
        }
        f1 = paramRectF.right - paramArrayOfFloat2.right;
        break;
        if (paramArrayOfFloat2.top < paramRectF.top) {
          f2 = paramRectF.top - paramArrayOfFloat2.top;
        } else if (paramArrayOfFloat2.bottom > paramRectF.bottom) {
          f2 = paramRectF.bottom - paramArrayOfFloat2.bottom;
        }
      }
      paramArrayOfFloat2.offset(f1, f2);
      break;
      paramFloat1 = f2;
      if (!areSimilarCoordinates(f2, paramRectF.right)) {
        break label186;
      }
      paramFloat1 = paramRectF.right;
      break label186;
      paramFloat2 = f1;
      if (!areSimilarCoordinates(f1, paramRectF.bottom)) {
        break label206;
      }
      paramFloat2 = paramRectF.bottom;
      break label206;
    }
    float f1 = (paramFloat1 - paramRectF.left) / (paramFloat1 - paramArrayOfFloat2.left);
    float f2 = (paramFloat2 - paramRectF.top) / (paramFloat2 - paramArrayOfFloat2.top);
    float f4 = (paramRectF.right - paramFloat1) / (paramArrayOfFloat2.right - paramFloat1);
    float f3 = (paramRectF.bottom - paramFloat2) / (paramArrayOfFloat2.bottom - paramFloat2);
    if (isValidScalingFactor(f1, false)) {
      if (isValidScalingFactor(f4, false))
      {
        f1 = Math.min(f1, f4);
        if (!isValidScalingFactor(f2, false)) {
          break label689;
        }
        if (!isValidScalingFactor(f3, false)) {
          break label686;
        }
        f2 = Math.min(f2, f3);
      }
    }
    for (;;)
    {
      f1 = Math.min(f1, f2);
      if ((f1 <= 1.0F) && (!areSimilarCoordinates(f1, 1.0F))) {
        break label707;
      }
      return false;
      break;
      if (isValidScalingFactor(f4, false))
      {
        f1 = f4;
        break;
      }
      return false;
      label686:
      continue;
      label689:
      if (!isValidScalingFactor(f3, false)) {
        break label705;
      }
      f2 = f3;
    }
    label705:
    return false;
    label707:
    scalePoints(f1, f1, paramFloat1, paramFloat2, paramArrayOfFloat1, paramInt1, paramArrayOfFloat1, paramInt1, paramInt3);
    return true;
    label726:
    return false;
  }
  
  public static boolean adjustRectByMinSize(RectF paramRectF1, RectF paramRectF2, float paramFloat1, float paramFloat2, int paramInt)
  {
    if (paramRectF1 != paramRectF2) {
      paramRectF1.set(paramRectF2);
    }
    paramRectF1.sort();
    if ((Float.isNaN(paramFloat1)) || (Float.isNaN(paramFloat2)) || (Float.isInfinite(paramFloat1)) || (Float.isInfinite(paramFloat2))) {
      return false;
    }
    if (paramRectF1.width() < paramFloat1)
    {
      if ((0xA0000 & paramInt) == 655360) {
        return false;
      }
    }
    else if (paramRectF1.height() >= paramFloat2) {
      return true;
    }
    if ((paramRectF1.height() < paramFloat2) && ((0x140000 & paramInt) == 1310720)) {
      return false;
    }
    float f;
    if ((0x10000 & paramInt) == 65536)
    {
      if ((Math.abs(paramRectF1.width()) <= 0.001F) || (Math.abs(paramRectF1.height()) <= 0.001F)) {
        return false;
      }
      f = paramRectF1.width() / paramRectF1.height();
      if (paramRectF1.width() < paramFloat1)
      {
        if ((paramInt & 0x20000) != 131072) {
          break label279;
        }
        paramRectF1.right = (paramRectF1.left + paramFloat1);
        label175:
        if (!Float.isNaN(f))
        {
          paramFloat1 /= f;
          if ((paramInt & 0x40000) != 262144) {
            break label332;
          }
          paramRectF1.bottom = (paramRectF1.top + paramFloat1);
        }
      }
      label208:
      if (paramRectF1.height() < paramFloat2)
      {
        if ((paramInt & 0x40000) != 262144) {
          break label385;
        }
        paramRectF1.bottom = (paramRectF1.top + paramFloat2);
        label237:
        if (!Float.isNaN(f))
        {
          paramFloat1 = paramFloat2 * f;
          if ((paramInt & 0x20000) != 131072) {
            break label438;
          }
          paramRectF1.right = (paramRectF1.left + paramFloat1);
        }
      }
    }
    for (;;)
    {
      return true;
      f = NaN.0F;
      break;
      label279:
      if ((paramInt & 0x80000) == 524288)
      {
        paramRectF1.left = (paramRectF1.right - paramFloat1);
        break label175;
      }
      paramRectF1.left -= (paramFloat1 - paramRectF1.width()) / 2.0F;
      paramRectF1.right = (paramRectF1.left + paramFloat1);
      break label175;
      label332:
      if ((0x100000 & paramInt) == 1048576)
      {
        paramRectF1.top = (paramRectF1.bottom - paramFloat1);
        break label208;
      }
      paramRectF1.top -= (paramFloat1 - paramRectF1.height()) / 2.0F;
      paramRectF1.bottom = (paramRectF1.top + paramFloat1);
      break label208;
      label385:
      if ((0x100000 & paramInt) == 1048576)
      {
        paramRectF1.top = (paramRectF1.bottom - paramFloat2);
        break label237;
      }
      paramRectF1.top -= (paramFloat2 - paramRectF1.height()) / 2.0F;
      paramRectF1.bottom = (paramRectF1.top + paramFloat2);
      break label237;
      label438:
      if ((paramInt & 0x80000) == 524288)
      {
        paramRectF1.left = (paramRectF1.right - paramFloat1);
      }
      else
      {
        paramRectF1.left -= (paramFloat1 - paramRectF1.width()) / 2.0F;
        paramRectF1.right = (paramRectF1.left + paramFloat1);
      }
    }
  }
  
  public static boolean adjustRectByWidthHeightRatio(RectF paramRectF1, RectF paramRectF2, float paramFloat, int paramInt)
  {
    if (paramRectF1 != paramRectF2) {
      paramRectF1.set(paramRectF2);
    }
    if ((Float.isNaN(paramFloat)) || (Float.isInfinite(paramFloat)) || (paramFloat <= 0.0F)) {
      return false;
    }
    if (Math.abs(paramRectF1.width() / paramRectF1.height() - paramFloat) <= 0.001F) {
      return true;
    }
    float f;
    if ((0xA0000 & paramInt) == 655360)
    {
      if ((paramInt & 0x140000) == 1310720) {
        return false;
      }
      f = paramRectF1.width() / paramFloat;
      if ((paramInt & 0x40000) == 262144) {
        paramRectF1.bottom = (paramRectF1.top + f);
      }
    }
    else
    {
      if ((0x20000 & paramInt) != 131072) {
        break label325;
      }
      f = paramRectF1.height() * paramFloat;
      paramFloat = paramRectF1.width() / paramFloat;
      if ((paramInt & 0x140000) != 1310720) {
        break label203;
      }
      paramRectF1.right = (paramRectF1.left + f);
    }
    for (;;)
    {
      return true;
      if ((paramInt & 0x100000) == 1048576)
      {
        paramRectF1.top = (paramRectF1.bottom - f);
        break;
      }
      paramRectF1.top -= (f - paramRectF1.height()) / 2.0F;
      paramRectF1.bottom = (paramRectF1.top + f);
      break;
      label203:
      if ((paramInt & 0x40000) == 262144)
      {
        if (f < paramRectF1.width()) {
          paramRectF1.right = (paramRectF1.left + f);
        } else {
          paramRectF1.bottom = (paramRectF1.top + paramFloat);
        }
      }
      else if ((paramInt & 0x100000) == 1048576)
      {
        if (f < paramRectF1.width()) {
          paramRectF1.right = (paramRectF1.left + f);
        } else {
          paramRectF1.top = (paramRectF1.bottom - paramFloat);
        }
      }
      else
      {
        paramRectF1.top -= (paramFloat - paramRectF1.height()) / 2.0F;
        paramRectF1.bottom = (paramRectF1.top + paramFloat);
        continue;
        label325:
        if ((0x80000 & paramInt) == 524288)
        {
          f = paramRectF1.height() * paramFloat;
          paramFloat = paramRectF1.width() / paramFloat;
          if ((paramInt & 0x140000) == 1310720)
          {
            paramRectF1.left = (paramRectF1.right - f);
          }
          else if ((paramInt & 0x40000) == 262144)
          {
            if (f < paramRectF1.width()) {
              paramRectF1.left = (paramRectF1.right - f);
            } else {
              paramRectF1.bottom = (paramRectF1.top + paramFloat);
            }
          }
          else if ((paramInt & 0x100000) == 1048576)
          {
            if (f < paramRectF1.width()) {
              paramRectF1.left = (paramRectF1.right - f);
            } else {
              paramRectF1.top = (paramRectF1.bottom - paramFloat);
            }
          }
          else
          {
            paramRectF1.top -= (paramFloat - paramRectF1.height()) / 2.0F;
            paramRectF1.bottom = (paramRectF1.top + paramFloat);
          }
        }
        else
        {
          paramFloat = paramRectF1.width() / paramFloat;
          paramRectF1.top -= (paramFloat - paramRectF1.height()) / 2.0F;
          paramRectF1.bottom = (paramRectF1.top + paramFloat);
        }
      }
    }
  }
  
  public static boolean adjustRectToContainsPoints(RectF paramRectF1, RectF paramRectF2, float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramRectF1 != paramRectF2) {
      paramRectF1.set(paramRectF2);
    }
    paramRectF1.sort();
    if (containsAll(paramRectF1, paramArrayOfFloat, paramInt1, paramInt2)) {
      return true;
    }
    int i = paramInt3;
    label62:
    RectF localRectF2;
    RectF localRectF1;
    float f1;
    float f2;
    if ((paramInt3 & 0x1) == 1)
    {
      if (((0xA0000 & paramInt3) != 0) || ((0x140000 & paramInt3) != 0)) {
        i = paramInt3 & 0xFFFFFFFE;
      }
    }
    else
    {
      localRectF2 = new RectF();
      getBoundingBox(localRectF2, paramArrayOfFloat, paramInt1, paramInt2);
      localRectF1 = null;
      if ((i & 0x1) != 1) {
        break label339;
      }
      f1 = 0.0F;
      f2 = 0.0F;
      paramInt2 = 1;
      if (paramRectF1.width() > localRectF2.width()) {
        break label185;
      }
      paramInt1 = 0;
      f1 = localRectF2.centerX() - paramRectF1.centerX();
      label127:
      if (paramRectF1.height() > localRectF2.height()) {
        break label250;
      }
      paramInt2 = 0;
      f2 = localRectF2.centerY() - paramRectF1.centerY();
    }
    for (;;)
    {
      if (paramInt2 == 0) {
        break label315;
      }
      paramRectF1.offset(f1, f2);
      return true;
      i = paramInt3;
      if ((0x600000 & paramInt3) == 0) {
        break label62;
      }
      break;
      label185:
      if (paramRectF1.left > localRectF2.left)
      {
        f1 = localRectF2.left - paramRectF1.left;
        paramInt1 = paramInt2;
        break label127;
      }
      paramInt1 = paramInt2;
      if (paramRectF1.right >= localRectF2.right) {
        break label127;
      }
      f1 = localRectF2.right - paramRectF1.right;
      paramInt1 = paramInt2;
      break label127;
      label250:
      if (paramRectF1.top > localRectF2.top)
      {
        f2 = localRectF2.top - paramRectF1.top;
        paramInt2 = paramInt1;
      }
      else
      {
        paramInt2 = paramInt1;
        if (paramRectF1.bottom < localRectF2.bottom)
        {
          f2 = localRectF2.bottom - paramRectF1.bottom;
          paramInt2 = paramInt1;
        }
      }
    }
    label315:
    label339:
    label369:
    label414:
    label447:
    float f3;
    if (paramRectF1 == paramRectF2)
    {
      localRectF1 = new RectF(paramRectF1);
      localRectF1.offset(f1, f2);
      if ((i & 0x2) != 2) {
        break label645;
      }
      paramArrayOfFloat = localRectF1;
      if (localRectF1 == null)
      {
        if (paramRectF1 != paramRectF2) {
          break label565;
        }
        paramArrayOfFloat = new RectF(paramRectF1);
      }
      paramRectF2 = new float[2];
      selectPivot(paramRectF2, 0, paramArrayOfFloat, i);
      if (paramArrayOfFloat.left <= localRectF2.left) {
        break label570;
      }
      f1 = (paramRectF2[0] - localRectF2.left) / (paramRectF2[0] - paramArrayOfFloat.left);
      if (paramArrayOfFloat.top <= localRectF2.top) {
        break label576;
      }
      f2 = (paramRectF2[1] - localRectF2.top) / (paramRectF2[1] - paramArrayOfFloat.top);
      if (paramArrayOfFloat.right >= localRectF2.right) {
        break label582;
      }
      f3 = (localRectF2.right - paramRectF2[0]) / (paramArrayOfFloat.right - paramRectF2[0]);
      label480:
      if (paramArrayOfFloat.bottom >= localRectF2.bottom) {
        break label588;
      }
    }
    label565:
    label570:
    label576:
    label582:
    label588:
    for (float f4 = (localRectF2.bottom - paramRectF2[1]) / (paramArrayOfFloat.bottom - paramRectF2[1]);; f4 = 1.0F)
    {
      f1 = Math.max(f1, f3);
      f2 = Math.max(f2, f4);
      if ((0x10000 & i) != 65536) {
        break label611;
      }
      f1 = Math.max(f1, f2);
      if (f1 >= 1.0F) {
        break label594;
      }
      return false;
      localRectF1 = paramRectF1;
      break;
      paramArrayOfFloat = paramRectF1;
      break label369;
      f1 = 1.0F;
      break label414;
      f2 = 1.0F;
      break label447;
      f3 = 1.0F;
      break label480;
    }
    label594:
    scaleRect(paramRectF1, paramArrayOfFloat, f1, f1, paramRectF2[0], paramRectF2[1]);
    for (;;)
    {
      return true;
      label611:
      if ((f1 < 1.0F) && (f2 < 1.0F)) {
        return false;
      }
      scaleRect(paramRectF1, paramArrayOfFloat, f1, f2, paramRectF2[0], paramRectF2[1]);
    }
    label645:
    return false;
  }
  
  public static boolean areSimilarCoordinates(float paramFloat1, float paramFloat2)
  {
    boolean bool2 = false;
    paramFloat1 -= paramFloat2;
    boolean bool1 = bool2;
    if (paramFloat1 <= 1.0E-4F)
    {
      bool1 = bool2;
      if (paramFloat1 >= -1.0E-4F) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean contains(RectF paramRectF, float paramFloat1, float paramFloat2)
  {
    if ((paramFloat1 >= paramRectF.left) || (areSimilarCoordinates(paramFloat1, paramRectF.left)))
    {
      if ((paramFloat2 >= paramRectF.top) || (areSimilarCoordinates(paramFloat2, paramRectF.top)))
      {
        if ((paramFloat1 > paramRectF.right) && (!areSimilarCoordinates(paramFloat1, paramRectF.right))) {
          break label86;
        }
        if ((paramFloat2 > paramRectF.bottom) && (!areSimilarCoordinates(paramFloat2, paramRectF.bottom))) {
          break label88;
        }
        return true;
      }
    }
    else {
      return false;
    }
    return false;
    label86:
    return false;
    label88:
    return false;
  }
  
  public static boolean contains(RectF paramRectF1, RectF paramRectF2)
  {
    if ((paramRectF2.left >= paramRectF1.left) || (areSimilarCoordinates(paramRectF2.left, paramRectF1.left)))
    {
      if ((paramRectF2.top >= paramRectF1.top) || (areSimilarCoordinates(paramRectF2.top, paramRectF1.top)))
      {
        if ((paramRectF2.right > paramRectF1.right) && (!areSimilarCoordinates(paramRectF2.right, paramRectF1.right))) {
          break label110;
        }
        if ((paramRectF2.bottom > paramRectF1.bottom) && (!areSimilarCoordinates(paramRectF2.bottom, paramRectF1.bottom))) {
          break label112;
        }
        return true;
      }
    }
    else {
      return false;
    }
    return false;
    label110:
    return false;
    label112:
    return false;
  }
  
  public static boolean containsAll(RectF paramRectF, float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    while (paramInt2 > 0)
    {
      int i = paramInt1 + 1;
      float f2 = paramArrayOfFloat[paramInt1];
      paramInt1 = i + 1;
      float f3 = paramArrayOfFloat[i];
      float f1;
      if (areSimilarCoordinates(f2, paramRectF.left))
      {
        f1 = paramRectF.left;
        if (!areSimilarCoordinates(f3, paramRectF.top)) {
          break label108;
        }
        f2 = paramRectF.top;
        label61:
        if ((f1 >= paramRectF.left) && (f1 <= paramRectF.right)) {
          break label133;
        }
      }
      label108:
      label133:
      while ((f2 < paramRectF.top) || (f2 > paramRectF.bottom))
      {
        return false;
        f1 = f2;
        if (!areSimilarCoordinates(f2, paramRectF.right)) {
          break;
        }
        f1 = paramRectF.right;
        break;
        f2 = f3;
        if (!areSimilarCoordinates(f3, paramRectF.bottom)) {
          break label61;
        }
        f2 = paramRectF.bottom;
        break label61;
      }
      paramInt2 -= 1;
    }
    return true;
  }
  
  public static void convertRectToPoints(Rect paramRect, float[] paramArrayOfFloat, int paramInt)
  {
    int i = paramInt + 1;
    paramArrayOfFloat[paramInt] = paramRect.left;
    paramInt = i + 1;
    paramArrayOfFloat[i] = paramRect.top;
    i = paramInt + 1;
    paramArrayOfFloat[paramInt] = paramRect.right;
    paramInt = i + 1;
    paramArrayOfFloat[i] = paramRect.top;
    i = paramInt + 1;
    paramArrayOfFloat[paramInt] = paramRect.right;
    paramInt = i + 1;
    paramArrayOfFloat[i] = paramRect.bottom;
    paramArrayOfFloat[paramInt] = paramRect.left;
    paramArrayOfFloat[(paramInt + 1)] = paramRect.bottom;
  }
  
  public static void convertRectToPoints(RectF paramRectF, float[] paramArrayOfFloat, int paramInt)
  {
    int i = paramInt + 1;
    paramArrayOfFloat[paramInt] = paramRectF.left;
    paramInt = i + 1;
    paramArrayOfFloat[i] = paramRectF.top;
    i = paramInt + 1;
    paramArrayOfFloat[paramInt] = paramRectF.right;
    paramInt = i + 1;
    paramArrayOfFloat[i] = paramRectF.top;
    i = paramInt + 1;
    paramArrayOfFloat[paramInt] = paramRectF.right;
    paramInt = i + 1;
    paramArrayOfFloat[i] = paramRectF.bottom;
    paramArrayOfFloat[paramInt] = paramRectF.left;
    paramArrayOfFloat[(paramInt + 1)] = paramRectF.bottom;
  }
  
  public static void convertRectToPoints(float[] paramArrayOfFloat, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    int i = paramInt + 1;
    paramArrayOfFloat[paramInt] = paramFloat1;
    paramInt = i + 1;
    paramArrayOfFloat[i] = paramFloat2;
    i = paramInt + 1;
    paramArrayOfFloat[paramInt] = paramFloat3;
    paramInt = i + 1;
    paramArrayOfFloat[i] = paramFloat2;
    i = paramInt + 1;
    paramArrayOfFloat[paramInt] = paramFloat3;
    paramInt = i + 1;
    paramArrayOfFloat[i] = paramFloat4;
    paramArrayOfFloat[paramInt] = paramFloat1;
    paramArrayOfFloat[(paramInt + 1)] = paramFloat4;
  }
  
  public static void getBoundingBox(RectF paramRectF, float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0)
    {
      paramRectF.setEmpty();
      return;
    }
    int i = paramInt1 + 1;
    float f7 = paramArrayOfFloat[paramInt1];
    float f1 = paramArrayOfFloat[i];
    float f5 = f7;
    float f2 = f1;
    paramInt1 = 1;
    i += 1;
    if (paramInt1 < paramInt2)
    {
      int j = i + 1;
      float f8 = paramArrayOfFloat[i];
      i = j + 1;
      float f6 = paramArrayOfFloat[j];
      float f3;
      float f4;
      if (f8 < f7)
      {
        f3 = f8;
        f4 = f5;
        label86:
        if (f6 >= f1) {
          break label152;
        }
        f8 = f6;
        f5 = f2;
      }
      for (;;)
      {
        paramInt1 += 1;
        f2 = f5;
        f7 = f3;
        f5 = f4;
        f1 = f8;
        break;
        f3 = f7;
        f4 = f5;
        if (f8 <= f5) {
          break label86;
        }
        f4 = f8;
        f3 = f7;
        break label86;
        label152:
        f5 = f2;
        f8 = f1;
        if (f6 > f2)
        {
          f5 = f6;
          f8 = f1;
        }
      }
    }
    paramRectF.set(f7, f1, f5, f2);
  }
  
  public static void getCentroid(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, int paramInt3)
  {
    if (paramInt3 <= 0) {
      return;
    }
    int i = paramInt2 + 1;
    float f2 = paramArrayOfFloat2[paramInt2];
    float f1 = paramArrayOfFloat2[i];
    paramInt2 = paramInt3 - 1;
    i += 1;
    while (paramInt2 > 0)
    {
      int j = i + 1;
      f2 += paramArrayOfFloat2[i];
      i = j + 1;
      f1 += paramArrayOfFloat2[j];
      paramInt2 -= 1;
    }
    paramArrayOfFloat1[paramInt1] = (f2 / paramInt3);
    paramArrayOfFloat1[(paramInt1 + 1)] = (f1 / paramInt3);
  }
  
  private static boolean isValidScalingFactor(float paramFloat, boolean paramBoolean)
  {
    if ((Float.isNaN(paramFloat)) || (Float.isInfinite(paramFloat))) {
      return false;
    }
    if (paramFloat == 0.0F) {
      return paramBoolean;
    }
    return true;
  }
  
  public static void offsetPoints(float paramFloat1, float paramFloat2, float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, int paramInt3)
  {
    int i = paramInt3;
    paramInt3 = paramInt2;
    paramInt2 = paramInt1;
    paramInt1 = i;
    while (paramInt1 > 0)
    {
      i = paramInt2 + 1;
      int j = paramInt3 + 1;
      paramArrayOfFloat2[paramInt3] += paramFloat1;
      paramInt2 = i + 1;
      paramInt3 = j + 1;
      paramArrayOfFloat2[j] += paramFloat2;
      paramInt1 -= 1;
    }
  }
  
  public static void scalePoints(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2, int paramInt3)
  {
    while (paramInt3 > 0)
    {
      int i = paramInt2 + 1;
      float f1 = paramArrayOfFloat2[paramInt2];
      paramInt2 = i + 1;
      float f2 = paramArrayOfFloat2[i];
      i = paramInt1 + 1;
      paramArrayOfFloat1[paramInt1] = ((f1 - paramFloat3) * paramFloat1 + paramFloat3);
      paramInt1 = i + 1;
      paramArrayOfFloat1[i] = ((f2 - paramFloat4) * paramFloat2 + paramFloat4);
      paramInt3 -= 1;
    }
  }
  
  public static void scaleRect(Rect paramRect1, Rect paramRect2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float f1 = paramRect2.left;
    float f2 = paramRect2.top;
    float f3 = paramRect2.right;
    float f4 = paramRect2.bottom;
    paramRect1.left = Math.round(paramFloat3 - (paramFloat3 - f1) * paramFloat1);
    paramRect1.top = Math.round(paramFloat4 - (paramFloat4 - f2) * paramFloat2);
    paramRect1.right = Math.round(paramFloat3 + (f3 - paramFloat3) * paramFloat1);
    paramRect1.bottom = Math.round(paramFloat4 + (f4 - paramFloat4) * paramFloat2);
  }
  
  public static void scaleRect(Rect paramRect1, Rect paramRect2, float paramFloat1, float paramFloat2, int paramInt)
  {
    float[] arrayOfFloat = new float[2];
    selectPivot(arrayOfFloat, 0, paramRect2, paramInt);
    scaleRect(paramRect1, paramRect2, paramFloat1, paramFloat2, arrayOfFloat[0], arrayOfFloat[1]);
  }
  
  public static void scaleRect(RectF paramRectF1, RectF paramRectF2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float f1 = paramRectF2.left;
    float f2 = paramRectF2.top;
    float f3 = paramRectF2.right;
    float f4 = paramRectF2.bottom;
    paramRectF1.left = (paramFloat3 - (paramFloat3 - f1) * paramFloat1);
    paramRectF1.top = (paramFloat4 - (paramFloat4 - f2) * paramFloat2);
    paramRectF1.right = (paramFloat3 + (f3 - paramFloat3) * paramFloat1);
    paramRectF1.bottom = (paramFloat4 + (f4 - paramFloat4) * paramFloat2);
  }
  
  public static void scaleRect(RectF paramRectF1, RectF paramRectF2, float paramFloat1, float paramFloat2, int paramInt)
  {
    float[] arrayOfFloat = new float[2];
    selectPivot(arrayOfFloat, 0, paramRectF2, paramInt);
    scaleRect(paramRectF1, paramRectF2, paramFloat1, paramFloat2, arrayOfFloat[0], arrayOfFloat[1]);
  }
  
  public static void selectPivot(float[] paramArrayOfFloat, int paramInt1, Rect paramRect, int paramInt2)
  {
    if ((0x20000 & paramInt2) != 0)
    {
      paramArrayOfFloat[paramInt1] = paramRect.left;
      paramInt1 += 1;
    }
    while ((0x40000 & paramInt2) != 0)
    {
      paramArrayOfFloat[paramInt1] = paramRect.top;
      return;
      if ((0x80000 & paramInt2) != 0)
      {
        paramArrayOfFloat[paramInt1] = paramRect.right;
        paramInt1 += 1;
      }
      else
      {
        paramArrayOfFloat[paramInt1] = paramRect.centerX();
        paramInt1 += 1;
      }
    }
    if ((0x100000 & paramInt2) != 0)
    {
      paramArrayOfFloat[paramInt1] = paramRect.bottom;
      return;
    }
    paramArrayOfFloat[paramInt1] = paramRect.centerY();
  }
  
  public static void selectPivot(float[] paramArrayOfFloat, int paramInt1, RectF paramRectF, int paramInt2)
  {
    if ((0x20000 & paramInt2) != 0)
    {
      paramArrayOfFloat[paramInt1] = paramRectF.left;
      paramInt1 += 1;
    }
    while ((0x40000 & paramInt2) != 0)
    {
      paramArrayOfFloat[paramInt1] = paramRectF.top;
      return;
      if ((0x80000 & paramInt2) != 0)
      {
        paramArrayOfFloat[paramInt1] = paramRectF.right;
        paramInt1 += 1;
      }
      else
      {
        paramArrayOfFloat[paramInt1] = paramRectF.centerX();
        paramInt1 += 1;
      }
    }
    if ((0x100000 & paramInt2) != 0)
    {
      paramArrayOfFloat[paramInt1] = paramRectF.bottom;
      return;
    }
    paramArrayOfFloat[paramInt1] = paramRectF.centerY();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/Geometry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */