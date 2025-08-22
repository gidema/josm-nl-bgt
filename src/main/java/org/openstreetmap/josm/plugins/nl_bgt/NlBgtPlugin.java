package org.openstreetmap.josm.plugins.nl_bgt;

import javax.swing.JMenu;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.util.GuiHelper;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.nl_bgt.gui.BgtDownloadAction;

public class NlBgtPlugin extends Plugin {
    @SuppressWarnings("unused")
    private boolean debugMode;

    private final JMenu menu;

    public NlBgtPlugin(PluginInformation info) {
        super(info);
        menu = MainApplication.getMenu().dataMenu;

        new Thread(() -> {
            // Add menu in EDT
            GuiHelper.runInEDT(this::buildMenu);
        }).start();
    }
    
    private void buildMenu() {
        JMenu netexMenu = new JMenu("NL BGT");
        netexMenu.add(new BgtDownloadAction());
        menu.add(netexMenu);
    }
}