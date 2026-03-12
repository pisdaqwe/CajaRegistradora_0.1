package ui.dialog;

public class AskMeDialogResult {
	private final boolean confirmed;
	private final String text;

	public AskMeDialogResult(boolean confirmed, String text) {
		this.confirmed = confirmed;
		this.text = text;

	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public String getText() {
		return text;
	}

	public static AskMeDialogResult confirmed(String text) {
		return new AskMeDialogResult(true, text);
		
	}
	public static AskMeDialogResult cancelled() {
		return new AskMeDialogResult(false, "");
		
	}
}
