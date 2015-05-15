package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.settings.ClassicPlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageState;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;

import java.util.SortedMap;

public final class CountryActor extends Group
{
  private final SortedMap <CountryImageState, Image> countryImageStatesToImages;
  private final CountryImageData countryImageData;
  private final CountryArmyTextActor countryArmyTextActor;
  private CountryImageState currentImageState = CountryImageState.UNOWNED;
  private boolean isEnabled = true;
  private Image currentImage;

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

    final Vector2 tempPosition = new Vector2 (countryImageData.getReferenceDestination ());
    tempPosition.y = ClassicPlayMapSettings.REFERENCE_HEIGHT - tempPosition.y;
    tempPosition.scl (ClassicPlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);

    setName (countryImageData.getName ());

    for (final Image countryImage : countryImageStatesToImages.values ())
    {
      countryImage.setVisible (false);
      countryImage.setPosition (tempPosition.x, tempPosition.y);
      countryImage.setScale (ClassicPlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING.x,
                             ClassicPlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING.y);
      addActor (countryImage);
    }

    addActor (countryArmyTextActor);

    changeStateTo (CountryImageState.UNOWNED);
  }

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
    if (!isEnabled || !ClassicPlayMapSettings.ENABLE_HOVER_EFFECTS) return;
    if (currentImageState == CountryImageState.DISABLED) return;

    show (CountryImageState.HIGHLIGHT);
  }

  public void onHoverEnd ()
  {
    if (!isEnabled || !ClassicPlayMapSettings.ENABLE_HOVER_EFFECTS) return;

    hide (CountryImageState.HIGHLIGHT);
  }

  public void onTouchDown ()
  {
    if (!isEnabled || !ClassicPlayMapSettings.ENABLE_CLICK_EFFECTS) return;
    if (currentImageState == CountryImageState.DISABLED) return;

    hide (currentImageState);
    show (CountryImageState.DISABLED);
  }

  public void onTouchUp ()
  {
    if (!isEnabled || !ClassicPlayMapSettings.ENABLE_CLICK_EFFECTS) return;

    hide (CountryImageState.DISABLED);
    show (currentImageState);
  }

  public Image getCurrentImage ()
  {
    return currentImage;
  }

  public Vector2 getReferenceDestination ()
  {
    return countryImageData.getReferenceDestination ();
  }

  public Vector2 getReferenceTextUpperLeft ()
  {
    return countryImageData.getReferenceTextUpperLeft ();
  }

  public float getReferenceWidth ()
  {
    return countryImageData.getReferenceWidth ();
  }

  public float getReferenceHeight ()
  {
    return countryImageData.getReferenceHeight ();
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

  public void disable ()
  {
    isEnabled = false;
  }

  public void enable ()
  {
    isEnabled = true;
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
