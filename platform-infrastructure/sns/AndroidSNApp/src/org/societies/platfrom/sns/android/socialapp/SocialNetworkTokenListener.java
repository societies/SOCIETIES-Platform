package org.societies.platfrom.sns.android.socialapp;

import org.scribe.model.Token;

public interface SocialNetworkTokenListener {

	public void onTokenAvailable(Token accessToken);
}
