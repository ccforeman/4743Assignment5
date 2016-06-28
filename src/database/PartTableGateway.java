package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import model.Part;

public class PartTableGateway {

	private Connection connection = null;
	
	public PartTableGateway() throws GatewayException, SQLException {
		
		DataSource ds = null;
		
		try {
			ds = getDataSource();
		} catch (RuntimeException | IOException e1) {
			throw new GatewayException(e1.getMessage());
		}
		
		if(ds == null)
			throw new GatewayException("Datasource is null!");
		
		try {
			connection = ds.getConnection();
		} catch (SQLException e) {
			throw new GatewayException("SQL Error: " + e.getMessage());
		}
		
	}
	
	
	public List<Part> fetchParts() throws GatewayException {
		
		List<Part> pl = new ArrayList<Part>();
		PreparedStatement st = null;
		ResultSet rs = null;
		Part p = null;
		
		try {
			st = connection.prepareStatement("SELECT * FROM part");
			rs = st.executeQuery();
			while(rs.next()) {
				p = new Part(rs.getString("partNum"), rs.getString("partName"), rs.getString("vendor"), rs.getString("unitOfQuantity"), rs.getString("vendorPartNum"));
				p.setId(rs.getLong("id"));
				pl.add(p);
			}
		} catch (SQLException e ) {
			throw new GatewayException(e.getMessage());
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException(e.getMessage());
			}
		}
		
		
		
		return pl;
	}
	
	public Part fetchPart(long id) throws GatewayException {
		Part fetchedPart = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = connection.prepareStatement("SELECT * FROM part WHERE id = ?");
			st.setLong(1, id);
			rs = st.executeQuery();
			rs.next();
			fetchedPart = new Part(rs.getString("partNum"), rs.getString("partName"), rs.getString("vendor"), rs.getString("unitOfQuantity"), rs.getString("vendorPartNum"));
		} catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		} finally {
			
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException(e.getMessage());
			}
		}
		
		return fetchedPart;
	}
	
	public long insertPart(Part p) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		long generatedId = 0;
		
		try {
			st = connection.prepareStatement("INSERT INTO part(partNum, partName, vendor, unitOfQuantity, vendorPartNum) VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, p.getPartNum());
			st.setString(2, p.getPartName());
			st.setString(3, p.getVendor());
			st.setString(4, p.getUnitOfQuantity());
			st.setString(5, p.getVendorPartNum());
			st.executeUpdate();
			rs = st.getGeneratedKeys();
			if(rs != null && rs.next()) {
				generatedId = rs.getLong(1);
			} else {
				throw new GatewayException("Couldn't get ID");
			}
			try {
				generatedId = rs.getLong(1);
			} catch (SQLException e) {
				throw new GatewayException("There was an error getting the id");
			}
		} catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException(e.getMessage());
			}
		}
		
		return generatedId;
	}
	
	public void deletePart(long id) throws GatewayException {
		PreparedStatement st = null;
		
		try {
			connection.setAutoCommit(false);
			st = connection.prepareStatement("DELETE FROM part WHERE id = ?");
			st.setLong(1, id);
			st.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
				System.err.println("Rolling back commit.");
			} catch (SQLException e1) {
				throw new GatewayException(e1.getMessage());
			}
			throw new GatewayException(e.getMessage());
		} finally {
			try {
				if(st != null)
					st.close();
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				throw new GatewayException(e.getMessage());
			}
		}
	}
	
	public void updatePart(Part p) throws GatewayException {
		PreparedStatement st = null;
		
		try {
			st = connection.prepareStatement("UPDATE part SET partNum = ?, partName = ?, vendor = ?, unitOfQuantity = ?, vendorPartNum = ? WHERE id = ?");
			st.setString(1, p.getPartNum());
			st.setString(2, p.getPartName());
			st.setString(3, p.getVendor());
			st.setString(4, p.getUnitOfQuantity());
			st.setString(5, p.getVendorPartNum());
			st.setLong(6, p.getId());
			st.executeUpdate();
		} catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException(e.getMessage());
			}
		}
	}
	
	public boolean partAlreadyExists(String partNum, long id) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = connection.prepareStatement("SELECT COUNT(id) as num_records FROM part WHERE partNum = ? and id != ?");
			st.setString(1, partNum);
			st.setLong(2, id);
			rs = st.executeQuery();
			rs.next();
			if(rs.getInt("num_records") > 0)
				return true;
		} catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		} finally {
			try {
				if(st != null)
					st.close();
				if(rs != null)
					rs.close();
			} catch (SQLException s) {
				throw new GatewayException(s.getMessage());
			}
		}
		
		return false;
	}
	
	public boolean checkLock(long id) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		boolean locked;
		
		try {
			st = connection.prepareStatement("SELECT plock FROM part WHERE id = ?");
			st.setLong(1, id);
			rs = st.executeQuery();
			rs.next();
			locked = rs.getBoolean("plock");
		} catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException(e.getMessage());
			}
		}
		
		return locked;
	}
	
	public void pessimisticLock(long id) throws GatewayException {
		PreparedStatement st = null;
		
		try {
			st = connection.prepareStatement("UPDATE part SET plock = ? WHERE id = ?");
			st.setBoolean(1, true);
			st.setLong(2, id);
			st.executeUpdate();
		}  catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException(e.getMessage());
			}
		}
	}
	
	public void releaseLock(long id) throws GatewayException {
		PreparedStatement st = null;
		
		try {
			st = connection.prepareStatement("UPDATE part SET plock = ? WHERE id = ?");
			st.setBoolean(1, false);
			st.setLong(2, id);
			st.executeUpdate();
		}  catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException(e.getMessage());
			}
		}
	}
	
	public void resetAllLocks() throws GatewayException {
		PreparedStatement st = null;
		
		try {
			st = connection.prepareStatement("UPDATE part SET plock = 0");
			st.executeUpdate();
		}  catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		} finally {
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException(e.getMessage());
			}
		}
	}
	
	
	
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private DataSource getDataSource() throws RuntimeException, IOException {
		Properties prop = new Properties();
		FileInputStream in = null;
		
		in = new FileInputStream("db.properties");
		prop.load(in);
		in.close();
		
		MysqlDataSource mds = new MysqlDataSource();
		mds.setURL(prop.getProperty("MYSQL_DB_URL"));
		mds.setUser(prop.getProperty("MYSQL_DB_USERNAME"));
		mds.setPassword(prop.getProperty("MYSQL_DB_PASSWORD"));
		
		return mds;
	}
}
