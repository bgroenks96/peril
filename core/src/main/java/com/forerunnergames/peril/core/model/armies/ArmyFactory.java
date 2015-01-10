package com.forerunnergames.peril.core.model.armies;

import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public final class ArmyFactory
{
  public static Army create ()
  {
    return new DefaultArmy (IdGenerator.generateUniqueId ());
  }

  public static ImmutableSet <Army> create (final int count)
  {
    final Builder <Army> armySetBuilder = ImmutableSet.builder ();

    for (int i = 0; i < count; i++)
    {
      armySetBuilder.add (new DefaultArmy (IdGenerator.generateUniqueId ()));
    }

    return armySetBuilder.build ();
  }

  private ArmyFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
