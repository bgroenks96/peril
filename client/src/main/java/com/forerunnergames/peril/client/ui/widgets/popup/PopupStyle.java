package com.forerunnergames.peril.client.ui.widgets.popup;

import com.forerunnergames.tools.common.Arguments;

public final class PopupStyle
{
  public static final String DEFAULT_WINDOW_STYLE_NAME = "dialog";
  public static final String DEFAULT_TEXT_BUTTON_STYLE_NAME = "default";
  public static final String DEFAULT_TITLE = "";
  public static final String DEFAULT_WINDOW_MESSAGE = "";
  public static final int DEFAULT_BORDER_THICKNESS_PIXELS = 12;
  public static final boolean DEFAULT_IS_RESIZABLE = false;
  public static final boolean DEFAULT_IS_MOVABLE = false;
  public static final float AUTO_H_CENTER = -1;
  public static final float AUTO_V_CENTER = -1;
  public static final float AUTO_WIDTH = -1;
  public static final float AUTO_HEIGHT = -1;
  public static final float DEFAULT_POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE_X = AUTO_H_CENTER;
  public static final float DEFAULT_POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE_Y = AUTO_V_CENTER;
  public static final float DEFAULT_WIDTH_REFERENCE_SCREEN_SPACE = AUTO_WIDTH;
  public static final float DEFAULT_HEIGHT_REFERENCE_SCREEN_SPACE = AUTO_HEIGHT;
  public static final float DEFAULT_TITLE_HEIGHT = AUTO_HEIGHT;
  private final String windowStyleName;
  private final String textButtonStyleName;
  private final String title;
  private final float titleHeight;
  private final String message;
  private final float positionUpperLeftReferenceScreenSpaceX;
  private final float positionUpperLeftReferenceScreenSpaceY;
  private final float widthReferenceScreenSpace;
  private final float heightReferenceScreenSpace;
  private final int borderThicknessPixels;
  private final boolean isResizable;
  private final boolean isMovable;

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

  public final String getTitle ()
  {
    return title;
  }

  public final float getTitleHeight ()
  {
    return titleHeight;
  }

  public final String getMessage ()
  {
    return message;
  }

  public float getPositionUpperLeftReferenceScreenSpaceX ()
  {
    return positionUpperLeftReferenceScreenSpaceX;
  }

  public float getPositionUpperLeftReferenceScreenSpaceY ()
  {
    return positionUpperLeftReferenceScreenSpaceY;
  }

  public float getWidthReferenceScreenSpace ()
  {
    return widthReferenceScreenSpace;
  }

  public float getHeightReferenceScreenSpace ()
  {
    return heightReferenceScreenSpace;
  }

  public int getBorderThicknessPixels ()
  {
    return borderThicknessPixels;
  }

  public boolean isResizable ()
  {
    return isResizable;
  }

  public boolean isMovable ()
  {
    return isMovable;
  }

  private PopupStyle (final String windowStyleName,
                      final String textButtonStyleName,
                      final String title,
                      final float titleHeight,
                      final String message,
                      final float positionUpperLeftReferenceScreenSpaceX,
                      final float positionUpperLeftReferenceScreenSpaceY,
                      final float widthReferenceScreenSpace,
                      final float heightReferenceScreenSpace,
                      final int borderThicknessPixels,
                      final boolean isResizable,
                      final boolean isMovable)
  {
    Arguments.checkIsNotNull (windowStyleName, "windowStyleName");
    Arguments.checkIsNotNull (textButtonStyleName, "textButtonStyleName");
    Arguments.checkIsNotNull (title, "title");
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNegative (borderThicknessPixels, "borderThicknessPixels");

    this.windowStyleName = windowStyleName;
    this.textButtonStyleName = textButtonStyleName;
    this.title = title;
    this.titleHeight = titleHeight;
    this.message = message;
    this.positionUpperLeftReferenceScreenSpaceX = positionUpperLeftReferenceScreenSpaceX;
    this.positionUpperLeftReferenceScreenSpaceY = positionUpperLeftReferenceScreenSpaceY;
    this.widthReferenceScreenSpace = widthReferenceScreenSpace;
    this.heightReferenceScreenSpace = heightReferenceScreenSpace;
    this.borderThicknessPixels = borderThicknessPixels;
    this.isResizable = isResizable;
    this.isMovable = isMovable;
  }

