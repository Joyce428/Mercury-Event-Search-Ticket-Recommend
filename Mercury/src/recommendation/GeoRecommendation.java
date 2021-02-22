package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommendedItems = new ArrayList<>();
		
		DBConnection dbConnection = DBConnectionFactory.getConnection();
		Set<String> favoriteItemIds = dbConnection.getFavoriteItemIds(userId);
		
		Map<String, Integer> allCategories = new HashMap<>();
		
		for(String itemId : favoriteItemIds) {
			Set<String> categories = dbConnection.getCategories(itemId);
			for(String category : categories) {
				allCategories.put(category, allCategories.getOrDefault(category, 0)+1);
			}
		}
		
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, (Entry<String, Integer> e1, Entry<String, Integer> e2)->{
			return Integer.compare(e2.getValue(), e1.getValue());
		});
		
		Set<String> visitedItemIds = new HashSet<String>();
		for(Entry<String,Integer> categoryEntry : categoryList) {
			List<Item> items = dbConnection.searchItems(lat, lon, categoryEntry.getKey());
		
			for(Item item : items) {
				if(!favoriteItemIds.contains(item.getItemId()) && !visitedItemIds.contains(item.getItemId())) {
					recommendedItems.add(item);
					visitedItemIds.add(item.getItemId());
				}
			}
			
		}
		
		dbConnection.close();
		
		return recommendedItems;		
	}
	
}
