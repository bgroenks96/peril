package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public final class DefaultContinentOwnerModel implements ContinentOwnerModel
{
  private final ContinentMapGraphModel continentMapGraphModel;
  private final CountryOwnerModel countryPlayMap;

  public DefaultContinentOwnerModel (final ContinentMapGraphModel continentMapGraphModel,
                                     final CountryOwnerModel countryPlayMap)
  {
    Arguments.checkIsNotNull (continentMapGraphModel, "continentMapGraphModel");
    Arguments.checkIsNotNull (countryPlayMap, "countryPlayMap");

    this.continentMapGraphModel = continentMapGraphModel;
    this.countryPlayMap = countryPlayMap;
  }

  @Override
  public boolean isContinentOwned (final Id continentId)
  {
    Arguments.checkIsNotNull (continentId, "continentId");

    return findOwnerOf (continentId).isPresent ();
  }

  @Override
  public boolean isContinentOwned (final String continentName)
  {
    Arguments.checkIsNotNull (continentName, "continentName");

    if (!continentMapGraphModel.existsContinentWith (continentName))
    {
      Exceptions.throwIllegalArg ("No continent with name [{}] exists.", continentName);
    }

    final Continent continent = continentMapGraphModel.continentWith (continentName);
    return findOwnerOf (continent.getId ()).isPresent ();
  }

  @Override
  public boolean isContinentOwnedBy (final Id continentId, final Id ownerId)
  {
    Arguments.checkIsNotNull (continentId, "continentId");
    Arguments.checkIsNotNull (ownerId, "ownerId");

    final Optional <Id> owner = findOwnerOf (continentId);

    return owner.isPresent () && ownerId.is (owner.get ());
  }

  @Override
  public Optional <Id> ownerOf (final Id continentId)
  {
    Arguments.checkIsNotNull (continentId, "continentId");

    return findOwnerOf (continentId);
  }

  @Override
  public ImmutableSet <ContinentPacket> getContinentsOwnedBy (final Id ownerId)
  {
    Arguments.checkIsNotNull (ownerId, "ownerId");

    final ImmutableSet.Builder <ContinentPacket> ownedContinents = ImmutableSet.builder ();
    for (final Id continentId : continentMapGraphModel)
    {
      if (!isContinentOwnedBy (continentId, ownerId)) continue;
      ownedContinents.add (continentMapGraphModel.continentPacketWith (continentId));
    }
    return ownedContinents.build ();
  }

  private Optional <Id> findOwnerOf (final Id continentId)
  {
    assert continentId != null;

    final Continent continent = continentMapGraphModel.continentWith (continentId);
    assert continent != null;

    Id continentOwner = null;
    for (final Id id : continent.getCountryIds ())
    {
      if (!countryPlayMap.isCountryOwned (id)) return Optional.absent ();

      final Id countryOwner = countryPlayMap.ownerOf (id);
      if (continentOwner == null)
      {
        continentOwner = countryOwner;
        continue;
      }

      if (continentOwner.isNot (countryOwner)) return Optional.absent ();
    }

    return Optional.of (continentOwner);
  }
}