  public static final class PopupStyleBuilder
  {
    private String windowStyleName = DEFAULT_WINDOW_STYLE_NAME;
    private String textButtonStyleName = DEFAULT_TEXT_BUTTON_STYLE_NAME;
    private String title = DEFAULT_TITLE;
    private float titleHeight = DEFAULT_TITLE_HEIGHT;
    private String message = DEFAULT_WINDOW_MESSAGE;
    private float positionUpperLeftReferenceScreenSpaceX = DEFAULT_POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE_X;
    private float positionUpperLeftReferenceScreenSpaceY = DEFAULT_POSITION_UPPER_LEFT_REFERENCE_SCREEN_SPACE_Y;
    private float widthReferenceScreenSpace = DEFAULT_WIDTH_REFERENCE_SCREEN_SPACE;
    private float heightReferenceScreenSpace = DEFAULT_HEIGHT_REFERENCE_SCREEN_SPACE;
    private int borderThicknessPixels = DEFAULT_BORDER_THICKNESS_PIXELS;
    private boolean isResizable = DEFAULT_IS_RESIZABLE;
    private boolean isMovable = DEFAULT_IS_MOVABLE;

    public PopupStyle build ()
    {
      return new PopupStyle (windowStyleName, textButtonStyleName, title, titleHeight, message,
              positionUpperLeftReferenceScreenSpaceX, positionUpperLeftReferenceScreenSpaceY,
              widthReferenceScreenSpace, heightReferenceScreenSpace, borderThicknessPixels, isResizable, isMovable);
    }

    public PopupStyleBuilder windowStyle (final String windowStyleName)
    {
      Arguments.checkIsNotNull (windowStyleName, "windowStyleName");

      this.windowStyleName = windowStyleName;

      return this;
    }

    public PopupStyleBuilder textButtonStyle (final String textuButtonStyleName)
    {
      Arguments.checkIsNotNull (textuButtonStyleName, "textuButtonStyleName");

      textButtonStyleName = textuButtonStyleName;

      return this;
    }

    public PopupStyleBuilder title (final String title)
    {
      Arguments.checkIsNotNull (title, "title");

      this.title = title;

      return this;
    }

    public PopupStyleBuilder titleHeight (final float titleHeight)
    {
      this.titleHeight = titleHeight;

      return this;
    }

    public PopupStyleBuilder message (final String message)
    {
      Arguments.checkIsNotNull (message, "message");

      this.message = message;

      return this;
    }

    public PopupStyleBuilder position (final float positionUpperLeftReferenceScreenSpaceX,
                                       final float positionUpperLeftReferenceScreenSpaceY)
    {
      this.positionUpperLeftReferenceScreenSpaceX = positionUpperLeftReferenceScreenSpaceX;
      this.positionUpperLeftReferenceScreenSpaceY = positionUpperLeftReferenceScreenSpaceY;

      return this;
    }

    public PopupStyleBuilder width (final float widthReferenceScreenSpace)
    {
      this.widthReferenceScreenSpace = widthReferenceScreenSpace;

      return this;
    }

    public PopupStyleBuilder height (final float heightReferenceScreenSpace)
    {
      this.heightReferenceScreenSpace = heightReferenceScreenSpace;

      return this;
    }

    public PopupStyleBuilder size (final float widthReferenceScreenSpace, final float heightReferenceScreenSpace)
    {
      return width (widthReferenceScreenSpace).height (heightReferenceScreenSpace);
    }

    public PopupStyleBuilder border (final int borderThicknessPixels)
    {
      Arguments.checkIsNotNegative (borderThicknessPixels, "borderThicknessPixels");

      this.borderThicknessPixels = borderThicknessPixels;

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
  }
}
