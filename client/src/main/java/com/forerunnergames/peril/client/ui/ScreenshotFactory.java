package com.forerunnergames.peril.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;

import com.forerunnergames.tools.common.Exceptions;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Most of the screenshot code was borrowed from the LibGDX wiki. Full credit goes to them.
 */
public class ScreenshotFactory
{
  private static final Logger log = LoggerFactory.getLogger (ScreenshotFactory.class);
  private static final String DEFAULT_SCREENSHOTS_DIR = System.getProperty ("user.home") + File.separator + "peril"
          + File.separator + "screenshots";
  private static final ScreenshotInputProcessor inputProcessor = new ScreenshotInputProcessor ();
  private static final ExecutorService fileIOExecutor = Executors.newSingleThreadExecutor ();
  private static volatile int counter = 1;

  static
  {
    try
    {
      final Path filePath = Paths.get (DEFAULT_SCREENSHOTS_DIR);
      if (!Files.exists (filePath)) Files.createDirectory (filePath);
    }
    catch (final IOException e)
    {
      log.error ("Error creating peril 'screenshots' directory: ", e);
    }
  }

  public static InputProcessor input ()
  {
    return inputProcessor;
  }

  public static void saveScreenshot ()
  {
    try
    {
      final Pixmap pixmap = getScreenshot (0, 0, Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
      fileIOExecutor.execute (new ScreenshotIO (pixmap));
    }
    catch (final Exception e)
    {
      log.error ("Error creating screenshot.", e);
    }
  }

  private static Pixmap getScreenshot (final int x, final int y, final int w, final int h)
  {
    final Pixmap pixmap = new Pixmap (w, h, Pixmap.Format.RGBA8888);
    final ByteBuffer pixelData = pixmap.getPixels ();
    Gdx.gl.glPixelStorei (GL20.GL_PACK_ALIGNMENT, 1);
    final int frameBufferStatus = Gdx.gl.glCheckFramebufferStatus (GL20.GL_FRAMEBUFFER);
    if (frameBufferStatus != GL20.GL_FRAMEBUFFER_COMPLETE)
    {
      log.warn ("Framebuffer may be missing or incomplete: status={}", frameBufferStatus);
    }

    final IntBuffer chkBuff = BufferUtils.newIntBuffer (16);
    Gdx.gl.glGetIntegerv (GL30.GL_DRAW_FRAMEBUFFER_BINDING, chkBuff);
    Gdx.gl.glGetIntegerv (GL30.GL_READ_FRAMEBUFFER_BINDING, chkBuff);
    chkBuff.rewind ();
    log.debug ("Draw framebuffer: {} | Read framebuffer: {}", chkBuff.get (), chkBuff.get ());

    Gdx.gl.glDisable (GL20.GL_DEPTH_TEST);
    Gdx.gl.glReadPixels (0, 0, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixelData);
    final int errChk = Gdx.gl.glGetError ();
    if (errChk != GL20.GL_NO_ERROR) Exceptions.throwRuntime ("Error in glReadPixels: {}", errChk);

    final int numBytes = w * h * 4;
    final byte[] lines = new byte [numBytes];
    final int numBytesPerLine = w * 4;
    for (int i = 0; i < h; i++)
    {
      pixelData.position ((h - i - 1) * numBytesPerLine);
      pixelData.get (lines, i * numBytesPerLine, numBytesPerLine);
    }
    pixelData.clear ();
    pixelData.put (lines);
    pixelData.flip ();
    return pixmap;
  }

  private static class ScreenshotIO implements Runnable
  {
    private final Pixmap pixmap;

    ScreenshotIO (final Pixmap pixmap)
    {
      this.pixmap = pixmap;
    }

    @Override
    public void run ()
    {
      FileHandle fh;
      do
      {
        fh = new FileHandle (DEFAULT_SCREENSHOTS_DIR + File.separator + "screenshot" + counter++ + ".png");
      }
      while (fh.exists ());

      PixmapIO.writePNG (fh, pixmap);
      if (fh.exists ()) log.info ("Successfully saved screenshot to {}", fh.path ());
    }
  }

  private static class ScreenshotInputProcessor extends InputAdapter
  {
    @Override
    public boolean keyDown (final int keycode)
    {
      if (keycode == Input.Keys.F12)
      {
        log.trace ("Screenshot input received [keycode={}].", keycode);
        saveScreenshot ();
        return true;
      }
      return false;
    }
  }
}
