package jp.hayamiti.state;

/**
 * ステートのスーパークラス action state getterを好きに追加して使う
 * @author HayamitiHirotaka
 *
 */
abstract public class State {
	/**
	 * action
	 * @author HayamitiHirotaka
	 *
	 */
	public abstract class Action{

	}
	/**
	 * mode
	 * @author HayamitiHirotaka
	 *
	 */
	public abstract class Mode{

	}
    public abstract  <T> void change(String action, T val);
}
