package org.societies.webapp.wrappers;

import org.societies.api.internal.privacytrust.privacy.model.dataobfuscation.ObfuscatorInfo;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;

import java.util.ArrayList;
import java.util.List;

public class RequestItemWrapper extends RequestItem {
    private List<Action> originalActions;
    private List<String> selectedActionNames;
    private ObfuscatorInfo obfuscatorInfo;

    public RequestItemWrapper(RequestItem prototype) {
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

    public List<Action> getSelectedActions() {
        List<Action> selectedActions = new ArrayList<Action>();

        for (Action action : this.originalActions) {
            for (String name : selectedActionNames) {
                if (action.getActionConstant().name().equals(name)) {
                    selectedActions.add(action);
                    break;
                }
            }
        }

        return selectedActions;
    }

    public List<Action> getOriginalActions() {
        return originalActions;
    }

    public ObfuscatorInfo getObfuscatorInfo() {
        return obfuscatorInfo;
    }

    public void setObfuscatorInfo(ObfuscatorInfo obfuscatorInfo) {
        this.obfuscatorInfo = obfuscatorInfo;
    }

    public RequestItem getRequestItem() {
        RequestItem item = new RequestItem();

        item.setActions(this.getActions());
        item.setConditions(this.getConditions());
        item.setResource(this.getResource());
        item.setOptional(this.isOptional());

        return item;
    }

}
