/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ndsr.idle;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

/**
 *
 * @author lkufel
 */
public class WindowsIdleTime implements IdleTime {

	public interface Kernel32 extends StdCallLibrary {

		Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

		public int GetTickCount();
	};

	public interface User32 extends StdCallLibrary {

		User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

		public static class LASTINPUTINFO extends Structure {

			public int cbSize = 8;
			public int dwTime;
		}

		public boolean GetLastInputInfo(LASTINPUTINFO result);
	};

	public int getIdleTimeMillisWin32() {
		User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
		User32.INSTANCE.GetLastInputInfo(lastInputInfo);
		return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
	}

	@Override
	public int getIdleTimeInMili() {
		return getIdleTimeMillisWin32();
	}

	@Override
	public int getIdleTime() {
		int timeInMillis = getIdleTimeMillisWin32();
		return timeInMillis / 1000;
	}

	@Override
	public int getIdleTimeInSec() {
		return getIdleTime();
	}

	@Override
	public int getIdleTimeInMin() {
		return getIdleTimeInSec() / 60;
	}
}
