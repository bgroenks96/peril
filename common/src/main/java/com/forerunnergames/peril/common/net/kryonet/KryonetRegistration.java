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

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.client.DefaultClientConfiguration;
import com.forerunnergames.tools.net.client.UnknownClientConfiguration;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.UnknownServerConfiguration;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.reflect.ClassPath;

import de.javakaffee.kryoserializers.UUIDSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

public final class KryonetRegistration
{
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (KryonetRegistration.class);
  private static final String COMMON_PACKAGE_NAME = "com.forerunnergames.peril.common";
  private static final ImmutableSet <String> INCLUDED_COMMON_PACKAGE_NAMES = ImmutableSet.of ("game", "map", "net");
  private static final ImmutableSet <String> EXCLUDED_COMMON_PACKAGE_NAMES = ImmutableSet.of ("game.rules", "map.io", "net.kryonet");

  /**
   * Additional classes to be registered, that exist outside the common package,
   * or cannot be collected automatically, for whatever reason.
   */
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic <Class <?>> type.
  // TODO Java 8: Remove @SuppressWarnings ("RedundantTypeArguments")
  @SuppressWarnings ("RedundantTypeArguments")
  private static final ImmutableSet <Class <?>> OTHER_CLASSES = ImmutableSet.<Class <?>> of (
          ArrayList.class,
          Classes.class,
          DefaultClientConfiguration.class,
          DefaultMessage.class,
          DefaultServerConfiguration.class,
          HashMap.class,
          Id.class,
          InetSocketAddress.class,
          Optional.class,
          Optional.absent ().getClass (),
          Optional.of ("").getClass (),
          PlayerPacket.TURN_ORDER_COMPARATOR.getClass (), // TODO Still can't get this one via reflection?! ;'(
          UnknownClientConfiguration.class,
          UnknownServerConfiguration.class);

  /**
   * Kryonet registration class set
   */
  public static final ImmutableSet <Class <?>> CLASSES = FluentIterable
          .from (INCLUDED_COMMON_PACKAGE_NAMES)
          .transformAndConcat (new PackageNamesToClassInfosFunction ())
          .filter (new RemoveExcludedClassInfosPredicate (EXCLUDED_COMMON_PACKAGE_NAMES))
          .transformAndConcat (new ClassInfosToClassesFunction ())
          .filter (new RemoveNonConcreteClassesPredicate ())
          .append (OTHER_CLASSES)
          .toSet ();

  // @formatter:on

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

    log.trace ("Registered custom serializers for [{}, {}, {}, {}, {}]", ImmutableList.class.getSimpleName (),
               ImmutableSet.class.getSimpleName (), ImmutableSortedSet.class.getSimpleName (),
               ImmutableMap.class.getSimpleName (), UUID.class.getSimpleName ());
  }

  private KryonetRegistration ()
  {
    Classes.instantiationNotAllowed ();
  }

  private static final class PackageNamesToClassInfosFunction implements
          Function <String, ImmutableSet <ClassPath.ClassInfo>>
  {
    @Override
    public ImmutableSet <ClassPath.ClassInfo> apply (final String input)
    {
      try
      {
        // @formatter:off
        return ClassPath.from (ClassLoader.getSystemClassLoader ()).getTopLevelClassesRecursive (COMMON_PACKAGE_NAME + "." + input);
        // @formatter:on
      }
      catch (final IOException e)
      {
        log.error ("Failed to read class for Kryonet registration.", e);
        return ImmutableSet.of ();
      }
    }
  }

  private static final class RemoveExcludedClassInfosPredicate implements Predicate <ClassPath.ClassInfo>
  {
    private final ImmutableSet <String> excludedCommonPackageNames;

    RemoveExcludedClassInfosPredicate (final ImmutableSet <String> excludedCommonPackageNames)
    {
      this.excludedCommonPackageNames = excludedCommonPackageNames;
    }

    @Override
    public boolean apply (final ClassPath.ClassInfo input)
    {
      return !StringUtils.endsWithAny (input.getPackageName (), excludedCommonPackageNames
              .toArray (new CharSequence [excludedCommonPackageNames.size ()]));
    }
  }

  private static final class ClassInfosToClassesFunction implements
          Function <ClassPath.ClassInfo, ImmutableSet <Class <?>>>
  {
    @Override
    public ImmutableSet <Class <?>> apply (final ClassPath.ClassInfo input)
    {
      try
      {
        final Class <?> nextClass = Class.forName (input.getName ());
        return ImmutableSet.<Class <?>> builder ().add (nextClass).add (nextClass.getDeclaredClasses ()).build ();
      }
      catch (final ClassNotFoundException e)
      {
        log.error ("Failed to read class for Kryonet registration.", e);
        return ImmutableSet.of ();
      }
    }
  }

  private static final class RemoveNonConcreteClassesPredicate implements Predicate <Class <?>>
  {
    @Override
    public boolean apply (final Class <?> input)
    {
      return !input.isInterface () && !Modifier.isAbstract (input.getModifiers ());
    }
  }
}
