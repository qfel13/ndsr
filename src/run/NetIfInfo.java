/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package run;

import java.net.*;
import java.util.*;

public class NetIfInfo {
    public static void main(String[] args) throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

		while (interfaces.hasMoreElements()) {
            NetworkInterface nif = interfaces.nextElement();
			List<InterfaceAddress> list  = nif.getInterfaceAddresses();
			for (InterfaceAddress l : list) {
				String s = l.getAddress().getHostAddress();
				System.out.println("host address = " + s);
			}
        }
    }
}