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

package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRow;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Strings;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public class OkPopup implements Popup
{
  private final DelegateDialog delegate;

  public OkPopup (final WidgetFactory widgetFactory,
                  final PopupStyle popupStyle,
                  final Stage stage,
                  final PopupListener listener)
  {
    Arguments.checkIsNotNull (popupStyle, "popupStyle");
    Arguments.checkIsNotNull (listener, "listener");

    delegate = new DelegateDialog (popupStyle.getTitle (), widgetFactory, popupStyle, stage, listener);

    addKeys ();
    addButtons ();
  }

  protected enum PopupAction
  {
    SUBMIT_AND_HIDE,
    SUBMIT,
    HIDE,
    NONE
  }

  @Override
  public final void show ()
  {
    delegate.show ();
  }

  @Override
  public void show (@Nullable final Action action)
  {
    delegate.show (action);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void hide ()
  {
    delegate.hide ();
  }

  @Override
  public void hide (@Nullable final Action action)
  {
    delegate.hide (action);
  }

  @Override
  public void setTitle (final String title)
  {
    Arguments.checkIsNotNull (title, "title");

    delegate.setTitle (title);
  }

  @Override
  public void setMessage (final Message message)
  {
    Arguments.checkIsNotNull (message, "message");

    delegate.setMessage (message);
  }

  @Override
  public final boolean isShown ()
  {
    return delegate.isShown ();
  }

  @Override
  public final void addListener (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    delegate.addListener (listener);
  }

  @Override
  public void enableInput ()
  {
    delegate.enableInput ();
  }

  @Override
  public void disableInput ()
  {
    delegate.disableInput ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void update (final float delta)
  {
    delegate.update (delta);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    delegate.refreshAssets ();
  }

  protected void addButtons ()
  {
    delegate.addButtons ();
  }

  protected final void addTextButton (final String buttonText, final PopupAction popupAction)
  {
    delegate.addTextButton (buttonText, popupAction);
  }

  protected final TextButton addTextButton (final String buttonText,
                                            final PopupAction popupAction,
                                            final EventListener listener)
  {
    return delegate.addTextButton (buttonText, popupAction, listener);
  }

  protected final Button addButton (final String style, final PopupAction popupAction, final EventListener listener)
  {
    return delegate.addButton (style, popupAction, listener);
  }

  protected final ImageButton addImageButton (final String style,
                                              final PopupAction popupAction,
                                              final EventListener listener)
  {
    return delegate.addImageButton (style, popupAction, listener);
  }

  protected final void changeButtonText (final String oldText, final String newText)
  {
    delegate.changeButtonText (oldText, newText);
  }

  protected void addKeys ()
  {
    delegate.addKeys ();
  }

  protected final void addKey (final int keyCode, final PopupAction popupAction)
  {
    delegate.addKey (keyCode, popupAction);
  }

  protected final void addKey (final int keyCode, final PopupAction popupAction, final KeyListener listener)
  {
    delegate.addKey (keyCode, popupAction, listener);
  }

  protected final Table getContentTable ()
  {
    return delegate.getContentTable ();
  }

  protected final Table getButtonTable ()
  {
    return delegate.getButtonTable ();
  }

  private final class DelegateDialog extends Dialog
  {
    private final Map <Button, String> buttonsToButtonStyleNames = new HashMap <> ();
    private final WidgetFactory widgetFactory;
    private final PopupStyle popupStyle;
    private final Stage stage;
    private final PopupListener listener;
    private final MessageBox <MessageBoxRow <Message>> messageBox;
    private boolean isShown = false;

    DelegateDialog (final String title,
                    final WidgetFactory widgetFactory,
                    final PopupStyle popupStyle,
                    final Stage stage,
                    final PopupListener listener)
    {
      super (title, widgetFactory.createWindowStyle (popupStyle.getWindowStyleName ()));

      getTitleLabel ().setEllipsis (false);

      Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
      Arguments.checkIsNotNull (popupStyle, "popupStyle");
      Arguments.checkIsNotNull (stage, "stage");
      Arguments.checkIsNotNull (listener, "listener");

      this.widgetFactory = widgetFactory;
      this.popupStyle = popupStyle;
      this.stage = stage;
      this.listener = listener;

      messageBox = widgetFactory.createPopupMessageBox (popupStyle.getMessageBoxScrollPaneStyleName (),
                                                        popupStyle.getMessageBoxRowLabelStyleName (),
                                                        popupStyle.getMessageBoxRowLabelAlignment (),
                                                        popupStyle.getMessageBoxScrollbarStyle ());

      setDebug (popupStyle.isDebug (), true);
      setModal (popupStyle.isModal ());
      configureWindow ();
      configureButtonTable ();
      configureContentTable ();
      configureMessageBox ();
    }

    @Override
    public Dialog show (final Stage stage, final Action action)
    {
      if (isShown) return this;

      stage.cancelTouchFocus ();

      OkPopup.this.refreshAssets ();

      super.show (stage, action);

      setSize ();
      setPosition ();

      isShown = true;

      listener.onShow ();

      return this;
    }

    @Override
    public Dialog show (final Stage stage)
    {
      if (isShown) return this;

      stage.cancelTouchFocus ();

      OkPopup.this.refreshAssets ();

      super.show (stage, Actions.sequence (Actions.alpha (0), Actions.fadeIn (0.2f, Interpolation.fade)));

      setSize ();
      setPosition ();

      isShown = true;

      listener.onShow ();

      return this;
    }

    @Override
    public void hide (@Nullable final Action action)
    {
      if (!isShown) return;

      super.hide (action);

      listener.onHide ();

      isShown = false;
    }

    @Override
    public void hide ()
    {
      if (!isShown) return;

      super.hide (Actions.fadeOut (0.2f, Interpolation.fade));

      listener.onHide ();

      isShown = false;
    }

    @Override
    protected void result (@Nullable final Object object)
    {
      if (!(object instanceof PopupAction) || object == PopupAction.NONE)
      {
        cancel ();
        return;
      }

      if (object == PopupAction.SUBMIT)
      {
        cancel ();
        listener.onSubmit ();
        return;
      }

      hide (Actions.sequence (Actions.fadeOut (0.2f, Interpolation.fade), Actions.run (new Runnable ()
      {
        @Override
        public void run ()
        {
          remove ();
          if (object == PopupAction.SUBMIT_AND_HIDE) listener.onSubmit ();
        }
      })));
    }

    public void configureWindow ()
    {
      setResizable (popupStyle.isResizable ());
      setMovable (popupStyle.isMovable ());
      pad (popupStyle.getBorderThickness ());

      if (popupStyle.getTitleHeight () == PopupStyle.AUTO_HEIGHT)
      {
        padTop (getPadTop () + getTitleTable ().getPrefHeight ());
      }
      else
      {
        padTop (getPadTop () + popupStyle.getTitleHeight ());
      }
    }

    public void configureMessageBox ()
    {
      if (!popupStyle.isMessageBox ()) return;

      getContentTable ().add (messageBox.asActor ()).expand ().fill ().top ().left ()
              .padLeft (popupStyle.getTextPaddingLeft ()).padRight (popupStyle.getTextPaddingRight ())
              .padTop (popupStyle.getTextPaddingTop ()).padBottom (popupStyle.getTextPaddingBottom ());

      setMessage (new DefaultMessage (popupStyle.getMessage ()));
    }

    public void addButtons ()
    {
      addTextButton ("OK", PopupAction.SUBMIT_AND_HIDE);
    }

    public void addTextButton (final String buttonText, final PopupAction popupAction)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");
      Arguments.checkIsNotNull (popupAction, "popupAction");

      addTextButton (popupStyle.getTextButtonStyleName (), buttonText, popupAction, new ClickListener ());
    }

    public TextButton addTextButton (final String buttonText,
                                     final PopupAction popupAction,
                                     final EventListener listener)
    {
      return addTextButton (popupStyle.getTextButtonStyleName (), buttonText, popupAction, listener);
    }

    public TextButton addTextButton (final String style,
                                     final String buttonText,
                                     final PopupAction popupAction,
                                     final EventListener listener)
    {
      Arguments.checkIsNotNull (style, "style");
      Arguments.checkIsNotNull (buttonText, "buttonText");
      Arguments.checkIsNotNull (popupAction, "popupAction");
      Arguments.checkIsNotNull (listener, "listener");

      final TextButton textButton = widgetFactory.createTextButton (buttonText, style, listener);

      textButton.padLeft (popupStyle.getButtonTextPaddingLeft ()).padRight (popupStyle.getButtonTextPaddingRight ())
              .padTop (popupStyle.getButtonTextPaddingTop ()).padBottom (popupStyle.getButtonTextPaddingBottom ());

      addButton (style, textButton, popupAction);

      return textButton;
    }

    public Button addButton (final String style, final PopupAction popupAction, final EventListener listener)
    {
      Arguments.checkIsNotNull (style, "style");
      Arguments.checkIsNotNull (popupAction, "popupAction");
      Arguments.checkIsNotNull (listener, "listener");

      final Button button = widgetFactory.createButton (style, listener);
      addButton (style, button, popupAction);

      return button;
    }

    public ImageButton addImageButton (final String style, final PopupAction popupAction, final EventListener listener)
    {
      Arguments.checkIsNotNull (style, "style");
      Arguments.checkIsNotNull (popupAction, "popupAction");
      Arguments.checkIsNotNull (popupAction, "popupAction");

      final ImageButton button = widgetFactory.createImageButton (style, listener);
      addButton (style, button, popupAction);

      return button;
    }

    public void changeButtonText (final String oldText, final String newText)
    {
      Arguments.checkIsNotNull (oldText, "oldText");
      Arguments.checkIsNotNull (newText, "newText");

      getTextButton (oldText).setText (newText);
    }

    public void addKeys ()
    {
      addKey (Input.Keys.ENTER, PopupAction.SUBMIT_AND_HIDE);
    }

    public void addKey (final int keyCode, final PopupAction popupAction)
    {
      Arguments.checkIsNotNull (popupAction, "popupAction");

      key (keyCode, popupAction);
    }

    public void addKey (final int keyCode, final PopupAction popupAction, final KeyListener listener)
    {
      Arguments.checkIsNotNull (popupAction, "popupAction");
      Arguments.checkIsNotNull (listener, "listener");

      addListener (new InputListener ()
      {
        @Override
        public boolean keyDown (final InputEvent event, final int keycode)
        {
          if (keyCode != keycode) return false;

          listener.keyDown ();
          return true;
        }
      });

      key (keyCode, popupAction);
    }

    public void setMessage (final Message message)
    {
      Arguments.checkIsNotNull (message, "message");

      messageBox.clear ();
      messageBox.addRow (widgetFactory.createMessageBoxRow (message, messageBox.getRowStyle ()));
    }

    public void show ()
    {
      show (stage);
    }

    public void show (@Nullable final Action action)
    {
      show (stage, action);
    }

    public boolean isShown ()
    {
      return isShown;
    }

    public void update (final float delta)
    {
      act (delta);
    }

    public void refreshAssets ()
    {
      setStyle (widgetFactory.createWindowStyle (popupStyle.getWindowStyleName ()));

      messageBox.refreshAssets ();

      for (final Map.Entry <Button, String> entry : buttonsToButtonStyleNames.entrySet ())
      {
        final Button button = entry.getKey ();
        final String styleName = entry.getValue ();
        button.setStyle (widgetFactory.createButtonStyle (styleName, button.getStyle ().getClass ()));
      }
    }

    public void setTitle (final String title)
    {
      Arguments.checkIsNotNull (title, "title");

      getTitleLabel ().setText (title);
    }

    public void enableInput ()
    {
      setTouchable (Touchable.enabled);
    }

    public void disableInput ()
    {
      setTouchable (Touchable.disabled);
    }

    private <T extends Button> void addButton (final String style, final T button, final PopupAction popupAction)
    {
      Arguments.checkIsNotNull (style, "style");
      Arguments.checkIsNotNull (button, "button");
      Arguments.checkIsNotNull (popupAction, "popupAction");

      final Cell <T> buttonCell = getButtonTable ().add (button);

      if (popupStyle.getButtonWidth () != PopupStyle.AUTO_WIDTH) buttonCell.width (popupStyle.getButtonWidth ());
      if (popupStyle.getButtonHeight () != PopupStyle.AUTO_HEIGHT) buttonCell.height (popupStyle.getButtonHeight ());

      if (popupAction != PopupAction.NONE) setObject (button, popupAction);

      buttonsToButtonStyleNames.put (button, style);
    }

    private TextButton getTextButton (final String buttonText)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");

      for (final Actor actor : getButtonTable ().getChildren ())
      {
        if (actor instanceof TextButton
                && buttonText.equals (((TextButton) actor).getText ().toString ())) return (TextButton) actor;
      }

      throw new IllegalStateException (Strings.format ("Cannot find button with text {}.", buttonText));
    }

    private void configureContentTable ()
    {
      getContentTable ().left ();
      getCell (getContentTable ()).padLeft (popupStyle.getTextBoxPaddingLeft ())
              .padRight (popupStyle.getTextBoxPaddingRight ()).padTop (popupStyle.getTextBoxPaddingTop ())
              .padBottom (popupStyle.getTextBoxPaddingBottom ());
    }

    private void configureButtonTable ()
    {
      getButtonTable ().defaults ().space (popupStyle.getButtonSpacing ());
      getCell (getButtonTable ()).right ();
    }

    private void setPosition ()
    {
      if (popupStyle.getPositionUpperLeftReferenceScreenSpaceX () == PopupStyle.AUTO_H_CENTER)
      {
        setX ((stage.getWidth () - getWidth ()) / 2);
      }
      else
      {
        setX (popupStyle.getPositionUpperLeftReferenceScreenSpaceX ());
      }

      if (popupStyle.getPositionUpperLeftReferenceScreenSpaceY () == PopupStyle.AUTO_V_CENTER)
      {
        setY ((stage.getHeight () - getHeight ()) / 2);
      }
      else
      {
        setY (popupStyle.getPositionUpperLeftReferenceScreenSpaceY () - getHeight ());
      }
    }

    private void setSize ()
    {
      if (popupStyle.getWidthReferenceScreenSpace () != PopupStyle.AUTO_WIDTH)
      {
        setWidth (popupStyle.getWidthReferenceScreenSpace ());
      }

      if (popupStyle.getHeightReferenceScreenSpace () != PopupStyle.AUTO_HEIGHT)
      {
        setHeight (popupStyle.getHeightReferenceScreenSpace ());
      }
    }
  }
}
