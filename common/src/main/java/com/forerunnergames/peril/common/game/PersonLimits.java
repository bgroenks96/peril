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

package com.forerunnergames.peril.common.game;

import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableMap;

public final class PersonLimits
{
  private final int playerLimit;
  private final int spectatorLimit;
  private ImmutableMap <PersonSentience, Integer> playerLimits;

  public static Builder builder ()
  {
    return new Builder ();
  }

  public int getPlayerLimit ()
  {
    return playerLimit;
  }

  public int getPlayerLimitFor (final PersonSentience sentience)
  {
    Arguments.checkIsNotNull (sentience, "sentience");

    final Integer playerLimit = playerLimits.get (sentience);

    if (playerLimit == null)
    {
      throw new IllegalStateException (Strings.format ("Cannot find any player limit for {}: [{}].",
                                                       PersonSentience.class.getSimpleName (), sentience));
    }

    return playerLimit;
  }

  public int getSpectatorLimit ()
  {
    return spectatorLimit;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: PlayerLimit: [{}] | SpectatorLimit: [{}] | PlayerLimits: [{}]", getClass ()
            .getSimpleName (), playerLimit, spectatorLimit, playerLimits);
  }

  private PersonLimits (final int humanPlayerLimit, final int aiPlayerLimit, final int spectatorLimit)
  {
    Arguments.checkIsNotNegative (humanPlayerLimit, "humanPlayerLimit");
    Arguments.checkIsNotNegative (aiPlayerLimit, "aiPlayerLimit");
    Arguments.checkIsNotNegative (spectatorLimit, "spectatorLimit");

    playerLimit = humanPlayerLimit + aiPlayerLimit;
    playerLimits = ImmutableMap.of (PersonSentience.HUMAN, humanPlayerLimit, PersonSentience.AI, aiPlayerLimit);
    this.spectatorLimit = spectatorLimit;
  }

  @RequiredForNetworkSerialization
  private PersonLimits ()
  {
    spectatorLimit = 0;
    playerLimit = 0;
  }

  public static class Builder
  {
    private int humanPlayerLimit = ClassicGameRules.DEFAULT_PLAYER_LIMIT;
    private int aiPlayerLimit = GameSettings.DEFAULT_AI_PLAYER_LIMIT;
    private int spectatorLimit = GameSettings.DEFAULT_SPECTATOR_LIMIT;

    public Builder humanPlayers (final int humanPlayerLimit)
    {
      Arguments.checkIsNotNegative (humanPlayerLimit, "humanPlayerLimit");

      this.humanPlayerLimit = humanPlayerLimit;

      return this;
    }

    public Builder aiPlayers (final int aiPlayerLimit)
    {
      Arguments.checkIsNotNegative (aiPlayerLimit, "aiPlayerLimit");

      this.aiPlayerLimit = aiPlayerLimit;

      return this;
    }

    public Builder spectators (final int spectatorLimit)
    {
      Arguments.checkIsNotNegative (spectatorLimit, "spectatorLimit");

      this.spectatorLimit = spectatorLimit;

      return this;
    }

    public PersonLimits build ()
    {
      return new PersonLimits (humanPlayerLimit, aiPlayerLimit, spectatorLimit);
    }

    @RequiredForNetworkSerialization
    private Builder ()
    {
    }
  }
}
