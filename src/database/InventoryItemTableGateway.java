package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import logging.ReportData;
import model.InventoryItem;

public class InventoryItemTableGateway {

	private Connection connection = null;
	
	public InventoryItemTableGateway() throws GatewayException, SQLException {
		
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
	
	
	public List<InventoryItem> fetchInventoryItems() throws GatewayException {
		
		List<InventoryItem> pl = new ArrayList<InventoryItem>();
		PreparedStatement st = null;
		ResultSet rs = null;
		InventoryItem i = null;
		
		try {
			st = connection.prepareStatement("SELECT * FROM inventoryItem");
			rs = st.executeQuery();
			while(rs.next()) {
				i = new InventoryItem(rs.getString("warehouseId"), rs.getString("partId"), rs.getDouble("quantity"));
				i.setId(rs.getLong("id"));
				pl.add(i);
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
	
	public InventoryItem fetchInventoryItem(long id) throws GatewayException {
		InventoryItem fetchedInventoryItem = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = connection.prepareStatement("SELECT * FROM inventoryItem WHERE id = ?");
			st.setLong(1, id);
			rs = st.executeQuery();
			rs.next();
			fetchedInventoryItem = new InventoryItem(rs.getString("warehouseId"), rs.getString("partId"), rs.getDouble("quantity"));
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
		
		return fetchedInventoryItem;
	}
	
	public long insertInventoryItem(InventoryItem i) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		long generatedId = 0;
		
		try {
			st = connection.prepareStatement("INSERT INTO inventoryItem(warehouseId, partId, quantity) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, i.getWarehouseId());
			st.setString(2, i.getPartId());
			st.setDouble(3, i.getQuantity());
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
	
	public void deleteInventoryItem(long id) throws GatewayException {
		PreparedStatement st = null;
		
		try {
			connection.setAutoCommit(false);
			st = connection.prepareStatement("DELETE FROM inventoryItem WHERE id = ?");
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
	
	public void updateInventoryItem(InventoryItem i) throws GatewayException {
		PreparedStatement st = null;
		
		try {
			st = connection.prepareStatement("UPDATE inventoryItem SET quantity = ? WHERE warehouseId = ? and partId = ?");
			st.setDouble(1, i.getQuantity());
			st.setString(2, i.getWarehouseId());
			st.setString(3, i.getPartId());
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
	
	public boolean inventoryItemAlreadyExists(String warehouseId, String partId, long id) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = connection.prepareStatement("SELECT COUNT(id) as num_records FROM inventoryItem WHERE warehouseId = ? and partId = ? and id != ?");
			st.setString(1, warehouseId);
			st.setString(2, partId);
			st.setLong(3, id);
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
	
	/* For Testing only */
	public boolean inventoryItemExists(String warehouseId, String partId, long id) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = connection.prepareStatement("SELECT COUNT(id) as num_records FROM inventoryItem WHERE warehouseId = ? and partId = ? and id = ?");
			st.setString(1, warehouseId);
			st.setString(2, partId);
			st.setLong(3, id);
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
	
	/*
	 * SELECT i.warehouseId, i.partId, p.partName, i.quantity, p.unitOfQuantity FROM `inventoryItem` i LEFT JOIN part p ON i.partId = p.partNum ORDER BY i.warehouseId, p.partName;
	 * 
	 */
	
	public List<ReportData> fetchDataForReport() throws GatewayException {
		List<ReportData> pl = new ArrayList<ReportData>();
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = connection.prepareStatement("SELECT i.warehouseId, i.partId, p.partName, i.quantity, p.unitOfQuantity "
					+ "FROM inventoryItem i LEFT JOIN part p ON i.partId = p.partNum ORDER BY i.warehouseId, p.partName");
			rs = st.executeQuery();
			while(rs.next()) {
				ReportData p = new ReportData(rs.getString("warehouseId"), rs.getString("partId"), rs.getString("partId"), rs.getDouble("quantity"), rs.getString("unitOfQuantity"));
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
