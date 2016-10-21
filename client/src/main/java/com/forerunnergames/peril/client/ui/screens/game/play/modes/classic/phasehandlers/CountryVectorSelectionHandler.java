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

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.input.listeners.PlayMapInputListener;
import com.forerunnergames.peril.common.net.events.interfaces.PlayerSelectCountryVectorEvent;

/**
 * API for selection of a country vector - a validated source country followed by selection of a validated target
 * country.
 *
 * The implementor decides the strategy for valid country selection with {@link #isValidSourceCountry(String)} &
 * {@link #isValidTargetCountry(String, String)}.
 */
public interface CountryVectorSelectionHandler
{
  /**
   * Sets the {@link PlayMap} that will be used to listen for country selection via {@link PlayMapInputListener}
   */
  void setPlayMap (final PlayMap playMap);

  /**
   * Start requesting a country selection. Should not be called more than once in a row - {@link #reset()} should be
   * called in between each invocation.
   *
   * @param event
   *          The original server event authorizing this country selection, used to validate the source & target.
   *
   * @see #restart()
   */
  void start (final PlayerSelectCountryVectorEvent event);

  /**
   * Start requesting another country selection with the event data from the last invocation of
   * {@link #start(PlayerSelectCountryVectorEvent)}, which must have been previously called. Should not be called more
   * than once in a row - {@link #reset()} should be called in between each invocation.
   *
   * @see #start(PlayerSelectCountryVectorEvent)
   */
  void restart ();

  /**
   * Should be called after country selection is finished, i.e., after {@link #onEnd(String, String)}.
   */
  void reset ();

  /**
   * Callback for when country selection has ended, i.e., when a valid source country & a valid target country have been
   * selected according to {@link #isValidSourceCountry(String)} & {@link #isValidTargetCountry(String, String)}.
   *
   * {@link #reset()} should be called after this callback, so that {@link #start(PlayerSelectCountryVectorEvent)} must
   * be called again in order to initiate another country selection, and so that otherwise country selection will remain
   * disabled.
   */
  void onEnd (final String sourceCountryName, final String targetCountryName);

  /**
   * Callback for when the source country is invalid according to {@link #isValidSourceCountry(String)}.
   */
  void onSelectInvalidSourceCountry (final String countryName);

  /**
   * Callback for when the target country is invalid according to {@link #isValidTargetCountry(String, String)}. The
   * source country has already been validated.
   */
  void onSelectInvalidTargetCountry (final String sourceCountryName, final String targetCountryName);

  /**
   * Whether the selected country name is a valid source country.
   *
   * The implementor decides the strategy for valid country selection with this method &
   * {@link #isValidTargetCountry(String, String)}.
   */
  boolean isValidSourceCountry (final String countryName);

  /**
   * Whether the selected country name is a valid target country. The source country has already been validated, and is
   * provided in case the implementation for determining whether the target country is valid depends on the source
   * country.
   *
   * The implementor decides the strategy for valid country selection with {@link #isValidSourceCountry(String)} & this
   * method.
   */
  boolean isValidTargetCountry (final String sourceCountryName, final String targetCountryName);
}
