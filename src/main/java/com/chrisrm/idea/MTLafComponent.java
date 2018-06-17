/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2018 Chris Magnussen and Elior Boukhobza
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package com.chrisrm.idea;

import com.chrisrm.idea.config.BeforeConfigNotifier;
import com.chrisrm.idea.config.ConfigNotifier;
import com.chrisrm.idea.config.ui.MTForm;
import com.chrisrm.idea.icons.tinted.TintedIconsService;
import com.chrisrm.idea.messages.MaterialThemeBundle;
import com.chrisrm.idea.ui.*;
import com.chrisrm.idea.utils.MTUiUtils;
import com.chrisrm.idea.utils.UIReplacer;
import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.laf.IntelliJTableSelectedCellHighlightBorder;
import com.intellij.ide.ui.laf.darcula.DarculaTableHeaderBorder;
import com.intellij.ide.ui.laf.darcula.DarculaTableHeaderUI;
import com.intellij.ide.ui.laf.darcula.DarculaTableSelectedCellHighlightBorder;
import com.intellij.ide.ui.laf.darcula.ui.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.*;

/**
 * Component for working on the Material Look And Feel
 */
public final class MTLafComponent extends JBPanel implements ApplicationComponent {

  private boolean willRestartIde = false;
  private MessageBusConnection connect;

  public MTLafComponent(final LafManager lafManager) {
    lafManager.addLafManagerListener(source -> installMaterialComponents());
  }

  /**
   * Install Material Design components
   */
  private void installMaterialComponents() {
    final MTConfig mtConfig = MTConfig.getInstance();

    installDefaults();

    if (mtConfig.getIsMaterialDesign()) {
      replaceButtons();
      replaceTextFields();
      replaceDropdowns();
      replaceProgressBar();
      replaceTree();
      replaceTableHeaders();
      replaceTables();
      replaceStatusBar();
      replaceSpinners();
      replaceCheckboxes();
      replaceRadioButtons();
      replaceSliders();
      replaceTextAreas();
      replaceTabbedPanes();
      replaceIcons();
      modifyRegistry();
    }
  }

  private void modifyRegistry() {
    Registry.get("ide.balloon.shadow.size").setValue(0);
  }

  private void installDefaults() {
    UIManager.put("Caret.width", 2);
    UIManager.put("Border.width", 2);
    UIManager.put("Button.arc", 6);
    UIManager.put("Component.arc", 0);

    UIManager.put("Menu.maxGutterIconWidth", 18);
    UIManager.put("MenuItem.maxGutterIconWidth", 18);
    UIManager.put("MenuItem.acceleratorDelimiter", "-");
    UIManager.put("MenuItem.border", new DarculaMenuItemBorder());
    UIManager.put("Menu.border", new DarculaMenuItemBorder());
    UIManager.put("TextArea.caretBlinkRate", 500);
    UIManager.put("Table.cellNoFocusBorder", JBUI.insets(10, 2, 10, 2));
    UIManager.put("CheckBoxMenuItem.borderPainted", false);
    UIManager.put("RadioButtonMenuItem.borderPainted", false);
    UIManager.put("ComboBox.squareButton", true);
    UIManager.put("ComboBox.padding", JBUI.insets(1, 5, 1, 5));
    UIManager.put("CheckBox.border.width", 3);
    UIManager.put("RadioButton.border.width", 3);

    UIManager.put("HelpTooltip.verticalGap", 4);
    UIManager.put("HelpTooltip.horizontalGap", 10);
    UIManager.put("HelpTooltip.maxWidth", 250);
    UIManager.put("HelpTooltip.xOffset", 1);
    UIManager.put("HelpTooltip.yOffset", 1);

    UIManager.put("HelpTooltip.defaultTextBorder", JBUI.insets(10, 10, 10, 16));
    UIManager.put("HelpTooltip.fontSizeDelta", 0);
    UIManager.put("HelpTooltip.smallTextBorder", JBUI.insets(4, 8, 5, 8));

    UIManager.put("Spinner.arrowButtonInsets", JBUI.insets(1, 1, 1, 1));
    UIManager.put("Spinner.editorBorderPainted", false);
    UIManager.put("ToolWindow.tab.verticalPadding", 5);
    UIManager.put("ScrollBarUI", JBScrollBar.class.getName());
    UIManager.put(JBScrollBar.class.getName(), JBScrollBar.class);

    UIManager.put("Focus.activeErrorBorderColor", new ColorUIResource(0xE53935));
    UIManager.put("Focus.inactiveErrorBorderColor", new ColorUIResource(0x743A3A));
    UIManager.put("Focus.activeWarningBorderColor", new ColorUIResource(0xFFB62C));
    UIManager.put("Focus.inactiveWarningBorderColor", new ColorUIResource(0x7F6C00));

    if (MTConfig.getInstance().getSelectedTheme().getThemeIsDark()) {
      installDarculaDefaults();
    } else {
      installLightDefaults();
    }
  }

