package org.societies.personalisation.preference.api.model.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.schema.personalisation.model.ContextPreferenceConditionBean;
import org.societies.api.internal.schema.personalisation.model.OperatorConstantsBean;
import org.societies.api.internal.schema.personalisation.model.PreferenceDetailsBean;
import org.societies.api.internal.schema.personalisation.model.PreferenceTreeModelBean;
import org.societies.api.internal.schema.personalisation.model.PreferenceTreeNodeBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.personalisation.model.ActionBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;

public class PreferenceUtils {

	public static PreferenceDetails toPreferenceDetails(PreferenceDetailsBean bean){
		PreferenceDetails details = new PreferenceDetails();
		details.setPreferenceName(bean.getPreferenceName());
		details.setServiceID(bean.getServiceID());
		details.setServiceType(bean.getServiceType());
		return details;
	}

	public static PreferenceDetailsBean toPreferenceDetailsBean(PreferenceDetails details){
		PreferenceDetailsBean bean = new PreferenceDetailsBean();
		bean.setPreferenceName(details.getPreferenceName());
		bean.setServiceID(details.getServiceID());
		bean.setServiceType(details.getServiceType());
		return bean;
	}

	public static ContextPreferenceConditionBean toContextPreferenceConditionBean(IPreferenceCondition condition){
		ContextPreferenceConditionBean bean = new ContextPreferenceConditionBean();

		if (condition.getCtxIdentifier()!=null){
			bean.setCtxIdentifier(CtxModelBeanTranslator.getInstance().fromCtxIdentifier(condition.getCtxIdentifier()));	
		}
		bean.setName(condition.getname());
		bean.setOperator(toOperatorConstantsBean(condition.getoperator()));
		bean.setType(condition.getType());
		bean.setValue(condition.getvalue());

		return bean;

	}

