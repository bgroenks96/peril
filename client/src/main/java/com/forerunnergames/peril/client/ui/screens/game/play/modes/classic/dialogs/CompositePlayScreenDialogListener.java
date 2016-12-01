/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import java.util.HashMap;
import java.util.Map;

public final class CompositePlayScreenDialogListener implements PlayScreenDialogListener
{
  private ClassToInstanceMap <PlayScreenDialogListener> listenerClassesToListeners = MutableClassToInstanceMap
          .create ();

  public CompositePlayScreenDialogListener (final PlayScreenDialogListener... listeners)
  {
    Arguments.checkIsNotNull (listeners, "listeners");
    Arguments.checkHasNoNullElements (listeners, "listeners");

    for (final PlayScreenDialogListener listener : listeners)
    {
      listenerClassesToListeners.put (listener.getClass (), listener);
    }
  }

  @Override
  public void onSubmit ()
  {
    for (final PlayScreenDialogListener listener : getListeners ())
    {
      listener.onSubmit ();
    }
  }

  @Override
  public void onShow ()
  {
    for (final PlayScreenDialogListener listener : getListeners ())
    {
      listener.onShow ();
    }
  }

  @Override
  public void onHide ()
  {
    for (final PlayScreenDialogListener listener : getListeners ())
    {
      listener.onHide ();
    }
  }

  @Override
  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    for (final PlayScreenDialogListener listener : getListeners ())
    {
      listener.setPlayMap (playMap);
    }
  }

  public void add (final PlayScreenDialogListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    final Map <Class <? extends PlayScreenDialogListener>, PlayScreenDialogListener> copy = new HashMap<> (
            listenerClassesToListeners);
    copy.put (listener.getClass (), listener);
    listenerClassesToListeners = MutableClassToInstanceMap.create (copy);
  }

  public void add (final PlayScreenDialogListener... listeners)
  {
    Arguments.checkIsNotNull (listeners, "listeners");
    Arguments.checkHasNoNullElements (listeners, "listeners");

    final Map <Class <? extends PlayScreenDialogListener>, PlayScreenDialogListener> copy = new HashMap<> (
            listenerClassesToListeners);

    for (final PlayScreenDialogListener listener : listeners)
    {
      copy.put (listener.getClass (), listener);
    }

    listenerClassesToListeners = MutableClassToInstanceMap.create (copy);
  }

  public void remove (final PlayScreenDialogListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    final Map <Class <? extends PlayScreenDialogListener>, PlayScreenDialogListener> copy = new HashMap<> (
            listenerClassesToListeners);
    copy.remove (listener.getClass ());
    listenerClassesToListeners = MutableClassToInstanceMap.create (copy);
  }

  public void remove (final PlayScreenDialogListener... listeners)
  {
    Arguments.checkIsNotNull (listeners, "listeners");
    Arguments.checkHasNoNullElements (listeners, "listeners");

    final Map <Class <? extends PlayScreenDialogListener>, PlayScreenDialogListener> copy = new HashMap<> (
            listenerClassesToListeners);

    for (final PlayScreenDialogListener listener : listeners)
    {
      copy.remove (listener.getClass ());
    }

    listenerClassesToListeners = MutableClassToInstanceMap.create (copy);
  }

  @SafeVarargs
  public final void remove (final Class <? extends PlayScreenDialogListener>... listenerClasses)
  {
    Arguments.checkIsNotNull (listenerClasses, "listenerClasses");
    Arguments.checkHasNoNullElements (listenerClasses, "listenerClasses");

    final Map <Class <? extends PlayScreenDialogListener>, PlayScreenDialogListener> copy = new HashMap<> (
            listenerClassesToListeners);

    for (final Class <? extends PlayScreenDialogListener> listenerClass : listenerClasses)
    {
      copy.remove (listenerClass);
    }

    listenerClassesToListeners = MutableClassToInstanceMap.create (copy);
  }

  public <T extends PlayScreenDialogListener> T get (final Class <T> listenerClass)
  {
    Arguments.checkIsNotNull (listenerClass, "listenerClass");

    final T listener = listenerClassesToListeners.getInstance (listenerClass);

    if (listener == null) Exceptions.throwIllegalArg ("{} not found.", listenerClass);

    return listener;
  }

  public void dispose ()
  {
    listenerClassesToListeners.clear ();
  }

  private Iterable <PlayScreenDialogListener> getListeners ()
  {
    return listenerClassesToListeners.values ();
  }
}
