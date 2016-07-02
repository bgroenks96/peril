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

import com.forerunnergames.peril.common.net.events.interfaces.PlayerSelectCountryVectorEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginAttackEvent;

/**
 * API for selection of a validated source country followed by selection of a validated destination country.
 *
 * The implementor decides the strategy for valid country selection with {@link #isValidSourceCountry(String)} &
 * {@link #isValidDestCountry(String, String)}.
 */
public interface CountrySelectionHandler
{
  /**
   * Start requesting a country selection. Should not be called more than once in a row - {@link #reset()} should be
   * called in between each invocation.
   *
   * @param requestEvent
   *          The original server request authorizing this country selection, used to validate the source & destination.
   */
  void start (final PlayerBeginAttackEvent requestEvent);

  /**
   * Should be called after country selection is finished, i.e., after {@link #onEnd(String, String)}.
   */
  void reset ();

  /**
   * Callback for when country selection has ended, i.e., when a valid source country & a valid destination country have
   * been selected according to {@link #isValidSourceCountry(String)} & {@link #isValidDestCountry(String, String)}.
   *
   * {@link #reset()} should be called after this callback, so that {@link #start(PlayerSelectCountryVectorEvent)} must
   * be called again in order to initiate another country selection, and so that otherwise country selection will remain
   * disabled.
   */
  void onEnd (final String sourceCountryName, final String destCountryName);

  /**
   * Callback for when the source country is invalid according to {@link #isValidSourceCountry(String)}.
   */
  void onSelectInvalidSourceCountry (final String countryName);

  /**
   * Callback for when the destination country is invalid according to {@link #isValidDestCountry(String, String)}. The
   * source country has already been validated.
   */
  void onSelectInvalidDestCountry (final String sourceCountryName, final String destCountryName);

  /**
   * Whether the selected country name is a valid source country.
   *
   * The implementor decides the strategy for valid country selection with this method &
   * {@link #isValidDestCountry(String, String)}.
   */
  boolean isValidSourceCountry (final String countryName);

  /**
   * Whether the selected country name is a valid destination country. The source country has already been validated,
   * and is provided in case the implementation for determining whether the destination country is valid depends on the
   * source country.
   *
   * The implementor decides the strategy for valid country selection with {@link #isValidSourceCountry(String)} & this
   * method.
   */
  boolean isValidDestCountry (final String sourceCountryName, final String destCountryName);
}
