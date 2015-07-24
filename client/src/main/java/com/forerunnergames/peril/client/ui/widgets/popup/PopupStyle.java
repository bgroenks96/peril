package com.forerunnergames.peril.client.ui.widgets.popup;

import com.forerunnergames.tools.common.Arguments;

public final class PopupStyle
{
  public static final String DEFAULT_WINDOW_STYLE_NAME = "dialog";
  public static final String DEFAULT_TEXT_BUTTON_STYLE_NAME = "default";
  public static final String DEFAULT_TITLE = "";
  public static final String DEFAULT_WINDOW_MESSAGE = "";
  public static final int DEFAULT_BORDER_THICKNESS = 0;
  public static final int DEFAULT_BUTTON_SPACING = 20;
  public static final int DEFAULT_BUTTON_TEXT_PADDING_LEFT = 0;
  public static final int DEFAULT_BUTTON_TEXT_PADDING_RIGHT = 0;
  public static final int DEFAULT_BUTTON_TEXT_PADDING_TOP = 0;
  public static final int DEFAULT_BUTTON_TEXT_PADDING_BOTTOM = 0;
  public static final int DEFAULT_TEXT_PADDING_LEFT = 0;
  public static final int DEFAULT_TEXT_PADDING_RIGHT = 0;
  public static final int DEFAULT_TEXT_PADDING_TOP = 0;
  public static final int DEFAULT_TEXT_PADDING_BOTTOM = 0;
  public static final int DEFAULT_TEXT_BOX_PADDING_LEFT = 0;
  public static final int DEFAULT_TEXT_BOX_PADDING_RIGHT = 0;
  public static final int DEFAULT_TEXT_BOX_PADDING_TOP = 0;
  public static final int DEFAULT_TEXT_BOX_PADDING_BOTTOM = 0;
  public static final boolean DEFAULT_IS_RESIZABLE = false;
  public static final boolean DEFAULT_IS_MOVABLE = false;
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
  private final boolean isResizable;
  private final boolean isMovable;
  private final boolean isDebug;

  public static PopupStyleBuilder builder ()
  {
    return new PopupStyleBuilder ();
  }

  public String getWindowStyleName ()
  {
    return windowStyleName;
  }