  /**
   * Replace buttons
   */
  private void replaceButtons() {
    UIManager.put("ButtonUI", MTButtonUI.class.getName());
    UIManager.getDefaults().put(MTButtonUI.class.getName(), MTButtonUI.class);

    UIManager.put("Button.border", new MTButtonPainter());

    UIManager.put("OptionButtonUI", MTOptionButtonUI.class.getName());
    UIManager.getDefaults().put(MTOptionButtonUI.class.getName(), MTOptionButtonUI.class);

    UIManager.put("OnOffButtonUI", MTOnOffButtonUI.class.getName());
    UIManager.put(MTOnOffButtonUI.class.getName(), MTOnOffButtonUI.class);
  }

  /**
   * Replace text fields
   */
  private void replaceTextFields() {
    UIManager.put("TextFieldUI", MTTextFieldUI.class.getName());
    UIManager.getDefaults().put(MTTextFieldUI.class.getName(), MTTextFieldUI.class);

    UIManager.put("PasswordFieldUI", MTPasswordFieldUI.class.getName());
    UIManager.getDefaults().put(MTPasswordFieldUI.class.getName(), MTPasswordFieldUI.class);

    UIManager.put("TextField.border", new MTTextBorder());
    UIManager.put("PasswordField.border", new MTTextBorder());
  }

  private void replaceDropdowns() {
    UIManager.put("ComboBoxUI", MTComboBoxUI.class.getName());
    UIManager.getDefaults().put(MTComboBoxUI.class.getName(), MTComboBoxUI.class);
  }

  /**
   * Replace progress bar
   */
  private void replaceProgressBar() {
    UIManager.put("ProgressBarUI", MTProgressBarUI.class.getName());
    UIManager.getDefaults().put(MTProgressBarUI.class.getName(), MTProgressBarUI.class);

    UIManager.put("ProgressBar.border", new MTProgressBarBorder());
  }

  /**
   * Replace trees
   */
  private void replaceTree() {
    UIManager.put("TreeUI", MTTreeUI.class.getName());
    UIManager.getDefaults().put(MTTreeUI.class.getName(), MTTreeUI.class);

    UIManager.put("List.sourceListSelectionBackgroundPainter", new MTSelectedTreePainter());
    UIManager.put("List.sourceListFocusedSelectionBackgroundPainter", new MTSelectedTreePainter());
  }

  /**
   * Replace Table headers
   */
  private void replaceTableHeaders() {
    UIManager.put("TableHeaderUI", MTTableHeaderUI.class.getName());
    UIManager.getDefaults().put(MTTableHeaderUI.class.getName(), MTTableHeaderUI.class);

    UIManager.put("TableHeader.border", new MTTableHeaderBorder());
    UIManager.put("Table.focusSelectedCellHighlightBorder", new MTTableSelectedCellHighlightBorder());
  }

