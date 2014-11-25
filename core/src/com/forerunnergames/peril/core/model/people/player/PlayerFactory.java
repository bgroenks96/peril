package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Id;
import com.forerunnergames.tools.common.Randomness;

import java.util.Arrays;
import java.util.List;

public final class PlayerFactory
{
  public static Player create (final String name, final Id id)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (id, "id");

    return new DefaultPlayer (name, id, PersonIdentity.UNKNOWN, PlayerColor.UNKNOWN, PlayerTurnOrder.UNKNOWN);
  }

  public static Player createRandom()
  {
    final String[] names = {"Ben", "Bob", "Jerry", "Oscar", "Evelyn", "Josh", "Eliza", "Aaron", "Maddy", "Brittany", "Jonathan", "Adam", "Brian"};
    final List <String> shuffledNames = Randomness.shuffle (Arrays.asList (names));
    final String randomName = shuffledNames.get (0);
    final int randomIdValue = Randomness.getRandomIntegerFrom (0, Integer.MAX_VALUE - 1);

    return create (randomName, new Id (randomIdValue));
  }
}
