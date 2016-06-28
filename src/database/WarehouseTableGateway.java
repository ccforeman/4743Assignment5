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

import model.InventoryItem;
import model.Warehouse;

public class WarehouseTableGateway {

	private Connection connection = null;
	
	public WarehouseTableGateway() throws GatewayException, SQLException {
		
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
	
	
	public List<Warehouse> fetchWarehouses() throws GatewayException {
		
		List<Warehouse> whl = new ArrayList<Warehouse>();
		PreparedStatement st = null;
		ResultSet rs = null;
		Warehouse wh = null;
		
		try {
			st = connection.prepareStatement("SELECT * FROM warehouse");
			rs = st.executeQuery();
			while(rs.next()) {
				wh = new Warehouse(rs.getString("warehouseName"), rs.getString("address"), rs.getString("city"), rs.getString("state"), rs.getString("zip"), rs.getDouble("capacity"), rs.getDouble("remainingCapacity"));
				wh.setId(rs.getLong("id"));
				whl.add(wh);
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
		
		
		
		return whl;
	}
	
	public Warehouse fetchWarehouse(long id) throws GatewayException {
		Warehouse fetchedWarehouse = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = connection.prepareStatement("SELECT * FROM warehouse WHERE id = ?");
			st.setLong(1, id);
			rs = st.executeQuery();
			rs.next();
			fetchedWarehouse = new Warehouse(rs.getString("warehouseName"), rs.getString("address"), rs.getString("city"), rs.getString("state"), rs.getString("zip"), rs.getDouble("capacity"), rs.getDouble("remainingCapacity"));
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
		
		return fetchedWarehouse;
	}
	
	public Warehouse fetchWarehouse(String warehouseName) throws GatewayException {
		Warehouse fetchedWarehouse = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = connection.prepareStatement("SELECT * FROM warehouse WHERE warehouseName = ?");
			st.setString(1, warehouseName);
			rs = st.executeQuery();
			rs.next();
			fetchedWarehouse = new Warehouse(rs.getString("warehouseName"), rs.getString("address"), rs.getString("city"), rs.getString("state"), rs.getString("zip"), rs.getDouble("capacity"), rs.getDouble("remainingCapacity"));
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
		
		return fetchedWarehouse;
	}
	
	public long insertWarehouse(Warehouse wh) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		long generatedId = 0;
		
		try {
			st = connection.prepareStatement("INSERT INTO warehouse(warehouseName, address, city, state, zip, capacity, remainingCapacity) VALUES (?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, wh.getWarehouseName());
			st.setString(2, wh.getAddress());
			st.setString(3, wh.getCity());
			st.setString(4, wh.getState());
			st.setString(5, wh.getZip());
			st.setDouble(6, wh.getCapacity());
			st.setDouble(7, wh.getRemainingCapacity());
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
	
	public void deleteWarehouse(long id) throws GatewayException {
		PreparedStatement st = null;
		
		try {
			connection.setAutoCommit(false);
			st = connection.prepareStatement("DELETE FROM warehouse WHERE id = ?");
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
	
	@SuppressWarnings("resource")
	public void updateWarehouse(Warehouse wh) throws GatewayException {
		PreparedStatement st = null;
		
		try {
			st = connection.prepareStatement("UPDATE warehouse SET warehouseName = ?, address = ?, city = ?, state = ?, zip = ?, capacity = ? WHERE id = ?;");
			st.setString(1, wh.getWarehouseName());
			st.setString(2, wh.getAddress());
			st.setString(3, wh.getCity());
			st.setString(4, wh.getState());
			st.setString(5, wh.getZip());
			st.setDouble(6, wh.getCapacity());
			st.setLong(7, wh.getId());
			st.executeUpdate();
			st = connection.prepareStatement("UPDATE warehouse SET remainingCapacity = CapacityLeft(?) WHERE id = ?");
			st.setString(1, wh.getWarehouseName());
			st.setLong(2, wh.getId());
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
	
	public boolean warehouseAlreadyExists(String warehouseName, long id) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = connection.prepareStatement("SELECT COUNT(id) as num_records FROM warehouse WHERE warehouseName = ? and id != ?");
			st.setString(1, warehouseName);
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
	
	public List<InventoryItem> fetchAllInventoryForWarehouse(String warehouseId) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		List<InventoryItem> warehouseInventory = new ArrayList<InventoryItem>();
		InventoryItemTableGateway itg = null;
		
		try {
			itg = new InventoryItemTableGateway();
		} catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		}
		
		try {
			st = connection.prepareStatement("SELECT * FROM inventoryItem WHERE warehouseId = ?");
			st.setString(1, warehouseId);
			rs = st.executeQuery();

			while(rs.next()) {
				InventoryItem i = new InventoryItem(rs.getString("warehouseId"), rs.getString("partId"), rs.getDouble("quantity"));
				i.setId(rs.getLong("id"));
				i.setGateway(itg);
				warehouseInventory.add(i);
			}
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
		
		return warehouseInventory;
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
