/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

import com.forerunnergames.peril.client.input.KeyRepeatListener;
import com.forerunnergames.tools.common.Message;

import javax.annotation.Nullable;

public interface Dialog extends KeyRepeatListener
{
  Dialog NULL = new NullDialog ();

  void show ();

  void show (@Nullable final Action action);

  void show (final String message);

  void show (final String message, @Nullable final Action action);

  void hide ();

  void hide (@Nullable final Action action);

  void setTitle (final String title);

  void setMessage (final Message message);

  boolean isShown ();

  void addListener (final EventListener listener);

  void enableInput ();

  void disableInput ();

  boolean isInputDisabled ();

  void enableSubmission ();

  void disableSubmission ();

  boolean isSubmissionDisabled ();

  void setSubmissionDisabled (final boolean isDisabled);

  void enableTextButton (final String buttonText);

  void disableTextButton (final String buttonText);

  void setTextButtonDisabled (final String buttonText, final boolean isDisabled);

  boolean isDisabledTextButton (final String buttonText);

  void setPosition (final int upperLeftReferenceScreenSpaceX, final int upperLeftReferenceScreenSpaceY);

  void setSize (final int widthReferenceScreenSpace, final int heightReferenceScreenSpace);

  void update (final float delta);

  void refreshAssets ();
}
