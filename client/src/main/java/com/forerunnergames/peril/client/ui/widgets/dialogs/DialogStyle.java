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

import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.widgets.messagebox.ScrollbarStyle;
import com.forerunnergames.tools.common.Arguments;

public final class DialogStyle
{
  public static final int DEFAULT_MESSAGE_BOX_ROW_LABEL_ALIGNMENT = Align.topLeft;
  public static final ScrollbarStyle DEFAULT_MESSAGE_BOX_SCROLLBAR_STYLE = new ScrollbarStyle (
          ScrollbarStyle.Scrollbars.OPTIONAL, 14, 14);
  public static final String DEFAULT_TITLE = "";
  public static final String DEFAULT_WINDOW_MESSAGE = "";
  public static final int DEFAULT_BORDER_THICKNESS = 0;
  public static final int DEFAULT_BUTTON_SPACING = 20;
  public static final int DEFAULT_BUTTON_TEXT_PADDING = 0;
  public static final int DEFAULT_BUTTON_TEXT_PADDING_LEFT = 0;
  public static final int DEFAULT_BUTTON_TEXT_PADDING_RIGHT = 0;
  public static final int DEFAULT_BUTTON_TEXT_PADDING_TOP = 0;
  public static final int DEFAULT_BUTTON_TEXT_PADDING_BOTTOM = 0;
  public static final int DEFAULT_TEXT_PADDING = 0;
  public static final int DEFAULT_TEXT_PADDING_LEFT = 0;
  public static final int DEFAULT_TEXT_PADDING_RIGHT = 0;
  public static final int DEFAULT_TEXT_PADDING_TOP = 0;
  public static final int DEFAULT_TEXT_PADDING_BOTTOM = 0;
  public static final int DEFAULT_TEXT_BOX_PADDING = 0;
  public static final int DEFAULT_TEXT_BOX_PADDING_LEFT = 0;
  public static final int DEFAULT_TEXT_BOX_PADDING_RIGHT = 0;
  public static final int DEFAULT_TEXT_BOX_PADDING_TOP = 0;
  public static final int DEFAULT_TEXT_BOX_PADDING_BOTTOM = 0;
  public static final boolean DEFAULT_IS_MESSAGE_BOX = true;
  public static final boolean DEFAULT_IS_RESIZABLE = false;
  public static final boolean DEFAULT_IS_MOVABLE = false;
  public static final boolean DEFAULT_IS_MODAL = true;
  public static final boolean DEFAULT_IS_DEBUG = false;
  public static final int AUTO_H_CENTER = -1;
  public static final int AUTO_V_CENTER = -1;
  public static final int AUTO_WIDTH = -1;
  public static final int AUTO_HEIGHT = -1;
  public static final int DEFAULT_POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE_X = AUTO_H_CENTER;
  public static final int DEFAULT_POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE_Y = AUTO_V_CENTER;
  public static final int DEFAULT_WIDTH_REFERENCE_SCREEN_SPACE = AUTO_WIDTH;
  public static final int DEFAULT_HEIGHT_REFERENCE_SCREEN_SPACE = AUTO_HEIGHT;
  public static final int DEFAULT_TITLE_HEIGHT = AUTO_HEIGHT;
  public static final int DEFAULT_BUTTON_WIDTH = AUTO_WIDTH;
  public static final int DEFAULT_BUTTON_HEIGHT = AUTO_HEIGHT;
  private final String windowStyleName;
  private final String textButtonStyleName;
  private final String messageBoxRowLabelStyleName;
  private final int messageBoxRowLabelAlignment;
  private final String messageBoxScrollPaneStyleName;
  private final ScrollbarStyle messageBoxScrollbarStyle;
  private final String title;
  private final int titleHeight;
  private final int buttonWidth;
  private final int buttonHeight;
  private final String message;
  private final int positionUpperLeftReferenceScreenSpaceX;
  private final int positionUpperLeftReferenceScreenSpaceY;
  private final int widthReferenceScreenSpace;
  private final int heightReferenceScreenSpace;
  private final int borderThickness;
  private final int buttonSpacing;
  private final int buttonTextPaddingLeft;
  private final int buttonTextPaddingRight;
  private final int buttonTextPaddingTop;
  private final int buttonTextPaddingBottom;
  private final int textPaddingLeft;
  private final int textPaddingRight;
  private final int textPaddingTop;
  private final int textPaddingBottom;
  private final int textBoxPaddingLeft;
  private final int textBoxPaddingRight;
  private final int textBoxPaddingTop;
  private final int textBoxPaddingBottom;
  private final boolean isMessageBox;
  private final boolean isResizable;
  private final boolean isMovable;
  private final boolean isModal;
  private final boolean isDebug;

