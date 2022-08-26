package jp.hayamiti;

import java.io.IOException;
import java.util.ArrayList;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.GetSuggestedNextHabitRes;
import jp.hayamiti.httpCon.DbCom.GetHabitsRes;
import jp.hayamiti.httpCon.DbCom.User;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.SuggestNextHabitState;
import jp.hayamiti.utils.MyStrBuilder;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class SuggestNextHabit {
	private static final String TAG = "SuggestNextHabit";

	/**
	 *
	 * @param pose
	 * @param mem
	 * @param motion
	 * @param sotawish
	 * @param mic
	 */
	final public static void suggestNextHabit(CRobotPose pose, CRobotMem mem, CSotaMotion motion,
			MotionAsSotaWish sotawish, CRecordMic mic) {
		//		String mode = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getMode();
		final Enum<SuggestNextHabitState.Mode> mode = ((SuggestNextHabitState) Store
				.getState(SuggestNextHabitState.class))
						.getMode();
		if (mode == SuggestNextHabitState.Mode.SUGGEST) {
			// 今週の生活習慣を得る 正確には1日前~7日前の生活習慣を得る
			// sotaと会話している人の名前を取得
			final FindNameState findNameState = (FindNameState) Store.getState(FindNameState.class);
			final ArrayList<User> fnResults = findNameState.getResults();
			final String nickName = fnResults.get(fnResults.size() - 1).getNickName();
			try {
				final GetHabitsRes res = JSONMapper.mapper.readValue(MyHttpCon.getHabits(nickName, true, 1, 7),
						GetHabitsRes.class);
				// 改善目標を得る
				int[] habit = new int[5];
				for (int i = 0, length = res.getResults().size(); i < length; i++) {
					habit[0] += res.getResults().get(i).isExercise() ? 1 : 0;
					habit[1] += res.getResults().get(i).isDrinking() ? 1 : 0;
					habit[2] += res.getResults().get(i).isEatBreakfast() ? 1 : 0;
					habit[3] += res.getResults().get(i).isEatSnack() ? 1 : 0;
					habit[4] += res.getResults().get(i).getGetUp() - (res.getResults().get(i).getSleep() - 24);
				}
				// 小数点以下が切り捨てられるから、とりあえずまとめてから割り算する
				habit[4] /=  res.getResults().size();
				final GetSuggestedNextHabitRes resNextHabit = JSONMapper.mapper.readValue(MyHttpCon.getSuggestedNextHabit(habit), GetSuggestedNextHabitRes.class);
				// TODO もっといい名前考えて
				String name = "";
				switch(resNextHabit.getIndex()) {
				case 0:
					name = "運動した日数";
					break;
				case 1:
					name = "お酒飲んだ日数";
					break;
				case 2:
					name = "朝食食べた日数";
					break;
				case 3:
					name = "おやつ食べた日";
					break;
				case 4:
					name = "平均睡眠時間";
					break;
				}
				int value = resNextHabit.getValue() - habit[resNextHabit.getIndex()];
				String action = "増やそう";
				if(value < 0) {
					value *= -1;
					action = "減らそう";
				}
				String unit = "日";
				if(resNextHabit.getIndex() == 4) {
					unit = "時間";
				}
				// 改善目標を言う
				String sentence = MyStrBuilder.build(124, "今週の",name, "は", habit[resNextHabit.getIndex()], unit,"だったよ。" );
				TextToSpeech.speech(sentence, sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
				sentence = MyStrBuilder.build(124, "次週は", value, unit, action);
				TextToSpeech.speech(sentence, sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				TextToSpeech.speech("通信に失敗したよ", sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
				return;
			}

		}
	}
}
