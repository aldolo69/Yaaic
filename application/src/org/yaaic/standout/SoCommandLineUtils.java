package org.yaaic.standout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yaaic.model.Conversation;

import android.content.Context;
import android.widget.EditText;

public class SoCommandLineUtils {

	public void doNickCompletion(SoTerminal soT, Context context) {
		String text = soT.textEdit.getText().toString();
		Conversation conversationForUserList = soT.server
				.getConversation(soT.sConversation);
		if (conversationForUserList == null) {
			return;

		}
		if (text.length() <= 0) {
			return;
		}

		String[] tokens = text.split("[\\s,.-]+");

		if (tokens.length <= 0) {
			return;
		}

		String word = tokens[tokens.length - 1].toLowerCase();
		tokens[tokens.length - 1] = null;

		int begin = soT.textEdit.getSelectionStart();
		int end = soT.textEdit.getSelectionEnd();
		int cursor = Math.min(begin, end);
		int sel_end = Math.max(begin, end);

		boolean in_selection = (cursor != sel_end);

		if (in_selection) {
			word = text.substring(cursor, sel_end);
		} else {
			// use the word at the curent cursor position
			while (true) {
				cursor -= 1;
				if (cursor <= 0 || text.charAt(cursor) == ' ') {
					break;
				}
			}

			if (cursor < 0) {
				cursor = 0;
			}

			if (text.charAt(cursor) == ' ') {
				cursor += 1;
			}

			sel_end = text.indexOf(' ', cursor);

			if (sel_end == -1) {
				sel_end = text.length();
			}

			word = text.substring(cursor, sel_end);
		}

		String[] users = null;

		if (conversationForUserList.getType() == Conversation.TYPE_CHANNEL) {
			users = soT.binder.getService().getConnection(soT.server.getId())
					.getUsersAsStringArray(conversationForUserList.getName());
		}

		// go through users and add matches

		if (users != null) {
			final List<String> result = new ArrayList<String>();
			final EditText et = soT.textEdit;
			final Integer iCursor = cursor;
			final Integer iSel_end = sel_end;

			for (int i = 0; i < users.length; i++) {
				String nick = removeStatusChar(users[i].toLowerCase());
				if (nick.startsWith(word.toLowerCase())) {
					result.add(nick);
				}
			}

			if(result.size()==0) return;
			if(result.size()==1) {
				et.setSelection(iCursor, iSel_end);
				insertNickCompletion(et, result.get(0));
				return;
			}
			Collections.sort(result);
			SoPopupUtilityList.SoPopListOnClickListener cl = new SoPopupUtilityList.SoPopListOnClickListener() {
				@Override
				public void onClick(String s) {
					// TODO Auto-generated method stub
					et.setSelection(iCursor, iSel_end);
					insertNickCompletion(et, s);

				};
			};
			new SoPopupUtilityList(context, soT.textEdit, result, cl);

		}

	}

	/*
	 * private static void popup(SoTerminal soT, Context context) {
	 * LayoutInflater inflater = (LayoutInflater) context
	 * .getSystemService(Context.LAYOUT_INFLATER_SERVICE); View v =
	 * inflater.inflate(R.layout.so_terminal_menu, null, false); SoPopupUtility
	 * mPu = new SoPopupUtility(context, v); mPu.showAtLocation(soT.textEdit,
	 * Gravity.CENTER, 0, 0);
	 * 
	 * }
	 */
	public static String removeStatusChar(String nick) {
		/* Discard status characters */
		if (nick.startsWith("@") || nick.startsWith("+")
				|| nick.startsWith("%")) {
			nick = nick.substring(1);
		}
		return nick;
	}

	private void insertNickCompletion(EditText input, String nick) {
		int start = input.getSelectionStart();
		int end = input.getSelectionEnd();
		nick = removeStatusChar(nick);

		if (start == 0) {
			nick += ":";
		}

		nick += " ";
		input.getText().replace(start, end, nick, 0, nick.length());
		// put cursor after inserted text
		input.setSelection(start + nick.length());
		input.clearComposingText();
	}

}
