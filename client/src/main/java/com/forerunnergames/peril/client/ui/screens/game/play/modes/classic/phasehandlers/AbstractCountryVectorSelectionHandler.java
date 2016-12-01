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

import com.forerunnergames.peril.client.events.SelectCountryRequestEvent;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.input.listeners.PlayMapInputListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.input.listeners.PlayMapInputListenerAdapter;
import com.forerunnergames.peril.common.net.events.interfaces.PlayerSelectCountryVectorEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Framework for asynchronous selection of server-request-validated source & target countries via
 * {@link PlayMapInputListener}
 *
 * Concrete implementations are only *required* to implement {@link #onEnd(String, String)}, which is the callback for
 * what should happen after the player successfully selects a source & target country.
 *
 * @see CountryVectorSelectionHandler
 */
abstract class AbstractCountryVectorSelectionHandler implements CountryVectorSelectionHandler
{
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  private final MBassador <Event> eventBus;
  private PlayMap playMap;
  private boolean isStarted;
  private PlayerSelectCountryVectorEvent event;
  @Nullable
  private String sourceCountryName;
  @Nullable
  private String targetCountryName;
  private final PlayMapInputListener listener = new PlayMapInputListenerAdapter ()
  {
    @Override
    public void onCountryLeftClicked (final String countryName, final float x, final float y)
    {
      Arguments.checkIsNotNull (countryName, "countryName");

      handleCountryLeftClicked (countryName);
    }
  };

  AbstractCountryVectorSelectionHandler (final PlayMap playMap, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playMap, "playMap");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
    this.playMap = playMap;
  }

  @Override
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

  /**
   * {@inheritDoc}
   *
   * Begins listening to current {@link PlayMap} via {@link PlayMapInputListener} for country clicks & asks the player
   * in the {@link PlayerSelectCountryVectorEvent} to choose a source country.
   */
  @Override
  @OverridingMethodsMustInvokeSuper
  public void start (final PlayerSelectCountryVectorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Preconditions.checkIsFalse (isStarted, "Cannot start a new country selection. One is already in progress. "
            + "Call reset() first.");

    this.event = event;

    isStarted = true;

    log.debug ("Country selection has started.");

    eventBus.publish (createSourceCountrySelectionRequest ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playMap.addListener (listener);
      }
    });
  }

  /**
   * {@inheritDoc}
   *
   * Begins listening to current {@link PlayMap} via {@link PlayMapInputListener} for country clicks & asks the player
   * in the {@link PlayerSelectCountryVectorEvent} to choose a source country, re-using the
   * {@link PlayerSelectCountryVectorEvent} from the previous call to {@link #start(PlayerSelectCountryVectorEvent)}.
   */
  @Override
  public void restart ()
  {
    Preconditions.checkIsTrue (event != null, "Cannot start another country selection. You must call "
            + "#start (final PlayerSelectCountryVectorEvent event) first in order to set the event data.");

    start (event);
  }

  /**
   * {@inheritDoc}
   *
   * Stops listening to current {@link PlayMap} via {@link PlayMapInputListener} for country clicks. Called
   * automatically after {@link #onEnd(String, String)}. Provided in case the implementor needs to immediately stop any
   * country selection which might be in progress. Saves any previous event data passed in from
   * {@link #start(PlayerSelectCountryVectorEvent)}, so that {@link #restart()} may be called immediately after this
   * method.
   */
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

    isStarted = false;
    sourceCountryName = null;
    targetCountryName = null;
  }

  @Override
  public void onSelectInvalidSourceCountry (final String countryName)
  {
    // Empty base implementation.
  }

  @Override
  public void onSelectInvalidTargetCountry (final String sourceCountryName, final String targetCountryName)
  {
    // Empty base implementation.
  }

  @Override
  public boolean isValidSourceCountry (final String countryName)
  {
    return event.isValidSourceCountry (countryName);
  }

  @Override
  public boolean isValidTargetCountry (final String sourceCountryName, final String targetCountryName)
  {
    return event.isValidVector (sourceCountryName, targetCountryName);
  }

  abstract SelectCountryRequestEvent createSourceCountrySelectionRequest ();

  abstract SelectCountryRequestEvent createTargetCountrySelectionRequest (final String sourceCountryName);

  final void handleCountryLeftClicked (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!isStarted)
    {
      log.warn ("Ignoring unauthorized country left-click on [{}].", countryName);
      return;
    }

    final boolean isValidSourceCountry = checkIsValidSourceCountry (countryName).succeeded ();
    final boolean isSelectingTargetCountry = isSelectingTargetCountry ();

    if (isSelectingSourceCountry () && isValidSourceCountry)
    {
      sourceCountryName = countryName;
      log.info ("Selected valid source country [{}].", sourceCountryName);
      eventBus.publish (createTargetCountrySelectionRequest (sourceCountryName));
      return;
    }

    if (isSelectingTargetCountry && checkIsValidTargetCountry (countryName).succeeded ())
    {
      targetCountryName = countryName;
      log.info ("Selected valid target country [{}].", targetCountryName);
      onEnd (sourceCountryName, targetCountryName);
      reset ();
      log.debug ("Country selection has ended.");
      return;
    }

    if (isSelectingTargetCountry && isValidSourceCountry)
    {
      sourceCountryName = countryName;
      log.info ("Re-selected valid source country [{}].", sourceCountryName);
      eventBus.publish (createTargetCountrySelectionRequest (sourceCountryName));
    }
  }

  private boolean isSelectingSourceCountry ()
  {
    return isStarted && event != null && sourceCountryName == null;
  }

  private boolean isSelectingTargetCountry ()
  {
    return isStarted && event != null && sourceCountryName != null && targetCountryName == null;
  }

  private Result <String> checkIsValidSourceCountry (final String countryName)
  {
    if (!isValidSourceCountry (countryName))
    {
      onSelectInvalidSourceCountry (countryName);
      log.warn ("Rejecting invalid source country selection [{}].", countryName);
      return Result.failure ("");
    }

    return Result.success ();
  }

  private Result <String> checkIsValidTargetCountry (final String countryName)
  {
    if (!isValidTargetCountry (sourceCountryName, countryName))
    {
      onSelectInvalidTargetCountry (sourceCountryName, countryName);
      log.warn ("Rejecting invalid target country selection [{}]. Validated source country selection: [{}].",
                countryName, sourceCountryName);
      return Result.failure ("");
    }

    return Result.success ();
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Phase (as verb): {} | Started: {} | Source Country: {} | Target Country: {}"
                                   + " | Server Request: {}",
                           getClass ().getSimpleName (), isStarted, sourceCountryName, targetCountryName, event);
  }
}
