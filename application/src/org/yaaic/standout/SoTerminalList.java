package org.yaaic.standout;

import java.util.ArrayList;

import org.yaaic.irc.IRCBinder;
import org.yaaic.model.Conversation;
import org.yaaic.model.Server;
import org.yaaic.receiver.ConversationReceiver;

import wei.mark.standout.StandOutWindow;
import android.content.Context;
import android.view.View;

public class SoTerminalList {
	private static SoTerminalList mTl = null;

	// we need a list of server/channel and related popups
	ArrayList<SoTerminal> mSoTerminals = null;

	// we need a list of server/receiver
	ArrayList<SoServer> mSoServers = null;

	public static SoTerminalList getSoTerminalList() {
		if (mTl == null) {
			mTl = new SoTerminalList();
			mTl.mSoTerminals = new ArrayList<SoTerminal>();
			mTl.mSoServers = new ArrayList<SoServer>();
		}
		return mTl;
	}

	//get server info
	public SoServer getSoServer(Server server) {
		if (mSoServers == null)
			return null;

		for (int indx = 0; indx < mSoServers.size(); indx++) {
			SoServer soS = mSoServers.get(indx);
			if (soS.server == server)
				return soS;
		}
		return null;
	}

	// collect a list of server with its receiver
	public void addSoServer(Server server, SoTerminalWindow tw,
			ConversationReceiver channelReceiver) {
		SoServer soS = new SoServer();
		soS.server = server;
		soS.terminalWindow = tw;
		soS.channelReceiver = channelReceiver;
		mSoServers.add(soS);

	}

	// get info about a popup
	public SoTerminal getSoTerminal(int id) {
		for (int indx = 0; indx < mSoTerminals.size(); indx++) {
			SoTerminal sl = mSoTerminals.get(indx);
			if (sl.iPopup == id)
				return sl;
		}
		return null;
	}

	// close a conversation. its window is closed also. the receiver is
	// deregistered if no other windows are open on that server
	public void closeConversation(Context ctx, String conv) {
		SoTerminal soT = getSoTerminal(conv);
		if (soT != null) {
			// how many conv on this server?1?then remove registration
			int channelOnServer = 0;
			for (int indx = 0; indx < mSoTerminals.size(); indx++) {
				SoTerminal sl = mSoTerminals.get(indx);
				if (sl.server == soT.server)
					channelOnServer++;
			}

			if (channelOnServer <= 1) {
				removeSoServer(soT.server);
			}

			StandOutWindow.close(ctx, SoTerminalWindow.class, soT.iPopup);
			removeSoTerminalList(soT.iPopup);
		}
	}

//close all server conversations
	public void closeServer(Context ctx, Server server) {
		ArrayList<String> conversations = new ArrayList<String>();
		for (int indx = 0; indx < mSoTerminals.size(); indx++) {
			SoTerminal sl = mSoTerminals.get(indx);
			if (sl.server == server) {
				conversations.add(sl.sConversation);
			}
		}

		for (int indx = 0; indx < conversations.size(); indx++) {
			closeConversation(ctx, conversations.get(indx));
		}

	}

	public SoTerminal getSoTerminal(View v) {
		for (int indx = 0; indx < mSoTerminals.size(); indx++) {
			SoTerminal sl = mSoTerminals.get(indx);
			if (sl.vMain == v || sl.textEdit == v || sl.textOutput == v)
				return sl;
		}
		return null;
	}

	public SoTerminal getSoTerminal(String channel) {
		for (int indx = 0; indx < mSoTerminals.size(); indx++) {
			SoTerminal sl = mSoTerminals.get(indx);
			if (sl.sConversation.compareToIgnoreCase(channel) == 0)
				return sl;
		}
		return null;
	}

	public Server getSoTerminalServer(int id) {
		for (int indx = 0; indx < mSoTerminals.size(); indx++) {
			SoTerminal sl = mSoTerminals.get(indx);
			if (sl.iPopup == id)
				return sl.server;
		}
		return null;
	}

	public String getSoTerminalConversationName(int id) {
		for (int indx = 0; indx < mSoTerminals.size(); indx++) {
			SoTerminal sl = mSoTerminals.get(indx);
			if (sl.iPopup == id)
				return sl.sConversation;
		}
		return null;
	}

	// public IRCBinder getSoTerminalBinder(int id) {
	// for (int indx = 0; indx < mSoTerminals.size(); indx++) {
	// SoTerminal sl = mSoTerminals.get(indx);
	// if (sl.iPopup == id)
	// return sl.binder;
	// }
	// return null;
	// }

	public int getSoTerminal(Server server, Conversation conv) {
		String sConversation = conv.getName();
		int iReturn = -1;
		for (int indx = 0; indx < mSoTerminals.size(); indx++) {
			SoTerminal sl = mSoTerminals.get(indx);
			if (sl.server == server
					&& sl.sConversation.compareToIgnoreCase(sConversation) == 0) {
				iReturn = sl.iPopup;
				break;
			}
		}
		return iReturn;
	}

	public void removeSoTerminalList(int id) {
		for (int indx = 0; indx < mSoTerminals.size(); indx++) {
			SoTerminal sl = mSoTerminals.get(indx);
			if (sl.iPopup == id)
				mSoTerminals.remove(indx);
		}

	}

	public void removeSoServer(Server server) {
		for (int indx = 0; indx < mSoServers.size(); indx++) {
			SoServer sl = mSoServers.get(indx);
			if (sl.server == server) {
				sl.terminalWindow.unregisterReceiver(sl.channelReceiver);
				mSoServers.remove(indx);
			}
		}

	}

	public int addSoTerminalList(Server server, IRCBinder binder,
			Conversation conv) {
		String sConversation = conv.getName();
		String sTitle = sConversation;
		
		if(sTitle.length()==0)
		{
			sTitle=server.getTitle();
		}
		int iReturn = -1;
		int iMax = -1;
		for (int indx = 0; indx < mSoTerminals.size(); indx++) {
			SoTerminal sl = mSoTerminals.get(indx);

			if (sl.iPopup > iMax)
				iMax = sl.iPopup;

			if (sl.server == server
					&& sl.sConversation.compareToIgnoreCase(sConversation) == 0) {
				iReturn = sl.iPopup;
				break;
			}
		}
		if (iReturn != -1) {
			return iReturn;
		} else {
			SoTerminal sl = new SoTerminal();
			sl.iPopup = iMax + 1;
			sl.server = server;
			sl.sConversation = sConversation;
			sl.sTitle = sTitle;
			sl.conversation = conv; 
			sl.binder = binder;
			mSoTerminals.add(sl);
			return sl.iPopup;
		}
	}

}
