package com.forerunnergames.peril.client.ui.screens.game.play.map.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;

import java.util.Map;

public final class CountrySprite
{
  private final Map <CountrySpriteState, Sprite> countrySpriteStatesToSprites;

  public CountrySprite (final Map <CountrySpriteState, Sprite> countrySpriteStatesToSprites)
  {
    Arguments.checkIsNotNull (countrySpriteStatesToSprites, "countrySpriteStatesToSprites");
    Arguments.checkHasNoNullKeysOrValues (countrySpriteStatesToSprites, "countrySpriteStatesToSprites");

    this.countrySpriteStatesToSprites = countrySpriteStatesToSprites;
  }

  public Sprite get (final CountrySpriteState state)
  {
    Arguments.checkIsNotNull (state, "state");
    Preconditions.checkIsTrue (countrySpriteStatesToSprites.containsKey (state),
                               "Cannot find country sprite with state [" + state + "].");

    return countrySpriteStatesToSprites.get (state);
  }
}