  public static DialogStyleBuilder builder ()
  {
    return new DialogStyleBuilder ();
  }

  public String getWindowStyleName ()
  {
    return windowStyleName;
  }

  public String getTextButtonStyleName ()
  {
    return textButtonStyleName;
  }

  public String getMessageBoxRowLabelStyleName ()
  {
    return messageBoxRowLabelStyleName;
  }

  public int getMessageBoxRowLabelAlignment ()
  {
    return messageBoxRowLabelAlignment;
  }

  public String getMessageBoxScrollPaneStyleName ()
  {
    return messageBoxScrollPaneStyleName;
  }

  public ScrollbarStyle getMessageBoxScrollbarStyle ()
  {
    return messageBoxScrollbarStyle;
  }

  public String getTitle ()
  {
    return title;
  }

  public int getTitleHeight ()
  {
    return titleHeight;
  }

  public int getButtonWidth ()
  {
    return buttonWidth;
  }

  public int getButtonHeight ()
  {
    return buttonHeight;
  }

  public String getMessage ()
  {
    return message;
  }

  public int getPositionUpperLeftReferenceScreenSpaceX ()
  {
    return positionUpperLeftReferenceScreenSpaceX;
  }

  public int getPositionUpperLeftReferenceScreenSpaceY ()
  {
    return positionUpperLeftReferenceScreenSpaceY;
  }

  public int getWidthReferenceScreenSpace ()
  {
    return widthReferenceScreenSpace;
  }

  public int getHeightReferenceScreenSpace ()
  {
    return heightReferenceScreenSpace;
  }

  public int getBorderThickness ()
  {
    return borderThickness;
  }

  public int getButtonSpacing ()
  {
    return buttonSpacing;
  }

  public int getButtonTextPaddingLeft ()
  {
    return buttonTextPaddingLeft;
  }

  public int getButtonTextPaddingRight ()
  {
    return buttonTextPaddingRight;
  }

  public int getButtonTextPaddingTop ()
  {
    return buttonTextPaddingTop;
  }

  public int getButtonTextPaddingBottom ()
  {
    return buttonTextPaddingBottom;
  }

  public int getTextPaddingLeft ()
  {
    return textPaddingLeft;
  }

  public int getTextPaddingRight ()
  {
    return textPaddingRight;
  }

  public int getTextPaddingTop ()
  {
    return textPaddingTop;
  }

  public int getTextPaddingBottom ()
  {
    return textPaddingBottom;
  }

  public int getTextBoxPaddingLeft ()
  {
    return textBoxPaddingLeft;
  }

  public int getTextBoxPaddingRight ()
  {
    return textBoxPaddingRight;
  }

  public int getTextBoxPaddingTop ()
  {
    return textBoxPaddingTop;
  }

  public int getTextBoxPaddingBottom ()
  {
    return textBoxPaddingBottom;
  }

  public boolean isMessageBox ()
  {
    return isMessageBox;
  }

  public boolean isResizable ()
  {
    return isResizable;
  }

  public boolean isMovable ()
  {
    return isMovable;
  }

  public boolean isModal ()
  {
    return isModal;
  }

  public boolean isDebug ()
  {
    return isDebug;
  }

