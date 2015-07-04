package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;

public final class DefaultCard extends AbstractAsset implements Card
{
  private final CardType type;

  public DefaultCard (final String name, final Id id, final CardType type)
  {
    super (name, id);

    Arguments.checkIsNotNull (type, "type");

    this.type = type;
  }

  @Override
  public CardType getType ()
  {
    return type;
  }

  @Override
  public boolean typeIs (final CardType level)
  {
    Arguments.checkIsNotNull (level, "level");

    return this.type == level;
  }
}
