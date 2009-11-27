package com.norex.openidtest.client;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OpenIdTest implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	private final LoginServiceAsync loginService = GWT
		.create(LoginService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		boolean loggedIn = "true".equals(Window.Location.getParameter("loggedIn"));
		
		if (!loggedIn) {
			showLoginPrompt();
			return;
		}

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		greetingService.doSomethingInteresting(new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				dialogBox
						.setText("Remote Procedure Call - Failure");
				serverResponseLabel
						.addStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML(SERVER_ERROR);
				dialogBox.center();
				closeButton.setFocus(true);
			}

			public void onSuccess(String result) {
				dialogBox.setText("Remote claimed identifier");
				serverResponseLabel
						.removeStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML("claimed identifier: " + result);
				dialogBox.center();
				closeButton.setFocus(true);
			}
		});
		
	}

	private void showLoginPrompt() {
		String email = Window.prompt("Not logged in - will try now. What is your email?", "dave.leblanc@norex.ca");

		loginService.loginAndGetAddressToRedirectTo(email, new AsyncCallback<AuthRedirectInfo>() {
			@Override
			public void onSuccess(AuthRedirectInfo result) {
				Window.Location.assign(result.getRedirectTo());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("login failed, exception: " + caught.getMessage());
			}
		});
	}
}