  private DialogStyle (final String windowStyleName,
                       final String textButtonStyleName,
                       final String messageBoxRowLabelStyleName,
                       final int messageBoxRowLabelAlignment,
                       final String messageBoxScrollPaneStyleName,
                       final ScrollbarStyle messageBoxScrollbarStyle,
                       final String title,
                       final int titleHeight,
                       final int buttonWidth,
                       final int buttonHeight,
                       final String message,
                       final int positionUpperLeftReferenceScreenSpaceX,
                       final int positionUpperLeftReferenceScreenSpaceY,
                       final int widthReferenceScreenSpace,
                       final int heightReferenceScreenSpace,
                       final int borderThickness,
                       final int buttonSpacing,
                       final int buttonTextPaddingLeft,
                       final int buttonTextPaddingRight,
                       final int buttonTextPaddingTop,
                       final int buttonTextPaddingBottom,
                       final int textPaddingLeft,
                       final int textPaddingRight,
                       final int textPaddingTop,
                       final int textPaddingBottom,
                       final int textBoxPaddingLeft,
                       final int textBoxPaddingRight,
                       final int textBoxPaddingTop,
                       final int textBoxPaddingBottom,
                       final boolean isMessageBox,
                       final boolean isResizable,
                       final boolean isMovable,
                       final boolean isModal,
                       final boolean isDebug)
  {
    Arguments.checkIsNotNull (windowStyleName, "windowStyleName");
    Arguments.checkIsNotNull (textButtonStyleName, "textButtonStyleName");
    Arguments.checkIsNotNull (messageBoxRowLabelStyleName, "messageBoxRowLabelStyleName");
    Arguments.checkIsNotNegative (messageBoxRowLabelAlignment, "messageBoxRowLabelAlignment");
    Arguments.checkIsNotNull (messageBoxScrollPaneStyleName, "messageBoxScrollPaneStyleName");
    Arguments.checkIsNotNull (messageBoxScrollbarStyle, "messageBoxScrollbarStyle");
    Arguments.checkIsNotNull (title, "title");
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNegative (borderThickness, "borderThickness");
    Arguments.checkIsNotNegative (buttonSpacing, "buttonSpacing");
    Arguments.checkIsNotNegative (buttonTextPaddingLeft, "buttonTextPaddingLeft");
    Arguments.checkIsNotNegative (buttonTextPaddingRight, "buttonTextPaddingRight");
    Arguments.checkIsNotNegative (buttonTextPaddingTop, "buttonTextPaddingTop");
    Arguments.checkIsNotNegative (buttonTextPaddingBottom, "buttonTextPaddingBottom");
    Arguments.checkIsNotNegative (textPaddingLeft, "textPaddingLeft");
    Arguments.checkIsNotNegative (textPaddingRight, "textPaddingRight");
    Arguments.checkIsNotNegative (textPaddingTop, "textPaddingTop");
    Arguments.checkIsNotNegative (textPaddingBottom, "textPaddingBottom");
    Arguments.checkIsNotNegative (textBoxPaddingLeft, "textBoxPaddingLeft");
    Arguments.checkIsNotNegative (textBoxPaddingRight, "textBoxPaddingRight");
    Arguments.checkIsNotNegative (textBoxPaddingTop, "textBoxPaddingTop");
    Arguments.checkIsNotNegative (textBoxPaddingBottom, "textBoxPaddingBottom");

    this.windowStyleName = windowStyleName;
    this.textButtonStyleName = textButtonStyleName;
    this.messageBoxRowLabelStyleName = messageBoxRowLabelStyleName;
    this.messageBoxRowLabelAlignment = messageBoxRowLabelAlignment;
    this.messageBoxScrollPaneStyleName = messageBoxScrollPaneStyleName;
    this.messageBoxScrollbarStyle = messageBoxScrollbarStyle;
    this.title = title;
    this.titleHeight = titleHeight;
    this.buttonWidth = buttonWidth;
    this.buttonHeight = buttonHeight;
    this.message = message;
    this.positionUpperLeftReferenceScreenSpaceX = positionUpperLeftReferenceScreenSpaceX;
    this.positionUpperLeftReferenceScreenSpaceY = positionUpperLeftReferenceScreenSpaceY;
    this.widthReferenceScreenSpace = widthReferenceScreenSpace;
    this.heightReferenceScreenSpace = heightReferenceScreenSpace;
    this.borderThickness = borderThickness;
    this.buttonSpacing = buttonSpacing;
    this.buttonTextPaddingLeft = buttonTextPaddingLeft;
    this.buttonTextPaddingRight = buttonTextPaddingRight;
    this.buttonTextPaddingTop = buttonTextPaddingTop;
    this.buttonTextPaddingBottom = buttonTextPaddingBottom;
    this.textPaddingLeft = textPaddingLeft;
    this.textPaddingRight = textPaddingRight;
    this.textPaddingTop = textPaddingTop;
    this.textPaddingBottom = textPaddingBottom;
    this.textBoxPaddingLeft = textBoxPaddingLeft;
    this.textBoxPaddingRight = textBoxPaddingRight;
    this.textBoxPaddingTop = textBoxPaddingTop;
    this.textBoxPaddingBottom = textBoxPaddingBottom;
    this.isMessageBox = isMessageBox;
    this.isResizable = isResizable;
    this.isMovable = isMovable;
    this.isModal = isModal;
    this.isDebug = isDebug;
  }

