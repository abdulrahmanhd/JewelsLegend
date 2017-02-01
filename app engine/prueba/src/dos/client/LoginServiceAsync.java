package dos.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import dos.client.LoginInfo;

public interface LoginServiceAsync {
	public void login(String requestUri, AsyncCallback<LoginInfo> async);
}
