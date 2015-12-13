package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public interface ContinentOwnerModel
{
  boolean isContinentOwned (final Id continentId);

  boolean isContinentOwned (final String continentName);

  boolean isContinentOwnedBy (final Id continentId, final Id ownerId);

  Optional <Id> ownerOf (final Id continentId);

  ImmutableSet <ContinentPacket> getContinentsOwnedBy (final Id ownerId);
}
