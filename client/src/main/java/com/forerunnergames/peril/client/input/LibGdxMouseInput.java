package com.forerunnergames.peril.client.input;

import com.badlogic.gdx.Input;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Point2D;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public final class LibGdxMouseInput implements MouseInput
{
  private final Table <Integer, Integer, Point2D> pointCache = HashBasedTable.create ();
  private final Input input;
  private Point2D point;
  private int x;
  private int y;

  public LibGdxMouseInput (final Input input)
  {
    Arguments.checkIsNotNull (input, "input");

    this.input = input;
  }

  @Override
  public Point2D getHoverCoordinate ()
  {
    x = input.getX();
    y = input.getY();

    point = pointCache.get (x, y);

    if (point != null) return point;

    point = new Point2D (x, y);
    pointCache.put (x, y, point);

    return point;
  }
}
