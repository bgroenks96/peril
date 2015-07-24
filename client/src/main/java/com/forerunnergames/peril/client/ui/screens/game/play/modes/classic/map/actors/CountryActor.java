package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.client.settings.ClassicPlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImages;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImageState;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;

public final class CountryActor extends Group
{
  private final CountryImages <CountryPrimaryImageState, CountryPrimaryImage> primaryImages;
  private final CountryImages <CountrySecondaryImageState, CountrySecondaryImage> secondaryImages;
  private final CountryImageData imageData;
  private final CountryArmyTextActor armyTextActor;
  private CountryPrimaryImageState currentPrimaryImageState = CountryPrimaryImageState.UNOWNED;
  private CountrySecondaryImageState currentSecondaryImageState = CountrySecondaryImageState.NONE;
  private boolean isEnabled = true;
  private CountryImage <CountryPrimaryImageState> currentPrimaryImage;
  private CountryImage <CountrySecondaryImageState> currentSecondaryImage;

  public CountryActor (final CountryImages <CountryPrimaryImageState, CountryPrimaryImage> primaryImages,
                       final CountryImages <CountrySecondaryImageState, CountrySecondaryImage> secondaryImages,
                       final CountryImageData imageData,
                       final CountryArmyTextActor armyTextActor)
  {
    Arguments.checkIsNotNull (primaryImages, "primaryImages");
    Arguments.checkIsNotNull (secondaryImages, "secondaryImages");
    Arguments.checkIsNotNull (imageData, "imageData");
    Arguments.checkIsNotNull (armyTextActor, "armyTextActor");

    this.primaryImages = primaryImages;
    this.secondaryImages = secondaryImages;
    this.imageData = imageData;
    this.armyTextActor = armyTextActor;

    final Vector2 tempPosition = new Vector2 (imageData.getReferenceDestination ());
    tempPosition.y = ClassicPlayMapSettings.REFERENCE_HEIGHT - tempPosition.y;
    tempPosition.scl (ClassicPlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);

    setName (imageData.getName ());
    setTransform (false);

    for (final CountryPrimaryImage primaryImage : primaryImages.getAll ())
    {
      primaryImage.setVisible (false);
      primaryImage.setPosition (tempPosition);
      primaryImage.setScale (ClassicPlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);
      addActor (primaryImage);
    }

    for (final CountrySecondaryImage countrySecondaryImage : secondaryImages.getAll ())
    {
      countrySecondaryImage.setVisible (false);
      countrySecondaryImage.setPosition (tempPosition);
      countrySecondaryImage.setScale (ClassicPlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);
      addActor (countrySecondaryImage);
    }

    changePrimaryStateTo (currentPrimaryImageState);
    changeSecondaryStateTo (currentSecondaryImageState);
  }

  public CountryPrimaryImageState getCurrentPrimaryImageState ()
  {
    return currentPrimaryImageState;
  }

  public CountrySecondaryImageState getCurrentSecondaryImageState ()
  {
    return currentSecondaryImageState;
  }

  public void changePrimaryStateRandomly ()
  {
    CountryPrimaryImageState randomState;

    do
    {
      randomState = Randomness.getRandomElementFrom (CountryPrimaryImageState.values ());
    }
    while (randomState.is (currentPrimaryImageState));

    changePrimaryStateTo (randomState);
  }

  public void changePrimaryStateTo (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    hide (currentPrimaryImageState);
    currentPrimaryImageState = state;
    currentPrimaryImage = primaryImages.get (state);
    armyTextActor.onPrimaryStateChange (state);
    show (state);
  }

  public void changeSecondaryStateTo (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    hide (currentSecondaryImageState);
    currentSecondaryImageState = state;
    currentSecondaryImage = secondaryImages.get (state);
    show (state);
  }

  public void nextPrimaryState ()
  {
    changePrimaryStateTo (currentPrimaryImageState.hasNext () ? currentPrimaryImageState.next ()
            : currentPrimaryImageState.first ());
  }

  public void onHoverStart ()
  {
    if (!isEnabled || !ClassicPlayMapSettings.ENABLE_HOVER_EFFECTS) return;
    if (currentPrimaryImageState == CountryPrimaryImageState.DISABLED) return;

    show (CountrySecondaryImageState.HOVERED);
  }

  public void onHoverEnd ()
  {
    if (!isEnabled || !ClassicPlayMapSettings.ENABLE_HOVER_EFFECTS) return;

    hide (CountrySecondaryImageState.HOVERED);
  }

  public void onTouchDown ()
  {
    if (!isEnabled || !ClassicPlayMapSettings.ENABLE_CLICK_EFFECTS) return;
    if (currentPrimaryImageState == CountryPrimaryImageState.DISABLED) return;
  }

  public void onTouchUp ()
  {
    if (!isEnabled || !ClassicPlayMapSettings.ENABLE_CLICK_EFFECTS) return;
  }

  public Drawable getCurrentPrimaryDrawable ()
  {
    return currentPrimaryImage.getDrawable ();
  }

  public Vector2 getReferenceDestination ()
  {
    return imageData.getReferenceDestination ();
  }

  public Vector2 getReferenceTextUpperLeft ()
  {
    return imageData.getReferenceTextUpperLeft ();
  }

  public float getReferenceWidth ()
  {
    return imageData.getReferenceWidth ();
  }

  public float getReferenceHeight ()
  {
    return imageData.getReferenceHeight ();
  }

  public void setArmies (final int armies)
  {
    armyTextActor.setArmies (armies);
  }

  public void incrementArmies ()
  {
    armyTextActor.incrementArmies ();
  }

  public void decrementArmies ()
  {
    armyTextActor.decrementArmies ();
  }

  public void changeArmiesBy (final int deltaArmies)
  {
    armyTextActor.changeArmiesBy (deltaArmies);
  }

  public void disable ()
  {
    isEnabled = false;
  }

  public void enable ()
  {
    isEnabled = true;
  }

  public int getAtlasIndex ()
  {
    return primaryImages.getAtlasIndex ();
  }

  public CountryArmyTextActor getArmyTextActor ()
  {
    return armyTextActor;
  }

  private void hide (final CountryPrimaryImageState state)
  {
    primaryImages.hide (state);
  }

  private void hide (final CountrySecondaryImageState state)
  {
    secondaryImages.hide (state);
  }

  private void show (final CountryPrimaryImageState state)
  {
    primaryImages.show (state);
  }

  private void show (final CountrySecondaryImageState state)
  {
    secondaryImages.show (state);
  }

  @Override
  public String toString ()
  {
    return String
            .format ("%1$s | Name: %2$s | Current Primary Image State: %3$s | Current Secondary Image State: %4$s"
                             + " | Current Primary Image: %5$s  | Current Secondary Image: %6$s | Enabled: %7$s | "
                             + "Image Data: %8$s | Army Text Actor: %9$s | Primary Images: %10$s "
                             + "| Secondary Images: %11$s", getClass ().getSimpleName (), getName (),
                     currentPrimaryImageState, currentSecondaryImageState, currentPrimaryImage, currentSecondaryImage,
                     isEnabled, imageData, armyTextActor, primaryImages, secondaryImages);
  }
}
