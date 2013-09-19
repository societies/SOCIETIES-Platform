package org.societies.webapp.wrappers;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

import java.util.ArrayList;
import java.util.List;

public class AccessControlResponseItemWrapper extends AccessControlResponseItem {

    public static void unwrapList(List<AccessControlResponseItem> listToUnwrap) {
        List<AccessControlResponseItem> responseItems = new ArrayList<AccessControlResponseItem>();
        for (AccessControlResponseItem item : listToUnwrap) {
            if (item instanceof AccessControlResponseItemWrapper) {
                AccessControlResponseItemWrapper wrapper = (AccessControlResponseItemWrapper) item;
                responseItems.add(wrapper.getResponseItem());
            } else {
                // no unwrapping to do
                responseItems.add(item);
            }
        }

        listToUnwrap.clear();
        listToUnwrap.addAll(responseItems);
    }

    public static void wrapList(List<AccessControlResponseItem> originalList) {
        List<AccessControlResponseItem> responseItemWrappers = new ArrayList<AccessControlResponseItem>();
        for (AccessControlResponseItem item : originalList) {
            if (item instanceof AccessControlResponseItemWrapper) {
                // no wrapping to do
                responseItemWrappers.add(item);
            } else {
                responseItemWrappers.add(new AccessControlResponseItemWrapper(item));
            }
        }

        originalList.clear();
        originalList.addAll(responseItemWrappers);
    }

    public AccessControlResponseItemWrapper(ResponseItem prototype) {
        this.responseItemId = prototype.getResponseItemId();
        this.requestItem = prototype.getRequestItem();
        this.decision = prototype.getDecision();

    }

    public RequestItemWrapper getRequestItemWrapper() {
        return (RequestItemWrapper) super.getRequestItem();
    }

    public boolean isPermitted() {
        return this.decision == Decision.PERMIT;
    }

    public void setPermitted(boolean permitted) {
        this.decision = (permitted ? Decision.PERMIT : Decision.DENY);
    }

    public AccessControlResponseItem getResponseItem() {
        AccessControlResponseItem original = new AccessControlResponseItem();

        original.setResponseItemId(this.responseItemId);
        original.setRequestItem(this.requestItem);
        original.setDecision(this.decision);
        original.setObfuscationLevel(this.obfuscationLevel);

        return original;
    }

}
