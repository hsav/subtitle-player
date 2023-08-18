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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.lowbudget.subtitles.model.Settings;

import lombok.Getter;
import lombok.Setter;
import say.swing.JFontChooser;

public class SettingsDialog extends JDialog {

  @Getter @Setter private boolean cancelled = false;

  // this setting is not modifiable through settings, we just need to retain its value when creating
  // a new Settings object
  private final String lastOpenFolder;

  private JSpinner opacitySpinner;
  private final JFontChooser fc = new JFontChooser();
  private JButton openFontDialogButton;

  public SettingsDialog(Settings settings) {
    this.lastOpenFolder = settings.getLastOpenFolder();
    setupFromSettings(settings);

    // create a top panel with the settings properties and a bottom panel for the buttons
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

    JPanel settingsPanel =
        createSettingsPanel(
            new Component[] {
              new JLabel("Subtitle opacity"), new JLabel("Font properties"),
            },
            new Component[] {this.opacitySpinner, openFontDialogButton});

    mainPanel.add(settingsPanel);
    JPanel buttonsPanel = createButtonsPanel();
    mainPanel.add(buttonsPanel);

    getContentPane().add(mainPanel);

    setTitle("Settings");
    setModal(true);
    pack();
    setLocationRelativeTo(null);
  }

  private void setupFromSettings(Settings settings) {
    // set up the font related properties at the font dialog
    fc.setSelectedFontFamily(settings.getFontName());
    fc.setSelectedFontStyle(settings.getFontStyle());
    fc.setSelectedFontSize(settings.getFontSize());

    // setup subtitle opacity at the spinner
    this.opacitySpinner = new JSpinner(new SpinnerNumberModel(settings.getOpacity(), 0, 255, 1));

    // create a "..." button that would open the font dialog when clicked
    this.openFontDialogButton =
        new JButton(
            new AbstractAction("...") {
              @Override
              public void actionPerformed(ActionEvent e) {
                fc.setSelectedFontFamily(settings.getFontName());
                fc.setSelectedFontStyle(settings.getFontStyle());
                fc.setSelectedFontSize(settings.getFontSize());
                fc.showDialog(null);
              }
            });
  }

  public Settings getNewSettings() {
    return new Settings(
        fc.getSelectedFontFamily(),
        fc.getSelectedFontStyle(),
        fc.getSelectedFontSize(),
        (int) this.opacitySpinner.getValue(),
        this.lastOpenFolder);
  }

  private JPanel createButtonsPanel() {
    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    okButton.addActionListener(new ButtonActionListener());

    cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    cancelButton.addActionListener(new ButtonActionListener(true));
    return buttonPanel;
  }

  /**
   * General purpose method to create a JPanel with relatively sensible defaults and achieving a
   * settings-like look-n-feel
   */
  private JPanel createSettingsPanel(Component[] labels, Component[] components) {
    if (labels.length != components.length) {
      throw new IllegalStateException("Labels and component arrays need to have the same length");
    }

    JPanel topPanel = new JPanel();
    GroupLayout layout = new GroupLayout(topPanel);
    topPanel.setLayout(layout);

    // Turn on automatically adding gaps between components
    layout.setAutoCreateGaps(true);

    // Turn on automatically creating gaps between components that touch
    // the edge of the container and the container.
    layout.setAutoCreateContainerGaps(true);

    GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

    GroupLayout.ParallelGroup labelsGroup = layout.createParallelGroup();

    GroupLayout.ParallelGroup componentsGroup = layout.createParallelGroup();

    // for each label - component pair add them to the corresponding group
    for (int i = 0; i < labels.length; i++) {
      labelsGroup.addComponent(labels[i]);
      componentsGroup.addComponent(components[i]);
    }
    hGroup.addGroup(labelsGroup);
    hGroup.addGroup(componentsGroup);
    layout.setHorizontalGroup(hGroup);

    GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

    for (int i = 0; i < components.length; i++) {
      Component label = labels[i];
      Component component = components[i];
      vGroup.addGroup(
          layout
              .createParallelGroup(GroupLayout.Alignment.CENTER)
              .addComponent(label)
              .addComponent(component));
    }
    layout.setVerticalGroup(vGroup);
    return topPanel;
  }

  private class ButtonActionListener implements ActionListener {

    final boolean isCancelListener;

    ButtonActionListener() {
      this(false);
    }

    ButtonActionListener(boolean isCancelListener) {
      this.isCancelListener = isCancelListener;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
      if (isCancelListener) {
        // set the cancelled flag on the dialog
        setCancelled(true);
      }
      setVisible(false);
      dispose();
    }
  }
}
