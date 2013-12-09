package org.societies.webapp.wrappers;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

import java.util.ArrayList;
import java.util.List;

public class ResponseItemWrapper extends ResponseItem {

    public static void unwrapList(List<ResponseItem> listToUnwrap) {
        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
        for (ResponseItem item : listToUnwrap) {
            if (item instanceof ResponseItemWrapper) {
                ResponseItemWrapper wrapper = (ResponseItemWrapper) item;
                responseItems.add(wrapper.getResponseItem());
            } else {
                // no unwrapping to do
                responseItems.add(item);
            }
        }

        listToUnwrap.clear();
        listToUnwrap.addAll(responseItems);
    }

    public static void wrapList(List<ResponseItem> originalList) {
        List<ResponseItem> responseItemWrappers = new ArrayList<ResponseItem>();
        for (ResponseItem item : originalList) {
            if (item instanceof ResponseItemWrapper) {
                // no wrapping to do
                responseItemWrappers.add(item);
            } else {
                responseItemWrappers.add(new ResponseItemWrapper(item));
            }
        }

        originalList.clear();
        originalList.addAll(responseItemWrappers);
    }

    public ResponseItemWrapper(ResponseItem prototype) {
        this.responseItemId = prototype.getResponseItemId();
        this.requestItem = prototype.getRequestItem();
        this.decision = prototype.getDecision();

    }

 //   public RequestItemWrapper getRequestItemWrapper() {
   //     return (RequestItemWrapper) super.getRequestItem();
    //}

    public boolean isPermitted() {
        return this.decision == Decision.PERMIT;
    }

    public void setPermitted(boolean permitted) {
        this.decision = (permitted ? Decision.PERMIT : Decision.DENY);
    }

    public ResponseItem getResponseItem() {
        ResponseItem original = new ResponseItem();

        original.setResponseItemId(this.responseItemId);
        original.setRequestItem(this.requestItem);
        original.setDecision(this.decision);

        return original;
    }

}