  public String getTextButtonStyleName ()
  {
    return textButtonStyleName;
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

  public boolean isResizable ()
  {
    return isResizable;
  }

  public boolean isMovable ()
  {
    return isMovable;
  }

  public boolean isDebug ()
  {
    return isDebug;
  }

  private PopupStyle (final String windowStyleName,
                      final String textButtonStyleName,
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
                      final boolean isResizable,
                      final boolean isMovable,
                      final boolean isDebug)
  {
    Arguments.checkIsNotNull (windowStyleName, "windowStyleName");
    Arguments.checkIsNotNull (textButtonStyleName, "textButtonStyleName");
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
    this.isResizable = isResizable;
    this.isMovable = isMovable;
    this.isDebug = isDebug;
  }

  public static final class PopupStyleBuilder
  {
    private String windowStyleName = DEFAULT_WINDOW_STYLE_NAME;
    private String textButtonStyleName = DEFAULT_TEXT_BUTTON_STYLE_NAME;
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
    private boolean isResizable = DEFAULT_IS_RESIZABLE;
    private boolean isMovable = DEFAULT_IS_MOVABLE;
    private boolean isDebug = DEFAULT_IS_DEBUG;

    public PopupStyle build ()
    {
      return new PopupStyle (windowStyleName, textButtonStyleName, title, titleHeight, buttonWidth, buttonHeight,
              message, positionUpperLeftReferenceScreenSpaceX, positionUpperLeftReferenceScreenSpaceY,
              widthReferenceScreenSpace, heightReferenceScreenSpace, borderThickness, buttonSpacing,
              buttonTextPaddingLeft, buttonTextPaddingRight, buttonTextPaddingTop, buttonTextPaddingBottom,
              textPaddingLeft, textPaddingRight, textPaddingTop, textPaddingBottom, textBoxPaddingLeft,
              textBoxPaddingRight, textBoxPaddingTop, textBoxPaddingBottom, isResizable, isMovable, isDebug);
    }

    public PopupStyleBuilder windowStyle (final String windowStyleName)
    {
      Arguments.checkIsNotNull (windowStyleName, "windowStyleName");

      this.windowStyleName = windowStyleName;

      return this;
    }

    public PopupStyleBuilder textButtonStyle (final String textButtonStyleName)
    {
      Arguments.checkIsNotNull (textButtonStyleName, "textButtonStyleName");

      this.textButtonStyleName = textButtonStyleName;

      return this;
    }

    public PopupStyleBuilder title (final String title)
    {
      Arguments.checkIsNotNull (title, "title");

      this.title = title;

      return this;
    }

    public PopupStyleBuilder titleHeight (final int titleHeight)
    {
      this.titleHeight = titleHeight;

      return this;
    }

    public PopupStyleBuilder buttonWidth (final int buttonWidth)
    {
      this.buttonWidth = buttonWidth;

      return this;
    }

    public PopupStyleBuilder buttonHeight (final int buttonHeight)
    {
      this.buttonHeight = buttonHeight;

      return this;
    }

    public PopupStyleBuilder buttonSize (final int buttonWidth, final int buttonHeight)
    {
      this.buttonWidth = buttonWidth;
      this.buttonHeight = buttonHeight;

      return this;
    }

    public PopupStyleBuilder message (final String message)
    {
      Arguments.checkIsNotNull (message, "message");

      this.message = message;

      return this;
    }

    public PopupStyleBuilder position (final int positionUpperLeftReferenceScreenSpaceX,
                                       final int positionUpperLeftReferenceScreenSpaceY)
    {
      this.positionUpperLeftReferenceScreenSpaceX = positionUpperLeftReferenceScreenSpaceX;
      this.positionUpperLeftReferenceScreenSpaceY = positionUpperLeftReferenceScreenSpaceY;

      return this;
    }

    public PopupStyleBuilder width (final int widthReferenceScreenSpace)
    {
      this.widthReferenceScreenSpace = widthReferenceScreenSpace;

      return this;
    }

    public PopupStyleBuilder height (final int heightReferenceScreenSpace)
    {
      this.heightReferenceScreenSpace = heightReferenceScreenSpace;

      return this;
    }

    public PopupStyleBuilder size (final int widthReferenceScreenSpace, final int heightReferenceScreenSpace)
    {
      return width (widthReferenceScreenSpace).height (heightReferenceScreenSpace);
    }

    public PopupStyleBuilder border (final int borderThickness)
    {
      Arguments.checkIsNotNegative (borderThickness, "borderThickness");

      this.borderThickness = borderThickness;

      return this;
    }

    public PopupStyleBuilder buttonSpacing (final int buttonSpacing)
    {
      Arguments.checkIsNotNegative (buttonSpacing, "buttonSpacing");

      this.buttonSpacing = buttonSpacing;

      return this;
    }

    public PopupStyleBuilder buttonTextPaddingLeft (final int buttonTextPaddingLeft)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingLeft, "buttonTextPaddingLeft");

      this.buttonTextPaddingLeft = buttonTextPaddingLeft;

      return this;
    }

