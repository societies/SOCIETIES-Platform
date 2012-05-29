package org.societies.android.privacytrust.datamanagement.accessor;

public class Constants {
	public static final boolean DEBUG = false;
	public static final boolean TRACE = true;
	
	public static final int AUTHENTICATION_MODE_ANONYMOUS = 0;
	public static final int AUTHENTICATION_MODE_IDENTIFIED = 1;
	public static final int WEBID = 0;
	public static final int LOGIN_PASSWORD = 1;
	/**
	 * @uml.property  name="AUTHENTICATE" readOnly="true"
	 */
	public static final int AUTHENTICATE = 0;
	/**
	 * @uml.property  name="CREATE_WEB_ID_FILE" readOnly="true"
	 */
	public static final int CREATE_WEB_ID_FILE = 1;
	/**
	 * @uml.property  name="AUTHENTICATE_TO_TA" readOnly="true"
	 */
	public static final int AUTHENTICATE_TO_TA = 2;
	/**
	 * @uml.property  name="REGISTER" readOnly="true"
	 */
	public static final int REGISTER = 3;
	/**
	 * @uml.property  name="PREPARE_AUTHENTICATION" readOnly="true"
	 */
	public static final int PREPARE_AUTHENTICATION = 4;
	/**
	 * @uml.property  name="AUTHENTICATE_TO_CIS" readOnly="true"
	 */
	public static final int AUTHENTICATE_TO_CIS = 5;
	/**
	 * @uml.property  name="AUTHENTICATE_TO_CIS" readOnly="true"
	 */
	public static final int CREATE_DB = 15;
	
	public static final int BUFFER_SIZE = 1024;
	public static final String CODE_SEPARATOR = "";
	
//	public static final String HOST_CIS = "192.168.200.87/Societies/SocietiesCIS";
//	public static final String HOST_TA = "192.168.200.87/Societies/SocietiesTA";
//	public static final String HOST_WM = "192.168.200.87/Societies/SocietiesTA";
	public static final String HOST_CIS = "cis.societies.maridat.com";
	public static final String HOST_TA = "ta.societies.maridat.com";
	public static final String HOST_WM = "ta.societies.maridat.com";

	/* --- DB --- */
	public static final String TABLE_CIS = "cis";
	public static final int DB_VERSION = 5;
	public static final String DB_NAME = "societies.db";

	public static final int ADD_CIS = 8;

	public static final int WRITE_FILE = 14;

	public static final int READ_FILE = 13;

