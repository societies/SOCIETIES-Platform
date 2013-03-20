package org.societies.webapp.wrappers;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;

import java.util.ArrayList;
import java.util.List;

public class RequestItemWrapper extends RequestItem {
    private List<Action> originalActions;
    private List<String> selectedActionNames;
    private RequestItem prototype;

    public RequestItemWrapper(RequestItem prototype) {
        this.prototype = prototype;
        this.originalActions = new ArrayList<Action>(prototype.getActions());
        this.actions = prototype.getActions();
        this.conditions = prototype.getConditions();
        this.resource = prototype.getResource();
        this.optional = prototype.isOptional();

        this.selectedActionNames = new ArrayList<String>();
        for (Action action : originalActions) {
            selectedActionNames.add(action.getActionConstant().name());
        }
    }

    public List<String> getSelectedActionNames() {
        return selectedActionNames;
    }

    public void setSelectedActionNames(List<String> selectedActionNames) {
        this.selectedActionNames = selectedActionNames;
    }

    public List<Action> getOriginalActions() {
        return originalActions;
    }

    public RequestItem getRequestItem() {
        return prototype;
    }
}