  private void replaceTables() {
    UIManager.put("TableHeader.cellBorder", new MTTableHeaderBorder());
    UIManager.put("Table.cellNoFocusBorder", new MTTableCellNoFocusBorder());
    UIManager.put("Table.focusCellHighlightBorder", new MTTableSelectedCellHighlightBorder());
  }

  private void replaceStatusBar() {
    final MessageBusConnection connect = ApplicationManager.getApplication().getMessageBus().connect();

    // On app init, set the statusbar borders
    connect.subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
      @Override
      public void projectOpened(@Nullable final Project projectFromCommandLine) {
        MTThemeManager.getInstance().setStatusBarBorders();
      }
    });

    // And also on config change
    connect.subscribe(ConfigNotifier.CONFIG_TOPIC, mtConfig -> MTThemeManager.getInstance().setStatusBarBorders());
  }

  private void replaceSpinners() {
    UIManager.put("SpinnerUI", MTSpinnerUI.class.getName());
    UIManager.getDefaults().put(MTSpinnerUI.class.getName(), MTSpinnerUI.class);

    UIManager.put("Spinner.border", new MTSpinnerBorder());
  }

  private void replaceCheckboxes() {
    UIManager.put("CheckBoxUI", MTCheckBoxUI.class.getName());
    UIManager.getDefaults().put(MTCheckBoxUI.class.getName(), MTCheckBoxUI.class);

    UIManager.put("CheckBoxMenuItemUI", MTCheckBoxMenuItemUI.class.getName());
    UIManager.getDefaults().put(MTCheckBoxMenuItemUI.class.getName(), MTCheckBoxMenuItemUI.class);

    UIManager.put("CheckBox.border", new MTCheckBoxBorder());
  }

  private void replaceRadioButtons() {
    UIManager.put("RadioButtonUI", MTRadioButtonUI.class.getName());
    UIManager.getDefaults().put(MTRadioButtonUI.class.getName(), MTRadioButtonUI.class);

    UIManager.put("RadioButtonMenuItemUI", MTRadioButtonMenuItemUI.class.getName());
    UIManager.getDefaults().put(MTRadioButtonMenuItemUI.class.getName(), MTRadioButtonMenuItemUI.class);
  }

  private void replaceSliders() {
    UIManager.put("SliderUI", MTSliderUI.class.getName());
    UIManager.getDefaults().put(MTSliderUI.class.getName(), MTSliderUI.class);
  }

  private void replaceTextAreas() {
    UIManager.put("TextAreaUI", MTTextAreaUI.class.getName());
    UIManager.getDefaults().put(MTTextAreaUI.class.getName(), MTTextAreaUI.class);
  }

  private void replaceTabbedPanes() {
    UIManager.put("TabbedPane.tabInsets", JBUI.insets(5, 10, 5, 10));
    UIManager.put("TabbedPane.contentBorderInsets", JBUI.insets(3, 1, 1, 1));

    UIManager.put("TabbedPaneUI", MTTabbedPaneUI.class.getName());
    UIManager.getDefaults().put(MTTabbedPaneUI.class.getName(), MTTabbedPaneUI.class);
  }

  private void replaceIcons() {
    final Icon collapsedIcon = getIcon(MTConfig.getInstance().getArrowsStyle().getCollapsedIcon());
    final Icon expandedIcon = getIcon(MTConfig.getInstance().getArrowsStyle().getExpandedIcon());

    UIManager.put("Tree.collapsedIcon", collapsedIcon);
    UIManager.put("Tree.expandedIcon", expandedIcon);
    UIManager.put("Menu.arrowIcon", collapsedIcon);
    //    UIManager.put("MenuItem.arrowIcon", collapsedIcon);
    UIManager.put("RadioButtonMenuItem.arrowIcon", collapsedIcon);
    UIManager.put("CheckBoxMenuItem.arrowIcon", collapsedIcon);

    UIManager.put("FileView.fileIcon", AllIcons.FileTypes.Unknown);
    UIManager.put("Table.ascendingSortIcon", AllIcons.General.SplitUp);
    UIManager.put("Table.descendingSortIcon", AllIcons.General.SplitDown);

    UIManager.put("TextField.darcula.searchWithHistory.icon", IconLoader.getIcon("/icons/darcula/searchWithHistory.png"));
    UIManager.put("TextField.darcula.search.icon", IconLoader.getIcon("/icons/darcula/search.png"));
    UIManager.put("TextField.darcula.clear.icon", IconLoader.getIcon("/icons/darcula/clear.png"));
  }

  private void installDarculaDefaults() {
    UIManager.put("EditorPaneUI", DarculaEditorPaneUI.class.getName());
    UIManager.put("TableHeaderUI", DarculaTableHeaderUI.class.getName());
    UIManager.put("Table.focusSelectedCellHighlightBorder", new DarculaTableSelectedCellHighlightBorder());
    UIManager.put("TableHeader.cellBorder", new DarculaTableHeaderBorder());

    UIManager.put("CheckBoxMenuItemUI", DarculaCheckBoxMenuItemUI.class.getName());
    UIManager.put("RadioButtonMenuItemUI", DarculaRadioButtonMenuItemUI.class.getName());
    UIManager.put("TabbedPaneUI", DarculaTabbedPaneUI.class.getName());

    UIManager.put("TextFieldUI", DarculaTextFieldUI.class.getName());
    UIManager.put("TextField.border", new DarculaTextBorder());

    UIManager.put("PasswordFieldUI", DarculaPasswordFieldUI.class.getName());
    UIManager.put("PasswordField.border", new DarculaTextBorder());
    UIManager.put("ProgressBarUI", DarculaProgressBarUI.class.getName());
    UIManager.put("ProgressBar.border", new DarculaProgressBarBorder());
    UIManager.put("FormattedTextFieldUI", DarculaTextFieldUI.class.getName());
    UIManager.put("FormattedTextField.border", new DarculaTextBorder());

    UIManager.put("TextAreaUI", DarculaTextAreaUI.class.getName());
    UIManager.put("CheckBoxUI", DarculaCheckBoxUI.class.getName());

    UIManager.put("CheckBox.border", new DarculaCheckBoxBorder());
    UIManager.put("ComboBoxUI", DarculaComboBoxUI.class.getName());
    UIManager.put("RadioButtonUI", DarculaRadioButtonUI.class.getName());
    UIManager.put("RadioButton.border", new DarculaCheckBoxBorder());

    UIManager.put("Button.border", new DarculaButtonPainter());
    UIManager.put("ButtonUI", DarculaButtonUI.class.getName());

    UIManager.put("ToggleButton.border", new DarculaButtonPainter());
    UIManager.put("ToggleButtonUI", DarculaButtonUI.class.getName());

    UIManager.put("SpinnerUI", MTSpinnerUI.class.getName());
    UIManager.put("Spinner.border", new MTSpinnerBorder());

    UIManager.put("TreeUI", DarculaTreeUI.class.getName());
    UIManager.put("OptionButtonUI", DarculaOptionButtonUI.class.getName());
  }

  private void installLightDefaults() {
    UIManager.put("EditorPaneUI", DarculaEditorPaneUI.class.getName());
    UIManager.put("TableHeaderUI", DarculaTableHeaderUI.class.getName());
    UIManager.put("Table.focusSelectedCellHighlightBorder", new IntelliJTableSelectedCellHighlightBorder());
    UIManager.put("TableHeader.cellBorder", new DarculaTableHeaderBorder());

    UIManager.put("CheckBoxMenuItemUI", DarculaCheckBoxMenuItemUI.class.getName());
    UIManager.put("RadioButtonMenuItemUI", DarculaRadioButtonMenuItemUI.class.getName());
    UIManager.put("TabbedPaneUI", DarculaTabbedPaneUI.class.getName());

    UIManager.put("TextFieldUI", DarculaTextFieldUI.class.getName());
    UIManager.put("TextField.border", new DarculaTextBorder());

    UIManager.put("PasswordFieldUI", DarculaPasswordFieldUI.class.getName());
    UIManager.put("PasswordField.border", new DarculaTextBorder());
    UIManager.put("ProgressBarUI", DarculaProgressBarUI.class.getName());
    UIManager.put("ProgressBar.border", new DarculaProgressBarBorder());
    UIManager.put("FormattedTextFieldUI", DarculaTextFieldUI.class.getName());
    UIManager.put("FormattedTextField.border", new DarculaTextBorder());

    UIManager.put("TextAreaUI", DarculaTextAreaUI.class.getName());
    UIManager.put("Tree.paintLines", false);

    UIManager.put("CheckBoxUI", DarculaCheckBoxUI.class.getName());
    UIManager.put("CheckBox.border", new DarculaCheckBoxBorder());
    UIManager.put("ComboBoxUI", DarculaComboBoxUI.class.getName());
    UIManager.put("RadioButtonUI", DarculaRadioButtonUI.class.getName());
    UIManager.put("RadioButton.border", new DarculaCheckBoxBorder());

    UIManager.put("Button.border", new DarculaButtonPainter());
    UIManager.put("ButtonUI", DarculaButtonUI.class.getName());

    UIManager.put("ToggleButton.border", new DarculaButtonPainter());
    UIManager.put("ToggleButtonUI", DarculaButtonUI.class.getName());

    UIManager.put("SpinnerUI", MTSpinnerUI.class.getName());
    UIManager.put("Spinner.border", new MTSpinnerBorder());

    UIManager.put("TreeUI", DarculaTreeUI.class.getName());
    UIManager.put("OptionButtonUI", DarculaOptionButtonUI.class.getName());
    UIManager.put("InternalFrameUI", DarculaInternalFrameUI.class.getName());
    UIManager.put("RootPaneUI", DarculaRootPaneUI.class.getName());
  }

  private Icon getIcon(final String icon) {
    if (icon == null) {
      return IconLoader.getTransparentIcon(AllIcons.Mac.Tree_white_down_arrow, 0);
    }
    return TintedIconsService.getIcon(icon + ".png");
  }

  @Override
  public void initComponent() {
    installMaterialComponents();

    // Patch UI components
    UIReplacer.patchUI();

    // Listen for changes on the settings
    connect = ApplicationManager.getApplication().getMessageBus().connect();
    connect.subscribe(ConfigNotifier.CONFIG_TOPIC, this::onSettingsChanged);
    connect.subscribe(BeforeConfigNotifier.BEFORE_CONFIG_TOPIC, (this::onBeforeSettingsChanged));
  }

  @Override
  public void disposeComponent() {
    connect.disconnect();
  }

  @NotNull
  @Override
  public String getComponentName() {
    return getClass().getName();
  }

  /**
   * Called when MT Config settings are changeds
   *
   * @param mtConfig
   * @param form
   */
  private void onBeforeSettingsChanged(final MTConfig mtConfig, final MTForm form) {
    // Force restart if material design is switched
    restartIdeIfNecessary(mtConfig, form);
  }

  /**
   * Restart IDE if necessary (ex: material design components)
   *
   * @param mtConfig
   * @param form
   */
  private void restartIdeIfNecessary(final MTConfig mtConfig, final MTForm form) {
    // Restart the IDE if changed
    if (mtConfig.needsRestart(form)) {
      final String title = MaterialThemeBundle.message("mt.restartDialog.title");
      final String message = MaterialThemeBundle.message("mt.restartDialog.content");

      final int answer = Messages.showYesNoDialog(message, title, Messages.getQuestionIcon());
      if (answer == Messages.YES) {
        willRestartIde = true;
      }
    }
  }

  /**
   * Called when MT Config settings are changeds
   *
   * @param mtConfig
   */
  private void onSettingsChanged(final MTConfig mtConfig) {
    MTThemeManager.getInstance().updateFileIcons();
    MTTreeUI.resetIcons();
    MTSelectedTreePainter.resetCache();

    if (willRestartIde) {
      MTUiUtils.restartIde();
    }
  }
}
