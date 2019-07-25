package org.lisaac.ldt.preferences;


import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;
import org.lisaac.ldt.LisaacPlugin;


public class LisaacTemplatePage extends TemplatePreferencePage {
  public LisaacTemplatePage() {
	  setPreferenceStore(LisaacPlugin.getDefault().getPreferenceStore());
    setTemplateStore(LisaacPlugin.getDefault().getTemplateStore());
    setContextTypeRegistry(LisaacPlugin.getDefault().getContextTypeRegistry());
  }
  
  protected boolean isShowFormatterSetting() {
      return true;
  }

  public boolean performOk() {
      boolean ok = super.performOk();

      LisaacPlugin.getDefault().savePluginPreferences();
      return ok;
  }
}
