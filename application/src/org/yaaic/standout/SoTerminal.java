package org.yaaic.standout;

import org.yaaic.irc.IRCBinder;
import org.yaaic.model.Conversation;
import org.yaaic.model.Scrollback;
import org.yaaic.model.Server;

import android.view.View;
import android.widget.EditText;

public class SoTerminal {

		public Server server;
		public String sConversation;
		public String sTitle;
 		public Conversation conversation;	
		public int iPopup;
		public boolean bVisibile=false;
		public IRCBinder binder;
		public EditText textEdit = null;
		public EditText textOutput = null;
		public SoEditTextSelectionUtility mEtsuEdit = null;
		public SoEditTextSelectionUtility mEtsuOutput = null;
		public Scrollback scrollback=null;
		View vMain=null;
	}
