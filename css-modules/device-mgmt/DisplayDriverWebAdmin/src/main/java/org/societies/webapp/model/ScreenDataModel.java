package org.societies.webapp.model;

import org.primefaces.model.SelectableDataModel;
import org.societies.webapp.model.Screens;

import javax.faces.model.ListDataModel;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sww2
 * Date: 10/07/13
 * Time: 12:02
 * To change this template use File | Settings | File Templates.
 */

public class ScreenDataModel extends ListDataModel<Screens> implements SelectableDataModel<Screens> {

    public ScreenDataModel(List<Screens> data) {
        super(data);
    }

    @Override
    public Object getRowKey(Screens screens) {
        return screens.getScreenID();
    }

    @Override
    public Screens getRowData(String rowKey) {
        List<Screens> screens = (List<Screens>) getWrappedData();

        for(Screens screen : screens) {
            if(screen.getScreenID().equals(rowKey))
                return screen;
        }

        return null;
    }

}
