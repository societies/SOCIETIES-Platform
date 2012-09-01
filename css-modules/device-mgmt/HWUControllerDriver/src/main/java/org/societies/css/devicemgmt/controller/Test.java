package org.societies.css.devicemgmt.controller;

public class Test {

	public static void main(String[] args){
		String serverInput = "$CTRID:Ctrl0#PRTID:IN0#VALUE:1";
		System.out.println(serverInput);
		if (serverInput.startsWith("$")){
			String[] fields = serverInput.split("#");

			if (fields.length>=3){
				String ctrlField = fields[0];

				String portField = fields[1];

				String valueField = fields[2];

				String controllerId = "";
				String portId = "";
				String value = "";
				int delim = 0;
				if (ctrlField.startsWith("$CTRID")){
					delim = ctrlField.indexOf(':');
					controllerId = ctrlField.substring(delim+1, ctrlField.length());
				}
				if (portField.startsWith("PRTID")){
					delim = portField.indexOf(':');
					portId = portField.substring(delim+1, portField.length());
				}

				if (valueField.startsWith("VALUE")){
					delim = valueField.indexOf(':');
					value = valueField.substring(delim+1, valueField.length());
				}

				System.out.println("Controller: "+controllerId);
				System.out.println("PortId: "+portId);
				System.out.println("Value: "+value);
			}

		}
	}
}