    public PopupStyleBuilder buttonTextPaddingRight (final int buttonTextPaddingRight)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingRight, "buttonTextPaddingRight");

      this.buttonTextPaddingRight = buttonTextPaddingRight;

      return this;
    }

    public PopupStyleBuilder buttonTextPaddingTop (final int buttonTextPaddingTop)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingTop, "buttonTextPaddingTop");

      this.buttonTextPaddingTop = buttonTextPaddingTop;

      return this;
    }

    public PopupStyleBuilder buttonTextPaddingBottom (final int buttonTextPaddingBottom)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingBottom, "buttonTextPaddingBottom");

      this.buttonTextPaddingBottom = buttonTextPaddingBottom;

      return this;
    }

    public PopupStyleBuilder buttonTextPaddingHorizontal (final int buttonTextPaddingHorizontal)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingHorizontal, "buttonTextPaddingHorizontal");

      buttonTextPaddingLeft = buttonTextPaddingHorizontal;
      buttonTextPaddingRight = buttonTextPaddingHorizontal;

      return this;
    }

    public PopupStyleBuilder buttonTextPaddingVertical (final int buttonTextPaddingVertical)
    {
      Arguments.checkIsNotNegative (buttonTextPaddingVertical, "buttonTextPaddingVertical");

      buttonTextPaddingTop = buttonTextPaddingVertical;
      buttonTextPaddingBottom = buttonTextPaddingVertical;

      return this;
    }

    public PopupStyleBuilder buttonTextPadding (final int buttonTextPadding)
    {
      Arguments.checkIsNotNegative (buttonTextPadding, "buttonTextPadding");

      buttonTextPaddingLeft = buttonTextPadding;
      buttonTextPaddingRight = buttonTextPadding;
      buttonTextPaddingTop = buttonTextPadding;
      buttonTextPaddingBottom = buttonTextPadding;

      return this;
    }

    public PopupStyleBuilder textPaddingLeft (final int textPaddingLeft)
    {
      Arguments.checkIsNotNegative (textPaddingLeft, "textPaddingLeft");

      this.textPaddingLeft = textPaddingLeft;

      return this;
    }

    public PopupStyleBuilder textPaddingRight (final int textPaddingRight)
    {
      Arguments.checkIsNotNegative (textPaddingRight, "textPaddingRight");

      this.textPaddingRight = textPaddingRight;

      return this;
    }

    public PopupStyleBuilder textPaddingTop (final int textPaddingTop)
    {
      Arguments.checkIsNotNegative (textPaddingTop, "textPaddingTop");

      this.textPaddingTop = textPaddingTop;

      return this;
    }

    public PopupStyleBuilder textPaddingBottom (final int textPaddingBottom)
    {
      Arguments.checkIsNotNegative (textPaddingBottom, "textPaddingBottom");

      this.textPaddingBottom = textPaddingBottom;

      return this;
    }

    public PopupStyleBuilder textPaddingHorizontal (final int textPaddingHorizontal)
    {
      Arguments.checkIsNotNegative (textPaddingHorizontal, "textPaddingHorizontal");

      textPaddingLeft = textPaddingHorizontal;
      textPaddingRight = textPaddingHorizontal;

      return this;
    }

    public PopupStyleBuilder textPaddingVertical (final int textPaddingVertical)
    {
      Arguments.checkIsNotNegative (textPaddingVertical, "textPaddingVertical");

      textPaddingTop = textPaddingVertical;
      textPaddingBottom = textPaddingVertical;

      return this;
    }

    public PopupStyleBuilder textPadding (final int textPadding)
    {
      Arguments.checkIsNotNegative (textPadding, "textPadding");

      textPaddingLeft = textPadding;
      textPaddingRight = textPadding;
      textPaddingTop = textPadding;
      textPaddingBottom = textPadding;

      return this;
    }

    public PopupStyleBuilder textBoxPaddingRight (final int textBoxPaddingRight)
    {
      Arguments.checkIsNotNegative (textBoxPaddingRight, "textBoxPaddingRight");

      this.textBoxPaddingRight = textBoxPaddingRight;

      return this;
    }

    public PopupStyleBuilder textBoxPaddingTop (final int textBoxPaddingTop)
    {
      Arguments.checkIsNotNegative (textBoxPaddingTop, "textBoxPaddingTop");

      this.textBoxPaddingTop = textBoxPaddingTop;

      return this;
    }

    public PopupStyleBuilder textBoxPaddingBottom (final int textBoxPaddingBottom)
    {
      Arguments.checkIsNotNegative (textBoxPaddingBottom, "textBoxPaddingBottom");

      this.textBoxPaddingBottom = textBoxPaddingBottom;

      return this;
    }

    public PopupStyleBuilder textBoxPaddingHorizontal (final int textBoxPaddingHorizontal)
    {
      Arguments.checkIsNotNegative (textBoxPaddingHorizontal, "textBoxPaddingHorizontal");

      textBoxPaddingLeft = textBoxPaddingHorizontal;
      textBoxPaddingRight = textBoxPaddingHorizontal;

      return this;
    }

    public PopupStyleBuilder textBoxPaddingVertical (final int textBoxPaddingVertical)
    {
      Arguments.checkIsNotNegative (textBoxPaddingVertical, "textBoxPaddingVertical");

      textBoxPaddingTop = textBoxPaddingVertical;
      textBoxPaddingBottom = textBoxPaddingVertical;

      return this;
    }

    public PopupStyleBuilder textBoxPadding (final int textBoxPadding)
    {
      Arguments.checkIsNotNegative (textBoxPadding, "textBoxPadding");

      textBoxPaddingLeft = textBoxPadding;
      textBoxPaddingRight = textBoxPadding;
      textBoxPaddingTop = textBoxPadding;
      textBoxPaddingBottom = textBoxPadding;

      return this;
    }

    public PopupStyleBuilder resizable (final boolean isResizable)
    {
      this.isResizable = isResizable;

      return this;
    }

    public PopupStyleBuilder movable (final boolean isMovable)
    {
      this.isMovable = isMovable;

      return this;
    }

    public PopupStyleBuilder debug ()
    {
      isDebug = true;

      return this;
    }
  }
}
