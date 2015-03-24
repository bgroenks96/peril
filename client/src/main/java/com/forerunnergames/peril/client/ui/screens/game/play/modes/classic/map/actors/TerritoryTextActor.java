package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.sprites.CountrySpriteState;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.geometry.Geometry;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Scaling2D;
import com.forerunnergames.tools.common.geometry.Size2D;

public final class TerritoryTextActor extends Actor
{
  private static final int TEXT_OFFSET_X = 35;
  private static final int TEXT_OFFSET_Y = -17;
  private final PlayMapInputDetection playMapInputDetection;
  private final MouseInput mouseInput;
  private final BitmapFont font;
  private CountrySpriteState countryState;
  private Point2D mouseHoverCoordinate;
  private String territoryNames;
  private Size2D screenSize;
  private Scaling2D scaling;
  private float x;
  private float y;

  public TerritoryTextActor (final PlayMapInputDetection playMapInputDetection, final MouseInput mouseInput)
  {
    Arguments.checkIsNotNull (playMapInputDetection, "playMapInputDetection");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");

    this.playMapInputDetection = playMapInputDetection;
    this.mouseInput = mouseInput;
    font = new BitmapFont ();

    setHoveredCountryState (CountrySpriteState.UNOWNED);
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    font.draw (batch, territoryNames, x, y);
  }

  @Override
  public void act (float delta)
  {
    super.act (delta);

    if (shouldUpdateScreenSize ()) updateScreenSize ();

    mouseHoverCoordinate = mouseInput.getHoverCoordinate ();

    x = (mouseHoverCoordinate.getX () + TEXT_OFFSET_X) * scaling.getX ();
    y = (screenSize.getHeight () - mouseHoverCoordinate.getY () + TEXT_OFFSET_Y) * scaling.getY ();

    territoryNames = playMapInputDetection.getPrintableTerritoryNamesAt (mouseHoverCoordinate, screenSize)
            + (countryState.is (CountrySpriteState.UNOWNED) ? "" : ", " + Strings.toProperCase (countryState.name ()));
  }

  public void setHoveredCountryState (final CountrySpriteState countryState)
  {
    Arguments.checkIsNotNull (countryState, "countryState");

    this.countryState = countryState;
  }

  private boolean shouldUpdateScreenSize ()
  {
    return screenSize == null || scaling == null
            || screenSize.isNot (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
  }

  private void updateScreenSize ()
  {
    screenSize = new Size2D (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    scaling = Geometry.divide (GraphicsSettings.REFERENCE_SCREEN_SIZE, screenSize);
    font.getData ().setScale (scaling.getX (), scaling.getY ());
  }
}
