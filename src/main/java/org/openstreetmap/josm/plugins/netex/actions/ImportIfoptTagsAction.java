
// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.netex.actions;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.csv.CSVFormat;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.PrimitiveId;
import org.openstreetmap.josm.data.osm.SimplePrimitiveId;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.gui.widgets.SwingFileChooser;
import org.openstreetmap.josm.io.MultiFetchServerObjectReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.tools.I18n;

public class ImportIfoptTagsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static String[] HEADERS = { "node_id", "osm_primitive_type", "key", "value"};
    private static CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader(HEADERS)
            .setSkipHeaderRecord(true)
            .build();

    public ImportIfoptTagsAction() {
        super(I18n.tr("Download Ifopt"));
//            putValue(Action.NAME, name);
        putValue("toolbar", ("opendata_download_netex"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var filter = new FileNameExtensionFilter("CSV file (*.csv)", "csv");
        var fileChooser = new SwingFileChooser(null);
        fileChooser.setFileSelectionMode(0);
        fileChooser.setFileFilter(filter);
        fileChooser.showOpenDialog(MainApplication.getMainPanel());
        var file = fileChooser.getSelectedFile();
        List<RefIfopt> ifoptList = new ArrayList<>();
        if (file != null) {
            try (var in = new FileReader(file);
                 var parser = csvFormat.parse(in);) {
                parser.forEach(record -> {
                    var nodeId = Long.valueOf(record.get("node_id"));
                    var key = record.get("key");
                    if ("ref:IFOPT".equals(key)) {
                        var primitiveType = record.get("osm_primitive_type");
                        OsmPrimitiveType osmPrimitiveType = switch(primitiveType) {
                        case "N": yield OsmPrimitiveType.NODE;
                        case "W": yield OsmPrimitiveType.WAY;
                        default: throw new IllegalArgumentException("Unexpected value: " + primitiveType);
                        };
                        var primitiveId = new SimplePrimitiveId(nodeId, osmPrimitiveType);
                        var value = record.get("value");
                        ifoptList.add(new RefIfopt(primitiveId, value));
                    }
                });
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        process(ifoptList);
     }
    
    private void process(List<RefIfopt> items) {
        var objectReader = new ObjectReader();
        items.forEach(item -> {
            objectReader.append(item.primitiveId());
        });
        var activeLayer = MainApplication.getLayerManager().getActiveDataLayer();
        if (activeLayer == null) {
            activeLayer = new OsmDataLayer(new DataSet(), "Data layer 1", null);
            MainApplication.getLayerManager().addLayer(activeLayer);
        }
        var activeDataSet = activeLayer.getDataSet();
        try {
            var dataSet = objectReader.parseOsm(NullProgressMonitor.INSTANCE);
            activeDataSet.mergeFrom(dataSet);
            List<Command> commands = new ArrayList<>();
            items.forEach(item -> {
                var primitive = activeDataSet.getPrimitiveById(item.primitiveId());
                if (primitive.get("ref:IFOPT") == null) {
                    commands.add(new ChangePropertyCommand(primitive, "ref:IFOPT", item.quayCode));
                }
            });
            if (!commands.isEmpty()) {
                var command = new SequenceCommand("Update ref:IFOPT tags", commands);
                UndoRedoHandler.getInstance().add(command);
            }
        } catch (OsmTransferException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    record RefIfopt (PrimitiveId primitiveId, String quayCode) {}
    
    class ObjectReader extends MultiFetchServerObjectReader {
        ObjectReader() {
            super();
        }
    }
}
