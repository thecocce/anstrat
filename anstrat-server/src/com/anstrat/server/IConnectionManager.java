package com.anstrat.server;

import java.net.InetSocketAddress;
import java.net.Socket;

import com.anstrat.network.NetworkMessage;

public interface IConnectionManager {
	void addConnection(Socket socket);
	void sendMessage(InetSocketAddress address, NetworkMessage message);
	void sendMessage(long userID, NetworkMessage message);
	void linkUserToAddress(long userID, InetSocketAddress address);
	long getUserID(InetSocketAddress address);
}