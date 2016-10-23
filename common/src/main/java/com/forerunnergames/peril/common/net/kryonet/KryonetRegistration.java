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

import com.forerunnergames.peril.common.game.rules.GameRulesFactory;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.common.net.LocalGameServerCreator;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.reflect.ClassPath;

import de.javakaffee.kryoserializers.UUIDSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSortedSetSerializer;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.UUID;

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings ("UnnecessaryFullyQualifiedName")
public final class KryonetRegistration
{
  private static final Logger log = LoggerFactory.getLogger (KryonetRegistration.class);

  // @formatter:off

  /**
   * Packages whose classes (and inner classes wherever possible) should be (recursively) auto-registered.
   */
  private static final ImmutableSet <String> INCLUDED_PACKAGES = ImmutableSet.of (
          "com.forerunnergames.peril.common.game",
          "com.forerunnergames.peril.common.map",
          "com.forerunnergames.peril.common.net",
          "com.forerunnergames.tools.net.client.configuration",
          "com.forerunnergames.tools.net.server.configuration");

  /**
   * Additional classes to be registered, that either exist outside the set of included packages,
   * or cannot be collected automatically, for whatever reason.
   *
   * Note: Only concrete (non-abstract, non-interface) classes need to be registered.
   *       Non-concrete classes will be filtered out.
   *       Inner classes will be auto-registered wherever possible.
   */
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic <Class <?>> type.
  // TODO Java 8: Remove @SuppressWarnings ("RedundantTypeArguments")
  @SuppressWarnings("RedundantTypeArguments")
  private static final ImmutableSet <Class <?>> INCLUDED_CLASSES = ImmutableSet.<Class <?>> of (
          // TODO Still can't get this one via reflection?! :'(
          com.forerunnergames.peril.common.net.packets.person.PlayerPacket.TURN_ORDER_COMPARATOR.getClass (),
          com.forerunnergames.tools.common.Classes.class,
          com.forerunnergames.tools.common.DefaultMessage.class,
          com.forerunnergames.tools.common.id.Id.class,
          java.util.concurrent.TimeUnit.class,
          java.util.ArrayList.class,
          java.util.HashMap.class,
          java.net.InetSocketAddress.class,
            // Notes on Guava's Optional:
            // Optional itself does not need to be registered because it is abstract.
            // Absent & Present are non-inner, package-private classes used by Optional,
            // and as such can only be included indirectly.
          com.google.common.base.Optional.absent ().getClass (), // Absent
          com.google.common.base.Optional.of ("").getClass ()); // Present

  /**
   * Packages whose classes should be excluded non-recursively from auto-registration.
   */
  private static final ImmutableSet <String> EXCLUDED_PACKAGES = ImmutableSet.of (
          "com.forerunnergames.peril.common.map.io",
          "com.forerunnergames.peril.common.net.kryonet");

  /**
   * Individual classes to be excluded from auto-registration,
   * that would otherwise be registered as part of one of the included package names.
   *
   * Note: Only need to exclude concrete (non-abstract, non-interface) classes.
   *       Non-concrete classes will be filtered out.
   */
  private static final ImmutableSet <Class <?>> EXCLUDED_CLASSES = ImmutableSet.of (
          GameRulesFactory.class,
          LocalGameServerCreator.class,
          PlayMapLoadingException.class);

  /**
   * The final Kryonet registration class set.
   */
  public static final ImmutableSet <Class <?>> CLASSES = FluentIterable
            .from (INCLUDED_PACKAGES)
            .transformAndConcat (new PackageToClassInfosFunction ())
            .filter (new RemoveExcludedClassInfosPredicate (EXCLUDED_PACKAGES))
            .transform (new ClassInfoToClassFunction ())
            .filter (Predicates.notNull ())
            .filter (new RemoveExcludedClassesPredicate (EXCLUDED_CLASSES))
            .append (INCLUDED_CLASSES)
            .transformAndConcat (new AddInnerClassesFunction ())
            .filter (new RemoveNonConcreteClassesPredicate ())
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

    log.trace ("Registered custom serializers for [{}, {}, {}, {}, {}, {}]", ImmutableList.class.getSimpleName (),
               ImmutableSet.class.getSimpleName (), ImmutableSortedSet.class.getSimpleName (),
               ImmutableMap.class.getSimpleName (), ImmutableMultimap.class.getSimpleName (),
               UUID.class.getSimpleName ());
  }

  private KryonetRegistration ()
  {
    Classes.instantiationNotAllowed ();
  }

  private static final class PackageToClassInfosFunction implements
          Function <String, ImmutableSet <ClassPath.ClassInfo>>
  {
    @Override
    public ImmutableSet <ClassPath.ClassInfo> apply (final String input)
    {
      try
      {
        return ClassPath.from (ClassLoader.getSystemClassLoader ()).getTopLevelClassesRecursive (input);
      }
      catch (final IOException e)
      {
        log.error ("Failed to read package [{}] for Kryonet registration.\n\nStack trace:\n\n", input, e);
        return ImmutableSet.of ();
      }
    }
  }

  private static final class RemoveExcludedClassInfosPredicate implements Predicate <ClassPath.ClassInfo>
  {
    private final ImmutableSet <String> excludedPackages;

    RemoveExcludedClassInfosPredicate (final ImmutableSet <String> excludedPackages)
    {
      this.excludedPackages = excludedPackages;
    }

    @Override
    public boolean apply (final ClassPath.ClassInfo input)
    {
      return !StringUtils.endsWithAny (input.getPackageName (),
                                       excludedPackages.toArray (new CharSequence [excludedPackages.size ()]));
    }
  }

  private static final class ClassInfoToClassFunction implements Function <ClassPath.ClassInfo, Class <?>>
  {
    @Override
    public Class <?> apply (final ClassPath.ClassInfo input)
    {
      try
      {
        return Class.forName (input.getName ());
      }
      catch (final ClassNotFoundException e)
      {
        log.error ("Failed to read class for Kryonet registration.", e);
        return null;
      }
    }
  }

  private static final class AddInnerClassesFunction implements Function <Class <?>, ImmutableSet <Class <?>>>
  {
    @Override
    public ImmutableSet <Class <?>> apply (final Class <?> input)
    {
      // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic <Class <?>> type.
      return ImmutableSet.<Class <?>> builder ().add (input).add (input.getDeclaredClasses ()).build ();
    }
  }

  private static final class RemoveExcludedClassesPredicate implements Predicate <Class <?>>
  {
    private final ImmutableSet <Class <?>> excludedClasses;

    RemoveExcludedClassesPredicate (final ImmutableSet <Class <?>> excludedClasses)
    {
      this.excludedClasses = excludedClasses;
    }

    @Override
    public boolean apply (final Class <?> input)
    {
      return !excludedClasses.contains (input);
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
