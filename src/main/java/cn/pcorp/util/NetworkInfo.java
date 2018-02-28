package cn.pcorp.util;

/** try to determine MAC address of local network card; this is done
 using a shell to run ifconfig (linux) or ipconfig (windows). The
 output of the processes will be parsed.

 <p>

 To run the whole thing, just type java NetworkInfo

 <p>

 Current restrictions:

 <ul>
 <li>Will probably not run in applets

 <li>Tested Windows / Linux only

 <li>Tested J2SDK 1.4 only

 <li>If a computer has more than one network adapters, only
 one MAC address will be returned

 <li>will not run if user does not have permissions to run
 ifconfig/ipconfig (e.g. under linux this is typically
 only permitted for root)
 </ul>

 */

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.StringTokenizer;

public final class NetworkInfo {
	public final static String getMacAddress() throws IOException {
		String os = System.getProperty("os.name");
		try {
			if (os.startsWith("Windows")) {
				return windowsParseMacAddress(windowsRunIpConfigCommand());
			} else if (os.startsWith("Linux")) {
				return linuxParseMacAddress(linuxRunIfConfigCommand());
			} else {
				throw new IOException("获取MAC地址失败:未知的操作系统 " + os);
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
			throw new IOException(ex.getMessage());
		}
	}

	/*
	 * Linux stuff
	 */
	private final static String linuxParseMacAddress(String ipConfigResponse)
			throws ParseException {
		String localHost = null;
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (java.net.UnknownHostException ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage(), 0);
		}

		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = null;

		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();
			boolean containsLocalHost = line.indexOf(localHost) >= 0;

			// see if line contains IP address
			if (containsLocalHost && lastMacAddress != null) {
				return lastMacAddress;
			}

			// see if line contains MAC address
			int macAddressPosition = line.indexOf("HWaddr");
			if (macAddressPosition <= 0)
				continue;

			String macAddressCandidate = line.substring(macAddressPosition + 6)
					.trim();
			if (linuxIsMacAddress(macAddressCandidate)) {
				lastMacAddress = macAddressCandidate;
				break;
			}
		}
		if (lastMacAddress == null) {
			ParseException ex = new ParseException("不能为服务器 " + localHost
					+ " 获取 [" + ipConfigResponse + "] 中的MAC地址", 0);
			ex.printStackTrace();
			throw ex;
		} else {
			return lastMacAddress;
		}
	}

	private final static boolean linuxIsMacAddress(String macAddressCandidate) {
		// TODO: use a smart regular expression
		if (macAddressCandidate.length() != 17)
			return false;
		return true;
	}

	private final static String linuxRunIfConfigCommand() throws IOException {
		Process p = Runtime.getRuntime().exec("ifconfig");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

		StringBuffer buffer = new StringBuffer();
		for (;;) {
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();

		stdoutStream.close();

		return outputText;
	}

	/*
	 * Windows stuff
	 */
	private final static String windowsParseMacAddress(String ipConfigResponse)
			throws ParseException {
		String localHost = null;
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (java.net.UnknownHostException ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage(), 0);
		}

		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = null;

		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();
			// see if line contains IP address
			if (line.endsWith(localHost) && lastMacAddress != null) {
				return lastMacAddress;
			}
			// see if line contains MAC address
			int macAddressPosition = line.indexOf(":");
			if (macAddressPosition <= 0)
				continue;
			String macAddressCandidate = line.substring(macAddressPosition + 1)
					.trim();
			if (!windowsIsMacAddress(macAddressCandidate)) {
				lastMacAddress = macAddressCandidate;
				break;
			}
		}
		if (lastMacAddress == null) {
			ParseException ex = new ParseException("不能从 [" + ipConfigResponse
					+ "] 获取MAC地址", 0);
			ex.printStackTrace();
			throw ex;
		} else {
			return lastMacAddress;
		}
	}

	private final static boolean windowsIsMacAddress(String macAddressCandidate) {
		if (macAddressCandidate.length() == 17
				&& macAddressCandidate.indexOf("-") != -1)
			return false;
		return true;
	}

	private final static String windowsRunIpConfigCommand() throws IOException {
		Process p = Runtime.getRuntime().exec("ipconfig /all");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

		StringBuffer buffer = new StringBuffer();
		for (;;) {
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();

		stdoutStream.close();

		return outputText;
	}

	/*
	 * Main
	 */
	public final static void main(String[] args) {
		try {
			System.out.println("Network infos");
			System.out.println("  Operating System: "
					+ System.getProperty("os.name"));
			System.out.println("  IP/Localhost: "
					+ InetAddress.getLocalHost().getHostAddress());
			System.out.println("  MAC Address: " + getMacAddress());
			System.out.println("  IP/Localhost: " + getIP());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static String getIP() throws Exception {
		// 根据网卡取本机配置的IP
		Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface ni = (NetworkInterface) netInterfaces
					.nextElement();
			System.out.println(ni.getName());
			if (ni.getInetAddresses().hasMoreElements()) {
				ip = (InetAddress) ni.getInetAddresses().nextElement();
				if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
						&& ip.getHostAddress().indexOf(":") == -1) {
					System.out.println("本机的ip=" + ip.getHostAddress());
					return ip.getHostAddress();
					// break;
				}
			}
		}
		return ip.getHostAddress();
	}
}
