package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.tools.common.Arguments;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public abstract class AbstractOkPopup implements Popup
{
  private final DelegateDialog delegate;

  public AbstractOkPopup (final Skin skin, final PopupStyle popupStyle, final Stage stage)
  {
    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (popupStyle, "popupStyle");

    delegate = new DelegateDialog (popupStyle.getTitle (), skin.get (popupStyle.getWindowStyleName (),
                                                                     Window.WindowStyle.class), popupStyle, stage, skin)
    {
      @Override
      public void onSubmit ()
      {
        AbstractOkPopup.this.onSubmit ();
      }
    };

    addKeys ();
    addButtons ();
  }

  public AbstractOkPopup (final Skin skin,
                          final Window.WindowStyle windowStyle,
                          final PopupStyle popupStyle,
                          final Stage stage)
  {
    Arguments.checkIsNotNull (popupStyle, "popupStyle");

    delegate = new DelegateDialog (popupStyle.getTitle (), windowStyle, popupStyle, stage, skin)
    {
      @Override
      public void onSubmit ()
      {
        AbstractOkPopup.this.onSubmit ();
      }
    };

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
  public final boolean isShown ()
  {
    return delegate.isShown ();
  }

  @Override
  public final Actor asActor ()
  {
    return delegate.asActor ();
  }

  protected void addButtons ()
  {
    delegate.addButtons ();
  }

  protected final void addButton (final String buttonText, final PopupAction popupAction)
  {
    delegate.addButton (buttonText, popupAction);
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

  private abstract class DelegateDialog extends Dialog implements Popup
  {
    private final PopupStyle popupStyle;
    private final Stage stage;
    private final Skin skin;
    private final Label.LabelStyle labelStyle;
    private boolean isShown = false;
    private final Map <String, TextButton> buttonTextToTextButtons = new HashMap <> ();

    public DelegateDialog (final String title,
                           final WindowStyle windowStyle,
                           final PopupStyle popupStyle,
                           final Stage stage,
                           final Skin skin)
    {
      super (title, windowStyle);

      Arguments.checkIsNotNull (popupStyle, "popupStyle");
      Arguments.checkIsNotNull (stage, "stage");
      Arguments.checkIsNotNull (skin, "skin");

      this.popupStyle = popupStyle;
      this.stage = stage;
      this.skin = skin;

      labelStyle = new Label.LabelStyle (Assets.droidSans20, Color.WHITE);

      // TODO Production: Remove
      //debug ();

      configureWindow ();
      configureButtonTable ();
      configureContentTable ();
      addText ();
    }

    @Override
    public Dialog show (final Stage stage, final Action action)
    {
      if (isShown) return this;

      super.show (stage, action);

      setSize ();
      setPosition ();

      isShown = true;

      return this;
    }

    @Override
    public Dialog show (final Stage stage)
    {
      if (isShown) return this;

      super.show (stage, Actions.sequence (Actions.alpha (0), Actions.fadeIn (0.2f, Interpolation.fade)));

      setSize ();
      setPosition ();

      isShown = true;

      return this;
    }

    @Override
    public void hide (final Action action)
    {
      if (!isShown) return;

      super.hide (action);

      isShown = false;
    }

    @Override
    public void hide ()
    {
      if (!isShown) return;

      super.hide (Actions.fadeOut (0.2f, Interpolation.fade));

      isShown = false;
    }

    @Override
    protected void result (@Nullable final Object object)
    {
      if (!(object instanceof PopupAction) || object != PopupAction.SUBMIT_AND_HIDE) return;

      onSubmit ();
    }

    @Override
    public void show ()
    {
      show (stage);
    }

    @Override
    public boolean isShown ()
    {
      return isShown;
    }

    @Override
    public Actor asActor ()
    {
      return this;
    }

    public void configureWindow ()
    {
      setResizable (popupStyle.isResizable ());
      setMovable (popupStyle.isMovable ());
      pad (popupStyle.getBorderThicknessPixels ());

      if (popupStyle.getTitleHeight () == PopupStyle.AUTO_HEIGHT)
      {
        padTop (getPadTop () + getTitleTable ().getPrefHeight ());
      }
      else
      {
        padTop (getPadTop () + popupStyle.getTitleHeight ());
      }
    }

    public void addText ()
    {
      final Label label = new Label (popupStyle.getMessage (), labelStyle);
      label.setWrap (true);
      label.setAlignment (Align.topLeft);

      getContentTable ().add (label).expand ().fill ().top ();
    }

    public void addButtons ()
    {
      addButton ("OK", PopupAction.SUBMIT_AND_HIDE);
    }

    public void addButton (final String buttonText, final PopupAction popupAction)
    {
      Arguments.checkIsNotNull (buttonText, "buttonText");
      Arguments.checkIsNotNull (popupAction, "popupAction");

      final TextButton textButton = new TextButton (buttonText, skin.get (TextButton.TextButtonStyle.class));

      textButton.pad (0);
      textButton.getLabelCell ().size (110, 38);

      button (textButton, popupAction);

      buttonTextToTextButtons.put (textButton.getText ().toString (), textButton);
    }

    public void changeButtonText (final String oldText, final String newText)
    {
      Arguments.checkIsNotNull (oldText, "oldText");
      Arguments.checkIsNotNull (newText, "newText");
      Arguments.checkIsTrue (buttonTextToTextButtons.containsKey (oldText), "Cannot find button with text [" + oldText + "].");

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

    private void configureContentTable ()
    {
      getContentTable ().left ();
      getCell (getContentTable ()).pad (26);
    }

    private void configureButtonTable ()
    {
      getButtonTable ().defaults().space (26);
      getButtonTable ().pad (26);
      getCell (getButtonTable ()).right ();
    }

    private void setPosition ()
    {
      if (popupStyle.getPositionUpperLeftReferenceScreenSpaceX () == PopupStyle.AUTO_H_CENTER)
      {
        setX (Math.round ((stage.getWidth () - getWidth ()) / 2));
      }
      else
      {
        setX (popupStyle.getPositionUpperLeftReferenceScreenSpaceX ());
      }

      if (popupStyle.getPositionUpperLeftReferenceScreenSpaceY () == PopupStyle.AUTO_V_CENTER)
      {
        setY (Math.round ((stage.getHeight () - getHeight ()) / 2));
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
