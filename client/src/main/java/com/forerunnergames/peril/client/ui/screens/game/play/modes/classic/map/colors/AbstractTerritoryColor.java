package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.color.RgbaColorComponent;

public abstract class AbstractTerritoryColor <T extends RgbaColorComponent> implements TerritoryColor <T>
{
  private final T colorComponent;

  protected AbstractTerritoryColor (final T colorComponent)
  {
    Arguments.checkIsNotNull (colorComponent, "colorComponent");

    this.colorComponent = colorComponent;
  }

  @Override
  public final T getComponent ()
  {
    return colorComponent;
  }

  @Override
  public int hashCode ()
  {
    return colorComponent.hashCode ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass () != o.getClass ()) return false;

    final AbstractTerritoryColor that = (AbstractTerritoryColor) o;

    return colorComponent.equals (that.colorComponent);
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Color Component: %2$s", getClass ().getSimpleName (), colorComponent);
  }
}
