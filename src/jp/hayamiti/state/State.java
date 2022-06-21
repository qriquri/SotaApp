package jp.hayamiti.state;

/**
 * ステートのスーパークラス action state getterを好きに追加して使う
 * @author HayamitiHirotaka
 *
 */
abstract public class State {
	public abstract <T> void dispatch(Enum<?> action, T val);
}
