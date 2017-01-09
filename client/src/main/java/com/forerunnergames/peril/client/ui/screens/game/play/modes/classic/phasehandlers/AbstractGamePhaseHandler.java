/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.badlogic.gdx.Gdx;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.input.listeners.PlayMapInputListener;
import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.net.events.remote.RemoteEvent;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractGamePhaseHandler implements GamePhaseHandler
{
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  private final MBassador <Event> eventBus;
  private final PlayMapInputListener listener = new PlayMapInputListener ()
  {
    @Override
    public void onCountryLeftClicked (final String countryName, final float x, final float y)
    {
      Arguments.checkIsNotNull (countryName, "countryName");

      AbstractGamePhaseHandler.this.onCountryLeftClicked (countryName, x, y);
    }

    @Override
    public void onCountryRightClicked (final String countryName, final float x, final float y)
    {
      Arguments.checkIsNotNull (countryName, "countryName");

      AbstractGamePhaseHandler.this.onCountryRightClicked (countryName, x, y);
    }

    @Override
    public void onNonCountryLeftClicked (final float x, final float y)
    {
      AbstractGamePhaseHandler.this.onNonCountryLeftClicked (x, y);
    }

    @Override
    public void onNonCountryRightClicked (final float x, final float y)
    {
      AbstractGamePhaseHandler.this.onNonCountryRightClicked (x, y);
    }
  };

  private PlayMap playMap;
  @Nullable
  private PlayerPacket selfPlayer;

  AbstractGamePhaseHandler (final PlayMap playMap, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMap = playMap;
    this.eventBus = eventBus;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void activate ()
  {
    eventBus.subscribe (this);
    reset ();
    log.debug ("Activated [{}]", getClass ().getSimpleName ());
  }

  @Override
  public final void activate (final GamePhase currentPhase)
  {
    Arguments.checkIsNotNull (currentPhase, "currentPhase");

    if (shouldActivateAndDeactivate (currentPhase)) activate ();
  }

  @Override
  public final void activate (final PlayerPacket currentPlayer, final GamePhase currentPhase)
  {
    Arguments.checkIsNotNull (currentPlayer, "currentPlayer");
    Arguments.checkIsNotNull (currentPhase, "currentPhase");

    if (shouldActivateAndDeactivate (currentPlayer, currentPhase)) activate ();
  }

  @Override
  public void cancel ()
  {
    // Empty base implementation.
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void deactivate ()
  {
    eventBus.unsubscribe (this);
    reset ();
    log.debug ("Deactivated [{}]", getClass ().getSimpleName ());
  }

  @Override
  public final void deactivate (final GamePhase currentPhase)
  {
    Arguments.checkIsNotNull (currentPhase, "currentPhase");

    if (shouldActivateAndDeactivate (currentPhase)) deactivate ();
  }

  @Override
  public final void deactivate (final PlayerPacket currentPlayer, final GamePhase currentPhase)
  {
    Arguments.checkIsNotNull (currentPlayer, "currentPlayer");
    Arguments.checkIsNotNull (currentPhase, "currentPhase");

    if (shouldActivateAndDeactivate (currentPlayer, currentPhase)) deactivate ();
  }

  // Default implementation checks if current game phase is one of the relevant phases for this handler,
  // which makes sense for the majority of implementations.
  protected boolean shouldActivateAndDeactivate (final GamePhase currentPhase)
  {
    return getPhases ().contains (currentPhase);
  }

  // Default implementation checks if current player is the self player &
  // current game phase is one of the relevant phases for this handler,
  // which makes sense for the majority of implementations.
  protected boolean shouldActivateAndDeactivate (final PlayerPacket currentPlayer, final GamePhase currentPhase)
  {
    return isSelf (currentPlayer) && getPhases ().contains (currentPhase);
  }

  final void listenForPlayMapCountryClicks ()
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playMap.addListener (listener);
      }
    });
  }

  final boolean existsCountryWithName (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return playMap.existsCountryWithName (countryName);
  }

  final Country getCountryWithName (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return playMap.getCountryWithName (countryName);
  }

  final void changeCountryArmiesBy (@AllowNegative final int deltaArmyCount, final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    playMap.changeArmiesBy (deltaArmyCount, countryName);
  }

  final boolean countryArmyCountIs (final int armyCount, final String countryName)
  {
    Arguments.checkIsNotNegative (armyCount, "armyCount");
    Arguments.checkIsNotNull (countryName, "countryName");

    return playMap.countryArmyCountIs (armyCount, countryName);
  }

  final boolean playerOwnsCountry (final PlayerColor color, final String countryName)
  {
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNull (countryName, "countryName");

    return playMap.primaryImageStateOfCountryIs (CountryPrimaryImageState.fromPlayerColor (color), countryName);
  }

  final void setCountryOwner (final PlayerColor color, final String countryName)
  {
    Arguments.checkIsNotNull (color, "color");

    playMap.setCountryState (countryName, CountryPrimaryImageState.fromPlayerColor (color));
  }

  final void setCountryUnowned (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    playMap.setCountryState (countryName, CountryPrimaryImageState.UNOWNED);
  }

  final void publish (final RemoteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    eventBus.publish (event);
  }

  void onCountryLeftClicked (final String countryName, final float x, final float y)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    // Empty base implementation.
  }

  void onCountryRightClicked (final String countryName, final float x, final float y)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    // Empty base implementation.
  }

  void onNonCountryLeftClicked (final float x, final float y)
  {
    // Empty base implementation.
  }

  void onNonCountryRightClicked (final float x, final float y)
  {
    // Empty base implementation.
  }

  final boolean isSelf (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return selfPlayer != null && player.is (selfPlayer);
  }

  @Nullable
  final PlayerPacket getSelfPlayer ()
  {
    return selfPlayer;
  }

  @Override
  public final void setSelfPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    selfPlayer = player;
  }

  @Override
  public final void updatePlayerForSelf (final PlayerPacket player)
  {
    if (!isSelf (player)) return;

    selfPlayer = player;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void reset ()
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playMap.removeListener (listener);
      }
    });
  }

  @Override
  public final void shutDown ()
  {
    deactivate ();
    reset ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    final PlayMap oldPlayMap = this.playMap;

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        oldPlayMap.removeListener (listener);
      }
    });

    this.playMap = playMap;
  }

  final String getSelfPlayerName ()
  {
    return selfPlayer != null ? selfPlayer.getName () : "";
  }
}
