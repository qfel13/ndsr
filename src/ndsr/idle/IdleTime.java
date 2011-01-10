package ndsr.idle;

/**
 * @author adro, qfel13
 */
public interface IdleTime {

	/**
	 * @return idle time in milliseconds
	 */
	int getIdleTimeInMili();

	/**
	 * @return idle time in seconds
	 */
	int getIdleTime();

	/**
	 * @return idle time in seconds
	 */
	int getIdleTimeInSec();

	/**
	 * @return idle time in minutes
	 */
	int getIdleTimeInMin();
}
