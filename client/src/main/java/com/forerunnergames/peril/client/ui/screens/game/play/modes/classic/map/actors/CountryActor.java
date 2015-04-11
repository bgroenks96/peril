package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.tools.CoordinateSpaces;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.geometry.Geometry;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;
import com.forerunnergames.tools.common.geometry.Translation2D;

import java.util.SortedMap;

public final class CountryActor extends Group
{
  private final SortedMap <CountryImageState, Image> countryImageStatesToImages;
  private final CountryImageData countryImageData;
  private final CountryArmyTextActor countryArmyTextActor;
  private CountryImageState currentImageState = CountryImageState.UNOWNED;
  private Image currentImage;

  // @formatter:off
  public CountryActor (final SortedMap <CountryImageState, Image> countryImageStatesToImages,
                       final CountryImageData countryImageData,
                       final CountryArmyTextActor countryArmyTextActor)
  {
    Arguments.checkIsNotNull (countryImageStatesToImages, "countryImageStatesToImages");
    Arguments.checkHasNoNullKeysOrValues (countryImageStatesToImages, "countryImageStatesToImages");
    Arguments.checkIsNotNull (countryImageData, "countryImageData");
    Arguments.checkIsNotNull (countryArmyTextActor, "countryArmyTextActor");

    this.countryImageStatesToImages = countryImageStatesToImages;
    this.countryImageData = countryImageData;
    this.countryArmyTextActor = countryArmyTextActor;

    final Point2D destPlayMapReferenceSpaceFlippedY =
            Geometry.absoluteValue (
                    Geometry.translate (
                            countryImageData.getDestPlayMapReferenceSpace (),
                            new Translation2D (0, -PlayMapSettings.REFERENCE_HEIGHT)));

    final Point2D destPlayMapActualSpaceFlippedY =
            CoordinateSpaces.referencePlayMapSpaceToActualPlayMapSpace (destPlayMapReferenceSpaceFlippedY);

    setName (countryImageData.getName ());

    for (final Image countryImage : countryImageStatesToImages.values ())
    {
      countryImage.setVisible (false);
      countryImage.setPosition (destPlayMapActualSpaceFlippedY.getX (), destPlayMapActualSpaceFlippedY.getY ());
      countryImage.setScale (
              PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING.getX (),
              PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING.getY ());
      addActor (countryImage);
    }

    addActor (countryArmyTextActor);

    changeStateTo (CountryImageState.UNOWNED);
  }
  // @formatter:on

  public CountryImageState getCurrentImageState ()
  {
    return currentImageState;
  }

  public void changeStateRandomly ()
  {
    CountryImageState randomState;

    do
    {
      randomState = Randomness.getRandomElementFrom (CountryImageState.values ());
    }
    while (randomState.is (currentImageState));

    changeStateTo (randomState);
  }

  public void changeStateTo (final CountryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    if (state.is (CountryImageState.HIGHLIGHT)) return;

    hide (currentImageState);
    currentImageState = state;
    currentImage = countryImageStatesToImages.get (currentImageState);
    countryArmyTextActor.onStateChange (state);
    show (currentImageState);
  }

  public void nextState ()
  {
    final CountryImageState state = getCurrentImageState ();

    changeStateTo (state.hasNextValid () ? state.nextValid () : state.first ());
  }

  public void onHoverStart ()
  {
    if (!PlayMapSettings.ENABLE_HOVER_EFFECTS) return;
    if (currentImageState == CountryImageState.DISABLED) return;

    show (CountryImageState.HIGHLIGHT);
  }

  public void onHoverEnd ()
  {
    if (!PlayMapSettings.ENABLE_HOVER_EFFECTS) return;

    hide (CountryImageState.HIGHLIGHT);
  }

  public void onTouchDown ()
  {
    if (!PlayMapSettings.ENABLE_CLICK_EFFECTS) return;
    if (currentImageState == CountryImageState.DISABLED) return;

    hide (currentImageState);
    show (CountryImageState.DISABLED);
  }

  public void onTouchUp ()
  {
    if (!PlayMapSettings.ENABLE_CLICK_EFFECTS) return;

    hide (CountryImageState.DISABLED);
    show (currentImageState);
  }

  public Image getCurrentImage ()
  {
    return currentImage;
  }

  public Point2D getDestPlayMapReferenceSpace ()
  {
    return countryImageData.getDestPlayMapReferenceSpace ();
  }

  public Point2D getCenterPlayMapReferenceSpace ()
  {
    return countryImageData.getTextUpperLeftPlayMapReferenceSpace ();
  }

  public Size2D getSizePlayMapReferenceSpace ()
  {
    return countryImageData.getSizePlayMapReferenceSpace ();
  }

  public void setArmies (final int armies)
  {
    countryArmyTextActor.setArmies (armies);
  }

  public void incrementArmies ()
  {
    countryArmyTextActor.incrementArmies ();
  }

  public void decrementArmies ()
  {
    countryArmyTextActor.decrementArmies ();
  }

  public void changeArmiesBy (final int deltaArmies)
  {
    countryArmyTextActor.changeArmiesBy (deltaArmies);
  }

  private void hide (final CountryImageState state)
  {
    setStateVisibility (state, false);
  }

  private void show (final CountryImageState state)
  {
    setStateVisibility (state, true);
  }

  private void setStateVisibility (final CountryImageState state, final boolean isVisible)
  {
    countryImageStatesToImages.get (state).setVisible (isVisible);
  }
}
