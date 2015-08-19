package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImages;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImageState;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;

public final class DefaultCountryActor implements CountryActor
{
  private final Group group = new Group ();
  private final CountryImages <CountryPrimaryImageState, CountryPrimaryImage> primaryImages;
  private final CountryImages <CountrySecondaryImageState, CountrySecondaryImage> secondaryImages;
  private final CountryImageData imageData;
  private final CountryArmyTextActor armyTextActor;
  private CountryPrimaryImageState currentPrimaryImageState = CountryPrimaryImageState.UNOWNED;
  private CountrySecondaryImageState currentSecondaryImageState = CountrySecondaryImageState.NONE;
  private boolean isEnabled = true;
  private CountryImage <CountryPrimaryImageState> currentPrimaryImage;
  private CountryImage <CountrySecondaryImageState> currentSecondaryImage;

  public DefaultCountryActor (final CountryImages <CountryPrimaryImageState, CountryPrimaryImage> primaryImages,
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
    tempPosition.y = PlayMapSettings.REFERENCE_HEIGHT - tempPosition.y;
    tempPosition.scl (PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);

    group.setName (imageData.getName ());
    group.setTransform (false);

    for (final CountryPrimaryImage primaryImage : primaryImages.getAll ())
    {
      primaryImage.setVisible (false);
      primaryImage.setPosition (tempPosition);
      primaryImage.setScale (PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);
      group.addActor (primaryImage);
    }

    for (final CountrySecondaryImage countrySecondaryImage : secondaryImages.getAll ())
    {
      countrySecondaryImage.setVisible (false);
      countrySecondaryImage.setPosition (tempPosition);
      countrySecondaryImage.setScale (PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);
      group.addActor (countrySecondaryImage);
    }

    changePrimaryStateTo (currentPrimaryImageState);
    changeSecondaryStateTo (currentSecondaryImageState);
  }

  @Override
  public CountryPrimaryImageState getCurrentPrimaryImageState ()
  {
    return currentPrimaryImageState;
  }

  @Override
  public CountrySecondaryImageState getCurrentSecondaryImageState ()
  {
    return currentSecondaryImageState;
  }

  @Override
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

  @Override
  public void changePrimaryStateTo (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    hide (currentPrimaryImageState);
    currentPrimaryImageState = state;
    currentPrimaryImage = primaryImages.get (state);
    armyTextActor.onPrimaryStateChange (state);
    show (state);
  }

  @Override
  public void changeSecondaryStateTo (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    hide (currentSecondaryImageState);
    currentSecondaryImageState = state;
    currentSecondaryImage = secondaryImages.get (state);
    show (state);
  }

  @Override
  public void nextPrimaryState ()
  {
    changePrimaryStateTo (currentPrimaryImageState.hasNext () ? currentPrimaryImageState.next () : currentPrimaryImageState.first ());
  }

  @Override
  public void onHoverStart ()
  {
    if (!isEnabled || !PlayMapSettings.ENABLE_HOVER_EFFECTS) return;
    if (currentPrimaryImageState == CountryPrimaryImageState.DISABLED) return;

    changeSecondaryStateTo (CountrySecondaryImageState.HOVERED);
  }

  @Override
  public void onHoverEnd ()
  {
    if (!isEnabled || !PlayMapSettings.ENABLE_HOVER_EFFECTS) return;

    changeSecondaryStateTo (CountrySecondaryImageState.NONE);
  }

  @Override
  public void onTouchDown ()
  {
    if (!isEnabled || !PlayMapSettings.ENABLE_CLICK_EFFECTS) return;
    if (currentPrimaryImageState == CountryPrimaryImageState.DISABLED) return;

    changeSecondaryStateTo (CountrySecondaryImageState.CLICKED);
  }

  @Override
  public void onTouchUp ()
  {
    if (!isEnabled || !PlayMapSettings.ENABLE_CLICK_EFFECTS) return;

    changeSecondaryStateTo (CountrySecondaryImageState.NONE);
  }

  @Override
  public Drawable getCurrentPrimaryDrawable ()
  {
    return currentPrimaryImage.getDrawable ();
  }

  @Override
  public Vector2 getReferenceDestination ()
  {
    return imageData.getReferenceDestination ();
  }

  @Override
  public Vector2 getReferenceTextUpperLeft ()
  {
    return imageData.getReferenceTextUpperLeft ();
  }

  @Override
  public float getReferenceWidth ()
  {
    return imageData.getReferenceWidth ();
  }

  @Override
  public float getReferenceHeight ()
  {
    return imageData.getReferenceHeight ();
  }

  @Override
  public void setArmies (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    armyTextActor.setArmies (armies);
  }

  @Override
  public void incrementArmies ()
  {
    armyTextActor.incrementArmies ();
  }

  @Override
  public void decrementArmies ()
  {
    armyTextActor.decrementArmies ();
  }

  @Override
  public void changeArmiesBy (final int deltaArmies)
  {
    armyTextActor.changeArmiesBy (deltaArmies);
  }

  @Override
  public void disable ()
  {
    isEnabled = false;
  }

  @Override
  public void enable ()
  {
    isEnabled = true;
  }

  @Override
  public int getAtlasIndex ()
  {
    return primaryImages.getAtlasIndex ();
  }

  @Override
  public CountryArmyTextActor getArmyTextActor ()
  {
    return armyTextActor;
  }

  @Override
  public Actor asActor ()
  {
    return group;
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
    return String.format (
                          "%1$s | Name: %2$s | Current Primary Image State: %3$s | Current Secondary Image State: %4$s"
                                  + " | Current Primary Image: %5$s  | Current Secondary Image: %6$s | Enabled: %7$s | "
                                  + "Image Data: %8$s | Army Text Actor: %9$s | Primary Images: %10$s "
                                  + "| Secondary Images: %11$s",
                          getClass ().getSimpleName (), group.getName (), currentPrimaryImageState,
                          currentSecondaryImageState, currentPrimaryImage, currentSecondaryImage, isEnabled, imageData,
                          armyTextActor, primaryImages, secondaryImages);
  }
}
