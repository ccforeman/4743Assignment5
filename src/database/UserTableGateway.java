//package database;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//import javax.sql.DataSource;
//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
//
//import model.Credentials;
//import model.InventoryItem;
//import model.User;
//
//public class UserTableGateway {
//
//	private Connection connection = null;
//	
//	public UserTableGateway() throws GatewayException, SQLException {
//		
//		DataSource ds = null;
//		
//		try {
//			ds = getDataSource();
//		} catch (RuntimeException | IOException e1) {
//			throw new GatewayException(e1.getMessage());
//		}
//		
//		if(ds == null)
//			throw new GatewayException("Datasource is null!");
//		
//		try {
//			connection = ds.getConnection();
//		} catch (SQLException e) {
//			throw new GatewayException("SQL Error: " + e.getMessage());
//		}
//		
//	}
//	
//	
////	public List<User> fetchUsers() throws GatewayException {
////		
////		List<User> whl = new ArrayList<User>();
////		PreparedStatement st = null;
////		ResultSet rs = null;
////		User wh = null;
////		
////		try {
////			st = connection.prepareStatement("SELECT * FROM user");
////			rs = st.executeQuery();
////			while(rs.next()) {
////				wh = new User();
////				wh.setId(rs.getString());
////				whl.add(wh);
////			}
////		} catch (SQLException e ) {
////			throw new GatewayException(e.getMessage());
////		} finally {
////			try {
////				if(rs != null)
////					rs.close();
////				if(st != null)
////					st.close();
////			} catch (SQLException e) {
////				throw new GatewayException(e.getMessage());
////			}
////		}
////		
////		
////		
////		return whl;
////	}
////	
//	public User fetchUserForLogin(String login, byte[] pwHash) throws GatewayException {
//		User fetchedUser = null;
//		PreparedStatement st = null;
//		ResultSet rs = null;
//		
//		try {
//			st = connection.prepareStatement("SELECT * FROM user WHERE login = ? and pwHash = ?");
//			st.setString(1, login);
//			st.setBytes(2, pwHash);
//			rs = st.executeQuery();
//			rs.next();
//			fetchedUser = 
//		} catch (SQLException e) {
//			throw new GatewayException(e.getMessage());
//		} finally {
//			
//			try {
//				if(rs != null)
//					rs.close();
//				if(st != null)
//					st.close();
//			} catch (SQLException e) {
//				throw new GatewayException(e.getMessage());
//			}
//		}
//		
//		return fetchedUser;
//	}
////	
////	public User fetchUser(String userName) throws GatewayException {
////		User fetchedUser = null;
////		PreparedStatement st = null;
////		ResultSet rs = null;
////		
////		try {
////			st = connection.prepareStatement("SELECT * FROM user WHERE userName = ?");
////			st.setString(1, userName);
////			rs = st.executeQuery();
////			rs.next();
////			fetchedUser = new User(rs.getString("userName"), rs.getString("address"), rs.getString("city"), rs.getString("state"), rs.getString("zip"), rs.getDouble("capacity"), rs.getDouble("remainingCapacity"));
////		} catch (SQLException e) {
////			throw new GatewayException(e.getMessage());
////		} finally {
////			
////			try {
////				if(rs != null)
////					rs.close();
////				if(st != null)
////					st.close();
////			} catch (SQLException e) {
////				throw new GatewayException(e.getMessage());
////			}
////		}
////		
////		return fetchedUser;
////	}
//	
//	public long insertUser(User u) throws GatewayException {
//		PreparedStatement st = null;
//		ResultSet rs = null;
//		long generatedId = 0;
//		
//		try {
//			st = connection.prepareStatement("INSERT INTO user(login, pwHash, fullName, credentials) VALUES (?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
//			st.setString(1, u.getLogin());
//			st.setBytes(2, u.getPwHash());
//			st.setString(3, u.getFullName());
//			st.setString(4, Credentials.NO_DELETE.name());
//			st.executeUpdate();
//			rs = st.getGeneratedKeys();
//			if(rs != null && rs.next()) {
//				generatedId = rs.getLong(1);
//			} else {
//				throw new GatewayException("Couldn't get ID");
//			}
//			try {
//				generatedId = rs.getLong(1);
//			} catch (SQLException e) {
//				throw new GatewayException("There was an error getting the id");
//			}
//		} catch (SQLException e) {
//			throw new GatewayException(e.getMessage());
//		} finally {
//			try {
//				if(rs != null)
//					rs.close();
//				if(st != null)
//					st.close();
//			} catch (SQLException e) {
//				throw new GatewayException(e.getMessage());
//			}
//		}
//		
//		return generatedId;
//	}
//	
//	public void deleteUser(long id) throws GatewayException {
//		PreparedStatement st = null;
//		
//		try {
//			connection.setAutoCommit(false);
//			st = connection.prepareStatement("DELETE FROM user WHERE id = ?");
//			st.setLong(1, id);
//			st.executeUpdate();
//			connection.commit();
//		} catch (SQLException e) {
//			try {
//				connection.rollback();
//				System.err.println("Rolling back commit.");
//			} catch (SQLException e1) {
//				throw new GatewayException(e1.getMessage());
//			}
//			throw new GatewayException(e.getMessage());
//		} finally {
//			try {
//				if(st != null)
//					st.close();
//				connection.setAutoCommit(true);
//			} catch (SQLException e) {
//				throw new GatewayException(e.getMessage());
//			}
//		}
//	}
//	
////	@SuppressWarnings("resource")
////	public void updateUser(User wh) throws GatewayException {
////		PreparedStatement st = null;
////		
////		try {
////			st = connection.prepareStatement("UPDATE user SET userName = ?, address = ?, city = ?, state = ?, zip = ?, capacity = ? WHERE id = ?;");
////			st.setString(1, wh.getUserName());
////			st.setString(2, wh.getAddress());
////			st.setString(3, wh.getCity());
////			st.setString(4, wh.getState());
////			st.setString(5, wh.getZip());
////			st.setDouble(6, wh.getCapacity());
////			st.setLong(7, wh.getId());
////			st.executeUpdate();
////			st = connection.prepareStatement("UPDATE user SET remainingCapacity = CapacityLeft(?) WHERE id = ?");
////			st.setString(1, wh.getUserName());
////			st.setLong(2, wh.getId());
////			st.executeUpdate();
////		} catch (SQLException e) {
////			throw new GatewayException(e.getMessage());
////		} finally {
////			try {
////				if(st != null)
////					st.close();
////			} catch (SQLException e) {
////				throw new GatewayException(e.getMessage());
////			}
////		}
////	}
//	
//	public boolean userAlreadyExists(String userName, long id) throws GatewayException {
//		PreparedStatement st = null;
//		ResultSet rs = null;
//		
//		try {
//			st = connection.prepareStatement("SELECT COUNT(id) as num_records FROM user WHERE userName = ? and id != ?");
//			st.setString(1, userName);
//			st.setLong(2, id);
//			rs = st.executeQuery();
//			rs.next();
//			if(rs.getInt("num_records") > 0)
//				return true;
//		} catch (SQLException e) {
//			throw new GatewayException(e.getMessage());
//		} finally {
//			try {
//				if(st != null)
//					st.close();
//				if(rs != null)
//					rs.close();
//			} catch (SQLException s) {
//				throw new GatewayException(s.getMessage());
//			}
//		}
//		
//		return false;
//	}
//	
//	
//	public void close() {
//		try {
//			connection.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private DataSource getDataSource() throws RuntimeException, IOException {
//		Properties prop = new Properties();
//		FileInputStream in = null;
//		
//		in = new FileInputStream("db.properties");
//		prop.load(in);
//		in.close();
//		
//		MysqlDataSource mds = new MysqlDataSource();
//		mds.setURL(prop.getProperty("MYSQL_DB_URL"));
//		mds.setUser(prop.getProperty("MYSQL_DB_USERNAME"));
//		mds.setPassword(prop.getProperty("MYSQL_DB_PASSWORD"));
//		
//		return mds;
//	}
//}
