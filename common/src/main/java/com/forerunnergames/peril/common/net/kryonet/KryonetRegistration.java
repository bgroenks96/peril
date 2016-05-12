/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.common.net.kryonet;

import com.esotericsoftware.kryo.Kryo;

import com.forerunnergames.peril.common.game.DefaultGameConfiguration;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.map.DefaultMapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerDefendCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerFortifyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.SpectatorJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.client.DefaultClientConfiguration;
import com.forerunnergames.tools.net.client.UnknownClientConfiguration;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.UnknownServerConfiguration;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import de.javakaffee.kryoserializers.UUIDSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KryonetRegistration
{
  /**
   * Kryonet registration class set
   */
  public static final ImmutableSet <Class <?>> CLASSES;
  private static final Logger log = LoggerFactory.getLogger (KryonetRegistration.class);
  private static final String COMMON_NET_PACKAGE_NAME = "com.forerunnergames.peril.common.net";
  // Set of all classes that are either:
  // 1) external to net package that must be registered, or
  // 2) are internal to the net package, but must be manually registered.
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic <Class <?>> type.
  // TODO Java 8: Remove @SuppressWarnings ("RedundantTypeArguments")
  // @formatter:off
  @SuppressWarnings ("RedundantTypeArguments")
  private static final ImmutableSet <Class <?>> OTHER = ImmutableSet.<Class <?>> of (
          AbstractCountryStateChangeDeniedEvent.Reason.class,
          ArrayList.class,
          Classes.class,
          DefaultClientConfiguration.class,
          DefaultGameConfiguration.class,
          DefaultMapMetadata.class,
          DefaultMessage.class,
          DefaultServerConfiguration.class,
          DieFaceValue.class,
          DieOutcome.class,
          DieRoll.class,
          GameMode.class,
          GameServerType.class,
          HashMap.class,
          Id.class,
          InetSocketAddress.class,
          InitialCountryAssignment.class,
          MapType.class,
          Optional.class,
          Optional.absent ().getClass (),
          Optional.of ("").getClass (),
          PlayerAttackCountryResponseDeniedEvent.Reason.class,
          PlayerDefendCountryResponseDeniedEvent.Reason.class,
          PlayerFortifyCountryResponseDeniedEvent.Reason.class,
          PlayerJoinGameDeniedEvent.Reason.class,
          PlayerTradeInCardsResponseDeniedEvent.class,
          PlayerPacket.TURN_ORDER_COMPARATOR.getClass (),
          SpectatorJoinGameDeniedEvent.class,
          UnknownClientConfiguration.class,
          UnknownServerConfiguration.class);
  // @formatter:on

  /*
   * Initializer block automatically adds non-interface classes from all 'net' child packages.
   *
   * Exclusions: 'kryonet' subpackage, any 'factories' subpackages
   */
  static
  {
    final Builder <Class <?>> classSetBuilder = ImmutableSet.builder ();
    classSetBuilder.addAll (OTHER);

    try
    {
      final ImmutableSet <ClassInfo> classInfos = ClassPath.from (ClassLoader.getSystemClassLoader ())
              .getTopLevelClassesRecursive (COMMON_NET_PACKAGE_NAME);

      for (final ClassInfo classInfo : classInfos)
      {
        // Don't serialize the kryonet subpackage
        if (classInfo.getPackageName ().endsWith ("kryonet")) continue;

        // Don't serialize any event factory subpackages
        if (classInfo.getPackageName ().endsWith ("factories")) continue;

        final Class <?> nextClass = Class.forName (classInfo.getName ());

        // Add only if class is not an interface.
        if (!nextClass.isInterface ()) classSetBuilder.add (nextClass);
      }
    }
    catch (final IOException | ClassNotFoundException e)
    {
      log.error ("Failed to read classes for Kryonet registration.", e);
    }

    CLASSES = classSetBuilder.build ();
  }

  @SuppressWarnings ({ "rawtypes", "unchecked" })
  public static void initialize (final Kryo kryo)
  {
    Arguments.checkIsNotNull (kryo, "kryo");

    // Workaround for https://github.com/EsotericSoftware/kryo/issues/216
    kryo.setInstantiatorStrategy (new InstantiatorStrategy ()
    {
      @Override
      public ObjectInstantiator newInstantiatorOf (final Class type)
      {
        try
        {
          type.getConstructor ();
          return new Kryo.DefaultInstantiatorStrategy ().newInstantiatorOf (type);
        }
        catch (final NoSuchMethodException | SecurityException ignored)
        {
          return new StdInstantiatorStrategy ().newInstantiatorOf (type);
        }
      }
    });

    kryo.setRegistrationRequired (true);
  }

  public static void registerCustomSerializers (final Kryo kryo)
  {
    Arguments.checkIsNotNull (kryo, "kryo");

    ImmutableListSerializer.registerSerializers (kryo);
    ImmutableSetSerializer.registerSerializers (kryo);
    ImmutableSortedSetSerializer.registerSerializers (kryo);
    ImmutableMapSerializer.registerSerializers (kryo);
    ImmutableMultimapSerializer.registerSerializers (kryo);

    kryo.register (UUID.class, new UUIDSerializer ());

    log.trace ("Registered custom serializers for [{}, {}, {}, {}]", ImmutableList.class.getSimpleName (),
               ImmutableSet.class.getSimpleName (), ImmutableSortedSet.class.getSimpleName (),
               ImmutableMap.class.getSimpleName (), UUID.class.getSimpleName ());
  }

  private KryonetRegistration ()
  {
    Classes.instantiationNotAllowed ();
  }
}
