package ndsr.idle;

import com.sun.jna.*;
import com.sun.jna.platform.unix.X11;

public class LinuxIdleTime implements IdleTime {

	private final X11 x11;
	private final Xss xss;
	
	public LinuxIdleTime() {
		x11 = X11.INSTANCE;
		xss = Xss.INSTANCE;
	}
	
	@Override
	public int getIdleTimeInMili() {
		X11.Window win = null;
		Xss.XScreenSaverInfo info = null;
		X11.Display dpy = null;
		

		int idlemillis = 0;
		try {
			dpy = x11.XOpenDisplay(null);
			win = x11.XDefaultRootWindow(dpy);
			info = xss.XScreenSaverAllocInfo();
			xss.XScreenSaverQueryInfo(dpy, win, info);

			idlemillis = info.idle.intValue();
		} finally {
			if (info != null) {
				x11.XFree(info.getPointer());
			}
			info = null;

			if (dpy != null) {
				x11.XCloseDisplay(dpy);
			}
			dpy = null;
		}
		return idlemillis;
	}

	@Override
	public int getIdleTime() {
		return getIdleTimeInSec();
	}

	@Override
	public int getIdleTimeInSec() {
		return getIdleTimeInMili() / 1000;
	}

	@Override
	public int getIdleTimeInMin() {
		return getIdleTimeInSec() / 60;
	}

	/** Definition (incomplete) of the Xext library. */
	interface Xss extends Library {

		Xss INSTANCE = (Xss) Native.loadLibrary("Xss", Xss.class);

		public class XScreenSaverInfo extends Structure {

			public X11.Window window; /* screen saver window */

			public int state; /* ScreenSaver{Off,On,Disabled} */

			public int kind; /* ScreenSaver{Blanked,Internal,External} */

			public NativeLong til_or_since; /* milliseconds */

			public NativeLong idle; /* milliseconds */

			public NativeLong event_mask; /* events */

		}

		XScreenSaverInfo XScreenSaverAllocInfo();

		int XScreenSaverQueryInfo(X11.Display dpy, X11.Drawable drawable, XScreenSaverInfo saver_info);
	}
}
