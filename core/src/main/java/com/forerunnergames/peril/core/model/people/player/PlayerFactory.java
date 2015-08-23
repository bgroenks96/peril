package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

public final class PlayerFactory
{
  public static PlayerBuilder builder (final String playerName)
  {
    return new PlayerBuilder (playerName);
  }

  public static Player create (final String name)
  {
    return builder (name).build ();
  }

  public static Player create (final String name,
                               final PersonIdentity identity,
                               final PlayerColor color,
                               final PlayerTurnOrder turnOrder)
  {
    return builder (name).identity (identity).color (color).turnOrder (turnOrder).build ();
  }

  /*
   * PlayerBuilder is public and static so that the type is visible both to the PlayerFactory.builder convenience method
   * and to external callers of the builder method.
   */
  public static final class PlayerBuilder
  {
    private final String name;
    private final Id id;
    private PersonIdentity identity = PersonIdentity.UNKNOWN;
    private PlayerColor color = PlayerColor.UNKNOWN;
    private PlayerTurnOrder turnOrder = PlayerTurnOrder.UNKNOWN;

    public PlayerBuilder (final String name)
    {
      Arguments.checkIsNotNull (name, "name");

      this.name = name;
      id = IdGenerator.generateUniqueId ();
    }

    public Player build ()
    {
      return new DefaultPlayer (name, id, identity, color, turnOrder);
    }

    public PlayerBuilder color (final PlayerColor color)
    {
      Arguments.checkIsNotNull (color, "color");

      this.color = color;

      return this;
    }

    public PlayerBuilder identity (final PersonIdentity identity)
    {
      Arguments.checkIsNotNull (identity, "identity");

      this.identity = identity;

      return this;
    }

    public PlayerBuilder turnOrder (final PlayerTurnOrder turnOrder)
    {
      Arguments.checkIsNotNull (turnOrder, "turnOrder");

      this.turnOrder = turnOrder;

      return this;
    }
  }
}
