package com.forerunnergames.peril.client.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.tools.common.Arguments;

public final class LibGdxMouseInput implements MouseInput
{
  private final Input input;
  private final Vector2 temp = new Vector2 ();

  public LibGdxMouseInput (final Input input)
  {
    Arguments.checkIsNotNull (input, "input");

    this.input = input;
  }

  @Override
  public int x ()
  {
    return input.getX ();
  }

  @Override
  public int y ()
  {
    return input.getY ();
  }

  @Override
  public Vector2 position ()
  {
    return temp.set (input.getX (), input.getY ());
  }
}
