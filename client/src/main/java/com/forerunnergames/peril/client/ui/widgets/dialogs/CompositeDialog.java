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

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

public final class CompositeDialog implements Dialog
{
  private final ImmutableSet <Dialog> dialogs;

  public CompositeDialog (final Dialog... dialogs)
  {
    Arguments.checkIsNotNull (dialogs, "dialogs");
    Arguments.checkHasNoNullElements (dialogs, "dialogs");

    this.dialogs = ImmutableSet.copyOf (dialogs);
  }

  @Override
  public void show ()
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.show ();
    }
  }

  @Override
  public void show (@Nullable final Action action)
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.show (action);
    }
  }

  @Override
  public void hide ()
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.hide ();
    }
  }

  @Override
  public void hide (@Nullable final Action action)
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.hide (action);
    }
  }

  @Override
  public void setTitle (final String title)
  {
    Arguments.checkIsNotNull (title, "title");

    for (final Dialog dialog : dialogs)
    {
      dialog.setTitle (title);
    }
  }

  @Override
  public void setMessage (final Message message)
  {
    Arguments.checkIsNotNull (message, "message");

    for (final Dialog dialog : dialogs)
    {
      dialog.setMessage (message);
    }
  }

  @Override
  public boolean isShown ()
  {
    for (final Dialog dialog : dialogs)
    {
      if (!dialog.isShown ()) return false;
    }

    return true;
  }

  @Override
  public void addListener (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    for (final Dialog dialog : dialogs)
    {
      dialog.addListener (listener);
    }
  }

  @Override
  public void enableInput ()
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.enableInput ();
    }
  }

  @Override
  public void disableInput ()
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.disableInput ();
    }
  }

  @Override
  public boolean isInputDisabled ()
  {
    for (final Dialog dialog : dialogs)
    {
      if (!dialog.isInputDisabled ()) return false;
    }

    return true;
  }

  @Override
  public void enableSubmission ()
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.enableSubmission ();
    }
  }

  @Override
  public void disableSubmission ()
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.disableSubmission ();
    }
  }

  @Override
  public void setSubmissionDisabled (final boolean isDisabled)
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.setSubmissionDisabled (isDisabled);
    }
  }

  @Override
  public boolean isSubmissionDisabled ()
  {
    for (final Dialog dialog : dialogs)
    {
      if (!dialog.isSubmissionDisabled ()) return false;
    }

    return true;
  }

  @Override
  public void enableTextButton (final String buttonText)
  {
    Arguments.checkIsNotNull (buttonText, "buttonText");

    for (final Dialog dialog : dialogs)
    {
      dialog.enableTextButton (buttonText);
    }
  }

  @Override
  public void disableTextButton (final String buttonText)
  {
    Arguments.checkIsNotNull (buttonText, "buttonText");

    for (final Dialog dialog : dialogs)
    {
      dialog.disableTextButton (buttonText);
    }
  }

  @Override
  public void setTextButtonDisabled (final String buttonText, final boolean isDisabled)
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.setTextButtonDisabled (buttonText, isDisabled);
    }
  }

  @Override
  public boolean isDisabledTextButton (final String buttonText)
  {
    for (final Dialog dialog : dialogs)
    {
      if (!dialog.isDisabledTextButton (buttonText)) return false;
    }

    return true;
  }

  @Override
  public void update (final float delta)
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.update (delta);
    }
  }

  @Override
  public void refreshAssets ()
  {
    for (final Dialog dialog : dialogs)
    {
      dialog.refreshAssets ();
    }
  }
}
