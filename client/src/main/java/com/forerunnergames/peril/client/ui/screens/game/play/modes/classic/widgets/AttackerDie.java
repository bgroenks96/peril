package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AttackerDie implements Comparable <AttackerDie>
{
  private static final Logger log = LoggerFactory.getLogger (AttackerDie.class);
  private static final DieFaceValue INITIAL_FACE_VALUE = DieFaceValue.SIX;
  private final int index;
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final Button button;
  private DieFaceValue currentFaceValue = INITIAL_FACE_VALUE;
  private boolean isBeingActivated = false;
  private boolean isBeingDeactivated = false;
  private List <DieListener> listeners = new ArrayList <> ();

  public AttackerDie (final int index, final ClassicModePlayScreenWidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.index = index;
    this.widgetFactory = widgetFactory;

    button = widgetFactory.createAttackPopupAttackerDieFaceButton (currentFaceValue);

    button.addListener (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void enter (final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor)
      {
        super.enter (event, x, y, pointer, fromActor);

        if (!button.isTouchable ()) return;
        if (isBeingActivated || isBeingDeactivated) return;

        isBeingActivated = button.isDisabled ();
        isBeingDeactivated = !button.isDisabled ();

        // We must temporarily enable to be able to change button styles to + and -.
        button.setDisabled (false);

        refreshAssets ();
      }

      @Override
      public void exit (final InputEvent event, final float x, final float y, final int pointer, final Actor toActor)
      {
        super.exit (event, x, y, pointer, toActor);

        if (!button.isTouchable ()) return;

        if (isBeingActivated) button.setDisabled (true);
        if (isBeingDeactivated) button.setDisabled (false);

        isBeingActivated = false;
        isBeingDeactivated = false;

        refreshAssets ();
      }

      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        if (!button.isTouchable ()) return;

        if (isBeingActivated) activate ();
        if (isBeingDeactivated) deactivate ();

        isBeingActivated = false;
        isBeingDeactivated = false;

        refreshAssets ();
      }
    });
  }

  @Override
  public int compareTo (final AttackerDie o)
  {
    if (index == o.index) return 0;

    return index < o.index ? -1 : 1;
  }

  public void refreshAssets ()
  {
    if (isBeingActivated)
    {
      button.setStyle (widgetFactory.createAttackPopupAttackerDieActivateDieButtonStyle ());
    }
    else if (isBeingDeactivated)
    {
      button.setStyle (widgetFactory.createAttackPopupAttackerDieDeactivateDieButtonStyle ());
    }
    else
    {
      button.setStyle (widgetFactory.createAttackPopupAttackerDieFaceButtonStyle (currentFaceValue));
    }
  }

  public void roll (final DieFaceValue faceValue)
  {
    Arguments.checkIsNotNull (faceValue, "faceValue");

    if (!isActive ()) return;

    setFaceValue (faceValue);
  }

  public void setTouchable (final boolean isTouchable)
  {
    button.setTouchable (isTouchable ? Touchable.enabled : Touchable.disabled);
  }

  public Actor asActor ()
  {
    return button;
  }

  public boolean isActive ()
  {
    // We could just be temporarily enabled while being activated.
    // In that case, no, we aren't really active yet.
    return !button.isDisabled () && !isBeingActivated;
  }

  public void addListener (final DieListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    listeners.add (listener);
  }

  public void deactivate ()
  {
    if (isInactive ())
    {
      log.trace ("Ignoring deactivation of die because it is not active [{}]...", this);
      return;
    }

    log.trace ("Deactivating die [{}]...", this);

    button.setDisabled (true);

    for (final DieListener listener : listeners)
    {
      listener.onDeactivate (this);
    }

    log.trace ("Deactivated die [{}].", this);
  }

  public void activate ()
  {
    if (isActive ())
    {
      log.trace ("Ignoring activation of die because it is already active [{}]...", this);
      return;
    }

    log.trace ("Activating die [{}]...", this);

    setFaceValue (INITIAL_FACE_VALUE);

    button.setDisabled (false);

    for (final DieListener listener : listeners)
    {
      listener.onActivate (this);
    }

    log.trace ("Activated die [{}].", this);
  }

  public void reset ()
  {
    listeners.clear ();
    button.setDisabled (false);
    button.setTouchable (Touchable.enabled);
    isBeingActivated = false;
    isBeingDeactivated = false;
    currentFaceValue = INITIAL_FACE_VALUE;
    refreshAssets ();
  }

  private boolean isInactive ()
  {
    return button.isDisabled ();
  }

  private void setFaceValue (final DieFaceValue faceValue)
  {
    if (currentFaceValue == faceValue) return;

    currentFaceValue = faceValue;

    refreshAssets ();
  }

  @Override
  public int hashCode ()
  {
    return index;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    return index == ((AttackerDie) obj).index;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Index: {} | Current Face Value: {} | Active: {} | Touchable: {}",
                           getClass ().getSimpleName (), index, currentFaceValue, isActive (), button.isTouchable ());
  }
}
