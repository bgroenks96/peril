package com.forerunnergames.peril.client.ui.screens.game.play.map.data;

import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Randomness;

import com.google.common.collect.ImmutableBiMap;

public final class CountrySpriteColorOrder
{
  private final ImmutableBiMap <PlayerColor, Integer> playerColorsToCountrySpriteIndices;

  public CountrySpriteColorOrder (final ImmutableBiMap <PlayerColor, Integer> playerColorsToCountrySpriteIndices)
  {
    Arguments.checkIsNotNull (playerColorsToCountrySpriteIndices, "playerColorsToCountrySpriteIndices");
    Arguments.checkHasNoNullKeysOrValues (playerColorsToCountrySpriteIndices, "playerColorsToCountrySpriteIndices");

    this.playerColorsToCountrySpriteIndices = playerColorsToCountrySpriteIndices;
  }

  public int getSpriteIndexOf (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
    Preconditions.checkIsTrue (playerColorsToCountrySpriteIndices.containsKey (color),
                               "Cannot find sprite index with color [" + color + "].");

    return playerColorsToCountrySpriteIndices.get (color);
  }

  public int getRandomSpriteIndex ()
  {
    return Randomness.getRandomElementFrom (playerColorsToCountrySpriteIndices.values ());
  }

  public PlayerColor getColorOf (final int spriteIndex)
  {
    final PlayerColor color = playerColorsToCountrySpriteIndices.inverse ().get (spriteIndex);

    return color != null ? color : PlayerColor.UNKNOWN;
  }

  public int count ()
  {
    return playerColorsToCountrySpriteIndices.size ();
  }
}
