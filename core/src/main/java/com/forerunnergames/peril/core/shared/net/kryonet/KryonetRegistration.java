package com.forerunnergames.peril.core.shared.net.kryonet;

import com.esotericsoftware.kryo.Kryo;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayer;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.rules.DefaultGameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;
import com.forerunnergames.peril.core.shared.net.GameServerType;
import com.forerunnergames.peril.core.shared.net.events.server.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.client.DefaultClientConfiguration;
import com.forerunnergames.tools.net.server.DefaultServerConfiguration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;

public final class KryonetRegistration
{
  /**
   * Kryonet registration class set
   */
  public static final ImmutableSet <Class <?>> CLASSES;
  private static final Logger log = LoggerFactory.getLogger (KryonetRegistration.class);
  private static final String CORE_NET_PACKAGE_NAME = "com.forerunnergames.peril.core.shared.net";
  // @formatter:on
  // Set of all classes external to net package that must be registered
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic <Class <?>> type.
  private static final ImmutableSet <Class <?>> EXTERNAL = ImmutableSet.<Class <?>> of (
  // @formatter:off
          ArrayList.class,
          ChangePlayerColorDeniedEvent.Reason.class,
          Classes.class,
          DefaultClientConfiguration.class,
          DefaultGameConfiguration.class,
          DefaultMessage.class,
          DefaultPlayer.class,
          DefaultServerConfiguration.class,
          GameMode.class,
          GameServerType.class,
          HashMap.class,
          Id.class,
          InetSocketAddress.class,
          InitialCountryAssignment.class,
          PersonIdentity.class,
          PlayerColor.class,
          PlayerJoinGameDeniedEvent.Reason.class,
          PlayerSelectCountryResponseDeniedEvent.Reason.class,
          PlayerTurnOrder.class);
  // @formatter:on

  /*
   * Initializer block automatically adds non-interface classes from all 'net' child packages.
   *
   * Exclusions: 'kryonet' subpackage, any 'factories' subpackages
   */
  static
  {
    final Builder <Class <?>> classSetBuilder = ImmutableSet.builder ();
    classSetBuilder.addAll (EXTERNAL);

    try
    {
      final ImmutableSet <ClassInfo> classInfos = ClassPath.from (ClassLoader.getSystemClassLoader ())
              .getTopLevelClassesRecursive (CORE_NET_PACKAGE_NAME);

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
    ImmutableMapSerializer.registerSerializers (kryo);

    log.debug ("Registered custom serializers for [{}, {}, {}]", ImmutableList.class.getSimpleName (),
               ImmutableSet.class.getSimpleName (), ImmutableMap.class.getSimpleName ());
  }

  private KryonetRegistration ()
  {
    Classes.instantiationNotAllowed ();
  }
}
