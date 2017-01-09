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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.personbox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowHighlighting;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.personicons.PersonIcon;
import com.forerunnergames.peril.client.ui.widgets.personicons.PersonIconWidgetFactory;
import com.forerunnergames.peril.common.net.packets.person.PersonPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class PersonBoxRow <T extends PersonIcon, U extends PersonPacket> implements MessageBoxRow <Message>
{
  private static final Logger log = LoggerFactory.getLogger (PersonBoxRow.class);
  private final PersonIconWidgetFactory <T, U> widgetFactory;
  private final MessageBoxRowStyle rowStyle;
  private final MessageBoxRowHighlighting highlighting;
  private final Table table = new Table ();
  private final Stack stack = new Stack ();
  private final Cell <Actor> messageRowLeftCell;
  private final Cell <Actor> messageRowRightCell;
  private final Cell <Actor> personIconCell;
  private T personIcon;
  private U person;
  private MessageBoxRow <Message> messageRowLeft;
  private MessageBoxRow <Message> messageRowRight;
  private Message message;

  protected PersonBoxRow (final U person,
                          final MessageBoxRowStyle rowStyle,
                          final PersonIconWidgetFactory <T, U> widgetFactory)
  {
    Arguments.checkIsNotNull (person, "person");
    Arguments.checkIsNotNull (rowStyle, "rowStyle");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.rowStyle = rowStyle;
    this.widgetFactory = widgetFactory;

    highlighting = widgetFactory.createMessageBoxRowHighlighting ();
    personIcon = widgetFactory.createPersonIcon (person);

    table.left ();
    messageRowLeftCell = table.add ((Actor) null).padLeft (10).width (40);
    personIconCell = table.add (personIcon.asActor ()).spaceRight (8);
    messageRowRightCell = table.add ((Actor) null).spaceLeft (8);

    stack.add (highlighting.asActor ());
    stack.add (table);

    setPerson (person);
    unhighlight ();
  }

  @Override
  public final Message getMessage ()
  {
    return message;
  }

  @Override
  public final String getMessageText ()
  {
    return message.getText ();
  }

  @Override
  public final void refreshAssets ()
  {
    messageRowLeft.refreshAssets ();
    messageRowRight.refreshAssets ();
    highlighting.refreshAssets ();
    personIcon.refreshAssets ();
    table.invalidateHierarchy ();
  }

  @Override
  public final Actor asActor ()
  {
    return stack;
  }

  public final boolean personIsNot (final U person)
  {
    Arguments.checkIsNotNull (person, "person");

    return this.person.isNot (person);
  }

  public final void highlight ()
  {
    highlighting.setVisible (true);
  }

  public final void unhighlight ()
  {
    highlighting.setVisible (false);
  }

  public final boolean personIs (final U person)
  {
    Arguments.checkIsNotNull (person, "person");

    return this.person.is (person);
  }

  public final U getPerson ()
  {
    return person;
  }

  @OverridingMethodsMustInvokeSuper
  public void setPerson (final U person)
  {
    Arguments.checkIsNotNull (person, "person");

    log.trace ("Setting person: Old person: [{}] | New person: [{}]", this.person, person);

    this.person = person;

    messageRowLeft = createMessageRow (createMessageTextLeft (person));
    messageRowRight = createMessageRow (createMessageTextRight (person));

    message = createMessage (createMessageText (messageRowLeft.getMessageText (), messageRowRight.getMessageText ()));

    messageRowLeftCell.setActor (messageRowLeft.asActor ());
    messageRowRightCell.setActor (messageRowRight.asActor ());

    personIcon = widgetFactory.createPersonIcon (person);
    personIconCell.setActor (personIcon.asActor ());

    table.invalidateHierarchy ();
  }

  public final boolean personHasName (final String personName)
  {
    Arguments.checkIsNotNull (personName, "personName");

    return person.hasName (personName);
  }

  protected final String getPersonName ()
  {
    return person.getName ();
  }

  protected abstract String createMessageTextLeft (final U person);

  protected abstract String createMessageTextRight (final U person);

  protected abstract String createMessageText (final String messageTextLeft, final String messageTextRight);

  protected final void setMessageTextRight (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    messageRowRight = createMessageRow (text);
    messageRowRightCell.setActor (messageRowRight.asActor ());
    message = createMessage (createMessageText (messageRowLeft.getMessageText (), messageRowRight.getMessageText ()));

    table.invalidateHierarchy ();
  }

  private Message createMessage (final String messageText)
  {
    return new DefaultMessage (messageText);
  }

  private MessageBoxRow <Message> createMessageRow (final String messageText)
  {
    return widgetFactory.createMessageBoxRow (new DefaultMessage (messageText), rowStyle);
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{} | Person: [{}] | Highlighting: [{}] | Person Icon: [{}] | Message: [{}] |  "
                                   + "Message Row: [{}] | Row Style: [{}] | Table: [{}] | Stack: [{}]",
                           super.toString (), person, highlighting, personIcon, message, messageRowLeft, rowStyle,
                           table, stack);
  }
}