  public static final class DialogStyleBuilder
  {
    private String windowStyleName = StyleSettings.DEFAULT_MODAL_DIALOG_WINDOW_STYLE;
    private String textButtonStyleName = StyleSettings.DEFAULT_DIALOG_TEXT_BUTTON_STYLE;
    private String messageBoxRowLabelStyleName = StyleSettings.DEFAULT_DIALOG_MESSAGE_BOX_ROW_LABEL_STYLE;
    private int messageBoxRowLabelAlignment = DEFAULT_MESSAGE_BOX_ROW_LABEL_ALIGNMENT;
    private String messageBoxScrollPaneStyleName = StyleSettings.DEFAULT_MESSAGE_BOX_SCROLLPANE_STYLE;
    private ScrollbarStyle messageBoxScrollbarStyle = DEFAULT_MESSAGE_BOX_SCROLLBAR_STYLE;
    private String title = DEFAULT_TITLE;
    private int titleHeight = DEFAULT_TITLE_HEIGHT;
    private int buttonWidth = DEFAULT_BUTTON_WIDTH;
    private int buttonHeight = DEFAULT_BUTTON_HEIGHT;
    private String message = DEFAULT_WINDOW_MESSAGE;
    private int positionUpperLeftReferenceScreenSpaceX = DEFAULT_POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE_X;
    private int positionUpperLeftReferenceScreenSpaceY = DEFAULT_POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE_Y;
    private int widthReferenceScreenSpace = DEFAULT_WIDTH_REFERENCE_SCREEN_SPACE;
    private int heightReferenceScreenSpace = DEFAULT_HEIGHT_REFERENCE_SCREEN_SPACE;
    private int borderThickness = DEFAULT_BORDER_THICKNESS;
    private int buttonSpacing = DEFAULT_BUTTON_SPACING;
    private int buttonTextPaddingLeft = DEFAULT_BUTTON_TEXT_PADDING_LEFT;
    private int buttonTextPaddingRight = DEFAULT_BUTTON_TEXT_PADDING_RIGHT;
    private int buttonTextPaddingTop = DEFAULT_BUTTON_TEXT_PADDING_TOP;
    private int buttonTextPaddingBottom = DEFAULT_BUTTON_TEXT_PADDING_BOTTOM;
    private int textPaddingLeft = DEFAULT_TEXT_PADDING_LEFT;
    private int textPaddingRight = DEFAULT_TEXT_PADDING_RIGHT;
    private int textPaddingTop = DEFAULT_TEXT_PADDING_TOP;
    private int textPaddingBottom = DEFAULT_TEXT_PADDING_BOTTOM;
    private int textBoxPaddingLeft = DEFAULT_TEXT_BOX_PADDING_LEFT;
    private int textBoxPaddingRight = DEFAULT_TEXT_BOX_PADDING_RIGHT;
    private int textBoxPaddingTop = DEFAULT_TEXT_BOX_PADDING_TOP;
    private int textBoxPaddingBottom = DEFAULT_TEXT_BOX_PADDING_BOTTOM;
    private boolean isMessageBox = DEFAULT_IS_MESSAGE_BOX;
    private boolean isResizable = DEFAULT_IS_RESIZABLE;
    private boolean isMovable = DEFAULT_IS_MOVABLE;
    private boolean isModal = DEFAULT_IS_MODAL;
    private boolean isDebug = DEFAULT_IS_DEBUG;

    public DialogStyle build ()
    {
      return new DialogStyle (windowStyleName, textButtonStyleName, messageBoxRowLabelStyleName,
              messageBoxRowLabelAlignment, messageBoxScrollPaneStyleName, messageBoxScrollbarStyle, title, titleHeight,
              buttonWidth, buttonHeight, message, positionUpperLeftReferenceScreenSpaceX,
              positionUpperLeftReferenceScreenSpaceY, widthReferenceScreenSpace, heightReferenceScreenSpace,
              borderThickness, buttonSpacing, buttonTextPaddingLeft, buttonTextPaddingRight, buttonTextPaddingTop,
              buttonTextPaddingBottom, textPaddingLeft, textPaddingRight, textPaddingTop, textPaddingBottom,
              textBoxPaddingLeft, textBoxPaddingRight, textBoxPaddingTop, textBoxPaddingBottom, isMessageBox,
              isResizable, isMovable, isModal, isDebug);
    }

