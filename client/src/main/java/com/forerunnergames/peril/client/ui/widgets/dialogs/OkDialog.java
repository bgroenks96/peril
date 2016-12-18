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

package com.forerunnergames.peril.client.ui.widgets.dialogs;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.forerunnergames.peril.client.input.GdxKeyRepeatSystem;
import com.forerunnergames.peril.client.input.KeyRepeatListener;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRow;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public class OkDialog implements Dialog
{
  private final DelegateDialog delegate;
  private final GdxKeyRepeatSystem keyRepeat;

  protected enum DialogAction
  {
    SUBMIT_AND_HIDE (DialogProperty.SUBMITTABLE, DialogProperty.HIDEABLE),
    SUBMIT (DialogProperty.SUBMITTABLE),
    HIDE (DialogProperty.HIDEABLE),
    NONE;

    private final ImmutableList <DialogProperty> properties;

    boolean isSubmittable ()
    {
      return properties.contains (DialogProperty.SUBMITTABLE);
    }

    boolean isHideable ()
    {
      return properties.contains (DialogProperty.HIDEABLE);
    }

    DialogAction (final DialogProperty... properties)
    {
      this.properties = ImmutableList.copyOf (properties);
    }
  }

  private enum DialogProperty
  {
    SUBMITTABLE,
    HIDEABLE
  }

  public OkDialog (final WidgetFactory widgetFactory,
                   final DialogStyle dialogStyle,
                   final Stage stage,
                   final DialogListener listener)
  {
    Arguments.checkIsNotNull (dialogStyle, "dialogStyle");
    Arguments.checkIsNotNull (listener, "listener");

    delegate = new DelegateDialog (dialogStyle.getTitle (), widgetFactory, dialogStyle, stage, listener);

    keyRepeat = new GdxKeyRepeatSystem (Gdx.input, new KeyRepeatListener ()
    {
      @Override
      public void onKeyUp (final int keyCode)
      {
        OkDialog.this.onKeyUp (keyCode);
      }

      @Override
      public void onKeyDownRepeating (final int keyCode)
      {
        OkDialog.this.onKeyDownRepeating (keyCode);
      }
    });

    keyRepeat.setKeyRepeatRate (Input.Keys.LEFT, 50);
    keyRepeat.setKeyRepeatRate (Input.Keys.RIGHT, 50);
    keyRepeat.setKeyRepeatRate (Input.Keys.UP, 50);
    keyRepeat.setKeyRepeatRate (Input.Keys.DOWN, 50);
    keyRepeat.setKeyRepeat (Input.Keys.LEFT, true);
    keyRepeat.setKeyRepeat (Input.Keys.RIGHT, true);
    keyRepeat.setKeyRepeat (Input.Keys.UP, true);
    keyRepeat.setKeyRepeat (Input.Keys.DOWN, true);
    keyRepeat.setKeyRepeat (Input.Keys.BACKSPACE, true);
    keyRepeat.setKeyRepeat (Input.Keys.FORWARD_DEL, true);

    addKeys ();
    addButtons ();
  }

  @Override
  public void onKeyDownRepeating (final int keyCode)
  {
    // Empty base implementation
  }

  @Override
  public void onKeyUp (final int keyCode)
  {
    // Empty base implementation
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void show ()
  {
    delegate.show ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void show (@Nullable final Action action)
  {
    delegate.show (action);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void show (final String message)
  {
    delegate.show (message);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void show (final String message, @Nullable final Action action)
  {
    delegate.show (message, action);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void hide ()
  {
    delegate.hide ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void hide (@Nullable final Action action)
  {
    delegate.hide (action);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void setTitle (final String title)
  {
    Arguments.checkIsNotNull (title, "title");

    delegate.setTitle (title);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
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
  @OverridingMethodsMustInvokeSuper
  public void enableInput ()
  {
    delegate.enableInput ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void disableInput ()
  {
    delegate.disableInput ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public boolean isInputDisabled ()
  {
    return delegate.isInputDisabled ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void enableSubmission ()
  {
    delegate.enableSubmission ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void disableSubmission ()
  {
    delegate.disableSubmission ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public boolean isSubmissionDisabled ()
  {
    return delegate.isSubmissionDisabled ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void setSubmissionDisabled (final boolean isDisabled)
  {
    delegate.setSubmissionDisabled (isDisabled);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void enableTextButton (final String buttonText)
  {
    delegate.enableTextButton (buttonText);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void disableTextButton (final String buttonText)
  {
    delegate.disableTextButton (buttonText);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void setTextButtonDisabled (final String buttonText, final boolean isDisabled)
  {
    delegate.setTextButtonDisabled (buttonText, isDisabled);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public boolean isDisabledTextButton (final String buttonText)
  {
    return delegate.isDisabledTextButton (buttonText);
  }

  @Override
  public final void setPosition (final int upperLeftReferenceScreenSpaceX, final int upperLeftReferenceScreenSpaceY)
  {
    delegate.setPosition (upperLeftReferenceScreenSpaceX, upperLeftReferenceScreenSpaceY);
  }

  @Override
  public final void setSize (final int widthReferenceScreenSpace, final int heightReferenceScreenSpace)
  {
    delegate.setSize (widthReferenceScreenSpace, heightReferenceScreenSpace);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void update (final float delta)
  {
    delegate.update (delta);
    keyRepeat.update ();
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

  protected final void addTextButton (final String buttonText, final DialogAction dialogAction)
  {
    delegate.addTextButton (buttonText, dialogAction);
  }

  protected final TextButton addTextButton (final String buttonText,
                                            final DialogAction dialogAction,
                                            final EventListener listener)
  {
    return delegate.addTextButton (buttonText, dialogAction, listener);
  }

  protected final Button addButton (final String style, final DialogAction dialogAction, final EventListener listener)
  {
    return delegate.addButton (style, dialogAction, listener);
  }

  protected final ImageButton addImageButton (final String style,
                                              final DialogAction dialogAction,
                                              final EventListener listener)
  {
    return delegate.addImageButton (style, dialogAction, listener);
  }

  protected final void changeButtonText (final String oldText, final String newText)
  {
    delegate.changeButtonText (oldText, newText);
  }

  protected void addKeys ()
  {
    delegate.addKeys ();
  }

  protected final void addKey (final int keyCode, final DialogAction dialogAction)
  {
    delegate.addKey (keyCode, dialogAction);
  }

  protected final void addKey (final int keyCode, final DialogAction dialogAction, final KeyListener listener)
  {
    delegate.addKey (keyCode, dialogAction, listener);
  }

  protected final Table getContentTable ()
  {
    return delegate.getContentTable ();
  }

  protected final Table getButtonTable ()
  {
    return delegate.getButtonTable ();
  }

  private final class DelegateDialog extends com.badlogic.gdx.scenes.scene2d.ui.Dialog
  {
    private final Map <Button, String> buttonsToButtonStyleNames = new HashMap<> ();
    private final WidgetFactory widgetFactory;
    private final DialogStyle dialogStyle;
    private final Stage stage;
    private final DialogListener listener;
    private final MessageBox <MessageBoxRow <Message>> messageBox;
    private Cell <Actor> messageBoxCell;
    private boolean isShown = false;
    private boolean isInputDisabled = false;
    private boolean isSubmissionDisabled = false;
    private int positionX;
    private int positionY;
    private int width;
    private int height;

    DelegateDialog (final String title,
                    final WidgetFactory widgetFactory,
                    final DialogStyle dialogStyle,
                    final Stage stage,
                    final DialogListener listener)
    {
      super (title, widgetFactory.createWindowStyle (dialogStyle.getWindowStyleName ()));

      getTitleLabel ().setEllipsis (false);

      Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
      Arguments.checkIsNotNull (dialogStyle, "dialogStyle");
      Arguments.checkIsNotNull (stage, "stage");
      Arguments.checkIsNotNull (listener, "listener");

      this.widgetFactory = widgetFactory;
      this.dialogStyle = dialogStyle;
      this.stage = stage;
      this.listener = listener;
      positionX = dialogStyle.getPositionUpperLeftReferenceScreenSpaceX ();
      positionY = dialogStyle.getPositionUpperLeftReferenceScreenSpaceY ();
      width = dialogStyle.getWidthReferenceScreenSpace ();
      height = dialogStyle.getHeightReferenceScreenSpace ();

      messageBox = widgetFactory.createDialogMessageBox (dialogStyle.getMessageBoxScrollPaneStyleName (),
                                                         dialogStyle.getMessageBoxRowLabelStyleName (),
                                                         dialogStyle.getMessageBoxRowLabelAlignment (),
                                                         dialogStyle.getMessageBoxScrollbarStyle ());

      setDebug (dialogStyle.isDebug (), true);
      setModal (dialogStyle.isModal ());
      configureWindow ();
      configureButtonTable ();
      configureContentTable ();
      configureMessageBox ();
    }

    @Override
    public com.badlogic.gdx.scenes.scene2d.ui.Dialog show (final Stage stage, @Nullable final Action action)
    {
      isShown = true;

      stage.cancelTouchFocus ();

      OkDialog.this.refreshAssets ();

      setSize (width, height);
      setPosition (positionX, positionY);
      clearActions ();

      if (action == null)
      {
        super.show (stage, null);
        listener.onShow ();
        return this;
      }

      super.show (stage, Actions.sequence (action, Actions.run (new Runnable ()
      {
        @Override
        public void run ()
        {
          listener.onShow ();
        }
      })));

      return this;
    }

    @Override
    public com.badlogic.gdx.scenes.scene2d.ui.Dialog show (final Stage stage)
    {
      isShown = true;

      stage.cancelTouchFocus ();

      OkDialog.this.refreshAssets ();

      setSize (width, height);
      setPosition (positionX, positionY);
      clearActions ();

      super.show (stage, Actions.sequence (Actions.alpha (0), Actions.fadeIn (0.2f, Interpolation.fade),
                                           Actions.run (new Runnable ()
                                           {
                                             @Override
                                             public void run ()
                                             {
                                               listener.onShow ();
                                             }
                                           })));

      return this;
    }

    @Override
    public void hide (@Nullable final Action action)
    {
      isShown = false;

      clearActions ();

      if (action == null)
      {
        super.hide (null);
        listener.onHide ();
        return;
      }

      super.hide (Actions.sequence (action, Actions.run (new Runnable ()
      {
        @Override
        public void run ()
        {
          listener.onHide ();
        }
      })));
    }

    @Override
    public void hide ()
    {
      isShown = false;

      clearActions ();

      super.hide (Actions.sequence (Actions.fadeOut (0.2f, Interpolation.fade), Actions.run (new Runnable ()
      {
        @Override
        public void run ()
        {
          listener.onHide ();
        }
      })));
    }

    @Override
    protected void result (@Nullable final Object object)
    {
      // Cancels call to DelegateDialog#hide from the button table's ChangeListener in
      // com.badlogic.gdx.scenes.scene2d.ui.Dialog#initialize when a DialogAction#Submit result is triggered via a
      // button click (or a key press from the InputListener in com.badlogic.gdx.scenes.scene2d.ui.Dialog#key).
      //
      // BUGFIX: This must be called even for HIDE-able DialogAction's because otherwise DelegateDialog#hide() would be
      // called in addition to DelegateDialog#hide(Action) already called in this method, subsequently clearing all
      // actions passed to DelegateDialog#hide(Action) via this method - i.e., they will never be run.
      cancel ();

      if (isInputDisabled || !(object instanceof DialogAction)) return;

      final DialogAction action = (DialogAction) object;

      if (action == DialogAction.NONE) return;
      if (action.isSubmittable () && isSubmissionDisabled) return;

      if (action == DialogAction.SUBMIT)
      {
        addAction (Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            listener.onSubmit ();
          }
        }));

        return;
      }

      hide (Actions.sequence (Actions.fadeOut (0.2f, Interpolation.fade), Actions.run (new Runnable ()
      {
        @Override
        public void run ()
        {
          remove ();
          if (action.isSubmittable ()) listener.onSubmit ();
        }
      })));
    }

    public void configureWindow ()
    {
      setResizable (dialogStyle.isResizable ());
      setMovable (dialogStyle.isMovable ());
      pad (dialogStyle.getBorderThickness ());

      if (dialogStyle.getTitleHeight () == DialogStyle.AUTO_HEIGHT)
      {
        padTop (getPadTop () + getTitleTable ().getPrefHeight ());
      }
      else
      {
        padTop (getPadTop () + dialogStyle.getTitleHeight ());
      }
    }

    public void configureMessageBox ()
    {
      if (!dialogStyle.isMessageBox ()) return;

      messageBoxCell = getContentTable ().add (messageBox.asActor ()).expand ().fill ().top ().left ()
              .padLeft (dialogStyle.getTextPaddingLeft ()).padRight (dialogStyle.getTextPaddingRight ())
              .padTop (dialogStyle.getTextPaddingTop ()).padBottom (dialogStyle.getTextPaddingBottom ());

      setMessage (new DefaultMessage (dialogStyle.getMessage ()));
    }

    public void addButtons ()
    {
      addTextButton ("OK", DialogAction.SUBMIT_AND_HIDE);
    }

    public void addTextButton (final String buttonText, final DialogAction dialogAction)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");
      Arguments.checkIsNotNull (dialogAction, "dialogAction");

      addTextButton (dialogStyle.getTextButtonStyleName (), buttonText, dialogAction, new ClickListener ());
    }

    public TextButton addTextButton (final String buttonText,
                                     final DialogAction dialogAction,
                                     final EventListener listener)
    {
      return addTextButton (dialogStyle.getTextButtonStyleName (), buttonText, dialogAction, listener);
    }

    public TextButton addTextButton (final String style,
                                     final String buttonText,
                                     final DialogAction dialogAction,
                                     final EventListener listener)
    {
      Arguments.checkIsNotNull (style, "style");
      Arguments.checkIsNotNull (buttonText, "buttonText");
      Arguments.checkIsNotNull (dialogAction, "dialogAction");
      Arguments.checkIsNotNull (listener, "listener");

      final TextButton textButton = widgetFactory.createTextButton (buttonText, style, listener);

      textButton.padLeft (dialogStyle.getButtonTextPaddingLeft ()).padRight (dialogStyle.getButtonTextPaddingRight ())
              .padTop (dialogStyle.getButtonTextPaddingTop ()).padBottom (dialogStyle.getButtonTextPaddingBottom ());

      addButton (style, textButton, dialogAction);

      return textButton;
    }

    public Button addButton (final String style, final DialogAction dialogAction, final EventListener listener)
    {
      Arguments.checkIsNotNull (style, "style");
      Arguments.checkIsNotNull (dialogAction, "dialogAction");
      Arguments.checkIsNotNull (listener, "listener");

      final Button button = widgetFactory.createButton (style, listener);
      addButton (style, button, dialogAction);

      return button;
    }

    public ImageButton addImageButton (final String style,
                                       final DialogAction dialogAction,
                                       final EventListener listener)
    {
      Arguments.checkIsNotNull (style, "style");
      Arguments.checkIsNotNull (dialogAction, "dialogAction");
      Arguments.checkIsNotNull (dialogAction, "dialogAction");

      final ImageButton button = widgetFactory.createImageButton (style, listener);
      addButton (style, button, dialogAction);

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
      addKey (Input.Keys.ENTER, DialogAction.SUBMIT_AND_HIDE);
    }

    public void addKey (final int keyCode, final DialogAction dialogAction)
    {
      Arguments.checkIsNotNull (dialogAction, "dialogAction");

      key (keyCode, dialogAction);
    }

    public void addKey (final int keyCode, final DialogAction dialogAction, final KeyListener listener)
    {
      Arguments.checkIsNotNull (dialogAction, "dialogAction");
      Arguments.checkIsNotNull (listener, "listener");

      addListener (new InputListener ()
      {
        @Override
        public boolean keyDown (final InputEvent event, final int keycode)
        {
          if (keyCode != keycode || isInputDisabled) return false;
          listener.keyDown ();
          return keyCode != Input.Keys.ESCAPE;
        }
      });

      key (keyCode, dialogAction);
    }

    public void setMessage (final Message message)
    {
      Arguments.checkIsNotNull (message, "message");

      messageBox.clear ();
      messageBox.addRow (widgetFactory.createMessageBoxRow (message, messageBox.getRowStyle ()));
      messageBoxCell.maxHeight (messageBox.asActor ().getHeight ());
    }

    public void show ()
    {
      show (stage);
    }

    public void show (@Nullable final Action action)
    {
      show (stage, action);
    }

    public void show (final String message)
    {
      Arguments.checkIsNotNull (message, "message");

      setMessage (new DefaultMessage (message));
      show ();
    }

    public void show (final String message, @Nullable final Action action)
    {
      Arguments.checkIsNotNull (message, "message");

      setMessage (new DefaultMessage (message));
      show (action);
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
      setStyle (widgetFactory.createWindowStyle (dialogStyle.getWindowStyleName ()));

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
      if (!isInputDisabled) return;

      setTouchable (Touchable.enabled);

      for (final Button button : buttonsToButtonStyleNames.keySet ())
      {
        button.setDisabled (false);
      }

      isInputDisabled = false;
    }

    public void disableInput ()
    {
      if (isInputDisabled) return;

      setTouchable (Touchable.disabled);

      for (final Button button : buttonsToButtonStyleNames.keySet ())
      {
        button.setDisabled (true);
      }

      isInputDisabled = true;
    }

    public boolean isInputDisabled ()
    {
      return isInputDisabled;
    }

    public void enableSubmission ()
    {
      isSubmissionDisabled = false;
    }

    public void disableSubmission ()
    {
      isSubmissionDisabled = true;
    }

    public boolean isSubmissionDisabled ()
    {
      return isSubmissionDisabled;
    }

    public void setSubmissionDisabled (final boolean isDisabled)
    {
      isSubmissionDisabled = isDisabled;
    }

    public void enableTextButton (final String buttonText)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");

      getTextButton (buttonText).setDisabled (false);
    }

    public void disableTextButton (final String buttonText)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");

      getTextButton (buttonText).setDisabled (true);
    }

    public void setTextButtonDisabled (final String buttonText, final boolean isDisabled)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");

      if (isDisabled)
      {
        disableTextButton (buttonText);
        return;
      }

      enableTextButton (buttonText);
    }

    public boolean isDisabledTextButton (final String buttonText)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");

      return getTextButton (buttonText).isDisabled ();
    }

    public void setPosition (final int upperLeftReferenceScreenSpaceX, final int upperLeftReferenceScreenSpaceY)
    {
      positionX = upperLeftReferenceScreenSpaceX;
      positionY = upperLeftReferenceScreenSpaceY;

      if (positionX == DialogStyle.AUTO_H_CENTER)
      {
        setX ((stage.getWidth () - getWidth ()) / 2.0f);
      }
      else
      {
        setX (positionX);
      }

      if (positionY == DialogStyle.AUTO_V_CENTER)
      {
        setY ((stage.getHeight () - getHeight ()) / 2.0f);
      }
      else
      {
        setY (positionY - getHeight ());
      }
    }

    public void setSize (final int widthReferenceScreenSpace, final int heightReferenceScreenSpace)
    {
      width = widthReferenceScreenSpace;
      height = heightReferenceScreenSpace;

      if (width != DialogStyle.AUTO_WIDTH) setWidth (width);
      if (height != DialogStyle.AUTO_HEIGHT) setHeight (height);
    }

    private <T extends Button> void addButton (final String style, final T button, final DialogAction dialogAction)
    {
      Arguments.checkIsNotNull (style, "style");
      Arguments.checkIsNotNull (button, "button");
      Arguments.checkIsNotNull (dialogAction, "dialogAction");

      final Cell <T> buttonCell = getButtonTable ().add (button);

      if (dialogStyle.getButtonWidth () != DialogStyle.AUTO_WIDTH) buttonCell.width (dialogStyle.getButtonWidth ());
      if (dialogStyle.getButtonHeight () != DialogStyle.AUTO_HEIGHT) buttonCell.height (dialogStyle.getButtonHeight ());
      if (dialogAction.isSubmittable () || dialogAction.isHideable ()) setObject (button, dialogAction);

      buttonsToButtonStyleNames.put (button, style);
    }

    private TextButton getTextButton (final String buttonText)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");

      for (final Actor actor : getButtonTable ().getChildren ())
      {
        if (actor instanceof TextButton && buttonText.equals (((TextButton) actor).getText ().toString ()))
        {
          return (TextButton) actor;
        }
      }

      throw new IllegalStateException (Strings.format ("Cannot find button with text {}.", buttonText));
    }

    private void configureContentTable ()
    {
      getContentTable ().left ();
      getCell (getContentTable ()).padLeft (dialogStyle.getTextBoxPaddingLeft ())
              .padRight (dialogStyle.getTextBoxPaddingRight ()).padTop (dialogStyle.getTextBoxPaddingTop ())
              .padBottom (dialogStyle.getTextBoxPaddingBottom ());
    }

    private void configureButtonTable ()
    {
      getButtonTable ().defaults ().space (dialogStyle.getButtonSpacing ());
      getButtonTable ().right ();
    }
  }
}
