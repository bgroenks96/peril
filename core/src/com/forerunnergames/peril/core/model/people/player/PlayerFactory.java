package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Id;

import java.util.concurrent.atomic.AtomicInteger;

public final class PlayerFactory
{
  private static AtomicInteger globallyUniqueIdValue = new AtomicInteger (Integer.MIN_VALUE + 1);

  public static Player create (final String name)
  {
    return builder(name).build();
  }

  public static Player create (final String name,
                               final PersonIdentity identity,
                               final PlayerColor color,
                               final PlayerTurnOrder turnOrder)
  {
    return builder (name)
            .withIdentity (identity)
            .withColor (color)
            .withTurnOrder (turnOrder)
            .build();
  }

  public static PlayerBuilder builder (final String name)
  {
    return new PlayerBuilder (name);
  }

  private static Id uniqueId()
  {
    final Id id = new Id (globallyUniqueIdValue.getAndIncrement());

    if (id.hasValue (Integer.MIN_VALUE)) throw new IllegalStateException ("Ran out of unique id's!");

    return id;
  }

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
      this.id = uniqueId();
    }

    public PlayerBuilder withIdentity (final PersonIdentity identity)
    {
      Arguments.checkIsNotNull (identity, "identity");

      this.identity = identity;

      return this;
    }

    public PlayerBuilder withColor (final PlayerColor color)
    {
      Arguments.checkIsNotNull (color, "color");

      this.color = color;

      return this;
    }

    public PlayerBuilder withTurnOrder (final PlayerTurnOrder turnOrder)
    {
      Arguments.checkIsNotNull (turnOrder, "turnOrder");

      this.turnOrder = turnOrder;

      return this;
    }

    public Player build()
    {
      return new DefaultPlayer (name, id, identity, color, turnOrder);
    }
  }
}