    public DialogStyleBuilder windowStyle (final String windowStyleName)
    {
      Arguments.checkIsNotNull (windowStyleName, "windowStyleName");

      this.windowStyleName = windowStyleName;

      return this;
    }

    public DialogStyleBuilder textButtonStyle (final String textButtonStyleName)
    {
      Arguments.checkIsNotNull (textButtonStyleName, "textButtonStyleName");

      this.textButtonStyleName = textButtonStyleName;

      return this;
    }

    public DialogStyleBuilder messageBoxRowLabelStyle (final String messageBoxRowLabelStyleName)
    {
      Arguments.checkIsNotNull (messageBoxRowLabelStyleName, "messageBoxRowLabelStyleName");

      this.messageBoxRowLabelStyleName = messageBoxRowLabelStyleName;

      return this;
    }

    public DialogStyleBuilder messageBoxRowLabelAlignment (final int messageBoxRowLabelAlignment)
    {
      Arguments.checkIsNotNegative (messageBoxRowLabelAlignment, "messageBoxRowLabelAlignment");

      this.messageBoxRowLabelAlignment = messageBoxRowLabelAlignment;

      return this;
    }

    public DialogStyleBuilder messageBoxScrollPaneStyle (final String messageBoxScrollPaneStyleName)
    {
      Arguments.checkIsNotNull (messageBoxScrollPaneStyleName, "messageBoxScrollPaneStyleName");

      this.messageBoxScrollPaneStyleName = messageBoxScrollPaneStyleName;

      return this;
    }

    public DialogStyleBuilder messageBoxScrollbarStyle (final ScrollbarStyle messageBoxScrollbarStyle)
    {
      Arguments.checkIsNotNull (messageBoxScrollbarStyle, "messageBoxScrollbarStyle");

      this.messageBoxScrollbarStyle = messageBoxScrollbarStyle;

      return this;
    }

    public DialogStyleBuilder title (final String title)
    {
      Arguments.checkIsNotNull (title, "title");

      this.title = title;

      return this;
    }

    public DialogStyleBuilder titleHeight (final int titleHeight)
    {
      this.titleHeight = titleHeight;

      return this;
    }

    public DialogStyleBuilder titleHeight (final float titleHeight)
    {
      this.titleHeight = Math.round (titleHeight);

      return this;
    }

    public DialogStyleBuilder buttonWidth (final int buttonWidth)
    {
      this.buttonWidth = buttonWidth;

      return this;
    }

    public DialogStyleBuilder buttonWidth (final float buttonWidth)
    {
      this.buttonWidth = Math.round (buttonWidth);

      return this;
    }

    public DialogStyleBuilder buttonHeight (final int buttonHeight)
    {
      this.buttonHeight = buttonHeight;

      return this;
    }

    public DialogStyleBuilder buttonHeight (final float buttonHeight)
    {
      this.buttonHeight = Math.round (buttonHeight);

      return this;
    }

    public DialogStyleBuilder buttonSize (final int buttonWidth, final int buttonHeight)
    {
      this.buttonWidth = buttonWidth;
      this.buttonHeight = buttonHeight;

      return this;
    }

    public DialogStyleBuilder buttonSize (final float buttonWidth, final float buttonHeight)
    {
      this.buttonWidth = Math.round (buttonWidth);
      this.buttonHeight = Math.round (buttonHeight);

      return this;
    }

    public DialogStyleBuilder message (final String message)
    {
      Arguments.checkIsNotNull (message, "message");

      this.message = message;

      return this;
    }

    public DialogStyleBuilder position (final int positionUpperLeftReferenceScreenSpaceX,
                                        final int positionUpperLeftReferenceScreenSpaceY)
    {
      this.positionUpperLeftReferenceScreenSpaceX = positionUpperLeftReferenceScreenSpaceX;
      this.positionUpperLeftReferenceScreenSpaceY = positionUpperLeftReferenceScreenSpaceY;

      return this;
    }

