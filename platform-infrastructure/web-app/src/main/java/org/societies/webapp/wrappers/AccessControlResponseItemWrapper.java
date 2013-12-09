package org.societies.webapp.wrappers;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

import java.util.ArrayList;
import java.util.List;

public class AccessControlResponseItemWrapper extends AccessControlResponseItem {
	
	private boolean isSelected;
	private int continuousSliderValue;
	private int discreteSliderValue;
	
	public int getContinuousSliderValue()
	{
		return this.continuousSliderValue;
	}
	
	public void setcontinuousSliderValue(int continuousSliderValue)
	{
		this.continuousSliderValue = continuousSliderValue;
		this.obfuscationLevel=((double) this.continuousSliderValue/(double)100);
	}
	
	public int getdiscreteSliderValue()
	{
		return this.discreteSliderValue;
	}
	
	public void setDiscreteSliderValue(int discreteSliderValue)
	{
		this.discreteSliderValue = discreteSliderValue;
		this.obfuscationLevel=((double)this.discreteSliderValue/((double)this.getRequestItemWrapper().getObfuscatorInfo().getNbOfObfuscationLevelStep()-1));
	
	}

	
	public boolean getIsSelected()
	{
		return this.isSelected;
	}
	
	public void setIsSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}
	

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
        original.setRemember(this.remember);
        original.setRequestorscope(this.requestorscope);

        return original;
    }

}
