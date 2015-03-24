package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors;

import com.forerunnergames.tools.common.color.RedRgbaColorComponent;

public final class CountryColor extends AbstractTerritoryColor <RedRgbaColorComponent>
{
  public CountryColor (final int redColorComponentValue)
  {
    this (new RedRgbaColorComponent (redColorComponentValue));
  }

  public CountryColor (final RedRgbaColorComponent redColorComponent)
  {
    super (redColorComponent);
  }
}
