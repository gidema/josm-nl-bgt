package org.openstreetmap.josm.plugins.netex;

import javax.swing.JMenu;

import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.util.GuiHelper;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.netex.actions.CheckRouteAction;
import org.openstreetmap.josm.plugins.netex.actions.ImportIfoptTagsAction;
import org.openstreetmap.josm.plugins.netex.util.NetexUtils;

public class NetexPlugin extends Plugin {
    @SuppressWarnings("unused")
    private boolean debugMode;

    private static NetexPlugin instance;

    private final JMenu menu;

    /* Data / NeTeX menu */
    /** Data / NeTeX / Check Route **/
    public final CheckRouteAction checkRouteAction = new CheckRouteAction();

    /** Data / NeTeX / Import Ifopt **/
    public final ImportIfoptTagsAction importIfoptAction = new ImportIfoptTagsAction();
    
    public NetexPlugin(PluginInformation info) {
        super(info);
        debugMode = Preferences.main().getBoolean("josm.debug", false);
        if (instance == null) {
            instance = this;
        } else {
            throw new IllegalStateException("Cannot instantiate plugin twice !");
        }
        menu = MainApplication.getMenu().dataMenu;

        new Thread(() -> {
            // Add menu in EDT
            GuiHelper.runInEDT(this::buildMenu);
        }).start();
        
        NetexUtils.deletePreviousTempDirs();

    }
    
    private void buildMenu() {
        JMenu netexMenu = new JMenu("NeTeX");
        netexMenu.add(importIfoptAction);
        netexMenu.add(checkRouteAction);
        menu.add(netexMenu);
       // TODO add icon
        //        moduleMenu.setIcon(icon);
     }

}