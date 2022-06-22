package jp.hayamiti.httpCon;

public class ApiResponse {
	public class BasicRes{
		public int sendTime;
		public String result;
	}
	public class SpRecRes extends BasicRes{

	}
	public class NameRecRes extends BasicRes{

	}
	public class YesOrNoRes extends BasicRes{

	}

	public class HabitQsRecRes extends BasicRes{
		public String text;
	}
}
