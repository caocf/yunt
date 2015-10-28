package com.dykj.trajectory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.bepo.core.PathConfig;

public class UDPClient {

	public static void send(String message) {
		int server_port = PathConfig.UDP_PORT;
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		InetAddress local = null;
		try {
			local = InetAddress.getByName(PathConfig.UDP_HOST);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		int msg_length = message.length();
		System.out.println(message);
		byte[] messageByte = message.getBytes();
		DatagramPacket p = new DatagramPacket(messageByte, msg_length, local, server_port);
		try {
			socket.send(p);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

}
