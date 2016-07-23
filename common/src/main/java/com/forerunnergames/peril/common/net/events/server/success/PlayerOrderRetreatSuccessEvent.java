package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class PlayerOrderRetreatSuccessEvent extends AbstractPlayerEvent implements PlayerSuccessEvent
{
  private final PlayerPacket defendingPlayer;
  private final CountryPacket attackingCountry;
  private final CountryPacket defendingCountry;

  public PlayerOrderRetreatSuccessEvent (final PlayerPacket attackingPlayer,
                                         final PlayerPacket defendingPlayer,
                                         final CountryPacket attackingCountry,
                                         final CountryPacket defendingCountry)
  {
    super (attackingPlayer);

    Arguments.checkIsNotNull (defendingPlayer, "defendingPlayer");
    Arguments.checkIsNotNull (attackingCountry, "attackingCountry");
    Arguments.checkIsNotNull (defendingCountry, "defendingCountry");

    this.defendingPlayer = defendingPlayer;
    this.attackingCountry = attackingCountry;
    this.defendingCountry = defendingCountry;
  }

  public PlayerPacket getAttackingPlayer ()
  {
    return getPlayer ();
  }

  public String getAttackingPlayerName ()
  {
    return getPlayerName ();
  }

  public PlayerPacket getDefendingPlayer ()
  {
    return defendingPlayer;
  }

  public String getDefendingPlayerName ()
  {
    return defendingPlayer.getName ();
  }

  public CountryPacket getAttackingCountry ()
  {
    return attackingCountry;
  }

  public String getAttackingCountryName ()
  {
    return attackingCountry.getName ();
  }

  public int getAttackingCountryArmyCount ()
  {
    return attackingCountry.getArmyCount ();
  }

  public CountryPacket getDefendingCountry ()
  {
    return getDefendingCountry ();
  }

  public String getDefendingCountryName ()
  {
    return defendingCountry.getName ();
  }

  public int getDefendingCountryArmyCount ()
  {
    return defendingCountry.getArmyCount ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | DefendingPlayer: {} | AttackingCountry: {} | DefendingCountry: {}", super.toString (),
                           defendingPlayer, attackingCountry, defendingCountry);
  }

  @RequiredForNetworkSerialization
  private PlayerOrderRetreatSuccessEvent ()
  {
    defendingPlayer = null;
    attackingCountry = null;
    defendingCountry = null;
  }
}
