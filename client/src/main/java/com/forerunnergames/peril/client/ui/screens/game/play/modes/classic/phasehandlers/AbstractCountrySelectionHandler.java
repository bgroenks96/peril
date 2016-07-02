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

import com.forerunnergames.peril.client.events.SelectCountryEvent;
import com.forerunnergames.peril.client.events.StatusMessageEventFactory;
import com.forerunnergames.peril.common.net.events.interfaces.PlayerSelectCountryVectorEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginAttackEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Framework for asynchronous selection of server-request-validated source & destination countries via
 * {@link SelectCountryEvent}.
 *
 * Concrete implementations are only *required* to implement {@link #onEnd(String, String)}, which is the callback for
 * what should happen after the player successfully selects a source & destination country.
 *
 * Note: This class must be subscribed on the {@link net.engio.mbassy.bus.MBassador} event bus before calling
 * {@link CountrySelectionHandler#start(PlayerSelectCountryVectorEvent)} in order to receive {@link SelectCountryEvent}
 * 's.
 *
 * Note: This class may be unsubscribed on the {@link net.engio.mbassy.bus.MBassador} event bus in order to stop
 * receiving {@link SelectCountryEvent}'s, but even if it remains subscribed, it will ignore any events received before
 * calling {@link CountrySelectionHandler#start(PlayerSelectCountryVectorEvent)} or after calling {@link #reset()}. If
 * unsubscribed, it must be resubscribed before calling
 * {@link CountrySelectionHandler#start(PlayerSelectCountryVectorEvent)} in order to receive events again.
 *
 * @see CountrySelectionHandler
 */
abstract class AbstractCountrySelectionHandler implements CountrySelectionHandler
{
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  private final String phaseAsVerb;
  private final MBassador <Event> eventBus;
  private boolean isStarted;
  private PlayerBeginAttackEvent requestEvent;
  @Nullable
  private String sourceCountryName;
  @Nullable
  private String destCountryName;

  AbstractCountrySelectionHandler (final String phaseAsVerb, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (phaseAsVerb, "phaseAsVerb");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.phaseAsVerb = phaseAsVerb;
    this.eventBus = eventBus;
  }

  /**
   * {@inheritDoc}
   *
   * Begins accepting {@link SelectCountryEvent}'s & asks the player in the {@link PlayerSelectCountryVectorEvent} to
   * choose a source country.
   */
  @Override
  @OverridingMethodsMustInvokeSuper
  public void start (final PlayerBeginAttackEvent requestEvent)
  {
    Arguments.checkIsNotNull (requestEvent, "requestEvent");
    Preconditions.checkIsFalse (isStarted, "Cannot start a new country selection. One is already in progress. "
            + "Call reset() first.");

    this.requestEvent = requestEvent;

    isStarted = true;

    log.debug ("Country selection has started.");

    eventBus.publish (StatusMessageEventFactory.create ("{}, choose a country to {} from.",
                                                        this.requestEvent.getPlayerName (), phaseAsVerb));
  }

  /**
   * {@inheritDoc}
   *
   * Stops accepting {@link SelectCountryEvent}'s. Called automatically after {@link #onEnd(String, String)}. Provided
   * in case the implementor needs to immediately stop any country selection which might be in progress.
   */
  @Override
  @OverridingMethodsMustInvokeSuper
  public void reset ()
  {
    isStarted = false;
    sourceCountryName = null;
    destCountryName = null;
  }

  @Override
  public void onSelectInvalidSourceCountry (final String countryName)
  {
    // Empty base implementation.
  }

  @Override
  public void onSelectInvalidDestCountry (final String sourceCountryName, final String destCountryName)
  {
    // Empty base implementation.
  }

  @Override
  public boolean isValidSourceCountry (final String countryName)
  {
    return requestEvent.isValidSourceCountry (countryName);
  }

  @Override
  public boolean isValidDestCountry (final String sourceCountryName, final String destCountryName)
  {
    return requestEvent.isValidVector (sourceCountryName, destCountryName);
  }

  @Handler
  final void onEvent (final SelectCountryEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String countryName = event.getCountryName ();

    if (!isStarted)
    {
      log.warn ("Ignoring unauthorized [{}].", event);
      return;
    }

    if (isSelectingSourceCountry () && checkIsValidSourceCountry (countryName).succeeded ())
    {
      sourceCountryName = countryName;
      log.info ("Selected valid source country [{}].", sourceCountryName);
      eventBus.publish (StatusMessageEventFactory.create ("{}, choose a country to {} to.",
                                                          requestEvent.getPlayerName (), phaseAsVerb));
      return;
    }

    if (isSelectingDestCountry () && checkIsValidDestCountry (countryName).succeeded ())
    {
      destCountryName = countryName;
      log.info ("Selected valid destination country [{}].", destCountryName);
      onEnd (sourceCountryName, destCountryName);
      log.debug ("Country selection has ended.");
      reset ();
    }
  }

  private boolean isSelectingSourceCountry ()
  {
    return isStarted && requestEvent != null && sourceCountryName == null;
  }

  private boolean isSelectingDestCountry ()
  {
    return isStarted && sourceCountryName != null && destCountryName == null;
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

  private Result <String> checkIsValidDestCountry (final String countryName)
  {
    if (!isValidDestCountry (sourceCountryName, countryName))
    {
      onSelectInvalidDestCountry (sourceCountryName, countryName);
      log.warn ("Rejecting invalid destination country selection [{}]. Validated source country selection: [{}].",
                countryName, sourceCountryName);
      return Result.failure ("");
    }

    return Result.success ();
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Phase (as verb): {} | Started: {} | Source Country: {} | Destination Country: {}"
                                   + " | Server Request: {}",
                           getClass ().getSimpleName (), phaseAsVerb, isStarted, sourceCountryName, destCountryName,
                           requestEvent);
  }
}
