package ndsr.idle;

public class ZeroIdleTime implements IdleTime {

	@Override
	public int getIdleTimeInMili() {
		return 0;
	}

	@Override
	public int getIdleTime() {
		return 0;
	}

	@Override
	public int getIdleTimeInSec() {
		return 0;
	}

	@Override
	public int getIdleTimeInMin() {
		return 0;
	}

}
