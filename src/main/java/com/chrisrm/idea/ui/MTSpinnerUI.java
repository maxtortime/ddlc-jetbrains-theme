/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Chris Magnussen and Elior Boukhobza
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
 *
 *
 */
package com.chrisrm.idea.ui;

import com.intellij.ide.ui.laf.darcula.ui.DarculaSpinnerUI;
import com.intellij.openapi.ui.GraphicsConfig;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.GraphicsUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author Konstantin Bulenkov
 */
public final class MTSpinnerUI extends DarculaSpinnerUI {
  private final FocusAdapter myFocusListener = new FocusAdapter() {
    @Override
    public void focusGained(final FocusEvent e) {
      spinner.repaint();
    }

    @Override
    public void focusLost(final FocusEvent e) {
      spinner.repaint();
    }
  };

  public static ComponentUI createUI(final JComponent c) {
    return new MTSpinnerUI();
  }

  @Override
  public void paint(final Graphics g, final JComponent c) {
    super.paint(g, c);
    final Border border = spinner.getBorder();
    if (border != null) {
      border.paintBorder(c, g, 0, 0, spinner.getWidth(), spinner.getHeight());
    }
  }

  @Override
  protected void uninstallListeners() {
    super.uninstallListeners();
    removeEditorFocusListener(spinner.getEditor());
  }

  @Override
  protected JButton createButton(@MagicConstant(intValues = {SwingConstants.NORTH,
      SwingConstants.SOUTH}) final int direction,
                                 final String name) {
    final JButton button = createArrow(direction);
    button.setName(name);
    button.setBorder(JBUI.Borders.empty(1));
    if (direction == SwingConstants.NORTH) {
      installNextButtonListeners(button);
    } else {
      installPreviousButtonListeners(button);
    }
    return button;
  }


  @Override
  protected void paintArrowButton(final Graphics g,
                                  final BasicArrowButton button,
                                  @MagicConstant(intValues = {SwingConstants.NORTH,
                                      SwingConstants.SOUTH}) final int direction) {
    final int y = direction == SwingConstants.NORTH ? button.getHeight() - 6 : 2;
    button.paintTriangle(g, (button.getWidth() - 8) / 2 - 1, y, 0, direction, spinner.isEnabled());
  }

  private void removeEditorFocusListener(final JComponent editor) {
    if (editor != null) {
      editor.getComponents()[0].removeFocusListener(myFocusListener);
    }
  }

  private JButton createArrow(@MagicConstant(intValues = {SwingConstants.NORTH,
      SwingConstants.SOUTH}) final int direction) {
    final Color shadow = UIUtil.getPanelBackground();
    // TODO: 6/17/18 this should probably be a property
    final Color enabledColor = new JBColor(Gray._255, UIUtil.getLabelForeground());
    final Color disabledColor = new JBColor(Gray._200, UIUtil.getLabelForeground().darker());
    final BasicArrowButton b = new BasicArrowButton(direction, shadow, shadow, enabledColor, shadow) {
      @Override
      public void paint(final Graphics g) {
        paintArrowButton(g, this, direction);
      }

      @Override
      public boolean isOpaque() {
        return false;
      }

      @Override
      public void paintTriangle(final Graphics g, final int x, final int y, final int size, final int direction, final boolean isEnabled) {
        final GraphicsConfig config = GraphicsUtil.setupAAPainting(g);
        final int mid;
        final int w = 8;
        final int h = 6;
        mid = w / 2;

        g.setColor(isEnabled ? enabledColor : disabledColor);

        g.translate(x, y);
        switch (direction) {
          case SOUTH:
            g.fillPolygon(new int[]{0,
                w,
                mid}, new int[]{1,
                1,
                h}, 3);
            break;
          case NORTH:
            g.fillPolygon(new int[]{0,
                w,
                mid}, new int[]{h - 1,
                h - 1,
                0}, 3);
            break;
          case WEST:
          case EAST:
          default:
        }
        g.translate(-x, -y);
        config.restore();
      }
    };
    final Border buttonBorder = UIManager.getBorder("Spinner.arrowButtonBorder");
    if (buttonBorder instanceof UIResource) {
      // Wrap the border to avoid having the UIResource be replaced by
      // the ButtonUI. This is the opposite of using BorderUIResource.
      b.setBorder(new CompoundBorder(buttonBorder, null));
    } else {
      b.setBorder(buttonBorder);
    }
    b.setInheritsPopupMenu(true);
    return b;
  }
}
