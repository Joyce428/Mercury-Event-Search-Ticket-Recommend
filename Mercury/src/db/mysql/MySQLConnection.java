package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterClient;

public class MySQLConnection implements DBConnection {	
	
	private Connection conn;
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();	
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
	
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	@Override
	public void close() {
		if(conn!=null) {
			try {
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		try {
			String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES (?, ?)";
			PreparedStatement ps = conn.prepareCall(sql);
			ps.setString(1,userId);
			for(String itemId : itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		try {
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			PreparedStatement ps = conn.prepareCall(sql);
			ps.setString(1,userId);
			for(String itemId : itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Set<String> getFavoriteItemIds(String userId){
		if (conn==null) {
			return new HashSet<>();
		}
		
		Set<String> favoriteItems = new HashSet<>();
		
		try {
			String sql = "SELECT item_id FROM history WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				String itemId = rs.getString("item_id");
				favoriteItems.add(itemId);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return favoriteItems;
		
	}
	
	@Override
	public Set<Item> getFavoriteItems(String userId){
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> favoriteItemIds = getFavoriteItemIds(userId);
		
		try {
			String sql = "SELECT * FROM items WHERE item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String itemId : favoriteItemIds) {
				stmt.setString(1,  itemId);
				ResultSet rs = stmt.executeQuery();
				ItemBuilder builder = new ItemBuilder();
			
				while(rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(itemId));
					builder.setDistance(rs.getDouble("distance"));
					builder.setRating(rs.getDouble("rating"));
				
					favoriteItems.add(builder.build());
				}
			}
		}catch(SQLException e){
			
			e.printStackTrace();
		}
		
		return favoriteItems;
	}
	
	@Override
	public Set<String> getCategories(String itemId){
		if(conn == null) {
			return null;
		}
		Set<String> categories = new HashSet<>();
		try {
			String sql = "SELECT category from categories WHERE item_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, itemId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				String category = rs.getString("category");
				categories.add(category);
			}
						
		}catch(SQLException e) {
			System.out.print(e.getMessage());
		}
		
		return categories;
				
	}
	
	@Override
	public List<Item> searchItems(double lat, double lon, String term){
		TicketMasterClient ticketMasterClient = new TicketMasterClient();
		List<Item> items = ticketMasterClient.search(lat, lon, term);
		
		//test
		
		
		//test end
		
		
		
		for(Item item : items) {
			saveItem(item);
		}
		
		return items;
	}
	
	@Override
	public void saveItem(Item item) {
		try {
			String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareCall(sql); // with the help of PreparedStatement, 
			//the code is more readable because we do not have to write a very long sql statement in one line
			ps.setString(1, item.getItemId());
			ps.setString(2, item.getName());
			ps.setDouble(3, item.getRating());
			ps.setString(4, item.getAddress());
			ps.setString(5, item.getImageUrl());
			ps.setString(6, item.getUrl());
			ps.setDouble(7, item.getDistance());
			ps.execute();
			
			sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
			ps = conn.prepareStatement(sql);
									//itemid 123
			ps.setString(1, item.getItemId());
									// pop, music
			for(String category : item.getCategories()) {
				ps.setString(2, category);
				//itemId 123, category music
				ps.execute();
			}
			// itemId, category
			// 123, pop
			// 123, music
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getFullname(String userId) {
		if(conn == null) {
			return "";
		}
		String name = "";
		try {
			String sql = "SELECT first_name, last_name from users WHERE user_id = ?";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			ResultSet result = preparedStatement.executeQuery();
			if(result.next()) {
				name = result.getString("first_name")+" "+result.getString("last_name");
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
				
		return name;
	}
	
	@Override
	public boolean verifyLogin(String userId, String password) {
		if(conn ==null) {
			return false;
		}
		try {
			String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, password);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean registerUser(String userId, String password, String firstName, String lastName) {
		if(conn == null) {
			return false;
		}
		
		try {
			String sql = "INSERT IGNORE INTO users VALUES(?, ?, ?, ?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, firstName);
			preparedStatement.setString(4, lastName);
			
			return preparedStatement.executeUpdate() == 1;
			
 		} catch(Exception e) {
 			e.printStackTrace();
		}
		return false;
	}
}
