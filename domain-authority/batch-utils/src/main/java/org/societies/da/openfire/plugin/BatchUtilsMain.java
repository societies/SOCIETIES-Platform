package org.societies.da.openfire.plugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class BatchUtilsMain {
	
	public static final String RELATIVE_URL = "/plugins/societies/societies?";
	public static final String DEFAULT_SECRET = "defaultSecret";

	public static void main(String[] args) {
		if (args.length<3)
			errorExit("ERROR! Usage: java -jar batch-utils.jar <domain authority host and port> <operation> <accounts file> [<secret>]");
		
		String secret = DEFAULT_SECRET;
		if (args.length==4)
			secret = args[3];
		
		String baseUrl = "http://"+args[0]+RELATIVE_URL+"secret="+secret;
		OperationType o = OperationType.valueOf(args[1]);
		Collection<AccountDetails> accountDetails = new ArrayList<AccountDetails>();
		
		try {
			BufferedReader br =  new BufferedReader(new FileReader(args[2]));
			String line = br.readLine();
			while (line != null) {
				accountDetails.add(new AccountDetails(line));
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			errorExit(e.getMessage());
		}
		
		BatchOpenfirePluginCall bopc = new BatchOpenfirePluginCall(baseUrl, System.out, o);
		bopc.process(accountDetails);
	}

	private static void errorExit(String string) {
		System.out.println("ERROR: "+string);
		System.exit(-1);
	}

}
