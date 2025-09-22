package org.openstreetmap.josm.plugins.nl_bgt.gui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.nl_bgt.io.MultiFeatureDownloader;
import org.openstreetmap.josm.plugins.nl_bgt.jts.Boundary;
import org.openstreetmap.josm.tools.ImageProvider;
import org.xml.sax.SAXException;

public class BgtDownloadAction extends AbstractAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private boolean cancelled = false;
    private Boundary boundary;
    private final SlippyMapDownloadDialog slippyDialog;

    private final MultiFeatureDownloader downloader = new MultiFeatureDownloader();

    public BgtDownloadAction() {
        super("Download", ImageProvider.get("download"));
        slippyDialog = new SlippyMapDownloadDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        run();
    }

    public void run() {
        cancelled = false;
        boundary = getBoundary();
        if (!cancelled) {
            DownloadTask task = new DownloadTask();
            MainApplication.worker.submit(task);
        }
    }

    private Boundary getBoundary() {
        var dialog = slippyDialog;
        dialog.restoreSettings();
        dialog.setVisible(true);
        if (dialog.isCanceled()) {
            cancelled = true;
            return null;
        }
        dialog.rememberSettings();
//        downloadOsm = dialog.cbDownloadOSM.isSelected();
//        downloadOpenData = dialog.cbDownloadODS.isSelected();
        return new Boundary(dialog.getSelectedDownloadArea());
    }

    private class DownloadTask extends PleaseWaitRunnable {

        public DownloadTask() {
            super(tr("Downloading data"));
        }

        @Override
        protected void cancel() {
//            downloader.cancel();
        }

        @Override
        protected void realRun() throws SAXException, IOException,
        OsmTransferException {
            downloader.run(boundary);
        }

        @Override
        protected void finish() {
//            MainApplication.getLayerManager().setActiveLayer(getContext().getComponent(OdLayerManager.class).getOsmDataLayer());
        }
    }
}
