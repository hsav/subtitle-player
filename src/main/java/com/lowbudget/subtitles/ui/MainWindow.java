/*
 * MIT License
 *
 * Copyright (c) 2023 Charalampos Savvidis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.lowbudget.subtitles.ui;

import com.lowbudget.subtitles.model.Player;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

@Slf4j
public class MainWindow extends JFrame {

  private final TranslucentPanel translucentPane;

  private boolean windowShown = false;

  private final Player player; // NOSONAR

  public MainWindow(Player player, Actions actions) {

    this.translucentPane = new TranslucentPanel(player, actions);

    this.player = player;

    setPreferredSize(new Dimension(600, 200));

    setUndecorated(true);
    setAlwaysOnTop(true);
    setBackground(UIConstants.COLOR_TRANSPARENT);
    setContentPane(translucentPane);

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // create our menu bar
    setJMenuBar(createMenuBar(actions));

    // register our window with global native mouse listener
    addWindowListener(new NativeHookRegisterWindowListener(this));

    turnOnDecoration();
  }

  /*package */ void turnOffDecoration() {
    if (!windowShown) {
      return;
    }

    log.debug("Turning off decoration");
    setBackground(UIConstants.COLOR_TRANSPARENT);
    getRootPane().setWindowDecorationStyle(JRootPane.NONE);

    this.translucentPane.setViewMode(true);
    getJMenuBar().setVisible(false);
    windowShown = false;
  }

  /*package */ void turnOnDecoration() {
    if (windowShown) {
      return;
    }
    log.debug("Turning on decoration");
    setBackground(UIConstants.WHITE_SEMI_TRANSPARENT);
    getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

    this.translucentPane.setViewMode(false);
    getJMenuBar().setVisible(true);

    windowShown = true;
  }

  private static JMenuBar createMenuBar(Actions actions) {
    JMenuBar menuBar = new JMenuBar();

    JMenu menu = new JMenu("File");
    addMenuItem(menu, actions.getLoadAction());
    addMenuItem(menu, actions.getSettingsAction());

    menuBar.add(menu);
    return menuBar;
  }

  private static void addMenuItem(JMenu menu, Action action) {
    JMenuItem loadItem = new JMenuItem(action);
    menu.add(loadItem);
  }


  @RequiredArgsConstructor
  @Slf4j
  private static class NativeHookRegisterWindowListener extends WindowAdapter {

    private final MainWindow window;

    @Override
    public void windowOpened(WindowEvent e) {
      // Initialize native hook.

      // Get the logger for "org.jnativehook" and set the level to off.
      Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
      logger.setLevel(Level.OFF);

      // Don't forget to disable the parent handlers.
      logger.setUseParentHandlers(false);

      try {
        // Set the event dispatcher to a swing safe executor service.
        GlobalScreen.setEventDispatcher(new SwingDispatchService());

        log.info("Registering native mouse listener hooks");
        GlobalScreen.registerNativeHook();
      } catch (NativeHookException ex) {
        log.error("Unexpected error", ex);
        System.exit(1);
      }

      NativeHookGlobalMouseListener mouseListener = new NativeHookGlobalMouseListener(window);
      GlobalScreen.addNativeMouseListener(mouseListener);
      GlobalScreen.addNativeMouseMotionListener(mouseListener);
    }

    @Override
    public void windowClosed(WindowEvent e) {
      // Clean up the native hook.
      try {
        log.info("Unregistering native mouse listener hooks");
        GlobalScreen.unregisterNativeHook();
        this.window.player.close();
      } catch (NativeHookException ex) {
        log.error("Unexpected error", ex);
      }
    }
  }

  /**
   * Global mouse listener that facilitates user interaction with our translucent window frame.
   *
   * <p>This class is responsible for two features:
   * <li>to allow detecting when the mouse is moving over our translucent window, so we can turn the
   *     window decoration on and off
   * <li>to keep track of the state of the left mouse button. When the button is pressed we do not
   *     hide the decoration if the mouse is moving outside the window to allow the user to graceful
   *     resize it.
   */
  @RequiredArgsConstructor
  private static class NativeHookGlobalMouseListener implements NativeMouseInputListener {

    /** the main window for which we turn decoration on and off */
    private final MainWindow window;

    /** Keeps track of the left mouse button state */
    private boolean leftButtonPressed = false;

    public void nativeMouseMoved(NativeMouseEvent e) {
      // System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY()); //NOSONAR

      Point p = e.getPoint();
      SwingUtilities.convertPointFromScreen(p, window);

      if (window.contains(p)) {
        // mouse is moving inside window
        window.turnOnDecoration();
      } else {
        // outside window - turn off decoration again if the left button is not pressed

        // Note: we assume when left button is pressed user is resizing the window
        // we need this otherwise as the user tries to resize and gets out of the original
        // bounds the decoration will be turned off
        if (!this.leftButtonPressed) {
          window.turnOffDecoration();
        }
      }
    }

    public void nativeMouseDragged(NativeMouseEvent e) {
      // System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY()); //NOSONAR
    }

    public void nativeMouseClicked(NativeMouseEvent e) {
      // System.out.println("Mouse Clicked: " + e.getClickCount()); //NOSONAR
    }

    public void nativeMousePressed(NativeMouseEvent e) {
      // System.out.println("Mouse Pressed: " + e.getButton()); //NOSONAR
      this.leftButtonPressed = e.getButton() == NativeMouseEvent.BUTTON1;
    }

    public void nativeMouseReleased(NativeMouseEvent e) {
      // System.out.println("Mouse Released: " + e.getButton()); //NOSONAR
      if (e.getButton() == NativeMouseEvent.BUTTON1) {
        this.leftButtonPressed = false;
      }
    }
  }
}
