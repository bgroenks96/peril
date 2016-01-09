package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.common.net.packets.person.ObserverPacket;
import com.forerunnergames.peril.server.controllers.ClientPlayerMapping.RegisteredClientPlayerNotFoundException;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.Remote;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class ClientObserverMapping
{
  private final BiMap <Remote, ObserverPacket> clientsToObservers;

  public ClientObserverMapping ()
  {

    clientsToObservers = Maps.synchronizedBiMap (HashBiMap.<Remote, ObserverPacket> create ());
  }

  public Optional <ObserverPacket> put (final Remote client, final ObserverPacket observer)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (observer, "observer");

    return Optional.fromNullable (clientsToObservers.forcePut (client, observer));
  }

  /**
   * Looks up the ObserverPacket mapped to the given client. This method will query Core for updated observer data
   * before returning mapped ObserverPacket.
   *
   * @throws RegisteredClientPlayerNotFoundException
   *           if the observer no longer exists in the core observer model
   */
  public Optional <ObserverPacket> observerFor (final Remote client) throws RegisteredClientObserverNotFoundException
  {
    Arguments.checkIsNotNull (client, "client");

    if (!clientsToObservers.containsKey (client)) return Optional.absent ();

    final ObserverPacket oldObserverPacket = clientsToObservers.get (client);
    final Optional <ObserverPacket> newPlayerQuery = Optional.fromNullable (clientsToObservers.get (client));
    if (!newPlayerQuery.isPresent ())
    {
      throw new RegisteredClientObserverNotFoundException (oldObserverPacket.getName (), client);
    }
    return newPlayerQuery;
  }

  public boolean existsObserverWith (final String name)
  {
    return observerWith (name).isPresent ();
  }

  public Optional <ObserverPacket> observerWith (final String name)
  {
    for (final ObserverPacket observer : clientsToObservers.values ())
    {
      if (observer.hasName (name)) return Optional.of (observer);
    }
    return Optional.absent ();
  }

  public Optional <Remote> clientFor (final ObserverPacket observer)
  {
    Arguments.checkIsNotNull (observer, "observer");

    return Optional.fromNullable (clientsToObservers.inverse ().get (observer));
  }

  public ImmutableSet <ObserverPacket> observers ()
  {
    return ImmutableSet.copyOf (clientsToObservers.values ());
  }

  public ImmutableSet <ObserverPacket> observersExcept (final ObserverPacket observer)
  {
    return ImmutableSet.copyOf (Sets.difference (clientsToObservers.values (), ImmutableSet.of (observer)));
  }

  public ImmutableSet <Remote> clients ()
  {
    return ImmutableSet.copyOf (clientsToObservers.keySet ());
  }

  public Optional <ObserverPacket> remove (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    return Optional.fromNullable (clientsToObservers.remove (client));
  }

  @Override
  public String toString ()
  {
    return clientsToObservers.toString ();
  }

  /**
   * Thrown to indicate that the observer registered to a client no longer exists.
   */
  final class RegisteredClientObserverNotFoundException extends Exception
  {
    final String message;

    RegisteredClientObserverNotFoundException (final String observerName, final Remote client)
    {
      Arguments.checkIsNotNull (observerName, "observerName");
      Arguments.checkIsNotNull (client, "client");

      message = Strings.format ("Player [{}] not found for client [{}].", observerName, client);
    }
  }
}
