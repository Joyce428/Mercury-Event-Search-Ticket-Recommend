package db.mongodb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq; // eq is like a static method = new Document(...)

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterClient;



public class MongoDBConnection implements DBConnection {
	private MongoClient mongoClient;
	private MongoDatabase db;
	
	public MongoDBConnection() {
		//connects to local mongodb server
		mongoClient = MongoClients.create();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}
	
	@Override
	public void close() {
		if(mongoClient != null) {
			mongoClient.close();
		}
	} 
	

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		if(db==null) {
			return;
		}
		db.getCollection("users").updateOne(eq("user_id","1111"), new Document("$push", new Document("favorite",new Document("$each",itemIds))));
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if(db==null) {
			return;
		}
		db.getCollection("users").updateOne(eq("user_id", userId),new Document("$pullAll", new Document("favorite",itemIds)));
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if(db==null) {
			return new HashSet<>();
		}
		Set<String> favoriteItemIds = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		if(iterable.first()!=null && iterable.first().containsKey("favorite")) {// if user exists, check whether he had added something to Favorite
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>)iterable.first().get("favorite"); // unchecked cast from Object to List<String>
			favoriteItemIds.addAll(list);
		}
		return favoriteItemIds;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {

		if(db==null) {
			return new HashSet<>();
		}
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> favoriteItemIds = getFavoriteItemIds(userId);
		for(String itemId : favoriteItemIds) {
			FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id",itemId));
			if(iterable.first()!=null) {
				ItemBuilder builder = new ItemBuilder();
				Document d = iterable.first();
				builder.setItemId(d.getString("item_id"));
				builder.setName(d.getString("name"));
				builder.setAddress(d.getString("address"));
				builder.setUrl(d.getString("url"));
				builder.setImageUrl(d.getString("image_url"));
				builder.setRating(d.getDouble("rating"));
				builder.setDistance(d.getDouble("distance"));
				builder.setCategories(getCategories(itemId));
				
				favoriteItems.add(builder.build());
			}
			
			
			
		}
		return favoriteItems;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterClient ticketMasterClient = new TicketMasterClient();
		List<Item> items = ticketMasterClient.search(lat, lon, term);
		for(Item item : items) {
			saveItem(item);
		}
		
		return items;
	}

	@Override
	public void saveItem(Item item) {
		if(db==null) {
			return;
		}
		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id",item.getItemId()));
		if(iterable.first()==null) {
			db.getCollection("items").insertOne(new Document()
					.append("item_id",item.getItemId())
					.append("distance",item.getDistance())
					.append("name", item.getName())
					.append("address", item.getAddress())
					.append("url", item.getUrl())
					.append("image_url", item.getImageUrl())
					.append("rating", item.getRating())
					.append("categories", item.getCategories()));
		}
	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean registerUser(String userId, String password, String firstName, String lastName) {
		// TODO Auto-generated method stub
		return false;
	}

}
