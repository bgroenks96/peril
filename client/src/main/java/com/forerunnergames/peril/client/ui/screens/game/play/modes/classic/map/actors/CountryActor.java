package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountrySpriteData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.sprites.CountrySprite;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.sprites.CountrySpriteState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.tools.CoordinateSpaces;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.geometry.Geometry;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

// @formatter:off
public final class CountryActor extends Actor
{
  private final CountrySprite countrySprite;
  private final CountrySpriteData spriteData;
  private final Sprite hoveredSprite;
  private final Sprite clickedSprite;
  private final float x;
  private final float y;
  private final float width;
  private final float height;
  private CountrySpriteState currentState;
  private Sprite currentSprite;
  private boolean isHovered = false;
  private boolean isTouchDown = false;

  public CountryActor (final CountrySprite countrySprite, final CountrySpriteData spriteData)
  {
    Arguments.checkIsNotNull (countrySprite, "countrySprite");
    Arguments.checkIsNotNull (spriteData, "spriteData");

    this.countrySprite = countrySprite;
    this.spriteData = spriteData;
    this.hoveredSprite = countrySprite.get (CountrySpriteState.HIGHLIGHT);
    this.clickedSprite = countrySprite.get (CountrySpriteState.DISABLED);

    final Point2D destReferenceScreenSpace = CoordinateSpaces.referencePlayMapSpaceToReferenceScreenSpace (spriteData.getDestPlayMapReferenceSpace ());
    x = destReferenceScreenSpace.getX();
    y = GraphicsSettings.REFERENCE_SCREEN_HEIGHT - destReferenceScreenSpace.getY();
    final Size2D sizeActualPlayMapSpace = Geometry.scale (spriteData.getSizePlayMapReferenceSpace(), PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);
    width = sizeActualPlayMapSpace.getWidth ();
    height = sizeActualPlayMapSpace.getHeight ();

    hoveredSprite.setSize (width, height);
    clickedSprite.setSize (width, height);
    hoveredSprite.setPosition (x, y);
    clickedSprite.setPosition (x, y);

    setName (spriteData.getName ());
    setPosition (x, y);
    setSize (width, height);
    setBounds (0, 0, width, height);
    changeStateTo (CountrySpriteState.UNOWNED);
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    currentSprite.draw (batch, parentAlpha);

    if (PlayMapSettings.ENABLE_HOVER_EFFECTS && isHovered) hoveredSprite.draw (batch, parentAlpha);
    if (PlayMapSettings.ENABLE_CLICK_EFFECTS && isTouchDown) clickedSprite.draw (batch, parentAlpha);
  }

  public CountrySpriteState getCurrentState ()
  {
    return currentState;
  }

  public void changeStateRandomly ()
  {
    CountrySpriteState randomState;

    do
    {
      randomState = Randomness.getRandomElementFrom (CountrySpriteState.values ());
    }
    while (randomState.is (currentState));

    changeStateTo (randomState);
  }

  public void changeStateTo (final CountrySpriteState state)
  {
    Arguments.checkIsNotNull (state, "state");

    currentState = state;
    currentSprite = countrySprite.get (state);
    currentSprite.setPosition (x, y);
    currentSprite.setSize (width, height);
  }

  public void nextState ()
  {
    final CountrySpriteState state = getCurrentState ();

    changeStateTo (state.hasNext () ? state.next () : state.first());
  }

  public void onHoverStart ()
  {
    isHovered = true;
  }

  public void onHoverEnd ()
  {
    isHovered = false;
  }

  public void onTouchDown ()
  {
    isTouchDown = true;
  }

  public void onTouchUp ()
  {
    isTouchDown = false;
  }

  public Sprite getCurrentSprite()
  {
    return countrySprite.get (currentState);
  }

  public Point2D getDestPlayMapReferenceSpace()
  {
    return spriteData.getDestPlayMapReferenceSpace ();
  }

  public Point2D getCenterPlayMapReferenceSpace()
  {
    return spriteData.getCenterPlayMapReferenceSpace ();
  }

  public Size2D getSizePlayMapReferenceSpace()
  {
    return spriteData.getSizePlayMapReferenceSpace ();
  }
}
