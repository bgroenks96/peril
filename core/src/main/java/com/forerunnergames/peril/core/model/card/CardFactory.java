package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.id.IdGenerator;

public final class CardFactory
{
  public static Card create (final String name, final CardType type)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (type, "type");

    return new DefaultCard (name, IdGenerator.generateUniqueId (), type);
  }

  private CardFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
