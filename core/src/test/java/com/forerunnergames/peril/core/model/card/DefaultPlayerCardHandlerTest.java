package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.core.model.rules.GameRules;

public class DefaultPlayerCardHandlerTest extends PlayerCardHandlerTest
{
  @Override
  protected PlayerCardHandler createPlayerCardHandler (final GameRules rules)
  {
    return new DefaultPlayerCardHandler (rules);
  }
}
