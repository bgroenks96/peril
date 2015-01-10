package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.core.model.armies.Army;
import com.forerunnergames.peril.core.model.people.person.AbstractPerson;
import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPlayer extends AbstractPerson implements Player
{
  private static final Logger log = LoggerFactory.getLogger (DefaultPlayer.class);

  private PlayerColor color;
  private PlayerTurnOrder turnOrder;
  private Set <Army> armiesInHand;

  public DefaultPlayer (final String name,
                        final Id id,
                        final PersonIdentity identity,
                        final PlayerColor color,
                        final PlayerTurnOrder turnOrder)
  {
    super (name, id, identity);

    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    this.color = color;
    this.turnOrder = turnOrder;
    this.armiesInHand = new HashSet <Army> ();
  }

  @Override
  public PlayerColor getColor ()
  {
    return color;
  }

  @Override
  public PlayerTurnOrder getTurnOrder ()
  {
    return turnOrder;
  }

  @Override
  public boolean has (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return this.color.equals (color);
  }

  @Override
  public boolean doesNotHave (final PlayerColor color)
  {
    return ! has (color);
  }

  @Override
  public boolean has (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return this.turnOrder.equals (turnOrder);
  }

  @Override
  public boolean doesNotHave (final PlayerTurnOrder turnOrder)
  {
    return ! has (turnOrder);
  }

  @Override
  public void setColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    this.color = color;
  }

  @Override
  public void setTurnOrder (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    this.turnOrder = turnOrder;
  }

  @Override
  public void addArmiesToHand (final ImmutableSet <Army> armies)
  {
    Arguments.checkIsNotNull (armies, "armies");
    Arguments.checkHasNoNullElements (armies, "armies");

    armiesInHand.addAll (armies);
  }

  @Override
  public void addArmyToHand (final Army army)
  {
    Arguments.checkIsNotNull (army, "army");

    armiesInHand.add (army);
  }

  @Override
  public void removeArmyFromHand (final Army army)
  {
    Arguments.checkIsNotNull (army, "army");

    if (! armiesInHand.remove (army))
    {
      log.warn ("Attempt to remove Army [id={}] from hand of player [{}] failed because it is not present.",
                army.getId (), getName ());
    }
  }

  @Override
  public void removeArmiesFromHand (final ImmutableSet <Army> armies)
  {
    Arguments.checkIsNotNull (armies, "armies");
    Arguments.checkHasNoNullElements (armies, "armies");

    if (! armiesInHand.removeAll (armies))
    {
      log.warn ("Attempt to remove {} Armies from hand of player [{}] failed because they are not present.",
                armies.size (), getName ());
    }
  }

  @Override
  public int getArmiesInHandCount ()
  {
    return armiesInHand.size ();
  }

  @Override
  public boolean hasArmiesInHandCount (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    return armiesInHand.size () == count;
  }

  @Override
  public ImmutableSet <Army> getArmiesInHand ()
  {
    return ImmutableSet.copyOf (armiesInHand);
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s | Color: %2$s | Turn order: %3$s | Armies in hand count: %4$s", super.toString (),
                          color, turnOrder, getArmiesInHandCount ());
  }

  @RequiredForNetworkSerialization
  protected DefaultPlayer ()
  {
  }
}