	public static final int HTTP_REQUEST = 16;
//	public static final String WEBID_URI = "http://"+Constants.HOST_WM+"/webid/fylhan_horsligne.rdf#me";
//	public static final String WEBID_PUBLICKEY = "3082010a0282010100c10424e0bb0e45405963e6d6c5c6dc50e60260b83343411d5b6dd69bd5529d646b4dd13ff608e48b587aedaea26c8474e054270e5c41f3f558552aac83d7df1e460a54ea0a948538c1f694ebf0993d5fce5fdfbf6890b74259c1832166000250ddf478780c2c994cf4390acde132c5b9c0ceaffeea8a3e17b4c1e1520d4a59437cb42985a60120ea9c2010f42d755a6a9ded33d6cfd8f17e8add6f2585eb774bdf83268549f68db5b024aa924b6329416ea01dc9fa85f709776e0d1a3e41164ab4397eaec1bc4e5fa1e83235bbdda43aca019c405db00132c86413062cba054568d40b65a08b0abc983a3a4d84910ec5cf792e3ee16ff815d1ebb8e0fdff5da90203010001";
//	public static final String WEBID_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n"+
//													"MIIDrTCCAxagAwIBAgIQbgwfgX5cE4vxcy/vjE2JdTANBgkqhkiG9w0BAQUFADBj\n"+
//													"MREwDwYDVQQKDAhGT0FGK1NTTDEmMCQGA1UECwwdVGhlIENvbW11bml0eSBvZiBT\n"+
//													"ZWxmIFNpZ25lcnMxJjAkBgNVBAMMHU5vdCBhIENlcnRpZmljYXRpb24gQXV0aG9y\n"+
//													"aXR5MB4XDTExMDUzMTExMDkyMVoXDTEyMDUyMTEzMDkyMVowgZ0xETAPBgNVBAoM\n"+
//													"CEZPQUYrU1NMMSYwJAYDVQQLDB1UaGUgQ29tbXVuaXR5IE9mIFNlbGYgU2lnbmVy\n"+
//													"czFPME0GCgmSJomT8ixkAQEMP2h0dHA6Ly8xOTIuMTY4LjIwMC44Ny9Tb2NpZXRp\n"+
//													"ZXMvU29jaWV0aWVzVEEvd2ViaWQvZnlsaGFuLnJkZiNtZTEPMA0GA1UEAwwGRnls\n"+
//													"aGFuMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwQQk4LsORUBZY+bW\n"+
//													"xcbcUOYCYLgzQ0EdW23Wm9VSnWRrTdE/9gjki1h67a6ibIR04FQnDlxB8/VYVSqs\n"+
//													"g9ffHkYKVOoKlIU4wfaU6/CZPV/OX9+/aJC3QlnBgyFmAAJQ3fR4eAwsmUz0OQrN\n"+
//													"4TLFucDOr/7qij4XtMHhUg1KWUN8tCmFpgEg6pwgEPQtdVpqne0z1s/Y8X6K3W8l\n"+
//													"het3S9+DJoVJ9o21sCSqkktjKUFuoB3J+oX3CXduDRo+QRZKtDl+rsG8Tl+h6DI1\n"+
//													"u92kOsoBnEBdsAEyyGQTBiy6BUVo1AtloIsKvJg6Ok2EkQ7Fz3kuPuFv+BXR67jg\n"+
//													"/f9dqQIDAQABo4GiMIGfMAwGA1UdEwEB/wQCMAAwDgYDVR0PAQH/BAQDAgLsMBEG\n"+
//													"CWCGSAGG+EIBAQQEAwIFoDAdBgNVHQ4EFgQUgCc8eJurx+la9K7bw3BSa55SAFww\n"+
//													"TQYDVR0RAQH/BEMwQYY/aHR0cDovLzE5Mi4xNjguMjAwLjg3L1NvY2lldGllcy9T\n"+
//													"b2NpZXRpZXNUQS93ZWJpZC9meWxoYW4ucmRmI21lMA0GCSqGSIb3DQEBBQUAA4GB\n"+
//													"AA6W7YgchKXzd9hGxIalDCevjgEwul11i48fze0jIseO4l5CbfJxjD/c1OoUVZOe\n"+
//													"vpgRA19h3/3zNXfky2H61mRDveZEoxDpCflx/YWrl378yDOOZFw84v50Fsd7nsoQ\n"+
//													"jNIPJPSrmzsFT8cLFun2YDKX1VZzZpbw5ZtaDlh4RYvn\n"+
//													"-----END CERTIFICATE-----\n";
	public static final String WEBID_URI = "http://www."+Constants.HOST_WM+"/webid/fylhan.rdf#me";
	public static final String WEBID_PUBLICKEY = "3082010a0282010100c9647eda4fc3a177594bcc5dec1c22a170bd0825544b85bb1d87689c8dddbe65aacd18a82342111b2793b2912a7be57f2da37be675dc0555e6ffbba43fdfa89bb8391816e075021a5ff7c6dfbb2f8f60d6a25b155e7c23b72ac19459b84228094acba0045440ed8e65a8a68a925667a251eda55b6d779d68a6ad8f1b99026ed2216eb9e9d16476d61cb5477a6f2cfcdb46e66992c7e4ff64c7225eb3fd9d457ddeee4ee1efafd8734028408a7860ebca681de6764c35773329198fd1bd3f80f50466bd5a3ee637ee6e233f114b78b29d43961d1d5229757cc188064c207b776fcef85a016f756521335bada34f6e7cec5a857e2784948bc3d67347cc979675230203010001";
	public static final String WEBID_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n"+
					"MIIDlTCCAv6gAwIBAgIQeVkL9irBv3xTyKYwYvw37jANBgkqhkiG9w0BAQUFADBj\n"+
					"MREwDwYDVQQKDAhGT0FGK1NTTDEmMCQGA1UECwwdVGhlIENvbW11bml0eSBvZiBT\n"+
					"ZWxmIFNpZ25lcnMxJjAkBgNVBAMMHU5vdCBhIENlcnRpZmljYXRpb24gQXV0aG9y\n"+
					"aXR5MB4XDTExMDYyMDEyMzkyMFoXDTEyMDYxMDE0MzkyMFowgZExETAPBgNVBAoM\n"+
					"CEZPQUYrU1NMMSYwJAYDVQQLDB1UaGUgQ29tbXVuaXR5IE9mIFNlbGYgU2lnbmVy\n"+
					"czFDMEEGCgmSJomT8ixkAQEMM2h0dHA6Ly90YS5zb2NpZXRpZXMubWFyaWRhdC5j\n"+
					"b20vd2ViaWQvZnlsaGFuLnJkZiNtZTEPMA0GA1UEAwwGRnlsaGFuMIIBIjANBgkq\n"+
					"hkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyWR+2k/DoXdZS8xd7BwioXC9CCVUS4W7\n"+
					"HYdonI3dvmWqzRioI0IRGyeTspEqe+V/LaN75nXcBVXm/7ukP9+om7g5GBbgdQIa\n"+
					"X/fG37svj2DWolsVXnwjtyrBlFm4QigJSsugBFRA7Y5lqKaKklZnolHtpVttd51o\n"+
					"pq2PG5kCbtIhbrnp0WR21hy1R3pvLPzbRuZpksfk/2THIl6z/Z1Ffd7uTuHvr9hz\n"+
					"QChAinhg68poHeZ2TDV3MykZj9G9P4D1BGa9Wj7mN+5uIz8RS3iynUOWHR1SKXV8\n"+
					"wYgGTCB7d2/O+FoBb3VlITNbraNPbnzsWoV+J4SUi8PWc0fMl5Z1IwIDAQABo4GW\n"+
					"MIGTMAwGA1UdEwEB/wQCMAAwDgYDVR0PAQH/BAQDAgLsMBEGCWCGSAGG+EIBAQQE\n"+
					"AwIFoDAdBgNVHQ4EFgQU23obJLoQgPOylOWI0MpH0l+CX7AwQQYDVR0RAQH/BDcw\n"+
					"NYYzaHR0cDovL3RhLnNvY2lldGllcy5tYXJpZGF0LmNvbS93ZWJpZC9meWxoYW4u\n"+
					"cmRmI21lMA0GCSqGSIb3DQEBBQUAA4GBAH03cCbxnsvKT7rUHdiHKGTngz0p6niS\n"+
					"ccH1CJGtoLNz9vH54tdfly4xtbjZ6tZjQE6AyG778yRQxrx115qaSFBVVGyX4sRp\n"+
					"MZmhiaAlzlpjnJ+daf/xXYmtenv3X3XP/bOIB5qrgMX96etF2V6GfJ7V8UUE3zNf\n"+
					"0m61tZgqVgjk\n"+
					"-----END CERTIFICATE-----\n";
	public static final String TABLE_PRIVACY_PERMISSION = "privacytrustprivacypermission";
	public static final String ADD_PrivacyPermission = "AddPrivacyPermission";
	public static final String DELETE_All_PrivacyPermission = "DeleteAllPrivacyPermissions";
	public static final String DELETE_PrivacyPermission = "DeletePrivacyPermission";

}
