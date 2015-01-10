package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.peril.core.model.people.person.AbstractPerson;
import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

public class DefaultPlayer extends AbstractPerson implements Player
{
  private PlayerColor color;
  private PlayerTurnOrder turnOrder;
  private int armiesInHand;

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
  }

  @RequiredForNetworkSerialization
  protected DefaultPlayer ()
  {
  }

  @Override
  public void addArmiesToHand (final int armies)
  {
    Arguments.checkIsNotNull (armies, "armies");
    Arguments.checkIsNotNegative (armies, "armies");
    Preconditions.checkIsTrue (canAddNArmiesToHand (armies), getCannotAddNArmiesToHandErrorMessage (armies));

    armiesInHand += armies;
  }

  @Override
  public boolean canAddArmiesToHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    return canAddNArmiesToHand (armies);
  }

  @Override
  public boolean canRemoveArmiesFromHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    return canRemoveNArmiesFromHand (armies);
  }

  @Override
  public boolean doesNotHave (final PlayerColor color)
  {
    return !has (color);
  }

  @Override
  public boolean doesNotHave (final PlayerTurnOrder turnOrder)
  {
    return !has (turnOrder);
  }

  @Override
  public int getArmiesInHand ()
  {
    return armiesInHand;
  }

  @Override
  public PlayerColor getColor ()
  {
    return color;
  }

  @Override
  public void setColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    this.color = color;
  }

  @Override
  public PlayerTurnOrder getTurnOrder ()
  {
    return turnOrder;
  }

  @Override
  public void setTurnOrder (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    this.turnOrder = turnOrder;
  }

  @Override
  public boolean has (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return this.color.equals (color);
  }

  @Override
  public boolean has (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return this.turnOrder.equals (turnOrder);
  }

  @Override
  public boolean hasArmiesInHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    return armiesInHand >= armies;
  }

  @Override
  public void removeArmiesFromHand (final int armies)
  {
    Arguments.checkIsNotNull (armies, "armies");
    Arguments.checkIsNotNegative (armies, "armies");
    Preconditions.checkIsTrue (canRemoveNArmiesFromHand (armies), getCannotRemoveNArmiesFromHandErrorMessage (armies));

    armiesInHand -= armies;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s | Color: %2$s | Turn order: %3$s | Armies in hand: %4$s", super.toString (), color,
                    turnOrder, getArmiesInHand ());
  }

  private boolean canAddNArmiesToHand (final int armies)
  {
    return GameSettings.MAX_ARMIES_IN_PLAYER_HAND - armies >= armiesInHand;
  }

  private boolean canRemoveNArmiesFromHand (final int armies)
  {
    return GameSettings.MIN_ARMIES_IN_PLAYER_HAND + armies <= armiesInHand;
  }

  private String getCannotAddNArmiesToHandErrorMessage (final int armies)
  {
    return "Can't add " + armies + " armies to hand: reached maximum value [" + GameSettings.MAX_ARMIES_IN_PLAYER_HAND
                    + "].";
  }

  private String getCannotRemoveNArmiesFromHandErrorMessage (final int armies)
  {
    return "Can't remove " + armies + " armies from hand: reached minimum value ["
                    + GameSettings.MIN_ARMIES_IN_PLAYER_HAND + "].";
  }
}
