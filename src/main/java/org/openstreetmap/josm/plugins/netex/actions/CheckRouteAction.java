package org.openstreetmap.josm.plugins.netex.actions;

import static org.openstreetmap.josm.data.osm.OsmPrimitiveType.RELATION;

import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.netex.RouteMatcher;
import org.openstreetmap.josm.plugins.netex.model.OsmPtRoute;
import org.openstreetmap.josm.tools.I18n;

public class CheckRouteAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    
    private static Set<String> ptRoutes = Set.of("bus", "train", "tram");

    public CheckRouteAction() {
        super(I18n.tr("Check route"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getSelectedBusRoute().ifPresentOrElse(osmRoute -> {
            var routeMatcher = new RouteMatcher(osmRoute);
            routeMatcher.run();
            JOptionPane pane = new JOptionPane(routeMatcher.getIssueReport());
            pane.setOptions(new Object[]{}); // Removes all buttons
            var dialog = pane.createDialog(MainApplication.getMainFrame(), I18n.tr("Route status")); // Create dialog with pane
            dialog.setModal(false); // IMPORTANT! Now the thread isn't blocked
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    dialog.setVisible(true);
                }
            });
//            JOptionPane.showMessageDialog(MainApplication.getMainFrame(), routeMatcher.getIssueReport());
        },
        () -> {
            JOptionPane.showMessageDialog(MainApplication.getMainFrame(), I18n.tr("Please select a single public transport relation."));
        });
    }
    
    private static Optional<OsmPtRoute> getSelectedBusRoute() {
        var layer = MainApplication.getLayerManager().getActiveDataLayer();
        if (layer == null) return Optional.empty();
        var selection = layer.getDataSet().getAllSelected();
        if (selection == null || selection.size() != 1) {
            return Optional.empty();
        }
        return layer.getDataSet().getAllSelected()
             .stream().filter(p -> p.getType() == RELATION)
             .findFirst()
             .map(p -> (Relation)p)
             .filter(r -> ("route".equals(r.get("type")) && ptRoutes.contains(r.get("route"))))
             .map(OsmPtRoute::parse);
    }
}
