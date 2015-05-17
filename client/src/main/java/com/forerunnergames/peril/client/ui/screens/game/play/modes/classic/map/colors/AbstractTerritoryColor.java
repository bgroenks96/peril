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
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    final AbstractTerritoryColor that = (AbstractTerritoryColor) obj;

    return colorComponent.equals (that.colorComponent);
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Color Component: %2$s", getClass ().getSimpleName (), colorComponent);
  }
}
