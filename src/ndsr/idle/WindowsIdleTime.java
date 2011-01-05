/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ndsr.idle;

/**
 *
 * @author adro
 */
public class WindowsIdleTime implements IdleTime {

	@Override
	public int getIdleTime() {
		return Win32IdleTime.getIdleTimeMillisWin32() / 1000;
	}
}
