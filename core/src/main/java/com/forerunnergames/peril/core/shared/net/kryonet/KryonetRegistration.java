package com.forerunnergames.peril.core.shared.net.kryonet;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayer;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.rules.DefaultGameConfiguration;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.DefaultServerConfiguration;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public final class KryonetRegistration
{
  private static final Logger log = Logger.getGlobal ();
  private static final String CORE_NET_PACKAGE_NAME = "com.forerunnergames.peril.core.shared.net";

  // Set of all classes external to net package that must be registered
  private static final ImmutableSet <Class <?>> EXTERNAL = ImmutableSet
  // TODO Java 8: Generalized target-type inference: Remove unnecessary
  // explicit generic <Class <?>> type.
          .<Class <?>> of (
                           // @formatter:off
                           ArrayList.class,
                           Classes.class,
                           DefaultGameConfiguration.class,
                           DefaultMessage.class,
                           DefaultPlayer.class,
                           DefaultServerConfiguration.class,
                           HashMap.class,
                           Id.class,
                           ImmutableSet.class,
                           InetSocketAddress.class,
                           PersonIdentity.class,
                           PlayerColor.class,
                           PlayerTurnOrder.class);
  // @formatter:on

  /**
   * Kryonet registration class set
   */
  public static final ImmutableSet <Class <?>> CLASSES;

  /*
   * Initializer block automatically adds non-interface classes from all 'net' child packages.
   */
  static
  {
    final Builder <Class <?>> classSetBuilder = ImmutableSet.builder ();
    classSetBuilder.addAll (EXTERNAL);
    try
    {
      final ImmutableSet <ClassInfo> cinfo = ClassPath.from (ClassLoader.getSystemClassLoader ())
              .getTopLevelClassesRecursive (CORE_NET_PACKAGE_NAME);
      for (final ClassInfo ci : cinfo)
      {
        final Class <?> nextClass = Class.forName (ci.getName ());
        // add only if class is concrete
        if (!nextClass.isInterface () && !Modifier.isAbstract (nextClass.getModifiers ()))
        {
          classSetBuilder.add (nextClass);
        }
      }
    }
    catch (final IOException | ClassNotFoundException e)
    {
      log.warning ("failed to read classes for Kryonet registration: " + e.toString ());
    }
    CLASSES = classSetBuilder.build ();
  }

  private KryonetRegistration ()
  {
    Classes.instantiationNotAllowed ();
  }
}
