package org.yaaic.standout;

import org.yaaic.R;
import org.yaaic.command.CommandParser;
import org.yaaic.listener.ConversationListener;
import org.yaaic.listener.ServerListener;
import org.yaaic.model.Broadcast;
import org.yaaic.model.Conversation;
import org.yaaic.model.Message;
import org.yaaic.model.Scrollback;
import org.yaaic.model.Settings;
import org.yaaic.receiver.ConversationReceiver;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

public class SoTerminalWindow extends StandOutWindow implements OnKeyListener,
		ServerListener, ConversationListener {

	// ServerReceiver serverReceiver;
	ConversationReceiver channelReceiver;

	private Context mContext;
	 
	/***********************************/
	/**
	 * On service connected
	 */

	/***********************************/

	public void onStatusUpdate() {
	}

	public void onConversationMessage(String target) {
		SoTerminal soT = SoTerminalList.getSoTerminalList().getSoTerminal(
				target);
		if (soT == null || soT.server == null)
			return;
		Conversation conversation = soT.server.getConversation(target);

		if (conversation == null)
			return;

		while (conversation.hasBufferedMessages()) {
			Message message = conversation.pollBufferedMessage();
			appendTextAndScroll(soT.textOutput, soT.mEtsuOutput,
					message.render(mContext));

		}

	}

	public void onNewConversation(String target) {
	}

	public void onRemoveConversation(String target) {
	}

	public void onTopicChanged(String target) {
	}

	/**************************** standout ************************/

	@Override
	public PopupWindow getDropDown(final int id) {
		return null;
	}

	@Override
	public boolean onHide(int id, Window window) {
		SoTerminal soT = SoTerminalList.getSoTerminalList().getSoTerminal(id);
		soT.bVisibile = false;
		return false;
	}

	@Override
	public boolean onShow(int id, Window window) {
		SoTerminal soT = SoTerminalList.getSoTerminalList().getSoTerminal(id);
		soT.bVisibile = true;

		// switch selected channel to the current one
		String sOldCh = soT.server.getSelectedConversation();
		if (sOldCh != null && sOldCh.length() != 0) {
			soT.server.getConversation(sOldCh).setStatus(
					Conversation.STATUS_DEFAULT);
		}

		 	soT.conversation.setStatus(Conversation.STATUS_SELECTED);
			soT.server.setSelectedConversation(soT.sConversation);
		 
		return false;
	}

	@Override
	public boolean onClose(int id, Window window) {
		SoTerminalList.getSoTerminalList().removeSoTerminalList(id);
		return false;
	}

	@Override
	public String getTitle(int id) {
		String sTitle = "";
		Settings settings = new Settings(this.getApplicationContext());
		SoTerminal soT = SoTerminalList.getSoTerminalList().getSoTerminal(id);

		sTitle = soT.sTitle; 

		int iMaxSize = settings.getChannelMaxSize();

		if (iMaxSize > 0 && sTitle.length() > iMaxSize) {
			sTitle = sTitle.subSequence(0, iMaxSize) + "\u2026";
		}

		return sTitle;
	}

	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		// TODO Auto-generated method stub
		mContext = this.getApplicationContext();


		SoTerminal soT = SoTerminalList.getSoTerminalList().getSoTerminal(id);

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.so_terminal_window, frame, true);
		soT.vMain = v;

		soT.scrollback = new Scrollback();
		
		
		soT.textOutput = (EditText) v.findViewById(R.id.outputText);
		soT.textEdit = (EditText) v.findViewById(R.id.editText);
		soT.textEdit.setOnKeyListener(this);
		Settings settings = new Settings(mContext);
		soT.textEdit.setTextSize(settings.getFontSize());
		soT.textEdit.setTypeface(settings.getFontType());

		soT.mEtsuEdit = new SoEditTextSelectionUtility((Context) this,
				soT.textEdit);

		soT.textOutput.setTextSize(settings.getFontSize());
		soT.textOutput.setTypeface(settings.getFontType());
		soT.textOutput.setRawInputType(InputType.TYPE_NULL);
		soT.textOutput.setOnKeyListener(this);
		soT.mEtsuOutput = new SoEditTextSelectionUtility((Context) this,
				soT.textOutput);
		soT.mEtsuOutput.setReadOnly();
		soT.mEtsuOutput.setNavigateOnClick(true);

		if (SoTerminalList.getSoTerminalList().getSoServer(soT.server) == null) {
			channelReceiver = new ConversationReceiver(soT.server.getId(), this);
			registerReceiver(channelReceiver, new IntentFilter(
					Broadcast.CONVERSATION_MESSAGE));
			registerReceiver(channelReceiver, new IntentFilter(
					Broadcast.CONVERSATION_NEW));
			registerReceiver(channelReceiver, new IntentFilter(
					Broadcast.CONVERSATION_REMOVE));
			registerReceiver(channelReceiver, new IntentFilter(
					Broadcast.CONVERSATION_TOPIC));

			SoTerminalList.getSoTerminalList().addSoServer(soT.server, this,
					channelReceiver);
		}
	}

	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		return new StandOutLayoutParams(id, 250, 300,
				StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER);

	}

	@Override
	public int getFlags(int id) {
		return super.getFlags(id)
				// StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE
				| StandOutFlags.FLAG_DECORATION_SYSTEM
				| StandOutFlags.FLAG_BODY_MOVE_ENABLE
				| StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
				| StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP
				| StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TOUCH
				| StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
				| StandOutFlags.FLAG_WINDOW_PINCH_RESIZE_ENABLE;
	}

	@Override
	public Animation getCloseAnimation(int id) {
		return null;
	}

	@Override
	public Animation getHideAnimation(int id) {
		return null;
	}

	@Override
	public Animation getShowAnimation(int id) {
		return null;
	}

	@Override
	public String getAppName() {
		// TODO Auto-generated method stub
		return this.getString(R.string.app_name);

	}

	@Override
	public int getAppIcon() {
		// TODO Auto-generated method stub
		return R.drawable.icon;
		// return 0;

	}

	@Override
	public Notification getPersistentNotification(int id) {
		// basic notification stuff
		// http://developer.android.com/guide/topics/ui/notifiers/notifications.html
		int icon = 0;
		long when = System.currentTimeMillis();
		Context c = getApplicationContext();
		String contentTitle = getPersistentNotificationTitle(id);
		String contentText = "";
		String tickerText = String.format("%s: %s", contentTitle, contentText);

		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(c, contentTitle, contentText, null);
		return notification;

	}

	@Override
	public String getHiddenNotificationTitle(int id) {
		SoTerminal soT = SoTerminalList.getSoTerminalList().getSoTerminal(id);
		return soT.sConversation + " " + this.getString(R.string.so_hidden);
	}

	@Override
	public String getHiddenNotificationMessage(int id) {
		SoTerminal soT = SoTerminalList.getSoTerminalList().getSoTerminal(id);
		return this.getString(R.string.so_click_to_restore);
	}

	// return an Intent that restores the MultiWindow
	@Override
	public Intent getHiddenNotificationIntent(int id) {
		return StandOutWindow.getShowIntent(this, getClass(), id);
	}

	/************ end of standout *****************************/

	/*********************/
	/* input from keyboard */
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		SoTerminal soT = SoTerminalList.getSoTerminalList().getSoTerminal(v);

		if (v == soT.textOutput) {
			if (soT.mEtsuOutput != null)
				soT.mEtsuOutput.resetGlobals();
			soT.textOutput.setSelection(soT.textOutput.getText().length());
			return true;
		}
		if (v == soT.textEdit) {
			if (soT.mEtsuEdit != null)
				soT.mEtsuEdit.resetGlobals();

			if ((event.getAction() == KeyEvent.ACTION_DOWN)
					&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
				soT.scrollback.addMessage(soT.textEdit.getText().toString());
				sendMessage(soT);
				return true;
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					String message = soT.scrollback.goBack();
					if (message != null) {
						soT.textEdit.setText(message);
					}
				}
				return true;
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					String message =  soT.scrollback.goForward();
					if (message != null) {
						soT.textEdit.setText(message);
					}
				}
				return true;
			}

			if (keyCode == KeyEvent.KEYCODE_TAB
					|| keyCode == KeyEvent.KEYCODE_SEARCH) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					SoCommandLineUtils clu = new SoCommandLineUtils();
					clu.doNickCompletion(soT, mContext);
				}
				return true;
			}

		}
		return false;
	}

	private void sendMessage(SoTerminal soT) {
		String text = soT.textEdit.getText().toString();
		soT.textEdit.setText("");
		if (text.equals("")) {
			// ignore empty messages
			return;
		}

 
		Conversation conversation = soT.conversation;
		if (conversation != null && soT.binder != null) {
			if (!text.trim().startsWith("/")) {
				if (conversation.getType() != Conversation.TYPE_SERVER) {
					String nickname = soT.binder.getService()
							.getConnection(soT.server.getId()).getNick();
					// conversation.addMessage(new Message("<" + nickname + "> "
					// + text));
					conversation.addMessage(new Message(text, nickname));
					soT.binder.getService().getConnection(soT.server.getId())
							.sendMessage(conversation.getName(), text);
				} else {
					Message message = new Message(
							getString(R.string.chat_only_form_channel));
					message.setColor(Message.COLOR_YELLOW);
					message.setIcon(R.drawable.warning);
					conversation.addMessage(message);
				}
				onConversationMessage(conversation.getName());
			} else {
				CommandParser.getInstance().parse(text, soT.server,
						conversation, soT.binder.getService());
			}
		}
	}

	/***************************/
	/* output to video */
	private void appendTextAndScroll(EditText textOutput,
			SoEditTextSelectionUtility teu, SpannableString ss) {

		if (textOutput != null) {
			String s = textOutput.getText().toString();
			int iStartSelection = textOutput.getSelectionStart();
			int iEndSelection = textOutput.getSelectionEnd();
			CharSequence cs = textOutput.getText();
			int iLen = s.length();
			int iDelta;
			if ((iLen > 4000 && iStartSelection == iEndSelection)
					|| iLen > 14000) {
				int iIndex = TextUtils.indexOf(cs, "\n");
				iDelta = iIndex + 1;
				textOutput.setText(TextUtils.concat(
						cs.subSequence(iIndex + 1, iLen), "\n", ss));
				if (iStartSelection != iEndSelection) {
					iStartSelection -= iDelta;
					if (iStartSelection < 0)
						iStartSelection = 0;
					iEndSelection -= iDelta;
					if (iEndSelection < 0) {
						iEndSelection = 0;
					}
					if ((iStartSelection == 0 && iEndSelection == 0)
							|| (iStartSelection == iEndSelection)) {
						iLen = textOutput.getText().length();
						// i'm here to say "go to end of list". BUT if the
						// scroll bar is not at the bottom
						// do not shift it at all
						textOutput.setSelection(iLen, iLen);
					}
					{
						textOutput.setSelection(iStartSelection, iEndSelection);

					}

				}
			} else {
				textOutput.setText(TextUtils.concat(cs, "\n", ss));
				if (iStartSelection != iEndSelection) {
					textOutput.setSelection(iStartSelection, iEndSelection);
				}
			}

			iLen = textOutput.getText().length();

			if (iEndSelection == iStartSelection) {
				textOutput.setSelection(iLen, iLen);
			}
		}
	}
	/***************************/

}
