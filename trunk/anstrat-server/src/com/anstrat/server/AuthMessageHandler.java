package com.anstrat.server;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Random;

import com.anstrat.network.NetworkMessage;
import com.anstrat.server.db.DatabaseHelper;
import com.anstrat.server.db.DatabaseHelper.DatabaseType;

/**
 * This class handles all NetworkMessages related to authentication.
 * @author jay
 *
 */
public class AuthMessageHandler {

	private static String[] randnames = new String[]{"Skallagrim","Leiknir","Folkmar","Gudrik","Osvif","Thorodd"};
	private final static int CREATEQUICK_RETRY_ATTEMPTS = 5;
	
	private MainServer server;
	private Random rand = new Random();
	
	/**l
	 * Constructor that should be used in creation of this class.
	 * @param server Required for proper login/logout handling and logging.
	 */
	public AuthMessageHandler(MainServer server)
	{
		this.server = server;
	}
	
	/**
	 * Attempts to authenticate the socket with the given user/pass parameters.
	 * @param username Username, case insensitive.
	 * @param password Password, case sensitive.
	 * @param socket The socket attempting to authenticate itself.
	 */
	public synchronized void login(String username, String password, PlayerSocket socket)
	{
		NetworkMessage response;
		// Usernames should consistently be handled in all lower case on the server side.
		String lowerCaseUsername = username.toLowerCase(Locale.ENGLISH);

		/* If the socket already is logged in as the user in question, the server
		 * should be robust 
		 */
		if(lowerCaseUsername.equals(socket.getUsername()))
		{
			response = new NetworkMessage("ACCEPT_LOGIN", (Serializable) socket.getNetworkName(), socket.getUser().getUserId());
		}
		else
		{
			java.sql.Connection conn = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			
			try
			{
				conn = DatabaseHelper.connect(DatabaseType.PostgreSQL);
			
				pst = conn.prepareStatement("SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?");
				pst.setString(1, lowerCaseUsername);
				pst.setString(2, HashUtil.getHash("SHA-256", password));
				
				rs = pst.executeQuery();
				
				if(rs.next())
				{
					PlayerSocket anotherInstance = server.getSocketForUser(lowerCaseUsername);
					if(anotherInstance!=null)
					{
						server.logout(anotherInstance, true);
						anotherInstance.sendMessage(new NetworkMessage("LOGIN_OVERRIDE","You have been logged out since you logged in elsewhere."));
					}
					
					User user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("displayedName"));
					socket.setUser(user);
					server.login(socket, user.getUsername(), user.getDisplayedName());
					response = new NetworkMessage("ACCEPT_LOGIN", (Serializable) user.getDisplayedName(), user.getUserId());
				}
				else
				{
					server.logln(socket.getConnection().toString()+" failed to log in as "+lowerCaseUsername+".");
					response = new NetworkMessage("DENY_LOGIN",(Serializable) "Invalid username or password. Please try again.");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
				response = new NetworkMessage("DENY_LOGIN",(Serializable) "An unknown error occurred. Please try again.");
			}
			finally
			{
				DatabaseHelper.closeResSet(rs);
				DatabaseHelper.closeStmt(pst);
				DatabaseHelper.closeConn(conn);
			}
		}

		socket.sendMessage(response);
	}
	
	/**
	 * Attempts to create a random user for the request socket.
	 * @param socket The socket to return the quick login data to.
	 */
	public synchronized void serveQuickLogin(PlayerSocket socket)
	{
		if(!socket.isLoggedIn())
		{
			int numAttempts = 0;
			
			/*
			 * The random factor may cause collisions, but it's highly unlikely that this will occur more than a few times.
			 * The operation will thus be aborted after a few attempts, since something else must have gone wrong.
			 */
			while(numAttempts++ < CREATEQUICK_RETRY_ATTEMPTS)
			{
				String name = randnames[rand.nextInt(randnames.length)] + rand.nextLong();
				if(name.length()>18)
					name = name.substring(0,18);
				
				if(!DatabaseHelper.usernameTaken(name) && !DatabaseHelper.displayednameTaken(name))
				{
					String password = ((Long) rand.nextLong()).toString();
					
					Long userId = DatabaseHelper.createUser(name, password, name);
					
					if(userId==-1l)
						break;
					else
					{
						socket.sendMessage(new NetworkMessage("ACCEPT_QUICK",userId,name,password));
						socket.setUser(new User(userId,name,name));
						server.login(socket, name, name);
						return;
					}
				}
			}
			
			socket.sendMessage(new NetworkMessage("REJECT_QUICK","An unknown error occured. Please try again in a little while."));
		}
		else
			socket.sendMessage(new NetworkMessage("REJECT_QUICK","Already logged in as "+socket.getNetworkName()));
	}
}