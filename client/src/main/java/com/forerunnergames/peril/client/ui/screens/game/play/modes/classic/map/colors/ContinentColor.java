package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors;

import com.forerunnergames.tools.common.color.GreenRgbaColorComponent;

public final class ContinentColor extends AbstractTerritoryColor <GreenRgbaColorComponent>
{
  public ContinentColor (final int greenColorComponentValue)
  {
    this (new GreenRgbaColorComponent (greenColorComponentValue));
  }

  public ContinentColor (final GreenRgbaColorComponent greenColorComponent)
  {
    super (greenColorComponent);
  }
}
