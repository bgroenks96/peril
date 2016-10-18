/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryImages;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountrySecondaryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountrySecondaryImageState;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultCountry implements Country
{
  private static final Logger log = LoggerFactory.getLogger (DefaultCountry.class);
  private final Group group = new Group ();
  private final CountryImages <CountryPrimaryImageState, CountryPrimaryImage> primaryImages;
  private final CountryImages <CountrySecondaryImageState, CountrySecondaryImage> secondaryImages;
  private final CountryImageData imageData;
  private final CountryArmyText armyText;
  private CountryPrimaryImageState primaryImageState = CountryPrimaryImageState.UNOWNED;
  private CountrySecondaryImageState secondaryImageState = CountrySecondaryImageState.NONE;
  private boolean isEnabled = true;
  private CountryImage <CountryPrimaryImageState> primaryImage;
  private CountryImage <CountrySecondaryImageState> secondaryImage;

  public DefaultCountry (final CountryImages <CountryPrimaryImageState, CountryPrimaryImage> primaryImages,
                         final CountryImages <CountrySecondaryImageState, CountrySecondaryImage> secondaryImages,
                         final CountryImageData imageData,
                         final CountryArmyText armyText,
                         final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (primaryImages, "primaryImages");
    Arguments.checkIsNotNull (secondaryImages, "secondaryImages");
    Arguments.checkIsNotNull (imageData, "imageData");
    Arguments.checkIsNotNull (armyText, "armyText");
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    this.primaryImages = primaryImages;
    this.secondaryImages = secondaryImages;
    this.imageData = imageData;
    this.armyText = armyText;

    final Vector2 referenceToActualPlayMapSpaceScaling = PlayMapSettings
            .referenceToActualPlayMapScaling (playMapReferenceSize);

    final Vector2 tempPosition = new Vector2 (imageData.getReferenceDestination ());
    tempPosition.y = playMapReferenceSize.y - tempPosition.y;
    tempPosition.scl (referenceToActualPlayMapSpaceScaling);

    group.setName (imageData.getCountryName ());
    group.setTransform (false);

    for (final CountryPrimaryImage primaryImage : primaryImages.getAll ())
    {
      primaryImage.setVisible (false);
      primaryImage.setPosition (tempPosition);
      primaryImage.setScale (referenceToActualPlayMapSpaceScaling);
      group.addActor (primaryImage.asActor ());
    }

    for (final CountrySecondaryImage countrySecondaryImage : secondaryImages.getAll ())
    {
      countrySecondaryImage.setVisible (false);
      countrySecondaryImage.setPosition (tempPosition);
      countrySecondaryImage.setScale (referenceToActualPlayMapSpaceScaling);
      group.addActor (countrySecondaryImage.asActor ());
    }

    changePrimaryStateTo (primaryImageState);
    changeSecondaryStateTo (secondaryImageState);
  }

  @Override
  public CountryPrimaryImageState getPrimaryImageState ()
  {
    return primaryImageState;
  }

  @Override
  public CountrySecondaryImageState getSecondaryImageState ()
  {
    return secondaryImageState;
  }

  @Override
  public void changePrimaryStateRandomly ()
  {
    CountryPrimaryImageState randomState;

    do
    {
      randomState = Randomness.getRandomElementFrom (CountryPrimaryImageState.values ());
    }
    while (randomState.is (primaryImageState));

    changePrimaryStateTo (randomState);
  }

  @Override
  public void changePrimaryStateTo (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    if (primaryImages.doesNotHave (state))
    {
      log.warn ("Cannot change {} [{}] to {} [{}] because that {} doesn't exist.", getClass ().getSimpleName (),
                imageData.getCountryName (), CountryPrimaryImageState.class.getSimpleName (), state,
                CountryPrimaryImageState.class.getSimpleName ());
      return;
    }

    hide (primaryImageState);
    primaryImageState = state;
    primaryImage = primaryImages.get (state);
    armyText.onPrimaryStateChange (state);
    show (state);
  }

  @Override
  public void changeSecondaryStateTo (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    if (secondaryImages.doesNotHave (state))
    {
      log.warn ("Cannot change {} [{}] to {} [{}] because that {} doesn't exist.", getClass ().getSimpleName (),
                imageData.getCountryName (), CountrySecondaryImageState.class.getSimpleName (), state,
                CountrySecondaryImageState.class.getSimpleName ());
      return;
    }

    hide (secondaryImageState);
    secondaryImageState = state;
    secondaryImage = secondaryImages.get (state);
    show (state);
  }

  @Override
  public void nextPrimaryState ()
  {
    changePrimaryStateTo (primaryImageState.hasNext () ? primaryImageState.next () : primaryImageState.first ());
  }

  @Override
  public void onHoverStart ()
  {
    if (!isEnabled || !PlayMapSettings.ENABLE_HOVER_EFFECTS) return;
    if (primaryImageState == CountryPrimaryImageState.DISABLED) return;

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
    if (primaryImageState == CountryPrimaryImageState.DISABLED) return;

    changeSecondaryStateTo (CountrySecondaryImageState.CLICKED);
  }

  @Override
  public void onTouchUp ()
  {
    if (!isEnabled || !PlayMapSettings.ENABLE_CLICK_EFFECTS) return;

    changeSecondaryStateTo (CountrySecondaryImageState.NONE);
  }

  @Nullable
  @Override
  public Drawable getPrimaryDrawable ()
  {
    return primaryImage.getDrawable ();
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
  public int getArmies ()
  {
    return armyText.getArmies ();
  }

  @Override
  public void setArmies (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    armyText.changeArmiesTo (armies);
  }

  @Override
  public boolean armyCountIs (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    return getArmies () == armies;
  }

  @Override
  public void incrementArmies ()
  {
    armyText.incrementArmies ();
  }

  @Override
  public void decrementArmies ()
  {
    armyText.decrementArmies ();
  }

  @Override
  public void changeArmiesBy (final int deltaArmies)
  {
    armyText.changeArmiesBy (deltaArmies);
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
  public CountryArmyText getArmyText ()
  {
    return armyText;
  }

  @Override
  public String getName ()
  {
    return group.getName () != null ? group.getName () : "";
  }

  @Override
  public void setName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    group.setName (name);
  }

  @Override
  public boolean hasName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return group.getName ().equals (name);
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
    return String.format ("%1$s | Name: %2$s | Primary Image State: %3$s | Secondary Image State: %4$s"
                                  + " | Primary Image: %5$s  | Secondary Image: %6$s | Enabled: %7$s | "
                                  + "Image Data: %8$s | Army Text: %9$s | Primary Images: %10$s "
                                  + "| Secondary Images: %11$s", getClass ().getSimpleName (), group.getName (),
                          primaryImageState, secondaryImageState, primaryImage, secondaryImage,
                          isEnabled, imageData, armyText, primaryImages, secondaryImages);
  }
}