	public static ContextPreferenceCondition toContextPreferenceCondition(ContextPreferenceConditionBean bean){
		if (bean==null){
			return null;
		}
		
		if (bean.getCtxIdentifier()==null){
			return new ContextPreferenceCondition(
					null, 
					toOperatorConstants(bean.getOperator()), 
					bean.getValue(), 
					bean.getName());
		}
		try {
			return new ContextPreferenceCondition(
					(CtxAttributeIdentifier) CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(bean.getCtxIdentifier()), 
					toOperatorConstants(bean.getOperator()), 
					bean.getValue(), 
					bean.getName());
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static PreferenceTreeNode toPreferenceTreeNode(PreferenceTreeNodeBean bean){
		if (bean.getCondition()!=null){
			PreferenceTreeNode node = new PreferenceTreeNode(toContextPreferenceCondition(bean.getCondition()));
			if (bean.getChildren()!=null){
				List<PreferenceTreeNodeBean> children = bean.getChildren();
				for (PreferenceTreeNodeBean child : children){
					node.add(toPreferenceTreeNode(child));
				}
			}
			return node;
		}else if (bean.getOutcome()!=null){
			return new PreferenceTreeNode(toPreferenceOutcome(bean.getOutcome()));
		}

		PreferenceTreeNode node = new PreferenceTreeNode();
		if (bean.getChildren()!=null){
			List<PreferenceTreeNodeBean> children = bean.getChildren();
			for (PreferenceTreeNodeBean child : children){
				node.add(toPreferenceTreeNode(child));
			}
		}

		return node;
	}


	public static PreferenceTreeNodeBean toPreferenceTreeNodeBean(IPreference node){
		PreferenceTreeNodeBean bean = new PreferenceTreeNodeBean();
		if (node.getOutcome()!=null){
			bean.setOutcome(toActionBean(node.getOutcome()));
			return bean;
		}


		if (node.getCondition()!=null){
			bean.setCondition(toContextPreferenceConditionBean(node.getCondition()));
		}

		Enumeration<IPreference> children = node.children();
		ArrayList<IPreference> preferencesBelow = new ArrayList<IPreference>();
		while(children.hasMoreElements()){
			preferencesBelow.add(children.nextElement());
		}
		
		return bean;

	}

	public static PreferenceTreeModel toPreferenceTreeModel(PreferenceTreeModelBean bean){
		PreferenceTreeNode preference = toPreferenceTreeNode(bean.getPreference());
		PreferenceDetails details = toPreferenceDetails(bean.getPreferenceDetails());
		return new PreferenceTreeModel(details, preference);
	}
	
	public static PreferenceTreeModelBean toPreferenceTreeModelBean(IPreferenceTreeModel model){
		PreferenceTreeModelBean bean = new PreferenceTreeModelBean();
		bean.setPreference(toPreferenceTreeNodeBean(model.getRootPreference()));
		bean.setPreferenceDetails(toPreferenceDetailsBean(model.getPreferenceDetails()));
		return bean;
	}
	public static Action toAction(ActionBean bean){
		return new Action(bean.getServiceID(), bean.getServiceType(), bean.getParameterName(), bean.getValue(), bean.isImplementable(), bean.isContextDependent(), bean.isProactive());

	}

	public static PreferenceOutcome toPreferenceOutcome(ActionBean bean){
		return new PreferenceOutcome(bean.getServiceID(), bean.getServiceType(), bean.getParameterName(), bean.getValue(), bean.isImplementable(), bean.isContextDependent(), bean.isProactive()); 
	}


	public static ActionBean toActionBean(IAction action){
		ActionBean bean = new ActionBean();
		bean.setContextDependent(action.isContextDependent());
		bean.setImplementable(action.isImplementable());
		bean.setProactive(action.isProactive());
		bean.setParameterName(action.getparameterName());
		bean.setServiceID(action.getServiceID());
		bean.setServiceType(action.getServiceType());
		bean.setValue(action.getvalue());

		return bean;
	}
	
	
	public static OperatorConstantsBean toOperatorConstantsBean(OperatorConstants operator){
		switch (operator) {
		case EQUALS : return OperatorConstantsBean.EQUALS;

		case GREATER_OR_EQUAL_THAN : return OperatorConstantsBean.GREATER_OR_EQUAL_THAN;

		case GREATER_THAN : return OperatorConstantsBean.GREATER_THAN;

		case LESS_OR_EQUAL_THAN : return OperatorConstantsBean.LESS_OR_EQUAL_THAN;

		case LESS_THAN : return OperatorConstantsBean.LESS_THAN;

		default : return OperatorConstantsBean.EQUALS;
		}
	}

	public static OperatorConstants toOperatorConstants(OperatorConstantsBean bean){
		switch (bean){
		case EQUALS : return OperatorConstants.EQUALS;
		case GREATER_OR_EQUAL_THAN : return OperatorConstants.GREATER_OR_EQUAL_THAN;
		case GREATER_THAN : return OperatorConstants.GREATER_THAN;
		case LESS_OR_EQUAL_THAN: return OperatorConstants.LESS_OR_EQUAL_THAN;
		case LESS_THAN: return OperatorConstants.LESS_THAN;
		default: return OperatorConstants.EQUALS;
		}
	}
	
	
	public static PreferenceDetails getCommunityPreferenceManagerDetails(IIdentityManager idm, ServiceResourceIdentifier serviceIDOf3pService, String downloadOrUpload) throws URISyntaxException{
		PreferenceDetails details = new PreferenceDetails();
		ServiceResourceIdentifier serviceID = new ServiceResourceIdentifier();
		serviceID.setIdentifier(new URI(idm.getThisNetworkNode().getBareJid()));
		serviceID.setServiceInstanceIdentifier("CommunityPreferenceManager");
		details.setServiceID(serviceID);
		details.setServiceType(downloadOrUpload);
		details.setPreferenceName(ServiceModelUtils.serviceResourceIdentifierToString(serviceIDOf3pService));
		
		return details;
		
		
	}
}
