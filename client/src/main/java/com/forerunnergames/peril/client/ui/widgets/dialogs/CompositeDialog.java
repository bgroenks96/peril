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

package com.forerunnergames.peril.client.ui.widgets.dialogs;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import com.forerunnergames.peril.client.input.KeyRepeatListenerAdapter;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Message;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public final class CompositeDialog extends KeyRepeatListenerAdapter implements Dialog
{
  private ClassToInstanceMap <Dialog> dialogClassesToDialogs = MutableClassToInstanceMap.create ();

  @Override
  public void show ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.show ();
    }
  }

  @Override
  public void show (@Nullable final Action action)
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.show (action);
    }
  }

  @Override
  public void show (final String message)
  {
    Arguments.checkIsNotNull (message, "message");

    for (final Dialog dialog : getDialogs ())
    {
      dialog.show (message);
    }
  }

  @Override
  public void show (final String message, @Nullable final Action action)
  {
    Arguments.checkIsNotNull (message, "message");

    for (final Dialog dialog : getDialogs ())
    {
      dialog.show (message, action);
    }
  }

  @Override
  public void hide ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.hide ();
    }
  }

  @Override
  public void hide (@Nullable final Action action)
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.hide (action);
    }
  }

  @Override
  public void setTitle (final String title)
  {
    Arguments.checkIsNotNull (title, "title");

    for (final Dialog dialog : getDialogs ())
    {
      dialog.setTitle (title);
    }
  }

  @Override
  public void setMessage (final Message message)
  {
    Arguments.checkIsNotNull (message, "message");

    for (final Dialog dialog : getDialogs ())
    {
      dialog.setMessage (message);
    }
  }

  /**
   * @return true if all dialogs are shown, false otherwise. Same as {@link #allAreShown()}.
   *
   * @see #allAreShown()
   * @see #anyIsShown()
   * @see #noneAreShown()
   * @see #noneAreShownExcept(Dialog)
   */
  @Override
  public boolean isShown ()
  {
    return allAreShown ();
  }

  @Override
  public void addListener (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    for (final Dialog dialog : getDialogs ())
    {
      dialog.addListener (listener);
    }
  }

  @Override
  public void enableInput ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.enableInput ();
    }
  }

  @Override
  public void disableInput ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.disableInput ();
    }
  }

  @Override
  public boolean isInputDisabled ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      if (!dialog.isInputDisabled ()) return false;
    }

    return true;
  }

  @Override
  public void enableSubmission ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.enableSubmission ();
    }
  }

  @Override
  public void disableSubmission ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.disableSubmission ();
    }
  }

  @Override
  public boolean isSubmissionDisabled ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      if (!dialog.isSubmissionDisabled ()) return false;
    }

    return true;
  }

  @Override
  public void setSubmissionDisabled (final boolean isDisabled)
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.setSubmissionDisabled (isDisabled);
    }
  }

  @Override
  public void enableTextButton (final String buttonText)
  {
    Arguments.checkIsNotNull (buttonText, "buttonText");

    for (final Dialog dialog : getDialogs ())
    {
      dialog.enableTextButton (buttonText);
    }
  }

  @Override
  public void disableTextButton (final String buttonText)
  {
    Arguments.checkIsNotNull (buttonText, "buttonText");

    for (final Dialog dialog : getDialogs ())
    {
      dialog.disableTextButton (buttonText);
    }
  }

  @Override
  public void setTextButtonDisabled (final String buttonText, final boolean isDisabled)
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.setTextButtonDisabled (buttonText, isDisabled);
    }
  }

  @Override
  public boolean isDisabledTextButton (final String buttonText)
  {
    for (final Dialog dialog : getDialogs ())
    {
      if (!dialog.isDisabledTextButton (buttonText)) return false;
    }

    return true;
  }

  @Override
  public void setPosition (final int upperLeftReferenceScreenSpaceX, final int upperLeftReferenceScreenSpaceY)
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.setPosition (upperLeftReferenceScreenSpaceX, upperLeftReferenceScreenSpaceY);
    }
  }

  @Override
  public void setSize (final int widthReferenceScreenSpace, final int heightReferenceScreenSpace)
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.setSize (widthReferenceScreenSpace, heightReferenceScreenSpace);
    }
  }

  @Override
  public void update (final float delta)
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.update (delta);
    }
  }

  @Override
  public void refreshAssets ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      dialog.refreshAssets ();
    }
  }

  /**
   * @return true if any {@link Dialog} is shown, false only if all dialogs are not shown.
   *
   * @see #allAreShown()
   * @see #noneAreShown()
   * @see #noneAreShownExcept(Dialog)
   * @see #noneAreShownExcept(Class)
   */
  public boolean anyIsShown ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      if (dialog.isShown ()) return true;
    }

    return false;
  }

  /**
   * @return true if all {@link Dialog}'s are shown, false otherwise.
   *
   * @see #anyIsShown()
   * @see #noneAreShown()
   * @see #noneAreShownExcept(Dialog)
   * @see #noneAreShownExcept(Class)
   */
  public boolean allAreShown ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      if (!dialog.isShown ()) return false;
    }

    return true;
  }

  /**
   * @return true if all {@link Dialog}'s are not shown, false otherwise.
   *
   * @see #noneAreShownExcept(Dialog)
   * @see #noneAreShownExcept(Class)
   * @see #allAreShown()
   * @see #anyIsShown()
   */
  public boolean noneAreShown ()
  {
    for (final Dialog dialog : getDialogs ())
    {
      if (dialog.isShown ()) return false;
    }

    return true;
  }

  /**
   * @return true if all {@link Dialog}'s except the specified dialog are not shown, false otherwise.
   *
   * @see #noneAreShownExcept(Class)
   * @see #noneAreShown()
   * @see #allAreShown()
   * @see #anyIsShown()
   */
  public boolean noneAreShownExcept (final Dialog exceptDialog)
  {
    Arguments.checkIsNotNull (exceptDialog, "exceptDialog");

    for (final Dialog dialog2 : getDialogs ())
    {
      if (dialog2.isShown () && !dialog2.equals (exceptDialog)) return false;
    }

    return true;
  }

  /**
   * @return true if all {@link Dialog}'s except the specified dialog are not shown, false otherwise.
   *
   * @see #noneAreShownExcept(Dialog)
   * @see #noneAreShown()
   * @see #allAreShown()
   * @see #anyIsShown()
   */
  public boolean noneAreShownExcept (final Class <? extends Dialog> exceptDialogClass)
  {
    Arguments.checkIsNotNull (exceptDialogClass, "exceptDialogClass");

    for (final Dialog dialog2 : getDialogs ())
    {
      if (dialog2.isShown () && !dialog2.getClass ().equals (exceptDialogClass)) return false;
    }

    return true;
  }

  public void add (final Dialog dialog)
  {
    Arguments.checkIsNotNull (dialog, "dialog");

    final Map <Class <? extends Dialog>, Dialog> copy = new HashMap<> (dialogClassesToDialogs);
    copy.put (dialog.getClass (), dialog);
    dialogClassesToDialogs = MutableClassToInstanceMap.create (copy);
  }

  public void add (final Dialog... dialogs)
  {
    Arguments.checkIsNotNull (dialogs, "dialogs");
    Arguments.checkHasNoNullElements (dialogs, "dialogs");

    final Map <Class <? extends Dialog>, Dialog> copy = new HashMap<> (dialogClassesToDialogs);

    for (final Dialog dialog : dialogs)
    {
      copy.put (dialog.getClass (), dialog);
    }

    dialogClassesToDialogs = MutableClassToInstanceMap.create (copy);
  }

  @SafeVarargs
  public final void remove (final Class <? extends Dialog>... dialogClasses)
  {
    Arguments.checkIsNotNull (dialogClasses, "dialogClasses");
    Arguments.checkHasNoNullElements (dialogClasses, "dialogClasses");

    final Map <Class <? extends Dialog>, Dialog> copy = new HashMap<> (dialogClassesToDialogs);

    for (final Class <? extends Dialog> dialogClass : dialogClasses)
    {
      copy.remove (dialogClass);
    }

    dialogClassesToDialogs = MutableClassToInstanceMap.create (copy);
  }

  public <T extends Dialog> T get (final Class <T> dialogClass)
  {
    Arguments.checkIsNotNull (dialogClass, "dialogClass");

    final T dialog = dialogClassesToDialogs.getInstance (dialogClass);

    if (dialog == null) Exceptions.throwIllegalArg ("{} not found.", dialogClass);

    return dialog;
  }

  public void dispose ()
  {
    dialogClassesToDialogs.clear ();
  }

  public boolean noneAreShownOf (final Class <? extends Dialog> dialogType)
  {
    Arguments.checkIsNotNull (dialogType, "dialogType");

    for (final Dialog dialog : getDialogs ())
    {
      if (dialog.isShown () && dialogType.isAssignableFrom (dialog.getClass ())) return false;
    }

    return true;
  }

  public boolean onlyAreShownOf (final Class <? extends Dialog> dialogType)
  {
    Arguments.checkIsNotNull (dialogType, "dialogType");

    for (final Dialog dialog : getDialogs ())
    {
      if (dialog.isShown () && !dialogType.isAssignableFrom (dialog.getClass ())) return false;
    }

    return true;
  }

  private Iterable <Dialog> getDialogs ()
  {
    return dialogClassesToDialogs.values ();
  }
}
