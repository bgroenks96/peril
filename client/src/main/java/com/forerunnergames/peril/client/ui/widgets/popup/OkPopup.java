package com.forerunnergames.peril.client.ui.widgets.popup;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;

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
    HIDE,
    NONE
  }

  @Override
  public final void show ()
  {
    delegate.show ();
  }

  @Override
  public final void hide ()
  {
    delegate.hide ();
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

  protected void onSubmit ()
  {
  }

  protected void addButtons ()
  {
    delegate.addButtons ();
  }

  protected final void addButton (final String buttonText, final PopupAction popupAction)
  {
    delegate.addButton (buttonText, popupAction);
  }

  protected final void addButton (final String buttonText, final PopupAction popupAction, final EventListener listener)
  {
    delegate.addButton (buttonText, popupAction, listener);
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

  protected final Table getContentTable ()
  {
    return delegate.getContentTable ();
  }

  private final class DelegateDialog extends Dialog
  {
    private final WidgetFactory widgetFactory;
    private final PopupStyle popupStyle;
    private final Stage stage;
    private final PopupListener listener;
    private final Map <String, TextButton> buttonTextToTextButtons = new HashMap <> ();
    private final MessageBox <Message> messageBox;
    private boolean isShown = false;

    DelegateDialog (final String title,
                    final WidgetFactory widgetFactory,
                    final PopupStyle popupStyle,
                    final Stage stage,
                    final PopupListener listener)
    {
      super (title, widgetFactory.createWindowStyle (popupStyle.getWindowStyleName ()));

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

      if (popupStyle.isDebug ()) setDebug (true, true);

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
    public void hide (final Action action)
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
      if (!(object instanceof PopupAction) || object != PopupAction.SUBMIT_AND_HIDE) return;

      hide (Actions.sequence (Actions.fadeOut (0.2f, Interpolation.fade), Actions.run (new Runnable ()
      {
        @Override
        public void run ()
        {
          remove ();
          onSubmit ();
          listener.onSubmit ();
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
      addButton ("OK", PopupAction.SUBMIT_AND_HIDE);
    }

    public void addButton (final String buttonText, final PopupAction popupAction)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");
      Arguments.checkIsNotNull (popupAction, "popupAction");

      addButton (buttonText, popupAction, new ClickListener ());
    }

    public void addButton (final String buttonText, final PopupAction popupAction, final EventListener listener)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");
      Arguments.checkIsNotNull (popupAction, "popupAction");
      Arguments.checkIsNotNull (listener, "listener");

      final TextButton textButton = widgetFactory.createTextButton (buttonText, popupStyle.getTextButtonStyleName (),
                                                                    listener);

      textButton.padLeft (popupStyle.getButtonTextPaddingLeft ()).padRight (popupStyle.getButtonTextPaddingRight ())
              .padTop (popupStyle.getButtonTextPaddingTop ()).padBottom (popupStyle.getButtonTextPaddingBottom ());

      final Cell <TextButton> textButtonCell = getButtonTable ().add (textButton);

      if (popupStyle.getButtonWidth () != PopupStyle.AUTO_WIDTH) textButtonCell.width (popupStyle.getButtonWidth ());
      if (popupStyle.getButtonHeight () != PopupStyle.AUTO_HEIGHT) textButtonCell
              .height (popupStyle.getButtonHeight ());

      if (popupAction != PopupAction.NONE) setObject (textButton, popupAction);

      buttonTextToTextButtons.put (textButton.getText ().toString (), textButton);
    }

    public void changeButtonText (final String oldText, final String newText)
    {
      Arguments.checkIsNotNull (oldText, "oldText");
      Arguments.checkIsNotNull (newText, "newText");
      Arguments.checkIsTrue (buttonTextToTextButtons.containsKey (oldText),
                             "Cannot find button with text [" + oldText + "].");

      buttonTextToTextButtons.get (oldText).setText (newText);
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

    public void setMessage (final Message message)
    {
      Arguments.checkIsNotNull (message, "message");

      messageBox.clear ();
      messageBox.addMessage (message);
    }

    public void show ()
    {
      show (stage);
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

      for (final TextButton button : buttonTextToTextButtons.values ())
      {
        button.setStyle (widgetFactory.createTextButtonStyle (popupStyle.getTextButtonStyleName ()));
      }
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