    public DialogStyleBuilder position (final float positionUpperLeftReferenceScreenSpaceX,
                                        final float positionUpperLeftReferenceScreenSpaceY)
    {
      this.positionUpperLeftReferenceScreenSpaceX = Math.round (positionUpperLeftReferenceScreenSpaceX);
      this.positionUpperLeftReferenceScreenSpaceY = Math.round (positionUpperLeftReferenceScreenSpaceY);

      return this;
    }

    public DialogStyleBuilder width (final int widthReferenceScreenSpace)
    {
      this.widthReferenceScreenSpace = widthReferenceScreenSpace;

      return this;
    }

    public DialogStyleBuilder width (final float widthReferenceScreenSpace)
    {
      this.widthReferenceScreenSpace = Math.round (widthReferenceScreenSpace);

      return this;
    }

    public DialogStyleBuilder height (final int heightReferenceScreenSpace)
    {
      this.heightReferenceScreenSpace = heightReferenceScreenSpace;

      return this;
    }

    public DialogStyleBuilder height (final float heightReferenceScreenSpace)
    {
      this.heightReferenceScreenSpace = Math.round (heightReferenceScreenSpace);

      return this;
    }

    public DialogStyleBuilder size (final int widthReferenceScreenSpace, final int heightReferenceScreenSpace)
    {
      return width (widthReferenceScreenSpace).height (heightReferenceScreenSpace);
    }

    public DialogStyleBuilder size (final float widthReferenceScreenSpace, final float heightReferenceScreenSpace)
    {
      return width (Math.round (widthReferenceScreenSpace)).height (Math.round (heightReferenceScreenSpace));
    }

    public DialogStyleBuilder border (final int borderThickness)
    {
      Arguments.checkIsNotNegative (borderThickness, "borderThickness");

      this.borderThickness = borderThickness;

      return this;
    }

    public DialogStyleBuilder buttonSpacing (final int buttonSpacing)
    {
      Arguments.checkIsNotNegative (buttonSpacing, "buttonSpacing");

      this.buttonSpacing = buttonSpacing;

      return this;
    }

    public DialogStyleBuilder buttonTextPaddingLeft (final int buttonTextPaddingLeft)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingLeft, "buttonTextPaddingLeft");

      this.buttonTextPaddingLeft = buttonTextPaddingLeft;

