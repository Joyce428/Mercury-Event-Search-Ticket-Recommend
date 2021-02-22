package db.mongodb;

import java.text.ParseException;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class MongoDBCreation {
	//create mongDB collecitons with index
	public static void main(String args[]) throws ParseException{
		MongoClient mongoClient = MongoClients.create();//omit  "localhost" "27017"
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);//specify the database name
		
		db.getCollection("users").drop(); // delete already existent collections(tables in sql)
		db.getCollection("itemds").drop();
		
		IndexOptions indexOptions = new IndexOptions().unique(true);
		db.getCollection("users").createIndex(new Document("user_id",1),indexOptions);
		db.getCollection("items").createIndex(new Document("item_id",1),indexOptions);
		
		
		//Step 4 insert fake userdata and create index
		db.getCollection("users").insertOne(new Document().append("user_id","1111").append("password", "3229c1097c00d497a0fd282d586be050").append("first_name","John").append("last_name","Smith"));
		
		mongoClient.close();
		System.out.println("Import is done successfully");
	}
}
