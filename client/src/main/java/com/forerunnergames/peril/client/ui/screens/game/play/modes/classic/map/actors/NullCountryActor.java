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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImageState;
import com.forerunnergames.peril.client.ui.widgets.NullDrawable;
import com.forerunnergames.tools.common.Arguments;

final class NullCountryActor implements CountryActor
{
  private final Group group = new Group ();
  private final Vector2 vector2 = new Vector2 ();
  private final Drawable drawable = new NullDrawable ();
  private final CountryArmyTextActor countryArmyTextActor = CountryArmyTextActor.NULL_COUNTRY_ARMY_TEXT_ACTOR;

  @Override
  public CountryPrimaryImageState getCurrentPrimaryImageState ()
  {
    return CountryPrimaryImageState.DISABLED;
  }

  @Override
  public CountrySecondaryImageState getCurrentSecondaryImageState ()
  {
    return CountrySecondaryImageState.NONE;
  }

  @Override
  public void changePrimaryStateRandomly ()
  {
  }

  @Override
  public void changePrimaryStateTo (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public void changeSecondaryStateTo (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public void nextPrimaryState ()
  {
  }

  @Override
  public void onHoverStart ()
  {
  }

  @Override
  public void onHoverEnd ()
  {
  }

  @Override
  public void onTouchDown ()
  {
  }

  @Override
  public void onTouchUp ()
  {
  }

  @Override
  public Drawable getCurrentPrimaryDrawable ()
  {
    return drawable;
  }

  @Override
  public Vector2 getReferenceDestination ()
  {
    return vector2;
  }

  @Override
  public Vector2 getReferenceTextUpperLeft ()
  {
    return vector2;
  }

  @Override
  public float getReferenceWidth ()
  {
    return 1.0f;
  }

  @Override
  public float getReferenceHeight ()
  {
    return 1.0f;
  }

  @Override
  public int getArmies ()
  {
    return 0;
  }

  @Override
  public void setArmies (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");
  }

  @Override
  public void incrementArmies ()
  {
  }

  @Override
  public void decrementArmies ()
  {
  }

  @Override
  public void changeArmiesBy (final int deltaArmies)
  {
  }

  @Override
  public void disable ()
  {
  }

  @Override
  public void enable ()
  {
  }

  @Override
  public int getAtlasIndex ()
  {
    return 0;
  }

  @Override
  public CountryArmyTextActor getArmyTextActor ()
  {
    return countryArmyTextActor;
  }

  @Override
  public Actor asActor ()
  {
    return group;
  }
}