      return this;
    }

    public DialogStyleBuilder buttonTextPaddingRight (final int buttonTextPaddingRight)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingRight, "buttonTextPaddingRight");

      this.buttonTextPaddingRight = buttonTextPaddingRight;

      return this;
    }

    public DialogStyleBuilder buttonTextPaddingTop (final int buttonTextPaddingTop)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingTop, "buttonTextPaddingTop");

      this.buttonTextPaddingTop = buttonTextPaddingTop;

      return this;
    }

    public DialogStyleBuilder buttonTextPaddingBottom (final int buttonTextPaddingBottom)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingBottom, "buttonTextPaddingBottom");

      this.buttonTextPaddingBottom = buttonTextPaddingBottom;

      return this;
    }

    public DialogStyleBuilder buttonTextPaddingHorizontal (final int buttonTextPaddingHorizontal)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingHorizontal, "buttonTextPaddingHorizontal");

      buttonTextPaddingLeft = buttonTextPaddingHorizontal;
      buttonTextPaddingRight = buttonTextPaddingHorizontal;

      return this;
    }

    public DialogStyleBuilder buttonTextPaddingVertical (final int buttonTextPaddingVertical)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingVertical, "buttonTextPaddingVertical");

      buttonTextPaddingTop = buttonTextPaddingVertical;
      buttonTextPaddingBottom = buttonTextPaddingVertical;

      return this;
    }

    public DialogStyleBuilder buttonTextPadding (final int buttonTextPadding)
    {
      Arguments.checkIsNotNegative (buttonTextPadding, "buttonTextPadding");

      buttonTextPaddingLeft = buttonTextPadding;
      buttonTextPaddingRight = buttonTextPadding;
      buttonTextPaddingTop = buttonTextPadding;
      buttonTextPaddingBottom = buttonTextPadding;

      return this;
    }

    public DialogStyleBuilder textPaddingLeft (final int textPaddingLeft)
    {
      Arguments.checkIsNotNegative (textPaddingLeft, "textPaddingLeft");

      this.textPaddingLeft = textPaddingLeft;

      return this;
    }

    public DialogStyleBuilder textPaddingRight (final int textPaddingRight)
    {
      Arguments.checkIsNotNegative (textPaddingRight, "textPaddingRight");

      this.textPaddingRight = textPaddingRight;

      return this;
    }

    public DialogStyleBuilder textPaddingTop (final int textPaddingTop)
    {
      Arguments.checkIsNotNegative (textPaddingTop, "textPaddingTop");

      this.textPaddingTop = textPaddingTop;

      return this;
    }

    public DialogStyleBuilder textPaddingBottom (final int textPaddingBottom)
    {
      Arguments.checkIsNotNegative (textPaddingBottom, "textPaddingBottom");

      this.textPaddingBottom = textPaddingBottom;

      return this;
    }

    public DialogStyleBuilder textPaddingHorizontal (final int textPaddingHorizontal)
    {
      Arguments.checkIsNotNegative (textPaddingHorizontal, "textPaddingHorizontal");

      textPaddingLeft = textPaddingHorizontal;
      textPaddingRight = textPaddingHorizontal;

      return this;
    }

    public DialogStyleBuilder textPaddingVertical (final int textPaddingVertical)
    {
      Arguments.checkIsNotNegative (textPaddingVertical, "textPaddingVertical");

      textPaddingTop = textPaddingVertical;
      textPaddingBottom = textPaddingVertical;

      return this;
    }

    public DialogStyleBuilder textPadding (final int textPadding)
    {
      Arguments.checkIsNotNegative (textPadding, "textPadding");

      textPaddingLeft = textPadding;
      textPaddingRight = textPadding;
      textPaddingTop = textPadding;
      textPaddingBottom = textPadding;

      return this;
    }

    public DialogStyleBuilder textBoxPaddingRight (final int textBoxPaddingRight)
    {
      Arguments.checkIsNotNegative (textBoxPaddingRight, "textBoxPaddingRight");

      this.textBoxPaddingRight = textBoxPaddingRight;

      return this;
    }

    public DialogStyleBuilder textBoxPaddingTop (final int textBoxPaddingTop)
    {
      Arguments.checkIsNotNegative (textBoxPaddingTop, "textBoxPaddingTop");

      this.textBoxPaddingTop = textBoxPaddingTop;

      return this;
    }

    public DialogStyleBuilder textBoxPaddingBottom (final int textBoxPaddingBottom)
    {
      Arguments.checkIsNotNegative (textBoxPaddingBottom, "textBoxPaddingBottom");

      this.textBoxPaddingBottom = textBoxPaddingBottom;

      return this;
    }

    public DialogStyleBuilder textBoxPaddingHorizontal (final int textBoxPaddingHorizontal)
    {
      Arguments.checkIsNotNegative (textBoxPaddingHorizontal, "textBoxPaddingHorizontal");

      textBoxPaddingLeft = textBoxPaddingHorizontal;
      textBoxPaddingRight = textBoxPaddingHorizontal;

      return this;
    }

    public DialogStyleBuilder textBoxPaddingVertical (final int textBoxPaddingVertical)
    {
      Arguments.checkIsNotNegative (textBoxPaddingVertical, "textBoxPaddingVertical");

      textBoxPaddingTop = textBoxPaddingVertical;
      textBoxPaddingBottom = textBoxPaddingVertical;

      return this;
    }

    public DialogStyleBuilder textBoxPadding (final int textBoxPadding)
    {
      Arguments.checkIsNotNegative (textBoxPadding, "textBoxPadding");

      textBoxPaddingLeft = textBoxPadding;
      textBoxPaddingRight = textBoxPadding;
      textBoxPaddingTop = textBoxPadding;
      textBoxPaddingBottom = textBoxPadding;

      return this;
    }

    public DialogStyleBuilder messageBox (final boolean isMessageBox)
    {
      this.isMessageBox = isMessageBox;

      return this;
    }

    public DialogStyleBuilder resizable (final boolean isResizable)
    {
      this.isResizable = isResizable;

      return this;
    }

    public DialogStyleBuilder movable (final boolean isMovable)
    {
      this.isMovable = isMovable;

      return this;
    }

    public DialogStyleBuilder modal (final boolean isModal)
    {
      this.isModal = isModal;

      return this;
    }

    public DialogStyleBuilder debug ()
    {
      isDebug = true;

      return this;
    }

    public DialogStyleBuilder debug (final boolean isDebug)
    {
      this.isDebug = isDebug;

      return this;
    }

    private DialogStyleBuilder ()
    {
    }
  }
}
