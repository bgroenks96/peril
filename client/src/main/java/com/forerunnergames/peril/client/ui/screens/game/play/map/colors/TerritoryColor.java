package com.forerunnergames.peril.client.ui.screens.game.play.map.colors;

import com.forerunnergames.tools.common.color.RgbaColorComponent;

public interface TerritoryColor <T extends RgbaColorComponent>
{
  public T getComponent ();

  public int hashCode ();

  public boolean equals (final Object o);
}
