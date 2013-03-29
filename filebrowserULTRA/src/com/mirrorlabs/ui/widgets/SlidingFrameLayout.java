package com.mirrorlabs.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class SlidingFrameLayout extends FrameLayout
{
  private final int durationMilliseconds = 1000;
  private final int displacementPixels = 200;

  private boolean isInOriginalPosition = true;
  private boolean isSliding = false;

  public SlidingFrameLayout(Context context)
  {
    super(context);
  }

  public SlidingFrameLayout(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public SlidingFrameLayout(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onAnimationEnd()
  {
    super.onAnimationEnd();

    if (isInOriginalPosition)
      offsetLeftAndRight(displacementPixels);
    else
      offsetLeftAndRight(-displacementPixels);

    isSliding = false;
    isInOriginalPosition = !isInOriginalPosition;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom)
  {
    super.onLayout(changed, left, top, right, bottom);

    // need this since otherwise this View jumps back to its original position
    // ignoring its displacement
    // when (re-)doing layout, e.g. when a fragment transaction is committed
    if (changed && !isInOriginalPosition)
      offsetLeftAndRight(displacementPixels);
  }

  public void toggleSlide()
  {
    // check whether frame layout is already sliding
    if (isSliding)
      return; // ignore request to slide

    if (isInOriginalPosition)
      startAnimation(new SlideRightAnimation());
    else
      startAnimation(new SlideLeftAnimation());

    isSliding = true;
  }

  private class SlideRightAnimation extends TranslateAnimation
  {
    public SlideRightAnimation()
    {
      super(
          Animation.ABSOLUTE, 0,
          Animation.ABSOLUTE, displacementPixels,
          Animation.ABSOLUTE, 0,
          Animation.ABSOLUTE, 0);

      setDuration(durationMilliseconds);
      setFillAfter(false);
    }
  }

  private class SlideLeftAnimation extends TranslateAnimation
  {
    public SlideLeftAnimation()
    {
      super(
          Animation.ABSOLUTE, 0,
          Animation.ABSOLUTE, -displacementPixels,
          Animation.ABSOLUTE, 0,
          Animation.ABSOLUTE, 0);

      setDuration(durationMilliseconds);
      setFillAfter(false);
    }
  }
}

