package com.forerunnergames.peril.client.input;

import com.badlogic.gdx.Input;

import com.forerunnergames.tools.common.Arguments;

public final class LibGdxMouseInput implements MouseInput
{
  private final Input input;

  public LibGdxMouseInput (final Input input)
  {
    Arguments.checkIsNotNull (input, "input");

    this.input = input;
  }

  @Override
  public float getHoverX ()
  {
    return input.getX ();
  }

  @Override
  public float getHoverY ()
  {
    return input.getY ();
  }
}
