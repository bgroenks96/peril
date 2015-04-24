package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.math.Vector2;

public interface ScreenSize
{
  int referenceWidth ();

  int referenceHeight ();

  Vector2 reference ();

  int actualWidth ();

  int actualHeight ();

  Vector2 actual ();

  float actualToReferenceScalingX ();

  float actualToReferenceScalingY ();

  Vector2 actualToReferenceScaling ();

  float referenceToActualScalingX ();

  float referenceToActualScalingY ();

  Vector2 referenceToActualScaling ();
}
